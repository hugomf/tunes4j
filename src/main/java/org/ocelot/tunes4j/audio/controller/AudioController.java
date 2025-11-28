package org.ocelot.tunes4j.audio.controller;

import java.util.concurrent.CompletableFuture;

import org.ocelot.tunes4j.audio.adapter.AudioPlayerAdapter;
import org.ocelot.tunes4j.audio.adapter.SpectrumAdapter;
import org.ocelot.tunes4j.audio.model.AudioPlayback;
import org.ocelot.tunes4j.audio.model.Song;
import org.ocelot.tunes4j.audio.service.PlaybackService;
import org.ocelot.tunes4j.audio.service.SpectrumService;
import org.ocelot.tunes4j.event.AudioDataEvent;
import org.ocelot.tunes4j.event.AudioPlaybackStateEvent;
import org.ocelot.tunes4j.event.AudioSongSelectedEvent;
import org.ocelot.tunes4j.event.AudioUserInteractionEvent;
import org.ocelot.tunes4j.event.PlayProgressEvent;
import org.ocelot.tunes4j.event.ProgressUpdateListener;
import org.ocelot.tunes4j.library.event.LibrarySongSelectedEvent;
import org.ocelot.tunes4j.library.model.LibrarySong;
import org.ocelot.tunes4j.notification.NotifierFactory;
import org.ocelot.tunes4j.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

/**
 * Audio Controller - Reactive Coordinator for Audio Bounded Context.
 * Handles event-driven communication between audio views, services, and adapters.
 * Implements the Observer Pattern for cross-component coordination.
 *
 * Uses AudioService for domain operations (search, library management, etc.)
 */
@Controller
public class AudioController extends BaseController implements ProgressUpdateListener {

    private final PlaybackService playbackService;
    private final SpectrumService spectrumService;
    private final AudioService audioService;
    private final AudioPlayerAdapter audioPlayerAdapter;
    private final SpectrumAdapter spectrumAdapter;

    @Autowired
    public void registerProgressListener() {
        // Register this controller as a progress listener for the playback service
        // This MUST happen after both PlaybackService and AudioController are created
        // to avoid circular dependency issues
        playbackService.setProgressListener(this);
        System.out.println("🎵 AUDIO CONTROLLER: Successfully registered as progress listener");
    }

    public AudioController(PlaybackService playbackService,
                          SpectrumService spectrumService,
                          AudioService audioService,
                          AudioPlayerAdapter audioPlayerAdapter,
                          SpectrumAdapter spectrumAdapter) {
        this.playbackService = playbackService;
        this.spectrumService = spectrumService;
        this.audioService = audioService;
        this.audioPlayerAdapter = audioPlayerAdapter;
        this.spectrumAdapter = spectrumAdapter;
    }



    /**
     * Handle song selection events from other bounded contexts (Library).
     * This implements the reactive flow: SongListView → AudioController → Playback
     * Event from Library bounded context (LibrarySongSelectedEvent)
     */
    @EventListener(LibrarySongSelectedEvent.class)
    public void onLibrarySongSelected(LibrarySongSelectedEvent event) {
        System.out.println("🎵 AUDIO CONTROLLER: RECEIVED LibrarySongSelectedEvent");

        if (event.getSource() == this) {
            return; // Avoid self-handling
        }

        LibrarySong libSong = event.getSelectedSong();
        System.out.println("🎵 AUDIO CONTROLLER: Processing library song selection: " + libSong.getTitle());

        // Convert LibrarySong to dto.Song using the ID
        // LibrarySong IDs should match dto.Song IDs
        try {
            org.ocelot.tunes4j.dto.Song dtoSong = audioService.getSongById(libSong.getId());
            if (dtoSong == null) {
                System.err.println("❌ AUDIO CONTROLLER: Song not found in audio service: " + libSong.getId());
                return;
            }

            System.out.println("🎵 AUDIO CONTROLLER: Found dto.Song: " + dtoSong.getTitle());

            // Publish event for UI components to update with current song info
            System.out.println("🎵 AUDIO CONTROLLER: Publishing AudioSongSelectedEvent for UI update: " + dtoSong.getTitle());
            publishEvent(new AudioSongSelectedEvent(this, dtoSong));

            // Trigger system notification with complete song metadata
            triggerPlaybackNotification(dtoSong);

            // Start playback asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    System.out.println("🎵 AUDIO CONTROLLER: Playing song: " + dtoSong.getTitle());
                    // Call PlaybackService to actually play the song
                    playbackService.playSong(dtoSong);
                } catch (Exception e) {
                    System.err.println("❌ AUDIO CONTROLLER: Failed to play song: " + e.getMessage());
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            System.err.println("❌ AUDIO CONTROLLER: Error converting LibrarySong to dto.Song: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle song selection events (e.g., from song list view).
     * This implements the reactive flow: SongListView → AudioController → Playback
     */
    @EventListener(AudioSongSelectedEvent.class)
    public void onSongSelected(AudioSongSelectedEvent event) {
        System.out.println("🎵 AUDIO CONTROLLER: RECEIVED AudioSongSelectedEvent");

        if (event.getSource() == this) {
            return; // Avoid self-handling
        }

        org.ocelot.tunes4j.dto.Song dtoSong = event.getSong();
        System.out.println("🎵 AUDIO CONTROLLER: Processing song selection: " + dtoSong.getTitle());

        // Start playback asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("🎵 AUDIO CONTROLLER: Playing song: " + dtoSong.getTitle());
                // Call PlaybackService to actually play the song
                playbackService.playSong(dtoSong);
            } catch (Exception e) {
                System.err.println("❌ AUDIO CONTROLLER: Failed to play song: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Handle user interaction events (play, pause, stop, etc.).
     */
    @EventListener(AudioUserInteractionEvent.class)
    public void onUserInteraction(AudioUserInteractionEvent event) {
        if (event.getSource() == this) {
            return; // Avoid self-handling
        }

        switch (event.getInteractionType()) {
            case PLAY:
                audioPlayerAdapter.resumeSong();
                playbackService.resume();
                break;
            case PAUSE:
                audioPlayerAdapter.pauseSong();
                playbackService.pause();
                break;
            case STOP:
                audioPlayerAdapter.stopSong();
                playbackService.stop();
                break;
            case VOLUME_CHANGE:
                if (event.getData() instanceof Number) {
                    double volume = ((Number) event.getData()).doubleValue();
                    audioPlayerAdapter.setVolumePercentage((int) volume);
                    playbackService.setVolume((int) volume);
                }
                break;
            case SEEK:
                if (event.getData() instanceof Number) {
                    int position = ((Number) event.getData()).intValue();
                    playbackService.seek(position);
                }
                break;
            case NEXT:
                Song nextSong = playbackService.next();
                if (nextSong != null) {
                    // Event will be published by playbackService
                }
                break;
            case PREVIOUS:
                Song prevSong = playbackService.previous();
                if (prevSong != null) {
                    // Event will be published by playbackService
                }
                break;
            case MUTE:
                // Toggle mute - set volume to 0 or restore previous volume
                // For simplicity, just notify that mute was requested
                // Real implementation might need to track previous volume
                publishEvent(new AudioUserInteractionEvent(this,
                    AudioUserInteractionEvent.InteractionType.VOLUME_CHANGE, 0));
                break;
        }
    }

    /**
     * Handle playback state events (for UI updates).
     * This creates the reactive flow: PlaybackService → AudioController → Views
     */
    @EventListener(AudioPlaybackStateEvent.class)
    public void onPlaybackStateChanged(AudioPlaybackStateEvent event) {
        if (event.getSource() == this) {
            return; // Avoid self-handling
        }

        // The event is already published to all listeners
        // Views will handle UI updates based on this event

        // Additional controller-level logic could go here
        updatePlaybackDependents(event);
    }

    /**
     * Handle audio data events from spectrum processing.
     * This enables reactive flow: SpectrumService → AudioController → Visualization Views
     */
    @EventListener(AudioDataEvent.class)
    public void onAudioDataAvailable(AudioDataEvent event) {
        if (event.getSource() == this) {
            return; // Avoid self-handling
        }

        // The audio data is already published to all listeners
        // Visualization views will handle spectrum updates
    }

    /**
     * Public API for views to request song playback.
     */
    public CompletableFuture<Void> playSong(String songId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Use AudioService to get song (returns dto.Song for now)
                org.ocelot.tunes4j.dto.Song dtoSong = audioService.getSongById(songId);
                if (dtoSong != null) {
                    // TODO: Convert dto.Song to domain Song using SongMapper
                    System.out.println("Playing song: " + dtoSong.getTitle());
                } else {
                    throw new IllegalArgumentException("Song not found: " + songId);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to play song: " + songId, e);
            }
            return null;
        });
    }

    /**
     * Public API for views to control playback.
     */
    public void pausePlayback() {
        publishEvent(new AudioUserInteractionEvent(this, AudioUserInteractionEvent.InteractionType.PAUSE));
    }

    public void resumePlayback() {
        publishEvent(new AudioUserInteractionEvent(this, AudioUserInteractionEvent.InteractionType.PLAY));
    }

    public void stopPlayback() {
        publishEvent(new AudioUserInteractionEvent(this, AudioUserInteractionEvent.InteractionType.STOP));
    }

    public void setVolume(int volume) {
        publishEvent(new AudioUserInteractionEvent(this, AudioUserInteractionEvent.InteractionType.VOLUME_CHANGE, volume));
    }

    public void seekTo(int position) {
        publishEvent(new AudioUserInteractionEvent(this, AudioUserInteractionEvent.InteractionType.SEEK, position));
    }

    /**
     * Get current playback state.
     */
    public AudioPlaybackStateEvent.PlaybackState getCurrentPlaybackState() {
        AudioPlayback.PlaybackState internalState = playbackService.getCurrentPlayback().getState();
        switch (internalState) {
            case PLAYING: return AudioPlaybackStateEvent.PlaybackState.PLAYING;
            case PAUSED: return AudioPlaybackStateEvent.PlaybackState.PAUSED;
            case STOPPED: return AudioPlaybackStateEvent.PlaybackState.STOPPED;
            case LOADING: return AudioPlaybackStateEvent.PlaybackState.LOADING;
            default: return AudioPlaybackStateEvent.PlaybackState.STOPPED;
        }
    }

    public Song getCurrentSong() {
        return playbackService.getCurrentPlayback().getCurrentSong();
    }

    public int getCurrentPosition() {
        return playbackService.getCurrentPlayback().getCurrentPosition();
    }

    /**
     * Set equalizer (adapter-level control).
     */
    public void setEqualizerBand(int band, double gain) {
        audioPlayerAdapter.setEqualizerBandGain(band, gain);
    }

    public double getEqualizerBand(int band) {
        return audioPlayerAdapter.getEqualizerBandGain(band);
    }

    public void loadEqualizerPreset(String presetName) {
        audioPlayerAdapter.loadEqualizerPreset(presetName);
    }

    public String getCurrentEqualizerPreset() {
        return audioPlayerAdapter.getEqualizerPreset();
    }

    public String[] getEqualizerPresets() {
        return audioPlayerAdapter.getEqualizerPresetNames();
    }

    /**
     * Process audio spectrum for visualization.
     */
    public void processAudioSpectrum(float[] audioData) {
        spectrumService.processAudioDataForVisualization(audioData);
    }

    /**
     * Configure spectrum analysis settings.
     */
    public void setSpectrumSampleSize(int sampleSize) {
        spectrumAdapter.setSampleSize(sampleSize);
    }

    /**
     * Get current spectrum sample size.
     */
    public int getSpectrumSampleSize() {
        return spectrumAdapter.getSampleSize();
    }

    /**
     * Implementation of ProgressUpdateListener interface.
     * Handles progress updates from the audio player.
     */
    @Override
    public void updateProgress(PlayProgressEvent event) {
        // Could publish AudioDataEvent or update UI directly
        // For now, just log the progress
        System.out.println("🎵 AUDIO CONTROLLER: Progress update - " + event.getCurrentProgress() + " bytes played");

        // In a full implementation, this could trigger events for:
        // - Progress bar updates
        // - Time display updates
        // - Spectrum visualization
    }

    /**
     * Trigger system notification when playback starts.
     * Uses complete dto.Song metadata for accurate display.
     */
    private void triggerPlaybackNotification(org.ocelot.tunes4j.dto.Song song) {
        new Thread(() -> {
            // Get artwork from song data if available
            java.awt.Image image = null;
            if (song.getArtWork() != null) {
                System.out.println("🔔 NOTIFICATION: Song has artwork data, size: " + song.getArtWork().length + " bytes");
                try {
                    // Convert byte[] to Image for notification
                    image = org.ocelot.tunes4j.utils.ImageUtils.read(song.getArtWork());
                    if (image != null) {
                        System.out.println("🔔 NOTIFICATION: Successfully converted artwork for notification: " + image.getWidth(null) + "x" + image.getHeight(null));
                    }
                } catch (Exception e) {
                    System.out.println("🔔 NOTIFICATION: Exception converting artwork for notification: " + e.getMessage());
                }
            } else {
                System.out.println("🔔 NOTIFICATION: No artwork data available for notification");
            }

            // Create notification with complete song metadata
            NotifierFactory.instance().push(image, song.getAlbum(), song.getTitle(), song.getArtist());
            System.out.println("🔔 NOTIFICATION: Playback started - " + song.getTitle() + " by " + song.getArtist());
        }).start();
    }

    // Private helper methods
    private void updatePlaybackDependents(AudioPlaybackStateEvent event) {
        // Handle cross-component dependencies based on playback state
        // For example, when playback stops, reset spectrum visualization
        if (event.getState() == AudioPlaybackStateEvent.PlaybackState.STOPPED) {
            // This could trigger other events or state updates
        }
    }
}
