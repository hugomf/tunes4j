package org.ocelot.tunes4j.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JPanel;
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
	
	private JPanel dummyPanel = new JPanel()	;
	
	//private Icon moviesPlaylistIcon 	= ResourceLoader.ICON_MOVIES;
	//private Icon tvShowsPlaylistIcon 	= ResourceLoader.ICON_TVSHOWS;
	private Icon podcastsPlaylistIcon 	= ResourceLoader.ICON_PODCASTS;
	private Icon playlistIcon 			= ResourceLoader.ICON_PLAYLIST;
	private Icon smartPlaylistIcon 		= ResourceLoader.ICON_SMARTPLAYLIST;
	
	private SourceListItem musicSourceItem  = new SourceListItem("Music", ResourceLoader.ICON_MUSIC);
	private SourceListItem radioSourceItem  = new SourceListItem("Radio Stations", ResourceLoader.ICON_RADIO);
	private SourceListItem gdriveSourceItem  = new SourceListItem("Google Drive", ResourceLoader.ICON_GDRIVE);
	private SourceListItem meganzSourceItem  = new SourceListItem("Mega Drive", ResourceLoader.ICON_MEGA);
	
	
	private ApplicationWindow parentFrame;
	private JSplitPane splitPane;
	
	public LeftSplitPane(ApplicationWindow parentFrame) {
		this.parentFrame = parentFrame;
	}
	
	public JSplitPane create() {

		model.addCategory(libraryCategory);
		
		model.addCategory(playlistCategory);
		
		model.addItemToCategory(musicSourceItem, libraryCategory);
		
		model.addItemToCategory(radioSourceItem, libraryCategory);
		
		model.addItemToCategory(gdriveSourceItem, libraryCategory);
		
		model.addItemToCategory(meganzSourceItem, libraryCategory);
		
		//model.addItemToCategory(new SourceListItem("Movies", moviesPlaylistIcon), libraryCategory);
		
		//model.addItemToCategory(new SourceListItem("TV Shows", tvShowsPlaylistIcon), libraryCategory);
		
		model.addItemToCategory(new SourceListItem("Podcasts", podcastsPlaylistIcon), libraryCategory);

		//model.addItemToCategory(new SourceListItem("My playlist", playlistIcon), playlistCategory);
		
		model.addItemToCategory(new SourceListItem("My smart playlist", smartPlaylistIcon), playlistCategory);

		fSourceList = new SourceList(model);
		
		fSourceList.setTransferHandler(new LibraryToPlaylistTransferHandler());
		

		fSourceList
				.addSourceListSelectionListener(new SourceListSelectionListener() {
					@Override
					public void sourceListItemSelected(SourceListItem item) {
						if (item == null)
							return;
						if (item.getText().equals("eMule")) {
							TabbedPaneDemo panel = new TabbedPaneDemo();
							splitPane.setRightComponent(panel);
						} else if (item.equals(musicSourceItem)) {
							splitPane.setRightComponent(parentFrame.getMediaTable().getTablePane());
							parentFrame.getPlayerPanel().show();
							parentFrame.getRadioPlayerPanel().hide();
						} else if (item.equals(radioSourceItem)) {
							splitPane.setRightComponent(parentFrame.getRadioTable().getTablePane());
							parentFrame.getPlayerPanel().hide();
							parentFrame.getRadioPlayerPanel().show();
						} else if (item.equals(gdriveSourceItem)) {
							splitPane.setRightComponent(dummyPanel);
						} else if (item.equals(meganzSourceItem)) {
							splitPane.setRightComponent(dummyPanel);
						} else if (item.getIcon() == podcastsPlaylistIcon) {
							splitPane.setRightComponent(dummyPanel);
						} else if (item.getIcon() == playlistIcon) {
							splitPane.setRightComponent(dummyPanel);
						} else if (item.getIcon() == smartPlaylistIcon) {
							splitPane.setRightComponent(dummyPanel);
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
				new PopupMenuCustomizerUsingStrings(null, "Item One", "Item Two", "Item Three"));
		fSourceList.installSourceListControlBar(controlBar);
		splitPane = MacWidgetFactory.createSplitPaneForSourceList(fSourceList,
				parentFrame.getMediaTable().getTablePane());
		
		splitPane.setDividerLocation(200);
		controlBar.installDraggableWidgetOnSplitPane(splitPane);
		//splitPane.setTransferHandler(new LibraryToPlaylistTransferHandler());
		fSourceList.setSelectedItem(musicSourceItem);
		return splitPane;
	}
	
	
	public void removeItemFromCategory() {
		
		if (fSourceList.getSelectedItem()==null) return;

		if (this.fSourceList.getSelectedItem().getText().equals("Music")) {
			this.parentFrame.getMediaTable().removeSelectedItems();
		} else if (this.fSourceList.getSelectedItem().getText().equals("Radio Stations")) {
			this.parentFrame.getRadioTable().removeSelectedItems();
		} else {
			fSourceList.getModel().removeItemFromCategory(fSourceList.getSelectedItem(), playlistCategory);
		}
	}
	
	public void selectAllFromCategory() {
		
		if (fSourceList.getSelectedItem()==null) return;
		
		if (this.fSourceList.getSelectedItem().getText().equals("Music")) {
			this.parentFrame.getMediaTable().getTable().selectAll();
		} else {
			this.parentFrame.getRadioTable().getTable().selectAll();;
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
