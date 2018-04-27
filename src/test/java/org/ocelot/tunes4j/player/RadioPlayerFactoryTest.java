package org.ocelot.tunes4j.player;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RadioPlayerFactoryTest {
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void shouldGetAACPlayerInstance() throws Exception {
		RadioPlayer accPlayerInstance = RadioPlayerFactory.getInstance("audio/aac");
		assertThat(accPlayerInstance, instanceOf(AACStreamPlayer.class));
		
	}

	@Test
	public void shouldGetAACInstance() throws Exception {
		RadioPlayer accPlayerInstance = RadioPlayerFactory.getInstance("audio/aacp");
		assertThat(accPlayerInstance, instanceOf(AACStreamPlayer.class));
		
	}
	
	@Test
	public void shouldGetMp3Instance() throws Exception {
		RadioPlayer accPlayerInstance = RadioPlayerFactory.getInstance("audio/mpeg");
		assertThat(accPlayerInstance, instanceOf(Mp3StreamPlayer.class));
		
	}
	
	@Test
	public void shouldGetOggInstance() throws Exception {
		RadioPlayer accPlayerInstance = RadioPlayerFactory.getInstance("application/ogg");
		assertThat(accPlayerInstance, instanceOf(OggStreamPlayer.class));
		
	}
	
	@Test
	public void shouldThrowExceptionWhenContentTypeNotSupported() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid Format:audio/mp4");
		
		RadioPlayer accPlayerInstance = RadioPlayerFactory.getInstance("audio/mp4");
		assertThat(accPlayerInstance, instanceOf(OggStreamPlayer.class));
		
	}
	
}
