package org.ocelot.tunes4j.player;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Mp3StreamPlayer implements RadioPlayer {

	public static String streamurl1 = "http://radio.flex.ru:8000/radionami";
	public static String streamurl2 = "http://radio.flex.ru:8000/premium128";
	public static String streamurl3 = "http://uplink.duplexfx.com:8800/";
	public static String streamurl4 = "http://hi1.streamingsoundtracks.com:8000/";	
	public static String streamurl5 = "http://airspectrum.cdnstream1.com:8114/1648_128";
	public static String streamurl6 = "http://airspectrum.cdnstream1.com:8116/1649_192";
	public static String streamurl7 = "http://18803.live.streamtheworld.com:80/XHMVSFM_SC";
	public static String streamurl8 = "http://noasrv.caster.fm:10182/stream";
	public static String streamurl9 = "http://stream2.dyndns.org:8000/xeco.mp3";
	
	
	
	public BasicPlayer player = null;
	
	public static void main(String[] args) throws Exception {        
	    
		Mp3StreamPlayer player = new Mp3StreamPlayer();
		InputStream is = getURLInputStream2(streamurl7);
		player.open(is);
		player.play();
		//GUIUtils.sleep(5000);
		player.stop();
	}
	
	public void open(InputStream is) throws Exception {
		this.player = new BasicPlayer();
		this.player.open(is);
	}
	
	private  static InputStream getURLInputStream(String sUrl) throws IOException {
		URL url = new URL(sUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty ("Icy-Metadata", "1");
		Map<String, List<String>> headerFields = conn.getHeaderFields();
		
		headerFields.entrySet().forEach(item->System.out.println(
				String.format("%s: %s",  
					item.getKey(), 
					item.getValue().stream().collect(Collectors.joining(", "))	
				)
			));
		
		int metaInt = conn.getHeaderFieldInt("icy-metaint",0);
		//InputStream is = new IcyInputStream(conn.getInputStream(), metaInt);
		return conn.getInputStream();
	}
	private  static InputStream getURLInputStream2(String sUrl) throws IOException  {
	
		OkHttpClient client = new OkHttpClient();
		 Request request = new Request.Builder()
		 .url(sUrl)
		 .build();
		 Response response = client.newCall(request).execute();
		 return response.body().byteStream();
	}

	@Override
	public void play()  {
		try {
			this.player.play();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if (player!= null) {
			try {
				player.stop();
			} catch (BasicPlayerException e) {
				e.printStackTrace();
			}
		}
	}
	
}
