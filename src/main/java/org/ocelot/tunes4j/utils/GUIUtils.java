package org.ocelot.tunes4j.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GUIUtils {

//	public static void centerWindow(Window window) {
//		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//		int  x = dim.width /2 - window.getWidth();
//		int y = dim.height/2 - window.getHeight();
//		window.setLocation(x, y);
//	}

	public static void centerWindow(Window window) {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation(dim.width/2-window.getSize().width/2, dim.height/2-window.getSize().height/2);
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
	
	
}
