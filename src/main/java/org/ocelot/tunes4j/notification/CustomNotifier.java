package org.ocelot.tunes4j.notification;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.ocelot.tunes4j.utils.ResourceLoader;

import com.birosoft.liquid.LiquidLookAndFeel;

public class CustomNotifier {
	
	
	private static final int CLOSE_DELAY = 3000;
	private static final Color BACKGROUND_COLOR = new Color(0f, 0f, 0f, 1f / 3f);
	private static final int HEIGHT = 80;
	private static final int WIDTH = 260;
	
	private JFrame frame = new JFrame();
	private JPanel mainPanel =  new JPanel();
	private JPanel panel =  new JPanel();
	
	private JLabel lblTitle = new JLabel();
	private JLabel lblSubtitle = new JLabel();
	private JLabel lblMessage = new JLabel();
	
    public static void main(String[] args) throws IllegalAccessException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException {

    		LiquidLookAndFeel.setLiquidDecorations(true, "mac");
    		UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
    		
    		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    		
    		//MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
    		SwingUtilities.invokeLater(new Runnable() {
    			public void run() {
    				try {
    					CustomNotifier notifier = new CustomNotifier();
    			    	notifier.display(ResourceLoader.ICON_APPICON.getImage(), "Album", "Song Author", "Song Name");
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    		});
    	
    	
    	
    }

	
	public void display(Image image, String title, String subtitle, String message) {
		mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTranslucency(frame);
        frame.setBackground(BACKGROUND_COLOR);
        frame.setShape(new RoundRectangle2D.Double(0, 0, WIDTH, HEIGHT, 25, 25));
        
        JLabel imagelbl = new JLabel();
        ImageIcon icon = new ImageIcon(getScaledImage(image,40,40));
		imagelbl.setIcon(icon);
        mainPanel.add(imagelbl);
        
        lblTitle.setFont(new Font(lblTitle.getFont().getName(), Font.PLAIN, 20));
        lblTitle.setText(title);
        lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(lblTitle);
        
        lblSubtitle.setFont(new Font(lblSubtitle.getFont().getName(), Font.PLAIN, 18));
        lblSubtitle.setText(subtitle);
        lblSubtitle.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(lblSubtitle);
        
        lblMessage.setFont(new Font(lblMessage.getFont().getName(), Font.PLAIN, 14));
        lblMessage.setText(message);
        panel.add(lblMessage);
        
        mainPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        mainPanel.add(panel);
        
        switchToDefaultLookAndFeel(frame);
        
        
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        setLocation(frame);
        
        
        sleep(CLOSE_DELAY);
        
        frame.dispose();
	}
	
	
	public void switchToDefaultLookAndFeel(JFrame frame) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(frame);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
        
	}
	
	private void setLocation(Window window) {
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) rect.getMaxX() - window.getWidth() - 20;
        int y = (int) rect.getMaxY() - window.getHeight() - 40;
		window.setLocation(x, y);
	}

	private void sleep(int milis) {
		try {
            Thread.sleep(milis);
        }
        catch(InterruptedException e) {
        }
	}
	
	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
    
    private  void setTranslucency( Window window){
        try {
               Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
               Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
               if (!mSetWindowOpacity.isAccessible()) {
                   mSetWindowOpacity.setAccessible(true);
               }
               mSetWindowOpacity.invoke(null, window, Float.valueOf(0.75f));
            } catch (NoSuchMethodException ex) {
               ex.printStackTrace();
            } catch (SecurityException ex) {
               ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
               ex.printStackTrace();
            } catch (IllegalAccessException ex) {
               ex.printStackTrace();
            } catch (IllegalArgumentException ex) {
               ex.printStackTrace();
            } catch (InvocationTargetException ex) {
               ex.printStackTrace();
            }
    }
}
