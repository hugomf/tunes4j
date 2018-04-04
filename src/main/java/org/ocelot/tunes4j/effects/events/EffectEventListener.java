package org.ocelot.tunes4j.effects.events;

public interface EffectEventListener<T> {

	public void addEffectEventListener(EffectEventHandler<T> handler);

	public void removeEffectUpdateEventListener(EffectEventHandler<T> handler);
	
}
