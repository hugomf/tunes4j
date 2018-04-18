package org.ocelot.tunes4j.gui;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class RadioStationTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	
	private static final long serialVersionUID = 1L;
	
	private JComponent component = null;
	
	private RadioStationTable table;

	public RadioStationTableCellEditor(RadioStationTable table) {
		super();
		this.table = table;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		component = new JTextField();
		((JTextField) component).setText((String) value);
		return component;
	}

	@Override
	public Object getCellEditorValue() {
		return ((JTextField) component).getText();
	}

	@Override
	public boolean isCellEditable(EventObject evt) {
		if (table.prevRow == table.currentRow) {
			return true;
		}
		return false;
	}

}