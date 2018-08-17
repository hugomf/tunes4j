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
package org.ocelot.tunes4j.gui.volumeslider;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JSlider;

import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;


public class VolumeSlider extends JSlider implements MouseWheelListener {

	private static final long serialVersionUID = 1L;

	private Tunes4JAudioPlayer player;
	
	protected VolumeSliderUI ui;

	public VolumeSlider(Tunes4JAudioPlayer player) {
		this.player=player;
		initialize();
	}

	protected void initialize() {
		setOpaque(false);
		setMinimum(0);
		setMaximum(100);
		setValue(getMaximum() / 2);
		setOrientation(HORIZONTAL);
		setPaintTicks(false);
		setPaintLabels(false);
		setFocusable(false);
		setPaintTrack(true);
		ui = new VolumeSliderUI();
		setUI(ui);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				setValue(ui.valueForXPosition(e.getX()));
				e.consume();
			}
		});

		addMouseWheelListener(this);

	}

	public void setValue(int n) {
		if (n != getValue()) {
			super.setValue(n);
			float newGain = n * 0.01f;
			player.setGain(newGain);
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		int scrollAmout = 5;
		if (notches < 0) {
			// Increase Volume
			setValue(Math.min(getValue() - (notches * scrollAmout), getMaximum()));
		} else {
			// Decrease Volume
			setValue(Math.max(getValue() - (notches * scrollAmout), getMinimum()));
		}
	}

	public void updateUI() {
		if (getParent() != null) {
			setBackground(getParent().getBackground());
		}
		if (getUI() != null) {
			getUI().installUI(this);
		}
	}
}
