package org.ocelot.tunes4j.notification;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.concurrent.CountDownLatch;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.ocelot.tunes4j.effects.FadeTransition;
import org.ocelot.tunes4j.effects.MoveEffect;
import org.ocelot.tunes4j.utils.GUIUtils;
import org.ocelot.tunes4j.utils.ImageUtils;
import org.ocelot.tunes4j.utils.ResourceLoader;

public class CustomNotifier {

	private static final Color BACKGROUND_COLOR = new Color(0f, 0f, 0f, 1f / 3f);
	private static final int HEIGHT = 65;
	private static final int WIDTH = 320;

	private JFrame frame = new JFrame();
	private JPanel mainPanel = new JPanel();
	private JPanel panel = new JPanel();
	private JLabel lblSubtitle = new JLabel();
	private JLabel lblMessage = new JLabel();
	//private LookAndFeel previousLF;

	public static void main(String[] args) throws Exception {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					CustomNotifier notifier = new CustomNotifier();
					notifier.display(ResourceLoader.ICON_APPICON.getImage(),
							"Arthur's Theme (Best That You Can Do) - Remastered", "Song Author", "Album");

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void display(Image image, String title, String subtitle, String message) {
		//this.previousLF = UIManager.getLookAndFeel();
		//setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		//SwingUtilities.updateComponentTreeUI(this.frame);
		//SwingUtilities.updateComponentTreeUI(this.mainPanel);
		renderUI(image, title, subtitle, message);
		
	}

	private void renderUI(Image image, String title, String subtitle, String message) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		//panel.setBorder(new LineBorder(Color.RED));

		JLabel imagelbl = new JLabel();
		ImageIcon icon = new ImageIcon(ImageUtils.resize(image, 40, 40));
		imagelbl.setIcon(icon);
		mainPanel.add(imagelbl, SwingConstants.CENTER);

		//JLabel lblTitle = new JLabel(title, 40);
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(new Font(lblTitle.getFont().getName(), Font.PLAIN, 12));
		lblTitle.setText(title);
		lblTitle.setPreferredSize(new Dimension(250, 20));
		lblTitle.setOpaque(false);
		panel.add(lblTitle);

		lblSubtitle.setFont(new Font(lblSubtitle.getFont().getName(), Font.PLAIN, 11));
		lblSubtitle.setText(subtitle);
		lblSubtitle.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblSubtitle);

		lblMessage.setFont(new Font(lblMessage.getFont().getName(), Font.PLAIN, 10));
		lblMessage.setText(message);
		lblMessage.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblMessage);

		mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		mainPanel.add(panel);

		frame.setLocationRelativeTo(null);
		frame.setUndecorated(true);
		frame.setFocusableWindowState(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBackground(BACKGROUND_COLOR);
		
		Point moveTo = getSystemTrayLocation(WIDTH, HEIGHT);
		Point moveFrom = getInitialLocation(moveTo);
		
		frame.setLocation(moveFrom);
		frame.add(mainPanel);
		frame.pack();
		
		GUIUtils.setRoundedWindow(frame, 25, 25);
		//frame.setOpacity(0.75f);
		//frame.getRootPane().setBorder(new DropShadowBorder());
		frame.setVisible(true);

		SplashNotificationWorker worker = new SplashNotificationWorker(frame, moveFrom, moveTo);
		worker.execute();
	}

	

	class SplashNotificationWorker extends SwingWorker<Boolean, Boolean> {

		private Window frame;
		private Point moveFrom;
		private Point moveTo;

		public SplashNotificationWorker(Window frame, Point moveFrom, Point moveTo) {
			this.frame = frame;
			this.moveFrom = moveFrom;
			this.moveTo = moveTo;
		}

		@Override
		protected Boolean doInBackground() throws Exception {
			CountDownLatch lock = new CountDownLatch(1);
			new MoveEffect(moveFrom, moveTo).apply(frame, lock);
			FadeTransition.fadeIn(frame, 14);
			return true;
		}
		
		@Override
		protected void done() {
			super.done();
			new Thread(new Runnable() {
				@Override
				public void run() {
					sleep(1500);
					FadeTransition.fadeOut(frame, 8);
					frame.setVisible(false);
					frame.dispose();
					//setLookAndFeel(previousLF.getClass().getName());
				}
			}).start();;
		}

	}
	
	private void setLookAndFeel(String lookAndFeel)  {
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Point getMaxScreenPoint() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
		int x = (int) rect.getMaxX();
		int y = (int) rect.getMaxY();
		return new Point(x, y);
	}

	private Point getInitialLocation(Point finalLocation) {
		Point maxPoint = getMaxScreenPoint();
		return new Point(maxPoint.x, finalLocation.y);
	}

	private Point getSystemTrayLocation(int width, int height) {
		Point maxPoint = getMaxScreenPoint();
		int x = (int) maxPoint.getX() - width - 30;
		int y = (int) maxPoint.getY() - height - 40;
		return new Point(x, y);
	}

	private void sleep(int milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
		}
	}

}
