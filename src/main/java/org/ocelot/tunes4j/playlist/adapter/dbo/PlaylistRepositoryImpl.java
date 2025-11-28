package org.ocelot.tunes4j.playlist.adapter.dbo;

import org.ocelot.tunes4j.playlist.adapter.PlaylistRepository;
import org.ocelot.tunes4j.playlist.model.Playlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Playlist Repository Implementation - JPA Adapter.
 *
 * Implements the PlaylistRepository contract using Spring Data JPA.
 * Maps between domain Playlist entities and persistence PlaylistEntity.
 */
@Repository
public class PlaylistRepositoryImpl implements PlaylistRepository {

    private final PlaylistJpaRepository jpaRepository;
    private final PlaylistMapper mapper;

    @Autowired
    public PlaylistRepositoryImpl(PlaylistJpaRepository jpaRepository, PlaylistMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Playlist save(Playlist playlist) {
        PlaylistEntity entity = mapper.toEntity(playlist);
        PlaylistEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Playlist> findById(String id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public List<Playlist> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Playlist> findByNameContaining(String namePart) {
        return jpaRepository.findByNameContaining(namePart).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
