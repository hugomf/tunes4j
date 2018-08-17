package org.ocelot.tunes4j.effects.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EffectEventSupport<T> implements Serializable, EffectEventListener<T> {

	private static final long serialVersionUID = -2952544828712628520L;
	
	private List<EffectEventHandler<T>> list = new ArrayList<EffectEventHandler<T>>();

	public void fireUpdateEvent(UpdateValueEvent<T> event) {
		notifyUpdateValueListeners(event);
	}
	
	public void fireEffectCompletedListeners(EffectEvent event) {
		notifyEffectCompletedListeners(event);
	}

	public void notifyUpdateValueListeners(UpdateValueEvent<T> event) {
		for (EffectEventHandler<T> item : list) {
			item.updateValue(event);
		}
	}
	
	public void notifyEffectCompletedListeners(EffectEvent event) {
		for (EffectEventHandler<T> item : list) {
			item.effectCompleted(event);
		}
	}

	@Override
	public void addEffectEventListener(EffectEventHandler<T> handler) {
		list.add(handler);
	}

	@Override
	public void removeEffectUpdateEventListener(EffectEventHandler<T> handler) {
		list.remove(handler);
	}


}
