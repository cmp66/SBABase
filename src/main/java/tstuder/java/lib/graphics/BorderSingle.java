package tstuder.java.lib.graphics;

/*
 * This class extends "Border". It draws a 1 pixel wide
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
 * Extends "Border". Draws a one pixel wide border.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class BorderSingle extends Border {

	private static Insets myInsets = new Insets( 1, 1, 1, 1 );
/**
 * Construct a BorderSingle object.
 */
public BorderSingle() {

	super( Color.black, Color.white);
}
/**
 * Construct a BorderSingle object.
 *
 * @param foreground The Border's foreground color.
 */
public BorderSingle( java.awt.Color foreground ) {

	super( foreground, Color.white);
}
/**
 * Paint the border. 
 * 
 * @see Border
 */
public void paint( Graphics g, int x, int y, int width, int height ) {

	Color c = g.getColor();

	g.setColor( foreground );
	g.drawRect( x, y, x + width - 1, y + height - 1 );

	g.setColor( c );
}
/**
 * Store the border's insets in the 'insets' member.
 * 
 */
protected void setInsets() {

	insets = BorderSingle.myInsets;
}
}
