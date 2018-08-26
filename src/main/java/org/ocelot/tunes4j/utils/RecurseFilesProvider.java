package org.ocelot.tunes4j.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;


@Component
public class RecurseFilesProvider {

	public static CustomFileFilter filefilter = new CustomFileFilter();

	public static void getFiles(File folder, List<File> files) {
		if (folder.isDirectory()) {
			for (File file : folder.listFiles(filefilter)) {
				getFiles(file, files);
			}
		} else if (folder.isFile()) {
			files.add(folder);
		}
	}

	public static List<File> getFiles(List<File> sourceFileList) {
		List<File> files = new ArrayList<File>();
		for (File file : sourceFileList) {
			getFiles(file, files);
		}
		return files;
	}
	

}
