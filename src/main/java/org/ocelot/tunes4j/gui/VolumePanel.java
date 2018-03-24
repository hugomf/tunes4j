/*
 * This file is part of FireflyClient.
 *
 * FireflyClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * FireflyClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FireflyClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2007 Vincent Cariven
 */
package org.ocelot.tunes4j.gui;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;
import org.ocelot.tunes4j.utils.ResourceLoader;


public class VolumePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Tunes4JAudioPlayer player;
	
	public VolumePanel(Tunes4JAudioPlayer player) {
		this.player=player;
		initialize();
	}

	protected void initialize() {
		ImageIcon volumeLowIcon = ResourceLoader.ICON_VOLUME_LOW;
		ImageIcon volumeHighIcon = ResourceLoader.ICON_VOLUME_LOW;
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JLabel volumeLowLabel = new JLabel(volumeLowIcon);
		JLabel volumeHighLabel = new JLabel(volumeHighIcon);
		VolumeSlider volumeSlider = new VolumeSlider(player);

		volumeLowLabel.setOpaque(false);
		volumeLowLabel.setVerticalAlignment(SwingConstants.CENTER);
		volumeLowLabel.setBackground(null);
		volumeLowLabel.setIconTextGap(0);
		volumeLowLabel.setBorder(new EmptyBorder(new Insets(2, 0, 0, 0)));

		volumeHighLabel.setOpaque(false);
		volumeHighLabel.setVerticalAlignment(SwingConstants.CENTER);
		volumeHighLabel.setBackground(null);
		volumeHighLabel.setIconTextGap(0);
		volumeHighLabel.setBorder(new EmptyBorder(new Insets(2, 4, 0, 0)));
		
		setPreferredSize(new Dimension(140,80));

		add(volumeLowLabel);
		add(volumeSlider);
		add(volumeHighLabel);
	}
}
