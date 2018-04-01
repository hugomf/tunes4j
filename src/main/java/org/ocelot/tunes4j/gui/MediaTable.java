package org.ocelot.tunes4j.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.collections.CollectionUtils;
import org.ocelot.tunes4j.dao.ColumnRepository;
import org.ocelot.tunes4j.dao.SongRepository;
import org.ocelot.tunes4j.dto.Column;
import org.ocelot.tunes4j.dto.Song;
import org.ocelot.tunes4j.notification.MacNotifier;
import org.ocelot.tunes4j.notification.NotifierFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.widgets.TableUtils;
import com.explodingpixels.widgets.TableUtils.SortDirection;


@Component
public class MediaTable {

	protected static boolean resizingColumnHasEnded = false;
	protected static final boolean notRestoringColumnState = true;
	private BeanTableModel<Song> model;
	private JTable table;
	private ProgressLoadDialog dialog;
	protected int prevRow = -1;
	protected int currentRow;
	private RowSorter<TableModel> sorter;
	
	
	@Autowired
	private SongRepository audioService;
	
	@Autowired
	private ColumnRepository columnService;

	@Autowired
	private ApplicationWindow parentFrame;
	
	public RowSorter<TableModel> getSorter() {
		return sorter;
	}

	public JTable getTable() {
		return table;
	}
	
	public BeanTableModel<?> getModel() {
		return model;
	}
	
	public SongRepository getAudioService() {
		return audioService;
	}
	
	public ApplicationWindow getApplicationWindow() {
		return parentFrame;
	}

	public JScrollPane getTablePane() {

		model = new BeanTableModel<Song>(Song.class);
		table = MacWidgetFactory.createITunesTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setDragEnabled(true);
		table.setFillsViewportHeight(true);
		table.setTransferHandler(new FileTransferHandler(parentFrame, table,
				audioService));
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		new TableColumnResizer(table);
		reorderColumnsInTable(HeaderConstants.HEADER_NAMES, table);
		configureSort(model);
		configureTableListeners();
		
		loadColumnWidth();
		listenForColumnWidthChanges();
		loadData();
		// ((DefaultCellEditor)table.getDefaultEditor(String.class)).setClickCountToStart(2);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		// //For Single Click Editing
		table.setDefaultEditor(Object.class, new MyTableCellEditor(this));
		JScrollPane scrollPane = new JScrollPane(table);
		// IAppWidgetFactory.makeIAppScrollPane(scrollPane);
		return scrollPane;
	}

	private void listenForColumnWidthChanges() {

		PropertyChangeListener pcl = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("preferredWidth")) {
					resizingColumnHasEnded = true;
				}
			}
		};
			
		getTable().getTableHeader().addMouseListener( new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseDragged(e);
				if(resizingColumnHasEnded) {
					updateAllColumsSize();
					resizingColumnHasEnded = false;
				}
			}

		});

		for (Enumeration<TableColumn> e = getTable().getColumnModel().getColumns(); 
				e.hasMoreElements();) {
			TableColumn tc = (TableColumn) e.nextElement();
			tc.addPropertyChangeListener(pcl);
		}
		
		
	}

	private void updateAllColumsSize() {
		int i = 0;
		for (Enumeration<TableColumn> e = getTable().getColumnModel().getColumns(); 
				e.hasMoreElements();) {
			TableColumn tc = (TableColumn) e.nextElement();
			saveColumnWidth(i++, tc.getPreferredWidth());
		}
	}

	private void saveColumnWidth(int index, int width) {
		 Column column = columnService.findById(index);
		 if(column==null) {
			column = new Column();
		 }
		 column.setId(index);
		 column.setSize(width);
		columnService.save(column);
	}

	private void loadData() {
		List<Song> list = (List<Song>) audioService.findAll();
		if (CollectionUtils.isNotEmpty(list)) {
			for (Object row : list) {
				Song bean = (Song) row;
				model.addRow(bean);
			}
		}
	}

	private void loadColumnWidth() {
		for (int i = 0; i < getTable().getColumnModel().getColumnCount(); i++) {
			Column column = columnService.findById(i);
			if (column != null) {
				getTable().getColumnModel().getColumn(i).setPreferredWidth(
						column.getSize());
			}
		}
	}

	public ProgressLoadDialog showDialog(List<File> list) {
		dialog = new ProgressLoadDialog(list, table, parentFrame,
				"Progress Dialog", true, audioService);
		return dialog;
	}

	public void removeSelectedItems() {
		int[] selectedRows = table.getSelectedRows();
		if (selectedRows != null && selectedRows.length > 0) {
			int[] selectedModelRows = new int[selectedRows.length];
			int j = 0;
			for (int i = 0; i < selectedRows.length; i++) {
				int modelRow = table.convertRowIndexToModel(selectedRows[i]);
				selectedModelRows[j] = modelRow;
				j++;
			}
			Arrays.sort(selectedModelRows);
			model.removeRows(selectedModelRows);
		}
	}

	public void configureSort(BeanTableModel<Song> model) {
		sorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorter);
		TableUtils.SortDelegate sortDelegate = new TableUtils.SortDelegate() {
			@Override
			public void sort(int columnModelIndex, SortDirection sortDirection) {
			}
		};
		TableUtils.makeSortable(table, sortDelegate);
	}

	// public void configureSort(BeanTableModel model) {
	// RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
	// table.setRowSorter(sorter);
	// }

	public void configureTableListeners() {

		final JPopupMenu popupMenu = new JPopupMenu();
		final String[] headers = HeaderConstants.HEADER_NAMES;
		for (String itemName : headers) {
			final JMenuItem item = new JCheckBoxMenuItem(itemName);
			item.setSelected(true);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					TableColumn column = getColumnByHeaderName(item.getText());
					setColumnVisible(column, item.isSelected());
				}
			});

			popupMenu.add(item);

		}
		popupMenu.add(new JSeparator());
		addToggleVisibilityMenuItem(popupMenu, "Select All", true);
		addToggleVisibilityMenuItem(popupMenu, "Clear All", false);

		table.getTableHeader().addMouseListener(
				new TableHeaderMouseListener(table, sorter, popupMenu));

		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				JTable table = (JTable) e.getSource();
				Point p = e.getPoint();
				int rowAtPoint = table.rowAtPoint(p);
				// Fix the sort order issue
				if (rowAtPoint < 0)
					return;
				int row = table.convertRowIndexToModel(rowAtPoint);
				if (table.isRowSelected(rowAtPoint)) {
					if (table.isEditing()) {
						table.getCellEditor().stopCellEditing();
					}
					if (e.getClickCount() == 1) {
						prevRow = currentRow;
						currentRow = rowAtPoint;
					}
					if (e.getClickCount() == 2 && rowAtPoint > -1) {
						// int row = table.convertRowIndexToModel(rowAtPoint);
						if (row > -1) {
							parentFrame.getPlayer().open(getSelectedFile());
							parentFrame.getPlayer().reset();
							parentFrame.getPlayer().play();
						}
					}
				}

			}
		});
		table.getModel().addTableModelListener(new MyTableModelListener(this));
		table.addMouseListener(new PopClickListener(this));
	}
	
	public File getSelectedFile() {
		int row = table.convertRowIndexToModel(table.getSelectedRow());
		if(row!=-1) {
			Song bean = (Song) model.getRow(row);
			String filePath = bean.getPath() + File.separator + bean.getFileName();
			triggerNotification(bean);
			return new File(filePath);
		}
		return null;
	}

	private void triggerNotification(Song bean) {
		
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				NotifierFactory.instance().push(bean.getTitle(), "Itunes4j", bean.getArtist());
				
			}
		});
		
	}
	

	public void addToggleVisibilityMenuItem(final JPopupMenu popupMenu,
			String label, final boolean visibility) {
		JMenuItem itemSelectAll = new JMenuItem(label);
		itemSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				for (int i = 0; i < popupMenu.getComponents().length; i++) {
					Object item = popupMenu.getComponent(i);
					if (item instanceof JCheckBoxMenuItem) {
						JCheckBoxMenuItem checkItem = (JCheckBoxMenuItem) item;
						checkItem.setSelected(visibility);
						setColumnVisible(getColumnByHeaderName(checkItem
								.getText()), visibility);
					}
				}
			}
		});
		popupMenu.add(itemSelectAll);
	}

	public void setColumnVisible(TableColumn column, boolean visible) {
		if (!visible) {
			column.setMinWidth(0);
			column.setMaxWidth(0);
		} else {
			column.setMinWidth(15);
			column.setMaxWidth(2147483647);
			column.setWidth(75);
			column.setPreferredWidth(75);
		}

	}

	public TableColumn getColumnByHeaderName(String headerName) {
		int selectedIndex = -1;
		TableColumn selectedColumn = null;
		for (int i = 0; i < model.getColumnCount(); i++) {
			if (headerName.equals(table.getColumnModel().getColumn(i)
					.getHeaderValue())) {
				selectedIndex = i;
			}
		}
		if (selectedIndex > -1) {
			selectedColumn = table.getColumnModel().getColumn(selectedIndex);
		}
		return selectedColumn;
	}

	public static void reorderColumnsInTable(final String[] order,
			final JTable table) {
		TableColumnModel model = table.getColumnModel();
		for (int newIndex = 0; newIndex < order.length; newIndex++) {
			String columnName = order[newIndex];
			int index = model.getColumnIndex(columnName);
			if (index != newIndex) {
				model.moveColumn(index, newIndex);
			}
		}
	}

}
