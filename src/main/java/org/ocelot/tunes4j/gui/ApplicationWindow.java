package org.ocelot.tunes4j.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.ocelot.tunes4j.dao.PlayListRepository;
import org.ocelot.tunes4j.dto.PlayList;
import org.ocelot.tunes4j.event.PlayProgressEvent;
import org.ocelot.tunes4j.event.ProgressUpdateListener;
import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;
import org.ocelot.tunes4j.utils.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tritonus.share.sampled.AudioUtils;

import com.explodingpixels.macwidgets.LabeledComponentGroup;
import com.explodingpixels.macwidgets.MacButtonFactory;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.UnifiedToolBar;

@Component
public class ApplicationWindow extends JFrame {

	private static final long serialVersionUID = -7319833806607935833L;
	private LeftSplitPane leftSplitPane;
	private JSplitPane splitPane;
	private ApplicationMenuBar applicationMenuBar;
	
	@Autowired
	private PlayListRepository playListDao;
	
	@Autowired
	private MediaTable mediaTable;
	
	private Tunes4JAudioPlayer player;
	
	private boolean sliderValueLocked = false;
	
	private SourceListModel model = new SourceListModel();
	private ImageIcon imageIcon = ResourceLoader.ICON_MUSIC;
	private Icon playlistIcon = ResourceLoader.ICON_PLAYLIST;
	private	SourceListCategory playlistCategory = new SourceListCategory("Playlists");

	public void renderUI() {
		player = new Tunes4JAudioPlayer();
		UnifiedToolBar toolBar = createUnifiedToolBar();
		splitPane = createSplitPane();
		setJMenuBar(createMenuBar());
		add(toolBar.getComponent(), BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		loadPlayList();
		setIconImage(imageIcon.getImage());
		setVisible(true);
		pack();
		
	}

	public JSplitPane createSplitPane() {
		leftSplitPane = new LeftSplitPane(mediaTable);
		leftSplitPane.create();
		return leftSplitPane.getSplitPane();
	}

	private UnifiedToolBar createUnifiedToolBar() {


		UnifiedToolBar toolBar = new UnifiedToolBar();
		VolumePanel volPanel =  new VolumePanel(player);
		toolBar.addComponentToLeft(volPanel);
		toolBar.addComponentToCenter(getPlayerPanel());
		toolBar.addComponentToRight(MacButtonFactory
				.makeUnifiedToolBarButton(new JButton("Advanced",
						ResourceLoader.ICON_GEAR)));
		toolBar.addComponentToRight(new LabeledComponentGroup("search",
				new SearchText(mediaTable)).getComponent());
		
		toolBar.installWindowDraggerOnWindow(this);
		return toolBar;
	}

	private JComponent getPlayerPanel() {

		JPanel panel = new JPanel();
		JSlider slider = new JSlider();
		JLabel label = new JLabel();
		
		PlayButton playButton = new PlayButton(player, mediaTable);
		CloseButton closeButton = new CloseButton(player, slider, playButton);
		
		slider.setValue(0);
		slider.setMaximum(1000);	
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (slider.getValueIsAdjusting()) {
					sliderValueLocked = true;
					int value = slider.getValue();
					int totalBytes = (int) player.getProperties().get("mp3.length.bytes");
					long bytesread = (long) value * totalBytes /1000l;
					label.setText(getTimeProgress(bytesread));
				}
				
			}
		});
		slider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				JSlider source  = (JSlider) e.getSource();
				player.skip(source.getValue());
				sliderValueLocked = false;
			}

		});
		
		label.setText("00:00:00.00");
		label.setFont(new Font("Arial", Font.PLAIN, 15));
		player.addProgressUpdateListener(new ProgressUpdateListener() {
			
			@Override
			public void updateProgress(PlayProgressEvent e) {
				shouldWaitForJSliderUpdate(slider, label, e);
			}

		});
		
		panel.setLayout(new FlowLayout());
		panel.add(playButton);
		panel.add(closeButton);
		panel.add(slider);
		panel.add(label);
		return panel;
	}

	public void loadPlayList() {
		List<PlayList> list = playListDao.findAll();
		if (CollectionUtils.isNotEmpty(list)) {
			for (PlayList row : list) {
				System.out.println("row:" + row);
				model.addItemToCategory(new SourceListItem(row.getName(),
						playlistIcon), playlistCategory);
			}
		}
	}

	public JScrollPane getMediaTablePane() {
		return mediaTable.getTablePane();
	}

	public JMenuBar createMenuBar() {
		applicationMenuBar = new ApplicationMenuBar(mediaTable, leftSplitPane);
		return applicationMenuBar.createMenuBar();
	}
	
	public ApplicationMenuBar getApplicationMenuBar() {
		return applicationMenuBar;
	}
	
	private boolean isValueLocked() {
		return sliderValueLocked;
	}
	
	private void shouldWaitForJSliderUpdate(JSlider slider, final JLabel label, PlayProgressEvent e) {
		if(!isValueLocked()) {
			int totalBytes = (int) player.getProperties().get("mp3.length.bytes");
			int bytesread = e.getCurrentProgress().intValue();
			label.setText(getTimeProgress(bytesread));
			
			long value = bytesread * 1000l / totalBytes;
			slider.setValue((int) value);
		}
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
