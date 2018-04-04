package org.ocelot.tunes4j.effects;

import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

import javax.swing.Timer;

public class MoveEffect  {
	
	private Point initLocation;
	
	private Point finalLocation;
	
	public MoveEffect(Point initLocation, Point finalLocation) {
		this.initLocation = initLocation;
		this.finalLocation = finalLocation;
	}
	
	public void apply(Window window, CountDownLatch lock) {

		 Point currentLocation = initLocation;
				 
		 Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

	            	 if(currentLocation.x <= finalLocation.x &&
	            			 currentLocation.y <= finalLocation.y) {
	            		 ((Timer)e.getSource()).stop();
	            		 lock.countDown();
	            	 }

	         	if(currentLocation.x > finalLocation.x ) 
           			currentLocation.x = currentLocation.x - 10;
           	 	if(currentLocation.y > finalLocation.y ) 
           	 			currentLocation.y = currentLocation.y - 10;
           	 	
           	 	window.setLocation(currentLocation);
           	 	 
           	 	
            }
        });
		 timer.setRepeats(true);
		 timer.setCoalesce(true);
		 timer.start();
	}

}
