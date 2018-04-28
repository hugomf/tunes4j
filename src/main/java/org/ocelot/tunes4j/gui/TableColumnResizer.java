package org.ocelot.tunes4j.gui;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class TableColumnResizer extends MouseInputAdapter{ 
    public static Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR); 
 
    private int mouseXOffset; 
    private Cursor otherCursor = resizeCursor; 
 
    private JTable table; 
 
    public TableColumnResizer(JTable table){ 
        this.table = table; 
        table.addMouseListener(this); 
        table.addMouseMotionListener(this); 
    }
    private boolean canResize(TableColumn column){ 
        return column != null 
                && table.getTableHeader().getResizingAllowed() 
                && column.getResizable(); 
    } 
 
    private TableColumn getResizingColumn(Point p){ 
        return getResizingColumn(p, table.columnAtPoint(p)); 
    } 
 
    private TableColumn getResizingColumn(Point p, int column){ 
        if(column == -1){ 
            return null; 
        } 
        int row = table.rowAtPoint(p); 
        if(row==-1) 
            return null; 
        Rectangle r = table.getCellRect(row, column, true); 
        r.grow( -3, 0); 
        if(r.contains(p)) 
            return null; 
 
        int midPoint = r.x + r.width / 2; 
        int columnIndex; 
        if(table.getTableHeader().getComponentOrientation().isLeftToRight()) 
            columnIndex = (p.x < midPoint) ? column - 1 : column; 
        else 
            columnIndex = (p.x < midPoint) ? column : column - 1; 
 
        if(columnIndex == -1) 
            return null; 
        return table.getTableHeader().getColumnModel().getColumn(columnIndex); 
    } 
 
    public void mousePressed(MouseEvent e){ 
        table.getTableHeader().setDraggedColumn(null); 
        table.getTableHeader().setResizingColumn(null); 
        table.getTableHeader().setDraggedDistance(0); 
 
        Point p = e.getPoint(); 
 
        int index = table.columnAtPoint(p); 
        if(index==-1) 
            return; 
 
        TableColumn resizingColumn = getResizingColumn(p, index); 
        if(!canResize(resizingColumn)) 
            return; 
 
        table.getTableHeader().setResizingColumn(resizingColumn); 
        if(table.getTableHeader().getComponentOrientation().isLeftToRight()) 
            mouseXOffset = p.x - resizingColumn.getWidth(); 
        else 
            mouseXOffset = p.x + resizingColumn.getWidth();
        
        if (e.getClickCount() >= 2) {
        		//System.out.println("e.getClickCount() :" + e.getClickCount() );
        		JTableHeader tableHeader = table.getTableHeader();
            int column = getIntendedColumn(tableHeader, e.getPoint());
            Rectangle r = tableHeader.getHeaderRect(column);
            r.grow(-3, 0);
            if(canResize(resizingColumn)) {
            	table.getColumnModel().getColumn(0).setWidth(200);
            }
          }
    } 
 
    private void swapCursor(){ 
        Cursor tmp = table.getCursor(); 
        table.setCursor(otherCursor); 
        otherCursor = tmp; 
    } 
 
    public void mouseMoved(MouseEvent e){ 
        if(canResize(getResizingColumn(e.getPoint())) 
           != (table.getCursor() == resizeCursor)){ 
            swapCursor(); 
        } 
    } 
 
    public void mouseDragged(MouseEvent e){ 
        int mouseX = e.getX(); 
 
        TableColumn resizingColumn = table.getTableHeader().getResizingColumn(); 
 
        boolean headerLeftToRight = 
                table.getTableHeader().getComponentOrientation().isLeftToRight(); 
 
        if(resizingColumn != null){ 
            int oldWidth = resizingColumn.getWidth(); 
            int newWidth; 
            if(headerLeftToRight){ 
                newWidth = mouseX - mouseXOffset; 
            } else{ 
                newWidth = mouseXOffset - mouseX; 
            } 
            resizingColumn.setWidth(newWidth); 
 
            Container container; 
            if((table.getTableHeader().getParent() == null) 
               || ((container = table.getTableHeader().getParent().getParent()) == null) 
                                || !(container instanceof JScrollPane)){ 
                return; 
            } 
 
            if(!container.getComponentOrientation().isLeftToRight() 
               && !headerLeftToRight){ 
                if(table != null){ 
                    JViewport viewport = ((JScrollPane)container).getViewport(); 
                    int viewportWidth = viewport.getWidth(); 
                    int diff = newWidth - oldWidth; 
                    int newHeaderWidth = table.getWidth() + diff; 
 
                    Dimension tableSize = table.getSize(); 
                    tableSize.width += diff; 
                    table.setSize(tableSize); 
 
                    if((newHeaderWidth >= viewportWidth) 
                       && (table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF)){ 
                        Point p = viewport.getViewPosition(); 
                        p.x = 
                                Math.max(0, Math.min(newHeaderWidth - viewportWidth, p.x + diff)); 
                        viewport.setViewPosition(p); 
 
                        mouseXOffset += diff; 
                    } 
                } 
            } 
        } 
    } 
 
    public void mouseReleased(MouseEvent e){ 
        table.getTableHeader().setResizingColumn(null); 
        table.getTableHeader().setDraggedColumn(null); 
    }
    
    private int getIntendedColumn(JTableHeader header, Point p)
    {
      p.move(p.x-3, p.y);
      return header.columnAtPoint(p);
    }
    
} 