package org.ocelot.tunes4j.effects;

public class Interpolator {
	
	public static float linearInterpolation(float start, float end, int count, int x) {
		float value = start + x * (end - start) / count;
		value = Math.round(value * (float) count) / (float) count;
		return value;
	}

}
