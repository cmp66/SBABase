package tstuder.java.lib.graphics;

/*
 * This class extends StringWrapFormatter. It implements methods to paint 
 * a string (possibly wrapped to multiple lines) within some 
 * destination rectangle. A simple coding scheme is supported to
 * allow lines of characters within the string to be drawn in
 * different colors.
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
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * <p>Extends the class "StringWrapFormatter" to support the drawing of wrapped,
 * multi-colored Text.
 * <p>Note: Color changes are only supported on a line by line basis. Color changes
 * within the same line are not supported.
 *
 * <p>It works as follows: Using the method
 * <code>setColors( Color[] )</code>, up to nine colors can be defined. These
 * colors can then be referenced in the string to be drawn. 
 * <p> By default, the colors are initialized as follows:
 * <pre>
 * 0 -- the cell's default color
 * 1 -- red
 * 2 -- green
 * 3 -- blue
 * 4 -- magenta
 * 5 -- cyan
 * 6 -- yellow
 * 7 -- dark gray
 * 8 -- gray
 * 9 -- light gray
 * </pre>
 * <p>The colors are
 * switched by embedding a character code "~x" in the string 
 * (where 'x' is the index of the color to be set). 
 * Upon encountering such a character code, a line break is forced and the color
 * is switched to the one specified in the code and remains active until the
 * end of the string reached or until a different color code is encountered. To embed
 * the character "~" in the string, simply double it, i.e. "~~" prints
 * "~". Embed a new line character ('\n') in the string to force a line break without
 * changing the color. 
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-09-24, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class ColorStringWrapFormatter extends StringWrapFormatter {

	private Color[] colors = { null, Color.red, Color.green, Color.blue,
										Color.magenta, Color.cyan, Color.yellow,
										Color.darkGray, Color.gray, Color.lightGray };
	

	//private int     toggleIndex;
	private int     toggleCount;
	private int[]   toggleVector = new int[8];
	private int[]   colorVector = new int[8];
/**
 * Default constructor. See class StringWrapFormatter or StringFormatter.
 */
public ColorStringWrapFormatter() {
	super();
}
/**
 * Constructor. See class StringWrapFormatter or StringFormatter.
 * @param hAlignment Horizontal alignment constant (defined in StringFormatter).
 * @param vAlignment Vertical alignment constant.
 */
public ColorStringWrapFormatter(int hAlignment, int vAlignment) {
	super(hAlignment, vAlignment);
}
/**
 * Constructor. See class StringWrapFormatter or StringFormatter.
 *
 * @param hAlignment Horizontal alignment constant (defined in StringFormatter).
 * @param vAlignment Vertical alignment constant.
 * @param tabstops int[]
 * @param decTabstop int
 */
public ColorStringWrapFormatter(int hAlignment, int vAlignment, int[] tabstops, int decTabstop) {
	super(hAlignment, vAlignment, tabstops, decTabstop);
}
/**
 * Locate and remember the positions in the string where a color change
 * occurs.
 * 
 * @param s The string to parse.
 * @param chars The characters of the string without the embedded color codes.
 * @return The number of valid characters in the chars array.
 */
private int colorParse( String s, char[] chars ) {

	int copyIndex = 0;
	boolean escape = false;
	char c;
	
	toggleCount = 0;

	for (int i=0; i<s.length(); i++) {
		c = s.charAt( i );
		if (escape) {
			if (c == '~') {
				chars[copyIndex++] = '~';
			} else {
				if (toggleVector.length == toggleCount) increaseCapacity();
				chars[copyIndex++] = '\n';
				toggleVector[toggleCount] = copyIndex;
				colorVector[toggleCount] = Character.digit( c, 10 );
				toggleCount++;
			}
			escape = false;
		} else {
			if (c == '~') {
				escape = true;
			} else {
				chars[copyIndex++] = c;
			}
		}
	}

	return copyIndex;
}
/**
 * See StringWrapFormatter.formatString(...)
 */
public void formatString( Graphics g, FontMetrics m, String s, int x, int y, 
	int width, int height ) {

	int toggleIndex = 0;	
	char[] chars = new char[s.length()];
	int length = colorParse( s, chars );

	if (g != null) colors[0] = g.getColor();
	
	// Calcluate the indexes into 'chars' at which to wrap.
	//
	wrap( m, chars, 0, length, width );

	// Depending on the number of lines and the vertical alignment, calculate the
	// y-coordinate of the baseline of the first line.
	//
	int curBaseline;
	int fontHeight = m.getHeight();
	int blockHeight = lineCount * fontHeight;

	int maxWidth = 0;

	if (vAlignment == ALIGNMENT_VCENTER) {
		curBaseline = (height + blockHeight) / 2 - blockHeight + fontHeight;
	} else if (vAlignment == ALIGNMENT_BOTTOM) {
		curBaseline = height - blockHeight + fontHeight;
	} else {
		curBaseline = fontHeight;
	}
	curBaseline = y + curBaseline - m.getDescent();

	// Print the wrapped text
	//
	for (int i=0; i<lineCount; i++) {
		
		int nextToggleIndex = toggleIndex < toggleCount ? toggleVector[toggleIndex] : length;

		if (nextToggleIndex == startIndexes[i]) {
			if (g != null) g.setColor( colors[colorVector[toggleIndex++]] );
		}
		drawChars( g, m, chars, startIndexes[i], pastIndexes[i]-startIndexes[i],
			x, curBaseline, width );
		curBaseline += fontHeight;
		if (preferredWidth > maxWidth) maxWidth = preferredWidth;
	}

	preferredHeight = blockHeight;
	preferredWidth = maxWidth;
}
/**
 * My own little dynamic array handling -- I just hate the idea of using
 * Vector and Integer objects for cases like this (putting
 * int wrapper objects into vectors); the code lookus ugly and the performance
 * is worse compared to a simple int[].
 */
private void increaseCapacity() {

	int capacity = toggleVector.length;
	int[] newToggleVector = new int[capacity*2];
	int[] newColorVector = new int[capacity*2];

	System.arraycopy( toggleVector, 0, newToggleVector, 0, capacity ); 
	System.arraycopy( colorVector, 0, newColorVector, 0, capacity );

	toggleVector = newToggleVector;
	colorVector = newColorVector;
}
/**
 * Is this a wrapping string formatter class?
 *
 * @return <code>true</code> if this class supports string wrapping,
 * <code>false</code> otherwise. This method returns true regardless of the
 * state of the 'wrapEnabled' property because new lines break a string
 * in any case (which is considered wrapping in the context of this call).
 */
public boolean isWrappingFormatter() {
	
	return true; 
}
/**
 * Allows you to define up to nine colors used for drawing different
 * parts of the string. See the class description for more info. 
 * <p>The passed
 * color array is copied into an internal color array, moved up by one array
 * entry. This frees entry [0] of the internal array to hold the current cell's 
 * default color. Hence, referring to color 0 in the string to be drawn selects
 * a cell's default color.
 *
 * @param colors The color lookup array. 
 */
public void setColors( Color[] colors ) {

	this.colors = new Color[colors.length+1];

	for (int i=0; i<colors.length; i++) {
		this.colors[i+1] = colors[i];
	}
}
}
