package org.ocelot.tunes4j.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class SearchText extends JTextField {
	
	private MediaTable mediaTable;
	private static final long serialVersionUID = -3797695028414675740L;

	public SearchText(MediaTable table) {
		super(18);
		this.mediaTable = table;
		initialize();
	}
	
	public void initialize() {
		putClientProperty("JTextField.variant", "search");
		setForeground(Color.GRAY);
		setFont(new Font(getFont().getName(),Font.ITALIC, 12));
		

		getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				getSorter().setRowFilter(null);
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (mediaTable != null) {
					String text = getText();

					if (text.length() == 0) {
						getSorter().setRowFilter(null);
					} else {
						String pattern = String.format("(?i)%s", text);
						getSorter().setRowFilter(RowFilter.regexFilter(pattern));
					}
				}
			}});
	}

	private TableRowSorter<TableModel> getSorter() {
		TableRowSorter<TableModel> sorter = 
				(TableRowSorter<TableModel>) mediaTable
					.getSorter();
		return sorter;
	}
	
	
}

