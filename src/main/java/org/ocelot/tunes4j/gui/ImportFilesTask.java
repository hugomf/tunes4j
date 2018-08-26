package org.ocelot.tunes4j.gui;

import java.awt.Toolkit;
import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import org.ocelot.tunes4j.service.FolderWatcherRegister;
import org.ocelot.tunes4j.utils.RecurseFilesProvider;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ch.qos.logback.classic.Logger;


public class ImportFilesTask extends SwingWorker<Integer, File> {
	
	private static Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(RecurseFilesProvider.class);

	private List<File> sourceFileList;
	
	private FolderWatcherRegister watcher;
	
	private int count;
	
	@Autowired
	public ImportFilesTask(List<File> sourceFileList, FolderWatcherRegister watcher) {
		this.sourceFileList = sourceFileList;
		this.watcher = watcher;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		int i = 0;
		File file;
		List<File> fileList = RecurseFilesProvider.getFiles(sourceFileList);
		this.count = fileList.size();
		while (i < fileList.size() && !isCancelled()) {
			file = fileList.get(i);
			publish(file);
            setProgress((++i * 100) / this.count);
            if(count < 300) {
            	waitFor(5);
            }
		}
		return null;
	}
	
	
	
	/**
     * Wait the given time in milliseconds
     * @param iMillis
     */
    private void waitFor (int iMillis) {
        try {
            Thread.sleep(iMillis);
        }
        catch (Exception ex) {
            System.err.println(ex);
        }
    }


	@Override
	public void done() {
		logger.info("Successfully Imported:" + count + " songs!");
		Toolkit.getDefaultToolkit().beep();
	}

	@Override
	protected void process(List<File> chunks) {
		for (File file : chunks) {
			this.watcher.updateMediaTable(file);
		}
	}
	
	


}