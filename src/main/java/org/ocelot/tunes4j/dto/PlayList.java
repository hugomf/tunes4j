package org.ocelot.tunes4j.dto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

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
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	@Column
	private String name;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "playlist_song", 
		joinColumns = @JoinColumn(name = "song_id", referencedColumnName = "id"), 
		inverseJoinColumns = @JoinColumn(name = "playlist_id", referencedColumnName = "id"))
	private List<Song> songs = new ArrayList<Song>();

	public PlayList(String name) {
		this.name = name;
	}

}
