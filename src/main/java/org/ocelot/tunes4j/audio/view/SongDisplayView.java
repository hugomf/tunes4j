package org.ocelot.tunes4j.audio.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.ocelot.tunes4j.components.JSLidingLabel;
import org.ocelot.tunes4j.components.RoundedJPanel;
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.event.AudioSongSelectedEvent;
import org.ocelot.tunes4j.utils.ImageUtils;
import org.ocelot.tunes4j.utils.ResourceLoader;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Song Display View - Reactive UI Component for Current Song Information.
 *
 * COPIED FROM: gui/SongDisplayPanel.java
 * ENHANCED WITH: Reactive event-driven architecture and Observer Pattern
 *
 * Displays song title, artist, album, artwork, and spectrum visualization.
 * Reacts to AudioSongSelectedEvent to update display.
 */
@Component
@SuppressWarnings("serial")
public class SongDisplayView extends RoundedJPanel {

    private JLabel artWorkImageLabel = new JLabel();

    private JSLidingLabel lblSongTitle = new JSLidingLabel("Song Title");

    private JLabel lblArtistAndAlbum = new JLabel("Artist - Album");

    private JPanel songDetailPanel = new JPanel();

    public SongDisplayView() {
        // Set theme-aware background immediately
        setBackground(javax.swing.UIManager.getColor("Panel.background"));
        renderUI();
    }

    /**
     * React to song selection events - update the display with new song information.
     */
    @EventListener(AudioSongSelectedEvent.class)
    public void onSongSelected(AudioSongSelectedEvent event) {
        System.out.println("🎵 SONG DISPLAY VIEW: RECEIVED AudioSongSelectedEvent");
        Song song = event.getSong();
        System.out.println("🎵 SONG DISPLAY VIEW: Processing song - " + song.getTitle());

        setSongTitle(song.getTitle());
        setArtist(song.getArtist() + " - " + song.getAlbum());

        Image img = ResourceLoader.ICON_APPICON.getImage();
        if (song.getArtWork() != null) {
            System.out.println("🎵 SONG DISPLAY VIEW: Song has artwork data, size: " + song.getArtWork().length + " bytes");
            try {
                img = ImageUtils.read(song.getArtWork());
                if (img != null) {
                    System.out.println("🎵 SONG DISPLAY VIEW: Successfully converted artwork to Image: " + img.getWidth(null) + "x" + img.getHeight(null));
                } else {
                    System.out.println("🎵 SONG DISPLAY VIEW: Album artwork conversion failed, using default icon");
                }
            } catch (Exception e) {
                System.out.println("🎵 SONG DISPLAY VIEW: Exception converting artwork: " + e.getMessage());
            }
        } else {
            System.out.println("🎵 SONG DISPLAY VIEW: No artwork data available, using default icon");
        }
        setArtwork(img);

        System.out.println("🎵 SONG DISPLAY VIEW: Updated display with song - " + song.getTitle());
    }

    /**
     * Render the UI components (copied from SongDisplayPanel.renderUI()).
     */
    private void renderUI() {
        Image resized = ImageUtils.resize(ResourceLoader.ICON_APPICON.getImage(), 60, 60);
        this.artWorkImageLabel.setIcon(new ImageIcon(resized));
        this.artWorkImageLabel.setHorizontalAlignment(SwingConstants.LEFT);

        lblSongTitle.setHorizontalAlignment(SwingConstants.LEFT);
        lblSongTitle.setFont(new Font("Verdana", Font.PLAIN, 16));
        lblSongTitle.setForeground(Color.black);
        lblSongTitle.setPreferredSize(new Dimension(250, 20));
        lblSongTitle.setInfinity(true);

        lblArtistAndAlbum.setHorizontalAlignment(SwingConstants.LEFT);
        lblArtistAndAlbum.setFont(new Font("Arial", Font.PLAIN, 12));
        lblArtistAndAlbum.setForeground(Color.black);
        lblArtistAndAlbum.setPreferredSize(new Dimension(250, 20));

        songDetailPanel.setLayout(new BoxLayout(songDetailPanel, BoxLayout.Y_AXIS));
        songDetailPanel.setOpaque(false);

        songDetailPanel.add(new JLabel("   "));
        songDetailPanel.add(new JLabel("   "));
        songDetailPanel.add(lblSongTitle);
        songDetailPanel.add(lblArtistAndAlbum);

        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0)); // Add 20px horizontal gap
        setForeground(Color.darkGray);

        add(artWorkImageLabel);
        add(songDetailPanel);
        setBackground(javax.swing.UIManager.getColor("Panel.background"));
    }

    public void setSongTitle(String title) {
        this.lblSongTitle.stop();
        this.lblSongTitle.setText(title);
        this.lblSongTitle.play(200);
    }

    public void setArtist(String artist) {
        this.lblArtistAndAlbum.setText(artist);
    }

    public void setArtwork(Image image) {
        // Ensure UI updates happen on the Event Dispatch Thread
        java.awt.EventQueue.invokeLater(() -> {
            ImageIcon resized = new ImageIcon(ImageUtils.resize(image, 60, 60));
            System.out.println("🎵 SONG DISPLAY VIEW: Setting artwork icon on label - icon not null: " + (resized != null));
            this.artWorkImageLabel.setIcon(resized);
            this.artWorkImageLabel.repaint();
            System.out.println("🎵 SONG DISPLAY VIEW: Artwork icon set and label repainted on EDT");
        });
    }

    /**
     * Get the main panel for integration with parent containers.
     */
    public JPanel getSongDisplayPanel() {
        return this;
    }

    /**
     * Refresh theme colors when theme changes.
     * Enhanced for reactive architecture to handle theme events.
     */
    public void refreshThemeColors() {
        System.out.println("🔄 SONG DISPLAY VIEW: Refreshing theme colors");
        java.awt.Color bg = javax.swing.UIManager.getColor("Panel.background");
        System.out.println("  └─ Panel.background: " + bg);

        // Update main panel background
        setBackground(bg);

        // Force repaint of the component
        repaint();
    }
}
