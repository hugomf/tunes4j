package org.ocelot.tunes4j.effects.events;

public class UpdateValueEvent<T> extends EffectEvent {
	
	private static final long serialVersionUID = 1L;

	private T value;
	
	public UpdateValueEvent(Object source) {
		super(source);
	}
	
	public UpdateValueEvent(Object source, T value) {
		super(source);
		this.value = value;
	}
	
	public T getValue() {
		return this.value;
	}

	
}
