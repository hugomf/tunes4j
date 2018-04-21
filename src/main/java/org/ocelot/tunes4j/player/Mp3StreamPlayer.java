package org.ocelot.tunes4j.player;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Mp3StreamPlayer {

	public static String streamurl1 = "http://radio.flex.ru:8000/radionami";
	public static String streamurl2 = "http://radio.flex.ru:8000/premium128";
	public static String streamurl3 = "http://uplink.duplexfx.com:8800/";
	public static String streamurl4 = "http://hi1.streamingsoundtracks.com:8000/";	
	public static String streamurl5 = "http://airspectrum.cdnstream1.com:8114/1648_128";
	public static String streamurl6 = "http://airspectrum.cdnstream1.com:8116/1649_192";
	public static String streamurl7 = "http://18803.live.streamtheworld.com:80/XHMVSFM_SC";
	
	public Player player = null;
	
	
	public static void main(String[] args) throws IOException {        
	    
		Mp3StreamPlayer player = new Mp3StreamPlayer();
		player.playUrl(streamurl2);
//		GUIUtils.sleep(10000);
//		player.stop();
	}
	
	private  void playUrl(String url) throws IOException {
		InputStream stream = getURLInputStream(url);
		new Thread(new Runnable() {
			@Override
			public void run() {
				stop();
				playStream(stream);
			}
		}).start();
	}
	
	private  static DataInputStream getURLInputStream(String sUrl) throws IOException {
		URL url = new URL(sUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		Map<String, List<String>> headerFields = connection.getHeaderFields();
		
		headerFields.entrySet().forEach(item->System.out.println(
				String.format("%s: %s",  
					item.getKey(), 
					item.getValue().stream().collect(Collectors.joining(", "))	
				)
			));
		
		return new DataInputStream(connection.getInputStream());
	}

	private  void playStream(InputStream stream) {
		
		try {	    	
	        this.player = new Player(stream);
	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	    }
	    try {
	        player.play();
	    } catch (JavaLayerException e) {
	        System.out.println(e.getMessage());
	    }
	}
	
	private  void stop() {
		if (player!= null) {
			player.close();
		}
	}
	
}
