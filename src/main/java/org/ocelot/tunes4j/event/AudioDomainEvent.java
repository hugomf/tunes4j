package org.ocelot.tunes4j.event;

import org.springframework.context.ApplicationEvent;

/**
 * Base class for all audio domain events in the reactive MVC architecture.
 * All domain events extend Spring's ApplicationEvent for reactive publishing.
 */
public abstract class AudioDomainEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AudioDomainEvent(Object source) {
        super(source);
    }
}
