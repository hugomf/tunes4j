package org.ocelot.tunes4j.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.ocelot.tunes4j.event.ItemSelectionEvent;
import org.ocelot.tunes4j.event.SourceListItemSelectionListener;
import org.ocelot.tunes4j.gui.sourcelist.SourceList;
import org.ocelot.tunes4j.gui.sourcelist.SourceListItem;
import org.springframework.beans.factory.annotation.Autowired;

public class SplitPane  {

	private SourceList sourceListPanel = new SourceList();

	private ApplicationWindow parentFrame;

	private JSplitPane plane;


	@Autowired
	public SplitPane(ApplicationWindow window) {
		this.parentFrame = window;
	}

	public JSplitPane create() {
		
		
		plane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				sourceListPanel.getContentPane(),
					this.parentFrame.getMediaTable().getTablePane());
		
		plane.setDividerLocation(300);
		//splitPane.putClientProperty("Quaqua.SplitPane.style","bar");
		plane.setDividerSize(1);
		plane.setContinuousLayout(true);
        ((BasicSplitPaneUI) plane.getUI()).getDivider().setBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(0xa5a5a5)));
        plane.setBorder(BorderFactory.createEmptyBorder());

        Dimension leftMinimumSize = new Dimension(200,0);
		plane.getLeftComponent().setMinimumSize(leftMinimumSize);
		
		Dimension rightMinimumSize = new Dimension(350,0);
		plane.getRightComponent().setMinimumSize(rightMinimumSize);
		
		plane.setUI(new BasicSplitPaneUI() {
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    public void setBorder(Border b) {
                    		Color oldColor = getBackground();
                    	 	Border transparentBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
                    		super.setBorder(transparentBorder);
                    }
                };
            }
        });
		
		sourceListPanel.addSourceListSelectionListener(new SourceListItemSelectionListener() {
			
			@Override
			public void selectItem(ItemSelectionEvent event) {
				SourceListItem item = event.getSourceListItem();
				String name = item.getName();
				int dividerLocation = plane.getDividerLocation();
				switch (name) {
					case "Music":
						plane.setRightComponent(parentFrame.getMediaTable().getTablePane());
						parentFrame.getPlayerPanel().show();
						parentFrame.getRadioPlayerPanel().hide();
						break;
					case "Radio Stations":
						plane.setRightComponent(parentFrame.getRadioTable().getTablePane());
						parentFrame.getPlayerPanel().hide();
						parentFrame.getRadioPlayerPanel().show();
						break;
//					default:
//						splitPane.setRightComponent(dummyPanel);
				}
				plane.getRightComponent().setMinimumSize(rightMinimumSize);
				plane.setDividerLocation(dividerLocation);
			}
		});
		
		
		SwingUtilities.invokeLater(() -> {    
			SourceListItem item = sourceListPanel.getSourceListModel().getCategories().get(0).getItems().get(0);
		    sourceListPanel.setSelectedItem(item);
		    
		});
		
		return this.plane;
	}

	public SourceList getSourceList() {
		return this.sourceListPanel;
	}

	
	public void selectAllFromCategory() {
		
		
		String selectedItem = sourceListPanel.getSelectedItem();
		if (selectedItem.equals("NONE")) return;
		
		if (selectedItem.equals("Music")) {
			this.parentFrame.getMediaTable().getTable().selectAll();
		} else {
			this.parentFrame.getRadioTable().getTable().selectAll();;
		}
		
	}
	
	public void removeItemFromCategory() {
		
		
		String selectedItem = sourceListPanel.getSelectedItem();
		if (selectedItem.equals("NONE")) return;

		if (selectedItem.equals("Music")) {
			this.parentFrame.getMediaTable().removeSelectedItems();
		} else if (selectedItem.equals("Radio Stations")) {
			this.parentFrame.getRadioTable().removeSelectedItems();
		} else {
			//fSourceList.getModel().removeItemFromCategory(fSourceList.getSelectedItem(), playlistCategory);
		}
	}
	

}
