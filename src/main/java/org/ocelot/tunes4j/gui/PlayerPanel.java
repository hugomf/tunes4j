package org.ocelot.tunes4j.gui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.ocelot.tunes4j.event.PlayProgressEvent;
import org.ocelot.tunes4j.event.ProgressUpdateListener;
import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;
import org.ocelot.tunes4j.utils.ResourceLoader;
import org.tritonus.share.sampled.AudioUtils;

public class PlayerPanel extends JPanel {

	private static final long serialVersionUID = 1960164432445166128L;

	private Tunes4JAudioPlayer player;

	private boolean sliderValueLocked = false;
	
	private JSlider slider = new JSlider();
	
	private JLabel label = new JLabel();
	
	private JToggleButton playButton = new JToggleButton();
	
	private JToggleButton stopButton = new JToggleButton();

	public PlayerPanel(ApplicationWindow parentFrame) {


		this.player = parentFrame.getPlayer();
		

		this.playButton.setBorder(BorderFactory.createEmptyBorder());
		this.playButton.setBorderPainted(false);
		this.playButton.setContentAreaFilled(false);
		this.playButton.setFocusPainted(false);
		
		this.playButton.setIcon(ResourceLoader.PLAY);
		this.playButton.setSelectedIcon(ResourceLoader.PAUSE);

		this.playButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				int state = e.getStateChange();
				if (state == ItemEvent.SELECTED) {
					if (player.isPaused()) {
						player.resume();
					} else {
						parentFrame.getMediaTable().playSelectedRow();
					}
				} else {
					player.pause();
				}
			}
		});

		this.stopButton.setBorder(BorderFactory.createEmptyBorder());
		this.stopButton.setBorderPainted(false);
		this.stopButton.setContentAreaFilled(false);
		this.stopButton.setFocusPainted(false);

		stopButton.setIcon(ResourceLoader.STOP);
		stopButton.setSelectedIcon(ResourceLoader.STOP_ON);
		stopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stopButton.setSelected(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						player.stop();
						System.out.println(slider.getValue());
						slider.setValue(0);
						System.out.println(slider.getValue());
						playButton.setSelected(false);
					}
				}).start();

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
					long bytesread = (long) value * totalBytes / 1000l;
					label.setText(getTimeProgress(bytesread));
				}

			}
		});
		
		slider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				JSlider source = (JSlider) e.getSource();
				player.skip(source.getValue());
				sliderValueLocked = false;
			}

		});
		
		label.setText("00:00:00.00");
		label.setFont(new Font("Arial", Font.PLAIN, 15));
		
		player.addProgressUpdateListener(new ProgressUpdateListener() {

			@Override
			public void updateProgress(PlayProgressEvent e) {
				
				if (!isValueLocked()) {
					int totalBytes = (int) player.getProperties().get("mp3.length.bytes");
					int bytesread = e.getCurrentProgress().intValue();
					label.setText(getTimeProgress(bytesread));
					long value = bytesread * 1000l / totalBytes;
					slider.setValue((int) value);
				}
				
			}

		});
		
		setLayout(new FlowLayout());
		add(playButton);
		add(stopButton);
		add(slider);
		add(label);
		
	}
	
	public JToggleButton getPlayButton() {
		return this.playButton;
	}

	public JToggleButton getStopButton() {
		return this.stopButton;
	}


	public String getTimeProgress(long bytesread) {
		float frameRate = (float) player.getProperties().get("mp3.framerate.fps");
		int frameSize = (int) player.getProperties().get("mp3.framesize.bytes");
		long ms = (long) AudioUtils.bytes2MillisD(bytesread, frameRate, frameSize);

		return DurationFormatUtils.formatDurationHMS(ms);
	}

	private boolean isValueLocked() {
		return sliderValueLocked;
	}
	

}
