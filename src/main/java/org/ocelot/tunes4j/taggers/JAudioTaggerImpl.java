package org.ocelot.tunes4j.taggers;


import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.ocelot.tunes4j.dto.Song;



public class JAudioTaggerImpl implements Tagger {

	private Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public Song parse(File sourceFile) {
		Song mp3Bean = null;
		MP3File mp3file;
		try {
			mp3file = (MP3File) AudioFileIO.read(sourceFile);
			mp3Bean = new Song();
			mp3Bean.setPath(sourceFile.getParent());
			mp3Bean.setFileName(sourceFile.getName());
			
			
			if(mp3file.hasID3v2Tag()) {
				mp3Bean.setArtist(mp3file.getID3v2Tag().getFirst(FieldKey.ARTIST));
				mp3Bean.setTitle(mp3file.getID3v2Tag().getFirst(FieldKey.TITLE));;
				mp3Bean.setAlbum(mp3file.getID3v2Tag().getFirst(FieldKey.ALBUM));
			}
			else if(mp3file.hasID3v1Tag()) {
				mp3Bean.setArtist(mp3file.getID3v1Tag().getFirst(FieldKey.ARTIST));
				mp3Bean.setTitle(mp3file.getID3v1Tag().getFirstTitle() );
				mp3Bean.setAlbum(mp3file.getID3v1Tag().getFirst(FieldKey.ALBUM));
			}
		} catch (CannotReadException e) {
			log.error("Error en el archivo: " + sourceFile.getPath());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Error en el archivo: " + sourceFile.getPath());
			e.printStackTrace();
		} catch (TagException e) {
			log.error("Error en el archivo: " + sourceFile.getPath());
			e.printStackTrace();
		} catch (ReadOnlyFileException e) {
			log.error("Error en el archivo: " + sourceFile.getPath());
			e.printStackTrace();
		} catch (InvalidAudioFrameException e) {
			log.error("Error en el archivo: " + sourceFile.getPath());
			e.printStackTrace();
		}
		return mp3Bean;
	}

	@Override
	public void save(File sourceFile, Song bean) {
		
	}

}
