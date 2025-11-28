package org.ocelot.tunes4j.audio.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain entity representing an Album in the audio bounded context.
 * An aggregate that contains songs and metadata about the album.
 */
public class Album {

    private final String id;
    private String title;
    private String artist;
    private String year;
    private final List<Song> songs;

    // Domain invariants
    public static final int MAX_TITLE_LENGTH = 200;
    public static final int MAX_ARTIST_LENGTH = 100;

    /**
     * Create a new Album entity.
     *
     * @param id unique identifier
     * @param title album title (required)
     * @param artist album artist (required)
     * @param year release year (optional)
     */
    public Album(String id, String title, String artist, String year) {
        this.id = Objects.requireNonNull(id, "Album ID cannot be null");

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Album title cannot be null or empty");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Album title exceeds maximum length: " + MAX_TITLE_LENGTH);
        }
        this.title = title.trim();

        if (artist == null || artist.trim().isEmpty()) {
            throw new IllegalArgumentException("Album artist cannot be null or empty");
        }
        if (artist.length() > MAX_ARTIST_LENGTH) {
            throw new IllegalArgumentException("Album artist exceeds maximum length: " + MAX_ARTIST_LENGTH);
        }
        this.artist = artist.trim();

        this.year = year;
        this.songs = new ArrayList<>();
    }

    /**
     * Create a new Album entity without year.
     */
    public Album(String id, String title, String artist) {
        this(id, title, artist, null);
    }

    /**
     * Add a song to this album.
     *
     * @param song the song to add
     */
    public void addSong(Song song) {
        Objects.requireNonNull(song, "Song cannot be null");
        if (!songs.contains(song)) {
            songs.add(song);
        }
    }

    /**
     * Remove a song from this album.
     *
     * @param song the song to remove
     */
    public void removeSong(Song song) {
        songs.remove(song);
    }

    /**
     * Get the number of songs in this album.
     *
     * @return song count
     */
    public int getSongCount() {
        return songs.size();
    }

    /**
     * Check if album has any songs.
     *
     * @return true if album has songs
     */
    public boolean hasSongs() {
        return !songs.isEmpty();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getYear() { return year; }

    /**
     * Get unmodifiable list of songs in this album.
     */
    public List<Song> getSongs() {
        return Collections.unmodifiableList(songs);
    }

    // Setters with business rules
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Album title cannot be null or empty");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Album title exceeds maximum length: " + MAX_TITLE_LENGTH);
        }
        this.title = title.trim();
    }

    public void setArtist(String artist) {
        if (artist == null || artist.trim().isEmpty()) {
            throw new IllegalArgumentException("Album artist cannot be null or empty");
        }
        if (artist.length() > MAX_ARTIST_LENGTH) {
            throw new IllegalArgumentException("Album artist exceeds maximum length: " + MAX_ARTIST_LENGTH);
        }
        this.artist = artist.trim();
    }

    public void setYear(String year) { this.year = year; }

    /**
     * Domain equality based on title and artist.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Album)) return false;
        Album album = (Album) o;
        return Objects.equals(title, album.title) &&
               Objects.equals(artist, album.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, artist);
    }

    @Override
    public String toString() {
        return String.format("Album{id='%s', title='%s', artist='%s', year='%s', songs=%d}",
                           id, title, artist, year != null ? year : "Unknown", songs.size());
    }
}
