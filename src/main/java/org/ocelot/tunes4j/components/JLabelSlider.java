package org.ocelot.tunes4j.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.lang.StringUtils;

public class JLabelSlider extends JLabel {
	
	private static final long serialVersionUID = 1L;

	private int maxLength;
	
	private char[] letters;
	
	private String buffer;
;	
	public JLabelSlider(String text, int maxLength ) {
			super();
			this.maxLength = maxLength;
			String padText = String.format("%-5s", text);
			this.letters = padText.toCharArray();
			if(this.letters.length > 0) {
				setText(getBuffer(0));
			}
	}
	
	public String getBuffer(int index) {
		int i=index;
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
	
	public void startSlideText(int ms) {
		new Thread(() -> {
			for (int i = 0; i < this.letters.length; i++) {
				setText(this.getBuffer(i));
				sleep(ms);
			}
		}).start();;
	}
	
	private static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {
		
		String longText = "Lorem ipsum dolor sit amet, oratio integre no quo, in option labores quaestio vim. Sapientem voluptatibus mel te.";
		JLabelSlider label = new JLabelSlider(longText, 50);
		label.setBackground(new Color(248, 213, 131));
		label.setPreferredSize(new Dimension(150, 180));
		
        JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				label.startSlideText(120);
			}
		});
		
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.getContentPane().add(button, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}
	
	
	
	
	
	
}
