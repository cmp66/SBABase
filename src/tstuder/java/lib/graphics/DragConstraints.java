package tstuder.java.lib.graphics;

/*
 * This class implements a class to support mouse dragging constraints. 
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

import java.awt.Point;

/**
 * A class to support dragging of screen objects with the mouse. 
 * <p>Originally written to be used by tstuder.java.lib.component.table.Table.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class DragConstraints {

	int left;
	int right;
	int top;
	int bottom;
/**
 * Construct a DragConstraints object. The parameters specify the bounding
 * rectangle for any drag operations constrained by this class.
 *
 * @param left The drag rectangle's left coordinate.
 * @param right The drag rectangle's right coordinate.
 * @param top The drag rectangle's top coordinate.
 * @param bottom The drag rectangle's bottom coordinate.
 * 
 */
public DragConstraints( int left, int right, int top, int bottom ) {
	super();

	this.left = left;
	this.right = right;
	this.top = top;
	this.bottom = bottom;
}
/**
 * Drag some point (x,y) horizontally and vertically.
 *
 * @param x The x coordinate the user would like to drag to.
 * @param y The y coordinate the user would like to drag to.
 * @return The Point the user is allowed to drag to.
 */
public Point dragBoth( int x, int y ) {

	return new Point( dragHorizontally( x ), dragVertically( y ) );
}
/**
 * Drag horizontally.
 *
 * @param The x coordinate the user would like to drag to.
 * @return The x coordinate the user is allowed to drag to.
 */
public int dragHorizontally( int x ) {

	if (x < left) {
		return left;
	} else if (x > right) {
		return right;
	} else {
		return x;
	}
}
/**
 * Drag verticaly.
 *
 * @param The y coordinate the user would like to drag to.
 * @return The y coordinate the user is allowed to drag to.
 */
public int dragVertically( int y ) {

	if (y < top) {
		return top;
	} else if (y > bottom) {
		return bottom;
	} else {
		return y;
	}
}
}
