package org.ocelot.tunes4j.effects;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ocelot.tunes4j.utils.GUIUtils;

public class FadeTransition2 {

	private static final float START_VALUE = 0.01f;
	private static final float END_VAUE = 1f;
	private static final int STEPS = 100;

	private int delay;
	
	private Consumer<Integer> triggerValue;

	
	public FadeTransition2(int delay) {
		this(null, delay);
	}
	
	public FadeTransition2(Consumer<Integer> triggerValue, int delay) {
		super();
		this.delay = delay;
		this.triggerValue = triggerValue;
	}
	
	public void setConsumer(Consumer<Integer> triggerValue){
		this.triggerValue = triggerValue;
	}
	
	
	public void start() {
		start((int) START_VALUE, (int) END_VAUE);
	}
	
	public void start(int start, int end) {
		if (this.triggerValue!=null) {
			interpolate(this.triggerValue, start, end, STEPS);
		}
	}
	
	
	public void interpolate(Consumer<Integer> triggerValue, int start, int end, int tmp) {
		int count =  end - start;
		
	    for (int i = 0; i <= count; ++ i) {
	    		
	   // 	int value = (int) (Math.abs(Math.sin(.032 * i))+45*Math.abs(Math.sin(.016 * i))+5);
	   	int value = linearInterpolation(start, end, count, i);
	    	
	        triggerValue.accept(value);
	        GUIUtils.sleep(this.delay);
	    }
	}
	


	private int logaritmic(int start, int end, int count, int i) {
        float a = (float) ((end - start)/(Math.log(2 * count + 1)));
        float b = start;
        return (int) (a * Math.log(2 * i + 1) + b);
	}
	
	
	public void interpolate(Consumer<Float> triggerValue, float start, float end, int count) {
	    for (int i = 0; i <= count; ++ i) {
	        float value = cosineInterpolation(start, end, count, i);
	        triggerValue.accept(value);
	        GUIUtils.sleep(this.delay);
	    }
	}

	private float linearInterpolation(float start, float end, int count, int x) {
		float value = start + x * (end - start) / count;
		value = Math.round(value * (float) count) / (float) count;
		return value;
	}
	
	private int linearInterpolation(int start, int end, int count, int x) {
		int value = start + x * (end - start) / count;
		value = Math.round(value * count) / count;
		return value;
	}
	
	
	private float cosineInterpolation(float start, float end, int count, int x) {
		float value = (float) ((1+Math.cos(Math.PI*x / (count-1)))/2 * (start-end)+end);
		value = (float) Math.round(value * (float) count) / (float) count;
		return value;
	}
	
	
	private int cosineInterpolation(int start, int end, int count, int x) {
		int value = (int) ((1+Math.cos(Math.PI*x / (count-1)))/2 * (start-end)+end);
		value = (int) Math.round(value * (int) count) / (int) count;
		return value;
	}
	

	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		List<Point> points = new ArrayList<Point>();
		
		FadeTransition2 transition = new FadeTransition2(14);
		
		JPanel panel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				Shape line = new Line2D.Double(3, 3, 303, 303);
				Shape rect = new Rectangle(3, 3, 303, 303);
				g2.drawOval(1, 303, 5, 5);
				g2.draw(line);
				g2.draw(rect);

				
				
				if(transition != null ) {
					
					Polygon p = new Polygon();
					
					transition.setConsumer(new Consumer<Integer>() {
						int x = 1;
						int y = 10;
						int delta = 5;
						
						@Override
						public void accept(Integer t) {
							 p.addPoint(x, t);
							 x++;
							
						}
					});
					transition.start(0, 7);
					g2.drawPolyline(p.xpoints, p.ypoints, p.npoints);
				}
				
			}
		};
		

		frame.setPreferredSize(new Dimension(340, 340));
		frame.add(panel);
		frame.pack();
		GUIUtils.centerWindow(frame);
		frame.setVisible(true);
	}
	
	
	
//	public static void main(String[] args) {
//
//		
//		JFrame frame = new JFrame();
//		frame.setUndecorated(true);
//		frame.setOpacity(0.01f);
//		FadeTransition transition = new FadeTransition(new Consumer<Float>() {
//			@Override
//			public void accept(Float value) {
//				//System.out.println("opacity=" + value);
//				frame.setOpacity(value);
//			}
//		} ,20);
//		JButton button = new JButton("Close");
//		
//		button.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				frame.dispose();
//			}
//		});
//		
//		frame.setPreferredSize(new Dimension(200, 120));
//		frame.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
//		frame.add(button);
//		frame.pack();
//		GUIUtils.centerWindow(frame);
//		frame.setVisible(true);
//		transition.start();
//		
//	}
	

}
