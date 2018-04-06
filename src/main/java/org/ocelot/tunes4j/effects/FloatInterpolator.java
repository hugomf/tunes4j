package org.ocelot.tunes4j.effects;

import java.util.function.Consumer;

import org.ocelot.tunes4j.utils.GUIUtils;

public class FloatInterpolator {
	
	private int delay;
	
	
	public void interpolate(Consumer<Float> triggerValue, float from, float to, int interpolations) {
		float value = from;
		while(value <= to) {
			triggerValue.accept(value);
			value = linearInterpolation(from, to, interpolations, value);
			GUIUtils.sleep(this.delay);
		}
	}

	private float linearInterpolation(float from, float to, int interpolations, float value) {
		float step = calculateStep(from, to, interpolations);
		value =  value + step;
		value =  Math.round(value * (float) interpolations) / (float) interpolations;
		return value;
	}

	private float calculateStep(float from, float to, int interpolations) {
		float resta = to - from;
		return resta / (float) interpolations;
	}
	

}
