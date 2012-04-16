package tstuder.java.lib.component.table;

/*
 * This class implements the TableRow interface. SimpleTableRow represents
 * a single data row to be displayed by the "Table" bean
 * (tstuder.java.lib.component.table.Table).
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

import java.awt.Color;


/**
 * A helper class implementing the TableRow interface. It allows the creation of
 * table rows consisting of an array of Strings (one for each column). Custom
 * (cell-level) fore- and background colors are not supported. If you need
 * cell-level color support, implement TableRow yourself and define the methods
 * getBackgrounds() and getForegrounds() to return the desired colors.
 *
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class SimpleTableRow implements TableRow {

   private String[] strings;

/**
 * Instantiate a table row consisting of an array of Strings -- one for each of the
 * table's columns.
 */
public SimpleTableRow( String[] strings ) {
   
	this.strings = strings;
}
/**
 * No cell-level color support by using this class -- return null.
 */
public Color[] getBackgrounds() {
   
	return null;
}
/**
 * No cell-level color support by using this class -- return null.
 */
public Color[] getForegrounds() {
   
	return null;
}
/**
 * Return this row's column strings.
 */
public String[] getStrings() {
   
	return strings;
}
}
