package org.ocelot.tunes4j.gui;

import java.awt.Toolkit;
import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.taggers.RegistryTagger;
import org.ocelot.tunes4j.taggers.Tagger;
import org.ocelot.tunes4j.utils.FileUtils;
import org.ocelot.tunes4j.utils.ResourceLoader;

public class ImportFilesTask extends SwingWorker<Integer, Song> {

	private RegistryTagger registry;
	private String strategy = ResourceLoader.ID3TAGGER_PARSER;
	private List<File> sourceFileList;
	private MediaTable mediaTable;

	public ImportFilesTask(List<File> sourceFileList, MediaTable mediaTable) {
		this.registry = new RegistryTagger();
		this.sourceFileList = sourceFileList;
		this.mediaTable = mediaTable;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		int i = 0;
		File file;
		List<File> fileList = FileUtils.getFiles(sourceFileList);
		Tagger tagger = this.registry.getInsance(this.strategy);
		while (i < fileList.size() && !isCancelled()) {
			file = fileList.get(i);
			// if(MimeUtil.getMimeTypes(file).contains("audio/mpeg")) {
			Song bean = tagger.parse(file);
			if (bean != null) {
				publish(bean);
			}
			// }
			setProgress(100 * (++i) / fileList.size());
		}

		if (i > 0) {
			System.out.println("Successfully Imported:" + i + " songs!");
		}
		return null;
	}

	@Override
	public void done() {
		Toolkit.getDefaultToolkit().beep();
	}

	@Override
	protected void process(List<Song> chunks) {
		for (Song bean : chunks) {
			this.mediaTable.getModel().addRow(bean);
			this.mediaTable.getAudioService().save(bean);
		}
	}

}