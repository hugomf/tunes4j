package org.ocelot.tunes4j.audio.event;

import org.springframework.context.ApplicationEvent;

/**
 * AudioPlaybackStateEvent - Notifies of playback state changes.
 * TODO: Implement proper playback states.
 */
public class AudioPlaybackStateEvent extends ApplicationEvent {

    private final String state;

    public AudioPlaybackStateEvent(Object source, String state) {
        super(source);
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
