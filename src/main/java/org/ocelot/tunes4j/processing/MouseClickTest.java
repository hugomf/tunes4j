package org.ocelot.tunes4j.processing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.GUIUtils;

@SuppressWarnings("serial")
public class MouseClickTest extends Processing {

	int value = 0;
	
	
	@Override
	public void settings() {
		size(100,100);
	}

	@Override
	public void setup() {
		
	}

	@Override
	public void draw() {

		fill(value);
		rect(25, 25, 50, 50);
	}

	@Override
	public void mouseClicked() {

		if (value == 0) {
			value = 255;
		} else {
			value = 0;
		}

	}

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				JFrame frame = new JFrame("MouseClickTest");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				MouseClickTest gp = new MouseClickTest();
				frame.add(gp, BorderLayout.NORTH);
				frame.pack();
				frame.setLocationByPlatform(true);
				GUIUtils.centerWindow(frame);
				frame.setVisible(true);
			}
		});
	}

}
