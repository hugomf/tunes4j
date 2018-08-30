package org.ocelot.tunes4j.processing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JComponent;
import javax.swing.Timer;

@SuppressWarnings("serial")
public abstract class Processing extends JComponent implements MouseListener, MouseMotionListener  {
	
	static final float TWO_PI = (float) (2.0 * Math.PI);

	
	public static final String RADIUS = "RADIUS";
	public static final String CENTER = "CENTER";
	public static final String CORNER = "CORNER";
	public static final String CORNERS = "CORNERS";
	
	protected String ellipseMode = CENTER;
	
	protected int width;
	protected int height;
	protected int mouseX;
	protected int mouseY;
	protected boolean mousePressed;
	
	private Graphics2D g2d;
	private Color shapeFillColor = Color.WHITE;
	private Color lineStrokeColor = Color.BLACK;
	private int lineStrokeWidth = 1;
	private boolean showStroke = true;
	
	private int frameRate = 60;
	private Timer timer = new Timer(1000/frameRate, event-> repaint());
	private boolean setupIsOff = true;
	
	public Processing() {
		addMouseListener(this);
		addMouseMotionListener(this);
		setOpaque(true);
		settings();
		this.timer.start();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(setupIsOff) {
			setup();
			setupIsOff = false;
		}
		g2d.setBackground(getBackground());
		g2d.clearRect(0, 0, width, height);
		draw();
	}
	
	public abstract void settings();
	
	public abstract void setup();

	public abstract void draw(); 
	
	public abstract void mouseClicked();
	
	protected void println(String string) {
		System.out.println(string);
	}
	
	
	protected void rect(float xpos, float ypos, float width, float height) {
		g2d.setColor(shapeFillColor);
		Rectangle2D.Double rectangle = new Rectangle2D.Double(xpos,ypos, width, height);
		g2d.fill(rectangle);
		drawShapeStrokeIfNeeded(rectangle);
	}

	protected void line(float xpos, float ypos, float width, float height) {
		g2d.setColor(lineStrokeColor);
		g2d.draw(new Line2D.Float(xpos,ypos, width, height));
	}

	protected void ellipse(float xpos, float ypos, float rad1, float rad2) {
		g2d.setColor(shapeFillColor);
		Ellipse2D.Double ellipse = createEllipse2D(xpos, ypos, rad1, rad2);
		g2d.fill(ellipse);		
		drawShapeStrokeIfNeeded(ellipse);
	}

	private void drawShapeStrokeIfNeeded(Shape ellipse) {
		if (showStroke) {
			g2d.setColor(lineStrokeColor);
			g2d.setStroke(new BasicStroke(lineStrokeWidth));
			g2d.draw(ellipse);			
		}
	}

	private Double createEllipse2D(float xpos, float ypos, float rad1, float rad2) {
		
		switch(ellipseMode) {
			case RADIUS:
				return new Ellipse2D.Double(xpos-rad1, ypos-rad2, rad1 * 2, rad2 * 2);
			case CORNER:
				return new Ellipse2D.Double(xpos, ypos, rad1, rad2);
			case CORNERS:
				return new Ellipse2D.Double(xpos, ypos, rad1/2, rad2/2);
			 default:
				return new Ellipse2D.Double((xpos-rad1/2), (ypos-rad2/2) , rad1, rad2);
		}
	}
	
	protected void ellipse(float xpos, float ypos, int rad1, int rad2) {
		ellipse(xpos,ypos, (float) rad1, (float) rad2);
	}
	
	protected int random(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
	
	protected Color color(float r, float g, float b) {
		return new Color(r,g,b);
	}
	
	protected Color color(int r, int g, int b) {
		return new Color(r,g,b);
	}
	
	protected void background(int grayscale) {
		background(grayscale,grayscale,grayscale);
	}
	
	protected void background(Color color) {
		setBackground(color);
	}

	protected void background(int r, int g, int b) {
		setBackground(new Color(r,g,b));
	}
	
	protected void fill(int greyscale) {
		this.shapeFillColor =  new Color(greyscale, greyscale, greyscale);
	}
	
	protected void fill(Color color) {
		this.shapeFillColor = color;
	}

	protected void fill(int r, int g, int b) {
		this.shapeFillColor = new Color(r,g,b);
	}

	protected void ellipseMode(String mode) {
		this.ellipseMode = mode;
	}
	
	protected float lerp(float a, float b, float f) {
	    return a + f * (b - a);
	}
	
	protected void translate(double tx, double ty) {
		g2d.translate(tx, ty);
	}


	protected float cos(float theta) {
		return (float) Math.cos(theta);
	}
	
	protected float sin(float theta) {
		return (float) Math.sin(theta);
	}
	
	protected double cos(double theta) {
		return Math.cos(theta);
	}
	
	protected double sin(double theta) {
		return Math.cos(theta);
	}
	
	
	protected void noStroke() {
		this.showStroke = false;
	}
	
	protected void frameRate(int i) {
		this.frameRate = i;
	}

	protected void size(int i, int j) {
		this.width  = i;
		this.height = j;
		this.setPreferredSize(new Dimension(i,j));
		this.setSize(new Dimension(i,j));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		mouseClicked();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
		
	}

	@Override
	public void mouseExited(MouseEvent e) {

		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.mousePressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.mousePressed = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mouseX = e.getX();
		this.mouseY = e.getY();
	}
	
}
