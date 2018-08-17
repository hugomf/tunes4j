package org.ocelot.tunes4j.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class JSLidingLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	private int maxLength;

	private String buffer;

	private String originalString;

	private boolean infinite = false;
	
	private boolean cancelled = false;

	private static final Object LOCK = new Object();

	public JSLidingLabel(String text) {
		super(text);
		this.originalString = text;
		
		this.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("preferredSize")) {
					maxLength = getMaxLength();
				}				
			}
		});
		
		
	}
	
	public void stop() {
		this.cancelled = true;
	}

	private int getMaxLength() {
		double width = this.getPreferredSize().getWidth();
		FontMetrics metrics = this.getFontMetrics(this.getFont());
		int totalWidthPerChar = metrics.stringWidth("A");
		return (int) width / totalWidthPerChar + totalWidthPerChar;
	}

	public void play(int ms) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (LOCK) {
					animateSlideText(ms);
				}
			}
		}).start();
		
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		this.originalString = text;
	}
	
	private boolean textFitsInLabel() {
		double preferredSizeWidth = getPreferredSize().getWidth();
		int currentFontMaxSize = getCurrentFontTextWidth();
		return currentFontMaxSize <= preferredSizeWidth;
	}
	
	private int getCurrentFontTextWidth() {
		if (this.getGraphics() == null) return this.getMaximumSize().width;
		FontMetrics metrics = this.getGraphics().getFontMetrics(this.getFont());
		return metrics.stringWidth(this.originalString);
	}

	private void animateSlideText(int ms) {
		this.cancelled = false;
		while(!this.cancelled) {
			if (textFitsInLabel()) {
				return;
			}
			String text = this.originalString + " ";
			String padText = String.format("%-5s", text);
			char[] letters = padText.toCharArray();
			
			for (int i = 0; i < letters.length; i++) {
				if(this.cancelled) {
					return;
				}
				super.setText(getTextChunk(i, letters));
				sleep(ms);
			}
			
		}  
	}

	private String getTextChunk(int index, char[] letters) {
		int i = index;
		String tmp = "";
		while (i < letters.length) {
			tmp = tmp + letters[i];
			if (tmp.length() > maxLength) {
				break;
			}
			buffer = tmp;
			i++;
		}

		return buffer;
	}

	private static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void setInfinity(boolean infinite) {
		this.infinite = infinite;
	}

	public boolean getInfinity() {
		return this.infinite;
	}

	public static void main(String[] args) {

		String longText = "Lorem ipsum dolor sit amet, oratio integre no quo, in option labores quaestio vim. Sapientem voluptatibus mel te.";
		JSLidingLabel label = new JSLidingLabel(longText);
		label.setBackground(new Color(248, 213, 131));
		label.setPreferredSize(new Dimension(150, 180));
		label.setInfinity(true);
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				label.play(120);
			}
		});
		JButton cancel = new JButton("Stop");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				label.stop();
			}
		});

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.getContentPane().add(button, BorderLayout.CENTER);
		frame.getContentPane().add(cancel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}



}
