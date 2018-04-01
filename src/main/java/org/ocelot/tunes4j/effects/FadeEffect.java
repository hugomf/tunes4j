package org.ocelot.tunes4j.effects;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;

import javax.swing.Timer;

public class FadeEffect {
	
	public enum FadeScale {
		LINEAR, LOGARITHMIC
	}
	
	private static final FadeScale FADE_SCALE_DEFAULT = FadeScale.LOGARITHMIC;

	public static Timer fadeIn(Window window,  float finalOpacity, CountDownLatch lock) {
		
		 Timer timer = new Timer(10, new ActionListener() {
			 float opacity = 0.0f;
			 int increase = 0;
			 private FadeScale scale = FADE_SCALE_DEFAULT;
   
			 @Override
            public void actionPerformed(ActionEvent e) {
				
           	 	increase++;
           	 	if(scale.equals(FadeScale.LINEAR)) {
           	 		opacity = (float) increase / 100;
           	 	} else {
           	 		float x = (float) increase / 10 + 1;
           	 		opacity =  (float) Math.log10(x);
           	 	}
				 
           	 	if(opacity >= finalOpacity) {
           	 		lock.countDown(); 
           	 		((Timer)e.getSource()).stop();
           	 		 
           	 	}
           	 	setWindowOpacity(window, opacity);
           	 	//System.out.println(String.format("opacity(%s):%s",increase,opacity));
            }
            
		 });
		 timer.setRepeats(true);
		 timer.setCoalesce(true);
		 timer.start();
		 return timer;
	}
	
	public static void fadeOut(Window window, float finalOpacity) {
		 Timer timer = new Timer(14, new ActionListener() {
			 float opacity = finalOpacity;
  
			 @Override
           public void actionPerformed(ActionEvent e) {
          	 	
				 opacity = new BigDecimal(opacity - 0.02f).setScale(2,RoundingMode.HALF_UP).floatValue();
          	 	if(opacity < 0.05f) {
          	 		 ((Timer)e.getSource()).stop();
          	 		 window.dispose();
          	 	}
          	 	setWindowOpacity(window, opacity);
          	 	
           }
           
		 });
		 timer.setRepeats(true);
		 timer.setCoalesce(true);
		 timer.start();
	}
	
	
	public static void setWindowOpacity( Window window, Float opacity){
        try {
               Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
               Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
               if (!mSetWindowOpacity.isAccessible()) {
                   mSetWindowOpacity.setAccessible(true);
               }
               mSetWindowOpacity.invoke(null, window, opacity);
            } catch (NoSuchMethodException ex) {
               ex.printStackTrace();
            } catch (SecurityException ex) {
               ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
               ex.printStackTrace();
            } catch (IllegalAccessException ex) {
               ex.printStackTrace();
            } catch (IllegalArgumentException ex) {
               ex.printStackTrace();
            } catch (InvocationTargetException ex) {
               ex.printStackTrace();
            }
    }

}
