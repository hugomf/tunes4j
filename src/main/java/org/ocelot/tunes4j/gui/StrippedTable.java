package org.ocelot.tunes4j.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


@SuppressWarnings("serial")
public class StrippedTable extends JTable
{

  private static final Color BACKGROUND_COLOR =  new Color(0.95f,0.96f,0.98f);


public StrippedTable( String[][] data, String[] fields )
  {
    super( data, fields );
    setFillsViewportHeight( true ); //to show the empty space of the table 
  }


  public StrippedTable(BeanTableModel<?> model) {
	  super(model);
	  setShowGrid(false);
  }


@Override
  public void paintComponent( Graphics g )
  {
    super.paintComponent( g );

    paintEmptyRows( g );
  }


  public void paintEmptyRows( Graphics g )
  {
    Graphics newGraphics = g.create();
    newGraphics.setColor( UIManager.getColor( "Table.gridColor" ) );

    Rectangle rectOfLastRow = getCellRect( getRowCount() - 1, 0, true );
    int firstNonExistentRowY = rectOfLastRow.y; //the top Y-coordinate of the first empty tablerow

    if ( getVisibleRect().height > firstNonExistentRowY ) //only paint the grid if empty space is visible
    {
      //fill the rows alternating and paint the row-lines:
      int rowYToDraw = (firstNonExistentRowY - 1) + getRowHeight(); //minus 1 otherwise the first empty row is one pixel to high
      int actualRow = getRowCount() - 1; //to continue the stripes from the area with table-data

      while ( rowYToDraw < getHeight() )
      {
        if ( actualRow % 2 == 0 ) {
          newGraphics.setColor( BACKGROUND_COLOR ); //change this to another color (Color.YELLOW, anyone?) to show that only the free space is painted
          newGraphics.fillRect( 0, rowYToDraw, getWidth(), getRowHeight() );
          newGraphics.setColor( UIManager.getColor( "Table.gridColor" ) );
        }
        newGraphics.drawLine( 0, rowYToDraw, getWidth(), rowYToDraw );
        rowYToDraw += getRowHeight();
        actualRow++;
      }


      //paint the column-lines:
      int x = 0;
      for ( int i = 0; i < getColumnCount(); i++ ) {
        TableColumn column = getColumnModel().getColumn( i );
        x += column.getWidth(); //add the column width to the x-coordinate
        newGraphics.drawLine( x - 1, firstNonExistentRowY, x - 1, getHeight() );
      }

      newGraphics.dispose();

    } //if empty space is visible

  } //paintEmptyRows


  @Override
  public Component prepareRenderer( TableCellRenderer renderer, int row, int column )
  {
    Component c = super.prepareRenderer( renderer, row, column );

    if ( !isRowSelected( row ) )
    {
      c.setBackground( row % 2 == 0 ? getBackground() : BACKGROUND_COLOR );
    }

    return c;
  }


  public static void main( String[] argv )
  {
    String data[][] = { { "A0", "B0", "C0" }, { "A1", "B1", "C1" }, { "A2", "B2", "C2" }, { "A3", "B3", "C3" }, { "A4", "B4", "C4" } };
    String fields[] = { "A", "B", "C" };

    JFrame frame = new JFrame( "a JTable with striped empty space" );
    StrippedTable table = new StrippedTable( data, fields );
    JScrollPane pane = new JScrollPane( table );

    frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    frame.add( pane );
    frame.setSize( 400, 300 );
    frame.setLocationRelativeTo( null );
    frame.setVisible( true );
  }

}