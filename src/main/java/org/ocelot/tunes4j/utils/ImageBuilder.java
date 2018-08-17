package org.ocelot.tunes4j.utils;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

public class ImageBuilder {

	
	private static final String BACKGROUND_IMAGE = "/icons/wi0054-32.png";
	private static final int DIAMETER = 26;
	private static final int SHADOW_GAP = 2;
	
	
	public static void main(String args[]) throws IOException {
		
		ImageBuilder b = new ImageBuilder();
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new FlowLayout(FlowLayout.CENTER));
		frame.getContentPane().add(new JButton(b.createLabeledImage("8")));
		frame.getContentPane().add(new JButton(b.createLabeledImage("218")));
		frame.getContentPane().add(new JButton(b.createLabeledImage("3218")));
		frame.pack();
		frame.setVisible(true);
		
		

	}

	public ImageIcon createLabeledImage(String text)  {

		
		BufferedImage sourceImg = getSourceImage();

	    int imgW = sourceImg.getWidth();
		int imgH = sourceImg.getHeight();
		Margin margin = new Margin(0, 0, imgW + DIAMETER, imgW + DIAMETER); // our reference point

		BufferedImage targetImg = new BufferedImage(margin.w, margin.h, sourceImg.getType());
		Graphics2D g2 = targetImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// g2.setColor(Color.blue);
		// g2.drawRect(margin.x,margin.y, margin.w, margin.h);

		int imgPosX = margin.x + margin.w / 2 - imgW / 2;
		int imgPosY = margin.y + margin.h / 2 - imgH / 2;
		g2.drawImage(sourceImg, null, imgPosX, imgPosY);
		// g2.setColor(Color.blue);
		// g2.fillRect(imgPosX, imgPosY, imgW, imgH);

		int ovalPosX = margin.x + margin.w - DIAMETER;
		int ovalPosY = margin.y;
		g2.setColor(new Color(0, 0, 0, 90));
		g2.fillOval(ovalPosX - SHADOW_GAP, ovalPosY + SHADOW_GAP, DIAMETER + SHADOW_GAP / 2, DIAMETER + SHADOW_GAP / 2);
		g2.setColor(Color.RED);
		g2.fillOval(ovalPosX - SHADOW_GAP, ovalPosY + SHADOW_GAP, DIAMETER, DIAMETER);

		int fontSize = 11;
		if (!text.isEmpty() && text.length() > 3) {
			fontSize = 9;
		}

		Font font = new Font("Arial", Font.BOLD, fontSize);
		g2.setFont(font);
		FontMetrics fm = g2.getFontMetrics(font);

		int txtW = fm.stringWidth(text);
		int txtH = fm.getHeight() + fm.getDescent() - 2;
		int txtX = ovalPosX + (DIAMETER / 2) - (txtW / 2) - SHADOW_GAP;
		int txtY = ovalPosY + DIAMETER - (txtH / 2);
		g2.setColor(Color.white);
		g2.drawString(text, txtX, txtY);
		g2.dispose();
		targetImg.coerceData(true);
		
		System.out.println(margin.x);
		return new ImageIcon(targetImg);
	}

	private BufferedImage getSourceImage()  {
		BufferedImage sourceImg;
		try {
			return ImageIO.read(FileUtils.getUrl(BACKGROUND_IMAGE));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	class Margin {

		public int x;
		public int y;
		public int w;
		public int h;

		public Margin(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

	}
	
}