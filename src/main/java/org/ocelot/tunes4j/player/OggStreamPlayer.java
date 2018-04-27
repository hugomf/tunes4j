package org.ocelot.tunes4j.player;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.ocelot.tunes4j.utils.GUIUtils;

public class OggStreamPlayer implements RadioPlayer {

	private static String streamurl1 = "http://stream.radioreklama.bg:80/radio1rock.ogg";
	
	private AudioInputStream din;
	
	private SourceDataLine line;
	
	private boolean running;

	public static void main(String[] args) throws Exception {
		OggStreamPlayer player = new OggStreamPlayer();
		InputStream stream = getURLInputStream(streamurl1);
		player.open(stream);
		player.play();
		GUIUtils.sleep(5000);
		player.stop();
	}

	private static InputStream getURLInputStream(String sUrl) throws IOException {
		URL url = new URL(sUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		Map<String, List<String>> headerFields = connection.getHeaderFields();

		headerFields.entrySet().forEach(item -> System.out.println(
				String.format("%s: %s", item.getKey(), item.getValue().stream().collect(Collectors.joining(", ")))));
		int metaInt = connection.getHeaderFieldInt("icy-metaint", 16000);
		return new BufferedInputStream(connection.getInputStream(), metaInt);
	}

	public void open(InputStream is) throws Exception {
		AudioInputStream in = AudioSystem.getAudioInputStream(is);
		if (in != null) {
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
					16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);

			// Get AudioInputStream that will be decoded by underlying VorbisSPI
			this.din = AudioSystem.getAudioInputStream(decodedFormat, in);
			
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
			this.line = (SourceDataLine) AudioSystem.getLine(info);
			this.line.open(decodedFormat);
		}
	}
	
	public void play() {
		try {
			rawplay();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	private void rawplay() throws IOException, LineUnavailableException {
		byte[] data = new byte[4096];
		this.running = true;
		if (line != null) {
			// Start
			line.start();
		    int nBytesRead = 0; 
		    while (nBytesRead != -1 && this.running) {
		        nBytesRead = din.read(data, 0, data.length);
		        if (nBytesRead != -1) {  
		        		line.write(data, 0, nBytesRead);
		        }
		    }
		     // Stop
		    line.drain();
		    line.stop();
		    line.close();
		    din.close();
		}		
	}



	@Override
	public void stop() {
		this.running = false;
	}

}
