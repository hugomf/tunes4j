package org.ocelot.tunes4j.player;

import java.io.InputStream;

public interface RadioPlayer {
	
	public void open(InputStream stream) throws Exception;
	
	public void play();
	
	public void stop();
	
}
