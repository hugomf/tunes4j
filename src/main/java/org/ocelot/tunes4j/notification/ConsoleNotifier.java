package org.ocelot.tunes4j.notification;

public class ConsoleNotifier implements Notifier {

	@Override
	public void push(String message, String title, String subtitle) {


		String cmd = new StringBuilder()
				.append("Notify: ")
				.append("\"" + title)
				.append("-" + subtitle)
				.append("\"")
				.append("\"" + message + "\"")
				.toString();
		
		System.out.println("Notification System not compatible!!!");
		System.out.println(cmd);
		
	}

}
