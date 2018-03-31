package org.ocelot.tunes4j.notification;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.net.URL;

import org.ocelot.tunes4j.utils.FileUtils;

public class WindowsSystemTrayNotifier implements Notifier {

	@Override
	public void push(String message, String title, String subtitle) {
		 if (SystemTray.isSupported()) {

			 displayTray(message, title, subtitle);
			 
        } else {
            System.err.println("System tray not supported!");
        }

	}
	
	 public void displayTray(String message, String title, String subtitle) {

	    try {

	    	SystemTray tray = SystemTray.getSystemTray();
	    	URL url = FileUtils.getUrl("/icons/prueba.png");
	    	Image image = Toolkit.getDefaultToolkit().getImage(url);
	    	TrayIcon trayIcon = new TrayIcon(image);
	    	trayIcon.setImageAutoSize(true);
	    	trayIcon.setToolTip(subtitle + " - " + message);
			tray.add(trayIcon);
			trayIcon.displayMessage(subtitle, message, MessageType.NONE);
			
	    } catch (Exception e) {
			e.printStackTrace();
		}
	 }
	
	
	
	
	///home/osboxes/.gradle/caches/modules-2/files-2.1/org.projectlombok/lombok/1.16.16/

}
