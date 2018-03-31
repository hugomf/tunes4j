package org.ocelot.tunes4j.utils;

import org.junit.Test;
import org.ocelot.tunes4j.notification.Notifier;
import org.ocelot.tunes4j.notification.NotifierFactory;

public class NotificationTest {
	
	@Test
	public void test(){
		
		Notifier notifier = NotifierFactory.instance();
		notifier.push("Cancion", "Tunes4J", "Autor");
		
	}
	

}
