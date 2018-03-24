package org.ocelot.tunes4j.notification;

import java.io.IOException;

public class UbuntuNotifier implements Notifier {

	@Override
	public void push(String message, String title, String subtitle) {

		String cmd = new StringBuilder()
				.append("\"" + title)
				.append("-" + subtitle)
				.append("\"")
				.append("\"" + message + "\"")
				.toString();
		
		try {
			Runtime.getRuntime().exec(new String[] { "/usr/bin/notify-send", "-t", "10000", cmd });
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
