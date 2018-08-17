package org.ocelot.tunes4j.processing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.GUIUtils;

@SuppressWarnings("serial")
public class PolarToCartesian extends Processing {

	float r;

	// Angle and angular velocity, accleration
	float theta;
	float theta_vel;
	float theta_acc;

	@Override
	public void settings() {
		size(640, 360);
	}

	@Override
	public void setup() {

		// Initialize all values
		r = height * 0.45f;
		theta = 0;
		theta_vel = 0;
		theta_acc = 0.0001f;

	}

	@Override
	public void draw() {

		background(0);

		// Translate the origin point to the center of the screen
		translate(width / 2, height / 2);

		// Convert polar to cartesian
		float x = r * cos(theta);
		float y = r * sin(theta);

		// Draw the ellipse at the cartesian coordinate
		ellipseMode(CENTER);
		// noStroke();
		fill(200);
		ellipse(x, y, 32, 32);

		// Apply acceleration and velocity to angle (r remains static in this example)
		theta_vel += theta_acc;
		theta += theta_vel;
	}

	@Override
	public void mouseClicked() {

	}

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				JFrame frame = new JFrame("PolarToCartesian");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				PolarToCartesian gp = new PolarToCartesian();
				frame.add(gp, BorderLayout.NORTH);
				frame.pack();
				frame.setLocationByPlatform(true);
				GUIUtils.centerWindow(frame);
				frame.setVisible(true);
			}
		});
	}

}
