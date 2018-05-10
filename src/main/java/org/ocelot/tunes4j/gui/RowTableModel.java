package org.ocelot.tunes4j.gui;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;


abstract class RowTableModel<T> extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	protected List<T> modelData;
	protected List<String> columnNames;
	protected Class<T>[] columnClasses;
	protected Boolean[] isColumnEditable;
	private Class<T> rowClass;
	private boolean isModelEditable = true;


	protected RowTableModel(Class<T> rowClass)
	{
		setRowClass( rowClass );
	}


	protected RowTableModel(List<String> columnNames)
	{
		this(new BasicEventList<T>(), columnNames);
	}

	protected RowTableModel(List<T> modelData, List<String> columnNames)
	{
		setDataAndColumnNames(modelData, columnNames);
	}

	protected RowTableModel(EventList<T> modelData, List<String> columnNames, Class<T> rowClass)
	{
		setDataAndColumnNames(modelData, columnNames);
		setRowClass( rowClass );
	}

	protected void setDataAndColumnNames(List<T> modelData, List<String> columnNames)
	{
		this.modelData = modelData;
		this.columnNames = columnNames;
		columnClasses = new Class[getColumnCount()];
		isColumnEditable = new Boolean[getColumnCount()];
		fireTableStructureChanged();
	}

	protected void setRowClass(Class<T> rowClass)
	{
		this.rowClass = rowClass;
	}

	public Class<?> getColumnClass(int column)
	{
		Class<?> columnClass = null;


		if (column < columnClasses.length)
			columnClass = columnClasses[column];


		if (columnClass == null)
			columnClass = super.getColumnClass(column);

		return columnClass;
	}

	public int getColumnCount()
	{
		return columnNames.size();
	}

	public String getColumnName(int column)
	{
		Object columnName = null;

		if (column < columnNames.size())
		{
			columnName = columnNames.get( column );
		}

		return (columnName == null) ? super.getColumnName( column ) : columnName.toString();
	}

	public int getRowCount()
	{
		return modelData.size();
	}

	public boolean isCellEditable(int row, int column)
	{
		Boolean isEditable = null;


		if (column < isColumnEditable.length)
			isEditable = isColumnEditable[column];

		return (isEditable == null) ? isModelEditable : isEditable.booleanValue();
	}

	public void addRow(T rowData)
	{
		insertRow(getRowCount(), rowData);
	}

	public T getRow(int row)
	{
		return modelData.get( row );
	}

	@SuppressWarnings("unchecked")
	public T[] getRowsAsArray(int... rows)
	{
		List<T> rowData = getRowsAsList(rows);
		T[] array = (T[])Array.newInstance(rowClass, rowData.size());
		return (T[]) rowData.toArray( array );
	}

	public List<T> getRowsAsList(int... rows)
	{
		ArrayList<T> rowData = new ArrayList<T>(rows.length);

		for (int i = 0; i < rows.length; i++)
		{
			rowData.add( getRow(rows[i]) );
		}

		return rowData;
	}

	public void insertRow(int row, T rowData)
	{
		modelData.add(row, rowData);
		fireTableRowsInserted(row, row);
	}

	public void insertRows(int row, List<T> rowList)
	{
		modelData.addAll(row, rowList);
		fireTableRowsInserted(row, row + rowList.size() - 1);
	}

	public void insertRows(int row, T[] rowArray)
	{
		List<T> rowList = new ArrayList<T>(rowArray.length);

		for (int i = 0; i < rowArray.length; i++)
		{
			rowList.add( rowArray[i] );
		}

		insertRows(row, rowList);
	}

	public void moveRow(int start, int end, int to)
	{
		if (start < 0)
		{
			String message = "Start index must be positive: " + start;
			throw new IllegalArgumentException( message );
		}

		if (end > getRowCount() - 1)
		{
			String message = "End index must be less than total rows: " + end;
			throw new IllegalArgumentException( message );
		}

		if (start > end)
		{
			String message = "Start index cannot be greater than end index";
			throw new IllegalArgumentException( message );
		}

		int rowsMoved = end - start + 1;

		if (to < 0
		||  to > getRowCount() - rowsMoved)
		{
			String message = "New destination row (" + to + ") is invalid";
			throw new IllegalArgumentException( message );
		}


		ArrayList<T> temp = new ArrayList<T>(rowsMoved);

		for (int i = start; i < end + 1; i++)
		{
			temp.add(modelData.get(i));
		}

		modelData.subList(start, end + 1).clear();
		modelData.addAll(to, temp);


		int first;
		int last;

		if (to < start)
		{
			first = to;
			last = end;
		}
		else
		{
			first = start;
			last = to + end - start;
		}

		fireTableRowsUpdated(first, last);
	}

	public void removeRowRange(int start, int end)
	{
		modelData.subList(start, end + 1).clear();
		fireTableRowsDeleted(start, end);
	}

	public void removeRows(int... rows)
	{
		for (int i = rows.length - 1; i >= 0; i--)
		{
			int row = rows[i];
			fireTableRowsDeleted(row, row);
			modelData.remove(row);
		}
	}

	public void replaceRow(int row, T rowData)
	{
		modelData.set(row, rowData);
		fireTableRowsUpdated(row, row);
	}

	public void setColumnClass(int column, Class<T> columnClass)
	{
		columnClasses[column] = columnClass;
		fireTableRowsUpdated(0, getColumnCount() - 1);
	}

	public void setColumnEditable(int column, boolean isEditable)
	{
		isColumnEditable[column] = isEditable ? Boolean.TRUE : Boolean.FALSE;
	}

	public void setModelEditable(boolean isModelEditable)
	{
		this.isModelEditable = isModelEditable;
	}

	public static String formatColumnName(String columnName)
	{
		if (columnName.length() < 3) return columnName;

		StringBuffer buffer = new StringBuffer( columnName );
		boolean isPreviousLowerCase = false;

		for (int i = 1; i < buffer.length(); i++)
		{
			boolean isCurrentUpperCase = Character.isUpperCase( buffer.charAt(i) );

			if (isCurrentUpperCase && isPreviousLowerCase)
			{
				buffer.insert(i, " ");
				i++;
			}

			isPreviousLowerCase = ! isCurrentUpperCase;
		}

		return buffer.toString();
	}
	
	
}
