package org.ocelot.tunes4j.service;

import org.apache.commons.lang.SystemUtils;


public class FolderWatcherFactory {
   
	
    public static FolderWatcherService getInstance() {

    	if (SystemUtils.IS_OS_WINDOWS) {
    		return new FolderMonitorService();
    	} else {
    		return new FolderMonitorForMacService();
    	} 	
	
	}
}
