package org.ocelot.tunes4j.event;

/**
 * Event fired when raw audio data is available for processing.
 * This includes FFT data, spectrum analysis, audio waveform data, etc.
 */
public class AudioDataEvent extends AudioDomainEvent {

    public enum DataType {
        SPECTRUM, WAVEFORM, FFT
    }

    private final DataType dataType;
    private final float[] audioData; // Audio data samples
    private final Object processedData; // Processed data like spectrum analysis

    /**
     * Create a new AudioDataEvent.
     *
     * @param source the object that published this event
     * @param dataType the type of audio data
     * @param audioData raw audio samples
     * @param processedData processed audio data, may be null
     */
    public AudioDataEvent(Object source, DataType dataType, float[] audioData, Object processedData) {
        super(source);
        this.dataType = dataType;
        this.audioData = audioData != null ? audioData.clone() : null; // Defensive copy
        this.processedData = processedData;
    }

    /**
     * Create a new AudioDataEvent without processed data.
     *
     * @param source the object that published this event
     * @param dataType the type of audio data
     * @param audioData raw audio samples
     */
    public AudioDataEvent(Object source, DataType dataType, float[] audioData) {
        this(source, dataType, audioData, null);
    }

    /**
     * Get the data type.
     *
     * @return the data type
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Get the raw audio data samples.
     *
     * @return copy of audio data samples, may be null
     */
    public float[] getAudioData() {
        return audioData != null ? audioData.clone() : null;
    }

    /**
     * Get the processed audio data.
     *
     * @return processed data, may be null
     */
    public Object getProcessedData() {
        return processedData;
    }
}
