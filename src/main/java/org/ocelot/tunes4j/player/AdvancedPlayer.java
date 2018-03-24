package org.ocelot.tunes4j.player;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class AdvancedPlayer {
	
	
	public static final int OPENED = 1001;
	public static final int PLAYING = 1002;
	public static final int PAUSED = 1003;
	public static final int SEEKING = 1004;
	public static final int STOPPED = 1005;
	public static final int UNKNOWN = 1006;
	
	private int currentState = UNKNOWN;
	
	AudioInputStream in;
	AudioInputStream decodedInputStream;
	SourceDataLine line;
	Thread playerThread;
	int encodedLength = -1;
	public static int SKIP_INACCURACY_SIZE = 1200;
	
	
	private void loadAudio(){
		
		if(decodedInputStream==null) {
			File file = new File("/Users/hugo/eclipse-workspace2/tunes4j/src/main/resources/amiga.mp3");
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
				encodedLength = decodedInputStream.available();
				line = getLine(decodedFormat);
				
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void startPlaying() {
		new Thread(){
			@Override
			public void run() {
				loadAudio();
				play();
				close(decodedInputStream);
				close(in);
			}
		}.start();
		
		
	}
	
	public void stopPlaying(){
		 if (line!=null && (currentState == PLAYING) || (currentState == PAUSED)) {
			 	line.flush();
				line.stop();
				try {
					decodedInputStream.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				playerThread = null;
				currentState = STOPPED;
		 }
	}
	
	private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(audioFormat);
		return line;
	}
	

	private void close(InputStream is){
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
		
	public void play() {
		byte[] data = new byte[4096];
		try {
			if (line != null) {
				currentState = PLAYING;
				line.start();
				int nBytesRead = 0;
				int nBytesWritten = 0;
				while (nBytesRead != -1) {
					nBytesRead = decodedInputStream.read(data, 0, data.length);
					if (nBytesRead != -1)
						nBytesWritten = line.write(data, 0, nBytesRead);
				}
				line.drain();
				line.stop();
				line.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
//	public void skipBytes(long bytes){
//		System.out.println("bytes:" + bytes);
//		long totalSkipped = 0;
//	
//		synchronized (decodedInputStream)
//        {
//        	long skipped = 0;
//        	
//        	try {
//	            if (decodedInputStream != null)
//	            {
//	                // Loop until bytes are really skipped.
//	                while (totalSkipped < (bytes - SKIP_INACCURACY_SIZE))
//	                {
//						skipped = decodedInputStream.skip(bytes - totalSkipped);
//	                    if (skipped == 0) break;
//	                    totalSkipped = totalSkipped + skipped;
//	                    System.out.println("Skipped : " + totalSkipped + "/" + bytes);
//	                    if (totalSkipped == -1) throw new Exception("SKIP NOT SUPPORTED");
//	                }
//	            }
//        	} catch (IOException e) {
//        		e.printStackTrace();
//        	} catch (Exception e) {
//				e.printStackTrace();
//			}
//        }
//	}
	
	
	protected long skipBytes(long bytes)
    {
        	long totalSkipped = 0;
            long skipped = 0;
                synchronized (decodedInputStream) {
                	try {
                		totalSkipped = bytes - getEncodedStreamPosition();
                		if(totalSkipped > 0) {
	                		while(skipped < (totalSkipped-1200)) {
	                			skipped = decodedInputStream.skip(totalSkipped);
	                			System.out.println("skipped:" + skipped);
	                		}
                		} else {
                			System.out.println("need Backwards implementation");
                		}
					} catch (IOException e) {
						e.printStackTrace();
					}   
                }
        return totalSkipped;
    }
	
	
	
	public int getByteLength() {
		return encodedLength;
	}
	
	protected int getEncodedStreamPosition()
    {
        int nEncodedBytes = -1;
          try
            {
                if (decodedInputStream != null) {
                    nEncodedBytes = encodedLength - decodedInputStream.available();
                }
            }
            catch (IOException e) {
            }
        return nEncodedBytes;
    }
	
	
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		 showJFrame();
	}
	
	
	public static void showJFrame() {
		final AdvancedPlayer player = new AdvancedPlayer();
		JButton play = new JButton("Play");
		player.loadAudio();
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.startPlaying();
			}
		});
		final JButton pause = new JButton("Paused");
		pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		final JButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.stopPlaying();
			}
		});
		
		final JSlider slider = new JSlider();
		slider.setMaximum(player.getByteLength());
		slider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				double rate = 0;
				//System.out.println(slider.getValue());
				//long skipBytes =0;
				if(!slider.getValueIsAdjusting()) {
					
					//rate = slider.getValue() * 1.0 / 1000;
					//long skipBytes = (long) Math.round((Integer) player.getByteLength() * rate);
					//player.skipBytes(skipBytes);
					player.skipBytes(slider.getValue());
				}
			}
			
		});
		
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(play);
		frame.getContentPane().add(pause);
		frame.getContentPane().add(stop);
		frame.getContentPane().add(slider);
		frame.setVisible(true);
		frame.setSize(300, 200);
		
	}


	
	
	
}
