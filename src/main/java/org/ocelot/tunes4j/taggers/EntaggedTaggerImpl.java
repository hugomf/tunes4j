package org.ocelot.tunes4j.taggers;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ocelot.tunes4j.dto.Song;

import entagged.audioformats.AudioFile;
import entagged.audioformats.AudioFileIO;
import entagged.audioformats.Tag;
import entagged.audioformats.exceptions.CannotReadException;

public class EntaggedTaggerImpl implements Tagger {

	private Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public Song parse(File sourceFile) {
		Song mp3Bean = null;
		AudioFile mp3file;
		try {
			mp3Bean = new Song();
			mp3file = AudioFileIO.read(sourceFile);
			Tag tag = mp3file.getTag();
			
			mp3Bean.setPath(sourceFile.getParent());
			mp3Bean.setFileName(sourceFile.getName());
			mp3Bean.setArtist(tag.getFirstArtist());
			mp3Bean.setTitle(tag.getFirstTitle());
			mp3Bean.setAlbum(tag.getFirstAlbum());

		} catch (CannotReadException e) {
			log.error("Error en el archivo: " + sourceFile.getPath());
			e.printStackTrace();
		} 
		return mp3Bean;
	}

	@Override
	public void save(File sourceFile, Song bean) {
		// TODO Auto-generated method stub
		
	}


}
