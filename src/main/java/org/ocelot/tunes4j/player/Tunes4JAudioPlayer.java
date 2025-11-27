package org.ocelot.tunes4j.player;

import java.io.File;
import java.util.Map;

import org.ocelot.tunes4j.event.PlayProgressEvent;
import org.ocelot.tunes4j.event.ProgressUpdateListener;
import org.ocelot.tunes4j.dsp.KJDigitalSignalProcessor;
import org.ocelot.tunes4j.gui.JPanelSpectrum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

public class Tunes4JAudioPlayer implements BasicPlayerListener {
	
	
	private static Logger logger = LoggerFactory.getLogger(Tunes4JAudioPlayer.class);

	public static final int STATE_UNSTARTED = 0;

	public static final int STATE_RUNNING = 1;

	public static final int STATE_STOPPED = 2;

	public static final int STATE_SUSPENDED = 3;

	private int runningState = STATE_UNSTARTED;

	private BasicPlayer player;

	private ProgressUpdateListener listener;

	private BasicController control;

	private Map<String, Object> properties;

	/** DSP bridge for audio visualization */
	private DSPBridge dspBridge;

	/** Direct reference to spectrum panel for raw audio processing */
	private JPanelSpectrum spectrumPanel;

	/** Last known running state */
	private int currentRunningState = STATE_UNSTARTED;

	public Tunes4JAudioPlayer() {
		player = new BasicPlayer();
		dspBridge = new DSPBridge();
	}

	public void open(File file) {
		control = (BasicController) player;
		player.addBasicPlayerListener(this);
		try {
			control.open(file);
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		try {
			player.seek(0);
			player.stop();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}

	public void play() {
		try {
			control.play();

		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		try {
			player.pause();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}

	public void resume() {
		try {
			player.resume();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}

	public void skip(int ms) {
		try {

			int totalBytes = (int) this.properties.get("mp3.length.bytes");
			double rate = ms * 1.0 / 1000;
			long skipBytes = Math.round(totalBytes * rate) - 245000L;

			player.seek(skipBytes);
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		try {
			player.stop();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}

	public void setGain(double newGain) {
		try {
			player.setGain(newGain);
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void opened(Object stream, Map properties) {
		logger.info("opened : " + properties.toString());
		this.properties = (Map<String, Object>) properties;
	}

	
	public Map getProperties() {
		return this.properties;
	}

	@Override
	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
		PlayProgressEvent event = new PlayProgressEvent(Long.valueOf(bytesread));
		listener.updateProgress(event);

		// Feed audio data directly to spectrum visualizer
		if (spectrumPanel != null && pcmdata != null && pcmdata.length > 0) {
			spectrumPanel.processAudioData(pcmdata);
		}

		// Feed audio data to DSP visualization pipeline (legacy)
		if (dspBridge != null && pcmdata != null && pcmdata.length > 0) {
			dspBridge.feedAudioData(pcmdata);
		}
	}

	public void stateUpdated(BasicPlayerEvent event) {
		setRunningState(event);
	}

	public void setController(BasicController controller) {   }

	public void setRunningState(BasicPlayerEvent event) {

		int code = event.getCode();
		int oldState = runningState;

		switch (code) {
		case BasicPlayerEvent.OPENED:
			runningState = STATE_UNSTARTED;
			break;
		case BasicPlayerEvent.PLAYING:
			runningState = STATE_RUNNING;
			break;
		case BasicPlayerEvent.RESUMED:
			runningState = STATE_RUNNING;
			break;
		case BasicPlayerEvent.PAUSED:
			runningState = STATE_SUSPENDED;
			break;
		case BasicPlayerEvent.STOPPED:
			runningState = STATE_STOPPED;
			break;
		default:
			runningState = STATE_UNSTARTED;
		}

		// If state changed to paused or stopped, reset spectrum bars to 0
		if ((code == BasicPlayerEvent.PAUSED || code == BasicPlayerEvent.STOPPED) &&
			oldState == STATE_RUNNING) {
			if (spectrumPanel != null) {
				spectrumPanel.resetPeaks();
			}
		}
	}

	public int getCurrentStatus() {
		return runningState;
	}

	public void addProgressUpdateListener(ProgressUpdateListener progressUpdateListener) {
		this.listener = progressUpdateListener;
	}

	public boolean isClosed() {
		return runningState == Tunes4JAudioPlayer.STATE_UNSTARTED || runningState == Tunes4JAudioPlayer.STATE_STOPPED;
	}

	public boolean isPlaying() {
		return runningState == Tunes4JAudioPlayer.STATE_RUNNING;
	}

	public boolean isPaused() {
		return runningState == Tunes4JAudioPlayer.STATE_SUSPENDED;
	}

	/**
	 * Add a DSP processor for audio visualization
	 */
	public void addDSPProcessor(KJDigitalSignalProcessor processor) {
		if (dspBridge != null) {
			dspBridge.addProcessor(processor);
		}
	}

	/**
	 * Remove a DSP processor
	 */
	public void removeDSPProcessor(KJDigitalSignalProcessor processor) {
		if (dspBridge != null) {
			dspBridge.removeProcessor(processor);
		}
	}

	/**
	 * Set the spectrum panel for direct audio data processing
	 */
	public void setSpectrumPanel(JPanelSpectrum spectrumPanel) {
		this.spectrumPanel = spectrumPanel;
	}

	/**
	 * Get the DSP bridge (for advanced configuration)
	 */
	public DSPBridge getDSPBridge() {
		return dspBridge;
	}
}
