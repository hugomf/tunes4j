package org.ocelot.tunes4j.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;

import javax.swing.JPanel;

import org.ocelot.tunes4j.dsp.KJFFT;

/**
 * Direct Spectrum Processor for Raw PCM Audio Data.
 * Processes audio data directly from BasicPlayer without KJ DSP framework.
 *
 * @author Cline
 */
public class SpectrumProcessor {

    private static final int SAMPLE_SIZE = 1024;
    private static final int NUM_BANDS = 16;

    private KJFFT fft;
    private float[] peaks; // Traditional peak hold values
    private float[] magnitudes;
    private int processCount;

    // Colors - Light gray background, gray LCD bars, red peaks
    private Color bgColor = new Color(0.95f,0.96f,0.98f); // SongDisplayPanel background
    private Color barColor = Color.DARK_GRAY;     // Gray LCD bars (better blending)
    private Color barColor2 = Color.DARK_GRAY;    // Gray LCD bars
    private Color peakColor = new Color(255, 100, 100); // Red peaks

    public SpectrumProcessor() {
        fft = new KJFFT(SAMPLE_SIZE);
        peaks = new float[NUM_BANDS];
        magnitudes = new float[SAMPLE_SIZE / 2];
        processCount = 0;
    }

    /**
     * Process raw PCM audio data (16-bit stereo, 44.1kHz assumed)
     * @param pcmData PCM audio data
     * @param offset offset in array
     * @param length length in bytes
     */
    public void processAudioData(byte[] pcmData, int offset, int length) {
        if (pcmData == null || length < 4) return;

        processCount++;

        // Convert PCM to float samples (16-bit stereo)
        float[] samples = convertPcmToFloat(pcmData, offset, length);

        if (samples.length >= SAMPLE_SIZE) {
            // Perform FFT analysis
            float[] spectrum = fft.calculate(samples);

            // Update magnitudes with proper scaling and frequency response
            for (int i = 0; i < Math.min(magnitudes.length, spectrum.length); i++) {
                // Get raw FFT magnitude
                float rawMag = Math.abs(spectrum[i]);

                // Convert bin index to approximate frequency for frequency response shaping
                float freq = (float) i / (SAMPLE_SIZE / 2) * 22050.0f;

                // Apply treble boost for visual appeal (high freqs rarely animate without this)
                float freqBoost = 1.0f;
                if (freq > 5000.0f) { // High treble
                    freqBoost = 3.0f;
                } else if (freq > 2000.0f) { // Upper mids
                    freqBoost = 2.0f;
                }

                // Apply aggressive power scaling for better visibility
                magnitudes[i] = (float) (Math.pow(rawMag, 2.0) * SAMPLE_SIZE * 4.0f * freqBoost);

                // Allow values above 1.0 for more visible bars
                magnitudes[i] = Math.min(10.0f, magnitudes[i]); // Cap at 10x height
            }

                // Update traditional peak hold indicators
            for (int i = 0; i < NUM_BANDS; i++) {
                int bin = getBinForBand(i);
                float magnitude = (bin < magnitudes.length) ? magnitudes[bin] : 0;

                // Push peak up immediately when new high is reached (same scale as bars)
                if (magnitude > peaks[i]) {
                    peaks[i] = magnitude;
                }
                // Peaks fall down slowly independently (like Winamp)
                else if (peaks[i] > 0) {
                    peaks[i] *= 0.99f; // Slower decay for smooth viewing
                }
            }
        }
    }

    /**
     * Convert 16-bit PCM to float samples
     */
    private float[] convertPcmToFloat(byte[] pcmData, int offset, int length) {
        int numSamples = Math.min(length / 4, SAMPLE_SIZE); // Stereo 16-bit = 4 bytes per sample
        float[] samples = new float[numSamples];

        for (int i = 0; i < numSamples; i++) {
            int sampleIndex = offset + i * 4;
            if (sampleIndex + 3 >= pcmData.length) break;

            // Read 16-bit little-endian stereo
            int left = (pcmData[sampleIndex + 1] << 8) | (pcmData[sampleIndex] & 0xFF);
            int right = (pcmData[sampleIndex + 3] << 8) | (pcmData[sampleIndex + 2] & 0xFF);

            // Convert to float (-1.0 to 1.0)
            left = Math.max(-32768, Math.min(32767, left));
            right = Math.max(-32768, Math.min(32767, right));

            // Average left and right channels
            samples[i] = ((float)(left + right) / 2.0f) / 32768.0f;
        }

        return samples;
    }

    /**
     * Render the spectrum on the given graphics context
     */
    public void renderSpectrum(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, width, height);

        // Draw frequency bars
        int barWidth = width / NUM_BANDS;
        int spacing = Math.max(2, barWidth / 6); // Increased spacing for LCD effect

        // Show minimal status if no data processed yet
        if (processCount == 0) {
            g2d.setColor(Color.GRAY);
            g2d.drawString("Ready", width / 2 - 20, height / 2);
            g2d.dispose();
            return;
        }

        for (int band = 0; band < NUM_BANDS; band++) {
            int bin = getBinForBand(band);
            float magnitude = (bin < magnitudes.length) ? magnitudes[bin] : 0;

            // Scale magnitude (0-10) to full meter height
            int barHeight = (int)(magnitude * height / 10.0f);

            int x = band * barWidth + spacing / 2;
            int barActualWidth = barWidth - spacing;
            int barY = height - barHeight;

            // LCD Pixel Effect: Draw vertical dotted segments instead of solid fills
            if (barHeight > 0) {
                int pixelHeight = 3; // Height of each LCD pixel segment
                int pixelSpacing = 2; // Space between pixel segments
                g2d.setColor(barColor);

                for (int y = barY; y < height; y += pixelHeight + pixelSpacing) {
                    int pixelActualHeight = Math.min(pixelHeight, height - y);
                    if (pixelActualHeight > 0) {
                        g2d.fillRect(x, y, barActualWidth, pixelActualHeight);
                    }
                }
            }

            // Traditional peak hold indicator - only show when significant (> 1.5f threshold)
            if (peaks[band] > 1.5f) { // Threshold prevents pesky bottom lines
                int peakY = height - (int)(peaks[band] * height / 10.0f);
                g2d.setColor(peakColor);
                g2d.setStroke(new BasicStroke(1)); // Thinner 1-pixel peaks
                g2d.drawLine(x, peakY, x + barActualWidth - 1, peakY);
            }
        }

        g2d.dispose();
    }

    private int getBinForBand(int band) {
        // Logarithmic frequency distribution for balanced visual representation
        // Maps 16 bands across 512 bins (0-22kHz) with logarithmic spacing

        final double minFreq = 20.0;    // Human hearing starts around 20Hz
        final double maxFreq = 22050.0; // Nyquist frequency for 44.1kHz sample rate
        final int totalBins = SAMPLE_SIZE / 2;

        // Convert bin index to frequency: bin * (sample_rate / fft_size)
        // For 1024 samples at 44.1kHz: Nyquist = 22050Hz

        if (band == 0) return 0; // DC component

        // Logarithmic scaling: map 20Hz - 20kHz logarithmically across bands
        double logMin = Math.log10(minFreq);
        double logMax = Math.log10(maxFreq);
        double logRange = logMax - logMin;

        // Map band index to logarithmic frequency position
        double bandPosition = (double) band / (NUM_BANDS - 1); // 0.0 to 1.0
        double logFreq = logMin + (bandPosition * logRange);
        double freq = Math.pow(10, logFreq);

        // Convert frequency back to bin index
        int bin = (int) Math.round((freq / maxFreq) * totalBins);

        // Ensure we don't exceed bounds
        return Math.min(bin, totalBins - 1);
    }

    public void reset() {
        processCount = 0;
        for (int i = 0; i < peaks.length; i++) {
            peaks[i] = 0;
            magnitudes[i] = 0;
        }
    }
}
