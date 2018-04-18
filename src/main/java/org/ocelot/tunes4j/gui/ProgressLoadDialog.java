package org.ocelot.tunes4j.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.ocelot.tunes4j.utils.GUIUtils;

public class ProgressLoadDialog extends JDialog {

	private static final long serialVersionUID = 4226774539185420945L;

	private List<File> fileList;
	private JProgressBar progressBar;
	private JTextArea taskOutput;
	private ImportFilesTask task;
	private ApplicationWindow parentFrame;

	public ProgressLoadDialog(List<File> fileList, ApplicationWindow parentFrame, boolean modal) {
		super(parentFrame,  modal);
		this.fileList = fileList;
		this.parentFrame = parentFrame;
		initComponents();
	}

	private void initComponents() {

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		taskOutput = new JTextArea(5, 20);
		taskOutput.setMargin(new Insets(5, 5, 5, 5));
		taskOutput.setEditable(false);
		taskOutput.setVisible(true);

		JPanel panel = new JPanel();
		panel.add(progressBar);
		add(BorderLayout.CENTER, panel);
		add(BorderLayout.NORTH, new JLabel("Progress..."));

		setUndecorated(true);
		setSize(300, 200);
		setLocationRelativeTo(parentFrame);
		add(panel, BorderLayout.PAGE_START);
		// add(new JScrollPane(taskOutput), BorderLayout.CENTER); // TO DEBUG ONLY
		executeTask();
		GUIUtils.setRoundedWindow(this, 8, 8);
		pack();
		setVisible(true);

	}

	private void executeTask() {
		task = new ImportFilesTask(this.fileList, this.parentFrame.getMediaTable());
		taskOutput.append("Importing...  \n");
		task.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					progressBar.setValue(Integer.parseInt(evt.getNewValue() + ""));
					taskOutput.append(evt.getNewValue() + " %\n");
				} else if ("state".equals(evt.getPropertyName())
						&& SwingWorker.StateValue.DONE == evt.getNewValue()) {
					setCursor(null);
					taskOutput.append("Done!\n");
					setVisible(false);
				}
			}
		});
		task.execute();
	}
	
	

	// private void createAndShowDialog() {
	//
	// JFrame parentFrame = new JFrame("Principal");
	// //DefaultTableModel model = new
	// DefaultTableModel(HeaderConstants.HEADER_NAMES,0);
	// BeanTableModel< Song> model = new BeanTableModel<Song>(Song.class);
	// JTable table = new JTable(model);
	// JScrollPane panel = new JScrollPane(table);
	// panel.setVisible(true);
	// panel.setOpaque(true);
	// parentFrame.add(panel, BorderLayout.CENTER);
	// parentFrame.setSize(500, 250);
	// parentFrame.setLocationRelativeTo(null);
	// parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// //parentFrame.pack();
	// parentFrame.setVisible(true);
	// List<File> list = new ArrayList<File>();
	// //list.add(new File("C:\\Users\\Hugo\\AppData\\Local\\Ares\\My Shared
	// Folder\\chidas espanol"));
	// //list.add(new File("H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Navide�as"));
	// //list.add(new
	// File("H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Infantiles/Chavo.mp3"));
	//
	// new ProgressLoadDialog(list, table, parentFrame, "Progress Dialog", true,
	// service);
	//
	// }



}