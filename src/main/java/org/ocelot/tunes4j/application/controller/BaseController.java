package org.ocelot.tunes4j.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Base controller class that provides event publishing capabilities.
 * All controllers in the reactive MVC architecture should extend this class
 * to enable reactive event-driven communication.
 */
@Component
public abstract class BaseController {

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    /**
     * Publish a domain event to all interested components.
     *
     * @param event the event to publish
     */
    protected void publishEvent(Object event) {
        if (eventPublisher != null) {
            eventPublisher.publishEvent(event);
        }
    }
}
