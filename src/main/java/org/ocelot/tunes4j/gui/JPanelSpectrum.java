package org.ocelot.tunes4j.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import org.ocelot.tunes4j.dsp.KJDigitalSignalProcessor;
import org.ocelot.tunes4j.dsp.KJFFT;

/**
 * Spectrum Analyzer using FFT analysis for frequency domain visualization.
 * Uses direct SpectrumProcessor for raw PCM audio data processing.
 *
 * @author Cline
 */
public class JPanelSpectrum extends JPanel {

    private static final long serialVersionUID = 1L;

    /** FFT processor for frequency analysis */
    private KJFFT fft;

    /** Sample buffer size */
    private static final int SAMPLE_SIZE = 1024;

    /** Number of frequency bands to display */
    private static final int NUM_BANDS = 32;

    /** Peak hold values for each band */
    private float[] peaks;

    /** Peak fall-off rate */
    private float peakFalloff = 0.95f;

    /** Magnitude values for each band */
    private float[] magnitudes;

    /** Colors for spectrum visualization */
    private Color bgColor = new Color(40, 40, 60); // Make background more visible
    private Color barColor = new Color(100, 220, 255);
    private Color barColor2 = new Color(150, 255, 200);
    private Color peakColor = new Color(255, 100, 100);

    /** Debug flag */
    private long lastProcessTime = 0;
    private long processCount = 0;

    private SpectrumProcessor spectrumProcessor;

    public JPanelSpectrum() {
        spectrumProcessor = new SpectrumProcessor();
        setOpaque(true);
        setBackground(bgColor);
    }

    /**
     * Process audio data directly from PCM bytes
     */
    public void processAudioData(byte[] pcmData) {
        if (spectrumProcessor != null) {
            spectrumProcessor.processAudioData(pcmData, 0, pcmData.length);
            repaint();
        }
    }

    /**
     * Map frequency bands to FFT bins with logarithmic spacing
     */
    private int getBinForBand(int band, boolean start) {
        // Logarithmic frequency spacing for better visual representation
        double normalizedBand = (double) band / (NUM_BANDS - 1);
        double frequencyHz = Math.pow(20000.0 / 20.0, normalizedBand) * 20.0; // 20Hz to 20kHz

        // Convert to FFT bin
        int bin = (int) (frequencyHz / 44100.0 * fft.getSampleSize()); // Assuming 44.1kHz sample rate

        // Adjust for start/end bounds
        if (start && band > 0) {
            return bin / 2; // Start from middle of previous range
        } else if (!start && band < NUM_BANDS - 1) {
            return bin + (getBinForBand(band + 1, true) - bin) / 2; // End at middle of next range
        }

        return Math.min(bin, fft.getSampleSize() / 2 - 1);
    }

    private int getBinForBand(int band) {
        // Simple linear mapping as fallback
        int binsPerBand = (fft.getSampleSize() / 2) / NUM_BANDS;
        return band * binsPerBand;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Render spectrum using the SpectrumProcessor
        if (spectrumProcessor != null) {
            spectrumProcessor.renderSpectrum(g, getWidth(), getHeight());
        }
    }

    /**
     * Get the FFT processor sample size
     */
    public int getSampleSize() {
        return fft.getSampleSize();
    }

    /**
     * Reset peak values
     */
    public void resetPeaks() {
        for (int i = 0; i < peaks.length; i++) {
            peaks[i] = 0;
        }
        repaint();
    }

    /**
     * Set background color
     */
    public void setBackgroundColor(Color color) {
        this.bgColor = color;
    }

    /**
     * Set bar colors
     */
    public void setBarColors(Color barColor, Color barColor2) {
        this.barColor = barColor;
        this.barColor2 = barColor2;
    }

    /**
     * Set peak color
     */
    public void setPeakColor(Color peakColor) {
        this.peakColor = peakColor;
    }
}
