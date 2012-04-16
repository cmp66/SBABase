package tstuder.java.lib.component.table;

/*
 * The interface that has to be implemented in order to handle events of 
 * type ColumnSortEvent
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

/**
 * The interface that has to be implemented in order to handle events of 
 * type ColumnSortEvent
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-12-04, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public interface ColumnSortListener {
/**
 * 
 * @param e The ColumnSortEvent to respond to.
 */
void sortColumn( ColumnSortEvent e );
}
