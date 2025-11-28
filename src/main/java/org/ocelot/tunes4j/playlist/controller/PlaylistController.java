package org.ocelot.tunes4j.playlist.controller;

import org.ocelot.tunes4j.application.controller.BaseController;
import org.ocelot.tunes4j.event.PlaylistSelectedEvent;
import org.ocelot.tunes4j.playlist.model.Playlist;
import org.ocelot.tunes4j.playlist.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

/**
 * Playlist Controller - Reactive Coordinator for Playlist Bounded Context.
 * Handles event-driven communication between playlist views, services, and adapters.
 * Implements the Observer Pattern for cross-component coordination.
 */
@Controller
public class PlaylistController extends BaseController {

    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    /**
     * Handle playlist selection events (e.g., from playlist view).
     * This implements the reactive flow: PlaylistView → PlaylistController → Domain Services
     */
    @EventListener(PlaylistSelectedEvent.class)
    public void onPlaylistSelected(PlaylistSelectedEvent event) {
        if (event.getSource() == this) {
            return; // Avoid self-handling
        }

        Playlist selectedPlaylist = event.getSelectedPlaylist();
        System.out.println("🎵 PLAYLIST CONTROLLER: Processing playlist selection - " +
                           selectedPlaylist.getName() + " (" + selectedPlaylist.getSongCount() + " songs)");

        // Domain logic: Load and publish playlist content
        // The playlist is already selected - this event triggers other components
        // to display the playlist contents in UI components
    }

    /**
     * Public API for creating playlists (called by views through events).
     */
    public Playlist createPlaylist(String playlistName) {
        System.out.println("🎵 PLAYLIST CONTROLLER: Creating playlist - " + playlistName);

        // Delegate to domain service for business logic
        Playlist createdPlaylist = playlistService.createPlaylist(playlistName);

        System.out.println("🎵 PLAYLIST CONTROLLER: Successfully created playlist - " + createdPlaylist.getId());
        return createdPlaylist;
    }

    /**
     * Public API for deleting playlists (called by views through events).
     */
    public void deletePlaylist(String playlistId) {
        System.out.println("🎵 PLAYLIST CONTROLLER: Deleting playlist - " + playlistId);

        try {
            playlistService.deletePlaylist(playlistId);
            System.out.println("🎵 PLAYLIST CONTROLLER: Successfully deleted playlist - " + playlistId);
        } catch (IllegalArgumentException e) {
            System.err.println("🎵 PLAYLIST CONTROLLER: Failed to delete playlist - " + e.getMessage());
            throw e; // Re-throw for error handling by calling code
        }
    }

    /**
     * Public API for adding songs to playlists.
     */
    public Playlist addSongToPlaylist(String playlistId, String songId) {
        System.out.println("🎵 PLAYLIST CONTROLLER: Adding song " + songId + " to playlist " + playlistId);

        // TODO: This needs access to AudioSongRepository to get the Song domain object
        // For now, this is a placeholder - implementation requires domain object resolution

        // Example of what it should do:
        // Song song = audioSongRepository.findById(songId);
        // Playlist updatedPlaylist = playlistService.addSongToPlaylist(playlistId, song);
        // return updatedPlaylist;

        throw new UnsupportedOperationException("Song addition not yet implemented - requires audio context integration");
    }

    /**
     * Public API for removing songs from playlists.
     */
    public Playlist removeSongFromPlaylist(String playlistId, String songId) {
        System.out.println("🎵 PLAYLIST CONTROLLER: Removing song " + songId + " from playlist " + playlistId);

        // TODO: This needs access to AudioSongRepository to get the Song domain object
        // For now, this is a placeholder - implementation requires domain object resolution

        // Example of what it should do:
        // Song song = audioSongRepository.findById(songId);
        // Playlist updatedPlaylist = playlistService.removeSongFromPlaylist(playlistId, song);
        // return updatedPlaylist;

        throw new UnsupportedOperationException("Song removal not yet implemented - requires audio context integration");
    }
}
