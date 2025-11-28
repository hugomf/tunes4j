package org.ocelot.tunes4j.event;

/**
 * Event fired when the audio playback state changes.
 * This includes state transitions like PLAYING, PAUSED, STOPPED, etc.
 *
 * Note: Currently accepting both dto.Song and audio.model.Song for compatibility.
 */
public class AudioPlaybackStateEvent extends AudioDomainEvent {

    public enum PlaybackState {
        PLAYING, PAUSED, STOPPED, LOADING, ERROR
    }

    private final PlaybackState state;
    private final Object currentSong; // Can be either dto.Song or audio.model.Song
    private final Integer currentPosition; // Position in milliseconds

    /**
     * Create a new AudioPlaybackStateEvent.
     *
     * @param source the object that published this event
     * @param state the new playback state
     * @param currentSong the currently playing song, may be null (any Song type)
     * @param currentPosition current position in milliseconds, may be null
     */
    public AudioPlaybackStateEvent(Object source, PlaybackState state, Object currentSong, Integer currentPosition) {
        super(source);
        this.state = state;
        this.currentSong = currentSong;
        this.currentPosition = currentPosition;
    }

    /**
     * Create a new AudioPlaybackStateEvent without position.
     *
     * @param source the object that published this event
     * @param state the new playback state
     * @param currentSong the currently playing song, may be null
     */
    public AudioPlaybackStateEvent(Object source, PlaybackState state, Object currentSong) {
        this(source, state, currentSong, null);
    }

    /**
     * Get the playback state.
     *
     * @return the playback state
     */
    public PlaybackState getState() {
        return state;
    }

    /**
     * Get the currently playing song.
     *
     * @return the currently playing song, may be null (Object type - can be dto.Song or audio.model.Song)
     */
    public Object getCurrentSong() {
        return currentSong;
    }

    /**
     * Get the current playback position in milliseconds.
     *
     * @return the current position, may be null
     */
    public Integer getCurrentPosition() {
        return currentPosition;
    }
}
