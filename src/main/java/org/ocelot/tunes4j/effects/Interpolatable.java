package org.ocelot.tunes4j.effects;

public interface  Interpolatable<T> {
	
	public void interpolate(T from, T to, int interpolations, int delay);

	
}
