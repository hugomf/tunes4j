package org.ocelot.tunes4j.library.adapter;


import org.ocelot.tunes4j.dto.Column;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColumnRepository extends JpaRepository<Column, Integer> { 

	public Column findById(int id);

}
