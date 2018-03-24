package org.ocelot.tunes4j.player;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.ocelot.tunes4j.event.PlayerEvent;
import org.ocelot.tunes4j.event.PlayerEventListener;
import org.ocelot.tunes4j.event.PlayerEvent.Type;
import org.tritonus.share.sampled.file.TAudioFileFormat;


public class MediaPlayer {

	public static int SKIP_INACCURACY_SIZE = 1200;
	
	public enum STATE {
		CLOSED, OPENED,PLAYING,PAUSED,SEEKING,STOPPED,UNKNOWN
	}
    
	protected EventListenerList listenerList;
	
	private AudioInputStream audioInputStream;
	
	private AudioFormat decodedFormat;
	
	private AudioFileFormat audioFileFormat;
	
	private AudioInputStream decodedInputStream;
	
	private SourceDataLine line;
	
	private Object sourceInputStream;
	
	private FloatControl gainControl; 
	
	private STATE currentState = STATE.CLOSED;
	
	private Object lock = new Object();
	
	private volatile boolean paused = false;
	
	private volatile Thread playerThread;
	
	protected int encodedLength;
	
	protected Long songDuration;
	
	protected Long seekPosition;
	
	public MediaPlayer() {
		audioInputStream = null;
		listenerList = new EventListenerList();
        reset();
	}
	
	public void pausePlayBack() {
		currentState = STATE.PAUSED;
	}

	public void resumePlayback() {
		synchronized (lock) {
			currentState = STATE.PLAYING;
			lock.notifyAll();
		}
	}

	public void openLine() throws LineUnavailableException {
		if (line != null) {
			if (!line.isOpen()) {
				line.open();
			}
		}
	}
	
	public void open(File sourceInputStream) {
		this.sourceInputStream =  sourceInputStream;
		open();
	}
	
	public void open(URL sourceInputStream) {
		this.sourceInputStream = sourceInputStream;
		open();
	}
	
	public void open() {
		try {
			reset();
			if (sourceInputStream instanceof URL){
				// Copy sourceInputStream
				
				
				audioInputStream = AudioSystem.getAudioInputStream((URL)sourceInputStream);
				audioFileFormat =  AudioSystem.getAudioFileFormat((URL) sourceInputStream);
			}
			if (sourceInputStream instanceof File) {
				
				//Copy Source Stream
/*				File input = (File) sourceInputStream;
				File output = new File("currentPlayback.mp3");
				FileUtils.copy(input, output);*/
				
				audioInputStream = AudioSystem.getAudioInputStream((File) sourceInputStream);
				audioFileFormat =  AudioSystem.getAudioFileFormat((File) sourceInputStream);
			}
			
			AudioFormat baseFormat = audioFileFormat.getFormat();
			decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);
			
			decodedInputStream = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);
			
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
			line = (SourceDataLine) AudioSystem.getLine(info);
			openLine();
			
			songDuration = (Long)((TAudioFileFormat) 
					audioFileFormat).properties().get("duration");

			//	encodedLength = (Integer)((TAudioFileFormat) 
			//	audioFileFormat).properties().get("mp3.length.frames") * (Integer)((TAudioFileFormat) 
			//				audioFileFormat).properties().get("mp3.framesize.bytes");
			
			
			encodedLength = audioInputStream.available();
			
			currentState = STATE.OPENED;
			if(line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
			}
			
			fireOnOpenEvent(new PlayerEvent(Type.ON_OPEN,this));
			
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	
    protected void reset()
    {
    	currentState = STATE.CLOSED;
        if (decodedInputStream != null)
        {
            synchronized (decodedInputStream) {
            	close(decodedInputStream);
            }
        }
        decodedInputStream = null;
        audioFileFormat = null;
        decodedFormat = null;
        encodedLength = -1;
        if (line != null) {
        	line.stop();
        	line.close();
        	line = null;
        }
        gainControl = null;
    }
	
	 /**
     * Returns true if Gain control is supported.
     */
    public boolean hasGainControl()
    {
        if (gainControl == null)
        {
            if ( (line != null) && (line.isControlSupported(FloatControl.Type.MASTER_GAIN))) { 
            	gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            }
        }
        return gainControl != null;
    }

    /**
     * Returns Gain value.
     */
    public float getGainValue() {
        if (hasGainControl()) {
            return gainControl.getValue();
        }
        else {
            return 0.0F;
        }
    }
    
    /**
     * Sets Gain value.
     * Line should be opened before calling this method.
     * Linear scale 0.0  <-->  1.0
     * Threshold Coef. : 1/2 to avoid saturation.
     */
    public void setGainValue(double fGain) {
        if (hasGainControl())
        {
            double minGainDB = getMinimumGain();
            double ampGainDB = ((10.0f / 20.0f) * getMaximumGain()) - getMinimumGain();
            double cste = Math.log(10.0) / 20;
            double valueDB = minGainDB + (1 / cste) * Math.log(1 + (Math.exp(cste * ampGainDB) - 1) * fGain);
            gainControl.setValue((float) valueDB);
        }
    }
    
    /**
     * Gets max Gain value.
     */
    public float getMaximumGain()
    {
        if (hasGainControl()) {
            return gainControl.getMaximum();
        }
        else {
            return 0.0F;
        }
    }

    /**
     * Gets min Gain value.
     */
    public float getMinimumGain() {
        if (hasGainControl()) {
            return gainControl.getMinimum();
        }
        else {
            return 0.0F;
        }
    }
    
	public int getLength() {
		 return encodedLength;
	}
	
	public int getSongDuration() {
		return songDuration.intValue();
	}
	
	public void playback(){
		currentState = STATE.PLAYING;
		playerThread = null;
		if (playerThread == null) {
			playerThread = new Thread() {
				@Override
				public void run() {
					try {
						startPlaying();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		}
		playerThread.start();
	}

	
	public void fireOnPlayEvent(Object e){
		//System.out.println("milliseconds(" + milliseconds + "/" + songDuration + ")" );
    	PlayerEvent onPlayEvent = new PlayerEvent(PlayerEvent.Type.ON_PLAY, e);
    	onPlayEvent.addProperty("song.bytelength", new Long(getEncodedStreamPosition()));
    	//onPlayEvent.addProperty("song.milliseconds",  new Long(milliseconds));
    	firePlayerEvents(onPlayEvent);
	}
	
	public void fireOnOpenEvent(Object e){
    	PlayerEvent onPlayEvent = new PlayerEvent(PlayerEvent.Type.ON_OPEN , e);
    	firePlayerEvents(onPlayEvent);
	}
	
	
	public void startPlaying() throws IOException {
		
		final Timer timer = new Timer (500, new ActionListener ()
		{
		    public void actionPerformed(ActionEvent e) {
		    	fireOnPlayEvent(e);
		    }
		}); 
		if(!isOpened()) {
			open();
		}
		if (line != null  && line.isOpen()) {
			byte[] data = new byte[4096];
			line.start();
			int nBytesRead = 0;
			timer.start();
			synchronized (lock) {
				Thread thisThread = Thread.currentThread();
				currentState = STATE.PLAYING;
				while (decodedInputStream!=null 
						&& nBytesRead != -1 && playerThread == thisThread) {
					nBytesRead = decodedInputStream.read(data,0,data.length);
					while (isPaused()) {
						if (line.isRunning()) {
							line.stop();
						}
						try {
							lock.wait();
						} catch (InterruptedException e) { }
					}
					if (!line.isRunning()) {
						line.start();
					}
					if(nBytesRead==-1) {
						nBytesRead=decodedInputStream.read(data, 0,
								data.length);
					}
					try {
						
						System.out.println("nBytesRead:" + nBytesRead);
						System.out.println("line:" + line);
						System.out.println("data:" + data);
						
						
						
						line.write(data, 0, nBytesRead);
					}catch(Exception e){
						timer.stop();
						System.out.println("nBytesRead:" + nBytesRead);
						System.out.println("line:" + line);
						System.out.println("data:" + data);
						reset();
						e.printStackTrace();
						//break;
					}
				}
				timer.stop();
			}
		} 
	}
	
	public void close(Object obj){
		try {
			if(obj!=null && obj instanceof AudioInputStream) {
				((AudioInputStream) obj).close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void stopPlaying(){
		 if (line!=null && (currentState == STATE.PLAYING) || (currentState == STATE.PAUSED)) {
			line.flush();
			line.stop();
			currentState = STATE.STOPPED;
			playerThread = null;
			synchronized (decodedFormat) {
				close(decodedFormat);
			}
			waitForFinish(800);
		 }
	}
	
	private void waitForFinish(long milliseconds){
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) { }
	}
	
	
	
	public long skipBytes(long bytes)
    {
        	long totalSkipped = 0;
            long skipped = 0;
                synchronized (audioInputStream) {
	        		totalSkipped = bytes - getEncodedStreamPosition();
	        		if(totalSkipped < 0) {
	        			reset();
	        			playback();
	        			waitForFinish(100);
	        			if(bytes > 0) {
	        				skipBytes(bytes);
	        			}
	        		}
	        		while(skipped < (totalSkipped-SKIP_INACCURACY_SIZE)) {
	        			try{
	        				skipped = audioInputStream.skip(totalSkipped);
	        			}catch(IOException e2){}
	        		}
                }
        return skipped;
    }
	
//	protected long getEncodedStreamPosition() {
//        long nEncodedBytes = -1;
//				nEncodedBytes = totalBytesRead;
//				System.out.println("Bytes " + nEncodedBytes + "/" + encodedLength + "read");
//		return nEncodedBytes;
//    }

	protected int getEncodedStreamPosition() {
        int nEncodedBytes = -1;
		try {
			if (audioInputStream != null) {
				nEncodedBytes = encodedLength - audioInputStream.available();
			}
		} catch (IOException e) {}
		return nEncodedBytes;
    }
	
	 // This methods allows classes to register for MyEvents
    public void addMyEventListener(PlayerEventListener listener) {
        listenerList.add(PlayerEventListener.class, listener);
    }
    // This methods allows classes to unregister for MyEvents
    public void removeMyEventListener(PlayerEventListener listener) {
        listenerList.remove(PlayerEventListener.class, listener);
    }
	// Fire Events
    void firePlayerEvents(PlayerEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==PlayerEventListener.class) {
            	switch (evt.getType()){
					case ON_OPEN:
						((PlayerEventListener)listeners[i+1]).fireOnOpenEvent(evt);break;
					case ON_PLAY:
						((PlayerEventListener)listeners[i+1]).fireOnPlayEvent(evt);break;
					case ON_STOP:
						((PlayerEventListener)listeners[i+1]).fireOnStopEvent(evt);break;
					case ON_PAUSE:
						((PlayerEventListener)listeners[i+1]).fireOnPauseEvent(evt);break;
					case ON_SEEK:
						((PlayerEventListener)listeners[i+1]).fireOnSeekEvent(evt);break;
				}
            }
        }
    }


	public static void main(String[] args) {
		final MediaPlayer player = new MediaPlayer();
		
		
		//URL url = JLayerPlayer.class.getResource("../resources/amiga.mp3");
		//player.open(url);
		
		player.open(new File("/Users/hugo/eclipse-workspace2/tunes4j/src/main/resources/amiga.mp3"));
		
		JFrame frame = new JFrame("Player");
		JButton play = new JButton("Play");
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.playback();
			}
		});
		final JButton pause = new JButton("Paused");
		pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (player.isPaused()) {
					pause.setText("Playing");
					player.resumePlayback();
				} else {
					pause.setText("Paused");
					player.pausePlayBack();
				}
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
		slider.setMaximum(player.getLength());

		player.addMyEventListener(new PlayerEventListener() {
			@Override
			public void fireOnPlayEvent(PlayerEvent evt) {
				slider.setValueIsAdjusting(true);
				slider.setValue(evt.getPropertyAsInt("song.bytelength"));
			}

		});
		
		slider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				if(!slider.getValueIsAdjusting()) {
					player.skipBytes(slider.getValue());
				}
			}
		});
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(play);
		frame.getContentPane().add(pause);
		frame.getContentPane().add(stop);
		frame.getContentPane().add(slider);
		frame.setVisible(true);
		frame.setSize(300, 100);
	}

	

	public boolean isPaused() {
		return currentState == STATE.PAUSED;
	}
	
	public boolean isClosed() {
		return currentState == STATE.CLOSED;
	}
	
	public boolean isOpened() {
		return currentState == STATE.OPENED;
	}

	public boolean isPlaying() {
		return currentState == STATE.PLAYING;
	}

	public boolean isSeeking() {
		return currentState == STATE.SEEKING;
	}
	
	public boolean isStoped() {
		return currentState == STATE.STOPPED;
	}

	
}
