package org.ocelot.tunes4j.playlist.view;

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
import org.ocelot.tunes4j.dto.PlayList;
import org.ocelot.tunes4j.event.PlaylistSelectedEvent;
import org.ocelot.tunes4j.gui.sourcelist.SourceListCategory;
import org.ocelot.tunes4j.gui.sourcelist.SourceListItem;
import org.ocelot.tunes4j.gui.sourcelist.SourceListModel;
import org.ocelot.tunes4j.utils.GUIUtils;
import org.ocelot.tunes4j.utils.PlayListNameGenerator;
import org.ocelot.tunes4j.utils.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

/**
 * Playlist View - Reactive UI Component for Playlist Tree Display.
 *
 * COPIED FROM: gui.sourcelist.SourceList.java
 * ENHANCED WITH: Reactive event-driven architecture using Observer Pattern
 *
 * Displays playlist hierarchy, handles selection events, and publishes PlaylistSelectedEvent
 * instead of using direct listeners.
 */
@Component
public class PlaylistView {

    private final ApplicationEventPublisher eventPublisher;

    private JTree tree;

    private JScrollPane scrollPane;

    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

    private DefaultTreeModel treeModel;

    private final SourceListModel sourceListModel;

    private String selectedItem = "NONE";

    // Field for reactive event handling (muted direct selection listeners)
    private boolean suppressDirectSelection = false;

    @Autowired
    public PlaylistView(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
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

    /**
     * React to playlist selection changes (received from other components).
     * This enables reactive synchronization of playlist selection across views.
     */
    @EventListener(PlaylistSelectedEvent.class)
    public void onPlaylistSelected(PlaylistSelectedEvent event) {
        if (event.getSource() == this) {
            return; // Avoid self-handling
        }

        // Update tree selection based on external playlist selection
        org.ocelot.tunes4j.playlist.model.Playlist playlist = event.getSelectedPlaylist();
        String playlistName = playlist.getName();

        System.out.println("🎵 PLAYLIST VIEW: Reactive selection of playlist - " + playlistName);

        // Find and select the corresponding tree node
        setSelectedItemByName(playlistName);
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
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(item);
            this.treeModel.insertNodeInto(itemNode, categoryNode, categoryNode.getChildCount());
        }
        expandPath(this.tree, new TreePath(categoryNode.getPath()));
    }

    private static void expandPath(final JTree tree, final TreePath path) {
        SwingUtilities.invokeLater(() -> {
            if ((tree == null) || (path == null))
                return;
            tree.expandPath(path);
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

        tree.putClientProperty("JTree.lineStyle", "None");
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);

        PlaylistCellRenderer renderer = new PlaylistCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);

        tree.setCellEditor(new PlaylistCellEditor(tree));
        tree.setBorder(BorderFactory.createEmptyBorder());
        tree.setCellRenderer(renderer);
        tree.setEditable(true);
        tree.setSelectionModel(new PlaylistSelectionModel());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);

        scrollPane = new JScrollPane(tree);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // ORIGINAL: Direct selection listener (commented out for reactive approach)
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

        // ENHANCED: Reactive event publishing instead of direct listeners
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (suppressDirectSelection) {
                    return; // Skip event publishing during reactive updates
                }

                TreePath path = e.getPath();
                Object node = path.getPathComponent(path.getPathCount() - 1);
                if (node instanceof DefaultMutableTreeNode) {
                    Object object = ((DefaultMutableTreeNode) node).getUserObject();
                    if (object instanceof SourceListItem) {
                        SourceListItem item = (SourceListItem) object;

                        // PUBLISH reactive event instead of direct callbacks
                        org.ocelot.tunes4j.playlist.model.Playlist selectedPlaylist = createPlaylistFromSourceListItem(item);
                        eventPublisher.publishEvent(new PlaylistSelectedEvent(this, selectedPlaylist));
                    }
                }
            }
        });
    }

    /**
     * Create a Playlist domain model from a SourceListItem.
     * In a real implementation, this would involve database lookup or service call.
     */
    private org.ocelot.tunes4j.playlist.model.Playlist createPlaylistFromSourceListItem(SourceListItem item) {
        // Create a temporary domain model for the event
        // In production, this would lookup the proper persisted playlist
        return new org.ocelot.tunes4j.playlist.model.Playlist(java.util.UUID.randomUUID().toString(), item.getName());
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
        libraryCategory.addItem( new SourceListItem("Music", ResourceLoader.ICON_MUSIC));
        libraryCategory.addItem( new SourceListItem("Radio Stations", ResourceLoader.ICON_RADIO));

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

    /**
     * Programmatically select a playlist item by name (used for reactive updates).
     */
    public void setSelectedItemByName(String itemName) {
        suppressDirectSelection = true;

        try {
            DefaultMutableTreeNode treeNode = findNodeByItemName(this.root, itemName);
            if (treeNode != null) {
                TreePath treePath = new TreePath(treeNode.getPath());
                this.tree.setSelectionPath(treePath);
                this.tree.scrollPathToVisible(treePath);
            }
        } finally {
            suppressDirectSelection = false;
        }
    }

    /**
     * Recursively search for a node by item name.
     */
    private DefaultMutableTreeNode findNodeByItemName(DefaultMutableTreeNode parentNode, String itemName) {
        if (parentNode.getUserObject() instanceof SourceListItem) {
            SourceListItem item = (SourceListItem) parentNode.getUserObject();
            if (itemName.equals(item.getName())) {
                return parentNode;
            }
        }

        if (parentNode.children().hasMoreElements()) {
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                DefaultMutableTreeNode foundNode = findNodeByItemName(childNode, itemName);
                if (foundNode != null) {
                    return foundNode;
                }
            }
        }

        return null;
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

    /**
     * Refresh theme colors when theme changes.
     */
    public void refreshThemeColors() {
        // Theme refresh logic would be implemented here
        System.out.println("🎨 PLAYLIST VIEW: Refreshing theme colors");
    }
}
