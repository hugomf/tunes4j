package org.ocelot.tunes4j.service;

import static java.lang.String.format;

import java.io.File;
import java.util.List;

import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.event.FileChangeEvent;
import org.ocelot.tunes4j.event.FileChangeEventListener;
import org.ocelot.tunes4j.gui.MediaTable;
import org.ocelot.tunes4j.taggers.RegistryTagger;
import org.ocelot.tunes4j.taggers.Tagger;
import org.ocelot.tunes4j.utils.RecurseFilesProvider;
import org.ocelot.tunes4j.utils.ResourceLoader;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;

@Component
public class FolderWatcherRegister {
	
	private static Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(RecurseFilesProvider.class);
	
	private FolderMonitorForMacService monitorService;
	
	private MediaTable mediaTable;
	
	private static RegistryTagger TAGGER_FACTORY = new RegistryTagger();
	
	@Autowired
	public FolderWatcherRegister(MediaTable mediaTable) {
		
		this.monitorService = new FolderMonitorForMacService();
		this.mediaTable = mediaTable;
		this.monitorService.addFileChangeListener(new FileChangeEventListener() {
			
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
		registerFolders();
		this.monitorService.startAsync();
	}
	
	
	public void updateMediaTable(File file) {
		Tagger tagger = TAGGER_FACTORY.getInsance(ResourceLoader.ID3TAGGER_PARSER);
		if(file.isFile() && file.exists()) {
			Song bean = tagger.parse(file);
			if (bean != null) {
				String folderPath = getFolderPath(file.getAbsolutePath());
				Song dbBean = this.mediaTable.getAudioService().findByPathAndFileName(folderPath, file.getName());
				if (dbBean != null) return;
				this.mediaTable.getModel().addRow(bean);
				this.mediaTable.getAudioService().save(bean);
			}
		}
	}
	
	public void removeMediaTable(File file) {
		logger.info("removing:" + file.getAbsolutePath());
		String folderPath = getFolderPath(file.getAbsolutePath());
		Song bean = this.mediaTable.getAudioService().findByPathAndFileName(folderPath, file.getName());
		if (bean != null) {
			this.mediaTable.getAudioService().delete(bean);
			this.mediaTable.getModel().removeRow(bean);
			this.mediaTable.getTable().revalidate();
		}  
	}
	
	private String getFolderPath(String fullPathFileName) {
		return fullPathFileName.substring(0, fullPathFileName.lastIndexOf(File.separator));
	}
	
	
	public void registerFolders() {
		List<String> folders  = this.mediaTable.getAudioService().findFolders();
		for (String folder : folders) {		
			this.monitorService.subscribeFolder(new File(folder));
		}
	}
	
	
}
