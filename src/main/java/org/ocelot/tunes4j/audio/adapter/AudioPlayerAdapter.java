package org.ocelot.tunes4j.audio.adapter;

import java.io.File;
import java.io.FileNotFoundException;

import org.ocelot.tunes4j.event.ProgressUpdateListener;
import org.ocelot.tunes4j.gui.JPanelSpectrum;
import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;
import org.springframework.stereotype.Component;

/**
 * Audio Player Adapter - External audio playback library adapter.
 * Wraps Tunes4JAudioPlayer functionality (Port/Adapter pattern).
 */
@Component
public class AudioPlayerAdapter {

    private final Tunes4JAudioPlayer audioPlayer;
    private ProgressUpdateListener progressListener;

    public AudioPlayerAdapter() {
        this.audioPlayer = new Tunes4JAudioPlayer();
        // Set a reasonable default volume (50% or 0.5)
        setVolume(0.5); // 50% volume by default
    }

    /**
     * Open and play a song file.
     */
    public void openSong(String filePath) throws FileNotFoundException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        File songFile = new File(filePath);
        if (!songFile.exists()) {
            throw new FileNotFoundException("Song file not found: " + filePath);
        }

        if (!songFile.canRead()) {
            throw new IllegalStateException("Cannot read song file: " + filePath);
        }

        audioPlayer.open(songFile);
    }

    /**
     * Start playback.
     */
    public void playSong() {
        // Make sure we have a progress listener before playing
        // Note: The actual setting should happen via addProgressListener()

        // Ensure volume is set before playing (just in case)
        setVolume(0.5); // 50% volume to ensure audio output

        System.out.println("🎵 AUDIO ADAPTER: Starting playback with volume set to 0.5");
        audioPlayer.play();
        System.out.println("🎵 AUDIO ADAPTER: play() called on BasicPlayer");
    }

    /**
     * Pause playback.
     */
    public void pauseSong() {
        audioPlayer.pause();
    }

    /**
     * Resume paused playback.
     */
    public void resumeSong() {
        audioPlayer.resume();
    }

    /**
     * Stop playback.
     */
    public void stopSong() {
        audioPlayer.stop();
    }

    /**
     * Seek to position (milliseconds).
     */
    public void seekToPosition(int milliseconds) {
        audioPlayer.skip(milliseconds);
    }

    /**
     * Set volume (0.0 to 1.0).
     */
    public void setVolume(double volume) {
        audioPlayer.setGain(volume);
    }

    /**
     * Set volume (0 to 100).
     */
    public void setVolumePercentage(int volumePercentage) {
        double volume = volumePercentage / 100.0;
        audioPlayer.setGain(volume);
    }

    /**
     * Get current status.
     */
    public int getPlaybackStatus() {
        return audioPlayer.getCurrentStatus();
    }

    /**
     * Check if currently playing.
     */
    public boolean isPlaying() {
        return audioPlayer.isPlaying();
    }

    /**
     * Check if currently paused.
     */
    public boolean isPaused() {
        return audioPlayer.isPaused();
    }

    /**
     * Check if stopped/closed.
     */
    public boolean isClosed() {
        return audioPlayer.isClosed();
    }

    /**
     * Add progress update listener.
     */
    public void addProgressListener(ProgressUpdateListener listener) {
        audioPlayer.addProgressUpdateListener(listener);
    }

    /**
     * Set spectrum panel for visualization.
     */
    public void setSpectrumPanel(JPanelSpectrum spectrumPanel) {
        audioPlayer.setSpectrumPanel(spectrumPanel);
    }

    // Equalizer methods

    /**
     * Set equalizer band gain.
     */
    public void setEqualizerBandGain(int band, double gain) {
        audioPlayer.setEqualizerBandGain(band, gain);
    }

    /**
     * Get equalizer band gain.
     */
    public double getEqualizerBandGain(int band) {
        return audioPlayer.getEqualizerBandGain(band);
    }

    /**
     * Load equalizer preset.
     */
    public void loadEqualizerPreset(String presetName) {
        audioPlayer.loadEqualizerPreset(presetName);
    }

    /**
     * Get current equalizer preset.
     */
    public String getEqualizerPreset() {
        return audioPlayer.getEqualizerPreset();
    }

    /**
     * Get available equalizer preset names.
     */
    public String[] getEqualizerPresetNames() {
        return audioPlayer.getEqualizerPresetNames();
    }

    /**
     * Get the underlying audio player for advanced operations.
     */
    public Tunes4JAudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
}
