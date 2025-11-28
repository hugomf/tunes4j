package org.ocelot.tunes4j.application.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.ocelot.tunes4j.application.view.menu.ApplicationMenuBar;
import org.ocelot.tunes4j.audio.view.AudioPlaybackView;
import org.ocelot.tunes4j.audio.view.AudioPlayerView;
import org.ocelot.tunes4j.audio.view.SongDisplayView;
import org.ocelot.tunes4j.library.view.SongListView;
import org.ocelot.tunes4j.playlist.view.PlaylistView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Application View - Main Window for the Tunes4J Application.
 *
 * COPIED FROM: gui.ApplicationWindow.java
 * ENHANCED WITH: Reactive event-driven architecture for cross-context communication
 *
 * Coordinates all reactive views and publishes application-wide events.
 * Integrates Audio, Playlist, and Application bounded contexts through events.
 */
@Component
public class ApplicationView extends JFrame {

    private final ApplicationEventPublisher eventPublisher;

    // Reactive view components from different bounded contexts
    private final PlaylistView playlistView;
    private final AudioPlayerView audioPlayerView;
    private final SongListView songListView; // INTEGRATED: Library bounded context provides UI component
    private final SongDisplayView songDisplayView;
    private final AudioPlaybackView audioPlaybackView;
    private final ApplicationMenuBar appMenuBar;

    private JPanel contentPanel;

    @Autowired
    public ApplicationView(ApplicationEventPublisher eventPublisher,
                          PlaylistView playlistView,
                          AudioPlayerView audioPlayerView,
                          SongListView songListView, // INTEGRATED: Library bounded context UI component
                          SongDisplayView songDisplayView,
                          AudioPlaybackView audioPlaybackView,
                          ApplicationMenuBar appMenuBar) {
        this.eventPublisher = eventPublisher;
        this.playlistView = playlistView;
        this.audioPlayerView = audioPlayerView;
        this.songListView = songListView; // INTEGRATED: Library bounded context UI component
        this.songDisplayView = songDisplayView;
        this.audioPlaybackView = audioPlaybackView;
        this.appMenuBar = appMenuBar;

        initializeApplication();
    }

    /**
     * Initialize the main application window and layout.
     * Sets up reactive component integration.
     */
    private void initializeApplication() {
        setupWindowProperties();
        setupLayout();
        setupEventHandling();
        setupWindowClosing();

        // Initial theme colors
        refreshThemeColors();
    }

    private void setupWindowProperties() {
        setTitle("Tunes4J - Reactive Media Player");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setPreferredSize(getDefaultSize());
        setLocationRelativeTo(null); // Center window

        // Set application icon
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/icon72.png")));
        } catch (Exception e) {
            // Icon not found, continue without
        }
    }

    private void setupLayout() {
        // Main content panel
        contentPanel = new JPanel(new BorderLayout());
        setContentPane(contentPanel);

        // Menu bar - reactive component
        setJMenuBar(appMenuBar.getMenuBar());

        // Create simple layout without LeftSplitPane for now
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Left: Playlist panel
        mainPanel.add(playlistView.getContentPane(), BorderLayout.WEST);

        // Right: Main content area
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Top: Song display and player controls
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(songDisplayView.getSongDisplayPanel(), BorderLayout.CENTER);
        topPanel.add(audioPlayerView.getPlayerPanel(), BorderLayout.SOUTH);

        // Center: Song list table - Library bounded context component
        rightPanel.add(songListView.getTablePane(), BorderLayout.CENTER);
        rightPanel.add(topPanel, BorderLayout.NORTH);

        mainPanel.add(rightPanel, BorderLayout.CENTER);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        pack();
    }

    private void setupEventHandling() {
        // TODO: Add @EventListener annotations for application-wide events
        // - Theme changes, window state, etc.

        System.out.println("🖥️ APPLICATION VIEW: Initialized with reactive component integration");
    }

    private void setupWindowClosing() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Publish application closing event
                eventPublisher.publishEvent(new ApplicationClosingEvent(this));

                // Exit application
                System.exit(0);
            }
        });
    }

    /**
     * Get default window size based on screen dimensions.
     */
    private Dimension getDefaultSize() {
        GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                         .getDefaultScreenDevice()
                                                         .getDefaultConfiguration();
        Rectangle screenRect = config.getBounds();
        double width = screenRect.getWidth() * 0.75;
        double height = screenRect.getHeight() * 0.75;
        return new Dimension((int) width, (int) height);
    }

    /**
     * Show the main application window (reactive API for controllers).
     */
    public void showApplication() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            toFront();
            System.out.println("🖥️ APPLICATION VIEW: Main window displayed");
        });
    }

    /**
     * Refresh theme colors across all integrated reactive components.
     */
    public void refreshThemeColors() {
        // Use UIManager to get current theme colors
        Color bgColor = javax.swing.UIManager.getColor("Panel.background");

        if (bgColor != null) {
            contentPanel.setBackground(bgColor);
            System.out.println("🎨 APPLICATION VIEW: Applied theme colors");
        }

        // Notify reactive components to refresh their themes
        if (playlistView != null) playlistView.refreshThemeColors();
        if (audioPlayerView != null) audioPlayerView.refreshThemeColors();
        if (songListView != null) songListView.refreshThemeColors(); // INTEGRATED: Library bounded context component
        if (songDisplayView != null) songDisplayView.refreshThemeColors();
        // Note: AudioPlaybackView may not have refreshThemeColors method
        if (appMenuBar != null) appMenuBar.refreshThemeColors();
    }

    /**
     * Inner class for application closing event.
     */
    public static class ApplicationClosingEvent extends ApplicationEvent {
        public ApplicationClosingEvent(Object source) {
            super(source);
        }
    }
}
