package org.ocelot.tunes4j.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.ocelot.tunes4j.dao.SongRepository;
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.utils.ImageBuilder;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;



public class FileTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 5631023586051978277L;
	
	private static Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FileTransferHandler.class);
	
	private final DataFlavor localObjectFlavor;
	private ApplicationWindow parentFrame;
	private ImageBuilder builder;
	
	public FileTransferHandler(ApplicationWindow frame, JTable table, SongRepository service) {
		super();
		this.parentFrame = frame;
		this.localObjectFlavor = new ActivationDataFlavor(
				Song[].class, 
				DataFlavor.javaJVMLocalObjectMimeType, 
				"Songs Array");
		this.builder = new ImageBuilder();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected Transferable createTransferable(JComponent c) {
		JTable table = (JTable) c ;
		BeanTableModel<Song> model = (BeanTableModel<Song>) table.getModel();
		int[] selectedRowIndexes = table.getSelectedRows();
		Object[] transferedObjects = model.getRowsAsArray(selectedRowIndexes);
		return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
	}
	

	@Override
	public int getSourceActions(JComponent c) {
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
	      return TransferHandler.MOVE; 
	}
	
	
	@Override
	public boolean canImport(JComponent target, DataFlavor[] flavors) {
		if (!(target instanceof JTable)) {
			return false;
		}
		return Arrays.stream(flavors).anyMatch(p -> p.equals(DataFlavor.javaFileListFlavor));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean importData(JComponent comp, Transferable t) {
		DataFlavor[] flavors = t.getTransferDataFlavors();
		boolean found = Arrays.stream(flavors).anyMatch(p -> p.equals(DataFlavor.javaFileListFlavor));
		if (found) {
			try {
				List<File> list = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
				new ProgressLoadDialog(list, this.parentFrame, true);
				return true;
			} catch (IOException ex) {
				logger.error("IOError getting data: " + ex);
			} catch (UnsupportedFlavorException e) {
				logger.error("Unsupported Flavor: " + e);
			}	
		}
		return false;
	}

}