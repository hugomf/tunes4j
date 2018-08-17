package org.ocelot.tunes4j.dao;


import org.ocelot.tunes4j.dto.Column;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColumnRepository extends JpaRepository<Column, Integer> { 

	public Column findById(int id);

}
