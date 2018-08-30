package org.ocelot.tunes4j.processing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.GUIUtils;

@SuppressWarnings("serial")
public class SineWave extends Processing {

	int xspacing = 2; // How far apart should each horizontal location be spaced
	int w; // Width of entire wave

	float theta = 0.0f; // Start angle at 0
	float amplitude = 25.0f; // Height of wave
	float period = 100.0f; // How many pixels before the wave repeats
	float dx; // Value for incrementing X, a function of period and xspacing
	float[] yvalues; // Using an array to store height values for the wave
	
	
	@Override
	public void settings() {
		size(180, 60);
	}

	@Override
	public void setup() {
		w = width+16;
		dx = (TWO_PI / period) * xspacing;
		yvalues = new float[w/xspacing];
	}

	@Override
	public void draw() {
		//background(0);
		background(new Color(0.95f,0.96f,0.98f));
		//background(Color.DARK_GRAY);
		renderWave();
	}

	void calcWave() {
		// Increment theta (try different values for 'angular velocity' here
		theta += 0.04;

		// For every x value, calculate a y value with sine function
		float x = theta;
		for (int i = 0; i < yvalues.length; i++) {
			yvalues[i] = sin(x) * amplitude;
			x += dx;
		}
	}

	void renderWave() {
		noStroke();
		fill(Color.darkGray);
		//fill(255);
		// A simple way to draw the wave with an ellipse at each location
		dx = (TWO_PI / period) * xspacing;
		calcWave();
		for (int x = 0; x < yvalues.length; x++) {
			ellipse(x * xspacing, height / 2 + yvalues[x], 2, 2);
		}
		
		fill(Color.RED);
		dx = (TWO_PI / 2 / period) * xspacing;
		calcWave();
		for (int x = 0; x < yvalues.length; x++) {
			ellipse(x * xspacing - 10, height / 2 + yvalues[x], 2, 2);
		}
		
	}

	@Override
	public void mouseClicked() {

	}

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				JFrame frame = new JFrame("SineWave");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				SineWave gp = new SineWave();
				frame.add(gp, BorderLayout.NORTH);
				frame.pack();
				frame.setLocationByPlatform(true);
				GUIUtils.centerWindow(frame);
				frame.setVisible(true);
			}
		});
	}

}
