package org.ocelot.tunes4j.gui;

/**
 * Spectrum Analyzer component - replacement for the original sinusoidal wave display.
 * This class extends JPanelSpectrum to provide a visual spectrum analyzer.
 *
 * @author Cline
 */
public class JPanelSoundWave extends JPanelSpectrum {

    private static final long serialVersionUID = 1L;

    public JPanelSoundWave() {
        super();
        // Configure spectrum analyzer for audio visualization
        setBackgroundColor(new java.awt.Color(15, 15, 25));
        setBarColors(new java.awt.Color(0, 255, 128), new java.awt.Color(0, 128, 255));
        setPeakColor(new java.awt.Color(255, 255, 255));
    }
}
