package org.ocelot.tunes4j.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;

import org.apache.commons.collections.CollectionUtils;
import org.ocelot.tunes4j.dao.PlayListRepository;
import org.ocelot.tunes4j.dto.PlayList;
import org.ocelot.tunes4j.utils.GUIUtils;
import org.ocelot.tunes4j.utils.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.explodingpixels.macwidgets.LabeledComponentGroup;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.UnifiedToolBar;

@Component
public class ApplicationWindow extends JFrame {

	private static final long serialVersionUID = -7319833806607935833L;
	
	@Autowired
	private PlayListRepository playListDao;
	
	@Autowired
	private MediaTable mediaTable;
	
	@Autowired
	private RadioStationTable radioTable;

	//private LeftSplitPane leftSplitPane;
	
	private SplitPane leftSplitPane;
	
	
	private JSplitPane splitPane;

	private ApplicationMenuBar applicationMenuBar;
	
	private PlayerPanel audioPlayerPanel;

	private RadioPlayerPanel radioPlayerPanel;
	
	private UnifiedToolBar toolBar;
	
	private SourceListModel model = new SourceListModel();
	
	private	SourceListCategory playlistCategory = new SourceListCategory("Playlists");
	
	
	private void setApplicationIcons(ApplicationWindow window, Image image) {
		window.setIconImage(image);
		GUIUtils.setDockImage(image);
	}

	public void renderUI() {
		setApplicationIcons(this, ResourceLoader.ICON_APPICON.getImage());
		this.audioPlayerPanel = new PlayerPanel();
		this.radioPlayerPanel = new RadioPlayerPanel();
		this.toolBar = createUnifiedToolBar();
		this.splitPane = createSplitPane();
		
		setJMenuBar(createMenuBar());
		add(toolBar.getComponent(), BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 600);
		setLocationRelativeTo(null);
		loadPlayList();
		pack();
		GUIUtils.centerWindow(this);
		setVisible(true);
	}

	

	public JSplitPane createSplitPane() {
		//leftSplitPane = new LeftSplitPane(this);
		leftSplitPane = new  SplitPane(this);
		return leftSplitPane.create();
		//return leftSplitPane.getSplitPane();
	}

	private UnifiedToolBar createUnifiedToolBar() {


		UnifiedToolBar toolBar = new UnifiedToolBar();
		
		//this.mainPlayerPanel = this.audioPlayerPanel.getPlayerPanel();
		toolBar.addComponentToLeft(this.audioPlayerPanel.getPlayerPanel());
		toolBar.addComponentToLeft(this.radioPlayerPanel.getPlayerPanel());
		toolBar.addComponentToCenter(this.radioPlayerPanel.getMainDisplayPanel());
  		toolBar.addComponentToCenter(this.audioPlayerPanel.getMainDisplayPanel());
//		toolBar.addComponentToRight(MacButtonFactory
//				.makeUnifiedToolBarButton(new JButton("Advanced",
//						ResourceLoader.ICON_GEAR)));
		toolBar.addComponentToRight(new LabeledComponentGroup("search",
				new SearchText(mediaTable)).getComponent());
		
		toolBar.installWindowDraggerOnWindow(this);
		return toolBar;
	}

	public void loadPlayList() {
		List<PlayList> list = playListDao.findAll();
		if (CollectionUtils.isNotEmpty(list)) {
			for (PlayList row : list) {
				model.addItemToCategory(new SourceListItem(row.getName(),
						ResourceLoader.ICON_PLAYLIST), playlistCategory);
			}
		}
	}
	
	public MediaTable getMediaTable() {
		return this.mediaTable;
	}
	
	public void setMediaTable(MediaTable mediaTable) {
		this.mediaTable = mediaTable;
	}
	
	public RadioStationTable getRadioTable() {
		return this.radioTable;
	}

	public PlayerPanel getPlayerPanel() {
		return this.audioPlayerPanel;
	}
	
	public RadioPlayerPanel getRadioPlayerPanel() {
		return this.radioPlayerPanel;
	}
	
	public UnifiedToolBar getToolBar() {
		return this.toolBar;
	}

	public JMenuBar createMenuBar() {
		applicationMenuBar = new ApplicationMenuBar(this, leftSplitPane);
		return applicationMenuBar.createMenuBar();
	}
	
	public ApplicationMenuBar getApplicationMenuBar() {
		return applicationMenuBar;
	}
	

}
