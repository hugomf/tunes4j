package org.ocelot.tunes4j;

import java.io.File;
import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;

/**
 * Direct test of Tunes4JAudioPlayer to verify basic functionality
 */
public class TestDirectAudio {
    public static void main(String[] args) {
        System.out.println("🎵 TESTING DIRECT AUDIO PLAYER");
        System.out.println("=============================");

        try {
            // Test file path
            String mp3Path = "src/main/resources/test.mp3";
            File mp3File = new File(mp3Path);

            System.out.println("🎵 Testing file: " + mp3Path);
            System.out.println("🎵 File exists: " + mp3File.exists());
            System.out.println("🎵 File readable: " + mp3File.canRead());
            System.out.println("🎵 File length: " + mp3File.length() + " bytes");

            if (!mp3File.exists()) {
                System.err.println("❌ Test file not found: " + mp3Path);
                return;
            }

            // Create audio player
            Tunes4JAudioPlayer player = new Tunes4JAudioPlayer();
            System.out.println("🎵 Created Tunes4JAudioPlayer");

            // Try to open and play
            System.out.println("🎵 Attempting to open song...");
            player.open(mp3File);

            System.out.println("🎵 Song opened successfully");

            System.out.println("🎵 Attempting to play...");
            player.play();

            System.out.println("🎵 Playback started");

            // Check status
            int status = player.getCurrentStatus();
            System.out.println("🎵 Player status: " + status);

            // Wait a bit
            System.out.println("🎵 Waiting 3 seconds for audio...");
            Thread.sleep(3000);

            System.out.println("🎵 Stopping playback...");
            player.stop();

            System.out.println("✅ DIRECT AUDIO TEST COMPLETED SUCCESSFULLY");

        } catch (Exception e) {
            System.err.println("❌ DIRECT AUDIO TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
