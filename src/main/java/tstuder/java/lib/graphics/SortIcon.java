package tstuder.java.lib.graphics;

/*
 * This class supports fast painting of a little icon showing the sort direction.
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
 * A class for fast painting of two icons indicating sort direction.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-12-02, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class SortIcon {

	public static final int  SIZE = 9;

	private Image		ascendingIcon;
	private Image		descendingIcon;
/**
 * Constructor. 
 * Creates two offscreen images of the two sort states (ascending/descending).
 * <p>The paint() method then paints one of the images depending on the
 * 'ascending' flag.
 * 
 * @param c The component onto which the icon will eventually be copied.
 * 
 */
public SortIcon( Component c ) {

	Graphics g;

	ascendingIcon = c.createImage( SIZE, SIZE );
	g = ascendingIcon.getGraphics();

	g.setColor( c.getBackground() );
	g.fillRect( 0, 0, SIZE, SIZE );

	g.setColor( Color.black );

	// . . . . . . . . . 
	// . . x . . . . . .  
	// . . . . . . . . . 
	// . . x x x . . . .  
	// . . . . . . . . .  
	// . . x x x x x . .  
	// . . . . . . . . . 
	// . . x x x x x x x  
	// . . . . . . . . . 

	for (int i=0; i<8; i+=2) {
		g.drawLine( 2, i+1, i+2, i+1 );
	}

	descendingIcon = c.createImage( SIZE, SIZE );
	g = descendingIcon.getGraphics();

	g.setColor( c.getBackground() );
	g.fillRect( 0, 0, SIZE, SIZE );

	g.setColor( Color.black );

	// . . . . . . . . . 
	// . . x x x x x x x  
	// . . . . . . . . .  
	// . . x x x x x . .  
	// . . . . . . . . . 
	// . . x x x . . . .  
	// . . . . . . . . . 
	// . . x . . . . . .  
	// . . . . . . . . . 

	for (int i=0; i<8; i+=2) {
		g.drawLine( 2, i+1, SIZE-1-i, i+1 );
	}
}
/**
 * Paint a little icon indicating sort direction within the rectangle defined by
 * 'x', 'y', 'width' and 'height'. The icon will be painted with constant
 * size. If the rectangle specified via the 'x', 'y', 'width' and 'height' 
 * parameters is bigger than the icon, the icon is centered within
 * that rectangle.
 * 
 * @param g The Graphics object onto which the icon will be painted.
 * @param x The icon's destination rectangle's X coordinate. 
 * @param y The icon's destination rectangle's Y coordinate. 
 * @param width The icon's destination rectangle's width.
 * @param height The icon's destination rectangle's height.
 */
public void paint( Graphics g, boolean ascending, int x, int y, int width, int height ) {

	int left = x + (width-SIZE) / 2;
	int top = y + (height-SIZE) / 2;

	if (ascending) {
		g.drawImage( ascendingIcon, left, top, null );
	} else {
		g.drawImage( descendingIcon, left, top, null );
	}
}
}
