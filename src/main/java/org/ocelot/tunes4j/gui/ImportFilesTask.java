package org.ocelot.tunes4j.gui;

import static java.lang.String.format;

import java.awt.Toolkit;
import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.event.FileChangeEvent;
import org.ocelot.tunes4j.event.FileChangeEventListener;
import org.ocelot.tunes4j.taggers.RegistryTagger;
import org.ocelot.tunes4j.taggers.Tagger;
import org.ocelot.tunes4j.utils.RecurseFilesProvider;
import org.ocelot.tunes4j.utils.ResourceLoader;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class ImportFilesTask extends SwingWorker<Integer, File> {
	
	private static Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(RecurseFilesProvider.class);

	private RegistryTagger registry;
	private String strategy = ResourceLoader.ID3TAGGER_PARSER;
	private List<File> sourceFileList;
	private MediaTable mediaTable;
	private RecurseFilesProvider recurseFilesProvider;

	public ImportFilesTask(List<File> sourceFileList, MediaTable mediaTable) {
		this.registry = new RegistryTagger();
		this.sourceFileList = sourceFileList;
		this.mediaTable = mediaTable;
		this.recurseFilesProvider  = new RecurseFilesProvider();
		
		this.recurseFilesProvider.addFileChangeListener(new FileChangeEventListener() {
			
			@Override
			public void triggerOnAddNewFileEvent(FileChangeEvent event) {
				logger.info(format("Event Add New:%s", event));
				updateMediaTable(event.getFile());
			}

			@Override
			public void triggerOnDeleteFileEvent(FileChangeEvent event) {
				logger.info(format("Event Delete:%s", event));
				removeMediaTable(event.getFile());
			}

			@Override
			public void triggerOnChangeFileEvent(FileChangeEvent event) {
				//updateMediaTable(event.getFile());
			}
			
		});
		
	}

	@Override
	protected Integer doInBackground() throws Exception {
		int i = 0;
		File file;
		List<File> fileList = recurseFilesProvider.getFiles(sourceFileList);
		while (i < fileList.size() && !isCancelled()) {
			file = fileList.get(i);
			publish(file);
			setProgress(100 * (++i) / fileList.size());
		}

		if (i > 0) {
			logger.info("Successfully Imported:" + i + " songs!");
		}
		return null;
	}

	@Override
	public void done() {
		Toolkit.getDefaultToolkit().beep();
	}

	@Override
	protected void process(List<File> chunks) {
		for (File file : chunks) {
			updateMediaTable(file);
		}
	}

	private void updateMediaTable(File file) {
		Tagger tagger = this.registry.getInsance(this.strategy);
		if(file.exists()) {
			Song bean = tagger.parse(file);
			if (bean != null) {
				this.mediaTable.getModel().addRow(bean);
				this.mediaTable.getAudioService().save(bean);
			}
		} else {
			logger.info("File Doesn't exist:" + file.getAbsolutePath());
		}
	}
	
	private void removeMediaTable(File file) {
		logger.info("removing:" + file.getAbsolutePath());
		String folderPath = getFolderPath(file.getAbsolutePath());
		Song bean = this.mediaTable.getAudioService().findByPathAndFileName(folderPath, file.getName());
		if (bean != null) {
			this.mediaTable.getAudioService().delete(bean);
			this.mediaTable.getModel().removeRow(bean);
			this.mediaTable.getTable().revalidate();
		}  
	}
	
	public String getFolderPath(String fullPathFileName) {
		return fullPathFileName.substring(0, fullPathFileName.lastIndexOf(File.separator));
	}



}