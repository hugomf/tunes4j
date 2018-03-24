package org.ocelot.tunes4j.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class SearchText extends JTextFieldRounded {
	private MediaTable mediaTable;
	private static final long serialVersionUID = -3797695028414675740L;

	public SearchText(MediaTable table) {
		super(24);
		this.mediaTable = table;
		initialize();
	}
	
	public void initialize() {
		putClientProperty("JTextField.variant", "search");
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				String text = getText();
				TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) mediaTable
						.getSorter();
				if (text.length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(RowFilter.regexFilter(text));
				}
			}});
	}
	
}

