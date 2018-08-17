package org.ocelot.tunes4j.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocelot.tunes4j.config.JpaConfiguration;
import org.ocelot.tunes4j.dto.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@ContextConfiguration(classes={JpaConfiguration.class})
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class SongServiceTest {

	@Autowired
	private SongRepository service;
	
	@Test
	public void shouldSave() {
		
		service.deleteAll();
		
		service.save(createAudioObject(1));
		service.save(createAudioObject(2));
		service.save(createAudioObject(3));
		List<Song> list = (List<Song>) service.findAll();
		assertNotNull(list);
		assertEquals(3,list.size());
	}
	
	@Test
	public void shouldGetById() {
		service.deleteAll();
		
		Song song = createAudioObject(1);
		service.save(song);
		assertNotNull(service.findById(song.getId()));
	}

	private Song createAudioObject(int index){
		Song song = new Song();
		song.setFileName("filename" + index);
		song.setPath("path" + index);
		song.setAuthor("author" + index);
		song.setAlbum("album" + index);
		song.setArtist("artist" + index);
		song.setGenre("genre"  + index);
		song.setTitle("title" + index);
		song.setTrackNumber("trackNumber"  + index);
		song.setYear("year" + index);
		return song;
	}
}
