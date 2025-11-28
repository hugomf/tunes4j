package org.ocelot.tunes4j.library.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.ocelot.tunes4j.library.BaseReactiveView;
import org.ocelot.tunes4j.library.event.LibraryUserInteractionEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * LibrarySearchView - Reactive Search Component (Library Bounded Context).
 *
 * RESPONSIBILITY: Handle song search and filtering within the song library table.
 * Migrated from gui/SearchText.java following COPY → ENHANCE pattern.
 *
 * KEY DIFFERENCE: This belongs to LIBRARY bounded context, NOT Audio!
 * Searches and filters songs in the reactive SongListView.
 *
 * Reactive Extensions:
 * - @EventListener: Listens for search-related events
 * - Observer Pattern: Publishes search/filter events
 * - Table Model Integration: Works with SongListView's table model
 */
@Component
public class LibrarySearchView extends JTextField {

    private SongListView songListView;
    private static final long serialVersionUID = -3797695028414675740L;

    public LibrarySearchView(SongListView songListView) {
        super(18);
        this.songListView = songListView;
        initialize();
    }

    public void initialize() {
        putClientProperty("JTextField.variant", "search");
        setForeground(Color.GRAY);
        setFont(new Font(getFont().getName(), Font.ITALIC, 12));

        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                clearSearchFilter();
            }

            @Override
            public void insertUpdate(DocumentEvent e) { }

            @Override
            public void changedUpdate(DocumentEvent e) { }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch(getText());
            }
        });
    }

    /**
     * REACTIVE: Listen for clear search events from Library context.
     * Clears the search filter when requested.
     */
    @EventListener
    public void onSearchClearedEvent(LibraryUserInteractionEvent event) {
        if (event.getAction() == LibraryUserInteractionEvent.Action.CLEAR_SEARCH) {
            clearSearchFilter();
        }
    }

    /**
     * Perform search with the given text.
     * Applies regex filter to the song list table.
     */
    private void performSearch(String searchText) {
        if (songListView != null) {
            if (searchText == null || searchText.trim().length() == 0) {
                clearSearchFilter();
            } else {
                applySearchFilter(searchText);
            }
        }
    }

    /**
     * Apply regex filter to search for songs.
     */
    private void applySearchFilter(String searchText) {
        try {
            TableRowSorter<TableModel> sorter = getSongListSorter();
            if (sorter != null) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                System.out.println("🔍 LIBRARY SEARCH: Applied filter - '" + searchText + "'");
            }
        } catch (Exception e) {
            // Invalid regex, clear filter
            clearSearchFilter();
        }
    }

    /**
     * Clear the search filter to show all songs.
     */
    private void clearSearchFilter() {
        TableRowSorter<TableModel> sorter = getSongListSorter();
        if (sorter != null) {
            sorter.setRowFilter(null);
            System.out.println("🔍 LIBRARY SEARCH: Cleared filter - showing all songs");
        }
    }

    /**
     * Get the table sorter from the associated SongListView.
     */
    private TableRowSorter<TableModel> getSongListSorter() {
        if (songListView != null && songListView.getSorter() != null) {
            @SuppressWarnings("unchecked")
            TableRowSorter<TableModel> sorter =
                (TableRowSorter<TableModel>) songListView.getSorter();
            return sorter;
        }
        return null;
    }

    /**
     * Set the associated SongListView for this search component.
     * Enables late binding for Spring dependency injection.
     */
    public void setSongListView(SongListView songListView) {
        this.songListView = songListView;
    }

    /**
     * Programmatically trigger a search.
     * Useful for external components to reset or apply filters.
     */
    public void search(String searchText) {
        setText(searchText);
        performSearch(searchText);
    }

    /**
     * Clear the search field and show all songs.
     */
    public void clearSearch() {
        setText("");
        clearSearchFilter();
    }
}
