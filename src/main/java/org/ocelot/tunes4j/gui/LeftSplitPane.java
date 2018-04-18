package org.ocelot.tunes4j.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JSplitPane;

import org.ocelot.tunes4j.utils.ResourceLoader;

import com.explodingpixels.macwidgets.MacIcons;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListControlBar;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.SourceListSelectionListener;
import com.explodingpixels.widgets.PopupMenuCustomizerUsingStrings;

public class LeftSplitPane {

	private SourceListModel model  = new SourceListModel();
	private SourceListCategory libraryCategory = new SourceListCategory("Library");
	private SourceListCategory playlistCategory = new SourceListCategory("Playlists");
	private SourceList fSourceList;
	private Icon musicPlaylistIcon 		= ResourceLoader.ICON_MUSIC;
	private Icon radioPlaylistIcon 	= ResourceLoader.ICON_RADIO;
	private Icon moviesPlaylistIcon 	= ResourceLoader.ICON_MOVIES;
	private Icon tvShowsPlaylistIcon 	= ResourceLoader.ICON_TVSHOWS;
	private Icon podcastsPlaylistIcon 	= ResourceLoader.ICON_PODCASTS;
	private Icon playlistIcon 			= ResourceLoader.ICON_PLAYLIST;
	private Icon smartPlaylistIcon 		= ResourceLoader.ICON_SMARTPLAYLIST;
	private MediaTable mediaTable;
	private RadioStationTable radioTable;
	private JSplitPane splitPane;
	
	public LeftSplitPane(MediaTable mediaTable, RadioStationTable radioTable) {
		this.mediaTable = mediaTable;
		this.radioTable = radioTable;
	}
	
	public JSplitPane create() {

		model.addCategory(libraryCategory);
		
		model.addCategory(playlistCategory);
		
		model.addItemToCategory(new SourceListItem("Music", musicPlaylistIcon), libraryCategory);
		
		model.addItemToCategory(new SourceListItem("Radio", radioPlaylistIcon), libraryCategory);
		
		model.addItemToCategory(new SourceListItem("Movies", moviesPlaylistIcon), libraryCategory);
		
		model.addItemToCategory(new SourceListItem("TV Shows", tvShowsPlaylistIcon), libraryCategory);
		
		model.addItemToCategory(new SourceListItem("Podcasts", podcastsPlaylistIcon), libraryCategory);

		model.addItemToCategory(new SourceListItem("My playlist", playlistIcon), playlistCategory);
		
		model.addItemToCategory(new SourceListItem("My smart playlist", smartPlaylistIcon), playlistCategory);

		fSourceList = new SourceList(model);

		fSourceList
				.addSourceListSelectionListener(new SourceListSelectionListener() {
					@Override
					public void sourceListItemSelected(SourceListItem item) {
						if (item == null)
							return;
						if (item.getText().equals("eMule")) {
							TabbedPaneDemo panel = new TabbedPaneDemo();
							splitPane.setRightComponent(panel);
						} else if (item.getText().equals("Music")) {
							splitPane.setRightComponent(mediaTable.getTablePane());
						} else if (item.getIcon() == playlistIcon) {
							splitPane.setRightComponent(mediaTable.getTablePane());
						} else if (item.getIcon() == radioPlaylistIcon) {
							splitPane.setRightComponent(radioTable.getTablePane());
						}
					}
				});
		SourceListControlBar controlBar = new SourceListControlBar();
		controlBar.createAndAddButton(MacIcons.PLUS, new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		controlBar.createAndAddButton(MacIcons.MINUS, null);
		controlBar.createAndAddPopdownButton(MacIcons.GEAR,
				new PopupMenuCustomizerUsingStrings(null, "Item One",
						"Item Two", "Item Three"));
		fSourceList.installSourceListControlBar(controlBar);
		splitPane = MacWidgetFactory.createSplitPaneForSourceList(fSourceList,
				mediaTable.getTablePane());
		splitPane.setDividerLocation(200);
		controlBar.installDraggableWidgetOnSplitPane(splitPane);
		return splitPane;
	}
	
	public void removeItemFromCategory() {
		if(fSourceList.getSelectedItem()!=null && playlistCategory!=null) {
			fSourceList.getModel().removeItemFromCategory(fSourceList.getSelectedItem(), playlistCategory);
		} else {
			mediaTable.removeSelectedItems();
		}
	}
	
	public JSplitPane getSplitPane() {
		return this.splitPane;
	}
	
	public void createPlaylist() {
	
		SourceListItem item = new SourceListItem("My playlist", playlistIcon);
		model.addItemToCategory(item, playlistCategory);
    }

}
