package org.ocelot.tunes4j.audio.model;

import java.util.List;

/**
 * Audio Playback Aggregate - Domain model for audio playback state and control.
 *
 * Represents the current playback session state and encapsulates playback rules.
 * This is the core domain entity for audio playback operations.
 */
public class AudioPlayback {

    public enum PlaybackState {
        STOPPED, PLAYING, PAUSED, LOADING, ERROR
    }

    public enum RepeatMode {
        OFF, ALL, ONE
    }

    public enum ShuffleMode {
        OFF, ON
    }

    private volatile PlaybackState state = PlaybackState.STOPPED;
    private volatile Song currentSong;
    private volatile Song nextSong;
    private volatile Song previousSong;

    private volatile RepeatMode repeatMode = RepeatMode.OFF;
    private volatile ShuffleMode shuffleMode = ShuffleMode.OFF;

    private volatile int currentPosition = 0;
    private volatile int totalDuration = 0;
    private volatile double volume = 0.8;

    private volatile boolean isMuted = false;
    private volatile double previousVolume = 0.8;

    /**
     * Start playback of a song.
     */
    public void startPlayback(Song song) {
        if (song == null) {
            throw new IllegalArgumentException("Song cannot be null");
        }
        this.currentSong = song;
        this.state = PlaybackState.LOADING;
        this.currentPosition = 0;
        this.totalDuration = 0; // Would be populated when audio file is loaded
    }

    /**
     * Resume playback.
     */
    public void resume() {
        if (currentSong != null && state == PlaybackState.PAUSED) {
            state = PlaybackState.PLAYING;
        }
    }

    /**
     * Pause playback.
     */
    public void pause() {
        if (state == PlaybackState.PLAYING) {
            state = PlaybackState.PAUSED;
        }
    }

    /**
     * Stop playback.
     */
    public void stop() {
        state = PlaybackState.STOPPED;
        currentPosition = 0;
    }

    /**
     * Seek to a specific position.
     */
    public void seek(int position) {
        if (position < 0) return;
        if (totalDuration > 0 && position > totalDuration) return;
        this.currentPosition = position;
    }

    /**
     * Toggle mute state.
     */
    public void toggleMute() {
        if (isMuted) {
            // Restore previous volume
            volume = previousVolume;
            isMuted = false;
        } else {
            // Save current volume and mute
            previousVolume = volume;
            volume = 0.0;
            isMuted = true;
        }
    }

    /**
     * Set volume (0.0 to 1.0).
     */
    public void setVolume(double volume) {
        this.volume = clamp(volume, 0.0, 1.0);
        if (this.volume > 0) {
            isMuted = false;
        }
    }

    /**
     * Update playback position.
     */
    public void updatePosition(int position) {
        this.currentPosition = position;
    }

    /**
     * Set total duration of current song.
     */
    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    /**
     * Get progress as a percentage (0.0 to 1.0).
     */
    public double getProgress() {
        return totalDuration > 0 ? (double) currentPosition / totalDuration : 0.0;
    }

    /**
     * Check if playback can resume.
     */
    public boolean canResume() {
        return currentSong != null && state == PlaybackState.PAUSED;
    }

    /**
     * Check if playback can pause.
     */
    public boolean canPause() {
        return state == PlaybackState.PLAYING;
    }

    /**
     * Check if playback can stop.
     */
    public boolean canStop() {
        return state != PlaybackState.STOPPED;
    }

    /**
     * Check if playback can seek.
     */
    public boolean canSeek() {
        return totalDuration > 0;
    }

    private double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    // Getters
    public PlaybackState getState() { return state; }
    public Song getCurrentSong() { return currentSong; }
    public int getCurrentPosition() { return currentPosition; }
    public int getTotalDuration() { return totalDuration; }
    public double getVolume() { return isMuted ? 0.0 : volume; }
    public boolean isMuted() { return isMuted; }
    public double getRawVolume() { return volume; }
    public RepeatMode getRepeatMode() { return repeatMode; }
    public ShuffleMode getShuffleMode() { return shuffleMode; }
    public Song getNextSong() { return nextSong; }
    public Song getPreviousSong() { return previousSong; }

    // Setters (used by playback controller)
    public void setNextSong(Song nextSong) { this.nextSong = nextSong; }
    public void setPreviousSong(Song previousSong) { this.previousSong = previousSong; }
    public void setRepeatMode(RepeatMode repeatMode) { this.repeatMode = repeatMode; }
    public void setShuffleMode(ShuffleMode shuffleMode) { this.shuffleMode = shuffleMode; }
}
