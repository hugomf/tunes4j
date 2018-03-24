package org.ocelot.tunes4j.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Entity
public class Column {
	
	@Id
	private Integer id;
	
	@javax.persistence.Column
	private int size;

}
