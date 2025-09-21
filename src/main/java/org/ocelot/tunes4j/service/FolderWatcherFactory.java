package org.ocelot.tunes4j.service;

public class FolderWatcherFactory {
   
	
    public static FolderWatcherService getInstance() {
        // Use Java NIO WatchService for all platforms - it's built-in and supports ARM64
        return new JavaNioFolderMonitorService();
    }
}
