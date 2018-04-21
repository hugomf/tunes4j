package org.ocelot.tunes4j.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Entity
public class RadioStation {
	
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	
	@Lob
	@Column
	private byte[] artWork;
	
	@Column
	private String name;
	
	@Column
	private String localFrequency;

	@Column
	private String description;
	
	@Column
	private String genre;

	@Column
	private String contentType;

	@Column
	private String url;
	
}
