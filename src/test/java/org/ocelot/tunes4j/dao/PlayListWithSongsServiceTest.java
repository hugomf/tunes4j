package org.ocelot.tunes4j.dao;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocelot.tunes4j.config.JpaConfiguration;
import org.ocelot.tunes4j.dto.PlayList;
import org.ocelot.tunes4j.dto.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;


@ContextConfiguration(classes={JpaConfiguration.class})
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class PlayListWithSongsServiceTest {

	@Autowired
	private PlayListRepository service;
	
	@Test
	public void shouldSave() {
		
		service.deleteAll();
		service.save(createPlayList(1));
		List<PlayList> list = (List<PlayList>) service.findAll();
		assertNotNull(list);
		assertThat(list.size(), equalTo(1));
		assertThat(list.get(0).getSongs().size(), equalTo(3));
		assertThat(list.get(0).getSongs().get(0).getTitle() , equalTo("title1"));
	}
	
	
	@Test
	public void shouldRemoveASongFromPlaylist() {
		
		service.deleteAll();
		service.save(createPlayList(1));
		List<PlayList> list = (List<PlayList>) service.findByName("PlayList1");
		List<Song> songs = list.get(0).getSongs();
		songs.remove(songs.get(0));
		service.save(list.get(0));
		list = (List<PlayList>) service.findByName("PlayList1");
		
		assertNotNull(list);
		assertThat(list.size(), equalTo(1));
		assertThat(list.get(0).getSongs().size(), equalTo(2));
		assertThat(list.get(0).getSongs().get(0).getTitle(), equalTo("title2"));
		

	}
	
	@Test
	public void shouldGetById() {
		service.deleteAll();
		
		PlayList playlist = createPlayList(1);
		service.save(playlist);
		assertNotNull(service.findById(playlist.getId()));
	}

	private PlayList createPlayList(int index){
		PlayList playlist = new PlayList();
		playlist.setName("PlayList" + index);
		List<Song> songs = createSongs();
		playlist.setSongs(songs);
		return playlist;
	}

	private List<Song> createSongs() {
		return Lists.newArrayList(createSong(1), createSong(2), createSong(3));
	}
	
	
	private Song createSong(int index){
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
