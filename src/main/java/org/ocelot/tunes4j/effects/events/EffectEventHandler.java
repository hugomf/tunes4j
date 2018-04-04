package org.ocelot.tunes4j.effects.events;

public interface EffectEventHandler<T> {
	
	public  void updateValue(UpdateValueEvent<T> event);

	public void effectCompleted(EffectEvent event);
	

}
