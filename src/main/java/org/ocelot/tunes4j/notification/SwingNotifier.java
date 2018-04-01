package org.ocelot.tunes4j.notification;

import java.awt.Image;

import org.ocelot.tunes4j.utils.ResourceLoader;

public class SwingNotifier implements Notifier {

	private CustomNotifier notifier = new CustomNotifier();
	
	@Override
	public void push(String message, String title, String subtitle) {

		notifier.display(null, title, subtitle, message);
	}

	@Override
	public void push(Image image, String message, String title, String subtitle) {
		if(image==null) {
			image = ResourceLoader.ICON_APPICON.getImage();
		}
		notifier.display(image, title, subtitle, message);
	}

}
