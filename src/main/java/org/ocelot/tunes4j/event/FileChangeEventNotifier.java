package org.ocelot.tunes4j.event;

import java.util.List;

import com.google.common.collect.Lists;


public class FileChangeEventNotifier {
	
	private List<FileChangeEventListener> fileChangeListeners = Lists.newArrayList();
	
	public void notifyListeners(FileChangeEvent event){
		for (FileChangeEventListener listener : fileChangeListeners) {
			if (event.getType() == FileChangeEvent.Type.ON_ADDNEW) {
				listener.triggerOnAddNewFileEvent(event);
			}
			if (event.getType() == FileChangeEvent.Type.ON_DELETE) {
				listener.triggerOnDeleteFileEvent(event);
			}
			if (event.getType() == FileChangeEvent.Type.ON_CHANGE) {
				listener.triggerOnChangeFileEvent(event);
			}
		}
	}
	
	public void registerListener(FileChangeEventListener listener) {
		if (this.fileChangeListeners.contains(listener)) return;
		this.fileChangeListeners.add(listener);
	}
	
	public void unregisterListener(FileChangeEventListener listener) {
		this.fileChangeListeners.remove(listener);
	}
	
	
}
