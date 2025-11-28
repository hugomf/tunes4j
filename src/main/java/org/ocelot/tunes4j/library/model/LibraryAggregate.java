package org.ocelot.tunes4j.library.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Library Aggregate - Root domain entity for the music library.
 *
 * Encapsulates the entire music library with search indexing and metadata management.
 * This aggregate ensures library consistency and provides domain-specific methods.
 */
public class LibraryAggregate {

    private final String libraryId;
    private final Map<String, LibrarySong> songs;
    private final SearchIndex searchIndex;

    public LibraryAggregate(String libraryId) {
        this.libraryId = libraryId;
        this.songs = new ConcurrentHashMap<>();
        this.searchIndex = new SearchIndex();
    }

    /**
     * Add a song to the library and update search index.
     */
    public void addSong(LibrarySong song) {
        songs.put(song.getId(), song);
        searchIndex.addSongToIndex(song);
    }

    /**
     * Remove a song from the library and update search index.
     */
    public void removeSong(String songId) {
        LibrarySong removed = songs.remove(songId);
        if (removed != null) {
            searchIndex.removeSongFromIndex(removed);
        }
    }

    /**
     * Search songs using the indexing system.
     */
    public SearchResult searchSongs(String query, SearchCriteria criteria) {
        return searchIndex.search(query, criteria);
    }

    /**
     * Get song by ID.
     */
    public LibrarySong getSong(String songId) {
        return songs.get(songId);
    }

    public String getLibraryId() { return libraryId; }
    public Map<String, LibrarySong> getAllSongs() { return songs; }
    public SearchIndex getSearchIndex() { return searchIndex; }

    // Inner classes for search functionality
    public static class LibrarySong {
        private final String id;
        private final String title;
        private final String artist;
        private final String album;
        private final String genre;
        private final long duration;

        public LibrarySong(String id, String title, String artist, String album,
                          String genre, long duration) {
            this.id = id;
            this.title = title;
            this.artist = artist;
            this.album = album;
            this.genre = genre;
            this.duration = duration;
        }

        // Getters
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        public String getAlbum() { return album; }
        public String getGenre() { return genre; }
        public long getDuration() { return duration; }
    }

    public static class SearchCriteria {
        private final boolean includeTitle = true;
        private final boolean includeArtist = true;
        private final boolean includeAlbum = true;
        private final boolean includeGenre = true;
        private final boolean fuzzySearch = true;

        // Additional criteria would be added here
    }

    public static class SearchResult {
        private final java.util.List<LibrarySong> results;
        private final long totalCount;
        private final long searchTimeMs;

        public SearchResult(java.util.List<LibrarySong> results, long totalCount, long searchTimeMs) {
            this.results = results;
            this.totalCount = totalCount;
            this.searchTimeMs = searchTimeMs;
        }

        public java.util.List<LibrarySong> getResults() { return results; }
        public long getTotalCount() { return totalCount; }
        public long getSearchTimeMs() { return searchTimeMs; }
    }

    public static class SearchIndex {
        // Placeholder for search indexing functionality
        // Would implement inverted index, tokenization, etc.

        public void addSongToIndex(LibrarySong song) {
            // Index the song for fast search
            System.out.println("Library Search: Indexed song - " + song.getTitle());
        }

        public void removeSongFromIndex(LibrarySong song) {
            // Remove song from search index
            System.out.println("Library Search: Removed song from index - " + song.getTitle());
        }

        public SearchResult search(String query, SearchCriteria criteria) {
            // Perform search with criteria
            System.out.println("Library Search: Searching for: " + query);
            // Placeholder implementation
            return new SearchResult(java.util.Collections.emptyList(), 0, 0);
        }
    }
}
