package org.ocelot.tunes4j.event;

import org.ocelot.tunes4j.playlist.model.Playlist;

/**
 * Event fired when a playlist is selected from the playlist tree.
 * This enables reactive updates of components that display playlist contents.
 */
public class PlaylistSelectedEvent extends PlaylistDomainEvent {

    private final Playlist selectedPlaylist;

    /**
     * Create a new PlaylistSelectedEvent.
     *
     * @param source the object that published this event
     * @param selectedPlaylist the playlist that was selected
     */
    public PlaylistSelectedEvent(Object source, Playlist selectedPlaylist) {
        super(source);
        this.selectedPlaylist = selectedPlaylist;
    }

    /**
     * Get the selected playlist.
     *
     * @return the selected playlist
     */
    public Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }
}
