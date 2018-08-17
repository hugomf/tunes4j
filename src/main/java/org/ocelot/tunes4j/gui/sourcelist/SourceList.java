package org.ocelot.tunes4j.gui.sourcelist;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.ArrayUtils;
import org.assertj.core.util.Lists;
import org.ocelot.tunes4j.event.ItemSelectionEvent;
import org.ocelot.tunes4j.event.SourceListItemSelectionListener;
import org.ocelot.tunes4j.utils.GUIUtils;
import org.ocelot.tunes4j.utils.PlayListNameGenerator;
import org.ocelot.tunes4j.utils.ResourceLoader;

public class SourceList {

	private JTree tree;

	private JScrollPane scrollPane;

	private List<SourceListItemSelectionListener> selectionListeners = Lists.newArrayList();

	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

	private DefaultTreeModel treeModel;
	
	private final SourceListModel sourceListModel;
	
	private String selectedItem = "NONE";

	@SuppressWarnings("serial")
	public SourceList() {
		this.sourceListModel = createSourceListModel();
		this.treeModel = new DefaultTreeModel(root) {
			@Override
			public void valueForPathChanged(TreePath path, Object newValue) {
				Object obj = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject(); 
				((SourceListItem)obj).setName(newValue.toString()); 
				super.valueForPathChanged(path, obj);
			}
		};
		
		
		initUI();
		
		transferModelToTree(this.sourceListModel);
	}

//	public SourceList(SourceListModel model) {
//		this.sourceListModel = model;
//
//		initUI();
//		
//		transferModelToTree(model);
//		
//	}
	
	public SourceListModel getSourceListModel() {
		return this.sourceListModel;
	}

	private void transferModelToTree(SourceListModel model) {

		for (int i = 0; i < model.getCategories().size(); i++) {
			addCategoryToTreeModel(model.getCategories().get(i), i);
		}

	}

	private void addCategoryToTreeModel(SourceListCategory category, int index) {
		DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(category);
		this.treeModel.insertNodeInto(categoryNode, this.root, index);
		for (SourceListItem item : category.getItems()) {
			DefaultMutableTreeNode itemNode = new 	DefaultMutableTreeNode(item);
			this.treeModel.insertNodeInto(itemNode, categoryNode, categoryNode.getChildCount());
		}
		expandPath(this.tree, new TreePath(categoryNode.getPath()));
	}
	
	
	private static void expandPath(final JTree tree, final TreePath path) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if ((tree == null) || (path == null))
					return;
				tree.expandPath(path);
			}
		});
	}
	
	
	private static DefaultMutableTreeNode getNodeForObject(DefaultMutableTreeNode parentNode, Object userObject) {
		if (parentNode.getUserObject().equals(userObject)) {
			return parentNode;
		} else if (parentNode.children().hasMoreElements()) {
			for (int i = 0; i < parentNode.getChildCount(); i++) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
				DefaultMutableTreeNode retVal = getNodeForObject(childNode, userObject);
				if (retVal != null) {
					return retVal;
				}
			}
		} else {
			return null;
		}
		return null;
	}


	
	public void initUI() {

		tree = new JTree(treeModel);

		// tree.putClientProperty("Quaqua.Tree.style", "sourceList");
		tree.putClientProperty("JTree.lineStyle", "None");
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);

		SourceListCellRenderer renderer = new SourceListCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);

		tree.setCellEditor(new SourceListCellEditor(tree));
		tree.setBorder(BorderFactory.createEmptyBorder());
		tree.setCellRenderer(renderer);
		tree.setEditable(true);
		tree.setSelectionModel(new SourceListSelectionModel());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);

		scrollPane = new JScrollPane(tree);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		tree.addTreeSelectionListener(event-> {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			String name = ((SourceListItem) node.getUserObject()).getName();
		    selectedItem = name;
		});
		
		tree.setUI(new javax.swing.plaf.basic.BasicTreeUI() {

			@Override
			public Rectangle getPathBounds(JTree tree, TreePath path) {
				if (tree != null && treeState != null) {
					return getPathBounds(path, tree.getInsets(), new Rectangle());
				}
				return null;
			}

			private Rectangle getPathBounds(TreePath path, Insets insets, Rectangle bounds) {
				bounds = treeState.getBounds(path, bounds);
				if (bounds != null) {
					bounds.width = tree.getWidth();
					bounds.y += insets.top;
				}
				return bounds;
			}

			@Override
			protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path,
					int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
				if (tree.isRowSelected(row)) {
					g.setColor(renderer.getBackgroundSelectionColor());
					g.fillRect(0, row * tree.getRowHeight(), tree.getWidth(), tree.getRowHeight());
				}

				super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
			}

		});

		tree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getPath();
				Object node = path.getPathComponent(path.getPathCount() - 1);
				if (node instanceof DefaultMutableTreeNode) {
					Object object = ((DefaultMutableTreeNode) node).getUserObject();
					if (object instanceof SourceListItem) {
						SourceListItem item = (SourceListItem) object;
						fireSourceListItemSelectEvents(item);
					}
				}
			}
		});

	}

	public void addSourceListSelectionListener(SourceListItemSelectionListener listener) {
		this.selectionListeners.add(listener);
	}

	protected void fireSourceListItemSelectEvents(SourceListItem item) {
		for (SourceListItemSelectionListener listener : selectionListeners) {
			ItemSelectionEvent event = new ItemSelectionEvent(this.tree, item);
			listener.selectItem(event);
		}
	}

	public void deletePlaylist() {

		TreePath[] paths = tree.getSelectionPaths();
		if (ArrayUtils.isEmpty(paths))
			return;

		DefaultTreeModel model = (DefaultTreeModel) (tree.getModel());
		DefaultMutableTreeNode node;
		for (int i = 0; i < paths.length; i++) {
			node = (DefaultMutableTreeNode) (paths[i].getLastPathComponent());
			SourceListItem item = (SourceListItem) node.getUserObject();
			if (item.isEditable()) {
				model.removeNodeFromParent(node);
				tree.updateUI();
			}
		}
	}

	
	SourceListCategory libraryCategory = new SourceListCategory("Library");
	SourceListCategory playlistCategory = new SourceListCategory("Playlist");

	public SourceListModel createSourceListModel() {
		
		//SourceListCategory libraryCategory = new SourceListCategory("Library");
		libraryCategory.addItem( new SourceListItem("Music", ResourceLoader.ICON_MUSIC));
		libraryCategory.addItem( new SourceListItem("Radio Stations", ResourceLoader.ICON_RADIO));
		
		
		//SourceListCategory playlistCategory = new SourceListCategory("Playlist");
		playlistCategory.addItem( new SourceListItem("Smart Playlist", ResourceLoader.ICON_SMARTPLAYLIST));
		playlistCategory.addItem( new SourceListItem("Playlist 1", ResourceLoader.ICON_PLAYLIST, true));
	
	    
		SourceListModel model = new SourceListModel();
		model.addCategory(libraryCategory);
		model.addCategory(playlistCategory);

		return model;
	}

	
	public void addPlaylist() {
		String[] names = playlistCategory.getItems().stream().map(SourceListItem::getName).toArray(String[]::new);
		String playlistName = PlayListNameGenerator.getInstance().findNext(names);
		SourceListItem item = new SourceListItem(playlistName, ResourceLoader.ICON_PLAYLIST, true);
		addEditableSourceListItem(playlistCategory, item);
	}
	
	
	public void addEditableSourceListItem(SourceListCategory category, SourceListItem item) {
		DefaultMutableTreeNode categoryNode = getNodeForObject(this.root, category);
		DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(item);
		this.treeModel.insertNodeInto(itemNode, categoryNode, categoryNode.getChildCount());
		category.addItem(item);
		TreePath treePath = new TreePath(this.treeModel.getPathToRoot(itemNode));
		this.tree.scrollPathToVisible(treePath);
		this.tree.setSelectionPath(treePath);
		this.tree.startEditingAtPath(treePath);
	}
	
	public void setSelectedItem(SourceListItem item) {
		this.sourceListModel.validateItemIsInModel(item);
		DefaultMutableTreeNode treeNode = getNodeForObject(this.root, item);
		this.tree.setSelectionPath(new TreePath(treeNode.getPath()));
	}	


	public JTree getTree() {
		return this.tree;
	}

	public JScrollPane getContentPane() {
		return this.scrollPane;
	}
	
	public String getSelectedItem() {
		return this.selectedItem;
	}

//	private void addNodesToCategory(DefaultMutableTreeNode categoryNode, SourceListItem[] items) {
//		for (SourceListItem item : items) {
//			DefaultMutableTreeNode nodeItem = new DefaultMutableTreeNode(item);
//			categoryNode.add(nodeItem);
//		}
//	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					// UIManager.setLookAndFeel("org.violetlib.aqua.AquaLookAndFeel");
					// UIManager.setLookAndFeel(UIManager.getLookAndFeel());
					SourceList panel = new SourceList();
					panel.getTree().addKeyListener(new KeyAdapter() {

						@Override
						public void keyPressed(KeyEvent e) {

							if ((e.getKeyCode() == KeyEvent.VK_N) && ((e.getModifiers() & KeyEvent.VK_META) != 0)) {
								panel.addPlaylist();
							}

							if ((e.getKeyCode() == KeyEvent.VK_N) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
								panel.addPlaylist();
							}

							if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
									&& ((e.getModifiers() & KeyEvent.VK_META) != 0)) {
								panel.deletePlaylist();
							}

							if ((e.getKeyCode() == KeyEvent.VK_DELETE)
									&& ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
								panel.deletePlaylist();
							}
						}

					});
					JFrame frame = new JFrame();
					frame.getRootPane().putClientProperty("Aqua.backgroundStyle", "vibrantUltraDark");
					frame.add(panel.getContentPane());
					frame.setPreferredSize(new Dimension(300, 400));
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setTitle("JTree Example");
					frame.pack();
					GUIUtils.centerWindow(frame);
					frame.setVisible(true);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
