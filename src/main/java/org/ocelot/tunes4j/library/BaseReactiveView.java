package org.ocelot.tunes4j.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * BaseReactiveView - Base class for reactive views in Library bounded context.
 *
 * Provides Observer Pattern communication through ApplicationEventPublisher.
 * All library view components should extend this class.
 */
@Component
public abstract class BaseReactiveView {

    @Autowired
    protected ApplicationEventPublisher publisher;

    protected BaseReactiveView() {
        // Constructor for Spring
    }

    public ApplicationEventPublisher getEventPublisher() {
        return publisher;
    }
}
