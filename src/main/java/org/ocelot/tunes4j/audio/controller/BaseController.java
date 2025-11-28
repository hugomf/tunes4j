package org.ocelot.tunes4j.audio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEvent;

/**
 * Base Controller - Provides common functionality for reactive controllers.
 * Handles event publishing and basic reactive patterns used across bounded contexts.
 */
public abstract class BaseController {

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    /**
     * Publish an event to all registered listeners (Observer Pattern).
     */
    protected void publishEvent(ApplicationEvent event) {
        if (eventPublisher != null) {
            System.out.println("📢 EVENT: " + event.getClass().getSimpleName());
            eventPublisher.publishEvent(event);
        }
    }
}
