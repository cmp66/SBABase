package tstuder.java.lib.graphics;

/*
 * This class supports the fast painting of a checkmark.
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
 * A class for fast painting of a checkmark in its checked or unchecked state.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class CheckMark {


	static final int  SIZE = 12;

	private Image		checkedMark;
	private Image		uncheckedMark;
/**
 * Constructor. 
 * Creates two offscreen images of the two checkmark states (checked/unchecked).
 * <p>The paint() method then uses these images to draw a checkmark. The
 * class was originally designed to support checkmarks in a table component where
 * drawing speed was very important. (drawImage() of a single image is much
 * faster than drawing the checkmark in the paint() method each time from scratch.)
 * 
 * @param c The component onto which the checkmark will eventually be copied.
 * @param hasFrame true, if a box is to be drawn around the checkmark.	
 * 
 */
public CheckMark( Component c, boolean hasFrame ) {

	Graphics g;

	checkedMark = c.createImage( SIZE, SIZE );
	g = checkedMark.getGraphics();

	g.setColor( Color.white );
	g.fillRect( 0, 0, SIZE, SIZE );

	g.setColor( Color.darkGray );

	if (hasFrame) g.drawRect( 0, 0, SIZE-1, SIZE-1 );

	g.drawLine( 2, 5, 2, 5 );
	g.drawLine( 5, 6, 9, 2 );
	g.drawLine( 5, 8, 9, 4 );

	g.setColor( Color.lightGray );
	g.drawLine( 1, 5, 1, 6 );
	g.drawLine( 2, 7, 4, 9 );
	g.drawLine( 5, 9, 10, 4 );
	g.drawLine( 10, 2, 10, 3 );
	g.drawLine( 4, 6, 8, 2 );

	g.setColor( Color.black );
	g.drawLine( 2, 6, 4, 8 );
	g.drawLine( 5, 7, 9, 3 );
	g.drawLine( 3, 6, 4, 7 );

	uncheckedMark = c.createImage( SIZE, SIZE );
	g = uncheckedMark.getGraphics();
	g.setColor( Color.white );
	g.fillRect( 0, 0, SIZE, SIZE );

	if (hasFrame) {
		g.setColor( Color.darkGray );
		g.drawRect( 0, 0, SIZE-1, SIZE-1 );
	}
}
/**
 * Paint a checkmark in state 'state' within the rectangle defined by
 * 'x', 'y', 'width' and 'height'. The checkmark will be painted with constant
 * size. If the rectangle specified via the 'x', 'y', 'width' and 'height' 
 * parameters is bigger than the checkmark, the checkmark is centered within
 * that rectangle.
 * 
 * @param g The Graphics object onto which the checkmark will be painted.
 * @param x The checkmark's destination rectangle's X coordinate. 
 * @param y The checkmark's destination rectangle's Y coordinate. 
 * @param width The ckeckmark's destination rectangle's width.
 * @param height The checkmarks's destination rectangle's height.
 */
public void paint( Graphics g, boolean state, int x, int y, int width, int height ) {

	int left = x + (width-SIZE) / 2;
	int top = y + (height-SIZE) / 2;

	if (state) {
		g.drawImage( checkedMark, left, top, null );
	} else {
		g.drawImage( uncheckedMark, left, top, null );
	}
}
}
