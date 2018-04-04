package org.ocelot.tunes4j.gui;

import java.io.File;

import javax.swing.JFrame;

import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;
import org.ocelot.tunes4j.utils.GUIUtils;

public class PlayerPanelTest {
	
	public static void main(String[] args) {

		PlayerPanel playerPanel = new PlayerPanel(new ApplicationWindow() {
			
			private static final long serialVersionUID = 1L;
			
			Tunes4JAudioPlayer player = new Tunes4JAudioPlayer();

			public Tunes4JAudioPlayer getPlayer() {
				player.open(new File("/Users/hugo/eclipse-workspace2/LaunchAndLearn/resources/amiga.mp3"));
				return player;
			};
			
			public MediaTable getMediaTable() {
				return new MediaTable() {
					@Override
					public void playSelectedRow() {
						player.play();
					}
				};
			}

		});

		JFrame frame = new JFrame("Player");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(playerPanel);
		frame.pack();
		GUIUtils.centerWindow(frame);
		frame.setVisible(true);

	}

}
