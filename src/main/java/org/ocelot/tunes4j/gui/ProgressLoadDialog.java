package org.ocelot.tunes4j.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.ocelot.tunes4j.components.ProgressCircleUI;
import org.ocelot.tunes4j.utils.GUIUtils;

public class ProgressLoadDialog extends JDialog {

	private static final long serialVersionUID = 4226774539185420945L;

	private List<File> fileList;
	private JProgressBar progressBar;
	private ImportFilesTask task;
	private ApplicationWindow parentFrame;

	public ProgressLoadDialog(List<File> fileList, ApplicationWindow parentFrame, boolean modal) {
		super(parentFrame, modal);
		this.fileList = fileList;
		this.parentFrame = parentFrame;
		initComponents();
	}
	

	private void initComponents() {

		setLayout(new FlowLayout());
		progressBar = ProgressCircleUI.makeUI(Color.BLUE);
		setUndecorated(true);
		setLocationRelativeTo(this.parentFrame);
		add(new JLabel("Importing..."));
		add(progressBar);
		executeTask();
		setSize(new Dimension(180, 180));
		setPreferredSize(new Dimension(180, 180));
		pack();
		GUIUtils.setRoundedWindow(this, 8, 8);
		setVisible(true);

	}

	private void executeTask() {
		task = new ImportFilesTask(this.fileList, this.parentFrame.getMediaTable());
		task.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					progressBar.setValue(Integer.parseInt(evt.getNewValue() + ""));
				} else if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE == evt.getNewValue()) {
					setCursor(null);
					setVisible(false);
				}
			}
		});
		task.execute();
	}

//	public static void main(String[] args) {
//	
//		ApplicationWindow parentFrame = new ApplicationWindow();
//		BeanTableModel<Song> model = new BeanTableModel<Song>(Song.class);
//		JTable table = new JTable(model);
//		JScrollPane panel = new JScrollPane(table);
//		panel.setVisible(true);
//		panel.setOpaque(true);
//		parentFrame.add(panel, BorderLayout.CENTER);
//		parentFrame.setSize(500, 250);
//		parentFrame.setLocationRelativeTo(null);
//		parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		// parentFrame.pack();
//		parentFrame.setVisible(true);
//		parentFrame.setMediaTable(new MediaTable());
//		
//		
//		List<File> list = new ArrayList<File>();
//		 list.add(new File("/Users/hugo/MEGA/SyncDrive"));
//		// list.add(new File("H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Navide�as"));
//		// list.add(new
//		// File("H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Infantiles/Chavo.mp3"));
//
//		new ProgressLoadDialog(list, parentFrame , true);
//
//	}

}