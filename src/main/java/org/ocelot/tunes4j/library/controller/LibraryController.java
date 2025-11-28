package org.ocelot.tunes4j.library.controller;

import java.util.List;

import org.ocelot.tunes4j.library.view.SongListView;
import org.ocelot.tunes4j.library.view.LibrarySearchView;
import org.ocelot.tunes4j.library.service.LibraryService;
import org.ocelot.tunes4j.library.model.LibrarySong;
import org.ocelot.tunes4j.library.event.LibraryUserInteractionEvent;
import org.ocelot.tunes4j.audio.event.AudioPlaybackStateEvent;
import org.ocelot.tunes4j.event.SongInfoEvent;
import org.ocelot.tunes4j.library.BaseReactiveView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

/**
 * LibraryController - MV(C) Coordinator for Library Bounded Context.
 *
 * RESPONSIBILITY: Orchestrate Library bounded context operations.
 * Handles communication between library views and domain services.
 * Implements MVC pattern: Controller coordinates View and Service/Model layers.
 *
 * Reactive Extensions:
 * - @EventListener: Listens to inter-context events
 * - Event Publishing: Publishes library domain events
 * - Coordinator Pattern: Manages reactive component interactions
 */
@Controller
public class LibraryController extends BaseReactiveView {

    private final LibraryService libraryService;
    private final SongListView songListView;
    private final LibrarySearchView librarySearchView;

    @Autowired
    public LibraryController(LibraryService libraryService,
                            SongListView songListView,
                            LibrarySearchView librarySearchView) {
        this.libraryService = libraryService;
        this.songListView = songListView;
        this.librarySearchView = librarySearchView;

        initializeLibraryContext();
        wireComponents();
    }

    /**
     * Initialize the Library bounded context.
     * Load songs and prepare reactive components.
     */
    private void initializeLibraryContext() {
        System.out.println("📚 LIBRARY CONTROLLER: Initializing library bounded context...");

        // Load initial song data into views
        refreshSongList();

        // Wire search view to song list view
        librarySearchView.setSongListView(songListView);

        System.out.println("📚 LIBRARY CONTROLLER: Initialized with " + libraryService.getLibrarySize() + " songs");
    }

    /**
     * Wire reactive components together within the Library context.
     * Set up event listeners and data binding.
     */
    private void wireComponents() {
        // Song list view is already configured to publish events
        // Song display view listens for selection events
        // Search view controls table filtering
        System.out.println("📚 LIBRARY CONTROLLER: Reactive components wired together");
    }

    /**
     * Refresh the song list with current library data.
     */
    public void refreshSongList() {
        List<LibrarySong> songs = libraryService.getAllSongs();
        songListView.setSongData(songs);
        System.out.println("📚 LIBRARY CONTROLLER: Refreshed song list with " + songs.size() + " songs");
    }

    /**
     * REACTIVE: Listen for audio playback state changes.
     * Forward relevant events to Library views for UI updates.
     */
    @EventListener
    public void onAudioPlaybackStateChanged(AudioPlaybackStateEvent event) {
        // Forward event to song list and display views
        System.out.println("📚 LIBRARY CONTROLLER: Received audio state - " + event.getState());
        // SongListView and SongDisplayView have their own @EventListener methods
        // that will handle this directly if needed
    }

    /**
     * REACTIVE: Listen for song info updates (metadata/artwork changes).
     * Update library displays when song information changes.
     */
    @EventListener
    public void onSongInfoUpdated(SongInfoEvent event) {
        System.out.println("📚 LIBRARY CONTROLLER: Received song info update - refreshing views");
        // Refresh song list to show any updated metadata
        songListView.getTable().repaint();
    }

    /**
     * Handle library user interactions from the UI.
     * Could be called from views or external controllers.
     */
    public void handleLibraryAction(LibraryUserInteractionEvent.Action action,
                                   List<LibrarySong> selectedSongs) {
        switch (action) {
            case ADD_TO_LIBRARY:
                libraryService.addSongsToLibrary(selectedSongs);
                refreshSongList();
                break;

            case REMOVE_FROM_LIBRARY:
                List<String> songIds = selectedSongs.stream()
                    .map(LibrarySong::getId)
                    .collect(java.util.stream.Collectors.toList());
                libraryService.removeSongsFromLibrary(songIds);
                refreshSongList();
                break;

            case MARK_FAVORITE:
                for (LibrarySong song : selectedSongs) {
                    libraryService.markSongAsFavorite(song.getId());
                }
                refreshSongList();
                break;

            default:
                System.out.println("📚 LIBRARY CONTROLLER: Unhandled action - " + action);
                break;
        }
    }

    /**
     * Search the library and update the display.
     */
    public void searchLibrary(String query) {
        librarySearchView.search(query);
        System.out.println("📚 LIBRARY CONTROLLER: Applied search filter - '" + query + "'");
    }

    /**
     * Clear the search filter.
     */
    public void clearSearch() {
        librarySearchView.clearSearch();
        System.out.println("📚 LIBRARY CONTROLLER: Cleared search filter");
    }

    /**
     * Get the number of songs in the library.
     */
    public int getLibrarySize() {
        return libraryService.getLibrarySize();
    }

    // Provided API methods for views and external controllers

    public LibraryService getLibraryService() {
        return libraryService;
    }

    public SongListView getSongListView() {
        return songListView;
    }

    public LibrarySearchView getLibrarySearchView() {
        return librarySearchView;
    }
}
