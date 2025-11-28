package org.ocelot.tunes4j.library.model;

import java.time.Duration;

/**
 * LibrarySong - Domain entity for Library bounded context.
 *
 * RESPONSIBILITY: Pure business logic for song entities within the song library.
 * Domain invariants and business rules related to song management in library context.
 *
 * Note: This is NOT for persistence (@Entity annotations go in DTO layer).
 * Note: This is NOT for audio playback (audio context handles that).
 */
public class LibrarySong {

    private final String id;
    private final String title;
    private final String artist;
    private final String album;
    private final String genre;
    private final Duration duration;
    private final String filePath;
    private final String fileName;

    // Business metadata for library organization
    private final String year;
    private final String trackNumber;
    private final String author;

    // Library-specific business state
    private boolean inLibrary;
    private boolean favorite;

    public LibrarySong(String id, String title, String artist, String album,
                       String genre, Duration duration, String filePath, String fileName) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.duration = duration;
        this.filePath = filePath;
        this.fileName = fileName;
        this.year = null;
        this.trackNumber = null;
        this.author = null;
        this.inLibrary = true;
        this.favorite = false;
    }

    // Full constructor for library management
    public LibrarySong(String id, String title, String artist, String album,
                       String genre, Duration duration, String filePath, String fileName,
                       String year, String trackNumber, String author,
                       boolean inLibrary, boolean favorite) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.duration = duration;
        this.filePath = filePath;
        this.fileName = fileName;
        this.year = year;
        this.trackNumber = trackNumber;
        this.author = author;
        this.inLibrary = inLibrary;
        this.favorite = favorite;
    }

    // Domain business rules and invariants

    /**
     * Business rule: A song must be identifiable and have basic metadata.
     */
    public boolean isValidForLibrary() {
        return id != null && !id.trim().isEmpty() &&
               title != null && !title.trim().isEmpty() &&
               artist != null && !artist.trim().isEmpty();
    }

    /**
     * Business rule: Can play song (forwards to Audio bounded context).
     * Library context manages library state, but playback is Audio responsibility.
     */
    public boolean canPlay() {
        return inLibrary && filePath != null && fileName != null;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getGenre() { return genre; }
    public Duration getDuration() { return duration; }
    public String getFilePath() { return filePath; }
    public String getFileName() { return fileName; }
    public String getYear() { return year; }
    public String getTrackNumber() { return trackNumber; }
    public String getAuthor() { return author; }
    public boolean isInLibrary() { return inLibrary; }
    public boolean isFavorite() { return favorite; }

    // Bean property mapping for UI integration
    public String getPath() { return filePath; }

    @Override
    public String toString() {
        return String.format("LibrarySong{id='%s', title='%s', artist='%s', album='%s'}",
                id, title, artist, album);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibrarySong that = (LibrarySong) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
