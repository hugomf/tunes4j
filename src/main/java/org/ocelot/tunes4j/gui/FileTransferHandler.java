package org.ocelot.tunes4j.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.ocelot.tunes4j.dao.SongRepository;
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.utils.ImageBuilder;



public class FileTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 5631023586051978277L;
	
	
	private final DataFlavor localObjectFlavor;
	ApplicationWindow parentFrame;
	JTable table;
	SongRepository service;
	JComponent source;
	ImageBuilder builder;
	
	public FileTransferHandler(ApplicationWindow frame, JTable table, SongRepository service) {
		super();
		this.parentFrame = frame;
		this.table = table;
		this.service = service;
		this.localObjectFlavor = new ActivationDataFlavor(Song[].class, DataFlavor.javaJVMLocalObjectMimeType,
				"Songs Array");
		this.builder = new ImageBuilder();

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
	public int getSourceActions(JComponent c) {
		System.out.println("getSourceActions");
	      Component glassPane = c.getRootPane().getGlassPane();
	      glassPane.setCursor(DragSource.DefaultMoveDrop);
	      if (!(c instanceof JTable)) {
	          return TransferHandler.NONE;
	      }
	      JTable source = (JTable) c;
	      int w = source.getColumnModel().getColumn(0).getPreferredWidth();
	      int h = source.getRowHeight() - 20; 
	      String text = String.valueOf(source.getSelectedRowCount());
	      setDragImage(builder.createLabeledImage(text).getImage());
	      setDragImageOffset(new Point(w / 2, h));
	      return TransferHandler.MOVE; //TransferHandler.COPY_OR_MOVE;
	}
	
	
	@Override
	public boolean canImport(JComponent target, DataFlavor[] arg1) {
		
		
		if (!(target instanceof JTable)) {
			return false;
		}
		
		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];
			if (flavor.equals(DataFlavor.javaFileListFlavor)) {
				return true;
			}
		}
		return true;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean importData(JComponent comp, Transferable t) {
		DataFlavor[] flavors = t.getTransferDataFlavors();

		for (int i = 0; i < flavors.length; i++) {
			DataFlavor flavor = flavors[i];
			try {
				if (flavor.equals(DataFlavor.javaFileListFlavor)) {
					List<File> list = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
					new ProgressLoadDialog(list, this.parentFrame, true);
					return true;
				} else {
					System.out.println("importData rejected: " + flavor);
				}
			} catch (IOException ex) {
				System.err.println("IOError getting data: " + ex);
			} catch (UnsupportedFlavorException e) {
				System.err.println("Unsupported Flavor: " + e);
			}
		}
		Toolkit.getDefaultToolkit().beep();
		return false;
	}
}