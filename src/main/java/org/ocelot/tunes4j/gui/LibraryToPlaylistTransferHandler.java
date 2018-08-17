package org.ocelot.tunes4j.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.ocelot.tunes4j.dto.Song;

@SuppressWarnings("serial")
public class LibraryToPlaylistTransferHandler extends TransferHandler {

	private final DataFlavor localObjectFlavor;
	private int[] indices;
	private int addIndex = -1; 
	private int addCount; 
	private JComponent source;

	protected LibraryToPlaylistTransferHandler() {
	        super();
	        localObjectFlavor = new ActivationDataFlavor(Song[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
	    }

	@Override
	@SuppressWarnings("unchecked")
	protected Transferable createTransferable(JComponent c) {
		source = c;
		JTable table = (JTable) c;
		BeanTableModel<Song> model = (BeanTableModel<Song>) table.getModel();
		int[] selectedRowIndexes = table.getSelectedRows();
		Object[] transferedObjects = model.getRowsAsArray(selectedRowIndexes);
		return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
	}

	@Override
	public boolean canImport(TransferSupport info) {
		//SourceListItem table = (SourceListItem) info.
		
		
		boolean isDroppable = info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
		// XXX bug?
		//table.setCursor(isDroppable ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
		return isDroppable;
	}
	



	@SuppressWarnings("PMD.ReplaceVectorWithList")
	@Override
	public boolean importData(TransferSupport info) {
//		if (!canImport(info)) {
//			return false;
//		}
//		TransferHandler.DropLocation tdl = info.getDropLocation();
//		if (!(tdl instanceof JTable.DropLocation)) {
//			return false;
//		}
//		JTable.DropLocation dl = (JTable.DropLocation) tdl;
//		JTable target = (JTable) info.getComponent();
//		DefaultTableModel model = (DefaultTableModel) target.getModel();
//		int index = dl.getRow();
//		// boolean insert = dl.isInsert();
//		int max = model.getRowCount();
//		if (index < 0 || index > max) {
//			index = max;
//		}
//		addIndex = index;
//		target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//		try {
//			Object[] values = (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
//			if (Objects.equals(source, target)) {
//				addCount = values.length;
//			}
//			for (int i = 0; i < values.length; i++) {
//				int idx = index++;
//				model.insertRow(idx, (Vector<?>) values[i]);
//				target.getSelectionModel().addSelectionInterval(idx, idx);
//			}
//			return true;
//		} catch (UnsupportedFlavorException | IOException ex) {
//			ex.printStackTrace();
//		}
		return false;
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		//cleanup(c, action == TransferHandler.MOVE);
	}

//	@SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
//	private void cleanup(JComponent c, boolean remove) {
//		if (remove && indices != null) {
//			c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//			DefaultTableModel model = (DefaultTableModel) ((JTable) c).getModel();
//			if (addCount > 0) {
//				for (int i = 0; i < indices.length; i++) {
//					if (indices[i] >= addIndex) {
//						indices[i] += addCount;
//					}
//				}
//			}
//			for (int i = indices.length - 1; i >= 0; i--) {
//				model.removeRow(indices[i]);
//			}
//		}
//		indices = null;
//		addCount = 0;
//		addIndex = -1;
//	}

}
