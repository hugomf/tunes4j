package org.ocelot.tunes4j.taggers;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.utils.FileUtils;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import ch.qos.logback.classic.Logger;

public class Mp3agicTaggerImpl implements Tagger {
	
	private static Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Mp3agicTaggerImpl.class);

	@Override
	public Song parse(File sourceFile) {

		Song song = new Song();
		song.setPath(sourceFile.getParent());
		song.setFileName(sourceFile.getName());
		song.setTitle(Files.getNameWithoutExtension(sourceFile.getName()));
		
		try {
			Mp3File mp3file = new Mp3File(sourceFile);


			if (mp3file.hasId3v1Tag()) {
				ID3v1 id3v1Tag = mp3file.getId3v1Tag();
				song.setTrackNumber(id3v1Tag.getTrack());
				song.setArtist(id3v1Tag.getArtist());
				song.setTitle(id3v1Tag.getTitle());
				song.setAlbum(id3v1Tag.getAlbum());
				song.setYear(id3v1Tag.getYear());
				song.setGenre(id3v1Tag.getGenreDescription());
			}

			if (mp3file.hasId3v2Tag()) {
				
				ID3v2 id3v2Tag = mp3file.getId3v2Tag();
				byte[] imageData = id3v2Tag.getAlbumImage();
				
				if (imageData != null) {
					String mimeType = id3v2Tag.getAlbumImageMimeType();
					song.setArtWork(imageData);
					song.setArtMimeType(mimeType);
				}

				song.setTrackNumber(id3v2Tag.getTrack());
				song.setArtist(id3v2Tag.getArtist());
				song.setTitle(id3v2Tag.getTitle());
				song.setAlbum(id3v2Tag.getAlbum());
				song.setYear(id3v2Tag.getYear());
				song.setGenre(id3v2Tag.getGenreDescription());
				song.setAuthor(id3v2Tag.getComposer());
			}

			if (StringUtils.isEmpty(song.getTitle())) {
				song.setTitle(FileUtils.getFileNameWithoutExtension(song.getFileName()));
			}

		} catch (Exception e) {
			logger.error(String.format("Invalid song file: %s", sourceFile.getAbsoluteFile().toString()), e);
		}
		return song;
	}

	@Override
	public void save(File sourceFile, Song bean) {

	}

}
