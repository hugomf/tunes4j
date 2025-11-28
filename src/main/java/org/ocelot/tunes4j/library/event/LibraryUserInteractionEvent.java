package org.ocelot.tunes4j.library.event;

import org.ocelot.tunes4j.library.model.LibrarySong;
import org.springframework.context.ApplicationEvent;

/**
 * LibraryUserInteractionEvent - General user interaction events in Library context.
 *
 * Observer Pattern: Published by library views for user actions like selection, UI changes.
 */
public class LibraryUserInteractionEvent extends ApplicationEvent {

    public enum Action {
        SONG_SELECTED,
        ADD_TO_LIBRARY,
        REMOVE_FROM_LIBRARY,
        MARK_FAVORITE,
        CLEAR_SEARCH,
        FILTER_APPLIED
    }

    private final LibrarySong song;
    private final Action action;
    private final Object data;

    private LibraryUserInteractionEvent(Object source, LibrarySong song, Action action, Object data) {
        super(source);
        this.song = song;
        this.action = action;
        this.data = data;
    }

    public static LibraryUserInteractionEvent songSelected(Object source, LibrarySong song, Action action) {
        return new LibraryUserInteractionEvent(source, song, action, null);
    }

    public static LibraryUserInteractionEvent userAction(Object source, Action action, Object data) {
        return new LibraryUserInteractionEvent(source, null, action, data);
    }

    public LibrarySong getSong() {
        return song;
    }

    public Action getAction() {
        return action;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "LibraryUserInteractionEvent{action=" + action +
               ", song=" + (song != null ? song.getTitle() : "null") + "}";
    }
}
