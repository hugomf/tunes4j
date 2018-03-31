package org.ocelot.tunes4j.notification;

import org.ocelot.tunes4j.utils.ResourceLoader;

import com.birosoft.liquid.LiquidLookAndFeel;

public class SwingNotifier implements Notifier {

	private CustomNotifier notifier = new CustomNotifier();
	
	@Override
	public void push(String message, String title, String subtitle) {

		notifier.display(ResourceLoader.ICON_APPICON.getImage(), title, subtitle, message);
	}

}
