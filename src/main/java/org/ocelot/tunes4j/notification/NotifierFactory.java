package org.ocelot.tunes4j.notification;

import org.springframework.stereotype.Component;

@Component
public class NotifierFactory {
	
	public static Notifier instance() {
		return new SwingNotifier();
	}

}
