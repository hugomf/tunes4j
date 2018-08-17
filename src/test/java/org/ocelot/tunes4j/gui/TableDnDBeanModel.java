package org.ocelot.tunes4j.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.activation.*;
import javax.swing.*;
import javax.swing.table.*;

import org.ocelot.tunes4j.dto.Song;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public final class TableDnDBeanModel extends JPanel {
	
	private final TransferHandler handler = new TableRowTransferHandler();
	private final List<Song> data = 
			Lists.newArrayList(createSong(1), 
				createSong(2), 
				createSong(3), 
				createSong(4),
				createSong(5));

	private Song createSong(int index) {
		Song song = new Song();
		song.setFileName("filename" + index);
		song.setPath("path" + index);
		song.setAuthor("author" + index);
		song.setAlbum("album" + index);
		song.setArtist("artist" + index);
		song.setGenre("genre" + index);
		song.setTitle("title" + index);
		song.setTrackNumber("trackNumber" + index);
		song.setYear("year" + index);
		return song;
	}

	private JTable makeDnDTable() {

		BeanTableModel<Song> tableModel = new BeanTableModel<Song>(Song.class, data);

		JTable table = new JTable(tableModel);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setTransferHandler(handler);
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setDragEnabled(true);
		table.setFillsViewportHeight(true);
		// table.setAutoCreateRowSorter(true); //XXX

		// Disable row Cut, Copy, Paste
		ActionMap map = table.getActionMap();
		Action dummy = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/* Dummy action */ }
		};
		map.put(TransferHandler.getCutAction().getValue(Action.NAME), dummy);
		map.put(TransferHandler.getCopyAction().getValue(Action.NAME), dummy);
		map.put(TransferHandler.getPasteAction().getValue(Action.NAME), dummy);
		return table;
	}

	public TableDnDBeanModel() {
		super(new BorderLayout());
		JPanel p = new JPanel(new GridLayout(2, 1));
		p.add(new JScrollPane(makeDnDTable()));
		p.add(new JScrollPane(makeDnDTable()));
		p.setBorder(BorderFactory.createTitledBorder("Drag & Drop JTable"));
		add(p);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setPreferredSize(new Dimension(320, 240));
	}

	public static void main(String... args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public static void createAndShowGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		JFrame frame = new JFrame("DragRowsAnotherTable");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(new TableDnDBeanModel());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}


@SuppressWarnings("serial")
class TableRowTransferHandler extends TransferHandler {
	private final DataFlavor localObjectFlavor;
	private int[] indices;
	private int addIndex = -1; // Location where items were added
	private int addCount; // Number of items added.
	private JComponent source;

	protected TableRowTransferHandler() {
		super();
		localObjectFlavor = new ActivationDataFlavor(Song[].class, DataFlavor.javaJVMLocalObjectMimeType,
				"Array of items");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Transferable createTransferable(JComponent c) {
		source = c;
		JTable table = (JTable) c;
		BeanTableModel<Song> model = (BeanTableModel<Song>) table.getModel();
		Song[] transferedObjects = model.getRowsAsArray(table.getSelectedRows());
		return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport info) {
		JTable table = (JTable) info.getComponent();
		boolean isDroppable = info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
		// XXX bug?
		table.setCursor(isDroppable ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
		return isDroppable;
	}

	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE; // TransferHandler.COPY_OR_MOVE;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public boolean importData(TransferHandler.TransferSupport info) {
		if (!canImport(info)) {
			return false;
		}
		TransferHandler.DropLocation tdl = info.getDropLocation();
		if (!(tdl instanceof JTable.DropLocation)) {
			return false;
		}
		JTable.DropLocation dl = (JTable.DropLocation) tdl;
		JTable target = (JTable) info.getComponent();
		BeanTableModel<Song> model = (BeanTableModel<Song>) target.getModel();
		int index = dl.getRow();
		// boolean insert = dl.isInsert();
		int max = model.getRowCount();
		if (index < 0 || index > max) {
			index = max;
		}
		addIndex = index;
		target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		try {
			Song[] values = (Song[]) info.getTransferable().getTransferData(localObjectFlavor);
			if (Objects.equals(source, target)) {
				addCount = values.length;
			}
			for (int i = 0; i < values.length; i++) {
				int idx = index++;
				model.addRow(values[i]);
				target.getSelectionModel().addSelectionInterval(idx, idx);
			}
			return true;
		} catch (UnsupportedFlavorException | IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		cleanup(c, action == TransferHandler.MOVE);
	}

	private void cleanup(JComponent c, boolean remove) {
		if (remove && indices != null) {
			c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			DefaultTableModel model = (DefaultTableModel) ((JTable) c).getModel();
			if (addCount > 0) {
				for (int i = 0; i < indices.length; i++) {
					if (indices[i] >= addIndex) {
						indices[i] += addCount;
					}
				}
			}
			for (int i = indices.length - 1; i >= 0; i--) {
				model.removeRow(indices[i]);
			}
		}
		indices = null;
		addCount = 0;
		addIndex = -1;
	}

}
