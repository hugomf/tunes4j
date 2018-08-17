package org.ocelot.tunes4j.player;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Component
public class RadioStreamPlayer implements RadioPlayer {

	private static final Logger logger = LoggerFactory.getLogger(RadioStreamPlayer.class);

	public static String mp3Url = "http://18803.live.streamtheworld.com:80/XHMVSFM_SC";
	private static String oggUrl = "http://stream.radioreklama.bg:80/radio1rock.ogg";
	private static String aacUrl = "http://18543.live.streamtheworld.com/XHRJFMAAC.aac";
	public static String streamurl9 = "http://stream2.dyndns.org:8000/xeco.mp3";
	
	private RadioPlayer radioPlayer;

	private String contentType;

	private ResponseBody body;

	private int metaint;

	public static void main(String[] args) throws Exception {
		RadioStreamPlayer player = new RadioStreamPlayer();
		player.open(aacUrl);
		player.play();
		// GUIUtils.sleep(8000);
		// player.open(aacUrl);
		// player.play();
		// GUIUtils.sleep(8000);
		// player.open(mp3Url);
		// player.play();

	}

	@Override
	public void open(InputStream stream) throws Exception {

		if (this.radioPlayer != null) {
			this.radioPlayer.stop();
		}

		this.radioPlayer = RadioPlayerFactory.getInstance(this.contentType);
		if (this.radioPlayer == null) {
			throw new IllegalArgumentException("Player is closed");
		}

		this.radioPlayer.open(stream);
	}

	@Override
	public void play() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				radioPlayer.play();
			}
		}).start();
	}

	@Override
	public void stop() {
		this.radioPlayer.stop();
		this.body.close();
	}

	private InputStream getURLInputStream(String sUrl) throws IOException {
		URL url = new URL(sUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// conn.setRequestProperty ("Icy-Metadata", "1");

		Map<String, List<String>> headerFields = conn.getHeaderFields();
		headerFields.entrySet().forEach(item -> logger.debug(
				String.format("%s: %s", item.getKey(), item.getValue().stream().collect(Collectors.joining(", ")))));
		this.contentType = conn.getHeaderField("Content-Type");
		this.metaint = conn.getHeaderFieldInt("icy-metaint", 16000);
		// return new BufferedInputStream(conn.getInputStream(),metaint);
		// return new IcyInputStream(conn.getInputStream(), this.metaint);
		return conn.getInputStream();
	}

	private InputStream getURLInputStream2(String sUrl) throws IOException {

		OkHttpClient client = new OkHttpClient().newBuilder()
		   .connectTimeout(10, TimeUnit.SECONDS)
		   .readTimeout(2, TimeUnit.DAYS).build();
				
		client.retryOnConnectionFailure();
		
		Request request = new Request.Builder().url(sUrl).build();
		Response response = client.newCall(request).execute();
		Headers headers = response.headers();

		headers.names().stream().forEach(item -> 
			logger.debug(
				String.format("%s: %s", item, headers.get(item))));

		this.contentType = response.header("Content-Type");
		this.body = response.body();

		return this.body.byteStream();
	}

	public void open(String url) throws Exception {
		open(getURLInputStream2(url));
	}

}
