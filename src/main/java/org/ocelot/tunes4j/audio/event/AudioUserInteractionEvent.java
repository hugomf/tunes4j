package org.ocelot.tunes4j.audio.event;

import org.ocelot.tunes4j.audio.model.Song;
import org.springframework.context.ApplicationEvent;

/**
 * AudioUserInteractionEvent - General user interaction events in Audio context.
 *
 * Observer Pattern: Published by views for user actions like selection, UI changes.
 */
public class AudioUserInteractionEvent extends ApplicationEvent {

    public enum Action {
        SONG_SELECTED,
        VOLUME_CHANGED,
        PLAY_CLICKED,
        PAUSE_CLICKED,
        STOP_CLICKED,
        NEXT_CLICKED,
        PREVIOUS_CLICKED
    }

    private final Song song;
    private final Action action;
    private final Object data;

    private AudioUserInteractionEvent(Object source, Song song, Action action, Object data) {
        super(source);
        this.song = song;
        this.action = action;
        this.data = data;
    }

    public static AudioUserInteractionEvent songSelected(Object source, Song song, Action action) {
        return new AudioUserInteractionEvent(source, song, action, null);
    }

    public static AudioUserInteractionEvent userAction(Object source, Action action, Object data) {
        return new AudioUserInteractionEvent(source, null, action, data);
    }

    public Song getSong() {
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
        return "AudioUserInteractionEvent{action=" + action +
               ", song=" + (song != null ? song.getTitle() : "null") + "}";
    }
}
