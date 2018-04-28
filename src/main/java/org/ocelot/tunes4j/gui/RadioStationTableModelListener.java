package org.ocelot.tunes4j.gui;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.ocelot.tunes4j.dto.RadioStation;

public class RadioStationTableModelListener implements TableModelListener {
	
	private RadioStationTable table;

	RadioStationTableModelListener(RadioStationTable table) {
		this.table = table;
	}

	public void tableChanged(TableModelEvent e) {
		int firstRow = e.getFirstRow();
		int lastRow = e.getLastRow();
		switch (e.getType()) {
			case TableModelEvent.UPDATE:
				if (!(firstRow == TableModelEvent.HEADER_ROW)) {
					for (int r = firstRow; r <= lastRow; r++) {
						RadioStation bean = (RadioStation) this.table.getModel().getRow(r);
						//System.out.println("Update tag:" + bean);
						table.getRadioStationService().save(bean);
						//System.out.println("Radio Station saved!!!");
					}
				}
			case TableModelEvent.DELETE:
				RadioStation bean = (RadioStation) this.table.getModel().getRow(firstRow);
				table.getRadioStationService().delete(bean);
				break;
		}
	}
	
}
