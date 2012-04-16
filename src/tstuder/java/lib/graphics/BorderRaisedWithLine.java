package tstuder.java.lib.graphics;

/*
 * This class extends "Border". It draws a 
 * border of single pixel width along the top and left edge and of
 * double pixel width along the right and bottom edge of a specified rectangle.
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

import java.awt.*;

/**
 * Like BorderRaised but draws an additional black line to the right
 * and the bottom of the border.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class BorderRaisedWithLine extends Border {

	private static Insets myInsets = new Insets( 1, 1, 2, 2 );
	
	private Color shadowColor;
/**
 * Construct a BorderRaisedWithLine object.
 */
public BorderRaisedWithLine() {

	this( Color.lightGray );
}
/**
 * Construct a BorderRaisedWithLine object.
 *
 * @param background The border's background color.
 */
public BorderRaisedWithLine( java.awt.Color background ) {

	super( Color.black, background);
	shadowColor = background.darker();
}
/**
 * Paint the border.
 *
 * See Border
 * 
 */
public void paint( Graphics g, int x, int y, int width, int height ) {

	Color c = g.getColor();

	int right = x + width - 1;
	int bottom = y + height - 1;

	g.setColor( foreground );
	// Bottom divider line
	g.drawLine( x, bottom, right, bottom );
	// Right divider line
	g.drawLine( right, y, right, bottom );

	right--;
	bottom--;

	g.setColor( Color.white );
	// Top white  line
	g.drawLine( x, y, right, y );
	// Left white  line
	g.drawLine( x, y, x, bottom );
	
	g.setColor( shadowColor );
	// Bottom shadow line
	g.drawLine( x, bottom, right, bottom );
	// Right shadow line
	g.drawLine( right, y, right, bottom );

	g.setColor( c );
}
/**
 * Store the border's insets in the 'insets' member.
 */
protected void setInsets() {

	insets = BorderRaisedWithLine.myInsets;
}
}
