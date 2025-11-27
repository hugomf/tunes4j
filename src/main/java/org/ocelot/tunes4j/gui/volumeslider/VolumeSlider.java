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
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;


public class VolumeSlider extends JSlider implements MouseWheelListener, ChangeListener {

	private static final long serialVersionUID = 1L;

	private Tunes4JAudioPlayer player;

	protected VolumeSliderUI ui;

	// Ultra-low latency debouncing (10ms delay)
	private Timer debounceTimer;
	private static final int DEBOUNCE_DELAY_MS = 10; // 10ms for minimal perceived delay
	private int pendingValue = -1; // Latest value waiting to be applied

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

		// Initialize debounce timer
		debounceTimer = new Timer("VolumeDebounceTimer", true);

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
			// Debounced volume update to eliminate perceived latency
			scheduleGainUpdate(n);
		}
	}

	/**
	 * Ultra-low latency debounced gain update (10ms delay)
	 */
	private void scheduleGainUpdate(int newValue) {
		pendingValue = newValue;

		// Cancel any pending update
		debounceTimer.cancel();
		debounceTimer = new Timer("VolumeDebounceTimer", true);

		// Apply the gain change after a tiny delay
		debounceTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (pendingValue != -1) {
					float newGain = pendingValue * 0.01f;
					player.setGain(newGain);
					pendingValue = -1;
				}
			}
		}, DEBOUNCE_DELAY_MS);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// No additional action needed - ChangeListener just ensures the interface is implemented
		// The debouncing is handled in setValue()
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
