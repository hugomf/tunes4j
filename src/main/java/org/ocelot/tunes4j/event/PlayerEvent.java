package org.ocelot.tunes4j.event;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;


public class PlayerEvent extends EventObject {
   
	public enum Type {
	    ON_PLAY, ON_PAUSE, ON_STOP, ON_SEEK, ON_OPEN, ON_CHANGED;
	}
	
	Type type;
	Object source;
	Map properties = null;
	
	public PlayerEvent(Type type, Object source) {
		super(source);
		properties = new HashMap();
		this.type = type;
	}
	
	public Type getType(){
		return type;
	}
	
	public void addProperty(String key, Object value) {
		properties.put(key , value);
	}

	public Object getProperty(String key) {
		return properties.get(key);
	}
	
	public int getPropertyAsInt(String key) {
		String prop = properties.get(key) + "";
		return Integer.parseInt(prop);
	}
	
}

