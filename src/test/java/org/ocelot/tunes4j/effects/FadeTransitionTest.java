package org.ocelot.tunes4j.effects;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.ocelot.tunes4j.utils.GUIUtils;


public class FadeTransitionTest {

	public static void main(String[] args) {

		
		JFrame frame = new JFrame();
		frame.setUndecorated(true);
		frame.setOpacity(0.01f);
		JButton button = new JButton("Close");
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		
		frame.setPreferredSize(new Dimension(200, 120));
		frame.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		frame.add(button);
		frame.pack();
		GUIUtils.centerWindow(frame);
		frame.setVisible(true);
		FadeTransition.fadeIn(frame, 14);
		
	}
	
}
