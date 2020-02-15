package tstuder.java.lib.graphics;

/*
 * This Border class paints an empty (zero-width) border. Subclass this
 * Border class to draw custom borders.
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
 * Draw a border around things. 
 * 
 * <p>This Border class paints an empty (zero-width) border. Subclass this
 * class to draw custom borders.
 *
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 * 
 */
public class Border {

	protected Color 	background;  	

	protected Color	foreground;		

	protected Insets	insets;			


	private static Insets myInsets = new Insets( 0, 0, 0, 0 );

/**
 * Construct a Border object.
 */
public Border() {

	this( Color.black, Color.lightGray );
}
/**
 * Construct a Border object.
 * 
 * @param foreground The foreground color of the Border. The actual use of the
 * foreground color is determined by some subclass of Border.
 * @param background The backgraound color of Border. The actual use of the 
 * background color is determined by some subclass of Border. 
 */
public Border( Color foreground, Color background ) {

	this.foreground = foreground;
	this.background = background;

	setInsets();
}
/**
 * Get the current background color.
 *
 * @return The Border's current background color.
 */
public Color getBackground() {

	return background;
}
/**
 * Get the current foreground color.
 * 
 * @return The Border's current foreground color.
 */
public Color getForeground() {

	return foreground;
}
/**
 * Get the Border's insets. 
 * 
 * @return The Border's (or subclass thereof) Insets.		
 */
public Insets getInsets() {

	return insets;
}
/**
 * Paint the Border within the specified rectangle.
 * 
 * @param g The Graphics object to paint to.
 * @param x Border rectangle's X coordinate.
 * @param y Border rectangle's Y coordinate.
 * @param width Border rectangle's width.
 * @param height Border rectangle's height.
 */
public void paint( Graphics g, int x, int y, int width, int height ) {

	// Nichts zeichnen. 
}
/**
 * Set the Border's background color.
 * 
 * @param background The new background color.
 */
public void setBackground( Color background ) {

	this.background = background;
}
/**
 * Set the Border's foreground color.
 * 
 * @param foreground The new foreground color.
 */
public void setForeground( Color foreground ) {

	this.foreground = foreground;
}
/**
 * Set the Border's insets. A subclass painting a more than zero width Border
 * should override this method and store the correct Insets in the 'insets' member.
 * 
 */
protected void setInsets() {

	insets = Border.myInsets;
}
}
