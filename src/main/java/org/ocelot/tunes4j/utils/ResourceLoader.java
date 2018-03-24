package org.ocelot.tunes4j.utils;

import static org.ocelot.tunes4j.utils.FileUtils.getUrl;

import java.net.URL;

import javax.swing.ImageIcon;


public class ResourceLoader {
	
	public static final String ICON_MUSIC_PATH  = "/icons/MBiTunesMusicPlaylist.png";
	
	
	public static final ImageIcon ICON_BLUEGLOBE = getImageIcon("/com/explodingpixels/macwidgets/icons/DotMac.png");
	public static final ImageIcon ICON_GREYGLOBE = getImageIcon("/com/explodingpixels/macwidgets/icons/Network.png");
	public static final ImageIcon ICON_PREFERENCES = getImageIcon("/com/explodingpixels/macwidgets/icons/PreferencesGeneral.png");
	public static final ImageIcon ICON_GEAR = getImageIcon("/com/explodingpixels/macwidgets/icons/Advanced.png");
	
	public static final ImageIcon ICON_MUSIC = getImageIcon(ICON_MUSIC_PATH);
	public static final ImageIcon ICON_MOVIES = getImageIcon("/icons/MBiTunesMoviesPlaylist.png");
	public static final ImageIcon ICON_TVSHOWS = getImageIcon("/icons/MBiTunesTVShowsPlaylist.png");
	public static final ImageIcon ICON_PODCASTS = getImageIcon("/icons/MBiTunes7PodcastsPlaylist.png");
	public static final ImageIcon ICON_PURCHASED = getImageIcon("/icons/MBiTunes7PurchasedPlaylist.png");
	public static final ImageIcon ICON_PLAYLIST = getImageIcon("/icons/MBiTunes7Playlist.png");
	public static final ImageIcon ICON_SMARTPLAYLIST = getImageIcon("/icons/MBiTunes7SmartPlaylist.png");
	public static final ImageIcon ICON_ARES = getImageIcon("/icons/Ares16.png");
	public static final ImageIcon ICON_EMULE = getImageIcon("/icons/emule16.png");
	public static final ImageIcon ICON_TXTFIELD_CLOSE = getImageIcon("/icons/close.png");
	public static final ImageIcon ICON_TXTFIELD_SEARCH = getImageIcon("/icons/lupa.png");
	public static final ImageIcon ICON_APPICON = getImageIcon(ICON_MUSIC_PATH);
	
	public static final ImageIcon ICON_VOLUME = getImageIcon("/icons/volume.png");
	public static final ImageIcon ICON_VOLUME_LOW = getImageIcon("/icons/volume-low.png");
	public static final ImageIcon ICON_VOLUME_HIGH = getImageIcon("/icons/volume-high.png");
	
	
	public static final ImageIcon PLAY = getImageIcon("/icons/play.png");
	public static final ImageIcon PLAY_ON = getImageIcon("/icons/play_on.png");
	public static final ImageIcon PAUSE = getImageIcon("/icons/pause.png");
	public static final ImageIcon PAUSE_ON = getImageIcon("/icons/pause_on.png");
	public static final ImageIcon STOP = getImageIcon("/icons/stop.png");
	public static final ImageIcon STOP_ON = getImageIcon("/icons/stop_on.png");

	//public static final ImageIcon  boxLeftOff = getImageIcon("/icons/search-left_option_off.png");
	//public static final ImageIcon  boxLeftOn = getImageIcon("/icons/search-left_option_on.png");
	public static final ImageIcon  boxRightOff = getImageIcon("/icons/search-right-off.png");
	public static final ImageIcon  boxRighOn = getImageIcon("/icons/search-right-on.png");

	public static final ImageIcon boxRightClearOff = getImageIcon("/icons/search-right-clear-off.png");
	public static final ImageIcon boxRightClearOn = getImageIcon("/icons/search-right-clear-on.png");
	public static final ImageIcon boxBackgroundOff = getImageIcon("/icons/search-bg-off.png");
	public static final ImageIcon boxBackgroundOn = getImageIcon("/icons/search-bg-on.png");
	
	
	public static final String ID3TAGGER_PARSER = "org.ocelot.tunes4j.taggers.JID3TaggerImpl";
	
	//private  Log log = LogFactory.getLog(ResourceLoader.class);
	
	private static ImageIcon getImageIcon(String imgName) {
        URL imgURL = getUrl(imgName);
        return new ImageIcon(imgURL);
    }


}
