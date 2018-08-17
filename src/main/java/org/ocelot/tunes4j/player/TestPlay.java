package org.ocelot.tunes4j.player;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestPlay {
	

	
	public static void main(String[] args) {
		
		
		testPlay("http://18543.live.streamtheworld.com/XHRJFMAAC.aac");
	}
	
	
	private static InputStream getURLStream(String url) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).addHeader("Icy-MetaData", "1") .build();
		Response response = client.newCall(request).execute();
		InputStream stream = response.body().byteStream();
		return stream;
	}

	
	public static void testPlay(String url)
	{
	  try {
		  
		InputStream is = getURLStream(url);
	    AudioInputStream in= AudioSystem.getAudioInputStream(is);
	    AudioInputStream din = null;
	    AudioFormat baseFormat = in.getFormat();
	    AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
              baseFormat.getSampleRate(),
              16,
              baseFormat.getChannels(),
              baseFormat.getChannels() * 2,
              baseFormat.getSampleRate(),
              false);
	    din = AudioSystem.getAudioInputStream(decodedFormat, in);
	    // Play now. 
	    rawplay(decodedFormat, din);
	    in.close();
	  } catch (Exception e)
	    {
	        //Handle exception.
	    }
	} 

	private static void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException,                                                                                                LineUnavailableException
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

	private static SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
	{
	  SourceDataLine res = null;
	  DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	  res = (SourceDataLine) AudioSystem.getLine(info);
	  res.open(audioFormat);
	  return res;
	} 
}
