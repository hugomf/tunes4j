package org.ocelot.tunes4j.gui;

import java.awt.FlowLayout;

import javax.swing.JFrame;

import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.utils.GUIUtils;

public class PlayerPanelTest {
	
	public static void main(String[] args) {
		
		PlayerPanel playerPanel = new PlayerPanel();
		

	}

	
	
	
	class PlayerPanelFrame {
		
		PlayerPanel playerPanel = new PlayerPanel();
		
		public PlayerPanelFrame() {
			initialize();
		}

		private void initialize() {
			playerPanel.setSong(createSong());
			JFrame frame = new JFrame("Player");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new FlowLayout());
			frame.add(playerPanel.getPlayerPanel());
			frame.add(playerPanel.getMainDisplayPanel());
			frame.pack();
			GUIUtils.centerWindow(frame);
			frame.setVisible(true);
			System.out.println("TEST!");
				
		}
		
		private Song createSong() {
			Song song = new Song();
			song.setTitle("I Want To Spend My Lifetime Loving You");
			song.setArtist("Freddy Mercury");
			song.setAlbum("Lo Mejor de la Salsa 90's");
			song.setPath("/Users/hugo/eclipse-workspace2/LaunchAndLearn/resources/");
			song.setFileName("amiga.mp3");
			return song;
		}
		
		
	}

}
