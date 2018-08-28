package org.ocelot.tunes4j.service;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.ocelot.tunes4j.event.FileChangeEvent;
import org.ocelot.tunes4j.event.FileChangeEventListener;
import org.ocelot.tunes4j.event.FileChangeEventNotifier;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractExecutionThreadService;

import ch.qos.logback.classic.Logger;


public class FolderMonitorService  extends AbstractExecutionThreadService implements FolderWatcherService {

	private static Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FolderMonitorService.class);

	private WatchService folderWatcher;
	
	private FileChangeEventNotifier notifier;
	
	private Path watchedFolder;
	
	public void addFileChangeListener(FileChangeEventListener listener) {
		notifier.registerListener(listener);
	}

	public void removeFileChangeListener(FileChangeEventListener listener) {
		notifier.unregisterListener(listener);
	}
	
	public void subscribeFolder(File folder) {
		try {
			this.watchedFolder = folder.toPath();
			logger.info(watchedFolder.toAbsolutePath().toString());
			watchedFolder.register(folderWatcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);

		} catch (IOException e) {
			String msg = String.format("Error while subscribing folder: %s", folder.getPath());
			logger.error(msg, e);
		}
	}

	public FolderMonitorService() {
		try {
			this.folderWatcher = FileSystems.getDefault().newWatchService();
			this.notifier = new FileChangeEventNotifier();
		} catch (IOException e) {
			logger.error("error in the FolderMonitor occurred:", e);
		}
	}

	@Override
	public void run() {
		while(isRunning()) {
			boolean complete = false;
			try {
				while(!complete) {
					WatchKey key = folderWatcher.take();
					for (final WatchEvent<?> event: key.pollEvents()) {
						syncFiles(event, key);
					}
					complete = !key.reset();
				}
				
			} catch (InterruptedException e) {
				logger.error("error in the startMonitor occurred:", e);
			} 	
		}
	}

	private void syncFiles(WatchEvent<?> event, WatchKey key) {
		Path path = (Path) event.context();
		File file = watchedFolder.resolve(path).toFile();
		
		if (StandardWatchEventKinds.ENTRY_CREATE == event.kind()) {
			this.notifier.notifyListeners(new FileChangeEvent(FileChangeEvent.Type.ON_ADDNEW, file));
		}
		if (StandardWatchEventKinds.ENTRY_DELETE == event.kind()) {
			this.notifier.notifyListeners(new FileChangeEvent(FileChangeEvent.Type.ON_DELETE, file));
		}
		if (StandardWatchEventKinds.ENTRY_MODIFY == event.kind()) {
			this.notifier.notifyListeners(new FileChangeEvent(FileChangeEvent.Type.ON_CHANGE, file));
		}
	}
	
	
	public static void main(String[] args) {

		FolderMonitorService monitor = new FolderMonitorService();
		monitor.subscribeFolder(new File("/Users/hugo/Documents/prueba"));
		
		monitor.addFileChangeListener(new FileChangeEventListener() {
			
			@Override
			public void triggerOnAddNewFileEvent(FileChangeEvent event) {
				logger.info(format("Event Add New:%s", event));
			}
			
			@Override
			public void triggerOnChangeFileEvent(FileChangeEvent event) {
				logger.info(format("Event Change:%s", event));
			}
			
			@Override
			public void triggerOnDeleteFileEvent(FileChangeEvent event) {
				logger.info(format("Event Delete:%s", event));
			}
		
		});
		
		monitor.startAsync();
		
	}

	

}
