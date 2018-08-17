package org.ocelot.tunes4j.gui;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;


public class jlpTest
{
private static String filename = "../resources/test.mp3";
	
	public static void testPlay()
	{
		
		try
		{
			AdvancedPlayer player = new AdvancedPlayer(jlpTest.class.getResourceAsStream(filename));
			player.play();
		}
		catch (JavaLayerException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		testPlay();
	}
}
