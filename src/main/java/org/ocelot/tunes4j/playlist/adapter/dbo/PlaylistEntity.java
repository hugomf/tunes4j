package org.ocelot.tunes4j.playlist.adapter.dbo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

/**
 * Playlist JPA Entity - Database representation of a playlist.
 */
@Data
@Entity
@Table(name = "PLAYLIST")
public class PlaylistEntity {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "SONG_COUNT", nullable = false)
    private int songCount = 0;

    // In a real implementation, this would be a @OneToMany relationship
    // For now, just storing the relationships in a separate table (not modeled here)
    // @OneToMany(mappedBy = "playlist")
    // private List<PlaylistSongEntity> playlistSongs = new ArrayList<>();

    public PlaylistEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update() {
        this.updatedAt = LocalDateTime.now();
    }
}
