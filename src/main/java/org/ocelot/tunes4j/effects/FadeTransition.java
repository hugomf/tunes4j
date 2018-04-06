package org.ocelot.tunes4j.effects;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.GUIUtils;

public class FadeTransition {

	private static final float START_VALUE = 0.01f;
	private static final float END_VAUE = 1f;
	private static final int STEPS = 100;
	private int delay;
	private Consumer<Float> triggerValue;

	
	public FadeTransition(int delay) {
		this(null, delay);
	}
	
	public FadeTransition(Consumer<Float> triggerValue, int delay) {
		super();
		this.delay = delay;
		this.triggerValue = triggerValue;
	}
	
	public void setConsumer(Consumer<Float> triggerValue){
		this.triggerValue = triggerValue;
	}
	
	public void start() {
		start(START_VALUE, END_VAUE);
	}
	
	public void start(float start, float end) {
		if (this.triggerValue!=null) {
			interpolate(this.triggerValue, start, end, STEPS);
		}
	}
	
	public void interpolate(Consumer<Float> triggerValue, float start, float end, int count) {
	    for (int i = 0; i <= count; ++ i) {
	        float value = linearInterpolation(start, end, count, i);
	        triggerValue.accept(value);
	        GUIUtils.sleep(this.delay);
	    }
	}

	private float linearInterpolation(float start, float end, int count, int x) {
		float value = start + x * (end - start) / count;
		value = Math.round(value * (float) count) / (float) count;
		return value;
	}
	
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setUndecorated(true);
		frame.setOpacity(0.01f);
	
		FadeTransition transition = new FadeTransition(14);
		transition.setConsumer(new Consumer<Float>() {
			@Override
			public void accept(Float value) {
				System.out.println("opacity=" + value);
				frame.setOpacity(value);
			}
		});
		
		JButton button = new JButton("Close");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		frame.setPreferredSize(new Dimension(200, 120));
		frame.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		frame.add(button);
		frame.pack();
		GUIUtils.centerWindow(frame);
		frame.setVisible(true);
		transition.start();
		
	}
	

}
