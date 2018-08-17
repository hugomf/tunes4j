package org.ocelot.tunes4j.player;

import static org.ocelot.tunes4j.player.MediaType.AAC_AUDIO;
import static org.ocelot.tunes4j.player.MediaType.MPEG_AUDIO;
import static org.ocelot.tunes4j.player.MediaType.OGG_AUDIO;

public class RadioPlayerFactory {
	
	
	
	private static final Mp3StreamPlayer mp3Player = new Mp3StreamPlayer();
	
	private static final AACStreamPlayer aacPlayer = new AACStreamPlayer();
	
	private static final OggStreamPlayer oggPlayer = new OggStreamPlayer();
	
	public static RadioPlayer getInstance(String contentType) {
		
		if (AAC_AUDIO.is(contentType)) return aacPlayer;
		
		if (MPEG_AUDIO.is(contentType)) return mp3Player;
		
		if (OGG_AUDIO.is(contentType)) return oggPlayer;
		
		throw new IllegalArgumentException("Invalid Format:" + contentType);
	}
}
