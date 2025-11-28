package org.ocelot.tunes4j.event;

/**
 * Event fired when a song is selected by the user in the audio bounded context.
 * This typically happens on double-click or play button actions.
 *
 * NOTE: Currently using DTO Song. Domain object conversion may be added later.
 */
public class AudioSongSelectedEvent extends AudioDomainEvent {

    private final org.ocelot.tunes4j.dto.Song song;

    /**
     * Create a new AudioSongSelectedEvent.
     *
     * @param source the object that published this event
     * @param song the selected song (currently DTO for view layer compatibility)
     */
    public AudioSongSelectedEvent(Object source, org.ocelot.tunes4j.dto.Song song) {
        super(source);
        this.song = song;
    }

    /**
     * Get the selected song.
     *
     * @return the selected song
     */
    public org.ocelot.tunes4j.dto.Song getSong() {
        return song;
    }

    /**
     * Get selected song converted to audio model Song.
     * TODO: Implement proper domain object conversion later.
     *
     * @return the selected song as audio model Song
     */
    public org.ocelot.tunes4j.audio.model.Song getSongAsAudioModel() {
        // For now, return null - proper conversion needed
        return null;
    }
}
