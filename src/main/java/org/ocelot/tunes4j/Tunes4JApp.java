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
			// Register audio service providers explicitly for Java 17 compatibility
			registerAudioServiceProviders();
			
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
	
	public static void registerAudioServiceProviders() {
		try {
			// Force registration of MP3 SPI service providers
			Class<?> mp3spiClass = Class.forName("javazoom.spi.mpeg.sampled.file.MpegAudioFileReader");
			System.out.println("MP3 SPI service provider loaded: " + mp3spiClass.getName());
			
			// Also try to load other audio service providers if available
			try {
				Class<?> vorbisClass = Class.forName("org.jcraft.jorbis.spi.vorbis.VorbisAudioFileReader");
				System.out.println("Vorbis service provider loaded: " + vorbisClass.getName());
			} catch (ClassNotFoundException e) {
				System.out.println("Vorbis service provider not available");
			}
			
		} catch (ClassNotFoundException e) {
			System.err.println("MP3 SPI service provider not found. Audio playback may not work.");
			System.err.println("Make sure mp3spi library is included in the classpath.");
		}
	}

}
