package org.ocelot.tunes4j.gui.sourcelist;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")
class SourceListCellEditor extends DefaultCellEditor {

	protected SourceListItem sourceListItem;
	
	private JTree tree;
	
	public SourceListCellEditor(JTree tree) {
	    super(new JTextField());
	    this.tree = tree;
	}
	
	
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row) {
		JTextField editor = null;
		sourceListItem = getSourceListItem(value);
		if (sourceListItem != null) {
			editor = (JTextField) (super.getComponent());
			editor.setText(sourceListItem.getName());
		}
		return editor;
	}

	
	public SourceListItem getSourceListItem(Object value) {
	    if (value instanceof DefaultMutableTreeNode) {
	      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	      Object userObject = node.getUserObject();
	      if (userObject instanceof SourceListItem) {
	        return (SourceListItem) userObject;
	      }
	    }
	    return null;
	  }
	
	@Override
	public boolean isCellEditable(EventObject event) {
		boolean retValue = super.isCellEditable(event);
		if (event instanceof MouseEvent && retValue) {
			MouseEvent mouseEvent = (MouseEvent) event;
			TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			if (path != null) {
				Object node = path.getLastPathComponent();
				System.out.println(node);
				if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
					if (treeNode.getUserObject() instanceof SourceListItem) {
						SourceListItem sourceItem = (SourceListItem) treeNode.getUserObject();
						return  sourceItem.isEditable();
					}
				}
			}
			return false;
		}
		return retValue;
	}

}