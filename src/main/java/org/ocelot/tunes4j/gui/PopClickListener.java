package org.ocelot.tunes4j.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

class PopClickListener extends MouseAdapter { 
	
	MediaTable mediaTable;
	JPopupMenu menu; 
	
	public PopClickListener(MediaTable mediaTable) {
		this.mediaTable = mediaTable;
	}

    public void mousePressed(MouseEvent e){ 
        if (e.isPopupTrigger()) 
            doPop(e); 
    } 
 
    public void mouseReleased(MouseEvent e){ 
        if (e.isPopupTrigger()) 
            doPop(e); 
    } 
 
    private void doPop(MouseEvent e){ 
    		menu = mediaTable.getApplicationWindow().getApplicationMenuBar().createPopUpMenu();
        menu.show(e.getComponent(), e.getX(), e.getY()); 
    } 
} 