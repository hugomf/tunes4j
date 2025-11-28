package org.ocelot.tunes4j.event;

import org.ocelot.tunes4j.playlist.model.Playlist;

/**
 * Event fired when a song is removed from a playlist.
 */
public class PlaylistSongRemovedEvent extends PlaylistDomainEvent {

    private final Playlist updatedPlaylist;
    private final String removedSongId;

    public PlaylistSongRemovedEvent(Object source, Playlist updatedPlaylist, String removedSongId) {
        super(source);
        this.updatedPlaylist = updatedPlaylist;
        this.removedSongId = removedSongId;
    }

    public Playlist getUpdatedPlaylist() {
        return updatedPlaylist;
    }

    public String getRemovedSongId() {
        return removedSongId;
    }

    @Override
    public String toString() {
        return String.format("PlaylistSongRemovedEvent{playlist='%s', song='%s'}",
                           updatedPlaylist.getName(), removedSongId);
    }
}
