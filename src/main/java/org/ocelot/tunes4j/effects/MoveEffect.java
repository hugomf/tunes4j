package org.ocelot.tunes4j.effects;

import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

import javax.swing.Timer;

public class MoveEffect {
	
	
	public static Timer moveIn(Window window, Point initLocation, Point finalLocation, CountDownLatch lock) {

		 Point currentLocation = initLocation;
				 
		 Timer timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

	            	 //System.out.println(currentLocation);
	            	
	            	 if(currentLocation.x <= finalLocation.x && 
	            			 currentLocation.y <= finalLocation.y) {
	            		 lock.countDown();
	            		 ((Timer)e.getSource()).stop();
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
		 return timer;
		
	}

}
