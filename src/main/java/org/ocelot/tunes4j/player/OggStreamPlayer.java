package org.ocelot.tunes4j.player;

import java.io.DataInputStream;
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

public class OggStreamPlayer {
	
	public static String streamurl1 = "http://stream.radioreklama.bg:80/radio1rock.ogg";
	
	public static void main(String[] args) throws IOException {        
		OggStreamPlayer player = new OggStreamPlayer();
		player.playUrl(streamurl1);
	}
	
	public void playUrl(String url) throws IOException {
		InputStream stream = getURLInputStream(url);
		testPlay(stream);
	}

	private DataInputStream getURLInputStream(String sUrl) throws IOException {
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

	public void testPlay(InputStream is)
	{
	  try
	  {
	    AudioInputStream in= AudioSystem.getAudioInputStream(is);
	    AudioInputStream din = null;
	    if (in != null)
	    {
	        AudioFormat baseFormat = in.getFormat();
	        AudioFormat  decodedFormat = new AudioFormat(
	                AudioFormat.Encoding.PCM_SIGNED,
	                baseFormat.getSampleRate(),
	                16,
	                baseFormat.getChannels(),
	                baseFormat.getChannels() * 2,
	                baseFormat.getSampleRate(),
	                false);
	         // Get AudioInputStream that will be decoded by underlying VorbisSPI
	        din = AudioSystem.getAudioInputStream(decodedFormat, in);
	        // Play now !
	        rawplay(decodedFormat, din);
	        in.close();		
	    }
	  }
	  catch (Exception e)
	  {
	    e.printStackTrace();
	  }
	}

	private void rawplay(AudioFormat targetFormat, 
	                                   AudioInputStream din) throws IOException, LineUnavailableException
	{
	   byte[] data = new byte[4096];
	  SourceDataLine line = getLine(targetFormat);		
	  if (line != null)
	  {
	     // Start
	    line.start();
	     int nBytesRead = 0, nBytesWritten = 0;
	     while (nBytesRead != -1)
	    {
	        nBytesRead = din.read(data, 0, data.length);
	         if (nBytesRead != -1) nBytesWritten = line.write(data, 0, nBytesRead);
	    }
	     // Stop
	    line.drain();
	    line.stop();
	    line.close();
	    din.close();
	  }		
	}

	private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
	{
	  SourceDataLine res = null;
	  DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	  res = (SourceDataLine) AudioSystem.getLine(info);
	  res.open(audioFormat);
	  return res;
	}
	
}
