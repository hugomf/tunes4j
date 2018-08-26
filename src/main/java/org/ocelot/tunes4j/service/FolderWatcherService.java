package org.ocelot.tunes4j.service;

import java.io.File;

import org.ocelot.tunes4j.event.FileChangeEventListener;

import com.google.common.util.concurrent.Service;

public interface FolderWatcherService {

	public void subscribeFolder(File folder);
	
	public void addFileChangeListener(FileChangeEventListener listener);
	
	public void removeFileChangeListener(FileChangeEventListener listener);

	public Service startAsync();
	
}
