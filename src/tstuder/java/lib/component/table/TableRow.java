package tstuder.java.lib.component.table;

/*
 * This interface defines the methods the Table bean (see class 
 * tstuder.java.lib.component.table.Table) calls to find out
 * the strings, foreground colors and background colors for the 
 * current table row.
 *
 * Copyright (C) 1999 Thomas Studer
 * mailto:tstuder@datacomm.ch
 * http://www.datacomm.ch/tstuder
 *
 * This interface is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This interface is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this interface; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

 import java.awt.Color;

/**
 * This interface defines the methods the Table bean (see class 
 * tstuder.java.lib.component.table.Table) calls to find out
 * the strings, foreground colors and background colors for the 
 * current table row.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public interface TableRow {
/**
 * Get the background colors for each of the cells of the current Table row.
 * 
 * @return An array of Color objects defining the background color for each of the
 * data cells of the current table row. Return <code>null</code> to use the table's 
 * default column/table background colors.
 */
Color[] getBackgrounds();
/**
 * Get the foreground colors for each of the cells of the current Table row.
 * 
 * @return An array of Color objects defining the foreground color for each of the
 * data cells of the current table row. Return <code>null</code> to use the table's 
 * default column/table foreground colors.
 */
Color[] getForegrounds();
/**
 * Get the array of String objects for the current table row.
 * 
 * @return The array of String objects making up the current table row's data.
 */
String[] getStrings();
}
