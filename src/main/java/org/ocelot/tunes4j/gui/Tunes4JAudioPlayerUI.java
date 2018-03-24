package org.ocelot.tunes4j.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.ocelot.tunes4j.event.PlayProgressEvent;
import org.ocelot.tunes4j.event.ProgressUpdateListener;
import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;
import org.tritonus.share.sampled.AudioUtils;

public class Tunes4JAudioPlayerUI {
	
	
	private Tunes4JAudioPlayer player;
	
	private boolean sliderValueLocked = false;
	
	public static void main(String[] args) {
		Tunes4JAudioPlayerUI ui = new Tunes4JAudioPlayerUI();
		ui.display();
	}
	
	public void display() {
		
		player = new Tunes4JAudioPlayer();
		
		JFrame frame = new JFrame("Player");
		JSlider slider = new JSlider();
		final JLabel label = new JLabel();
		
		player.open(new File("/Users/hugo/eclipse-workspace2/tunes4j/src/main/resources/Eye of the Tiger.mp3"));
		
		JButton play = new JButton("Play");
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (player.getCurrentStatus() == Tunes4JAudioPlayer.STATE_STOPPED 
						|| player.getCurrentStatus() == Tunes4JAudioPlayer.STATE_UNSTARTED) {
					player.play();
					play.setText("Play");
				} else if (player.getCurrentStatus() == Tunes4JAudioPlayer.STATE_RUNNING ) {
					player.pause();
					play.setText("Paused!");
				}
				else if (player.getCurrentStatus() == Tunes4JAudioPlayer.STATE_SUSPENDED) {
				  player.resume();
				  play.setText("Play");
				} 
			}
			
		});
		
		final JButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.stop();
				slider.setValue(0);
			}
		});
		
		slider.setValue(0);
		slider.setMaximum(1000);	
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (slider.getValueIsAdjusting()) {
					sliderValueLocked = true;
					int value = slider.getValue();
					int totalBytes = (int) player.getProperties().get("mp3.length.bytes");
					long bytesread = (long) value * totalBytes /1000l;
					label.setText(getTimeProgress(bytesread));
				}
				
			}
		});
		slider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				JSlider source  = (JSlider) e.getSource();
				player.skip(source.getValue());
				sliderValueLocked = false;
			}

		});
		
	
		label.setText("00:00:00.00");
		label.setFont(new Font("Arial", Font.PLAIN, 15));
		player.addProgressUpdateListener(new ProgressUpdateListener() {
			
			@Override
			public void updateProgress(PlayProgressEvent e) {
				shouldWaitForJSliderUpdate(slider, label, e);
			}

		});
		
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(play);
		frame.getContentPane().add(stop);
		frame.getContentPane().add(slider);
		frame.getContentPane().add(label);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(300, 100);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		
	}
	
	private void shouldWaitForJSliderUpdate(JSlider slider, final JLabel label, PlayProgressEvent e) {
		if(!isValueLocked()) {
			int totalBytes = (int) player.getProperties().get("mp3.length.bytes");
			int bytesread = e.getCurrentProgress().intValue();
			label.setText(getTimeProgress(bytesread));
			
			long value = bytesread * 1000l / totalBytes;
			slider.setValue((int) value);
		}
	}
	
	
	public String getTimeProgress(long bytesread) {
		float frameRate = (float) player.getProperties().get("mp3.framerate.fps");
		int frameSize = (int) player.getProperties().get("mp3.framesize.bytes");
		long ms  = (long) AudioUtils.bytes2MillisD(bytesread, frameRate, frameSize);

		return DurationFormatUtils.formatDurationHMS(ms);
	}
	
	private boolean isValueLocked() {
		return sliderValueLocked;
	}


}





