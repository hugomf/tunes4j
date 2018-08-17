package org.ocelot.tunes4j.taggers;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.ID3Tag;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v1.ID3V1Tag;
import org.blinkenlights.jid3.v1.ID3V1_0Tag;
import org.blinkenlights.jid3.v1.ID3V1_1Tag;
import org.blinkenlights.jid3.v1.ID3V1Tag.Genre;
import org.blinkenlights.jid3.v2.ID3V2Tag;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;
import org.blinkenlights.jid3.v2.TCOMTextInformationID3V2Frame;
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.utils.FileUtils;



public class JID3TaggerImpl implements Tagger {

	private Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public void save(File sourceFile, Song mp3Bean) {
		MediaFile mp3file = new MP3File(sourceFile);
		System.out.println("mp3Bean=" + mp3Bean);
		try {
			ID3V2_3_0Tag oID3V2Tag = new ID3V2_3_0Tag();
			ID3V1_1Tag oID3V1_1Tag = new ID3V1_1Tag();
			ID3V1_0Tag oID3V1_0Tag = new ID3V1_0Tag();
			
			//Update 3V2 Tags
			oID3V2Tag.setArtist(mp3Bean.getArtist());
			oID3V2Tag.setTitle(mp3Bean.getTitle());
			oID3V2Tag.setAlbum(mp3Bean.getAlbum());
			Integer trackNumber = parseInt(mp3Bean.getTrackNumber());
			if(trackNumber!=null && trackNumber > 0) {
				oID3V2Tag.setTrackNumber(trackNumber.intValue());
			}
			oID3V2Tag.setGenre(mp3Bean.getGenre());
			Integer year = parseInt(mp3Bean.getYear());
			if(year!=null) {
				oID3V2Tag.setYear(year.intValue());
			}
			if(mp3Bean.getAuthor()!=null) {
				oID3V2Tag.setTCOMTextInformationFrame(new TCOMTextInformationID3V2Frame(mp3Bean.getAuthor()));
			}
			mp3file.setID3Tag(oID3V2Tag);
			
			//Update ID3V1_1 Tags
			oID3V1_1Tag.setArtist(mp3Bean.getArtist());
			oID3V1_1Tag.setTitle(mp3Bean.getTitle());
			oID3V1_1Tag.setAlbum(mp3Bean.getAlbum());
			if(trackNumber!=null && trackNumber > 0) {
				oID3V1_1Tag.setAlbumTrack(trackNumber);
			}
			setGenre(oID3V1_1Tag,mp3Bean);
			oID3V1_1Tag.setYear(mp3Bean.getYear());
			mp3file.setID3Tag(oID3V1_1Tag);
			
			//Update ID3V1_0 Tags
			oID3V1_0Tag.setArtist(mp3Bean.getArtist());
			oID3V1_0Tag.setTitle(mp3Bean.getTitle());
			oID3V1_0Tag.setAlbum(mp3Bean.getAlbum());
			setGenre(oID3V1_0Tag,mp3Bean);
			
			oID3V1_0Tag.setYear(mp3Bean.getYear());
			mp3file.setID3Tag(oID3V1_0Tag);
			mp3file.sync();
			
			
		} catch (ID3Exception e) {
			System.out.println("Error en el archivo: " + sourceFile.getPath());
			e.printStackTrace();
		}
		
		
	}

	public Integer parseInt(String value){
		if(value!=null){
			try{
				return Integer.parseInt(value);
			}catch(Exception e) {
				return null;
			}
		}
		return null;
	
	}
	
	private void setGenre(ID3V1Tag oID3V1Tag, Song mp3Bean) { 
		try {
			if(mp3Bean.getGenre()!=null && mp3Bean.getGenre().length()>0) {	
				oID3V1Tag.setGenre(Genre.lookupGenre(mp3Bean.getGenre()));
			}
		} catch(ID3Exception e){
			oID3V1Tag.setGenre(Genre.Undefined);
			System.out.println("Using a default genre:" + Genre.Undefined);
			e.printStackTrace();
		}

	}
	
	
	@Override
	public Song parse(File sourceFile) {
		Song mp3Bean = null;
		MediaFile mp3file = new MP3File(sourceFile);
		ID3Tag[] aoID3Tag = null;
		try {
			
			mp3Bean = new Song();
			mp3Bean.setPath(sourceFile.getParent());
			mp3Bean.setFileName(sourceFile.getName());
			
			
			try {
				aoID3Tag = mp3file.getTags();
			} catch(ID3Exception e) {
				System.out.println("Error en el archivo: " + sourceFile.getPath());
				e.printStackTrace();
			}
			if (aoID3Tag !=null && aoID3Tag.length > 0) {
				ID3V2_3_0Tag oID3V2Tag = null;
				ID3V1_1Tag oID3V1_1Tag = null;
				ID3V1_0Tag oID3V1_0Tag = null;
				
				
				for(ID3Tag tag : aoID3Tag ){
					if (tag instanceof ID3V2Tag) {
						oID3V2Tag = (ID3V2_3_0Tag) tag;
						mp3Bean.setTitle(oID3V2Tag.getTitle());
						mp3Bean.setAlbum(oID3V2Tag.getAlbum());
						try { mp3Bean.setTrackNumber(oID3V2Tag.getTrackNumber() + ""); }catch (ID3Exception e) {mp3Bean.setTrackNumber("");}
						mp3Bean.setGenre(oID3V2Tag.getGenre());
						try { mp3Bean.setYear(oID3V2Tag.getYear() + ""); }catch (ID3Exception e) {mp3Bean.setYear("");}
						TCOMTextInformationID3V2Frame tcomt = oID3V2Tag.getTCOMTextInformationFrame();
						if(tcomt!=null) {
							String[] authors = oID3V2Tag.getTCOMTextInformationFrame().getComposers();
							String author = (authors==null)? null : authors[0];
							mp3Bean.setAuthor(author);
						}
					}
					else if (tag instanceof ID3V1_1Tag) {
						oID3V1_1Tag = (ID3V1_1Tag) tag;
						mp3Bean.setTitle(oID3V1_1Tag.getTitle() );
						mp3Bean.setAlbum(oID3V1_1Tag.getAlbum());
						mp3Bean.setTrackNumber(oID3V1_1Tag.getAlbumTrack() + "");
						mp3Bean.setGenre(oID3V1_1Tag.getGenre().toString());
						mp3Bean.setYear(oID3V1_1Tag.getYear() + "");
					}else if (tag instanceof ID3V1_0Tag) {
						oID3V1_0Tag = (ID3V1_0Tag) tag;
						mp3Bean.setTitle(oID3V1_0Tag.getTitle() );
						mp3Bean.setAlbum(oID3V1_0Tag.getAlbum());
						mp3Bean.setGenre(oID3V1_0Tag.getGenre().toString());
						mp3Bean.setYear(oID3V1_0Tag.getYear());
					}
				}
				//merge values if not found
				mp3Bean.setArtist(lookUpValue("artist",oID3V2Tag,oID3V1_1Tag,oID3V1_0Tag));
				mp3Bean.setTitle(lookUpValue("title",oID3V2Tag,oID3V1_1Tag,oID3V1_0Tag));
				mp3Bean.setAlbum(lookUpValue("album",oID3V2Tag,oID3V1_1Tag,oID3V1_0Tag));
				mp3Bean.setTrackNumber(lookUpValue("trackNumber",oID3V2Tag,oID3V1_1Tag,oID3V1_0Tag));
				mp3Bean.setGenre(lookUpValue("genre",oID3V2Tag,oID3V1_1Tag,oID3V1_0Tag));
				mp3Bean.setYear(lookUpValue("year",oID3V2Tag,oID3V1_1Tag,oID3V1_0Tag));
			}
			
			if(StringUtils.isEmpty(mp3Bean.getTitle())) {
				mp3Bean.setTitle(FileUtils.getFileNameWithoutExtension(mp3Bean.getFileName()));
			}
			
		} catch (Exception e) {
			System.out.println("Error en el archivo: " + sourceFile.getPath());
			e.printStackTrace();
		}
		return mp3Bean;
	}
	

	
	
	public String lookUpValue(String fieldName,ID3V2_3_0Tag tagV2_3,
								ID3V1_1Tag tagV1_1, ID3V1_0Tag tagV1_0  ){

		String value = getProperty(fieldName, tagV2_3);
		if (value.length() < 1) {
			value = getProperty(fieldName, tagV1_1);
			if (value.length() < 1) {
				value = getProperty(fieldName, tagV1_0);
			}
		}
		return value;
	}
	
	public String getProperty(String fieldName, ID3Tag  tag) {
		String value;
		try {
			if(tag!=null) {
					value = (String) BeanUtils.getProperty(tag, fieldName);
				 if(value!=null) {
					 return value.trim();
				 }
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Throwable targetException = e.getTargetException();
		    if (tag instanceof ID3V2_3_0Tag && targetException instanceof ID3Exception) {
		    	return "";
		    } else {
		    	e.printStackTrace();
		    }
		} catch (NoSuchMethodException e) {
			//e.printStackTrace();
		} 
		return "";
	}


	
	
}
