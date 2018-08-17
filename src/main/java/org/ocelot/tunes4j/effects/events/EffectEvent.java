package org.ocelot.tunes4j.effects.events;

import java.util.EventObject;

public class EffectEvent extends EventObject {
	
	private static final long serialVersionUID = 1L;

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public EffectEvent(Object source) {
		super(source);
	}
	
	public EffectEvent(Object source,String message) {
		super(source);
		this.message = message;
	}
	
}
