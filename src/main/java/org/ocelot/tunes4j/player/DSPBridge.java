package org.ocelot.tunes4j.player;

import javax.sound.sampled.SourceDataLine;

import org.ocelot.tunes4j.dsp.KJDSPAudioDataConsumer;
import org.ocelot.tunes4j.dsp.KJDigitalSignalProcessor;

/**
 * Bridge between BasicPlayer audio output and KJ DSP system.
 * Connects audio data from BasicPlayer to the DSP visualization pipeline.
 *
 * @author Cline
 */
public class DSPBridge {

    /** DSP audio data consumer */
    private KJDSPAudioDataConsumer dspConsumer;

    /** Sample size for DSP processing */
    private static final int SAMPLE_SIZE = 1024;

    /** Frames per second for DSP processing */
    private static final int FPS = 70;

    public DSPBridge() {
        dspConsumer = new KJDSPAudioDataConsumer(SAMPLE_SIZE, FPS);
        // Initialize DSP consumer without SourceDataLine for direct audio data feeding
    }

    /**
     * Connect the DSP bridge to an audio SourceDataLine (legacy method)
     */
    public void connect(SourceDataLine sourceDataLine) {
        if (dspConsumer != null && sourceDataLine != null) {
            dspConsumer.start(sourceDataLine);
        }
    }

    /**
     * Initialize DSP consumer without SourceDataLine
     * Used when we feed audio data directly
     */
    public void initialize() {
        // For BasicPlayer interface, we skip the SourceDataLine connection
        // and feed audio data directly via writeAudioData
    }

    /**
     * Disconnect from current audio stream
     */
    public void disconnect() {
        if (dspConsumer != null) {
            dspConsumer.stop();
        }
    }

    /**
     * Add a DSP processor to the pipeline
     */
    public void addProcessor(KJDigitalSignalProcessor processor) {
        if (dspConsumer != null) {
            dspConsumer.add(processor);
        }
    }

    /**
     * Remove a DSP processor from the pipeline
     */
    public void removeProcessor(KJDigitalSignalProcessor processor) {
        if (dspConsumer != null) {
            dspConsumer.remove(processor);
        }
    }

    /**
     * Feed audio data to the DSP system
     */
    public void feedAudioData(byte[] audioData, int offset, int length) {
        if (dspConsumer != null && audioData != null && length > 0) {
            dspConsumer.writeAudioData(audioData, offset, length);
        }
    }

    /**
     * Feed audio data to the DSP system (convenience method)
     */
    public void feedAudioData(byte[] audioData) {
        if (dspConsumer != null && audioData != null && audioData.length > 0) {
            dspConsumer.writeAudioData(audioData);
        }
    }

    /**
     * Check if DSP bridge is connected
     */
    public boolean isConnected() {
        return dspConsumer != null;
    }

    /**
     * Get the DSP audio consumer (for advanced usage)
     */
    public KJDSPAudioDataConsumer getDspConsumer() {
        return dspConsumer;
    }
}
