package org.ocelot.tunes4j.gui;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class MyTableCellEditor extends AbstractCellEditor implements
		TableCellEditor {
	private static final long serialVersionUID = 1L;
	private JComponent component = null;
	private MediaTable media;

	public MyTableCellEditor(MediaTable media) {
		super();
		this.media = media;
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
		if (media.prevRow == media.currentRow) {
			return true;
		}
		return false;
	}

}