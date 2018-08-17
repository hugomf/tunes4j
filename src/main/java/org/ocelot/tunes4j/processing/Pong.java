package org.ocelot.tunes4j.processing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.GUIUtils;

@SuppressWarnings("serial")
public class Pong extends Processing {

	float x, y, speedX, speedY;
	float diam = 10f;
	float rectSize = 200f;

	@Override
	public void settings() {
		size(640, 480);
	}

	@Override
	public void setup() {
		reset();
	}

	protected void reset() {
		x = width / 2;
		y = height / 2;
		speedX = random(3, 5);
		speedY = random(3, 5);
	}

	@Override
	public void draw() {

		background(0);
		fill(0, 255, 0);

		ellipse(x, y, diam, diam);

		rect(0, 0, 20, height);
		rect(width - 30, mouseY - rectSize / 2, 10, rectSize);

		x += speedX;
		y += speedY;

		// if ball hits movable bar, invert X direction
		if (x > width - 30 && x < width - 20 && y > mouseY - rectSize / 2 && y < mouseY + rectSize / 2) {
			speedX = speedX * -1;
		}

		// if ball hits wall, change direction of X
		if (x < 25) {
			speedX *= -1.1;
			speedY *= 1.1;
			x += speedX;
		}

		// if ball hits up or down, change direction of Y
		if (y > height || y < 0) {
			speedY *= -1;
		}

	}

	@Override
	public void mouseClicked() {
		reset();
	}

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				JFrame frame = new JFrame("Pong");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Pong gp = new Pong();
				frame.add(gp, BorderLayout.NORTH);
				frame.pack();
				frame.setLocationByPlatform(true);
				GUIUtils.centerWindow(frame);
				frame.setVisible(true);
			}
		});
	}

}
