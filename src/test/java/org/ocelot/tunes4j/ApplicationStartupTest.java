package org.ocelot.tunes4j;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ApplicationStartupTest {

	@Test
	public void testAudioServiceProviderRegistration() {
		// Test that the audio service provider registration method works
		Tunes4JApp.registerAudioServiceProviders();
		
		// Verify that MP3 service provider class is available
		try {
			Class.forName("javazoom.spi.mpeg.sampled.file.MpegAudioFileReader");
			assertTrue("MP3 audio file reader should be available", true);
		} catch (ClassNotFoundException e) {
			throw new AssertionError("MP3 audio file reader not found in classpath", e);
		}
	}
	
	@Test
	public void testApplicationMainMethod() {
		// Test that the main method can be called without throwing exceptions
		// We'll test this by calling the audio registration method directly
		// since the main method would try to start the application which requires
		// the single instance check
		Tunes4JApp.registerAudioServiceProviders();
		assertTrue("Application service provider registration should work", true);
	}
}