package org.ocelot.tunes4j.player;

import java.util.Arrays;

public enum MediaType {
	
	AAC_AUDIO("audio/aac", "audio/aacp"),
	MPEG_AUDIO("audio/mpeg"),
	OGG_AUDIO("application/ogg");

	private String[] media;
	
	MediaType(String... media) {
		this.media = media;
	}
	
	
	boolean is(String format) {
		return Arrays.asList(this.media).contains(format);
	}
}
