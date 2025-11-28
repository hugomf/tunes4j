package org.ocelot.tunes4j;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.ocelot.tunes4j.config.AppConfiguration;
// import org.ocelot.tunes4j.audio.view.SongListView; // REMOVED: SongList belongs in Library bounded context
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.event.AudioSongSelectedEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Simple test class to verify audio playback works without full GUI.
 */
public class TestAudioPlayback {

    public static void main(String[] args) {
        System.out.println("🧪 TESTING AUDIO PLAYBACK FUNCTIONALITY");
        System.out.println("=====================================");

        try {
            // Initialize Spring context
            ApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
            System.out.println("✅ Spring context initialized");

            // Get components
            // SongListView songListView = context.getBean(SongListView.class); // ARCHITECTURAL CORRECTION: Removed, belongs in Library bounded context
            ApplicationEventPublisher eventPublisher = context.getBean(ApplicationEventPublisher.class);

            System.out.println("✅ Retrieved Spring beans");

            // Check if songs are loaded
            System.out.println("🎵 Checking song loading...");

            // Small delay to let components initialize
            Thread.sleep(2000);

            // Create a test song
            Song testSong = new Song();
            testSong.setId("test-song-1");
            testSong.setTitle("Test MP3 Song");
            testSong.setArtist("Test Artist");
            testSong.setAlbum("Test Album");
            testSong.setPath("src/main/resources");
            testSong.setFileName("test.mp3");
            testSong.setGenre("Test");

            System.out.println("🎵 Publishing test AudioSongSelectedEvent...");

            // Publish event to trigger playback
            eventPublisher.publishEvent(new AudioSongSelectedEvent(null, testSong));

            System.out.println("🎵 Event published, waiting for audio to play...");

            // Wait for audio to start (if it works)
            Thread.sleep(5000);

            System.out.println("🧪 Test completed - check logs above for playback success");

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.exit(0);
    }
}
