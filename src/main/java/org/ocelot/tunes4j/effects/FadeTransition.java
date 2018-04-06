package org.ocelot.tunes4j.effects;


import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.GUIUtils;

public class FadeTransition {

	private static final float START_VALUE = 0.01f;
	private static final float END_VAUE = 1f;
	private static final int STEPS = 100;

	public static void fadeIn(Window window,int delay) {
		fadeIn(window, START_VALUE, END_VAUE, STEPS, delay);
	}
	
	public static void fadeOut(Window window,int delay) {
		fadeOut(window, START_VALUE, END_VAUE, STEPS, delay);
	}
	
	public static void fadeIn(Window window, float start, float end, int count, int delay) {
	    for (int i = 0; i <= count; ++ i) {
	        float value = linearInterpolation(start, end, count, i);
	        GUIUtils.setWindowOpacity(window, value);
	        GUIUtils.sleep(delay);
	    }
	}
	
	public static void fadeOut(Window window, float start, float end, int count, int delay) {
	    for (int i = count; i > 0 ; i--) {
	        float value = linearInterpolation(start, end, count, i);
	        GUIUtils.setWindowOpacity(window, value);
	        GUIUtils.sleep(delay);
	    }
	}

	private static float linearInterpolation(float start, float end, int count, int x) {
		float value = start + x * (end - start) / count;
		value = Math.round(value * (float) count) / (float) count;
		return value;
	}
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setPreferredSize(new Dimension(200, 120));
		JButton button = new JButton("Close");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FadeTransition.fadeOut(frame,8);
				frame.dispose();
			}
		});
		frame.add(button);
		frame.pack();
		GUIUtils.centerWindow(frame);
		frame.setOpacity(0.01f);
		frame.setVisible(true);
		FadeTransition.fadeIn(frame,8);
		
	}
	
}	
