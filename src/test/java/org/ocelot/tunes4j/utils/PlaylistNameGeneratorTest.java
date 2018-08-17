package org.ocelot.tunes4j.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

public class PlaylistNameGeneratorTest {
	
	
	
	@Test
	public void shouldReturnPlaylistWhenCurrentPlaylistNamesAreEmpty() throws Exception {
		
		String[] names = {};
		String playlistName = PlayListNameGenerator.getInstance().findNext(names);
		assertThat(playlistName, equalTo("Playlist 1"));
		
	}
	
	@Test
	public void shouldReturnPlaylistWhenNamesHaveOnlyOneEmptyName() throws Exception {
		String[] names = {""};
		String playlistName = PlayListNameGenerator.getInstance().findNext(names);
		assertThat(playlistName, equalTo("Playlist 1"));
	}
	
	
	@Test
	public void shouldReturnIncreaseSequenceWhenExistingPlaylistIsPresentInArrayWithWeirdName() throws Exception {
		String[] names = {"jahljdhskadhsla  424243PlayList 			1"};
		String playlistName = PlayListNameGenerator.getInstance().findNext(names);
		assertThat(playlistName, equalTo("Playlist 2"));
		
		
	}
	
	@Test
	public void shouldReturnIncreaseSequenceWhenExistingPlaylistIsPresentInArray() throws Exception {
		String[] names = {"Playlist 1","Playlist 2", "Playlist 3"};
		String playlistName = PlayListNameGenerator.getInstance().findNext(names);
		assertThat(playlistName, equalTo("Playlist 4"));
		
	}
	
	
	@Test
	public void shouldReturnIncreaseSequenceWhenExistingPlaylistIsPresentInArray2() throws Exception {
		String[] names = {
				"Other playlist",
				"Rock viral",
				"radio best selections",
				"Playlist 3",
				"Oldies but goodies",
				"Playlist playlist not good", 
				"Playlist 5", 
				"Playlist for old people"};
		
		String playlistName = PlayListNameGenerator.getInstance().findNext(names);
		assertThat(playlistName, equalTo("Playlist 1"));
		names = (String []) ArrayUtils.add(names, playlistName);
		
		playlistName = PlayListNameGenerator.getInstance().findNext(names);
		assertThat(playlistName, equalTo("Playlist 2"));
		names = (String []) ArrayUtils.add(names, playlistName);
		
		playlistName = PlayListNameGenerator.getInstance().findNext(names);
		assertThat(playlistName, equalTo("Playlist 4"));
		names = (String []) ArrayUtils.add(names, playlistName);
		
		playlistName = PlayListNameGenerator.getInstance().findNext(names);
		assertThat(playlistName, equalTo("Playlist 6"));
		names = (String []) ArrayUtils.add(names, playlistName);
		
	}

	
	
	

}
