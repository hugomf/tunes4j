package org.ocelot.tunes4j.audio.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Domain object representing spectrum analysis data for audio visualization.
 * This contains frequency domain data calculated from audio samples.
 */
public class SpectrumData {

    public enum SpectrumType {
        MAGNITUDE, POWER, DECIBEL
    }

    private final String id;
    private final float[] spectrum;
    private final SpectrumType type;
    private final int sampleSize;
    private final long timestamp;

    // Domain invariants
    public static final int MIN_SAMPLE_SIZE = 256;
    public static final int MAX_SAMPLE_SIZE = 8192;

    /**
     * Create a new SpectrumData object.
     *
     * @param id unique identifier
     * @param spectrum the spectrum data (defensive copy made)
     * @param type the type of spectrum data
     * @param sampleSize the original audio sample size used
     */
    public SpectrumData(String id, float[] spectrum, SpectrumType type, int sampleSize) {
        this.id = Objects.requireNonNull(id, "SpectrumData ID cannot be null");
        this.spectrum = Arrays.copyOf(Objects.requireNonNull(spectrum, "Spectrum data cannot be null"), spectrum.length);
        this.type = Objects.requireNonNull(type, "Spectrum type cannot be null");

        if (sampleSize < MIN_SAMPLE_SIZE || sampleSize > MAX_SAMPLE_SIZE) {
            throw new IllegalArgumentException("Sample size must be between " + MIN_SAMPLE_SIZE + " and " + MAX_SAMPLE_SIZE);
        }
        this.sampleSize = sampleSize;
        this.timestamp = System.currentTimeMillis();

        validateSpectrumData();
    }

    /**
     * Validate spectrum data consistency.
     */
    private void validateSpectrumData() {
        if (spectrum.length == 0) {
            throw new IllegalArgumentException("Spectrum data cannot be empty");
        }

        if (sampleSize % 2 != 0) {
            throw new IllegalArgumentException("Sample size must be a power of 2");
        }

        // For FFT, spectrum should be half the length of sample size
        if (spectrum.length > sampleSize / 2) {
            throw new IllegalArgumentException("Spectrum data length cannot exceed sampleSize/2");
        }

        // Validate data ranges based on type
        for (float value : spectrum) {
            validateValue(value);
        }
    }

    /**
     * Validate individual spectrum values based on type.
     */
    private void validateValue(float value) {
        switch (type) {
            case MAGNITUDE:
                if (value < 0) {
                    throw new IllegalArgumentException("Magnitude values cannot be negative");
                }
                break;
            case POWER:
                if (value < 0) {
                    throw new IllegalArgumentException("Power values cannot be negative");
                }
                break;
            case DECIBEL:
                // Decibels can be negative, but not extremely negative
                if (value < -200) {
                    throw new IllegalArgumentException("Decibel values seem unreasonably low");
                }
                break;
        }
    }

    /**
     * Get spectrum value at specific bin index.
     */
    public float getValue(int binIndex) {
        if (binIndex < 0 || binIndex >= spectrum.length) {
            throw new IndexOutOfBoundsException("Bin index out of bounds: " + binIndex);
        }
        return spectrum[binIndex];
    }

    /**
     * Get the magnitude as a normalized value between 0 and 1.
     */
    public float getNormalizedValue(int binIndex) {
        float value = getValue(binIndex);
        switch (type) {
            case MAGNITUDE:
                // Assume max magnitude is 1.0, normalize
                return Math.min(value, 1.0f);
            case POWER:
                // Power can be larger, normalize roughly
                return Math.min(value / 100f, 1.0f);
            case DECIBEL:
                // Convert dB to normalized (0-1 range roughly)
                return Math.max(0f, Math.min((value + 100f) / 100f, 1.0f));
            default:
                return value;
        }
    }

    /**
     * Get frequency for a given bin index (approximation).
     * Assumes standard sample rate of 44100 Hz.
     */
    public double getFrequency(int binIndex) {
        if (binIndex < 0 || binIndex >= spectrum.length) {
            throw new IndexOutOfBoundsException("Bin index out of bounds: " + binIndex);
        }
        final double SAMPLE_RATE = 44100.0;
        return (binIndex * SAMPLE_RATE) / sampleSize;
    }

    /**
     * Find the peak bin index in a frequency range.
     */
    public int findPeak(int startBin, int endBin) {
        if (startBin < 0 || endBin >= spectrum.length || startBin > endBin) {
            throw new IllegalArgumentException("Invalid bin range: " + startBin + " to " + endBin);
        }

        int peakIndex = startBin;
        float peakValue = spectrum[startBin];

        for (int i = startBin + 1; i <= endBin; i++) {
            if (spectrum[i] > peakValue) {
                peakValue = spectrum[i];
                peakIndex = i;
            }
        }

        return peakIndex;
    }

    /**
     * Calculate average value in a frequency range.
     */
    public float getAverage(int startBin, int endBin) {
        if (startBin < 0 || endBin >= spectrum.length || startBin > endBin) {
            throw new IllegalArgumentException("Invalid bin range: " + startBin + " to " + endBin);
        }

        float sum = 0f;
        for (int i = startBin; i <= endBin; i++) {
            sum += spectrum[i];
        }

        return sum / (endBin - startBin + 1);
    }

    // Getters
    public String getId() { return id; }
    public float[] getSpectrum() { return Arrays.copyOf(spectrum, spectrum.length); } // Defensive copy
    public SpectrumType getType() { return type; }
    public int getSampleSize() { return sampleSize; }
    public int getSpectrumSize() { return spectrum.length; }
    public long getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpectrumData)) return false;
        SpectrumData that = (SpectrumData) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("SpectrumData{id='%s', type=%s, size=%d, sampleSize=%d}",
                           id, type, spectrum.length, sampleSize);
    }
}
