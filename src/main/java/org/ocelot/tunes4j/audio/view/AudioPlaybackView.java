package org.ocelot.tunes4j.audio.view;

import org.ocelot.tunes4j.audio.controller.AudioController;
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.event.AudioPlaybackStateEvent;
import org.ocelot.tunes4j.event.AudioSongSelectedEvent;
import org.ocelot.tunes4j.event.AudioUserInteractionEvent;
import org.ocelot.tunes4j.event.AudioDataEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Audio Playback View - Reactive UI Component.
 * Implements @EventListener interfaces to reactively update UI based on domain events.
 * Demonstrates the Observer Pattern in the reactive MVC architecture.
 */
@Component
public class AudioPlaybackView {

    private final AudioController audioController;

    // Mock UI state (would be actual UI components in real app)
    private String currentSongTitle = "No song selected";
    private String playbackStateText = "Stopped";
    private int currentVolume = 70;
    private int currentPosition = 0;

    @Autowired
    public AudioPlaybackView(AudioController audioController) {
        this.audioController = audioController;
        updateUIDisplay();
    }

    /**
     * React to song selection changes.
     * This implements the reactive flow: PlaybackService → Views (via events)
     */
    @EventListener(AudioSongSelectedEvent.class)
    public void onSongChanged(AudioSongSelectedEvent event) {
        Song song = event.getSong();
        currentSongTitle = song.getTitle() + " - " + song.getArtist();

        System.out.println("🎵 Song changed: " + currentSongTitle);
        updateUIDisplay();
    }

    /**
     * React to playback state changes.
     */
    @EventListener(AudioPlaybackStateEvent.class)
    public void onPlaybackStateChanged(AudioPlaybackStateEvent event) {
        switch (event.getState()) {
            case PLAYING:
                playbackStateText = "▶️ Playing";
                break;
            case PAUSED:
                playbackStateText = "⏸️ Paused";
                break;
            case STOPPED:
                playbackStateText = "⏹️ Stopped";
                break;
            case LOADING:
                playbackStateText = "⏳ Loading...";
                break;
            case ERROR:
                playbackStateText = "❌ Error";
                break;
        }

        // Update position if available
        if (event.getCurrentPosition() != null) {
            currentPosition = event.getCurrentPosition();
        }

        System.out.println("🎵 Playback state: " + playbackStateText);
        updateUIDisplay();
    }

    /**
     * React to user interaction events (for UI synchronization).
     */
    @EventListener(AudioUserInteractionEvent.class)
    public void onUserInteraction(AudioUserInteractionEvent event) {
        switch (event.getInteractionType()) {
            case VOLUME_CHANGE:
                if (event.getData() instanceof Number) {
                    currentVolume = ((Number) event.getData()).intValue();
                    System.out.println("🔊 Volume changed to: " + currentVolume);
                }
                break;
            case MUTE:
                System.out.println("🔇 Mute toggled");
                break;
            case PLAY:
                System.out.println("▶️ Play requested");
                break;
            case PAUSE:
                System.out.println("⏸️ Pause requested");
                break;
            case STOP:
                System.out.println("⏹️ Stop requested");
                break;
            case NEXT:
                System.out.println("⏭️ Next requested");
                break;
            case PREVIOUS:
                System.out.println("⏮️ Previous requested");
                break;
            case SEEK:
                // Already handled in playback state changes
                break;
        }
        updateUIDisplay();
    }

    /**
     * React to audio data events (spectrum visualization updates).
     */
    @EventListener(AudioDataEvent.class)
    public void onAudioDataAvailable(AudioDataEvent event) {
        switch (event.getDataType()) {
            case SPECTRUM:
                // Update spectrum visualization
                System.out.println("📊 Spectrum data updated");
                break;
            case WAVEFORM:
                // Update waveform visualization
                System.out.println("📈 Waveform data updated");
                break;
            case FFT:
                // Raw FFT data for custom processing
                System.out.println("🧮 FFT data updated");
                break;
        }
        // In real implementation, would update spectrum bars, waveform display, etc.
    }

    /**
     * Simulate UI control actions (would be triggered by actual UI components).
     */
    public void onPlayButtonClicked() {
        audioController.resumePlayback();
    }

    public void onPauseButtonClicked() {
        audioController.pausePlayback();
    }

    public void onStopButtonClicked() {
        audioController.stopPlayback();
    }

    public void onVolumeSliderChanged(int volume) {
        audioController.setVolume(volume);
    }

    public void onSeekBarChanged(int position) {
        audioController.seekTo(position);
    }

    public void onMuteButtonClicked() {
        // Trigger mute through controller (views shouldn't publish events directly)
        // This would call an appropriate controller method
        System.out.println("🔇 Mute requested - integrated implementation needed");
    }

    /**
     * Get UI state for display.
     */
    public String getCurrentSongDisplay() {
        return currentSongTitle;
    }

    public String getPlaybackStateDisplay() {
        return playbackStateText;
    }

    public int getCurrentVolume() {
        return currentVolume;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Update the mock UI display (simulates real UI refresh).
     */
    private void updateUIDisplay() {
        System.out.println("📱 UI Updated:");
        System.out.println("   Song: " + currentSongTitle);
        System.out.println("   State: " + playbackStateText);
        System.out.println("   Volume: " + currentVolume + "%");
        System.out.println("   Position: " + currentPosition + " ms");
        System.out.println();
    }
}
