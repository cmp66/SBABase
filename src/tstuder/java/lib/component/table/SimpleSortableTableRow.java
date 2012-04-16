package tstuder.java.lib.component.table;

/*
 * This class extends the TableRow interface. SimpleSortableTableRow represents
 * a single data row to be displayed by the "Table" bean
 * (tstuder.java.lib.component.table.Table). It implements the ComlumnSortable
 * interface and thus supports comparing two TableRow objects.
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

import java.text.Collator;

import tstuder.java.lib.util.ColumnSortable;

/**
 * A helper class extending the SimpleTableRow class
 * and implementing ColumnSortable interface. It allows the creation of
 * table rows consisting of an array of Strings (one for each column). Custom
 * (cell-level) fore- and background colors are not supported. If you need
 * cell-level color support, implement TableRow yourself and define the methods
 * getBackgrounds() and getForegrounds() to return the desired colors.
 *
 * <p>SimpleSortableTableRow implements the ColumnSortable interface and thus
 * contains a method to compare two TableRow objects. If you inititiate a Table,
 * passing an instance of ColumnSortableVector as the rows vector (containing
 * SimpleSortableTableRow objects or other objects implementing the ColumnSortable
 * interface) the Table class will automatically allow you
 * to sort the table data by clicking on a column heading in
 * the Titlebar.
 *
 * <p>This class is just a helper class for very simple table rows. The
 * class sorts on the strings returned by getStrings() (of the inherited
 * SimpleTableRow class). If you want to sort on anything other than strings (for
 * example on numerical values that are being displayed by the table), you'll have to
 * implement the ColumnSortable interface in the class that represents your table
 * row.
 *
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-12-02, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class SimpleSortableTableRow extends SimpleTableRow implements ColumnSortable {

	/** The collator is used to compare two strings honoring the default
	* (or some other) locale */
	private static Collator collator = Collator.getInstance();
/**
 * Instantiate a table row consisting of an array of Strings -- one for each of the
 * table's columns.
 */
public SimpleSortableTableRow( String[] strings ) {

	super( strings );
}
/**
 * Compare two SimpleSortableTableRow objects by column index.
 *
 * @param object1 The first of the two objects to be compared.
 * @param object2 The second of the two objects to be compared.
 * @param columnIndex The column to compare on.
 *
 * @return @see Collator.compare()
 */
public int compare(Object object1, Object object2, int columnIndex) {
	
	return collator.compare( 
		((TableRow)object1).getStrings()[columnIndex],
		((TableRow)object2).getStrings()[columnIndex] );
}
/**
 * Get the current Collator instance used for string comparisons.
 *
 * @return java.text.Collator
 */
public static Collator getCollator() {

	return collator;
}
/**
 * Set the Collator object to be used for String comparisons.
 *
 * @param collator java.text.Collator
 */
public static void setCollator( Collator collator ) {

	SimpleSortableTableRow.collator = collator;
}
}
