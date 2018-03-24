package org.ocelot.tunes4j.gui;

import java.awt.Toolkit;
import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import org.ocelot.tunes4j.dao.SongRepository;
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.taggers.RegistryTagger;
import org.ocelot.tunes4j.taggers.Tagger;
import org.ocelot.tunes4j.utils.FileUtils;
import org.ocelot.tunes4j.utils.ResourceLoader;


public class ImportFilesTask extends SwingWorker<Object, Object> {

	private BeanTableModel<Song> model;
	private RegistryTagger registry = null;
	private String strategy = ResourceLoader.ID3TAGGER_PARSER;
	private List<File> sourceFileList;
	private SongRepository service;
	
    public ImportFilesTask(BeanTableModel<Song> model, List<File> sourceFileList,SongRepository service) {
    	this.model = model;
    	this.registry = new RegistryTagger();
		this.sourceFileList=sourceFileList;
		this.service = service;
    }

    
	@Override
	protected Object doInBackground() throws Exception {
		// Get Files from FileSystem
		// Parse Files
		int i = 0;
		File file;
		List<File> fileList = FileUtils.getFiles(sourceFileList);
		int size=fileList.size();
		Tagger tagger = registry.getInsance(this.strategy);
		while( i < fileList.size() &&  !isCancelled() ) {
			file = fileList.get(i);
			//if(MimeUtil.getMimeTypes(file).contains("audio/mpeg")) {
				Song bean = tagger.parse(file);
				if(bean!=null) {
					publish(bean);
				}
			//}
			setProgress(100 * (++i) / size);
		}
		return null;
	}
	 /**
     * 
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
    }
    
	@Override
    protected void process(List<Object> chunks) {
        for (Object row : chunks) {
        	Song bean = (Song) row;
        	model.addRow(bean);
        	service.save(bean);
        }
    }
	
}