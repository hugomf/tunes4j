package org.ocelot.tunes4j.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.SystemUtils;
import org.ocelot.tunes4j.player.Tunes4JAudioPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ApplicationMenuBar {
	
	private static Logger logger = LoggerFactory.getLogger(ApplicationMenuBar.class);

	private JMenuBar menuBar;
	private ApplicationWindow parentFrame;
	//private LeftSplitPane leftSplitPane;
	private SplitPane leftSplitPane;
	private JMenu fileMenu;
	

	public ApplicationMenuBar(ApplicationWindow parentFrame, SplitPane leftSplitPane) {

		this.parentFrame = parentFrame;
		this.leftSplitPane = leftSplitPane;
	}

	public JMenu buildFileMenu() {

		fileMenu = new JMenu("File");
		JMenuItem importFolderItem = buildImportFolderMenuItem();
		fileMenu.add(importFolderItem);
		
		JMenuItem addURLMenuItem = buildAddURLMenuItem();
		fileMenu.add(addURLMenuItem);
		
		
//		JMenuItem importFileItem = new JMenuItem("Add File(s) ...");
//		fileMenu.add(importFileItem);
//		fileMenu.add(new JSeparator());
		JMenuItem newPlayListItem = buildPlayListMenuItem();
		fileMenu.add(newPlayListItem);

//		JMenuItem newPlayListSelectItem = new JMenuItem("New PlayList From Selection");
//		fileMenu.add(newPlayListSelectItem);
//		JMenuItem newSmartPlayListItem = new JMenuItem("New SmartList PlayList");
//		fileMenu.add(newSmartPlayListItem);
		fileMenu.add(new JSeparator());
		JMenuItem exitItem = new JMenuItem("Exit");
		fileMenu.add(exitItem);
		exitItem.addActionListener(e -> System.exit(0));
		return fileMenu;
	}

	private JMenuItem buildAddURLMenuItem() {
		
		JMenuItem menuItem = new JMenuItem("Add URL");
		if(SystemUtils.IS_OS_WINDOWS) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
		} else if(SystemUtils.IS_OS_MAC_OSX) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.META_MASK));
		}
		menuItem.addActionListener(e -> new NetworkURLDialog(parentFrame));
		return menuItem;
	
	}

	private JMenuItem buildPlayListMenuItem() {
		JMenuItem newPlayListItem = new JMenuItem("New PlayList");
		if(SystemUtils.IS_OS_WINDOWS) {
			newPlayListItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		} else if(SystemUtils.IS_OS_MAC_OSX) {
			newPlayListItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.META_MASK));
		}
		newPlayListItem.addActionListener(e -> leftSplitPane.getSourceList().addPlaylist());
		return newPlayListItem;
	}

	private JMenuItem buildImportFolderMenuItem() {
		JMenuItem importFolderItem = new JMenuItem("Import Folder ... ");
		importFolderItem.addActionListener(e -> {
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
				new ProgressLoadDialog(fileList,parentFrame, true);
			} else {
				logger.info("No Selection ");
			}
		});
		return importFolderItem;
	}

	public JMenu buildEditMenu() {
		
		JMenu editMenu = new JMenu("Edit");
		JMenuItem selectAllItem = new JMenuItem("Select All");
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		selectAllItem.addActionListener(e -> {
				leftSplitPane.selectAllFromCategory();
		});
		JMenuItem deleteItem = new JMenuItem("Delete ");
		deleteItem.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
		deleteItem.setMnemonic(KeyEvent.VK_D);
		deleteItem.addActionListener(event->{
				leftSplitPane.removeItemFromCategory();
		});
		editMenu.setMnemonic(KeyEvent.VK_E);
		editMenu.add(selectAllItem);
		editMenu.add(deleteItem);
		return editMenu;
	}

	public JMenu buildViewMenu() {
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);

		// Get current theme and available themes from ThemeManager
		ThemeManager themeManager = ThemeManager.getInstance();
		String currentThemeName = themeManager.getCurrentTheme();
		List<ThemeDefinition> availableThemes = themeManager.getAvailableThemes();

		// Group theme items so only one can be selected at a time
		ButtonGroup themeGroup = new ButtonGroup();

		// Add available themes to menu
		for (ThemeDefinition theme : availableThemes) {
			JMenuItem themeItem = new JRadioButtonMenuItem(theme.getName());
			boolean isSelected = theme.getName().equals(currentThemeName) ||
								 (ThemeManager.SYSTEM_THEME.equals(currentThemeName) &&
								  (theme.getName().contains("Light") || theme.getName().contains("Default")) &&
								  !ThemeManager.isSystemDarkMode());
			themeItem.setSelected(isSelected);

			themeItem.addActionListener(e -> switchTheme(theme.getName()));
			themeGroup.add(themeItem);
			viewMenu.add(themeItem);
		}

		return viewMenu;
	}

    /**
     * Switch to a new theme and refresh the entire UI using a comprehensive approach
     */
    private void switchTheme(String theme) {
		System.out.println("🎨 ATTEMPTING THEME SWITCH: " + ThemeManager.getInstance().getCurrentTheme() + " → " + theme);

		// First, verify that theme loading and application work
		ThemeManager.getInstance().setTheme(theme);
		System.out.println("✅ Theme set in ThemeManager: " + ThemeManager.getInstance().getCurrentTheme());

		// Immediate check - is the theme actually applied?
		Object bgColor = javax.swing.UIManager.get("Panel.background");
		System.out.println("🔍 Panel.background after theme set: " + bgColor);

		// Force refresh on EDT with SYSTEM look and feel reset
		SwingUtilities.invokeLater(() -> {
			try {
				System.out.println("🔄 Starting comprehensive UI refresh...");

				// CRITICAL: Reset the Look and Feel to get our new colors
				try {
					// Get current L&F name
					javax.swing.LookAndFeel currentLAF = javax.swing.UIManager.getLookAndFeel();
					String lafName = currentLAF.getName();
					System.out.println("🔄 Resetting Look and Feel: " + lafName);

					// Force L&F to reload our UIManager colors
					javax.swing.UIManager.setLookAndFeel(currentLAF);

				} catch (javax.swing.UnsupportedLookAndFeelException ulafe) {
					System.err.println("❌ L&F reset failed: " + ulafe.getMessage());
				}

				// Meanwhile, visually prepare
				parentFrame.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

				// Brief pause to let L&F changes settle
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {}

				// Force repaint of every component we can find
				parentFrame.repaint();
				if (menuBar != null) menuBar.repaint();

				// Update ALL windows including hidden ones that might be used for menus
				for (java.awt.Window win : java.awt.Window.getWindows()) {
					if (win != null && win.isDisplayable()) {
						System.out.println("🔄 Refreshing window: " + win.getClass().getSimpleName());
						SwingUtilities.updateComponentTreeUI(win);
						win.revalidate();
						win.repaint();
					}
				}

				// Special handling for root pane components (which include the main UI)
				java.awt.Component[] rootComponents = parentFrame.getRootPane().getContentPane().getComponents();
				for (java.awt.Component comp : rootComponents) {
					System.out.println("🔄 Root component refresh: " + comp.getClass().getSimpleName());
					updateSpecificComponentRecursively(comp);
				}

				// Force specific components to refresh their UIManager colors
				SwingUtilities.invokeLater(() -> {
					System.out.println("🎨 REFRESHING SPECIFIC COMPONENTS");
					refreshThemeAwareComponents(parentFrame);
				});

				// Refresh test window if it exists
				ThemeManager.refreshTestWindow();

				// Final validation and repaint
				parentFrame.validate();
				parentFrame.revalidate();
				parentFrame.repaint();

				parentFrame.setCursor(java.awt.Cursor.getDefaultCursor());

				System.out.println("✅ THEME SWITCH COMPLETE: " + theme);
				System.out.println("🎨 Final verification - Panel.background: " + javax.swing.UIManager.get("Panel.background"));
				System.out.println("🔄 Test window refreshed: " + (ThemeManager.testWindow != null && ThemeManager.testWindow.isDisplayable() ? "Yes" : "No test window open"));

			} catch (Exception ex) {
				System.err.println("❌ Critical error during theme switch: " + ex.getMessage());
				ex.printStackTrace();
				parentFrame.setCursor(java.awt.Cursor.getDefaultCursor());
			}
		});
	}

	/**
	 * Recursively update specific component types with debugging
	 */
	private void updateSpecificComponentRecursively(java.awt.Component component) {
		if (component == null) return;

		// Log what component we're updating
		if (component instanceof javax.swing.JComponent) {
			System.out.println("🔄 Updating " + component.getClass().getSimpleName() + " (" + component.getName() + ")");
			component.revalidate();
			component.repaint();
		}

		// Special handling for tables (most critical component)
		if (component instanceof javax.swing.JTable) {
			javax.swing.JTable table = (javax.swing.JTable) component;
			System.out.println("📊 Double-refreshing JTable: " + table.getName());
			table.revalidate();
			table.repaint();
			table.revalidate(); // Double refresh for table data
			table.repaint();
		}

		// Recurse on children
		if (component instanceof java.awt.Container) {
			java.awt.Container container = (java.awt.Container) component;
			for (java.awt.Component child : container.getComponents()) {
				updateSpecificComponentRecursively(child);
			}
		}
	}

	/**
	 * Force a complete UI update for a given window, ensuring all components pick up theme changes
	 */
	private void forceCompleteUIUpdate(java.awt.Window window) {
		if (window != null) {
			// Multiple passes to ensure all components update
			SwingUtilities.updateComponentTreeUI(window);

			// Force repainting of all child components recursively
			updateComponentColorsRecursively(window);

			// Force layout and repaint
			window.revalidate();
			window.repaint();

			// Additional pass for good measure
			SwingUtilities.updateComponentTreeUI(window);
			if (window instanceof java.awt.Container) {
				((java.awt.Container) window).validate();
			}
			window.repaint();
		}
	}

	/**
	 * Comprehensive component refresh to ensure ALL Swing components pick up theme changes
	 */
	private void updateComponentColorsRecursively(java.awt.Component component) {
		if (component != null) {
			// Ensure JComponents are opaque and refresh their colors
			if (component instanceof javax.swing.JComponent) {
				javax.swing.JComponent jcomp = (javax.swing.JComponent) component;
				jcomp.setOpaque(true); // Critical for color display
				jcomp.putClientProperty("Nimbus.Overrides.InheritDefaults", false); // Force color inheritance
				jcomp.revalidate();
				jcomp.repaint();

				// Special handling for specific component types
				updateSpecificComponent(jcomp);
			}

			// Handle containers recursively
			if (component instanceof java.awt.Container) {
				java.awt.Container container = (java.awt.Container) component;

				// Recurse on children first (depth-first)
				for (java.awt.Component child : container.getComponents()) {
					updateComponentColorsRecursively(child);
				}

				// Then handle the container itself
				if (container instanceof java.awt.Container) {
					container.revalidate();
					container.repaint();
				}
			} else {
				// Non-container components
				component.revalidate();
				component.repaint();
			}
		}
	}

	/**
	 * Handle special cases for specific Swing component types
	 */
	private void updateSpecificComponent(javax.swing.JComponent component) {
		if (component instanceof javax.swing.JTable) {
			javax.swing.JTable table = (javax.swing.JTable) component;
			table.revalidate();
			table.repaint();
			// Force table to update its cell renderers
			table.getTableHeader().repaint();
			if (table.getTableHeader() != null) {
				table.getTableHeader().revalidate();
				table.getTableHeader().repaint();
			}

		} else if (component instanceof javax.swing.JButton) {
			// Force button to update its rendering
			java.awt.Component parent = component.getParent();
			if (parent != null) {
				parent.revalidate();
			}

		} else if (component instanceof javax.swing.JList) {
			// Force list to update its cell renderers
			javax.swing.JList<?> list = (javax.swing.JList<?>) component;
			javax.swing.ListModel<?> model = list.getModel();
			list.revalidate();

		} else if (component instanceof javax.swing.JTextField) {
			// Text fields need caret color refresh
			component.repaint();

		} else if (component instanceof javax.swing.JScrollBar) {
			// Scroll bars need immediate repaint
			component.repaint();

		} else if (component instanceof javax.swing.JMenuBar || component instanceof javax.swing.JMenuItem) {
			// Menu components need repaint
			component.repaint();

		} else if (component instanceof javax.swing.JTree) {
			// Tree components
			javax.swing.JTree tree = (javax.swing.JTree) component;
			tree.revalidate();

		} else if (component instanceof javax.swing.JSplitPane) {
			// Split panes
			javax.swing.JSplitPane split = (javax.swing.JSplitPane) component;
			split.revalidate();

		} else if (component instanceof javax.swing.JPanel) {
			// Custom panels and player components
			component.revalidate();
		}

		// Additional general properties to ensure color inheritance
		component.putClientProperty("HiliteColor", null); // Force color recalculations
		component.putClientProperty("SwingUtilities.updateComponentTreeUI", Boolean.TRUE);
	}

	/**
	 * Refresh specific components that use UIManager colors
	 * This directly calls refresh methods on components we know use theme colors
	 */
	private void refreshThemeAwareComponents(ApplicationWindow parentFrame) {
		System.out.println("🎨 Refreshing theme-aware components...");

		// Refresh MediaTable components (song table)
		if (parentFrame.getMediaTable() != null) {
			parentFrame.getMediaTable().refreshThemeColors();
			System.out.println("✅ Refreshed MediaTable colors");
		}

		// Refresh RadioStationTable components (radio table)
		if (parentFrame.getRadioTable() != null) {
			parentFrame.getRadioTable().refreshThemeColors();
			System.out.println("✅ Refreshed RadioStationTable colors");
		}

		// Refresh PlayerPanel components (contains SongDisplayPanel)
		if (parentFrame.getPlayerPanel() != null) {
			parentFrame.getPlayerPanel().refreshThemeColors();
		}

		// Also refresh any embedded components in containers
		refreshAllComponentColors(parentFrame);
	}



	/**
	 * Force ALL components in the application to refresh their UIManager-derived colors
	 * This ensures components that cache colors get refreshed after theme switches
	 */
	private void refreshAllComponentColors(java.awt.Container container) {
		if (container == null) return;

		for (java.awt.Component component : container.getComponents()) {
			if (component instanceof javax.swing.JComponent) {
				javax.swing.JComponent jcomp = (javax.swing.JComponent) component;
				System.out.println("🎨 Refreshing color-aware component: " + jcomp.getClass().getSimpleName());

				// Special handling for custom components we know use UIManager colors
				if (jcomp.getClass().getSimpleName().equals("SongDisplayPanel") ||
					jcomp instanceof javax.swing.JPanel ||
					jcomp instanceof javax.swing.JScrollPane ||
					jcomp instanceof javax.swing.JTable) {
					// Force the UIManager to recreate the component's UI delegate
					javax.swing.SwingUtilities.updateComponentTreeUI(jcomp);
					// Force repaint
					jcomp.revalidate();
					jcomp.repaint();
					System.out.println("  └─ Forced UI refresh");
				}
			}

			// Recurse on containers
			if (component instanceof java.awt.Container) {
				refreshAllComponentColors((java.awt.Container) component);
			}
		}
	}

	private void updateThemeCheckmarks() {
		// Menu checkmarks are handled automatically by the radio button group
		menuBar.revalidate();
		menuBar.repaint();
	}

	public JMenuBar createMenuBar() {
		menuBar = new JMenuBar();
		menuBar.add(buildFileMenu());
		menuBar.add(buildEditMenu());
		menuBar.add(buildViewMenu());
		menuBar.add(buildControlsMenu());
		return menuBar;
	}

	public JMenu buildControlsMenu() {
		JMenu controlsMenu = new JMenu("Controls");

		JMenuItem equalizerItem = new JMenuItem("Equalizer");
		equalizerItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		equalizerItem.addActionListener(e -> {
			try {
				PlayerPanel playerPanel = parentFrame.getPlayerPanel();
				if (playerPanel != null) {
					Tunes4JAudioPlayer player = playerPanel.getPlayer();
					new EqualizerDialog(parentFrame, player); // Use actual player if available
				} else {
					System.err.println("PlayerPanel is null - cannot open equalizer");
				}
			} catch (Exception ex) {
				System.err.println("Error opening equalizer: " + ex.getMessage());
				JOptionPane.showMessageDialog(parentFrame, "Error opening equalizer: " + ex.getMessage());
			}
		});
		controlsMenu.add(equalizerItem);

		// DEBUG: Add test window to prove theming system works
		JMenuItem testThemesItem = new JMenuItem("TEST Theme System (Proof It Works)");
		testThemesItem.setForeground(java.awt.Color.BLUE);
		testThemesItem.addActionListener(e -> {
			ThemeManager.showThemeTestWindow();
			javax.swing.JOptionPane.showMessageDialog(parentFrame,
				"Test window opened! When you switch themes, watch this window change colors.\n" +
				"If it changes → Theme system works perfectly!\n" +
				"If it doesn't → Something is broken.",
				"Theme System Test",
				javax.swing.JOptionPane.INFORMATION_MESSAGE);
		});
		controlsMenu.add(testThemesItem);

		return controlsMenu;
	}

	public JPopupMenu createPopUpMenu() {
		JPopupMenu popUpMenu = buildEditMenu().getPopupMenu();
		return popUpMenu;
	}
}
