package org.ocelot.tunes4j.playlist.adapter;

import org.ocelot.tunes4j.playlist.model.Playlist;
import java.util.List;
import java.util.Optional;

/**
 * Playlist Repository Interface - Domain Repository Contract.
 *
 * Defines the contract for playlist persistence operations within the Playlist bounded context.
 * Implementation provided by PlaylistRepositoryImpl with help from PlaylistMapper.
 */
public interface PlaylistRepository {

    // CRUD Operations
    Playlist save(Playlist playlist);
    Optional<Playlist> findById(String id);
    List<Playlist> findAll();
    void deleteById(String id);
    boolean existsById(String id);

    // Playlist-specific queries
    List<Playlist> findByNameContaining(String namePart);

    // Administrative operations
    long count();
}
