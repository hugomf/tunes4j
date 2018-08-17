package org.ocelot.tunes4j.taggers;


import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.farng.mp3.MP3File;
import org.ocelot.tunes4j.dto.Song;





public class JID3LibTaggerImpl implements Tagger {

	private Log logger = LogFactory.getLog(this.getClass());
	
	@Override
	public Song parse(File sourceFile) {
		Song mp3Bean = null;
		MP3File mp3file;
		mp3Bean = new Song();
    	mp3Bean.setPath(sourceFile.getParent());
		mp3Bean.setFileName(sourceFile.getName());
		try {
	    	mp3file = new MP3File(sourceFile);
	    	  if (mp3file.hasLyrics3Tag()) {
		    	mp3Bean.setArtist(mp3file.getLyrics3Tag().getLeadArtist());
				mp3Bean.setTitle(mp3file.getLyrics3Tag().getSongTitle());
				mp3Bean.setAlbum(mp3file.getLyrics3Tag().getAlbumTitle());
		    } else if (mp3file.hasID3v2Tag()) {
		    	mp3Bean.setArtist(mp3file.getID3v2Tag().getLeadArtist());
				mp3Bean.setTitle(mp3file.getID3v2Tag().getSongTitle());
				mp3Bean.setAlbum(mp3file.getID3v2Tag().getAlbumTitle());
		    } else if  (mp3file.hasID3v1Tag() ) {
		    	mp3Bean.setArtist(mp3file.getID3v1Tag().getArtist());
				mp3Bean.setTitle(mp3file.getID3v1Tag().getSongTitle());
				mp3Bean.setAlbum(mp3file.getID3v1Tag().getAlbum());
		    }
	    } catch (Exception e) {
	    	logger.debug("Error en el archivo: " + sourceFile.getPath(), e);
		}
		return mp3Bean;
	}

	@Override
	public void save(File sourceFile, Song bean) {  }

}
