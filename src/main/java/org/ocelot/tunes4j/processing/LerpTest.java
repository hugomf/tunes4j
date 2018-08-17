package org.ocelot.tunes4j.processing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.GUIUtils;

@SuppressWarnings("serial")
public class LerpTest extends Processing {

	float x;
	float y;

	@Override
	public void settings() {
		size(640, 360);
	}

	@Override
	public void setup() {
		// noStroke();
	}

	@Override
	public void draw() {

		background(51);
		// lerp() calculates a number between two numbers at a specific increment.
		// The amt parameter is the amount to interpolate between the two values
		// where 0.0 equal to the first point, 0.1 is very near the first point, 0.5
		// is half-way in between, etc.

		// Here we are moving 5% of the way to the mouse location each frame
		x = lerp(x, mouseX, 0.05f);
		y = lerp(y, mouseY, 0.05f);

		fill(255);
		// stroke(255);
		ellipse(x, y, 66, 66);
	}

	@Override
	public void mouseClicked() {

	}

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				JFrame frame = new JFrame("LerpTest");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				LerpTest gp = new LerpTest();
				frame.add(gp, BorderLayout.NORTH);
				frame.pack();
				frame.setLocationByPlatform(true);
				GUIUtils.centerWindow(frame);
				frame.setVisible(true);
			}
		});
	}

}
