package org.ocelot.tunes4j.player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SimplePlayer {
	AudioInputStream in;
	
	private void open(String filePath){
		File file = new File(filePath);
		AudioInputStream decodedInputStream = null;
		try {
			in= AudioSystem.getAudioInputStream(file);
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat =
			    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
			                    baseFormat.getSampleRate(),16,
			                    baseFormat.getChannels(),
			                    baseFormat.getChannels() * 2,
			                    baseFormat.getSampleRate(),
			                    false);
			decodedInputStream = AudioSystem.getAudioInputStream(decodedFormat, in);
			rawplay(decodedFormat, decodedInputStream);
			close(decodedInputStream);
			close(in);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		res = (SourceDataLine) AudioSystem.getLine(info);
		res.open(audioFormat);
		return res;
	}
	
	private void rawplay(AudioFormat targetFormat, AudioInputStream din) {
		byte[] data = new byte[4096];
		SourceDataLine line;
		try {
			line = getLine(targetFormat);
			if (line != null) {
				// Start
				line.start();
				int nBytesRead = 0, nBytesWritten = 0;
				while (nBytesRead != -1) {
					nBytesRead = din.read(data, 0, data.length);
					if (nBytesRead != -1)
						nBytesWritten = line.write(data, 0, nBytesRead);
				}
				// Stop
				line.drain();
				line.stop();
				line.close();
			}
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void close(InputStream is){
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		SimplePlayer player = new SimplePlayer();
		player.open("/Users/hugo/eclipse-workspace2/LaunchAndLearn/resources/amiga.mp3");

	}
	
}
