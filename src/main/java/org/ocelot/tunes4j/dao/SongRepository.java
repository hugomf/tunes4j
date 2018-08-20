package org.ocelot.tunes4j.dao;

import org.ocelot.tunes4j.dto.Song;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SongRepository extends CrudRepository<Song, String> { 

	public Song findById(String id);
	
	public Song findByPathAndFileName(String path, String fileName);
	
}


