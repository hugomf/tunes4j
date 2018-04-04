package org.ocelot.tunes4j.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

public class ApplicationMenuBar {

	private JMenuBar menuBar;
	
	private MediaTable mediaTable;
	
	private LeftSplitPane leftSplitPane;
	
	private JMenu fileMenu;

	public ApplicationMenuBar(MediaTable mediaTable, LeftSplitPane leftSplitPane) {

		this.mediaTable = mediaTable;
		this.leftSplitPane = leftSplitPane;

	}

	public JMenu createFileMenu() {

		fileMenu = new JMenu("File");
		JMenuItem importFolderItem = new JMenuItem("Import Folder ... ");
		importFolderItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("choosertitle");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
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
		newPlayListItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
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
				mediaTable.getTable().selectAll();
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
