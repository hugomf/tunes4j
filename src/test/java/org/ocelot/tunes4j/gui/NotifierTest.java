package org.ocelot.tunes4j.gui;

import org.ocelot.tunes4j.notification.NotifierFactory;


public class NotifierTest {

	public static void main(String[] args) throws Throwable {

		NotifierFactory.instance().push("Hola1", "Mundo1", "subtitulo1");
		NotifierFactory.instance().push("Hola2", "Mundo2", "subtitulo2");
		NotifierFactory.instance().push("Hola3", "Mundo3", "subtitulo3");
		NotifierFactory.instance().push("Hola4", "Mundo4", "subtitulo4");
		
	}
    
	
	

}