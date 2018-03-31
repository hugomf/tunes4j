package org.ocelot.tunes4j.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode
@Entity
public class Song  {
	
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	
	@Column
	private String path;
	
	@Column
	private String fileName;
	
	@Column
	private String artist;
	
	@Column
	private String title;
	
	@Column
	private String album;
	
	@Column
	private String trackNumber;
	
	@Column
	private String genre;
	
	@Column
	private String author;
	
	@Column
	private String year;

}