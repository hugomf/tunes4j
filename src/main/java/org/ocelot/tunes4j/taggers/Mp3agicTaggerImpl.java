package org.ocelot.tunes4j.taggers;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.lang.StringUtils;
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.utils.FileUtils;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class Mp3agicTaggerImpl implements Tagger {

	@Override
	public Song parse(File sourceFile) {
		
		Song song = new Song();
		try {
			Mp3File mp3file = new Mp3File(sourceFile);
			
			song.setPath(sourceFile.getParent());
			song.setFileName(sourceFile.getName());
			
			if (mp3file.hasId3v1Tag()) {
		        	ID3v1 id3v1Tag = mp3file.getId3v1Tag();
		        	System.out.println("Track: " + id3v1Tag.getTrack());
		        	System.out.println("Artist: " + id3v1Tag.getArtist());
		        	System.out.println("Title: " + id3v1Tag.getTitle());
		        	System.out.println("Album: " + id3v1Tag.getAlbum());
		        	System.out.println("Year: " + id3v1Tag.getYear());
		        	System.out.println("Genre: " + id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")");
		        	System.out.println("Comment: " + id3v1Tag.getComment());
		        	
		        	song.setTrackNumber(id3v1Tag.getTrack());
		        	song.setArtist(id3v1Tag.getArtist());
		        	song.setTitle(id3v1Tag.getTitle());
		        	song.setAlbum(id3v1Tag.getAlbum());
		        song.setYear(id3v1Tag.getYear());
		        song.setGenre(id3v1Tag.getGenreDescription());
	        } 
			
			if (mp3file.hasId3v2Tag()) {
		        	ID3v2 id3v2Tag = mp3file.getId3v2Tag();
		        	System.out.println("Track: " + id3v2Tag.getTrack());
		        	System.out.println("Artist: " + id3v2Tag.getArtist());
		        	System.out.println("Title: " + id3v2Tag.getTitle());
		        	System.out.println("Album: " + id3v2Tag.getAlbum());
		        	System.out.println("Year: " + id3v2Tag.getYear());
		        	System.out.println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
		        	System.out.println("Comment: " + id3v2Tag.getComment());
		        	
		        	
		        	byte[] imageData = id3v2Tag.getAlbumImage();
		            if (imageData != null) {
						String mimeType = id3v2Tag.getAlbumImageMimeType();
						song.setArtWork(imageData);
						song.setArtMimeType(mimeType);
						System.out.println("Mime type: " + mimeType);
						System.out.println("length: " + imageData.length);
						// Write image to file - can determine appropriate file extension from the mime type
						//RandomAccessFile file = new RandomAccessFile("album-artwork", "rw");
						//file.close();
		            }
		        	
		        	song.setTrackNumber(id3v2Tag.getTrack());
		        	song.setArtist(id3v2Tag.getArtist());
		        	song.setTitle(id3v2Tag.getTitle());
		        	song.setAlbum(id3v2Tag.getAlbum());
		        song.setYear(id3v2Tag.getYear());
		        song.setGenre(id3v2Tag.getGenreDescription());
		        song.setAuthor(id3v2Tag.getComposer());
			}
			
			if(StringUtils.isEmpty(song.getTitle())) {
				song.setTitle(FileUtils.getFileNameWithoutExtension(song.getFileName()));
			}
			
		
		} catch (UnsupportedTagException e) {
			e.printStackTrace();
		} catch (InvalidDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return song;
	}

	@Override
	public void save(File sourceFile, Song bean) {
		// TODO Auto-generated method stub
		
	}

}
