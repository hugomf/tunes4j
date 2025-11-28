package org.ocelot.tunes4j.playlist.service;

import org.ocelot.tunes4j.playlist.model.Playlist;
import org.ocelot.tunes4j.playlist.adapter.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Playlist Service - Domain Service Layer for Playlist Bounded Context.
 *
 * Encapsulates business logic for playlist management operations.
 * Coordinates between domain models, repositories, and publishes domain events.
 */
@Service
@Transactional
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository, ApplicationEventPublisher eventPublisher) {
        this.playlistRepository = playlistRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Create a new playlist with the given name.
     */
    public Playlist createPlaylist(String playlistName) {
        Playlist newPlaylist = new Playlist(java.util.UUID.randomUUID().toString(), playlistName);
        return playlistRepository.save(newPlaylist);
    }

    /**
     * Find a playlist by its unique identifier.
     */
    public Playlist findPlaylistById(String playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found: " + playlistId));
    }

    /**
     * Get all playlists.
     */
    public List<Playlist> findAllPlaylists() {
        return playlistRepository.findAll();
    }

    /**
     * Find playlists by name (partial match).
     */
    public List<Playlist> findPlaylistsByName(String name) {
        return playlistRepository.findByNameContaining(name);
    }

    /**
     * Delete a playlist permanently.
     */
    public void deletePlaylist(String playlistId) {
        if (!playlistRepository.existsById(playlistId)) {
            throw new IllegalArgumentException("Playlist not found: " + playlistId);
        }

        playlistRepository.deleteById(playlistId);
    }

    /**
     * Get playlist library statistics.
     */
    public PlaylistLibraryStats getPlaylistStats() {
        List<Playlist> allPlaylists = playlistRepository.findAll();

        long totalPlaylists = allPlaylists.size();
        long totalSongs = allPlaylists.stream()
            .mapToLong(Playlist::getSongCount)
            .sum();

        return new PlaylistLibraryStats(
            (int) totalPlaylists,
            (int) totalSongs,
            (int) totalPlaylists, // editable playlists (all for now)
            0                     // system playlists (none for now)
        );
    }

    /**
     * Playlist library statistics DTO.
     */
    public static class PlaylistLibraryStats {
        private final int playlistCount;
        private final int totalSongsInPlaylists;
        private final long editablePlaylists;
        private final long systemPlaylists;

        public PlaylistLibraryStats(int playlistCount, int totalSongsInPlaylists,
                                   long editablePlaylists, long systemPlaylists) {
            this.playlistCount = playlistCount;
            this.totalSongsInPlaylists = totalSongsInPlaylists;
            this.editablePlaylists = editablePlaylists;
            this.systemPlaylists = systemPlaylists;
        }

        // Getters
        public int getPlaylistCount() { return playlistCount; }
        public int getTotalSongsInPlaylists() { return totalSongsInPlaylists; }
        public long getEditablePlaylists() { return editablePlaylists; }
        public long getSystemPlaylists() { return systemPlaylists; }
    }
}
