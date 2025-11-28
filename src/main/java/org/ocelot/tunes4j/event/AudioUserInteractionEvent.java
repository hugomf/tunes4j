package org.ocelot.tunes4j.event;

/**
 * Event fired when a user interacts with audio controls.
 * This includes play, pause, stop, next, previous, volume changes, etc.
 */
public class AudioUserInteractionEvent extends AudioDomainEvent {

    public enum InteractionType {
        PLAY, PAUSE, STOP, NEXT, PREVIOUS, SEEK, VOLUME_CHANGE, MUTE
    }

    private final InteractionType interactionType;
    private final Object data; // Optional additional data for the interaction

    /**
     * Create a new AudioUserInteractionEvent.
     *
     * @param source the object that published this event
     * @param interactionType the type of user interaction
     * @param data optional additional data for the interaction
     */
    public AudioUserInteractionEvent(Object source, InteractionType interactionType, Object data) {
        super(source);
        this.interactionType = interactionType;
        this.data = data;
    }

    /**
     * Create a new AudioUserInteractionEvent without additional data.
     *
     * @param source the object that published this event
     * @param interactionType the type of user interaction
     */
    public AudioUserInteractionEvent(Object source, InteractionType interactionType) {
        this(source, interactionType, null);
    }

    /**
     * Get the interaction type.
     *
     * @return the interaction type
     */
    public InteractionType getInteractionType() {
        return interactionType;
    }

    /**
     * Get the optional interaction data.
     *
     * @return the interaction data, may be null
     */
    public Object getData() {
        return data;
    }
}
