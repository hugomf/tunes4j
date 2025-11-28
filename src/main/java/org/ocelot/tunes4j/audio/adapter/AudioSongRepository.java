package org.ocelot.tunes4j.audio.adapter;

import org.ocelot.tunes4j.audio.model.Song;
import java.util.List;
import java.util.Optional;

/**
 * Audio Song Repository Interface - Domain Repository Contract.
 *
 * Defines the contract for song persistence operations within the Audio bounded context.
 * Implementation provided by SongRepositoryImpl with help from SongMapper.
 */
public interface AudioSongRepository {

    // Domain entity operations
    Song save(Song song);
    Optional<Song> findById(String id);
    Song findByPathAndFileName(String path, String fileName);

    // Domain-specific queries
    void deleteById(String id);
    boolean existsById(String id);
    Iterable<Song> findAll();
    List<String> findFolders();

    // Audio-specific queries
    List<Song> findByArtist(String artist);
    List<Song> findByAlbum(String album);
    List<Song> findByTitleContaining(String titlePart);
}
