package org.ocelot.tunes4j.library.view;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.collections.CollectionUtils;
import org.ocelot.tunes4j.audio.event.AudioPlaybackStateEvent;
import org.ocelot.tunes4j.dao.ColumnRepository;
import org.ocelot.tunes4j.dto.Column;
import org.ocelot.tunes4j.event.SongInfoEvent;
import org.ocelot.tunes4j.gui.ApplicationWindow;
import org.ocelot.tunes4j.gui.BeanTableModel;
import org.ocelot.tunes4j.gui.HeaderConstants;
import org.ocelot.tunes4j.gui.ProgressLoadDialog;
import org.ocelot.tunes4j.gui.StrippedTable;
import org.ocelot.tunes4j.gui.TableColumnResizer;
import org.ocelot.tunes4j.gui.TableHeaderMouseListener;
import org.ocelot.tunes4j.library.BaseReactiveView;
import org.ocelot.tunes4j.library.event.LibrarySongSelectedEvent;
import org.ocelot.tunes4j.library.event.LibraryUserInteractionEvent;
import org.ocelot.tunes4j.library.model.LibrarySong;
import org.ocelot.tunes4j.notification.NotifierFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.explodingpixels.widgets.TableUtils;
import com.explodingpixels.widgets.TableUtils.SortDirection;

/**
 * SongListView - Reactive Song List Component (Library Bounded Context).
 *
 * RESPONSIBILITY: Display and manage song library with reactive event handling.
 * Migrated from gui/MediaTable.java following COPY → ENHANCE pattern.
 *
 * KEY DIFFERENCE: This belongs to LIBRARY bounded context, NOT Audio!
 * Manages song library display, selection, and organization.
 *
 * Reactive Extensions:
 * - @EventListener: Responds to library and audio events
 * - Event Publishing: Fires song selection and library interaction events
 * - Observer Pattern: Communicates through ApplicationEventPublisher
 */
@Component
public class SongListView extends BaseReactiveView {

	protected static boolean resizingColumnHasEnded = false;
	protected static final boolean notRestoringColumnState = true;
	private BeanTableModel<LibrarySong> model;
	private StrippedTable table;
	private JScrollPane scrollPane;
	private ProgressLoadDialog dialog;
	protected int prevRow = -1;
	protected int currentRow;
	private RowSorter<TableModel> sorter;
	private LibrarySong selectedSong;

	@Autowired
	private ColumnRepository columnService;

	@Autowired
	private ApplicationWindow parentFrame; // TEMPORARY - will be removed when fully reactive

	public RowSorter<TableModel> getSorter() {
		return sorter;
	}

	public JTable getTable() {
		return table;
	}

	public BeanTableModel<LibrarySong> getModel() {
		return model;
	}

	public ApplicationWindow getApplicationWindow() {
		return parentFrame;
	}

	@Autowired
	public SongListView(ColumnRepository columnService, ApplicationWindow parentFrame) {
		this.columnService = columnService;
		this.parentFrame = parentFrame;
	}

	public JScrollPane getTablePane() {

		if (model == null) {
			model = new BeanTableModel<>(LibrarySong.class);
		}
		if (table == null) {
			table = new StrippedTable(model);
		}
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setDragEnabled(true);
		table.setFillsViewportHeight(true);
		// TODO: Implement drag and drop when needed
		// table.setTransferHandler(new FileTransferHandler(parentFrame, table));
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		new TableColumnResizer(table);
		reorderColumnsInTable(HeaderConstants.HEADER_NAMES, table);
		configureSort(model);
		configureTableListeners();

		loadColumnWidth();
		listenForColumnWidthChanges();
		loadData();

		// Skip cell editor for now - will implement when needed
		// table.setDefaultEditor(Object.class, new MediaTableCellEditor(this));

		table.setIntercellSpacing(new Dimension(0, 0));
		table.setShowGrid(false);

	    UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setBackground(javax.swing.UIManager.getColor("Panel.background"));
		scrollPane.getViewport().setBackground(javax.swing.UIManager.getColor("Panel.background"));

		this.scrollPane = scrollPane;
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
			column.setId(index);
			column.setSize(width);
		columnService.save(column);
		}
	}

	private void loadData() {
		// TODO: Replace with reactive data injection from Library service
		// For now, this component is self-contained until Library controller provides data
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

	// TODO: Implement file import dialog when needed
	// public ProgressLoadDialog showDialog(List<File> list) {
	//     // Temporarily disabled to avoid gui dependencies
	//     // dialog = new ProgressLoadDialog(list, parentFrame,  true);
	//     // return dialog;
	//     return null;
	// }

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

	public void configureSort(BeanTableModel<LibrarySong> songModel) {
		sorter = new TableRowSorter<>(songModel);
		table.setRowSorter(sorter);
		TableUtils.SortDelegate sortDelegate = new TableUtils.SortDelegate() {
			@Override
			public void sort(int columnModelIndex, SortDirection sortDirection) {
			}
		};
		TableUtils.makeSortable(table, sortDelegate);
	}

	public void configureTableListeners() {

		final JPopupMenu popupMenu = new JPopupMenu();
		final String[] headers = HeaderConstants.HEADER_NAMES;
		for (String itemName : headers) {
			final JMenuItem item = new JCheckBoxMenuItem(itemName);
			item.setSelected(true);
			item.addActionListener(event -> {
				TableColumn column = getColumnByHeaderName(item.getText());
				setColumnVisible(column, item.isSelected());
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
			public void mouseReleased(MouseEvent e) {
				JTable table = (JTable) e.getSource();
				Point p = e.getPoint();
				int rowAtPoint = table.rowAtPoint(p);
				// Fix the sort order issue
				if (rowAtPoint < 0)
					return;
				if (table.isRowSelected(rowAtPoint)) {
					if (table.isEditing()) {
						table.getCellEditor().stopCellEditing();
					}
					if (e.getClickCount() == 1) {
						prevRow = currentRow;
						currentRow = rowAtPoint;
						handleSongSelection();
					}
					if (e.getClickCount() == 2 && rowAtPoint > -1) {
						handleSongActivation();
					}
				}
			}
		});

		// Skip table model listener for now - basic functionality works without it
		// table.getModel().addTableModelListener(new MediaTableModelListener(this));
		table.addMouseListener(new SongListPopClickListener(this));
	}

	/**
	 * REACTIVE: Handle single-click song selection.
	 * Publishes LibraryUserInteractionEvent.SONG_SELECTED via Observer Pattern.
	 */
	private void handleSongSelection() {
		LibrarySong song = getSelectedSong();
		if (song != null) {
			selectedSong = song;
			LibraryUserInteractionEvent event = LibraryUserInteractionEvent.songSelected(
				this, song, LibraryUserInteractionEvent.Action.SONG_SELECTED
			);

			System.out.println("🎵 LIBRARY: SONG_SELECTED - " + song.getTitle());
			publisher.publishEvent(event);
		}
	}

	/**
	 * REACTIVE: Handle double-click song activation.
	 * Publishes LibrarySongSelectedEvent via Observer Pattern (feeds to Audio context).
	 */
	private void handleSongActivation() {
		LibrarySong song = getSelectedSong();
		if (song != null) {
			selectedSong = song;
			LibrarySongSelectedEvent event = new LibrarySongSelectedEvent(this, song);

			System.out.println("🎵 LIBRARY: SONG_ACTIVATED - " + song.getTitle());
			publisher.publishEvent(event);

			// Notification moved to AudioController when playback actually starts
			// to ensure complete song metadata from dto.Song
		}
	}

	/**
	 * REACTIVE: Listen for audio playback state changes and update UI.
	 * This implements the Observer Pattern for cross-bounded-context communication.
	 */
	@EventListener
	public void onAudioPlaybackStateChanged(AudioPlaybackStateEvent event) {
		// Update visual indicators based on playback state
		System.out.println("🎵 LIBRARY: RECEIVED AUDIO STATE - " + event.getState());

		// TODO: Update row highlighting, play indicators, etc.
		// For now, keeping existing visual feedback
	}

	/**
	 * REACTIVE: Listen for song info updates (artwork, metadata changes).
	 * Updates the library display when song information changes.
	 */
	@EventListener
	public void onSongInfoUpdated(SongInfoEvent event) {
		// Refresh library display when song information changes
		table.repaint();

		System.out.println("🎵 LIBRARY: SONG INFO UPDATED - " + event.getSong().getTitle());
	}

	public LibrarySong getSelectedSong() {
		int selectedRow = table.getSelectedRow();
		if(selectedRow < 0) {
			JOptionPane.showMessageDialog(getApplicationWindow(),
				"Please choose a song!", "Aviso", JOptionPane.WARNING_MESSAGE);
			return null;
		}
		int row = table.convertRowIndexToModel(selectedRow);
		LibrarySong song = (LibrarySong) model.getRow(row);
		return song;
	}

	/**
	 * LEGACY: Keep notification logic for now - will be moved to service.
	 */
	private void triggerSongNotification(LibrarySong song) {
		new Thread(() -> {
			Image image = null;
			// TODO: Get image from song metadata when available
			NotifierFactory.instance().push(image, song.getAlbum(), song.getTitle(), song.getArtist());
		}).start();
	}

	public void addToggleVisibilityMenuItem(final JPopupMenu popupMenu,
			String label, final boolean visibility) {
		JMenuItem itemSelectAll = new JMenuItem(label);
		itemSelectAll.addActionListener(event -> {
			for (int i = 0; i < popupMenu.getComponents().length; i++) {
				Object item = popupMenu.getComponent(i);
				if (item instanceof JCheckBoxMenuItem) {
					JCheckBoxMenuItem checkItem = (JCheckBoxMenuItem) item;
					checkItem.setSelected(visibility);
					setColumnVisible(getColumnByHeaderName(checkItem
							.getText()), visibility);
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

	/**
	 * INJECT METHOD: Library controller/service should call this to populate the view.
	 * Lazily initializes the model if not yet created.
	 */
	public void setSongData(List<LibrarySong> songs) {
		// Ensure model is initialized if setSongData is called before UI creation
		if (model == null) {
			model = new BeanTableModel<>(LibrarySong.class);
		}

		if (CollectionUtils.isNotEmpty(songs)) {
			// Clear existing rows first - get all rows as an array
			int totalRows = model.getRowCount();
			if (totalRows > 0) {
				int[] allRows = new int[totalRows];
				for (int i = 0; i < totalRows; i++) {
					allRows[i] = i;
				}
				model.removeRows(allRows);
			}

			for (LibrarySong song : songs) {
				model.addRow(song);
			}
		}
	}

	/**
	 * Refresh theme colors when theme changes
	 */
	public void refreshThemeColors() {
		if (table instanceof StrippedTable) {
			((StrippedTable) table).refreshThemeColors();
		}
	}
}
