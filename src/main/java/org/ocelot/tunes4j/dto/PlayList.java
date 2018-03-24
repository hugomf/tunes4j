package org.ocelot.tunes4j.dto;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class PlayList {
	
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	
	@Column
	private String name;
	
	@Transient
	private List<Song> mediaItems = new ArrayList<Song>();

	public PlayList(String name) {
		this.name = name;
	}
	
}
