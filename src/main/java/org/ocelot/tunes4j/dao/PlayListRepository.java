package org.ocelot.tunes4j.dao;


import java.util.List;

import org.ocelot.tunes4j.dto.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayListRepository extends JpaRepository<PlayList, String>{

	public PlayList findById(String id);
	
	public List<PlayList> findByName(String name);
	
}
