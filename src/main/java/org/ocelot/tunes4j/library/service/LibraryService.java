package org.ocelot.tunes4j.library.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.ocelot.tunes4j.library.model.LibrarySong;
import org.ocelot.tunes4j.dao.SongRepository;
import org.ocelot.tunes4j.dto.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * LibraryService - Domain Service for Library Bounded Context.
 *
 * RESPONSIBILITY: Handle song library business logic and domain operations.
 * Manages song entities and their state within the library domain.
 *
 * This service owns no GUI logic - it provides clean domain operations
 * for views and controllers in the Library bounded context.
 */
@Service
public class LibraryService {

    private final SongRepository songRepository;

    // In-memory library for now - will use repository later
    private final List<LibrarySong> librarySongs;

    @Autowired
    public LibraryService(SongRepository songRepository) {
        this.songRepository = songRepository;
        this.librarySongs = new ArrayList<>();

        // Load initial library data from repository
        loadLibraryFromRepository();
    }

    /**
     * Load all songs from the repository into the library domain model.
     */
    private void loadLibraryFromRepository() {
        try {
            Iterable<Song> songs = songRepository.findAll();
            for (Song song : songs) {
                LibrarySong libSong = convertToLibrarySong(song);
                librarySongs.add(libSong);
            }
            System.out.println("📚 LIBRARY SERVICE: Loaded " + librarySongs.size() + " songs from repository");
        } catch (Exception e) {
            System.err.println("📚 LIBRARY SERVICE: Failed to load songs from repository: " + e.getMessage());
        }
    }

    /**
     * DOMAIN OPERATION: Add multiple songs to the library.
     */
    public void addSongsToLibrary(List<LibrarySong> newSongs) {
        List<LibrarySong> validSongs = newSongs.stream()
            .filter(LibrarySong::isValidForLibrary)
            .collect(Collectors.toList());

        for (LibrarySong song : validSongs) {
            if (!song.isInLibrary()) {
                // Mark as in library and add
                LibrarySong inLibrarySong = new LibrarySong(
                    song.getId(), song.getTitle(), song.getArtist(), song.getAlbum(),
                    song.getGenre(), song.getDuration(), song.getFilePath(), song.getFileName(),
                    song.getYear(), song.getTrackNumber(), song.getAuthor(),
                    true, false // inLibrary = true, favorite = false
                );
                librarySongs.add(inLibrarySong);
            }
        }

        System.out.println("📚 LIBRARY SERVICE: Added " + validSongs.size() + " songs to library");
    }

    /**
     * DOMAIN OPERATION: Remove songs from library.
     */
    public void removeSongsFromLibrary(List<String> songIds) {
        librarySongs.removeIf(song -> songIds.contains(song.getId()));

        // Also remove from repository
        for (String songId : songIds) {
            try {
                Optional<Song> optionalSong = songRepository.findById(songId);
                if (optionalSong.isPresent()) {
                    songRepository.delete(optionalSong.get());
                }
            } catch (Exception e) {
                System.err.println("📚 LIBRARY SERVICE: Failed to remove song " + songId + " from repository: " + e.getMessage());
            }
        }

        System.out.println("📚 LIBRARY SERVICE: Removed " + songIds.size() + " songs from library");
    }

    /**
     * DOMAIN OPERATION: Mark song as favorite.
     */
    public void markSongAsFavorite(String songId) {
        findSongById(songId).ifPresent(song -> {
            // Create new instance with favorite flag set
            LibrarySong updatedSong = new LibrarySong(
                song.getId(), song.getTitle(), song.getArtist(), song.getAlbum(),
                song.getGenre(), song.getDuration(), song.getFilePath(), song.getFileName(),
                song.getYear(), song.getTrackNumber(), song.getAuthor(),
                song.isInLibrary(), true
            );
            replaceSongInLibrary(song, updatedSong);
        });
    }

    /**
     * DOMAIN OPERATION: Search songs by query.
     */
    public List<LibrarySong> searchSongs(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(librarySongs);
        }

        String lowercaseQuery = query.toLowerCase();
        return librarySongs.stream()
            .filter(song -> matchesQuery(song, lowercaseQuery))
            .collect(Collectors.toList());
    }

    /**
     * DOMAIN OPERATION: Get all songs in library.
     */
    public List<LibrarySong> getAllSongs() {
        return new ArrayList<>(librarySongs);
    }

    /**
     * DOMAIN OPERATION: Get favorite songs.
     */
    public List<LibrarySong> getFavoriteSongs() {
        return librarySongs.stream()
            .filter(LibrarySong::isFavorite)
            .collect(Collectors.toList());
    }

    /**
     * DOMAIN OPERATION: Get song by ID.
     */
    public Optional<LibrarySong> findSongById(String songId) {
        return librarySongs.stream()
            .filter(song -> song.getId().equals(songId))
            .findFirst();
    }

    /**
     * DOMAIN OPERATION: Get songs by artist.
     */
    public List<LibrarySong> getSongsByArtist(String artist) {
        return librarySongs.stream()
            .filter(song -> artist.equalsIgnoreCase(song.getArtist()))
            .collect(Collectors.toList());
    }

    /**
     * DOMAIN OPERATION: Check if song can be played.
     */
    public boolean isSongPlayable(String songId) {
        return findSongById(songId)
            .map(LibrarySong::canPlay)
            .orElse(false);
    }

    // Private helper methods

    private boolean matchesQuery(LibrarySong song, String query) {
        return containsIgnoreCase(song.getTitle(), query) ||
               containsIgnoreCase(song.getArtist(), query) ||
               containsIgnoreCase(song.getAlbum(), query) ||
               containsIgnoreCase(song.getGenre(), query);
    }

    private boolean containsIgnoreCase(String text, String query) {
        return text != null && text.toLowerCase().contains(query);
    }

    private void replaceSongInLibrary(LibrarySong oldSong, LibrarySong newSong) {
        int index = librarySongs.indexOf(oldSong);
        if (index >= 0) {
            librarySongs.set(index, newSong);
        }
    }

    /**
     * Convert DTO Song to domain LibrarySong.
     */
    private LibrarySong convertToLibrarySong(Song song) {
        return new LibrarySong(
            song.getId().toString(),
            song.getTitle() != null ? song.getTitle() : "Unknown Title",
            song.getArtist() != null ? song.getArtist() : "Unknown Artist",
            song.getAlbum() != null ? song.getAlbum() : "",
            song.getGenre() != null ? song.getGenre() : "",
            Duration.ZERO, // Duration not available in Song DTO - set to zero for now
            song.getPath(),
            song.getFileName(),
            song.getYear(),
            song.getTrackNumber(),
            song.getAuthor(),
            true, // inLibrary
            false // favorite
        );
    }

    /**
     * Convert string duration representation to Duration.
     * Simple implementation - can be enhanced.
     */
    private Duration stringToDuration(String durationStr) {
        if (durationStr == null || durationStr.trim().isEmpty()) {
            return Duration.ZERO;
        }
        try {
            // Try parsing as seconds first
            long seconds = Long.parseLong(durationStr.trim());
            return Duration.ofSeconds(seconds);
        } catch (NumberFormatException e) {
            return Duration.ZERO;
        }
    }

    /**
     * Get the current size of the library.
     */
    public int getLibrarySize() {
        return librarySongs.size();
    }
}
