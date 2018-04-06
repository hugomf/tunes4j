package org.ocelot.tunes4j;

import java.awt.Image;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.ocelot.tunes4j.gui.ApplicationWindow;
import org.ocelot.tunes4j.utils.GUIUtils;
import org.ocelot.tunes4j.utils.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.medsea.mimeutil.MimeUtil;

@Component
public class Tunes4JLauncher {

	@Autowired
	private ApplicationWindow window;

	public void launch() throws ClassNotFoundException, InstantiationException, 
		IllegalAccessException, UnsupportedLookAndFeelException {

		//LiquidLookAndFeel.setLiquidDecorations(true, "mac");
		//UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					setApplicationIcons(window, ResourceLoader.ICON_APPICON.getImage());
					window.setTitle("tunes4J");
					window.renderUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void setApplicationIcons(ApplicationWindow window, Image image) {
		window.setIconImage(image);
		GUIUtils.setDockImage(image);
	}

}
