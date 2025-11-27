package org.ocelot.tunes4j.player;

import java.io.File;
import java.util.Map;

import org.ocelot.tunes4j.event.PlayProgressEvent;
import org.ocelot.tunes4j.event.ProgressUpdateListener;
import org.ocelot.tunes4j.dsp.Equalizer;
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

	/** Professional 10-band audio equalizer */
	private Equalizer equalizer;

	/** Last known running state */
	private int currentRunningState = STATE_UNSTARTED;

	/** Process count for debug logging */
	private int processCount = 0;

	public Tunes4JAudioPlayer() {
		player = new BasicPlayer();
		dspBridge = new DSPBridge();

		// Initialize equalizer for real-time audio processing
		equalizer = new Equalizer(44100.0, 2, 4096); // 44.1kHz, stereo, standard buffer
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

		// ORIGINAL AUDIO FOR SPEAKERS (BasicPlayer handles this directly)
		byte[] processedPcmData = pcmdata;

		// EQUALIZER PROCESSING ONLY FOR VISUALIZATION (current limitation)
		// ⚠️  BasicPlayer plays original PCM directly to speakers
		// ⚠️  Our equalizer only affects spectrum visualization
		// This is a fundamental architectural limitation
		if (equalizer != null && pcmdata != null && pcmdata.length > 0) {
			// Convert byte[] to double[] for equalizer processing
			double[] samples = byteArrayToDoubleArray(pcmdata);

			// Apply equalizer processing
			if (samples != null) {
				equalizer.processSamples(samples, samples); // In-place processing

				// Convert back to byte[] for spectrum processing only
				processedPcmData = doubleArrayToByteArray(samples);

				// Debug: Log current preset and gains occasionally
				processCount++;
				if (processCount % 100 == 0) { // Every 100th buffer to avoid spam
					String preset = equalizer.getCurrentPreset();
					System.out.printf("VISUAL EQ - Preset: %s (Bass: %.1f, Treble: %.1f)\n",
						preset,
						(equalizer.getBandGain(0) + equalizer.getBandGain(1)) / 2.0,
						(equalizer.getBandGain(5) + equalizer.getBandGain(6) + equalizer.getBandGain(7)) / 3.0);
				}
			}
		}

		// Feed processed audio data to spectrum visualizer only
		if (spectrumPanel != null && processedPcmData != null && processedPcmData.length > 0) {
			spectrumPanel.processAudioData(processedPcmData);
		}

		// Feed processed audio data to DSP visualization pipeline (legacy)
		if (dspBridge != null && processedPcmData != null && processedPcmData.length > 0) {
			dspBridge.feedAudioData(processedPcmData);
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

	/**
	 * Get the equalizer for audio processing controls
	 */
	public Equalizer getEqualizer() {
		return equalizer;
	}

	/**
	 * Set equalizer band gain (0-9 bands, -20 to +20 dB)
	 */
	public void setEqualizerBandGain(int band, double gain) {
		if (equalizer != null) {
			equalizer.setBandGain(band, gain);
		}
	}

	/**
	 * Get equalizer band gain (0-9 bands)
	 */
	public double getEqualizerBandGain(int band) {
		return equalizer != null ? equalizer.getBandGain(band) : 0.0;
	}

	/**
	 * Load an equalizer preset
	 */
	public void loadEqualizerPreset(String presetName) {
		if (equalizer != null) {
			equalizer.loadPreset(presetName);
		}
	}

	/**
	 * Get current equalizer preset name
	 */
	public String getEqualizerPreset() {
		return equalizer != null ? equalizer.getCurrentPreset() : "Flat";
	}

	/**
	 * Get available equalizer preset names
	 */
	public String[] getEqualizerPresetNames() {
		return equalizer != null ? equalizer.getPresetNames() : new String[]{"Flat"};
	}

	/**
	 * Convert 16-bit PCM byte array to double array for DSP processing
	 */
	private double[] byteArrayToDoubleArray(byte[] pcmData) {
		if (pcmData == null || pcmData.length == 0 || pcmData.length % 4 != 0) {
			return null;
		}

		int sampleCount = pcmData.length / 4; // Stereo 16-bit = 4 bytes per sample
		double[] samples = new double[sampleCount];

		for (int i = 0; i < sampleCount; i++) {
			int sampleIndex = i * 4;

			// Read 16-bit little-endian stereo (same conversion as in SpectrumProcessor)
			int left = (pcmData[sampleIndex + 1] << 8) | (pcmData[sampleIndex] & 0xFF);
			int right = (pcmData[sampleIndex + 3] << 8) | (pcmData[sampleIndex + 2] & 0xFF);

			// Convert to float (-1.0 to 1.0)
			left = Math.max(-32768, Math.min(32767, left));
			right = Math.max(-32768, Math.min(32767, right));

			// Average left and right channels for mono processing
			samples[i] = ((float)(left + right) / 2.0f) / 32768.0f;
		}

		return samples;
	}

	/**
	 * Convert double array back to 16-bit PCM byte array
	 */
	private byte[] doubleArrayToByteArray(double[] samples) {
		if (samples == null || samples.length == 0) {
			return null;
		}

		int byteCount = samples.length * 4; // Stereo output: 4 bytes per sample
		byte[] pcmData = new byte[byteCount];

		for (int i = 0; i < samples.length; i++) {
			int sampleIndex = i * 4;

			// Convert double back to 16-bit int (clipping protection)
			int sampleValue = (int)(samples[i] * 32767.0);
			sampleValue = Math.max(-32768, Math.min(32767, sampleValue));

			// Write as stereo (duplicate mono to both channels)
			pcmData[sampleIndex] = (byte)(sampleValue & 0xFF);     // Left LSB
			pcmData[sampleIndex + 1] = (byte)((sampleValue >> 8) & 0xFF); // Left MSB
			pcmData[sampleIndex + 2] = (byte)(sampleValue & 0xFF);     // Right LSB
			pcmData[sampleIndex + 3] = (byte)((sampleValue >> 8) & 0xFF); // Right MSB
		}

		return pcmData;
	}
}
