package org.ocelot.tunes4j.processing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.GUIUtils;

@SuppressWarnings("serial")
public class CheckCircles extends Processing {

	@Override
	public void settings() {
		size(480, 120);
	}

	@Override
	public void setup() {
		
		ellipseMode(RADIUS);  // Set ellipseMode to RADIUS
		fill(255);  // Set fill to white
		ellipse(50, 50, 30, 30);  // Draw white ellipse using RADIUS mode

		ellipseMode(CENTER);  // Set ellipseMode to CENTER
		fill(100);  // Set fill to gray
		ellipse(50, 50, 30, 30);  // Draw gray ellipse using CENTER mode

		ellipseMode(CORNER);  // Set ellipseMode is CORNER
		fill(255);  // Set fill to white
		ellipse(25, 25, 50, 50);  // Draw white ellipse using CORNER mode
//
		ellipseMode(CORNERS);  // Set ellipseMode to CORNERS
		fill(100);  // Set fill to gray
		ellipse(25, 25, 50, 50);  // Draw gray ellipse using CORNERS mode
		
	}

	public void draw() {
		
	}

	@Override
	public void mouseClicked() {

	}

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				JFrame frame = new JFrame("LerpTest");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				CheckCircles gp = new CheckCircles();
				frame.add(gp, BorderLayout.NORTH);
				frame.pack();
				frame.setLocationByPlatform(true);
				GUIUtils.centerWindow(frame);
				frame.setVisible(true);
			}
		});
	}

}
