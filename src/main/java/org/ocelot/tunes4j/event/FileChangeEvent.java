package org.ocelot.tunes4j.event;

import java.io.File;
import java.util.EventObject;

@SuppressWarnings("serial")
public class FileChangeEvent extends EventObject {
	
	public enum Type {
	    ON_ADDNEW, ON_DELETE, ON_CHANGE;
	}
	
	private String message;
	private Type type;
	private File file;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public FileChangeEvent(Type type, File file) {
		super(file);
		this.type = type;
		this.file = file;
	}

	public Type getType() {
		return type;
	}
	
	public File getFile() {
		return file;
	} 
}
