package org.ocelot.tunes4j.player;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.adts.ADTSDemultiplexer;

public class AACStreamPlayer {

	public static String streamurl9 = "http://18543.live.streamtheworld.com/XHRJFMAAC.aac";

	
	public static void main(String[] args) throws IOException, LineUnavailableException {        
		AACStreamPlayer player = new AACStreamPlayer();
		player.playUrl(streamurl9);
	}
	
	public void playUrl(String url) throws IOException, LineUnavailableException {
		InputStream stream = getStreamFromUrl(url);
		testPlay(stream);
	}

	private InputStream getStreamFromUrl(String urlPath) throws IOException {
		
//		OkHttpClient client = new OkHttpClient.Builder()
//				.connectTimeout(15,  TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS).build();
//		Request request = new Request.Builder().url(urlPath).addHeader("Icy-MetaData", "1") .build();
//		Response response = client.newCall(request).execute();
//		System.out.println(response.headers());	
//		InputStream stream = response.body().byteStream();
		
		URL url = new URL(urlPath);
		URLConnection connection = url.openConnection();
		Map<String, List<String>> headerFields = connection.getHeaderFields();
		headerFields.entrySet().forEach(item->System.out.println(String.format("%s,%s", item.getKey(), item.getValue())));
		InputStream stream  = connection.getInputStream();
		return stream;
	}

	public void testPlay(InputStream is) throws IOException, LineUnavailableException {
	  
		final ADTSDemultiplexer adts = new ADTSDemultiplexer(new DataInputStream(is));
		AudioFormat aufmt = new AudioFormat(adts.getSampleFrequency(), 16, adts.getChannelCount(), true, true);
		System.out.println(aufmt);
		final Decoder dec = new Decoder(adts.getDecoderSpecificInfo());
		
		final SampleBuffer buf = new SampleBuffer();
		SourceDataLine line = getLine(aufmt);
		byte[] b;
		while(true) {
			b = adts.readNextFrame();
			dec.decodeFrame(b, buf);

			if(line!=null&&formatChanged(line.getFormat(), buf)) {
				//format has changed (e.g. SBR has started)
				line.stop();
				line.close();
				line = null;
				aufmt = new AudioFormat(buf.getSampleRate(), buf.getBitsPerSample(), buf.getChannels(), true, true);
			}
			if(line==null) {
				line = AudioSystem.getSourceDataLine(aufmt);
				line.open();
				line.start();
			}
			b = buf.getData();
			line.write(b, 0, b.length);
		}
		
		
	}
	
	
	private static boolean formatChanged(AudioFormat af, SampleBuffer buf) {
		return af.getSampleRate()!=buf.getSampleRate()
				||af.getChannels()!=buf.getChannels()
				||af.getSampleSizeInBits()!=buf.getBitsPerSample()
				||af.isBigEndian()!=buf.isBigEndian();
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
