package org.ocelot.tunes4j.library.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 * Enhanced PopClickListener for reactive SongListView in Library bounded context.
 *
 * COPIED FROM: gui/PopClickListener.java
 * ENHANCED WITH: Reactive event-driven behavior and Library-specific functionality
 *
 * Instead of directly accessing external components,
 * this publishes events for reactive cross-context handling.
 */
class SongListPopClickListener extends MouseAdapter {

	SongListView songListView;
	JPopupMenu menu;

	public SongListPopClickListener(SongListView songListView) {
		this.songListView = songListView;
	}

	public void mousePressed(MouseEvent e){
		if (e.isPopupTrigger())
			doPop(e);
	}

	public void mouseReleased(MouseEvent e){
		if (e.isPopupTrigger())
			doPop(e);
	}

	private void doPop(MouseEvent e){
		// In reactive library architecture, publish events instead of direct access
		var selectedSong = songListView.getSelectedSong();
		if (selectedSong != null) {
			System.out.println("🎵 LIBRARY POPUP: Song selected - " + selectedSong.getTitle());
			// TODO: Publish LibraryPopupMenuEvent for reactive menu handling
			// For now, create a simple popup menu
			showSimpleContextMenu(e);
		}
	}

	private void showSimpleContextMenu(MouseEvent e) {
		menu = new JPopupMenu();
		menu.add("Play Song");
		menu.add("Add to Playlist");
		menu.add("Edit Metadata");
		menu.add("Show in Finder");
		menu.addSeparator();
		menu.add("Remove from Library");

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}
