package org.ocelot.tunes4j.audio.service;

import java.io.FileNotFoundException;

import org.ocelot.tunes4j.audio.model.Song;
import org.ocelot.tunes4j.audio.model.AudioPlayback;
import org.ocelot.tunes4j.audio.adapter.AudioPlayerAdapter;
import org.ocelot.tunes4j.event.ProgressUpdateListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Playback Service - Domain service for audio playback operations.
 * Handles business logic related to song playback, queue management, and playback state.
 */
@Service
public class PlaybackService {

    private final AudioPlayback audioPlayback = new AudioPlayback();
    private final AudioPlayerAdapter audioPlayerAdapter;

    @Autowired
    public PlaybackService(AudioPlayerAdapter audioPlayerAdapter) {
        this.audioPlayerAdapter = audioPlayerAdapter;
    }

    /**
     * Called by AudioController to register itself as a progress listener.
     * This breaks the circular dependency by registering the listener after both beans are created.
     */
    public void setProgressListener(ProgressUpdateListener listener) {
        if (listener != null) {
            audioPlayerAdapter.addProgressListener(listener);
            System.out.println("🎵 PLAYBACK SERVICE: Progress listener registered successfully");
        } else {
            System.err.println("⚠️ PLAYBACK SERVICE: Null progress listener provided");
        }
    }

    /**
     * Start playing a song using DTO (which contains file path).
     * Converts DTO to domain model for business logic.
     */
    public void playSong(org.ocelot.tunes4j.dto.Song songDto) {
        if (songDto == null) {
            throw new IllegalArgumentException("Song DTO cannot be null");
        }

        try {
            String filePath = songDto.getPath() + java.io.File.separator + songDto.getFileName();
            System.out.println("🎵 PLAYBACK SERVICE: Constructed filepath: " + filePath);

            // Check if file exists, and handle resource paths
            java.io.File songFile = new java.io.File(filePath);
            System.out.println("🎵 PLAYBACK SERVICE: File exists: " + songFile.exists());
            System.out.println("🎵 PLAYBACK SERVICE: File can read: " + songFile.canRead());
            System.out.println("🎵 PLAYBACK SERVICE: File length: " + songFile.length());

            if (!songFile.exists()) {
                // Try resolving as a resource path
                java.io.File resourcesDir = new java.io.File("src/main/resources");
                if (resourcesDir.exists()) {
                    java.io.File resourceFile = new java.io.File(resourcesDir, songDto.getFileName());
                    if (resourceFile.exists() && resourceFile.canRead()) {
                        filePath = resourceFile.getAbsolutePath();
                        System.out.println("🎵 PLAYBACK SERVICE: Resolved to absolute path: " + filePath);
                    }
                }
            }

            // Open the song file for playback
            System.out.println("🎵 PLAYBACK SERVICE: Attempting to open song file: " + filePath);
            audioPlayerAdapter.openSong(filePath);

            // Convert DTO to domain model
            Song domainSong = convertDtoToDomain(songDto);
            audioPlayback.startPlayback(domainSong);

            // Start actual playback
            audioPlayerAdapter.playSong();

            System.out.println("🎵 PLAYBACK SERVICE: Started playing song: " + songDto.getTitle());

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to open song file for: " + songDto.getTitle(), e);
        }
    }

    /**
     * Convert DTO to domain Song (for business logic).
     */
    private Song convertDtoToDomain(org.ocelot.tunes4j.dto.Song dto) {
        // Duration not available in DTO, use zero for now
        java.time.Duration duration = java.time.Duration.ofSeconds(0);
        return new Song(dto.getId(), dto.getTitle(), dto.getArtist(), dto.getAlbum(), dto.getGenre(), duration);
    }

    /**
     * Resume paused playback.
     */
    public void resume() {
        audioPlayback.resume();
    }

    /**
     * Pause current playback.
     */
    public void pause() {
        audioPlayback.pause();
    }

    /**
     * Stop playback.
     */
    public void stop() {
        audioPlayback.stop();
    }

    /**
     * Seek to position.
     */
    public void seek(int position) {
        audioPlayback.seek(position);
    }

    /**
     * Set volume (0-100).
     */
    public void setVolume(int volume) {
        audioPlayback.setVolume(volume / 100.0);
    }

    /**
     * Get next song in queue.
     */
    public Song next() {
        // TODO: Implement queue logic
        return audioPlayback.getNextSong();
    }

    /**
     * Get previous song in queue.
     */
    public Song previous() {
        // TODO: Implement queue logic
        return audioPlayback.getPreviousSong();
    }

    /**
     * Get current playback state.
     */
    public AudioPlayback getCurrentPlayback() {
        return audioPlayback;
    }
}
