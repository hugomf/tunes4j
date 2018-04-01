package org.ocelot.tunes4j;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.ocelot.tunes4j.gui.ApplicationWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Tunes4JLauncher {

	@Autowired
	private ApplicationWindow window;

	public void launch() throws ClassNotFoundException, InstantiationException, 
		IllegalAccessException, UnsupportedLookAndFeelException {

		//LiquidLookAndFeel.setLiquidDecorations(true, "mac");
		//UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		//MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					window.setTitle("tunes4J");
					window.renderUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
