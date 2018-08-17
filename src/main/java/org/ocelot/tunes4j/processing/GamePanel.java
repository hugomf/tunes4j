package org.ocelot.tunes4j.processing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

import org.ocelot.tunes4j.utils.GUIUtils;

import lombok.AllArgsConstructor;
import lombok.Data;

@SuppressWarnings("serial")
public class GamePanel extends JComponent implements KeyListener {

	private static final int WIDE = 640;
	private static final int HIGH = 480;
	private final static int DELAY = (int) (1000 / 30);

	private GoalKeeper goalKeeper = new GoalKeeper(100, 25, 28.8f, 20, 100, Color.RED);
	private Ball ball = new Ball(0, 0, 8.8f, 8.2f, 10, Color.BLACK, 1, 1);

	@Data
	@AllArgsConstructor
	class GoalKeeper {
		private float x;
		private float y;
		private float speed;
		private int width;
		private int height;
		private Color color;
		
		public float getX2() {
			return this.x + this.width; 
		}
		
		public float getY2() {
			return  this.y + this.height;
		}
		
		public void moveDown() {
			float speed = this.speed;
			int height = HIGH - this.height;
			if(this.y >= height) {
				this.y = height;
				speed = 0;
			}
			this.y =  this.y + speed;
		}

		public void moveUp() {
			float speed = this.speed;
			if (this.y <= 0) {
				this.y = 0;
				speed = 0;
			}
			this.y =  this.y - speed;
		}
		
		public void draw(Graphics2D g2) {
			g2.setColor(this.getColor());
			g2.fill(new Rectangle2D.Float(this.x, this.y, this.getWidth(), this.getHeight()));
		}

	}

	@Data
	@AllArgsConstructor
	class Ball {
		
		private float x;
		private float y;
		private float velx;
		private float vely;
		private int diameter;
		private Color color;
		
		private float directionx;
		private float directiony;
		
		public float getY2() {
			return this.y + this.diameter;
		}
		
		private void move(GoalKeeper keeper) {
			// update the ball position
			
			this.x = this.x + (this.velx * this.directionx);
			this.y = this.y + (this.vely * this.directiony);
			
			if (this.x > WIDE-this.diameter || this.x < this.diameter) { 
				this.directionx *= -1;
			}
			
			if (this.y > HIGH-this.diameter || this.y < this.diameter) {
				this.directiony *= -1;
			}
			
			if (this.x - this.diameter < keeper.getX2() && (this.y <= keeper.getY2()
					&& this.getY2() >= keeper.getY())) {
				this.directionx *= -1;
			}
			
		}
		
		
		private void draw(Graphics2D g2) {
			g2.setColor(this.getColor());
			g2.fill(new Ellipse2D.Double(this.x - this.diameter, this.y-this.diameter, this.diameter * 2, this.diameter * 2));
		}
		
	}

	private Timer timer = new Timer(DELAY, event -> {
		ball.move(this.goalKeeper);
		repaint();
	});

	public GamePanel() {

		this.ball.setX(random(goalKeeper.x + 10, this.getPreferredSize().width - ball.diameter));
		this.ball.setY(random(goalKeeper.y + 10, this.getPreferredSize().height - ball.diameter));

		timer.start();
		this.addKeyListener(this);
		this.setFocusable(true);

	}

	private float random(float min, float max) {
		return (float) ThreadLocalRandom.current().nextDouble(min, max + 1);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.goalKeeper.draw(g2);
		this.ball.draw(g2);
	}


	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDE, HIGH);
	}

	@Override
	public void keyPressed(KeyEvent e) {

		int keyCode = e.getKeyCode();
		if( keyCode == KeyEvent.VK_UP) {
			this.goalKeeper.moveUp();
		}
		if( keyCode == KeyEvent.VK_DOWN) {
			this.goalKeeper.moveDown();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void keyReleased(KeyEvent e) { }

	
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				JFrame frame = new JFrame("GamePanel");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				GamePanel gp = new GamePanel();
				frame.add(gp, BorderLayout.NORTH);
				frame.pack();
				frame.setLocationByPlatform(true);
				GUIUtils.centerWindow(frame);
				frame.setVisible(true);
			}
		});
	}
	
}
