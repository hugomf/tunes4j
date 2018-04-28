package org.ocelot.tunes4j.gui;

import java.awt.FlowLayout;

import javax.swing.JFrame;

import org.ocelot.tunes4j.dto.RadioStation;
import org.ocelot.tunes4j.utils.GUIUtils;

public class RadioPlayerPanelTest {
	
	public static void main(String[] args) {
		

		RadioPlayerPanel playerPanel = new RadioPlayerPanel();
		playerPanel.setRadioStation(createRadioStation());
		JFrame frame = new JFrame("Player");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.add(playerPanel.getPlayerPanel());
		frame.add(playerPanel.getSliderPanel());
		frame.pack();
		GUIUtils.centerWindow(frame);
		frame.setVisible(true);

	}

	private static RadioStation createRadioStation() {
		RadioStation station = new RadioStation();
		station.setUrl("http://18803.live.streamtheworld.com:80/XHMVSFM_SC");
		station.setName("Name");
		station.setGenre("Latin");
		station.setDescription("Description");
		return station;
	}

}
