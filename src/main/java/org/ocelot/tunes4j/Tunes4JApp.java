package org.ocelot.tunes4j;

import javax.swing.UnsupportedLookAndFeelException;

import org.ocelot.tunes4j.config.AppConfiguration;
import org.ocelot.tunes4j.config.JpaConfiguration;
import org.ocelot.tunes4j.utils.SingleInstanceVerifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Tunes4JApp {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {

		try {
			
			SingleInstanceVerifier.checkIfRunning();

			@SuppressWarnings("resource")
			AnnotationConfigApplicationContext context = 
					new AnnotationConfigApplicationContext(AppConfiguration.class, JpaConfiguration.class);
			context.registerShutdownHook();
			Tunes4JLauncher service = context.getBean(Tunes4JLauncher.class);
	        service.launch();
		
		} catch(Exception e) { 
			e.printStackTrace();
		} 

	}

}
