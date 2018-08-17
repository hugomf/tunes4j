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
	
//	private JPanel updateJPanel(JTree tree, SourceListItem item) {
//		
//		this.panel.setBackground(getBackgroundNonSelectionColor());
//		this.label.setForeground(getTextNonSelectionColor());
//	    
//		if(selected) {
//	      this.panel.setBackground(getBackgroundSelectionColor());
//	      this.label.setForeground(getTextSelectionColor());
//	    }
//	    panel.setLayout(new BorderLayout());
//	    System.out.println(tree.getWidth());
//	    this.label.setText(item.getName());
//		this.label.setIcon(item.getNodeIcon());
//	    panel.add(this.label);
//	    return this.panel;
//	}
	

}