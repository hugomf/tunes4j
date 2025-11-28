package org.ocelot.tunes4j.library.event;

import org.ocelot.tunes4j.library.model.LibrarySong;
import org.springframework.context.ApplicationEvent;

/**
 * LibrarySongSelectedEvent - Triggered when user double-clicks to activate a song from library.
 *
 * Observer Pattern: Published by SongListView, consumed by AudioController.
 * This bridges Library bounded context to Audio bounded context.
 */
public class LibrarySongSelectedEvent extends ApplicationEvent {

    private final LibrarySong selectedSong;

    public LibrarySongSelectedEvent(Object source, LibrarySong selectedSong) {
        super(source);
        this.selectedSong = selectedSong;
    }

    public LibrarySong getSelectedSong() {
        return selectedSong;
    }

    @Override
    public String toString() {
        return "LibrarySongSelectedEvent{song='" + selectedSong.getTitle() + "'}";
    }
}
