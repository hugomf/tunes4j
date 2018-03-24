package org.ocelot.tunes4j.taggers;


import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;

import org.ocelot.tunes4j.dto.Song;


public class ListID3Tagger extends AbstractID3Tagger  {

	private List list = null;

	public ListID3Tagger(String strategy) {
		super(strategy);
		// TODO Auto-generated constructor stub
	}
	
	public void parseFolder(File sourceFolder, List list) {
		this.list = list;
		super.parseFolder(sourceFolder);
	}
	
	public void fileTagged(Song mp3FileBean) {
		list.add(mp3FileBean);
		
	}
	
	

	
	
	

}
