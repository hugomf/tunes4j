package org.ocelot.tunes4j.dao;

import java.util.Optional;

import org.ocelot.tunes4j.dto.RadioStation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RadioStationRepository extends CrudRepository<RadioStation, String> {

	public Optional<RadioStation> findById(String id);
	
}


