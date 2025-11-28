package org.ocelot.tunes4j.application.controller;

import org.ocelot.tunes4j.application.controller.BaseController;
import org.ocelot.tunes4j.application.view.ApplicationView;
import org.ocelot.tunes4j.application.view.menu.ApplicationMenuBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

/**
 * Application Controller - Reactive Coordinator for Application Bounded Context.
 * Handles event-driven communication for application-wide features.
 *
 * Manages:
 * - Application lifecycle (startup, shutdown)
 * - Menu actions and system events
 * - Cross-context coordination
 * - Workspace/state management
 */
@Controller
public class ApplicationController extends BaseController {

    private final ApplicationView applicationView;
    private final ApplicationMenuBar applicationMenuBar;

    @Autowired
    public ApplicationController(ApplicationView applicationView, ApplicationMenuBar applicationMenuBar) {
        this.applicationView = applicationView;
        this.applicationMenuBar = applicationMenuBar;

        // Initialize application immediately
        initializeApplication();
    }

    /**
     * Initialize application on startup.
     * Shows the main application window.
     */
    private void initializeApplication() {
        System.out.println("🖥️ APPLICATION CONTROLLER: Initializing reactive media player");

        // Show the main application window
        applicationView.showApplication();

        System.out.println("🖥️ APPLICATION CONTROLLER: Reactive MVC application initialized");
    }

    /**
     * Handle file import requests from menu.
     */
    @EventListener(ApplicationMenuBar.FileImportRequestedEvent.class)
    public void onFileImportRequested(ApplicationMenuBar.FileImportRequestedEvent event) {
        System.out.println("🖥️ APPLICATION CONTROLLER: File import requested");

        // TODO: Implement file import dialog and logic
        // - Show file chooser dialog
        // - Process selected files
        // - Add to library/playlist
        // - Publish import completion events
    }

    /**
     * Handle application exit requests from menu.
     */
    @EventListener(ApplicationMenuBar.ApplicationExitRequestedEvent.class)
    public void onApplicationExitRequested(ApplicationMenuBar.ApplicationExitRequestedEvent event) {
        System.out.println("🖥️ APPLICATION CONTROLLER: Application exit requested");

        // TODO: Implement clean shutdown
        // - Save user preferences
        // - Close database connections
        // - Stop playback services
        // - Exit gracefully

        System.exit(0);
    }

    /**
     * Handle theme refresh requests.
     */
    @EventListener(ApplicationMenuBar.ThemeRefreshRequestedEvent.class)
    public void onThemeRefreshRequested(ApplicationMenuBar.ThemeRefreshRequestedEvent event) {
        System.out.println("🖥️ APPLICATION CONTROLLER: Theme refresh requested");

        // Refresh theme across all reactive components
        applicationView.refreshThemeColors();
    }

    /**
     * Handle playlist view toggle requests.
     */
    @EventListener(ApplicationMenuBar.ViewTogglePlaylistEvent.class)
    public void onViewTogglePlaylist(ApplicationMenuBar.ViewTogglePlaylistEvent event) {
        System.out.println("🖥️ APPLICATION CONTROLLER: Toggle playlist view");

        // TODO: Implement playlist sidebar show/hide logic
    }

    /**
     * Handle window minimize requests.
     */
    @EventListener(ApplicationMenuBar.WindowMinimizeRequestedEvent.class)
    public void onWindowMinimizeRequested(ApplicationMenuBar.WindowMinimizeRequestedEvent event) {
        System.out.println("🖥️ APPLICATION CONTROLLER: Window minimize requested");

        // Minimize the application window
        applicationView.setState(java.awt.Frame.ICONIFIED);
    }

    /**
     * Handle about dialog requests.
     */
    @EventListener(ApplicationMenuBar.AboutDialogRequestedEvent.class)
    public void onAboutDialogRequested(ApplicationMenuBar.AboutDialogRequestedEvent event) {
        System.out.println("🖥️ APPLICATION CONTROLLER: About dialog requested");

        // TODO: Show about dialog
        javax.swing.JOptionPane.showMessageDialog(applicationView,
            "Tunes4J - Reactive Media Player\nBuilt with DDD and Observer Pattern",
            "About Tunes4J",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Handle system info requests.
     */
    @EventListener(ApplicationMenuBar.SystemInfoRequestedEvent.class)
    public void onSystemInfoRequested(ApplicationMenuBar.SystemInfoRequestedEvent event) {
        System.out.println("🖥️ APPLICATION CONTROLLER: System info requested");

        // TODO: Show system information dialog
        String info = "Java Version: " + System.getProperty("java.version") + "\n" +
                     "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "\n" +
                     "Architecture: " + System.getProperty("os.arch");

        javax.swing.JOptionPane.showMessageDialog(applicationView,
            info,
            "System Information",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Handle application closing events from the main window.
     */
    @EventListener(ApplicationView.ApplicationClosingEvent.class)
    public void onApplicationClosing(ApplicationView.ApplicationClosingEvent event) {
        System.out.println("🖥️ APPLICATION CONTROLLER: Application closing");

        // Perform any final cleanup if needed
        // The ApplicationView already handles System.exit(0)
    }

    /**
     * Public API to programmatically refresh themes across the application.
     */
    public void refreshApplicationTheme() {
        applicationView.refreshThemeColors();
    }

    /**
     * Public API to minimize the application.
     */
    public void minimizeApplication() {
        applicationView.setState(java.awt.Frame.ICONIFIED);
    }
}
