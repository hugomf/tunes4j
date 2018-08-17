package org.ocelot.tunes4j.processing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.GUIUtils;

@SuppressWarnings("serial")
public class BouncingBall extends Processing {

	int rad = 60;        // Width of the shape
	float xpos, ypos;    // Starting position of shape    

	float xspeed = 2.8f;  // Speed of the shape
	float yspeed = 2.2f;  // Speed of the shape

	int xdirection = 1;  // Left or Right
	int ydirection = 1;  // Top to Bottom
	

	@Override
	public void settings() {
		size(640, 360);
	}
	
	@Override
	public void setup() {
		println("rad:" + rad);
		frameRate(30);
		ellipseMode(RADIUS);

		xpos = width/2;
		ypos = height/2;
		
	}

	@Override
	public void draw() {

		 background(102);
			
		 // Update the position of the shape
		  xpos = xpos + ( xspeed * xdirection );
		  ypos = ypos + ( yspeed * ydirection );
		  
		  // Test to see if the shape exceeds the boundaries of the screen
		  // If it does, reverse its direction by multiplying by -1
		  if (xpos > width-rad || xpos < rad) {
		    xdirection *= -1;
		  }
		  
		  if (ypos > height-rad || ypos < rad) {
		    ydirection *= -1;
		  }

		  // Draw the shape
		  //println("xpos:" + xpos + " ypos:" + ypos);
		  ellipse(xpos, ypos, rad, rad);
	}
	
	
	@Override
	public void mouseClicked() { }
	
	
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				JFrame frame = new JFrame("BouncingBall");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				BouncingBall gp = new BouncingBall();
				frame.add(gp, BorderLayout.NORTH);
				frame.pack();
				frame.setLocationByPlatform(true);
				GUIUtils.centerWindow(frame);
				frame.setVisible(true);
			}
		});
	}



	
}
