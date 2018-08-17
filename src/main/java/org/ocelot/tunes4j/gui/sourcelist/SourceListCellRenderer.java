package org.ocelot.tunes4j.gui.sourcelist;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

@SuppressWarnings("serial")
class SourceListCellRenderer extends DefaultTreeCellRenderer {
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		Object o = ((DefaultMutableTreeNode) value).getUserObject();
		if (o instanceof SourceListItem) {
			SourceListItem item = (SourceListItem) o;
		    label.setText(item.getName());
			label.setIcon(item.getNodeIcon());
		}
		if (o instanceof SourceListCategory) {
			SourceListCategory category = (SourceListCategory) o;
		    label.setText(category.getName());
		}
		return label;
		
	}
	
	

}