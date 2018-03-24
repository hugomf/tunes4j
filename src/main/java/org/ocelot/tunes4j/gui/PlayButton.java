package org.ocelot.tunes4j.gui;

import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;
import org.ocelot.tunes4j.utils.ResourceLoader;

public class PlayButton extends JLabel  {

	private static final long serialVersionUID = 1L;

	protected ImageIcon playIcon		 	= ResourceLoader.PLAY;
	
	protected ImageIcon pressedPlayIcon 	= ResourceLoader.PLAY_ON;
	
	protected ImageIcon pauseIcon 			= ResourceLoader.PAUSE;
	
	protected ImageIcon pressedPauseIcon	= ResourceLoader.PAUSE_ON;
	
	private Tunes4JAudioPlayer player;
	
	private MediaTable table;
	
	public PlayButton(Tunes4JAudioPlayer player, MediaTable table) {
		this.player = player;
		this.table = table;
		initialize();
	}
	protected void initialize() {

		setOpaque(false);
		setVerticalAlignment(SwingConstants.CENTER);
		setIcon(this.playIcon);

		setBackground(null);
		setIconTextGap(0);
		setBorder(new EmptyBorder(0, 0, 0, 0));

		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if(player.isClosed()) {
					setIcon(pauseIcon);
					player.open(table.getSelectedFile());
					player.play();
				}else if(player.isPlaying()) {
					setIcon(playIcon);
					player.pause();
				} else if(player.isPaused()) {
					setIcon(pauseIcon);
					player.resume();
				}
			}

			public void mousePressed(java.awt.event.MouseEvent e) {
				if (getIcon().toString().equals(playIcon.toString())) {
					setIcon(pressedPlayIcon);
				} 
				else if (getIcon().toString().equals(pauseIcon.toString())) {
					setIcon(pressedPauseIcon);
				}
			}

			public void mouseReleased(MouseEvent arg0) {
				if (getIcon().toString().equals(pressedPlayIcon.toString())) {
					setIcon(playIcon);
				} else {
					setIcon(pauseIcon);
				}
			}
		});
	}
	
	public void reset() {
		setIcon(this.playIcon);
	}

}
