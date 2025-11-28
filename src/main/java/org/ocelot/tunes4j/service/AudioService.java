package org.ocelot.tunes4j.service;

import java.util.List;

import org.ocelot.tunes4j.dao.SongRepository;
import org.ocelot.tunes4j.dto.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Audio Service - Domain Service Layer for Song/Media Operations.
 *
 * Key responsibilities:
 * - Source Playlist: Music from database/library
 * - Business logic for audio operations
 * - Search functionality
 * - Play history management
 * - Coordinate between controllers and repositories
 *
 * Acts as the domain layer gateway for all audio-related operations.
 */
@Service
@Transactional(readOnly = true)
public class AudioService {

    private final SongRepository songRepository;

    @Autowired
    public AudioService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    /**
     * Get all songs from the music library.
     * This forms the "Music" playlist in the source list.
     */
    public List<Song> getAllSongs() {
        return (List<Song>) songRepository.findAll();
    }

    /**
     * Get a song by ID for reactive playback.
     */
    public Song getSongById(String songId) {
        return songRepository.findById(songId)
            .orElseThrow(() -> new RuntimeException("Song not found: " + songId));
    }

    /**
     * Search songs by artist, title, album, or genre.
     */
    public List<Song> searchSongs(String query) {
        // Simple search implementation - can be enhanced with full-text search
        // Or delegate to repository methods

        // For now, basic implementation that searches across multiple fields
        final String searchQuery = query.toLowerCase();
        return getAllSongs().stream()
            .filter(song -> song.getTitle().toLowerCase().contains(searchQuery) ||
                          song.getArtist().toLowerCase().contains(searchQuery) ||
                          song.getAlbum().toLowerCase().contains(searchQuery) ||
                          (song.getGenre() != null && song.getGenre().toLowerCase().contains(searchQuery)))
            .toList();
    }

    /**
     * Get songs by playlist ID (when playlist integration is complete).
     */
    public List<Song> getSongsByPlaylist(String playlistId) {
        // TODO: Implement when playlist-song relationship is established
        // This will involve playlist_song junction table queries
        return List.of(); // Placeholder
    }

    /**
     * Get recently played songs (for playback history feature).
     */
    public List<Song> getRecentlyPlayed() {
        // TODO: Implement when play history is tracked
        // Could use a separate table or field in Song entity
        return getAllSongs().subList(Math.max(0, getAllSongs().size() - 10), getAllSongs().size());
    }

    /**
     * Add a song to the library (when importing files).
     */
    @Transactional
    public Song addSong(Song song) {
        // TODO: Add validation and duplicate checking logic
        return songRepository.save(song);
    }

    /**
     * Remove a song from the library.
     */
    @Transactional
    public void removeSong(String songId) {
        songRepository.deleteById(songId);
    }

    /**
     * Update song metadata (tags, etc).
     */
    @Transactional
    public Song updateSongMetadata(String songId, Song updatedSong) {
        Song existingSong = getSongById(songId);
        // Apply updates to title, artist, album, etc.
        existingSong.setTitle(updatedSong.getTitle());
        existingSong.setArtist(updatedSong.getArtist());
        existingSong.setAlbum(updatedSong.getAlbum());
        existingSong.setGenre(updatedSong.getGenre());
        // Update other fields as they become available in DTO

        return songRepository.save(existingSong);
    }

    /**
     * Get songs by artist.
     */
    public List<Song> getSongsByArtist(String artist) {
        return getAllSongs().stream()
            .filter(song -> artist.equals(song.getArtist()))
            .toList();
    }

    /**
     * Get songs by album.
     */
    public List<Song> getSongsByAlbum(String album) {
        return getAllSongs().stream()
            .filter(song -> album.equals(song.getAlbum()))
            .toList();
    }

    /**
     * Get albums for a specific artist.
     */
    public List<String> getAlbumsByArtist(String artist) {
        return getAllSongs().stream()
            .filter(song -> artist.equals(song.getArtist()))
            .map(Song::getAlbum)
            .distinct()
            .sorted()
            .toList();
    }

    /**
     * Get all artists.
     */
    public List<String> getAllArtists() {
        return getAllSongs().stream()
            .map(Song::getArtist)
            .filter(artist -> artist != null && !artist.isEmpty())
            .distinct()
            .sorted()
            .toList();
    }

    /**
     * Get all genres.
     */
    public List<String> getAllGenres() {
        return getAllSongs().stream()
            .map(Song::getGenre)
            .filter(genre -> genre != null && !genre.isEmpty())
            .distinct()
            .sorted()
            .toList();
    }

    /**
     * Get library statistics.
     */
    public AudioLibraryStats getLibraryStats() {
        List<Song> allSongs = getAllSongs();
        return new AudioLibraryStats(
            allSongs.size(),
            0L, // File size not available in current DTO
            0L, // Duration not available in current DTO
            getAllArtists().size(),
            allSongs.stream().filter(song -> song.getAlbum() != null).map(Song::getAlbum).distinct().count()
        );
    }

    /**
     * Audio library statistics DTO.
     */
    public static class AudioLibraryStats {
        private final int songCount;
        private final long totalSize;
        private final long totalDuration;
        private final int artistCount;
        private final long albumCount;

        public AudioLibraryStats(int songCount, long totalSize, long totalDuration, int artistCount, long albumCount) {
            this.songCount = songCount;
            this.totalSize = totalSize;
            this.totalDuration = totalDuration;
            this.artistCount = artistCount;
            this.albumCount = albumCount;
        }

        // Getters
        public int getSongCount() { return songCount; }
        public long getTotalSize() { return totalSize; }
        public long getTotalDuration() { return totalDuration; }
        public int getArtistCount() { return artistCount; }
        public long getAlbumCount() { return albumCount; }
    }
}
