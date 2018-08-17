package org.ocelot.tunes4j.taggers;

import java.util.HashMap;
import java.util.Map;

public class RegistryTagger {
	
	private Map map = new HashMap();
	
	public void registerAll() {
		register("org.ocelot.tunes4j.taggers.JAudioTaggerImpl");
		register("org.ocelot.tunes4j.taggers.JID3TaggerImpl");
		register("org.ocelot.tunes4j.taggers.EntaggedTaggerImpl");
		register("org.ocelot.tunes4j.taggers.Mp3agicTaggerImpl");
	}
	
	public void register(String classFullName){
		try {
			if (!map.containsKey(classFullName)) {
				map.put(classFullName, Class.forName(classFullName).newInstance());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public Tagger getInsance(String classFullName) {
		register(classFullName);
		return (Tagger) map.get(classFullName);
	}

}
