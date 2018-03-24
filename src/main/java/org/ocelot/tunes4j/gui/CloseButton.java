package org.ocelot.tunes4j.gui;

import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;
import org.ocelot.tunes4j.utils.ResourceLoader;

public class CloseButton extends JLabel  {

	private static final long serialVersionUID = 1L;

	protected ImageIcon icon = ResourceLoader.STOP;
	
	protected ImageIcon pressedIcon 	= ResourceLoader.STOP_ON;
	
	private Tunes4JAudioPlayer player;
	
	private JSlider slider;
	
	private PlayButton playButton;
	
	public CloseButton(Tunes4JAudioPlayer player, JSlider slider, PlayButton playButton) {
		this.player = player;
		this.slider = slider;
		this.playButton = playButton;
		initialize();
	}
	protected void initialize() {

		setOpaque(false);
		setVerticalAlignment(SwingConstants.CENTER);
		setIcon(this.icon);

		setBackground(null);
		setIconTextGap(0);
		setBorder(new EmptyBorder(0, 0, 0, 0));

		addMouseListener(new java.awt.event.MouseAdapter() {

			public void mouseClicked(java.awt.event.MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						player.stop();
						System.out.println(slider.getValue());
						slider.setValue(0);
						System.out.println(slider.getValue());
						playButton.reset();					}
				});
			}

			public void mousePressed(java.awt.event.MouseEvent e) {
				if (getIcon().toString().equals(icon.toString())) {
					setIcon(pressedIcon);
				} else {
					setIcon(icon);
				}
			}

			public void mouseReleased(MouseEvent arg0) {
				if (getIcon().toString().equals(pressedIcon.toString())) {
					setIcon(icon);
				} else {
					setIcon(pressedIcon);
				}
			}
		});
	}

}
