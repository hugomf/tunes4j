package org.ocelot.tunes4j.utils;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang.SystemUtils;

public class GUIUtils {

	public static void centerWindow(Window window) {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation(dim.width / 2 - window.getSize().width / 2, dim.height / 2 - window.getSize().height / 2);
	}
	
	public static void setRoundedWindow(Window window, int cornerWidth, int cornerHeight) {
		window.setShape(new RoundRectangle2D.Double(0, 0, window.getWidth(), window.getHeight(), cornerWidth, cornerHeight));
	}
	
	public void switchToDefaultLookAndFeel(JComponent component) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(component);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
	}
		
	
	public static void setDockImage(Image image) {
		
		try {
			
			if (!SystemUtils.IS_OS_MAC_OSX) return;
			
		    // Use modern JavaFX approach or skip if not available
		    try {
		        Class<?> taskbarClass = Class.forName("java.awt.Taskbar");
		        Method getTaskbar = taskbarClass.getMethod("getTaskbar");
		        Object taskbar = getTaskbar.invoke(null);
		        Method setIconImage = taskbarClass.getMethod("setIconImage", java.awt.Image.class);
		        setIconImage.invoke(taskbar, image);
		    } catch (ClassNotFoundException | NoSuchMethodException |
		             IllegalAccessException | InvocationTargetException e) {
		        // Fallback to old EAWT approach with module access
		        try {
		            Class<?> util = Class.forName("com.apple.eawt.Application");
		            Method getApplication = util.getMethod("getApplication");
		            Object application = getApplication.invoke(util);
		            Method setDockIconImage = util.getMethod("setDockIconImage", java.awt.Image.class);
		            setDockIconImage.invoke(application, image);
		        } catch (Exception ex) {
		            // If both approaches fail, just log and continue
		            System.err.println("Could not set dock icon: " + ex.getMessage());
		        }
		    }
		} catch (Exception e) {
		    System.err.println("Error setting dock image: " + e.getMessage());
		}
		
	}
	
	
	public void switchToDefaultLookAndFeel(JFrame frame) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(frame);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

	}
	

	public static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
