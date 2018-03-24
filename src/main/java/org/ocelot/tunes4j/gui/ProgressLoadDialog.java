package org.ocelot.tunes4j.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.ocelot.tunes4j.dao.SongRepository;
import org.ocelot.tunes4j.dto.Song;


public class ProgressLoadDialog extends JDialog {

	private static final long serialVersionUID = 4226774539185420945L;

	private List<File> fileList;
	private JProgressBar progressBar;
	private JTable table;
    private JTextArea taskOutput;
    private ImportFilesTask task;
    private JFrame parentFrame;
    private SongRepository service;
    
    public ProgressLoadDialog(List<File> fileList,
    								JTable table, JFrame frame, 
    										String title, boolean modal,SongRepository service) {
    	super(frame, title, modal);
    	this.fileList = fileList;
    	this.table = table;
    	this.parentFrame = frame;
    	this.service = service;
    	initComponents();
    }

    /**
	 * This method initializes this
	 * @return void
	 */
	private void initComponents() {
		
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        //progressBar.setPreferredSize(new Dimension(10,10));
        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(progressBar);
        add(BorderLayout.CENTER, panel);
        add(BorderLayout.NORTH, new JLabel("Progress..."));
        setSize(300, 200);
        setLocationRelativeTo(parentFrame);
        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        executeTask();
        setVisible(true);
	
	}
    
	
	public void executeTask() {

//			task = new ImportFilesTask((DefaultTableModel) table.getModel(), 
//					strategy,fileList,service);
		
			BeanTableModel<Song> model = (BeanTableModel<Song>) table.getModel();
			task = new ImportFilesTask(model ,fileList,service);
			taskOutput.append("Importing...  \n");
			task.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					if ("progress".equals(evt.getPropertyName())) {
						progressBar.setValue(Integer.parseInt(evt.getNewValue() + ""));
		                taskOutput.append(evt.getNewValue() + " %\n");
		            }else if ("state".equals(evt.getPropertyName())
		                    && SwingWorker.StateValue.DONE == evt.getNewValue()) {
		            	setCursor(null);
		            	taskOutput.append("Done!\n");
		            	setVisible(false);
		            }
				}
			});
		//}
		task.execute();		
		
	}
	
    private  void createAndShowDialog() {

    	JFrame parentFrame = new JFrame("Principal");
    	//DefaultTableModel model = new DefaultTableModel(HeaderConstants.HEADER_NAMES,0);
        BeanTableModel< Song> model  = new BeanTableModel<Song>(Song.class);
    	JTable table = new JTable(model);
        JScrollPane panel = new JScrollPane(table);
        panel.setOpaque(true);
        parentFrame.add(panel, BorderLayout.CENTER);
        parentFrame.setSize(500, 400);
        parentFrame.setLocationRelativeTo(null);
        parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //parentFrame.pack();
        parentFrame.setVisible(true);
        List<File> list = new ArrayList<File>();
        //list.add(new File("C:\\Users\\Hugo\\AppData\\Local\\Ares\\My Shared Folder\\chidas espanol"));
        //list.add(new File("H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Navide�as"));
        //list.add(new File("H:/ORGANIZADO/MEDIA/AUDIO/MUSICA/VARIOS/Infantiles/Chavo.mp3"));
        
        new ProgressLoadDialog(list, table, parentFrame, "Progress Dialog", true, service);
        
    }

    public void onImportingData(List<File> list) {

	    	if (task==null) {
			BeanTableModel<Song> model = (BeanTableModel<Song>) table.getModel();
			task = new ImportFilesTask(model, list, service);
			task.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					if ("progress".equals(evt.getPropertyName())) {
						progressBar.setValue(Integer.parseInt(evt.getNewValue() + ""));
						taskOutput.append(evt.getNewValue() + " %\n"); 
					}else if ("state".equals(evt.getPropertyName())
							&& SwingWorker.StateValue.DONE == evt.getNewValue()) {
						setCursor(null);
						taskOutput.append("Done!\n");
						setVisible(false);
					}
				}
			});
	    	}
	}
        
    
 }