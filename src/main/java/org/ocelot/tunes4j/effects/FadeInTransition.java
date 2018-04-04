package org.ocelot.tunes4j.effects;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

import javax.swing.Timer;

public class FadeInTransition {
	
//	public enum FadeScale {
//		LINEAR, LOGARITHMIC
//	}
//	
//	private static final FadeScale FADE_SCALE_DEFAULT = FadeScale.LOGARITHMIC;
	
	private float finalOpacity;
	


	public void apply(Window window,  CountDownLatch lock) {
		
		 Timer timer = new Timer(10, new ActionListener() {
			 float currentOpacity = 0.0f;
			 int increase = 0;
			 //private FadeScale scale = FADE_SCALE_DEFAULT;
   
			 @Override
            public void actionPerformed(ActionEvent e) {
				
           	 	increase++;
//           	 	if(scale.equals(FadeScale.LINEAR)) {
           	 		currentOpacity = (float) increase / 100;
//           	 	} else {
//           	 		float x = (float) increase / 10 + 1;
//           	 		currentOpacity =  (float) Math.log10(x);
//           	 	}
				 
           	 	if(currentOpacity >= finalOpacity) {
           	 		((Timer)e.getSource()).stop();
           	 		lock.countDown();
           	 	}
           	 	window.setOpacity(currentOpacity);
            }
            
		 });
		 timer.setRepeats(true);
		 timer.setCoalesce(true);
		 timer.start();
	}
	
	

	
	

}
