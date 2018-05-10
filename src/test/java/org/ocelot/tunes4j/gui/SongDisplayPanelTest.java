package org.ocelot.tunes4j.gui;

import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.ResourceLoader;

public class SongDisplayPanelTest {
	
	public static void main(String[] args) {
		SongDisplayPanel panel = new SongDisplayPanel();
		
		
		panel.setArtwork(ResourceLoader.ICON_GEAR.getImage());
		panel.setSongTitle("Titulo");
		panel.setArtist("Hasta la vista Artista");
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
}
