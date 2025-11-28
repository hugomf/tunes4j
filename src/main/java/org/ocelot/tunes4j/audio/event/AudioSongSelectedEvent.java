package org.ocelot.tunes4j.audio.event;

import org.ocelot.tunes4j.audio.model.Song;
import org.springframework.context.ApplicationEvent;

/**
 * AudioSongSelectedEvent - Triggered when user double-clicks to activate a song.
 *
 * Observer Pattern: Published by SongListView, consumed by AudioController.
 */
public class AudioSongSelectedEvent extends ApplicationEvent {

    private final Song selectedSong;

    public AudioSongSelectedEvent(Object source, Song selectedSong) {
        super(source);
        this.selectedSong = selectedSong;
    }

    public Song getSelectedSong() {
        return selectedSong;
    }

    @Override
    public String toString() {
        return "AudioSongSelectedEvent{song='" + selectedSong.getTitle() + "'}";
    }
}
