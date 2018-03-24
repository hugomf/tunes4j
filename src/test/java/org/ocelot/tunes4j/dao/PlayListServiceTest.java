package org.ocelot.tunes4j.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocelot.tunes4j.config.JpaConfiguration;
import org.ocelot.tunes4j.dto.PlayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@ContextConfiguration(classes={JpaConfiguration.class})
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class PlayListServiceTest {

	@Autowired
	private PlayListRepository service;
	
	@Test
	public void shouldSave() {
		
		service.deleteAll();
		
		service.save(createPlayList(1));
		service.save(createPlayList(2));
		service.save(createPlayList(3));
		List<PlayList> list = (List<PlayList>) service.findAll();
		assertNotNull(list);
		assertEquals(3,list.size());
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
		playlist.setName("PlayList");
		return playlist;
	}
}
