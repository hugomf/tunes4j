package org.ocelot.tunes4j.playlist.adapter.dbo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Playlist Spring Data JPA Repository.
 *
 * Extends JpaRepository to get default CRUD operations.
 * Provides playlist-specific query methods.
 */
@Repository
public interface PlaylistJpaRepository extends JpaRepository<PlaylistEntity, String> {

    /**
     * Find playlists whose name contains the given string (case-insensitive).
     */
    List<PlaylistEntity> findByNameContaining(String namePart);

    /**
     * Count playlists with exact name match.
     */
    long countByName(String name);
}
