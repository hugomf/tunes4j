package org.ocelot.tunes4j.playlist.model;

import org.ocelot.tunes4j.audio.model.Song;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Playlist Domain Entity - Pure business logic for music playlists.
 * 
 * This represents a playlist in the domain, free from persistence concerns.
 * Handles business rules around playlist management like adding/removing songs.
 * 
 * DIFFERENT FROM: playlist/adapter/dbo/PlayList.java (JPA DTO)
 */
public class Playlist {
    
    private final String id;
    private final String name;
    private final List<Song> songs;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Playlist(String id, String name) {
        this(id, name, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
    }

    public Playlist(String id, String name, List<Song> songs) {
        this(id, name, songs, LocalDateTime.now(), LocalDateTime.now());
    }
    
    public Playlist(String id, String name, List<Song> songs, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "Playlist ID cannot be null");
        this.name = Objects.requireNonNull(name, "Playlist name cannot be empty");
        this.songs = new ArrayList<>(Objects.requireNonNull(songs, "Songs list cannot be null"));
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
        
        validateBusinessRules();
    }
    
    private void validateBusinessRules() {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Playlist name cannot be empty");
        }
        if (songs.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Playlist cannot contain null songs");
        }
    }
    
    // Business Operations
    
    /**
     * Add a song to this playlist.
     * Business rule: No duplicate songs allowed.
     */
    public Playlist addSong(Song song) {
        Objects.requireNonNull(song, "Cannot add null song to playlist");
        
        List<Song> newSongs = new ArrayList<>(this.songs);
        if (!newSongs.contains(song)) {
            newSongs.add(song);
        }
        
        return new Playlist(this.id, this.name, newSongs, this.createdAt, LocalDateTime.now());
    }
    
    /**
     * Remove a song from this playlist.
     */
    public Playlist removeSong(Song song) {
        Objects.requireNonNull(song, "Cannot remove null song from playlist");
        
        List<Song> newSongs = new ArrayList<>(this.songs);
        newSongs.remove(song);
        
        return new Playlist(this.id, this.name, newSongs, this.createdAt, LocalDateTime.now());
    }
    
    /**
     * Check if this playlist contains the given song.
     */
    public boolean containsSong(Song song) {
        return this.songs.contains(song);
    }
    
    /**
     * Create a playlist with a new name.
     * Business rule: Name cannot be empty.
     */
    public Playlist rename(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Playlist name cannot be empty");
        }
        
        return new Playlist(this.id, newName.trim(), this.songs, this.createdAt, LocalDateTime.now());
    }
    
    // Getters (no setters - immutable except through business operations above)
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Song> getSongs() {
        return Collections.unmodifiableList(songs);
    }
    
    public int getSongCount() {
        return songs.size();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Playlist)) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(id, playlist.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Playlist{id='%s', name='%s', songs=%d}", 
                           id, name, songs.size());
    }
}
