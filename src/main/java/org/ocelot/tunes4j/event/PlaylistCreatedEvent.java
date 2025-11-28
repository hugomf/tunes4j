package org.ocelot.tunes4j.event;

import org.ocelot.tunes4j.playlist.model.Playlist;

/**
 * Event fired when a new playlist is created.
 */
public class PlaylistCreatedEvent extends PlaylistDomainEvent {

    private final Playlist newPlaylist;

    public PlaylistCreatedEvent(Object source, Playlist newPlaylist) {
        super(source);
        this.newPlaylist = newPlaylist;
    }

    public Playlist getNewPlaylist() {
        return newPlaylist;
    }

    @Override
    public String toString() {
        return String.format("PlaylistCreatedEvent{playlist=%s}", newPlaylist);
    }
}
