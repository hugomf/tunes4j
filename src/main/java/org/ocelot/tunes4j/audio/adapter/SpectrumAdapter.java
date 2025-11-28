package org.ocelot.tunes4j.audio.adapter;

import org.springframework.stereotype.Component;

/**
 * Spectrum Adapter - FFT processing and spectrum analysis adapter.
 * Wraps external spectrum processing libraries (Port/Adapter pattern).
 */
@Component
public class SpectrumAdapter {

    private int sampleSize = 512;

    /**
     * Get current sample size for spectrum analysis.
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Set sample size for spectrum analysis.
     */
    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }
}
