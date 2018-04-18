package org.ocelot.tunes4j.gui;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.SystemUtils;

public class ApplicationMenuBar {

	private JMenuBar menuBar;
	private ApplicationWindow parentFrame;
	private LeftSplitPane leftSplitPane;
	private JMenu fileMenu;
	

	public ApplicationMenuBar(ApplicationWindow parentFrame, LeftSplitPane leftSplitPane) {

		this.parentFrame = parentFrame;
		this.leftSplitPane = leftSplitPane;
	}

	public JMenu createFileMenu() {

		fileMenu = new JMenu("File");
		JMenuItem importFolderItem = new JMenuItem("Import Folder ... ");
		importFolderItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileFilter mp3Filter = new FileNameExtensionFilter("MP3 File", "mp3");
				chooser.addChoosableFileFilter(mp3Filter);
				chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
				chooser.setDialogTitle("Select MP3 File Format");
				chooser.setMultiSelectionEnabled(true);
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					
					List<File> fileList = new ArrayList<File>();
					if (chooser.getSelectedFile() != null) {
						fileList.add(chooser.getSelectedFile());
					} 
					
					if (chooser.getSelectedFiles() != null){
						File[] selectedFiles = chooser.getSelectedFiles();
						fileList = Arrays.asList(selectedFiles);
					}
					
					ProgressLoadDialog dialog = new ProgressLoadDialog(fileList,parentFrame, true);
				} else {
					System.out.println("No Selection ");
				}
			}
		});
		fileMenu.add(importFolderItem);
		JMenuItem importFileItem = new JMenuItem("Add File(s) ...");
		fileMenu.add(importFileItem);
		fileMenu.add(new JSeparator());
		JMenuItem newPlayListItem = new JMenuItem("New PlayList");
		
		if(SystemUtils.IS_OS_WINDOWS) {
			newPlayListItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		} else if(SystemUtils.IS_OS_MAC_OSX) {
			newPlayListItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.META_MASK));
		}
		
		fileMenu.add(newPlayListItem);
		newPlayListItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				leftSplitPane.createPlaylist();
			}
		});
		JMenuItem newPlayListSelectItem = new JMenuItem("New PlayList From Selection");
		fileMenu.add(newPlayListSelectItem);
		JMenuItem newSmartPlayListItem = new JMenuItem("New SmartList PlayList");
		fileMenu.add(newSmartPlayListItem);
		fileMenu.add(new JSeparator());
		JMenuItem exitItem = new JMenuItem("Exit");
		fileMenu.add(exitItem);
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		return fileMenu;
	}

	public JMenu createEditMenu() {
		JMenu editMenu = new JMenu("Edit");
		JMenuItem selectAllItem = new JMenuItem("Select All");
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectAllItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentFrame.getMediaTable().getTable().selectAll();
			}
		});
		JMenuItem deleteItem = new JMenuItem("Delete ");
		deleteItem.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
		deleteItem.setMnemonic(KeyEvent.VK_D);
		deleteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				leftSplitPane.removeItemFromCategory();
			}
		});
		editMenu.setMnemonic(KeyEvent.VK_E);
		editMenu.add(selectAllItem);
		editMenu.add(deleteItem);
		return editMenu;
	}

	public JMenuBar createMenuBar() {
		menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		return menuBar;
	}

	public JPopupMenu createPopUpMenu() {
		JPopupMenu popUpMenu = createEditMenu().getPopupMenu();
		return popUpMenu;
	}
}
