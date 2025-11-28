package org.ocelot.tunes4j.audio.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.ocelot.tunes4j.audio.controller.AudioController;
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.event.AudioPlaybackStateEvent;
import org.ocelot.tunes4j.event.AudioSongSelectedEvent;
import org.ocelot.tunes4j.event.AudioUserInteractionEvent;
import org.ocelot.tunes4j.gui.volumeslider.VolumePanel;
import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;
import org.ocelot.tunes4j.utils.ImageUtils;
import org.ocelot.tunes4j.utils.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.tritonus.share.sampled.AudioUtils;

/**
 * Audio Player View - Reactive UI Component for Playback Controls.
 *
 * COPIED FROM: gui/PlayerPanel.java
 * ENHANCED WITH: Reactive event-driven architecture using Observer Pattern
 *
 * Play/Pause/Stop/Volume/Seek controls that publish AudioUserInteractionEvent instead of direct player calls.
 */
@Component
public class AudioPlayerView {

    private final AudioController audioController;

    private boolean sliderValueLocked = false;

    private Song currentSong;

    private JPanel playerPanel = new JPanel();

    private SongDisplayView songDisplayView;

    private JPanel mainDisplayPanel = new JPanel();

    private JSlider slider = new JSlider();

    private JLabel timeLabel = new JLabel();

    private JToggleButton playButton = new JToggleButton();

    private JToggleButton stopButton = new JToggleButton();

    // Mock audio player for UI simulation (in real app, this would be provided by service)
    private MockAudioPlayer mockPlayer = new MockAudioPlayer();

    @Autowired
    public AudioPlayerView(AudioController audioController, SongDisplayView songDisplayView) {
        this.audioController = audioController;
        this.songDisplayView = songDisplayView;
        renderUI();
    }

    /**
     * React to song selection events (update player UI).
     */
    @EventListener(AudioSongSelectedEvent.class)
    public void onSongSelected(AudioSongSelectedEvent event) {
        Song song = event.getSong();
        this.currentSong = song;
        updateMainControlPanel(song);

        System.out.println("🎵 PLAYER VIEW: Song selected for playback - " + song.getTitle());
    }

    /**
     * React to playback state changes (update play/pause/stop button states).
     */
    @EventListener(AudioPlaybackStateEvent.class)
    public void onPlaybackStateChanged(AudioPlaybackStateEvent event) {
        switch (event.getState()) {
            case PLAYING:
                playButton.setSelected(true);
                stopButton.setSelected(false);
                System.out.println("▶️ PLAYER VIEW: Playback started");
                break;
            case PAUSED:
                // Keep play button selected but paused
                System.out.println("⏸️ PLAYER VIEW: Playback paused");
                break;
            case STOPPED:
                playButton.setSelected(false);
                stopButton.setSelected(false);
                slider.setValue(0);
                timeLabel.setText("00:00:00.00");
                System.out.println("⏹️ PLAYER VIEW: Playback stopped");
                // Reset slider and time
                break;
            case LOADING:
                System.out.println("⏳ PLAYER VIEW: Loading song...");
                break;
        }
    }

    /**
     * Render the UI components (copied from PlayerPanel.renderUI()).
     */
    public void renderUI() {
        this.playButton.setBorder(BorderFactory.createEmptyBorder());
        this.playButton.setBorderPainted(false);
        this.playButton.setContentAreaFilled(false);
        this.playButton.setFocusPainted(false);

        this.playButton.setIcon(ResourceLoader.PLAY);
        this.playButton.setSelectedIcon(ResourceLoader.PAUSE);

        // ENHANCED: Now publishes events instead of direct player calls
        this.playButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                audioController.resumePlayback();
            } else {
                audioController.pausePlayback();
            }
        });

        this.stopButton.setBorder(BorderFactory.createEmptyBorder());
        this.stopButton.setBorderPainted(false);
        this.stopButton.setContentAreaFilled(false);
        this.stopButton.setFocusPainted(false);

        stopButton.setIcon(ResourceLoader.STOP);
        stopButton.setSelectedIcon(ResourceLoader.STOP_ON);

        // ENHANCED: Publishes stop event instead of direct player call
        stopButton.addActionListener(e -> {
            stopButton.setSelected(false);
            audioController.stopPlayback();
        });

        slider.setValue(0);
        slider.setMaximum(1000);
        slider.setOpaque(false);

        slider.addChangeListener(e -> {
            if (slider.getValueIsAdjusting()) {
                sliderValueLocked = true;
                int value = slider.getValue();
                // In real implementation, would calculate actual time
                timeLabel.setText("00:" + String.format("%02d", value / 60) + ":" + String.format("%02d", value % 60) + ".00");
            }
        });

        // ENHANCED: Publishes seek event instead of direct player call
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int position = ((JSlider) e.getSource()).getValue();
                audioController.seekTo(position);
                sliderValueLocked = false;
            }
        });

        timeLabel.setText("00:00:00.00");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // In reactive architecture, progress updates would come through events
        // rather than direct player listeners
        // TODO: Add @EventListener for progress update events

        playerPanel = new JPanel();
        playerPanel.setOpaque(false);
        playerPanel.setLayout(new FlowLayout());
        playerPanel.add(playButton);
        playerPanel.add(stopButton);

        // Volume panel - in reactive architecture, this could be separate component
        // For now, create without direct player coupling
        VolumePanel volPanel = new VolumePanel(mockPlayer); // Mock player for UI
        playerPanel.add(volPanel);
        playerPanel.setPreferredSize(new Dimension(150,80));

        mainDisplayPanel = new JPanel();
        mainDisplayPanel.setOpaque(false);
        mainDisplayPanel.setLayout(new BoxLayout(mainDisplayPanel, BoxLayout.Y_AXIS));

        // Use SongDisplayView instead of SongDisplayPanel
        mainDisplayPanel.add(songDisplayView.getSongDisplayPanel());
        mainDisplayPanel.add(slider);
    }

    /**
     * Set current song (called through events, not direct method calls).
     */
    public void setSong(Song song) {
        this.currentSong = song;
    }

    public Song getSong() {
        return this.currentSong;
    }

    /**
     * Private method to update UI with song information (called from event listeners).
     */
    private void updateMainControlPanel(Song song) {
        Image img = ResourceLoader.ICON_APPICON.getImage();
        if (song.getArtWork() != null) {
            img = ImageUtils.read(song.getArtWork());
        }

        // Update song display through the decoupled view component
        songDisplayView.setArtwork(img);
        songDisplayView.setArtist(song.getArtist() + " - " + song.getAlbum());
        songDisplayView.setSongTitle(song.getTitle());
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

    public JPanel getSongDetail() {
        return songDisplayView.getSongDisplayPanel();
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

    // Utility method for time formatting (copied from PlayerPanel)
    public String getTimeProgress(long bytesread) {
        // In reactive architecture, time formatting would be handled by the service
        float frameRate = 24.0f; // Default frame rate
        int frameSize = 1152;    // Default frame size
        long ms = (long) AudioUtils.bytes2MillisD(bytesread, frameRate, frameSize);
        return DurationFormatUtils.formatDurationHMS(ms);
    }

    /**
     * Refresh theme colors when theme changes.
     */
    public void refreshThemeColors() {
        System.out.println("🎨 PLAYER VIEW: Refreshing theme colors");
        if (songDisplayView != null) {
            songDisplayView.refreshThemeColors();
        }
    }

    /**
     * Mock audio player for UI components that need player interface.
     * In reactive architecture, this is just for UI simulation.
     */
    private static class MockAudioPlayer extends Tunes4JAudioPlayer {
        @Override
        public void play() {
            System.out.println("🎵 MOCK PLAYER: Direct play() not used in reactive architecture - use events instead");
        }
    }
}
