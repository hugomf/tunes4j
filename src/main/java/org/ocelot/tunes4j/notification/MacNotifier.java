package org.ocelot.tunes4j.notification;

import java.io.IOException;

public class MacNotifier implements Notifier {
	
	
	public void push(String message, String title, String subtitle) {
		
		String cmd = new StringBuilder()
				.append("display notification ")
				.append("\"" + message + "\"")
				.append("with title ")
				.append("\"" + title + "\"")
				.append("subtitle ")
				.append("\"" + subtitle + "\"")
				.toString();
		
		try {
			Runtime.getRuntime().exec(new String[] { "osascript", "-e", cmd });
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}