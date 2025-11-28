package org.ocelot.tunes4j.event;

import org.springframework.context.ApplicationEvent;

/**
 * Base class for all playlist-related domain events.
 * Extends ApplicationEvent for reactive MVC architecture.
 */
public abstract class PlaylistDomainEvent extends ApplicationEvent {

    public PlaylistDomainEvent(Object source) {
        super(source);
    }
}
