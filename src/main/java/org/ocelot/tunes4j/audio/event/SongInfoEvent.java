package org.ocelot.tunes4j.audio.event;

import org.ocelot.tunes4j.audio.model.Song;
import org.springframework.context.ApplicationEvent;

/**
 * SongInfoEvent - Notifies when song information is updated.
 * TODO: Implement song metadata changes.
 */
public class SongInfoEvent extends ApplicationEvent {

    private final Song song;

    public SongInfoEvent(Object source, Song song) {
        super(source);
        this.song = song;
    }

    public Song getSong() {
        return song;
    }
}
