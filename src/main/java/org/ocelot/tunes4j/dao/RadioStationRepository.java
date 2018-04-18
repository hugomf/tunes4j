package org.ocelot.tunes4j.dao;

import org.ocelot.tunes4j.dto.RadioStation;
import org.ocelot.tunes4j.dto.Song;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RadioStationRepository extends CrudRepository<RadioStation, String> { 

	public Song findById(String id);
	
}


