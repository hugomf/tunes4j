package org.ocelot.tunes4j.taggers;

import java.io.File;
import java.io.FilenameFilter;

import org.ocelot.tunes4j.dto.Song;

import eu.medsea.mimeutil.MimeUtil;


public abstract class AbstractID3Tagger {
	
	private RegistryTagger registry = null;
	private String strategy = "";
	
	private FilenameFilter filefilter = new FilenameFilter() {
		@Override
		public boolean accept(File file, String name) {
			boolean result = true;
			if (!file.isDirectory())  {
				if(name.endsWith(".mp3")) {
					result = MimeUtil.getMimeTypes(file).contains("audio/mpeg3");
				}
			}
			return result;
		}
    };
	
	public AbstractID3Tagger(String strategy) {
		this.registry = new RegistryTagger();
		this.strategy=strategy;
	}
	
	public void parseFolder(File sourceFolder){
		if(sourceFolder != null && strategy!=null && strategy.length() > 0){
			startParsing(sourceFolder);
		}
	}
	
	private void startParsing(File folder){
		if (folder.isDirectory()) {
			for (File file : folder.listFiles(filefilter)) {
				startParsing(file);
			}
		} else if (folder.isFile()) {
			Tagger tagger = registry.getInsance(this.strategy);
			fileTagged(tagger.parse(folder));
		}
	}
	
	public abstract void fileTagged(Song mp3FileBean);
	
}
