package org.ocelot.tunes4j.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.ocelot.tunes4j.components.JSLidingLabel;
import org.ocelot.tunes4j.components.RoundedJPanel;
import org.ocelot.tunes4j.components.volumeslider.VolumePanel;
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.event.PlayProgressEvent;
import org.ocelot.tunes4j.event.ProgressUpdateListener;
import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;
import org.ocelot.tunes4j.utils.ImageUtils;
import org.ocelot.tunes4j.utils.ResourceLoader;
import org.tritonus.share.sampled.AudioUtils;

public class PlayerPanel  {

	private Tunes4JAudioPlayer player = new Tunes4JAudioPlayer();

	private boolean sliderValueLocked = false;
	
	private Song currentSong;
	
	private JPanel playerPanel = new JPanel();
	
	private JPanel sliderPanel = new JPanel();
	
	private RoundedJPanel controlPanel;
	
	private JLabel artWorkImageLabel = new JLabel();
	
	private JSLidingLabel lblSongTitle = new JSLidingLabel("Song Title");
	
	private JLabel lblArtistAndAlbum = new JLabel("Artist - Album");
	
	private JSlider slider = new JSlider();
	
	private JLabel timeLabel = new JLabel();
	
	private JToggleButton playButton = new JToggleButton();
	
	private JToggleButton stopButton = new JToggleButton();

	public PlayerPanel() {
		renderUI();
	}
	
	public void renderUI() {

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
						play();
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
						stop();
					}
				}).start();

			}
		});
		
		
		
		Image resized = ImageUtils.resize(ResourceLoader.ICON_APPICON.getImage(), 60, 60);
		artWorkImageLabel.setIcon(new ImageIcon(resized));
		artWorkImageLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		
		lblSongTitle.setHorizontalAlignment(SwingConstants.LEFT);
		lblSongTitle.setFont(new Font("Verdana", Font.PLAIN, 16));
		lblSongTitle.setForeground(Color.black);
		lblSongTitle.setPreferredSize(new Dimension(250, 20));
		lblSongTitle.setInfinity(true);
		
		
		lblArtistAndAlbum.setHorizontalAlignment(SwingConstants.LEFT);
		lblArtistAndAlbum.setFont(new Font("Arial", Font.PLAIN, 12));
		lblArtistAndAlbum.setForeground(Color.black);
		lblArtistAndAlbum.setPreferredSize(new Dimension(250, 20));
		
		JPanel songDetailPanel = new JPanel();
		songDetailPanel.setLayout(new BoxLayout(songDetailPanel, BoxLayout.Y_AXIS));
		songDetailPanel.setOpaque(false);
		songDetailPanel.add(new JLabel("   "));
		songDetailPanel.add(new JLabel("   "));
		songDetailPanel.add(lblSongTitle);
		songDetailPanel.add(lblArtistAndAlbum);
		
		
		this.controlPanel = new RoundedJPanel();
		this.controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.controlPanel.setForeground(Color.darkGray);
		//this.controlPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		this.controlPanel.add(artWorkImageLabel);
		this.controlPanel.add(songDetailPanel);
		this.controlPanel.setBackground(new Color(0.95f,0.96f,0.98f));
		
		
		slider.setValue(0);
		slider.setMaximum(1000);
		slider.setOpaque(false);
		
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (slider.getValueIsAdjusting()) {
					sliderValueLocked = true;
					int value = slider.getValue();
					int totalBytes = (int) player.getProperties().get("mp3.length.bytes");
					long bytesread = (long) value * totalBytes / 1000l;
					timeLabel.setText(getTimeProgress(bytesread));
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
		
		timeLabel.setText("00:00:00.00");
		timeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		player.addProgressUpdateListener(new ProgressUpdateListener() {

			@Override
			public void updateProgress(PlayProgressEvent e) {
				
				if (!isValueLocked()) {
					int totalBytes = (int) player.getProperties().get("mp3.length.bytes");
					int bytesread = e.getCurrentProgress().intValue();
					timeLabel.setText(getTimeProgress(bytesread));
					long value = bytesread * 1000l / totalBytes;
					slider.setValue((int) value);
				}
				
			}

		});
		
		playerPanel = new JPanel();
		//playerPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		playerPanel.setOpaque(false);
		playerPanel.setLayout(new FlowLayout());
		playerPanel.add(playButton);
		playerPanel.add(stopButton);
		playerPanel.setPreferredSize(new Dimension(150,80));
		VolumePanel volPanel =  new VolumePanel(this.player);
		playerPanel.add(volPanel);
		
		sliderPanel = new JPanel();
		sliderPanel.setOpaque(false);
		//sliderPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		sliderPanel.setPreferredSize(new Dimension(350, 110));
		sliderPanel.add(controlPanel);
		//sliderPanel.add(timeLabel);
		sliderPanel.add(slider);

	}
	
	public void setSong(Song song) {
		this.currentSong = song;
	}
	
	public Song getSong() {
		return this.currentSong;
	}
	
	public void play(Song songFile) {
		this.currentSong = songFile;
		play();
	}
	
	public void play() {
		this.player.open(this.currentSong.getSongFile());
		this.player.reset();
		this.player.play();
		updateMainControlPanel(this.currentSong);
		getPlayButton().setSelected(true);
	}
	
	
	private void updateMainControlPanel(Song song) {

		Image img = ResourceLoader.ICON_APPICON.getImage();
		if (song.getArtWork() != null) {
			img = ImageUtils.read(song.getArtWork());
		}
		ImageIcon resized = new ImageIcon(ImageUtils.resize(img, 60, 60));
	 	this.artWorkImageLabel.setIcon(resized);
	 	this.lblArtistAndAlbum.setText(this.currentSong.getArtist() + " - " + this.currentSong.getAlbum());
	 	
		this.lblSongTitle.stop();
		this.lblSongTitle.setText(this.currentSong.getTitle());
		this.lblSongTitle.play(200);
		
	}

	public void stop() {
		this.player.stop();
		//System.out.println(slider.getValue());
		this.slider.setValue(0);
		//System.out.println(slider.getValue());
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
	
	public JPanel getSliderPanel() {
		return this.sliderPanel;
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
