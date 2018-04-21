package org.ocelot.tunes4j.player;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.adts.ADTSDemultiplexer;

public class Radio {
	public static String streamurl1 = "http://18543.live.streamtheworld.com/XHRJFMAAC.aac";
	public static String streamurl2 = "http://15373.live.streamtheworld.com:80/XHMVSFMAAC_SC";
	public static String streamurl3 = "http://14953.live.streamtheworld.com/XHRED_FMAAC.aac";

	public static String streamurl4 =  "http://cast4.audiostream.com.br:8649/aac";
	public static String streamurl5 = "http://sc6.shoutcaststreaming.us:8062/";
	public static String streamurl6 = "http://cast.bbstalkradio.com:8000/stream";

	public static void main(String[] args) {
		try {
			decode(streamurl1);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("error while decoding: "+e.toString());
		}
	}

	private static void decode(String url) throws Exception {
		final SampleBuffer buf = new SampleBuffer();

		SourceDataLine line = null;
		byte[] b;
		try {

			DataInputStream in = getURLInputStream(url);

			final ADTSDemultiplexer adts = new ADTSDemultiplexer(in);
			AudioFormat aufmt = new AudioFormat(adts.getSampleFrequency(), 16, adts.getChannelCount(), true, true);
			final Decoder dec = new Decoder(adts.getDecoderSpecificInfo());

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
		finally {
			if(line!=null) {
				line.stop();
				line.close();
			}
		}
	}

	private static boolean formatChanged(AudioFormat af, SampleBuffer buf) {
		return af.getSampleRate()!=buf.getSampleRate()
				||af.getChannels()!=buf.getChannels()
				||af.getSampleSizeInBits()!=buf.getBitsPerSample()
				||af.isBigEndian()!=buf.isBigEndian();
	}

	private static DataInputStream getURLSocketInputStream(String url) throws IOException, URISyntaxException {
		
		final URI uri = new URI(url);
		final Socket sock = new Socket(uri.getHost(), uri.getPort()>0 ? uri.getPort() : 80);
		//send HTTP request
		final PrintStream out = new PrintStream(sock.getOutputStream());
		String path = uri.getPath();
		if(path==null||path.equals("")) path = "/";
		if(uri.getQuery()!=null) path += "?"+uri.getQuery();
		out.println("GET "+path+" HTTP/1.1");
		out.println("Host: "+uri.getHost());
		out.println();

		//read response (skip header)
		final DataInputStream in = new DataInputStream(sock.getInputStream());
		String x;
		do {
			x = in.readLine();
			System.out.println(x);
		}
		while(x!=null&&!x.trim().equals(""));
		return in;
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
	
	
	
}
