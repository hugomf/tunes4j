package org.ocelot.tunes4j.dao;

import java.util.List;

import org.ocelot.tunes4j.dto.Song;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SongRepository extends CrudRepository<Song, String> { 

	public Song findById(String id);
	
	public Song findByPathAndFileName(String path, String fileName);
	
	@Query("SELECT new java.lang.String(s.path) FROM Song s GROUP BY s.path")
	public List<String> findFolders();

}


