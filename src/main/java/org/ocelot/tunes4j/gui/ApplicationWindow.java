package org.ocelot.tunes4j.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.ocelot.tunes4j.dao.PlayListRepository;
import org.ocelot.tunes4j.dto.PlayList;
import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;
import org.ocelot.tunes4j.utils.GUIUtils;
import org.ocelot.tunes4j.utils.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tritonus.share.sampled.AudioUtils;

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

	private LeftSplitPane leftSplitPane;
	
	private JSplitPane splitPane;

	private ApplicationMenuBar applicationMenuBar;
	
	private PlayerPanel playerPanel;
	
	private Tunes4JAudioPlayer player;
	
	private SourceListModel model = new SourceListModel();
	
	private	SourceListCategory playlistCategory = new SourceListCategory("Playlists");
	
	
	private void setApplicationIcons(ApplicationWindow window, Image image) {
		window.setIconImage(image);
		GUIUtils.setDockImage(image);
	}

	public void renderUI() {
		setApplicationIcons(this, ResourceLoader.ICON_APPICON.getImage());
		player = new Tunes4JAudioPlayer();
		playerPanel = new PlayerPanel(player);
		UnifiedToolBar toolBar = createUnifiedToolBar();
		splitPane = createSplitPane();
		setJMenuBar(createMenuBar());
		add(toolBar.getComponent(), BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		loadPlayList();
		setVisible(true);
		pack();
	}

	

	public JSplitPane createSplitPane() {
		leftSplitPane = new LeftSplitPane(mediaTable, radioTable);
		leftSplitPane.create();
		return leftSplitPane.getSplitPane();
	}

	private UnifiedToolBar createUnifiedToolBar() {


		UnifiedToolBar toolBar = new UnifiedToolBar();
		
		
		toolBar.addComponentToLeft(this.playerPanel.getPlayerPanel());
		toolBar.addComponentToCenter(this.playerPanel.getSliderPanel());
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
				System.out.println("row:" + row);
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
		return this.playerPanel;
	}

	public JMenuBar createMenuBar() {
		applicationMenuBar = new ApplicationMenuBar(this, leftSplitPane);
		return applicationMenuBar.createMenuBar();
	}
	
	public ApplicationMenuBar getApplicationMenuBar() {
		return applicationMenuBar;
	}
	
	public String getTimeProgress(long bytesread) {
		float frameRate = (float) player.getProperties().get("mp3.framerate.fps");
		int frameSize = (int) player.getProperties().get("mp3.framesize.bytes");
		long ms  = (long) AudioUtils.bytes2MillisD(bytesread, frameRate, frameSize);

		return DurationFormatUtils.formatDurationHMS(ms);
	}

	public Tunes4JAudioPlayer getPlayer() {
		return player;
	}

}
