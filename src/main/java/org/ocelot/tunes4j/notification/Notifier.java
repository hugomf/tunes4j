package org.ocelot.tunes4j.notification;

import java.awt.Image;

public interface Notifier {

	public void push(String message, String title, String subtitle);
	
	public void push(Image image, String message, String title, String subtitle);

	
}
