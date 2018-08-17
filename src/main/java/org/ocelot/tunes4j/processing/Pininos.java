package org.ocelot.tunes4j.processing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.GUIUtils;

@SuppressWarnings("serial")
public class Pininos extends Processing {

	@Override
	public void settings() {
		size(480, 120);
	}

	@Override
	public void setup() {
		noStroke();
	}

	@Override
	public void draw() {
		if (mousePressed) {
			fill(0);
		} else {
			fill(255);
		}
		ellipse(mouseX, mouseY, 80, 80);
	}

	@Override
	public void mouseClicked() {

	}

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				JFrame frame = new JFrame("Pininos");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Pininos gp = new Pininos();
				frame.add(gp, BorderLayout.NORTH);
				frame.pack();
				frame.setLocationByPlatform(true);
				GUIUtils.centerWindow(frame);
				frame.setVisible(true);
			}
		});
	}

}
