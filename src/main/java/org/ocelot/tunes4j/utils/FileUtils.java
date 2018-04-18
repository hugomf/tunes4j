package org.ocelot.tunes4j.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.Files;

public class FileUtils {
	
	public static CustomFileFilter filefilter = new CustomFileFilter();
	
	public static void copy(File s, File t) throws IOException {
		Files.copy(s, t);
	}
	
	public static String getFileNameWithoutExtension(String fileName) {
		int whereDot = fileName.lastIndexOf('.');
		if (0 < whereDot && whereDot <= fileName.length() - 2 ) {
			return fileName.substring(0, whereDot);
		} 
		return "";
	}
	
	/**
	 * Recursive function that gets Media Files from the FileSystem
	 * @param folder
	 * @param list
	 */
	public static void  getFiles(File folder, List<File> list){
		if (folder.isDirectory()) {
			for (File file : folder.listFiles(filefilter)) {
				getFiles(file,list);
			}
		} else if (folder.isFile()) {
				list.add(folder);
		}
	}
	
	public static List<File> getFiles( List<File> sourceFileList) {
		List<File> files = new ArrayList<File>();
		for (File file : sourceFileList) {
			getFiles(file, files);
		}
		return files;
	}
	
	public static URL getUrl(String resourcePath) {
		URL imgURL = ResourceLoader.class.getResource(resourcePath);
        if(imgURL != null) {
	        	return imgURL;
        }
	    throw new IllegalArgumentException("Image Resource not Found = " + resourcePath);
	}
	
	public static boolean deleteFile(String filePath, String fileName) {
		File file = new File(filePath + System.getProperty("file.separator") + fileName);
		return file.delete();
	}

	public static boolean createEmptyFile(String path, String fileName)  {
		new File(path).mkdirs();
		try {
			return new File(path, fileName).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}	

	private static File[] getFiles(File fromPath, String fileName) {
		
		File[] matches = fromPath.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().contains(fileName);
			}
		});
		return matches;
	}
	
	private static File[] findFiles(File path, String fileName) {
	    for(File file: path.listFiles()){
	        if(file.isDirectory()){
	        		return findFiles(file, fileName);
	        }
	    }
	    if(path.isDirectory()){
	        return getFiles(path, fileName);
	    }
	    return null;
	}
	
	public static String[] findFiles(String path, String fileName) {

		File[] files  = findFiles(new File(path), fileName);
		String[] filesPath = new String[files.length];
		int index = 0;
		for (File file : files) {
			filesPath[index] = file.getAbsolutePath();
			System.out.println(filesPath[index]);
			index++;
		}
		return filesPath;
	}
	
}
