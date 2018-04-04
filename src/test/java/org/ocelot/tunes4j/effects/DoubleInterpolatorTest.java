package org.ocelot.tunes4j.effects;

import java.util.ArrayList;
import java.util.List;

import org.ocelot.tunes4j.effects.events.EffectEvent;
import org.ocelot.tunes4j.effects.events.EffectEventHandler;
import org.ocelot.tunes4j.effects.events.UpdateValueEvent;

public class DoubleInterpolatorTest {

	
	public static void main(String[] args) {
		
		List<Double> list = new ArrayList<Double>();
		DoubleInterpolator test = new DoubleInterpolator();
		test.addEffectEventListener(new EffectEventHandler<Double>() {

			@Override
			public void updateValue(UpdateValueEvent<Double> event) {
				System.out.println(event.getValue());
				list.add(event.getValue());
			}

			@Override
			public void effectCompleted(EffectEvent event) {
				System.out.println(event.getMessage());
				System.out.println("items:" +  list.size());
			}
		});
		test.interpolate(1.8d, 3.5d, 100, 14);
	}

	

	
}
