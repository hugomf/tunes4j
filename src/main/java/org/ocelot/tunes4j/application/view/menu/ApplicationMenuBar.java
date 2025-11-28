package org.ocelot.tunes4j.application.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.ocelot.tunes4j.audio.controller.AudioController;
import org.ocelot.tunes4j.utils.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Application Menu Bar - Reactive Menu System for Tunes4J.
 *
 * COPIED FROM: gui.ApplicationMenuBar.java
 * ENHANCED WITH: Reactive event-driven actions using Observer Pattern
 *
 * Menu actions publish domain events instead of direct service calls.
 * Supports reactive menu state updates.
 */
@Component
public class ApplicationMenuBar {

    private final ApplicationEventPublisher eventPublisher;
    private final AudioController audioController;

    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu, viewMenu, playbackMenu, windowMenu, helpMenu;

    @Autowired
    public ApplicationMenuBar(ApplicationEventPublisher eventPublisher, AudioController audioController) {
        this.eventPublisher = eventPublisher;
        this.audioController = audioController;
        initializeMenuBar();
    }

    /**
     * Initialize the menu bar with reactive menu items.
     */
    private void initializeMenuBar() {
        menuBar = new JMenuBar();

        createFileMenu();
        createEditMenu();
        createViewMenu();
        createPlaybackMenu();
        createWindowMenu();
        createHelpMenu();

        // Add all menus to the bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(playbackMenu);
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);
    }

    private void createFileMenu() {
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        // Import Files - Publish import event
        JMenuItem importItem = new JMenuItem("Import Files...");
        importItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        importItem.addActionListener(e -> {
            eventPublisher.publishEvent(new FileImportRequestedEvent(this));
        });
        fileMenu.add(importItem);

        fileMenu.addSeparator();

        // Exit - Publish exit event
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exitItem.addActionListener(e -> {
            eventPublisher.publishEvent(new ApplicationExitRequestedEvent(this));
        });
        fileMenu.add(exitItem);
    }

    private void createEditMenu() {
        editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        // Cut, Copy, Paste - Standard actions
        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        editMenu.add(cutItem);

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        editMenu.add(copyItem);

        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        editMenu.add(pasteItem);
    }

    private void createViewMenu() {
        viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);

        // Toggle playlist sidebar
        JMenuItem togglePlaylistItem = new JMenuItem("Toggle Playlist");
        togglePlaylistItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        togglePlaylistItem.addActionListener(e -> {
            eventPublisher.publishEvent(new ViewTogglePlaylistEvent(this));
        });
        viewMenu.add(togglePlaylistItem);

        // Refresh theme colors
        JMenuItem refreshThemeItem = new JMenuItem("Refresh Theme");
        refreshThemeItem.addActionListener(e -> {
            eventPublisher.publishEvent(new ThemeRefreshRequestedEvent(this));
        });
        viewMenu.add(refreshThemeItem);
    }

    private void createPlaybackMenu() {
        playbackMenu = new JMenu("Playback");
        playbackMenu.setMnemonic(KeyEvent.VK_P);

        // Playback controls - Delegate to AudioController
        JMenuItem playItem = new JMenuItem("Play");
        playItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
        playItem.addActionListener(e -> audioController.resumePlayback());
        playbackMenu.add(playItem);

        JMenuItem pauseItem = new JMenuItem("Pause");
        pauseItem.addActionListener(e -> audioController.pausePlayback());
        playbackMenu.add(pauseItem);

        JMenuItem stopItem = new JMenuItem("Stop");
        stopItem.addActionListener(e -> audioController.stopPlayback());
        playbackMenu.add(stopItem);

        playbackMenu.addSeparator();

        // Next/Previous - Delegate to AudioController
        JMenuItem nextItem = new JMenuItem("Next");
        nextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.CTRL_MASK));
        nextItem.addActionListener(e -> {
            // TODO: Add next song logic
            System.out.println("▶️ Next song requested");
        });
        playbackMenu.add(nextItem);

        JMenuItem prevItem = new JMenuItem("Previous");
        prevItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.CTRL_MASK));
        prevItem.addActionListener(e -> {
            // TODO: Add previous song logic
            System.out.println("◀️ Previous song requested");
        });
        playbackMenu.add(prevItem);
    }

    private void createWindowMenu() {
        windowMenu = new JMenu("Window");
        windowMenu.setMnemonic(KeyEvent.VK_W);

        // Minimize window
        JMenuItem minimizeItem = new JMenuItem("Minimize");
        minimizeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.ALT_MASK));
        minimizeItem.addActionListener(e -> {
            eventPublisher.publishEvent(new WindowMinimizeRequestedEvent(this));
        });
        windowMenu.add(minimizeItem);
    }

    private void createHelpMenu() {
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        // About dialog
        JMenuItem aboutItem = new JMenuItem("About Tunes4J");
        aboutItem.addActionListener(e -> {
            eventPublisher.publishEvent(new AboutDialogRequestedEvent(this));
        });
        helpMenu.add(aboutItem);

        // System info
        JMenuItem systemInfoItem = new JMenuItem("System Information");
        systemInfoItem.addActionListener(e -> {
            eventPublisher.publishEvent(new SystemInfoRequestedEvent(this));
        });
        helpMenu.add(systemInfoItem);
    }

    /**
     * Get the main menu bar for integration.
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * Refresh theme colors for menu components.
     */
    public void refreshThemeColors() {
        System.out.println("🎨 APPLICATION MENU BAR: Refreshing theme colors");
        // Menu bars handle theme colors automatically via UIManager
    }

    // Event classes for reactive communication

    public static class FileImportRequestedEvent extends org.springframework.context.ApplicationEvent {
        public FileImportRequestedEvent(Object source) { super(source); }
    }

    public static class ApplicationExitRequestedEvent extends org.springframework.context.ApplicationEvent {
        public ApplicationExitRequestedEvent(Object source) { super(source); }
    }

    public static class ViewTogglePlaylistEvent extends org.springframework.context.ApplicationEvent {
        public ViewTogglePlaylistEvent(Object source) { super(source); }
    }

    public static class ThemeRefreshRequestedEvent extends org.springframework.context.ApplicationEvent {
        public ThemeRefreshRequestedEvent(Object source) { super(source); }
    }

    public static class WindowMinimizeRequestedEvent extends org.springframework.context.ApplicationEvent {
        public WindowMinimizeRequestedEvent(Object source) { super(source); }
    }

    public static class AboutDialogRequestedEvent extends org.springframework.context.ApplicationEvent {
        public AboutDialogRequestedEvent(Object source) { super(source); }
    }

    public static class SystemInfoRequestedEvent extends org.springframework.context.ApplicationEvent {
        public SystemInfoRequestedEvent(Object source) { super(source); }
    }
}
