package org.ocelot.tunes4j.event;

import org.ocelot.tunes4j.playlist.model.Playlist;

/**
 * Event fired when a song is added to a playlist.
 */
public class PlaylistSongAddedEvent extends PlaylistDomainEvent {

    private final Playlist updatedPlaylist;
    private final String addedSongId;

    public PlaylistSongAddedEvent(Object source, Playlist updatedPlaylist, String addedSongId) {
        super(source);
        this.updatedPlaylist = updatedPlaylist;
        this.addedSongId = addedSongId;
    }

    public Playlist getUpdatedPlaylist() {
        return updatedPlaylist;
    }

    public String getAddedSongId() {
        return addedSongId;
    }

    @Override
    public String toString() {
        return String.format("PlaylistSongAddedEvent{playlist='%s', song='%s'}",
                           updatedPlaylist.getName(), addedSongId);
    }
}
