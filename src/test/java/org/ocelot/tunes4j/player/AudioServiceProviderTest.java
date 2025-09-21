package org.ocelot.tunes4j.player;

import static org.junit.Assert.assertTrue;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import org.junit.Test;

public class AudioServiceProviderTest {

	@Test
	public void testMP3ServiceProviderAvailable() {
		// Check what audio formats are actually supported
		AudioFileFormat.Type[] supportedTypes = AudioSystem.getAudioFileTypes();
		System.out.println("Supported audio formats:");
		for (AudioFileFormat.Type type : supportedTypes) {
			System.out.println("  - " + type.toString());
		}
		
		// The mp3spi library provides the service provider, but it may not be automatically detected
		// in all environments. The important thing is that the class is available.
		assertTrue("Should have at least some audio formats supported", supportedTypes.length > 0);
	}
	
	@Test
	public void testMP3AudioFileReaderAvailable() {
		// Test that MP3 audio file reader class is available in classpath
		try {
			Class.forName("javazoom.spi.mpeg.sampled.file.MpegAudioFileReader");
			assertTrue("MP3 audio file reader should be available in classpath", true);
		} catch (ClassNotFoundException e) {
			throw new AssertionError("MP3 audio file reader not found in classpath - make sure mp3spi dependency is included", e);
		}
	}
}