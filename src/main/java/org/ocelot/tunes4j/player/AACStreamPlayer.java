package org.ocelot.tunes4j.player;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.adts.ADTSDemultiplexer;

public class AACStreamPlayer implements RadioPlayer {

	public static String streamurl1 = "http://18543.live.streamtheworld.com/XHRJFMAAC.aac";
	public static String streamurl2 = "http://15373.live.streamtheworld.com:80/XHMVSFMAAC_SC";
	public static String streamurl3 = "http://14953.live.streamtheworld.com/XHRED_FMAAC.aac";
	public static String streamurl4 = "http://cast4.audiostream.com.br:8649/aac";
	public static String streamurl5 = "http://cast.bbstalkradio.com:8000/stream";

	private ADTSDemultiplexer adts;

	private SourceDataLine line;

	private AudioFormat aufmt;

	private Decoder dec;

	private boolean running = false;

	public static void main(String[] args) {
		try {
			AACStreamPlayer player = new AACStreamPlayer();
			InputStream in = getURLInputStream(streamurl1);
			player.open(in);
			player.play();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("error while decoding: " + e.toString());
		}
	}

	public void open(InputStream in) throws Exception {
		this.adts = new ADTSDemultiplexer(in);
		this.aufmt = new AudioFormat(adts.getSampleFrequency(), 16, adts.getChannelCount(), true, true);
		this.dec = new Decoder(adts.getDecoderSpecificInfo());
	}

	public void play() {
		try {
			rawPlay();
		} catch (AACException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	private synchronized void rawPlay() throws IOException, AACException, LineUnavailableException {
		final SampleBuffer buf = new SampleBuffer();
		byte[] b;
		this.running = true;
		try {
			while (running) {
				b = this.adts.readNextFrame();
				this.dec.decodeFrame(b, buf);

				if (line != null && formatChanged(line.getFormat(), buf)) {
					// format has changed (e.g. SBR has started)
					line.stop();
					line.close();
					line = null;
					aufmt = new AudioFormat(buf.getSampleRate(), buf.getBitsPerSample(), buf.getChannels(), true, true);
				}
				if (line == null) {
					line = AudioSystem.getSourceDataLine(this.aufmt);
					line.open();
					line.start();
				}
				b = buf.getData();
				line.write(b, 0, b.length);
			}
		} finally {
			if (line != null) {
				line.stop();
				line.close();
			}
		}
	}

	public void stop() {
		this.running = false;
	}

	private static boolean formatChanged(AudioFormat af, SampleBuffer buf) {
		return af.getSampleRate() != buf.getSampleRate() || af.getChannels() != buf.getChannels()
				|| af.getSampleSizeInBits() != buf.getBitsPerSample() || af.isBigEndian() != buf.isBigEndian();
	}

	private static InputStream getURLInputStream(String sUrl) throws IOException {
		URL url = new URL(sUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		Map<String, List<String>> headerFields = connection.getHeaderFields();

		headerFields.entrySet().forEach(item -> System.out.println(
				String.format("%s: %s", item.getKey(), item.getValue().stream().collect(Collectors.joining(", ")))));

		return connection.getInputStream();
	}

}
