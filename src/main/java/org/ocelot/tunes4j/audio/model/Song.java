package org.ocelot.tunes4j.audio.model;

import java.time.Duration;
import java.util.Objects;

/**
 * Song Domain Entity - Pure Business Logic (DDD Domain Layer).
 *
 * CONTAINS: Business rules, validation, domain behaviors
 * EXCLUDES: JPA annotations, technical concerns, infrastructure issues
 *
 * This is a pure domain object used for business logic.
 * Infrastructure concerns (persistence, etc.) belong in other layers.
 */
public class Song {

    private final String id;
    private final String title;
    private final String artist;
    private final String album;
    private final String genre;
    private final Duration duration;

    public Song(String id, String title, String artist, String album, String genre, Duration duration) {
        this.id = validateId(id);
        this.title = validateTitle(title);
        this.artist = artist; // Optional field
        this.album = album;  // Optional field
        this.genre = genre;  // Optional field
        this.duration = duration != null ? duration : Duration.ZERO;
    }

    // Business Rules Validation
    private String validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw newIllegalArgumentException("Song ID cannot be null or empty");
        }
        return id.trim();
    }

    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw newIllegalArgumentException("Song title cannot be null or empty");
        }
        return title.trim();
    }

    private static IllegalArgumentException newIllegalArgumentException(String message) {
        return new IllegalArgumentException(message);
    }

    // Domain Behaviors / Business Methods
    public boolean isClassical() {
        return "Classical".equalsIgnoreCase(genre);
    }

    public boolean isByArtist(String artistName) {
        return artistName != null && artistName.equalsIgnoreCase(artist);
    }

    public boolean hasDuration() {
        return duration != null && !duration.isZero();
    }

    public String getFormattedTitle() {
        return title + (artist != null ? " - " + artist : "");
    }

    // Immutable Getters (Value Objects)
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getGenre() { return genre; }
    public Duration getDuration() { return duration; }

    // Domain Equality (by business identity)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return Objects.equals(id, song.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Song{id='%s', title='%s', artist='%s'}",
                           id, title, artist);
    }

    // Factory Methods (Domain Creation Patterns)
    public static Song create(String id, String title) {
        return new Song(id, title, null, null, null, null);
    }

    public static Song createWithArtist(String id, String title, String artist) {
        return new Song(id, title, artist, null, null, null);
    }

    public static Song createComplete(String id, String title, String artist,
                                     String album, String genre, Duration duration) {
        return new Song(id, title, artist, album, genre, duration);
    }
}
