package tstuder.java.applet.gui.tabledemo;

/*
 * This class implements the TableRow interface. ColorTableRow represents
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
import tstuder.java.lib.component.table.*;
import tstuder.java.lib.util.Util;


/**
 * This class implements the TableRow interface. ColorTableRow represents
 * a single data row to be displayed by the "Table" bean
 * (tstuder.java.lib.component.table.Table).
 *
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class ColorTableRow implements TableRow {
	
   private String[] strings;
public ColorTableRow(String[] strings) {

	this.strings = strings;
}
public Color[] getBackgrounds() {

	Color[] colors = new Color[strings.length];

	for (int i=0; i<colors.length; i++) {
		colors[i] = new Color( 0x00ffffff - (Util.hashString( strings[i] ) >>> 8) );
	}
	
	return colors;
}
public Color[] getForegrounds() {

	Color[] colors = new Color[strings.length];

	for (int i=0; i<colors.length; i++) {
		colors[i] = new Color( Util.hashString( strings[i] ) >>> 8 );
	}
	
	return colors;
}
public String[] getStrings() {
	return strings;
}
}
