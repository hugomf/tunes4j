package org.ocelot.tunes4j.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.ocelot.tunes4j.utils.GUIUtilities;

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
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (mediaTable != null) {
					String text = getText();
					TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) mediaTable
							.getSorter();
					if (text.length() == 0) {
						sorter.setRowFilter(null);
					} else {
						sorter.setRowFilter(RowFilter.regexFilter(text));
					}
				}
			}});
		
	}
	
	public static void main(String[] args) {
		
		SearchText text = new SearchText(null);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(text);
		frame.pack();
		GUIUtilities.centerWindow(frame);
		frame.setVisible(true);
		
	}
	
}

