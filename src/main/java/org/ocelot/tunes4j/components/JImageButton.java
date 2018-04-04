package org.ocelot.tunes4j.components;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class JImageButton extends JLabel  {

	private static final long serialVersionUID = 1L;

	protected ImageIcon icon;
	
	protected ImageIcon hoverIcon;
	
	protected ImageIcon pressedIcon;
	
	
	public JImageButton(Image icon, Image pressedIcon, Image hoverIcon) {
		this(new ImageIcon(icon),  new ImageIcon(pressedIcon), new ImageIcon(hoverIcon));
	}

	public JImageButton(ImageIcon icon, ImageIcon pressedIcon, ImageIcon hoverIcon) {
		super();
		this.icon = icon;
		this.hoverIcon = hoverIcon;
		this.pressedIcon = pressedIcon;
		display();
	}
	
	public JImageButton() {

	}

	public void display() {

		setOpaque(false);
		setVerticalAlignment(SwingConstants.CENTER);
		setIcon(this.icon);
		setSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));

		setBackground(null);
		setIconTextGap(0);
		setBorder(new EmptyBorder(0, 0, 0, 0));

		addMouseListener(new java.awt.event.MouseAdapter() {

			public void mousePressed(java.awt.event.MouseEvent e) {
				super.mousePressed(e);
				setIcon(pressedIcon);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				setIcon(hoverIcon);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				setIcon(icon);
			}
			

			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				setIcon(icon);
			}
		});
	}
	

}
