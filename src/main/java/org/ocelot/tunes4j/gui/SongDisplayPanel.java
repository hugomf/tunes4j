package org.ocelot.tunes4j.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.ocelot.tunes4j.components.JSLidingLabel;
import org.ocelot.tunes4j.components.RoundedJPanel;
import org.ocelot.tunes4j.utils.ImageUtils;
import org.ocelot.tunes4j.utils.ResourceLoader;

@SuppressWarnings("serial")
public class SongDisplayPanel extends RoundedJPanel {
	
	private JLabel artWorkImageLabel = new JLabel();
	
	private JSLidingLabel lblSongTitle = new JSLidingLabel("Song Title");
	
	private JLabel lblArtistAndAlbum = new JLabel("Artist - Album");
	
	private JPanel songDetailPanel = new JPanel();
	
	public SongDisplayPanel() {
		renderUI();
	}

	private void renderUI() {

		Image resized = ImageUtils.resize(ResourceLoader.ICON_APPICON.getImage(), 60, 60);
		this.artWorkImageLabel.setIcon(new ImageIcon(resized));
		this.artWorkImageLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		lblSongTitle.setHorizontalAlignment(SwingConstants.LEFT);
		lblSongTitle.setFont(new Font("Verdana", Font.PLAIN, 16));
		lblSongTitle.setForeground(Color.black);
		lblSongTitle.setPreferredSize(new Dimension(250, 20));
		lblSongTitle.setInfinity(true);
		
		
		lblArtistAndAlbum.setHorizontalAlignment(SwingConstants.LEFT);
		lblArtistAndAlbum.setFont(new Font("Arial", Font.PLAIN, 12));
		lblArtistAndAlbum.setForeground(Color.black);
		lblArtistAndAlbum.setPreferredSize(new Dimension(250, 20));
		
		songDetailPanel.setLayout(new BoxLayout(songDetailPanel, BoxLayout.Y_AXIS));
		songDetailPanel.setOpaque(false);
		songDetailPanel.add(new JLabel("   "));
		songDetailPanel.add(new JLabel("   "));
		songDetailPanel.add(lblSongTitle);
		songDetailPanel.add(lblArtistAndAlbum);
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setForeground(Color.darkGray);
		//this.controlPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		add(artWorkImageLabel);
		add(songDetailPanel);
		setBackground(new Color(0.95f,0.96f,0.98f));
		
	}
	
	public void setSongTitle(String title) {
		this.lblSongTitle.stop();
		this.lblSongTitle.setText(title);
		this.lblSongTitle.play(200);
	}
	
	public void setArtist(String artist) {
		this.lblArtistAndAlbum.setText(artist);
	}
	
	public void setArtwork(Image image) {
		ImageIcon resized = new ImageIcon(ImageUtils.resize(image, 60, 60));
		this.artWorkImageLabel.setIcon(resized);
	}
	
}
