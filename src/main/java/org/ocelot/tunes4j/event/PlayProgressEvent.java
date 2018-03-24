package org.ocelot.tunes4j.event;

import java.util.EventObject;

public class PlayProgressEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private Long currentProgress = -1l;

	public PlayProgressEvent(Object source) {
		super(source);
		if (source instanceof Long) {
			this.currentProgress = (Long) source;
		}
	}

	public Long getCurrentProgress() {
		return this.currentProgress ;
	}
	
}
