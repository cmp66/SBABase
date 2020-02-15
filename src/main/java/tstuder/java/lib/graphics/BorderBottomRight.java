package tstuder.java.lib.graphics;

/*
 * This class extends "Border". It draws a border consisting 
 * of two single pixel lines -- one along the bottom edge and the other one along
 * the right edge of the border rectangle.
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
 * Extends "Border". Draws a border consisting of two single pixel lines -- one
 * along the bottom edge and the other one along
 * the right edge of the border rectangle.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class BorderBottomRight extends Border {

	private static Insets myInsets = new Insets( 0, 0, 1, 1 );
/**
 * Construct a BorderBottomRight object.
 */
public BorderBottomRight() {

	this( Color.gray );
}
/**
 * Construct a BorderBottomRight object.
 * @param foreground The Border's foreground color.
 */
public BorderBottomRight( java.awt.Color foreground ) {

	super( foreground, Color.white);
}
/**
 * Paint the Border.
 * 
 * @see Border
 */
public void paint( Graphics g, int x, int y, int width, int height ) {

	Color c = g.getColor();

	g.setColor( foreground );
	g.drawLine( x, y + height- 1, x + width - 1, y + height - 1 );
	g.drawLine( x + width - 1, y, x + width - 1, y + height - 1 );

	g.setColor( c );
}
/**
 * Set the Border's insets.
 * 
 */
protected void setInsets() {

	insets = BorderBottomRight.myInsets;
}
}
