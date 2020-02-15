package tstuder.java.lib.graphics;

/*
 * This class extends "Border". It draws a single pixel wide
 * border within a specified rectangle.
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
 * <p>Extends "Border". Draws a one pixel wide, raised border.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class BorderRaised extends Border {

	private static Insets myInsets = new Insets( 1, 1, 1, 1 );

	private Color shadowColor;
/**
 * Construct a BorderRaised object.
 */
public BorderRaised() {

	this( Color.lightGray );
}
/**
 * Construct a BorderRaised.
 * @param foreground The border's foreground color.
 */
public BorderRaised( java.awt.Color background ) {

	super( Color.black, background);
	shadowColor = background.darker();
}
/**
 * Paint the border.
 *
 * @see Border
 */
public void paint( Graphics g, int x, int y, int width, int height ) {

	Color c = g.getColor();

	int right = x + width - 1;
	int bottom = y + height - 1;

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
 * 
 */
protected void setInsets() {

	insets = BorderRaised.myInsets;
}
}
