package tstuder.java.lib.graphics;

/*
 * This class extends "Border". It draws a 2 pixel wide
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
 * This BorderLowered2 class extends "Border". It draws a 2 pixel wide
 * border within a specified rectangle.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class BorderLowered2 extends Border {

	private static Insets myInsets = new Insets( 2, 2, 2, 2 );
/**
 * BorderLowered constructor comment.
 */
public BorderLowered2() {
	this( Color.black );
}
/**
 * Construct a BorderLowered2 object.
 *
 * @param foreground The border's foreground color.
 */
public BorderLowered2( java.awt.Color foreground ) {
	super( foreground, Color.lightGray );
}
/**
 * Paint the border.
 *
 * @see Border
 */
public void paint( Graphics g, int x, int y, int width, int height ) {

	Color c = g.getColor();

	g.setColor( Color.white );
	// Lower white line
	g.drawLine( x, y + height - 1, x + width - 1, y + height - 1 );
	// Right white line
	g.drawLine( x + width - 1, y, x + width - 1, y + height - 1 );
	
	g.setColor( Color.lightGray );
	// Bottom gray line
	g.drawLine( x + 1, y + height - 2, x + width - 2, y + height - 2 );
	// Right gray line
	g.drawLine( x + width - 2, y + 1, x + width - 2, y + height - 2 );

	g.setColor( foreground );
	// Left black line
	g.drawLine( x + 1, y + 1, x + 1,  y + height - 3 );
	// Top black  line
	g.drawLine( x + 1, y + 1, x + width - 3, y + 1 );

	g.setColor( GraphicsUtil.brighter( foreground ) );
	// Left dark gray line
	g.drawLine( x, y, x, y + height - 2 );
	// Top dark gray line
	g.drawLine( x, y, x + width - 2, y );


	g.setColor( c );
}
/**
 * Store the border's insets in the 'insets' member.
 * 
 */
protected void setInsets() {

	insets = BorderLowered2.myInsets;
}
}
