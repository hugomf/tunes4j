package org.ocelot.tunes4j.audio.view;

import org.ocelot.tunes4j.audio.controller.AudioController;
import org.ocelot.tunes4j.audio.model.SpectrumData;
import org.ocelot.tunes4j.event.AudioDataEvent;
import org.ocelot.tunes4j.event.AudioPlaybackStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Audio Spectrum View - Reactive Visualization Component.
 * Implements @EventListener for AudioDataEvent to update spectrum visualizations.
 * Demonstrates reactive UI updates driven by domain events.
 */
@Component
public class AudioSpectrumView {

    private final AudioController audioController;

    // Mock spectrum visualization state
    private float[] currentSpectrum = new float[0];
    private String spectrumType = "None";
    private int spectrumSize = 0;
    private boolean isVisible = true;
    private String[] spectrumBarDisplay = new String[0];

    @Autowired
    public AudioSpectrumView(AudioController audioController) {
        this.audioController = audioController;
        initializeSpectrumBars();
    }

    /**
     * Primary event listener: React to audio data updates.
     * This is the core reactive behavior demonstrating Observer Pattern.
     */
    @EventListener(AudioDataEvent.class)
    public void onAudioDataReceived(AudioDataEvent event) {
        if (!isVisible) {
            return; // Don't process if visualization is disabled
        }

        switch (event.getDataType()) {
            case SPECTRUM:
                updateSpectrumVisualization(event);
                break;
            case WAVEFORM:
                updateWaveformVisualization(event);
                break;
            case FFT:
                updateFFTVisualization(event);
                break;
        }
    }

    /**
     * React to playback state changes for spectrum behavior.
     */
    @EventListener(AudioPlaybackStateEvent.class)
    public void onPlaybackStateChanged(AudioPlaybackStateEvent event) {
        switch (event.getState()) {
            case STOPPED:
                resetSpectrumToIdle();
                break;
            case PLAYING:
                // Could animate spectrum bars when playing
                break;
            case PAUSED:
                // Could dim spectrum bars when paused
                break;
            default:
                // Other states don't affect spectrum display
                break;
        }
    }

    /**
     * Update spectrum visualization with processed spectrum data.
     */
    private void updateSpectrumVisualization(AudioDataEvent event) {
        Object processedData = event.getProcessedData();
        if (processedData instanceof SpectrumData) {
            SpectrumData spectrumData = (SpectrumData) processedData;

            currentSpectrum = spectrumData.getSpectrum();
            spectrumSize = spectrumData.getSpectrumSize();
            spectrumType = spectrumData.getType().toString();

            generateSpectrumBars(currentSpectrum);

            System.out.println("🌈 Spectrum Updated:");
            System.out.println("   Type: " + spectrumType);
            System.out.println("   Size: " + spectrumSize + " bins");
            System.out.println("   Bars: " + String.join(" ", spectrumBarDisplay));
            System.out.println();
        }
    }

    /**
     * Update waveform visualization.
     */
    private void updateWaveformVisualization(AudioDataEvent event) {
        float[] audioData = event.getAudioData();
        if (audioData != null && audioData.length > 0) {
            generateWaveformBars(audioData);

            System.out.println("📈 Waveform Updated:");
            System.out.println("   Samples: " + audioData.length);
            System.out.println("   Wave: " + String.join(" ", spectrumBarDisplay));
            System.out.println();
        }
    }

    /**
     * Update FFT visualization (raw frequency domain data).
     */
    private void updateFFTVisualization(AudioDataEvent event) {
        // Raw FFT data - would need processing for display
        System.out.println("🧮 FFT Data Available (processing needed for display)");
    }

    /**
     * Generate visual spectrum bars based on frequency magnitudes.
     */
    private void generateSpectrumBars(float[] spectrum) {
        final int NUM_BARS = 20; // Number of spectrum bars to display
        spectrumBarDisplay = new String[NUM_BARS];

        for (int i = 0; i < NUM_BARS; i++) {
            int binIndex = (i * spectrum.length) / NUM_BARS;
            if (binIndex < spectrum.length) {
                float magnitude = spectrum[binIndex];

                // Normalize and scale to bar height (0-8)
                int barHeight = Math.max(1, Math.min(8, Math.round(magnitude * 8)));
                StringBuilder bar = new StringBuilder();

                for (int h = 0; h < barHeight; h++) {
                    bar.append("█");
                }

                // Pad with spaces if needed for consistent height
                for (int h = barHeight; h < 8; h++) {
                    bar.insert(0, " ");
                }

                spectrumBarDisplay[i] = bar.toString();
            } else {
                spectrumBarDisplay[i] = "        "; // Empty bar
            }
        }
    }

    /**
     * Generate simple waveform bars.
     */
    private void generateWaveformBars(float[] audioData) {
        final int NUM_BARS = 10;
        spectrumBarDisplay = new String[NUM_BARS];

        for (int i = 0; i < NUM_BARS; i++) {
            int sampleIndex = (i * audioData.length) / NUM_BARS;
            if (sampleIndex < audioData.length) {
                float sample = Math.abs(audioData[sampleIndex]);
                int barHeight = Math.max(1, Math.min(4, Math.round(sample * 4)));

                StringBuilder bar = new StringBuilder();
                for (int h = 0; h < barHeight; h++) {
                    bar.append("░");
                }

                spectrumBarDisplay[i] = bar.toString();
            } else {
                spectrumBarDisplay[i] = "    ";
            }
        }
    }

    /**
     * Reset spectrum to idle state (no audio playing).
     */
    private void resetSpectrumToIdle() {
        if (spectrumBarDisplay.length > 0) {
            // Set all bars to minimum
            for (int i = 0; i < spectrumBarDisplay.length; i++) {
                spectrumBarDisplay[i] = "        ";
            }
        }

        System.out.println("💤 Spectrum Reset to Idle State");
        displaySpectrumBars();
    }

    /**
     * Initialize spectrum bars to default state.
     */
    private void initializeSpectrumBars() {
        final int NUM_BARS = 20;
        spectrumBarDisplay = new String[NUM_BARS];
        for (int i = 0; i < NUM_BARS; i++) {
            spectrumBarDisplay[i] = "        ";
        }
    }

    /**
     * Display current spectrum bars.
     */
    private void displaySpectrumBars() {
        System.out.println("   Bars: " + String.join(" ", spectrumBarDisplay));
    }

    /**
     * Control spectrum visualization visibility.
     */
    public void setVisible(boolean visible) {
        this.isVisible = visible;
        if (!visible) {
            System.out.println("🔇 Spectrum visualization disabled");
        } else {
            System.out.println("🔊 Spectrum visualization enabled");
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Get current spectrum data for statistics or diagnostics.
     */
    public float[] getCurrentSpectrum() {
        return currentSpectrum.clone();
    }

    public String getSpectrumType() {
        return spectrumType;
    }

    public int getSpectrumSize() {
        return spectrumSize;
    }
}
