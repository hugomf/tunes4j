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
package org.ocelot.tunes4j.components.volumeslider;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

import org.ocelot.tunes4j.utils.ResourceLoader;

public class VolumeSliderUI extends BasicSliderUI {

	protected static Color borderColor;
	protected static Color trackColor;
	protected static Color thumbBackColor;

	protected static ImageIcon thumbIcon;

	public static ComponentUI createUI(JComponent c) {
		return new VolumeSliderUI();
	}

	public VolumeSliderUI() {
		super(null);
	}

	public void installUI(JComponent c) {
		thumbIcon = ResourceLoader.ICON_VOLUME;
		borderColor = new Color(132, 130, 132);
		trackColor = new Color(247, 247, 247);
		thumbBackColor = Color.DARK_GRAY;
		super.installUI(c);
		scrollListener.setScrollByBlock(false);
	}

	public void paintThumb(Graphics g) {
		
		int trackHeight = thumbRect.height / 2;
		int trackTop = (thumbRect.height - trackHeight) / 2 + 2;
		int trackLeft = 0;
		
		Rectangle knobBounds = thumbRect;
		g.translate(knobBounds.x, knobBounds.y);
		thumbIcon.paintIcon(slider, g, 0, 1);
		g.translate(-knobBounds.x, -knobBounds.y);

		Graphics2D g2D = (Graphics2D) g;
		GradientPaint gradient = new GradientPaint(trackLeft, trackTop + trackHeight, borderColor, trackLeft, trackTop
				+ (trackHeight / 2), thumbBackColor, true);
		g2D.setPaint(gradient);
		g2D.fillRect(trackLeft, trackTop, thumbRect.x , trackHeight);
	}

	public void paintTrack(Graphics g) {

		Graphics2D g2D = (Graphics2D) g;

		g2D.translate(trackRect.x/2, trackRect.y);

		int trackWidth = contentRect.width - 6;
		int trackHeight = thumbRect.height / 2;
		int trackTop = (thumbRect.height - trackHeight) / 2 + 1;
		int trackLeft = 0;

		GradientPaint gradient = new GradientPaint(trackLeft, trackTop + trackHeight, borderColor, trackLeft, trackTop
				+ (trackHeight / 2), trackColor, true);
		g2D.setPaint(gradient);
		g2D.fillRect(trackLeft, trackTop, trackWidth, trackHeight);

		g2D.setColor(borderColor);
		g2D.drawLine(trackLeft, trackTop, trackLeft, trackTop + trackHeight - 1);
		g2D.drawLine(trackLeft + trackWidth, trackTop, trackLeft + trackWidth, trackTop + trackHeight - 1);
		
		g2D.translate(-trackRect.x/2, -trackRect.y);
	}

	public void paintFocus(Graphics g) {
	}

	protected Dimension getThumbSize() {
		Dimension size = new Dimension();
		size.width = thumbIcon.getIconWidth();
		size.height = thumbIcon.getIconHeight();
		return size;
	}

	protected int getTrackWidth() {
		final double kIdealTrackWidth = 7.0;
		final double kIdealThumbHeight = 16.0;
		final double kWidthScalar = kIdealTrackWidth / kIdealThumbHeight;

		return (int) (kWidthScalar * thumbRect.height);

	}

	protected int getTrackLength() {
		return trackRect.width;
	}

	protected int getThumbOverhang() {
		return (int) (getThumbSize().getHeight() - getTrackWidth()) / 2;
	}

}
