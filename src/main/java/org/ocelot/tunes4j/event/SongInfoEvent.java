package org.ocelot.tunes4j.event;

import org.ocelot.tunes4j.audio.model.Song;

/**
 * Event fired when song information is updated or displayed.
 * This includes metadata changes, song display updates, etc.
 */
public class SongInfoEvent extends AudioDomainEvent {

    private final Song song;
    private final boolean metadataChanged;

    /**
     * Create a new SongInfoEvent.
     *
     * @param source the object that published this event
     * @param song the song whose information changed
     * @param metadataChanged whether the song metadata was actually modified
     */
    public SongInfoEvent(Object source, Song song, boolean metadataChanged) {
        super(source);
        this.song = song;
        this.metadataChanged = metadataChanged;
    }

    /**
     * Create a new SongInfoEvent for display purposes without metadata change.
     *
     * @param source the object that published this event
     * @param song the song being displayed
     */
    public SongInfoEvent(Object source, Song song) {
        this(source, song, false);
    }

    /**
     * Get the song.
     *
     * @return the song
     */
    public Song getSong() {
        return song;
    }

    /**
     * Check if the song metadata was changed.
     *
     * @return true if metadata was changed, false otherwise
     */
    public boolean isMetadataChanged() {
        return metadataChanged;
    }
}
