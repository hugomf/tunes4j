package org.ocelot.tunes4j.service;

import static com.barbarysoftware.watchservice.StandardWatchEventKind.ENTRY_CREATE;
import static com.barbarysoftware.watchservice.StandardWatchEventKind.ENTRY_DELETE;
import static com.barbarysoftware.watchservice.StandardWatchEventKind.ENTRY_MODIFY;
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;

import org.ocelot.tunes4j.event.FileChangeEvent;
import org.ocelot.tunes4j.event.FileChangeEventListener;
import org.ocelot.tunes4j.event.FileChangeEventNotifier;
import org.slf4j.LoggerFactory;

import com.barbarysoftware.watchservice.WatchEvent;
import com.barbarysoftware.watchservice.WatchKey;
import com.barbarysoftware.watchservice.WatchService;
import com.barbarysoftware.watchservice.WatchableFile;
import com.google.common.util.concurrent.AbstractExecutionThreadService;

import ch.qos.logback.classic.Logger;


public class FolderMonitorForMacService  extends AbstractExecutionThreadService implements FolderWatcherService {

	private static Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FolderMonitorForMacService.class);

	private WatchService folderWatcher = WatchService.newWatchService();
	
	private FileChangeEventNotifier notifier;
	
	private WatchableFile watchedFolder;
	
	public void addFileChangeListener(FileChangeEventListener listener) {
		notifier.registerListener(listener);
	}

	public void removeFileChangeListener(FileChangeEventListener listener) {
		notifier.unregisterListener(listener);
	}
	
	public void subscribeFolder(File folder) {
		try {
			this.watchedFolder = new WatchableFile(folder);
			logger.info(watchedFolder.toString());
			this.watchedFolder.register(folderWatcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

		} catch (IOException e) {
			String msg = String.format("Error while subscribing folder: %s", folder.getPath());
			logger.error(msg, e);
		}
	}

	public FolderMonitorForMacService() {
		try {
			this.folderWatcher = WatchService.newWatchService();
			this.notifier = new FileChangeEventNotifier();
		} catch (Exception e) {	
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
		File file = (File) event.context();
		if (ENTRY_CREATE == event.kind()) {
			this.notifier.notifyListeners(new FileChangeEvent(FileChangeEvent.Type.ON_ADDNEW, file));
		}
		if (ENTRY_DELETE == event.kind()) {
			this.notifier.notifyListeners(new FileChangeEvent(FileChangeEvent.Type.ON_DELETE, file));
		}
		if (ENTRY_MODIFY == event.kind()) {
			this.notifier.notifyListeners(new FileChangeEvent(FileChangeEvent.Type.ON_CHANGE, file));
		}
	}
	
	
	public static void main(String[] args) {

		FolderMonitorForMacService monitor = new FolderMonitorForMacService();
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
