package org.ocelot.tunes4j.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.ocelot.tunes4j.utils.ResourceLoader;

public class JTextFieldRounded extends JTextField {

	private static final long serialVersionUID = -1890212370885110264L;
	
	public JTextFieldRounded(int cols) {
        super(cols);
        setOpaque(false);
        setForeground(Color.GRAY);
        Font newTextFieldFont=new Font(getFont().getName(),getFont().getStyle(),11);
        setFont(newTextFieldFont);
        setPreferredSize(new Dimension(190,20));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
           
    }
 
	@Override
	public Insets getInsets() {
		return new Insets(1,32,0,20);
	}
    
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                         RenderingHints.VALUE_ANTIALIAS_ON);
        
        RenderingHints renderHints = new RenderingHints(
        		RenderingHints.KEY_ANTIALIASING, 
        			RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING, 
        		RenderingHints.VALUE_RENDER_QUALITY);
        renderHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
        		RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(renderHints);
        g2d.setColor(Color.GRAY);
        g2d.fillRoundRect(0, 0, w, h, h, h);
        g2d.setColor(getBackground());
        g2d.fillRoundRect(2, 2, w-2, h-2, h, h);
        
        renderSearchButton(g);
        renderDeleteButton(g);
   
        super.paintComponent(g);
    }

	private void renderDeleteButton(Graphics g) {
		ImageIcon image = ResourceLoader.txtClosed;
		int imagey = (getHeight() - image.getIconHeight())/2+1;
		int imagex = getBorder().getBorderInsets(this).right;
		imagex = getWidth()-image.getIconWidth()-imagex-6; 
		g.drawImage(image.getImage(),imagex,imagey,image.getIconWidth(),image.getIconHeight(),null);
	}

	private void renderSearchButton(Graphics g) {
		ImageIcon image = ResourceLoader.ICON_TXTFIELD_SEARCH;
		int imagey = (getHeight() - image.getIconHeight())/2+1;
		int imagex = getBorder().getBorderInsets(this).left + 10;
		g.drawImage(image.getImage(),imagex,imagey,image.getIconWidth(),image.getIconHeight(),null);
	}


	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		JTextFieldRounded text = new JTextFieldRounded(24);
		frame.add(text);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	
	
}

