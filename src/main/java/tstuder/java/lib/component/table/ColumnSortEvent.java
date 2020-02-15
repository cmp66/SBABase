package tstuder.java.lib.component.table;

/*
 * This class represents the event that is being fired to start sorting 
 * a particular table column.
 *
 * Copyright (C) 1999 Thomas Studer
 * mailto:tstuder@datacomm.ch
 * http://www.datacomm.ch/tstuder
 *
 * This class is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This class is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this class; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * This class represents the event that is being fired to start sorting 
 * a particular table column. It is fired by the Titlebar and
 * handled by the Table.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-12-04, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class ColumnSortEvent extends java.util.EventObject {

    private static final long serialVersionUID = 1L;
    private int columnIndex;
	private boolean ascending;
/**
 * 
 * @param source The event's source object.
 * @param columnIndex The index of the column that is to be sorted.
 * @param ascending A boolean flag telling whether the column is to
 * be sorted in ascending or descending order.
 */
public ColumnSortEvent(Object source, int columnIndex, boolean ascending ) {
	super(source);

	this.columnIndex = columnIndex;
	this.ascending = ascending;
}
/**
 * 
 * @return <code>true</code> to sort ascending, <code>false</code> to sort
 * descending.
 */
public boolean getAscending() {
	
	return ascending;
}
/**
 * 
 * @return The zero-based index of the column to sort.
 */
public int getColumnIndex() {
	
	return columnIndex;
}
}
