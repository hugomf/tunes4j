package org.ocelot.tunes4j.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

 public class TableHeaderMouseListener implements MouseListener {
        private final JTableHeader th;
        private final JTable table;
        private final RowSorter<TableModel> sorter;
        private final JPopupMenu popupMenu;
        
        public TableHeaderMouseListener(JTable table, 
        									RowSorter<TableModel> sorter, 
        												JPopupMenu popupMenu) {
        	this.table = table;
        	this.th = table.getTableHeader();
            this.sorter = sorter;
            this.popupMenu = popupMenu;
        }
		@Override
		public void mouseClicked(MouseEvent e) { }
		@Override
		public void mouseEntered(MouseEvent e) { }
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {
			int columnIndex = th.columnAtPoint(e.getPoint());
			if (columnIndex != -1) {
				columnIndex = table.convertColumnIndexToModel(columnIndex);
				sorter.toggleSortOrder(columnIndex);
			}
			maybeShowPopup(e);
		}
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
		private void maybeShowPopup(MouseEvent e) {
			if (table.isEditing()) {        
				table.getCellEditor().stopCellEditing();      
			}
			if (e.isPopupTrigger() && MouseEvent.BUTTON3_MASK != 0) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

    }
