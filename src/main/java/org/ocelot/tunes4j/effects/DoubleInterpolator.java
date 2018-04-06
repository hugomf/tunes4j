package org.ocelot.tunes4j.effects;

import org.ocelot.tunes4j.effects.events.EffectEvent;
import org.ocelot.tunes4j.effects.events.EffectEventHandler;
import org.ocelot.tunes4j.effects.events.EffectEventListener;
import org.ocelot.tunes4j.effects.events.EffectEventSupport;
import org.ocelot.tunes4j.effects.events.UpdateValueEvent;

public class DoubleInterpolator implements Interpolatable<Double>, EffectEventListener<Double> {
	
	private EffectEventSupport<Double> ues = new EffectEventSupport<Double>();
	
	@Override
	public void addEffectEventListener(EffectEventHandler<Double> handler) {
		ues.addEffectEventListener(handler);
		
	}
	
	@Override
	public void removeEffectUpdateEventListener(EffectEventHandler<Double> handler) {
		ues.addEffectEventListener(handler);
	}

	@Override
	public void interpolate(Double from, Double to, int interpolations, int delay) {
		double value = from;
		while(true) {
			double step = calculateStep(from, to, interpolations);
			value = value + step;
			if (value > to) {
				this.ues.fireEffectCompletedListeners(new EffectEvent(this,"completed"));
				break;
			}
			this.ues.fireUpdateEvent(new UpdateValueEvent<Double>(this, value));
			sleep(delay);
		}
	}

	private double calculateStep(Double from, Double to, int interpolations) {
		double resta = to - from;
		return resta / (double) interpolations;
	}
	
	private void sleep(int delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}




}
