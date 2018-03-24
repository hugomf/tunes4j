package test;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

public class MP3PlayerTest {

	
	
	@Test
	public void shouldPlay() throws FileNotFoundException {

		MP3Player player = MP3Player.getPlayer();
		player.play(new File("/Users/hugo/eclipse-workspace2/tunes4j/src/main/resources/amiga.mp3"), new Callback<Exception>() {
			@Override
			public void callback(Exception e) {
				e.printStackTrace();
			}
			
		});
	}

}
