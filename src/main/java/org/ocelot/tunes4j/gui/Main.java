package org.ocelot.tunes4j.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

public class Main {
	  public static void main(String args[]) {
	    JFrame frame = new JFrame("Compound Borders");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    Border lineBorder = LineBorder.createBlackLineBorder();
	    Border bevelBorder = BorderFactory.createRaisedBevelBorder();
	    Border bevelLineBorder = new CompoundBorder(bevelBorder, lineBorder);
	    JButton bevelLineButton = new JButton("Bevel Line");
	    bevelLineButton.setBorder(bevelLineBorder);
	    Border redBorder = BorderFactory.createLineBorder(Color.MAGENTA, 2);
	    Border orangeBorder = BorderFactory.createLineBorder(Color.BLUE, 2);
	    Border yellowBorder = BorderFactory.createLineBorder(Color.YELLOW, 5);
	    Border greenBorder = BorderFactory.createLineBorder(Color.GREEN, 2);
	    Border blueBorder = BorderFactory.createLineBorder(Color.ORANGE, 4);
	    Border magentaBorder = BorderFactory.createLineBorder(Color.RED, 3);
	    Border twoColorBorder = new CompoundBorder(magentaBorder, blueBorder);
	    Border threeColorBorder = new CompoundBorder(twoColorBorder, greenBorder);
	    Border fourColorBorder = new CompoundBorder(threeColorBorder, yellowBorder);
	    Border fiveColorBorder = new CompoundBorder(fourColorBorder, orangeBorder);
	    Border sixColorBorder = new CompoundBorder(fiveColorBorder, redBorder);
	    JButton rainbowButton = new JButton("Rainbow");
	    rainbowButton.setBorder(sixColorBorder);
	    Container contentPane = frame.getContentPane();
	    contentPane.setLayout(new GridLayout(1, 2));
	    contentPane.add(bevelLineButton);
	    contentPane.add(rainbowButton);
	    frame.setSize(300, 100);
	    frame.setVisible(true);
	  }
	}
