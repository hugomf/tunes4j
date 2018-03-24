package test;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MP3Player {

    private static final MP3Player mp3Player = new MP3Player();
    
    private Player player = null;
    private final Object pause;
    
    private MP3Player() {
        this.pause = new Object();
    }
    
    public static MP3Player getPlayer() {
        return(mp3Player);
    }
    
    public void play(File file, Callback<Exception> onError) throws FileNotFoundException {
        try {
            stop();
            player = new Player(new FileInputStream(file));
            player.play();
        } catch(JavaLayerException e) {
            onError.callback(e);
        }
    }
    
    public void pause(int ms) {
        stop();
        try {
            synchronized(pause) {
                pause.wait(ms);
            }
        } catch(InterruptedException ex) {
        }
    }
    
    public void stop() {
        if((player != null) && !player.isComplete()) {
            player.close();
        } else {
            synchronized(pause) {
                pause.notify();
            }
        }
    }
    
}