package org.ocelot.tunes4j.dto;

import java.beans.Transient;
import java.io.File;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;


@Data
@Entity
public class Song {

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	@Lob
	@Column
	private byte[] artWork;

	@Column
	private String artMimeType;

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

	@ManyToMany(mappedBy = "songs")
	private List<PlayList> playlists;

	@Transient
	public File getSongFile() {
		String filePath = getPath() + File.separator + getFileName();
		File songFile = new File(filePath);
		return songFile;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Song))
			return false;
		Song song = (Song) o;
		return song.getFileName().equals(this.getFileName()) 
				&& song.getPath().equals(this.getPath());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPath() + File.separator + getFileName());
	}

}
