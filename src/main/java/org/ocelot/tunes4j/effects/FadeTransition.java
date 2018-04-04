package org.ocelot.tunes4j.effects;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;

import javax.swing.Timer;

public class FadeTransition {

	private static final float STEP = 0.01f;

//	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
//
//	public void addPropertyChangeListener(PropertyChangeListener listener) {
//		this.pcs.addPropertyChangeListener(listener);
//	}
//
//	public void removePropertyChangeListener(PropertyChangeListener listener) {
//		this.pcs.removePropertyChangeListener(listener);
//	}
	
	private float fromValue = 0.0f;
	
	private float toValue = 0.0f;

	private int speed = 14;
	
	public FadeTransition(float fromValue, float toValue, int speed) {
		this.fromValue = fromValue;
		this.toValue = toValue;
		this.speed = speed;
	}
	
	public void apply(Window window, CountDownLatch lock) {

		Timer timer = new Timer(speed, new ActionListener() {
			float opacity = fromValue;

			@Override
			public void actionPerformed(ActionEvent e) {

				if (toValue - fromValue > 0) {
					opacity = new BigDecimal(opacity + STEP).setScale(2, RoundingMode.HALF_UP).floatValue();
					if (opacity >= toValue) {
						((Timer) e.getSource()).stop();
						lock.countDown();
					}
				} else {
					opacity = new BigDecimal(opacity - STEP).setScale(2, RoundingMode.HALF_UP).floatValue();
					if (opacity <= fromValue) {
						((Timer) e.getSource()).stop();
						lock.countDown();
					}
				}
				System.out.println(opacity);
				//pcs.firePropertyChange("value", opacity, opacity);
				window.setOpacity(opacity);
			}

		});
		timer.setRepeats(true);
		timer.start();
	}

}
