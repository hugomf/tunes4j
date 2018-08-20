package org.ocelot.tunes4j.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ocelot.tunes4j.event.FileChangeEventListener;
import org.ocelot.tunes4j.gui.FolderMonitorService;

public class RecurseFilesProvider {

	public static CustomFileFilter filefilter = new CustomFileFilter();
	public FolderMonitorService monitor = new FolderMonitorService();
	
	public  void getFiles(File folder, List<File> files) {
		if (folder.isDirectory()) {
			monitor.subscribeFolder(folder);
			for (File file : folder.listFiles(filefilter)) {
				getFiles(file, files);
			}
		} else if (folder.isFile()) {
			files.add(folder);
		}
	}

	public  List<File> getFiles(List<File> sourceFileList) {
		List<File> files = new ArrayList<File>();
		for (File file : sourceFileList) {
			getFiles(file, files);
		}
		monitor.startAsync();
		return files;
	}
	
	public void addFileChangeListener(FileChangeEventListener listener) {
		this.monitor.addFileChangeListener(listener);
	}


}
