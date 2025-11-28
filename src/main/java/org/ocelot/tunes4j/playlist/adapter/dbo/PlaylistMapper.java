package org.ocelot.tunes4j.playlist.adapter.dbo;

import org.ocelot.tunes4j.playlist.model.Playlist;
import org.springframework.stereotype.Component;

/**
 * Playlist Data Mapper - Converts between domain and persistence objects.
 *
 * Implements the Adapter pattern to bridge domain models and JPA entities.
 */
@Component
public class PlaylistMapper {

    /**
     * Convert domain Playlist to JPA PlaylistEntity.
     */
    public PlaylistEntity toEntity(Playlist domain) {
        if (domain == null) {
            return null;
        }

        PlaylistEntity entity = new PlaylistEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());

        // Map domain duration to count (temporary - domain model needs enhancement)
        entity.setSongCount(domain.getSongCount());

        // Set timestamps if they exist in domain (currently not implemented)
        // entity.setCreatedAt(domain.getCreatedAt());
        // entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }

    /**
     * Convert JPA PlaylistEntity to domain Playlist.
     */
    public Playlist toDomain(PlaylistEntity entity) {
        if (entity == null) {
            return null;
        }

        Playlist domain = new Playlist(entity.getId(), entity.getName());
        // Note: Duration mapping needs to be enhanced in domain model

        return domain;
    }
}
