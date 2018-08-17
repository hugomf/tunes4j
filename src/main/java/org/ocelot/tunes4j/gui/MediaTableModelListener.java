package org.ocelot.tunes4j.gui;

import java.io.File;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.ocelot.tunes4j.dao.SongRepository;
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.taggers.RegistryTagger;
import org.ocelot.tunes4j.taggers.Tagger;
import org.ocelot.tunes4j.utils.ResourceLoader;

public class MediaTableModelListener implements TableModelListener {

	private MediaTable mediaTable;

	public MediaTableModelListener(MediaTable mediaTable) {
		this.mediaTable = mediaTable;
	}

	public void tableChanged(TableModelEvent e) {
		int firstRow = e.getFirstRow();
		int lastRow = e.getLastRow();
		switch (e.getType()) {
		case TableModelEvent.UPDATE:
			if (!(firstRow == TableModelEvent.HEADER_ROW)) {
				for (int r = firstRow; r <= lastRow; r++) {
					Song bean = (Song) getModel().getRow(r);
					System.out.println("Update tag:" + bean);
					RegistryTagger registry = new RegistryTagger();
					Tagger tagger = registry
							.getInsance(ResourceLoader.ID3TAGGER_PARSER);
					String filePath = bean.getPath() + "\\"
							+ bean.getFileName();
					tagger.save(new File(filePath), bean);
					getAudioService().save(bean);
					System.out.println("file saved!!!");
				}
			}
		case TableModelEvent.DELETE:
			Song bean = (Song) getModel().getRow(firstRow);
			getAudioService().delete(bean);
			break;
		}
	}
	
	public BeanTableModel<?> getModel() {
		return mediaTable.getModel();
	}
	
	public SongRepository getAudioService() {
		return mediaTable.getAudioService();
	}
	
}
