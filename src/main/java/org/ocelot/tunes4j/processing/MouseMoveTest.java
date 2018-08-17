package org.ocelot.tunes4j.processing;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.GUIUtils;

@SuppressWarnings("serial")
public class MouseMoveTest extends Processing {

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

		 background(204);
		 line(mouseX, 20, mouseX, 80);
	}

	@Override
	public void mouseClicked() {

	}

	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				JFrame frame = new JFrame("MouseMoveTest");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				MouseMoveTest gp = new MouseMoveTest();
				frame.add(gp, BorderLayout.NORTH);
				frame.pack();
				frame.setLocationByPlatform(true);
				GUIUtils.centerWindow(frame);
				frame.setVisible(true);
			}
		});
	}

}
