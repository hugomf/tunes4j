package org.ocelot.tunes4j.utils;

import java.io.File;
import java.io.FileFilter;


public class CustomFileFilter implements FileFilter {
	@Override
	public boolean accept(File file) {
		if (file.isDirectory() || file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
			return true;
		}
		return false;
	}
}