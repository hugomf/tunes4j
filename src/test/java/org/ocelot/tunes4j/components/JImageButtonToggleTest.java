package org.ocelot.tunes4j.components;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JToggleButton;

import org.ocelot.tunes4j.utils.GUIUtils;
import org.ocelot.tunes4j.utils.ResourceLoader;

public class JImageButtonToggleTest {
	
	
	public static void main(String[] args) {
		
		
		JToggleButton button1 = new JToggleButton();
		ItemListener itemListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				 int state = e.getStateChange();
			        if (state == ItemEvent.SELECTED) {
			            System.out.println("Selected"); // show your message here
			        } else {
			            System.out.println("Deselected"); // remove your message
			        }				
			}
		};
		
		button1.setIcon(ResourceLoader.PLAY);
		button1.setRolloverIcon(ResourceLoader.PLAY_ON);
		button1.setSelectedIcon(ResourceLoader.PAUSE);
		button1.setRolloverSelectedIcon(ResourceLoader.PAUSE_ON);
		
		button1.addItemListener(itemListener);
		button1.setBorderPainted(false);
		
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(button1);
		frame.pack();
		GUIUtils.centerWindow(frame);
		frame.setVisible(true);
	}

}
