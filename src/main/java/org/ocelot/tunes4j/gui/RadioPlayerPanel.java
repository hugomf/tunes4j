package org.ocelot.tunes4j.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.ocelot.tunes4j.dto.RadioStation;
import org.ocelot.tunes4j.player.RadioStreamPlayer;
import org.ocelot.tunes4j.utils.ImageUtils;
import org.ocelot.tunes4j.utils.ResourceLoader;

public class RadioPlayerPanel  {

	private RadioStreamPlayer radioPlayer = new RadioStreamPlayer();

//	private boolean sliderValueLocked = false;
	
	private RadioStation currentRadioStation;
	
	private JPanel playerPanel = new JPanel();
	
	private SongDisplayPanel songDetailsPanel = new SongDisplayPanel();
	
	private JPanel mainDisplayPanel = new JPanel();
	
	private JSlider slider = new JSlider();
	
	private JLabel timeLabel = new JLabel();
	
	private JToggleButton playButton = new JToggleButton();
	
	private JToggleButton stopButton = new JToggleButton();


	public RadioPlayerPanel() {
		renderUI();
	}
	
	public void renderUI() {

		this.playButton.setBorder(BorderFactory.createEmptyBorder());
		this.playButton.setBorderPainted(false);
		this.playButton.setContentAreaFilled(false);
		this.playButton.setFocusPainted(false);
		
		this.playButton.setIcon(ResourceLoader.PLAY);
		this.playButton.setSelectedIcon(ResourceLoader.PAUSE);

//		this.playButton.addItemListener(new ItemListener() {
//
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				int state = e.getStateChange();
//				if (state == ItemEvent.SELECTED) {
//					if (player.isPaused()) {
//						player.resume();
//					} else {
//						play();
//					}
//				} else {
//					player.pause();
//				}
//			}
//		});
		
		
		this.playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				play();
			}
		});

		this.stopButton.setBorder(BorderFactory.createEmptyBorder());
		this.stopButton.setBorderPainted(false);
		this.stopButton.setContentAreaFilled(false);
		this.stopButton.setFocusPainted(false);

		this.stopButton.setIcon(ResourceLoader.STOP);
		this.stopButton.setSelectedIcon(ResourceLoader.STOP_ON);
		this.stopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stopButton.setSelected(false);
				stop();
			}
		});
		
		playerPanel = new JPanel();
		//playerPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		playerPanel.setOpaque(false);
		playerPanel.setLayout(new FlowLayout());
		playerPanel.add(playButton);
		playerPanel.add(stopButton);
		

		
		
		playerPanel.setPreferredSize(new Dimension(150,80));
//		VolumePanel volPanel =  new VolumePanel(this.player);
//		playerPanel.add(volPanel);
		
		this.slider.setValue(0);
		this.slider.setMaximum(1000);
		this.slider.setOpaque(false);
		this.slider.setEnabled(false);
		
//		slider.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				if (slider.getValueIsAdjusting()) {
//					sliderValueLocked = true;
//					int value = slider.getValue();
//					int totalBytes = (int) player.getProperties().get("mp3.length.bytes");
//					long bytesread = (long) value * totalBytes / 1000l;
//					timeLabel.setText(getTimeProgress(bytesread));
//				}
//
//			}
//		});
		
//		slider.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				JSlider source = (JSlider) e.getSource();
//				player.skip(source.getValue());
//				sliderValueLocked = false;
//			}
//
//		});
		
		timeLabel.setText("00:00:00.00");
		timeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
//		player.addProgressUpdateListener(new ProgressUpdateListener() {
//
//			@Override
//			public void updateProgress(PlayProgressEvent e) {
//				
//				if (!isValueLocked()) {
//					int totalBytes = (int) player.getProperties().get("mp3.length.bytes");
//					int bytesread = e.getCurrentProgress().intValue();
//					timeLabel.setText(getTimeProgress(bytesread));
//					long value = bytesread * 1000l / totalBytes;
//					slider.setValue((int) value);
//				}
//				
//			}
//
//		});
		
		mainDisplayPanel = new JPanel();
		mainDisplayPanel.setOpaque(false);
		//sliderPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
		//mainDisplayPanel.setLayout(new FlowLayout());
		
		//mainDisplayPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		mainDisplayPanel.setLayout(new BoxLayout(mainDisplayPanel, BoxLayout.Y_AXIS));
		mainDisplayPanel.add(this.songDetailsPanel);
		//sliderPanel.add(timeLabel);
		mainDisplayPanel.add(slider);

	}
	
	public void setRadioStation(RadioStation radioStation) {
		this.currentRadioStation = radioStation;
	}
	
	public RadioStation getRadioStation() {
		return this.currentRadioStation;
	}
	
	public void play(RadioStation radioStation) {
		this.currentRadioStation = radioStation;
		play();
	}
	
	public void play() {
		try {
			this.radioPlayer.open(this.currentRadioStation.getUrl());
			this.radioPlayer.play();
			updateMainControlPanel(this.currentRadioStation);
			getPlayButton().setSelected(true);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void updateMainControlPanel(RadioStation station) {

		Image img = ResourceLoader.ICON_APPICON.getImage();
		if (station.getArtWork() != null) {
			img = ImageUtils.read(station.getArtWork());
		}
		this.songDetailsPanel.setArtwork(img);
		this.songDetailsPanel.setSongTitle("Streaming: " + station.getName());
		this.songDetailsPanel.setArtist(station.getUrl() + "-" + station.getGenre());
		
	}

	public void stop() {
		this.radioPlayer.stop();
//		System.out.println(slider.getValue());
//		this.slider.setValue(0);
//		System.out.println(slider.getValue());
		this.playButton.setSelected(false);
	}
	
	public JToggleButton getPlayButton() {
		return this.playButton;
	}

	public JToggleButton getStopButton() {
		return this.stopButton;
	}
	
	public JPanel getPlayerPanel() {
		return this.playerPanel;
	}
	
	public JPanel getMainDisplayPanel() {
		return this.mainDisplayPanel;
	}
	
	public void show() {
		this.playerPanel.setVisible(true);
		this.mainDisplayPanel.setVisible(true);
	}
	
	public void hide() {
		this.playerPanel.setVisible(false);
		this.mainDisplayPanel.setVisible(false);
	}
	
//	public String getTimeProgress(long bytesread) {
//		float frameRate = (float) player.getProperties().get("mp3.framerate.fps");
//		int frameSize = (int) player.getProperties().get("mp3.framesize.bytes");
//		long ms = (long) AudioUtils.bytes2MillisD(bytesread, frameRate, frameSize);
//
//		return DurationFormatUtils.formatDurationHMS(ms);
//	}

//	private boolean isValueLocked() {
//		return sliderValueLocked;
//	}
	

}
