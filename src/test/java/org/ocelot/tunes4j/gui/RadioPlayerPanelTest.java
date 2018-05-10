package org.ocelot.tunes4j.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
		frame.add(playerPanel.getMainDisplayPanel());
		
		JButton showButton = new JButton("Show");
		showButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				playerPanel.show();
			}
		});
		JButton hideButton = new JButton("Hide");
		hideButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				playerPanel.hide();
			}
		});
		
		frame.add(showButton);
		frame.add(hideButton);
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
