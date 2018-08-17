package org.ocelot.tunes4j.gui;

import javax.swing.JFrame;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.mockito.Mockito;
import org.ocelot.tunes4j.utils.GUIUtils;

public class SearchTextTest {
	
	public static void main(String[] args) {
		
		MediaTable mediaTable = Mockito.mock(MediaTable.class);
		
		TableRowSorter<TableModel> sorter = Mockito.mock(TableRowSorter.class);
		Mockito.when(mediaTable.getSorter()).thenReturn(sorter);
		
		SearchText text = new SearchText(mediaTable);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(text);
		frame.pack();
		GUIUtils.centerWindow(frame);
		frame.setVisible(true);
		
	}

}
