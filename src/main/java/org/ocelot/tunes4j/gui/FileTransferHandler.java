package org.ocelot.tunes4j.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.ocelot.tunes4j.dao.SongRepository;



public class FileTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 5631023586051978277L;
	
	ApplicationWindow parentFrame;
	JTable table;
	SongRepository service;
	
	public FileTransferHandler(ApplicationWindow frame, JTable table, SongRepository service) {
		this.parentFrame = frame;
		this.table = table;
		this.service = service;
	}
	
	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		for (int i = 0; i < arg1.length; i++) {
			DataFlavor flavor = arg1[i];
			if (flavor.equals(DataFlavor.javaFileListFlavor)) {
				return true;
			}
		}
		return true;
	}

	public boolean importData(JComponent comp, Transferable t) {
		DataFlavor[] flavors = t.getTransferDataFlavors();

		for (int i = 0; i < flavors.length; i++) {
			DataFlavor flavor = flavors[i];
			try {
				if (flavor.equals(DataFlavor.javaFileListFlavor)) {
					List list = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
					ProgressLoadDialog dialog = new ProgressLoadDialog(list, this.parentFrame, true);
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