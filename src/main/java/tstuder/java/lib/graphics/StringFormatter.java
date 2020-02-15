package tstuder.java.lib.graphics;

/*
 * This class implements methods to paint a string within some
 * destination rectangle.
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
 * Formatted drawing of a String within a
 * specified rectangle.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * &nbsp; ts, 1999-09-24, v1.0.2,  - Added isWrappingFormatter() to let
 * &nbsp;                          clients of this class figure out if this
 * &nbsp;                          class supports string wrapping. Mainly used
 * &nbsp;                          for performance optimization in class
 * &nbsp;                          ...table.DataArea.
 * &nbsp;                          - Added getter/setter methods for the horizontal and
 * &nbsp;                          and vertical alignment modes.
 * &nbsp; ts, 2000-05-24, v1.0.3,  - Improved temporary memory handling.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class StringFormatter {

	/** Left alignment of the string */
	public static final int ALIGNMENT_LEFT					= 1; // Default

	/** Left alignment with tab support */
	public static final int ALIGNMENT_LEFT_WITH_TABS	= 2;

	/** Center alignment of the string */
	public static final int ALIGNMENT_CENTER				= 4;

	/** Right alignment of the string */
	public static final int ALIGNMENT_RIGHT				= 8;

	/** Right alignment of the string around the specified decimal delimiter
 	 *  (e.g. the decimal point */
	public static final int ALIGNMENT_RIGHT_DECIMAL		= 16;

	/** Vertical top alignment of the string */
	public static final int ALIGNMENT_TOP					= 2; // Default

	/** Vertical center alignment of the string */
	public static final int ALIGNMENT_VCENTER  			= 4;

	/** Vertical bottom alignment of the string */
	public static final int ALIGNMENT_BOTTOM				= 8;

	/** The default number of decimal places used for the alignment option
	 *  ALIGNMENT_RIGHT_DECIMAL */
	public static final int DEFAULT_NUM_DECIMALS			= 2;

	protected int				hAlignment						= ALIGNMENT_LEFT;

	/** Current vertical alignment. One of the ALIGNMENT... constants, above. */
	protected int				vAlignment						= ALIGNMENT_TOP;
	
	/** The current tabstop settings in widths of the digit zero for the current font */
	private int[]				tabstops							= null; 

	/** <code>true</code> if the formatted string should be clipped to the
	 *  destination rectangle and indicating it by appending 'dotsString'. */
	private boolean			clip								= true; 
															  
	/** The position of the decimal point in widths of the digit '0' from
	 *  the right. */
	private int					decTabstop						= 3;

	private static char		decimalDelimiter 				= '.';

	/** This string is appended to strings that had to be clipped because of a lack
  	 *  of space */
	private static char[] 	dotsString 						= { '.', '.' };
	
	/** The number of lines needed to draw the string */
	protected int				lineCount;

	/** <code>true</code> if the string was clipped. */
	protected boolean			wasClipped;

	/** The preferred height of the formatted string */
	protected int 				preferredHeight;

	/** The preferred widht of the formatted string */	
	protected int				preferredWidth;

/**
 * Construct a StringFormatter object.
 */
public StringFormatter() {

	this( ALIGNMENT_LEFT, ALIGNMENT_TOP, null, DEFAULT_NUM_DECIMALS );
}
/**
 * Construct a StringFormatter object.
 * 
 * @param hAlignment One of the horizontal alignment constants.
 * @param vAlignment One of the vertical alignment constants.
 */
public StringFormatter( int hAlignment, int vAlignment ) {

	this( hAlignment, vAlignment, null, DEFAULT_NUM_DECIMALS );
}
/**
 * Construct a StringFormatter object.
 * 
 * @param hAlignment One of the horizontal alignment constants.
 * @param vAlignment One of the vertical alignment constants.
 * @param tabstops An array of <code>int</code>s specifying the tabstops in 
 * widths of the character zero of the current font.
 * @param decTabstop The position of the decimal point in widths of the character
 * zero of the current font (from the right edge of the destination rectangle). 
 * Only relevant for the alignment option ALIGNMENT_RIGHT_DECIMAL.
 */
public StringFormatter( int hAlignment, int vAlignment, int[] tabstops, int decTabstop ) {

	this.hAlignment = hAlignment;
	this.vAlignment = vAlignment;
	this.tabstops = tabstops;
	this.decTabstop = decTabstop;
}
/**
 * Calculate the vertical baseline coordinate.
 * 
 * @return int The offset from the top of the destination rectangle where
 * painting of the string should start (depends on the current
 * vertical alignment option).
 * @param m The FontMetrics object used for the calculation.
 * @param height The height of the destination rectangle.
 */
public int calcBaselineOffset( FontMetrics m, int height ) {

	switch (vAlignment) {

		case ALIGNMENT_BOTTOM:
			return height - m.getDescent();

		case ALIGNMENT_VCENTER:
			return (height + m.getHeight()) / 2 - m.getDescent();

		default:
			return m.getHeight() - m.getDescent();
	}
}
/**
 * Draw a number of characters within a specified destination rectangle.
 * 
 * @param g Graphics
 * @param m FontMetrics
 * @param chars char[]
 * @param start int
 * @param len int
 * @param x int
 * @param y int
 * @param width The available width for drawing
 * @param w The width of the String to be drawn
 * @param hAlign
 */
private final void drawAligned( Graphics g, FontMetrics m, char[] chars, 
	int start, int len, int x, int y, int width, int w, int hAlign ) {

	if (len > 0) {
		switch (hAlign) {

			case ALIGNMENT_LEFT:
				g.drawChars( chars, start, len, x, y );
				break;
			case ALIGNMENT_RIGHT:
				g.drawChars( chars, start, len, x + width - w, y );
				break;
			case ALIGNMENT_CENTER:
				g.drawChars( chars, start, len, x + (width - w) / 2, y );
				break;
		}
	}
}
/**
 * Draw a number of characters at a specified position.
 * 
 * @param g Graphics
 * @param m FontMetrics
 * @param chars char[]
 * @param start int
 * @param len int
 * @param x int
 * @param y int
 * @param width int
 */
public final void drawChars( Graphics g, FontMetrics m, char[] chars, int start, 
									  int len, int x, int y, int width ) {

	preferredHeight = m.getHeight();
	preferredWidth = 0;
	lineCount = 1;
	wasClipped = false;

	if (len == 0) return;
	
	int w;

	if (hAlignment == ALIGNMENT_RIGHT_DECIMAL) {


		drawCharsDecimal( g, m, chars, start, len, x, y, width );
		

	} else if (hAlignment == ALIGNMENT_LEFT_WITH_TABS) {

		drawCharsTabbed( g, m, chars, start, len, x, y, width );

	} else {

		w = m.charsWidth( chars, start, len );
		preferredWidth = w;

		if (w <= width) {	

			if (g != null) {
				drawAligned( g, m, chars, start, len, x, y, width, w, hAlignment );
			}
		} else if (clip == false) {
		
			wasClipped = true;
			if (g != null) {
				drawAligned( g, m, chars, start, len, x, y, width, w, hAlignment );
			}
		} else {

			wasClipped = true;
			if (g != null) {
				drawClipped( g, m, chars, start, len, x, y, width, w, hAlignment );
			}
		}
	}
}
/**
 * Right aligned decimal formatting of numbers.
 * 
 * @param g Graphics
 * @param m FontMetrics
 * @param chars char[]
 * @param start int
 * @param len int
 * @param x int
 * @param y int
 * @param width int
 */
private final void drawCharsDecimal( Graphics g, FontMetrics m, char[] chars, 
												 int start, int len, int x, int y, int width ) {

	int i, w;
	int zeroWidth = m.charWidth( '0' );
	
	if (width < zeroWidth) return;
	
	int decWidth = decTabstop * zeroWidth + m.charWidth( decimalDelimiter );
	int magWidth;
	
	decWidth = Math.min( decWidth, width-zeroWidth );
	magWidth = Math.max( zeroWidth, width-decWidth );

	// Locate decimal delimiter
	i = start + len - 1;
	while (i >= start) {
		if (chars[i] == decimalDelimiter) break;
		i--;
	}

	if (i<start) {

		// No decimal delimiter found
		w = m.charsWidth( chars, start, len );
		if (w <= magWidth) {
			if (g != null) {
				drawAligned( g, m, chars, start, len, x, y, magWidth, w, ALIGNMENT_RIGHT );
			}
		} else {
			if (g != null) {
				drawClippedLeft( g, m, chars, start, len, x, y, magWidth, w );
			}
			wasClipped = true;
		}
		
	} else {

		// Decimal delimiter located at 'i'

		// Draw magnitude
		int l = i-start;
		w = m.charsWidth( chars, start, l );
		if (w <= magWidth) {
			if (g != null) {
				drawAligned( g, m, chars, start, l, x, y, magWidth, w, ALIGNMENT_RIGHT );
			}
		} else {
			if (g != null) {
				drawClippedLeft( g, m, chars, start, l, x, y, magWidth, w );
			}
			wasClipped = true;
		}

		// Draw decimals
		l = len - i;
		w = m.charsWidth( chars, i, l );
		if (w <= decWidth) {
			if (g != null) {
				drawAligned( g, m, chars, i, l, x+magWidth, y, decWidth, w, ALIGNMENT_LEFT);
			}
		} else {
			if (l > 2 || (l == 2 && chars[i+1] != '0')) {
				if (g != null) {
					drawClipped( g, m, chars, i, l, x+magWidth, y, decWidth, w, ALIGNMENT_LEFT);
				}
				wasClipped = true;
			}
		}
	}

	preferredWidth = w + decWidth;
}
/**
 * Left aligned formatting of strings with embedded tabs.
 * 
 * @param g Graphics
 * @param m FontMetrics
 * @param chars char[]
 * @param start int
 * @param len int
 * @param x int
 * @param y int
 * @param width int
 */
private final void drawCharsTabbed( Graphics g, FontMetrics m, char[] chars, int start, 												int len, int x, int y, int width ) {

	int i, w;

	if (tabstops == null) {

		w = m.charsWidth( chars, start, len );
		preferredWidth = w;
		if (w <= width) {
			if (g != null && len > 0) {
				g.drawChars( chars, start, len, x, y );
			}
		} else {

			wasClipped = true;			
			if (g != null) {
				drawClipped( g, m, chars, start, len, x, y, width, w, ALIGNMENT_LEFT );
			}
		}

	} else {

		final int zero = m.charWidth( '0' );
		int tab = 0;
		int curStart = start;
		int curX = x;
		int maxX = x + width;
		int past = start + len;
		int curWidth;
			
		i = nextTabIndex( chars, start, len );
		int curLen = i-curStart;

		do {
			if (tab >= tabstops.length || i == past) {
				curWidth = Short.MAX_VALUE;
			} else {
				curWidth = tabstops[tab] * zero;
			}
				
			if (curX + curWidth > maxX) curWidth = maxX - curX;
				
			w = m.charsWidth( chars, curStart, curLen );
			if (w <= curWidth) {
				if (g != null && curLen > 0) {
					g.drawChars( chars, curStart, curLen, curX, y );
				}
				if (i >= past) {
					preferredWidth = curX + w - x;
					return;
				}
			} else {
				if (g != null) {
					drawClipped( g, m, chars, curStart, curLen, curX, y, 
						curWidth, w, ALIGNMENT_LEFT );
				}
				if (i >= past) {
					wasClipped = true;
					preferredWidth = curX + w - x;
					return;
				}
			}

			tab++;
			curX += curWidth;
			curStart += curLen + 1;
			i = nextTabIndex( chars, curStart, len - curStart );
			curLen = i-curStart;
						
		} while (true);
	}
}
/**
 * Draw a string of characters aligned according to 'hAlign' within the rectangle
 * starting at 'x', 'y', having width 'width'.
 *
 * 'drawClipped' means that the string is painted with an appended 
 * string of dots if some of the string falls outside the destination rectangle.
 * 
 * @param g Graphics to draw to.
 * @param m FontMetrics to be used for drawing.
 * @param chars Character data array.
 * @param start start index into 'chars'.
 * @param len Number of characters to draw from 'chars'.
 * @param x Left most coordinate for drawing.
 * @param y Baseline y coordinate for drawing.
 * @param width Available width for drawing.
 * @param w The width of the string to be drawn.
 * @param hAlign The alignment constant.
 */
private final void drawClipped( Graphics g, FontMetrics m, char[] chars, int start, 
										  int len, int x, int y, int width, int w, int hAlign ) {

	int dotsWidth = m.charsWidth( dotsString, 0, 2 );
	int i;
			
	// Append '..' to the right of the string.
	
	i = start + len - 1;			
	w += dotsWidth;
	
	while (i >= start && w > width) {
		w -= m.charWidth( chars[i--] );
	}

	int count = i-start+1;
		
	switch (hAlign) {

		case ALIGNMENT_LEFT:

			if (count > 0) g.drawChars( chars, start, count, x, y );
			g.drawChars( dotsString, 0, 2, x + w - dotsWidth, y );
			break;
		
		case ALIGNMENT_RIGHT:

			if (count > 0) g.drawChars( chars, start, count, x + width - w, y );
			x = Math.max( x + width - dotsWidth, x );
			g.drawChars( dotsString, 0, 2, x, y );
			break;

		case ALIGNMENT_CENTER:
							
			int left = x+(width-w)/2;
			if (count > 0) g.drawChars( chars, start, count, left, y );
			left = Math.max( left + w - dotsWidth, x ); 
			g.drawChars( dotsString, 0, 2, left, y );
			break;
		}
	}
/**
 * Draw a string of characters right aligned within the rectangle
 * starting at 'x', 'y', having width 'width'.
 *
 * 'drawClippedLeft' means that the string is painted with a prepended 
 * string of dots if some of the string falls outside the destination rectangle.
 * 
 * @param g Graphics to draw to.
 * @param m FontMetrics to be used for drawing.
 * @param chars Character data array.
 * @param start start index into 'chars'.
 * @param len Number of characters to draw from 'chars'.
 * @param x Left most coordinate for drawing.
 * @param y Baseline y coordinate for drawing.
 * @param width Available width for drawing.
 * @param w The width of the string to be drawn.
 */
private final void drawClippedLeft( Graphics g, FontMetrics m, char[] chars, int start, 
										  int len, int x, int y, int width, int w ) {

	int dotsWidth = m.charsWidth( dotsString, 0, 2 );
	int i, end;
			
	// Append '..' to the left of the string.
		
	i = start;			
	w += dotsWidth;
	end = start + len - 1;
			
	while (i <= end && w > width) {
		w -= m.charWidth( chars[i++] );
	}

	int count = end-i+1;
	g.drawChars( dotsString, 0, 2, Math.max( x, x + width - w), y );
	if (count > 0) g.drawChars( chars, i, count, x + width - w + dotsWidth, y );

}
/**
 * Format a String within a specified destination rectangle.
 * 
 * @param g java.awt.Graphics
 * @param m java.awt.FontMetrics
 * @param s java.lang.String
 * @param x int
 * @param y int
 * @param width int
 * @param height int
 */
public void formatString( Graphics g, FontMetrics m, String s, int x, int y, 
	int width, int height ) {

	drawChars( g, m, s.toCharArray(), 0, s.length(), x, 
		y + calcBaselineOffset( m, height ), width );
}
/**
 * Get the 'clip' field.
 * 
 * @return <code>true</code> if the formatted strings are clipped to their destination
 * rectanble by appending the String stored in the 'dotsString' member. 
 * <code>false</code> otherwise.
 *
 */
public final boolean getClip() {

	return clip;
}
/**
 * Get the current decimal delimiter (e.g. decimal point) character.
 * 
 * @return The contents of the 'decimalDelimiter' field.
 */
public static char getDecimalDelimiter() {

	return decimalDelimiter;
}
/**
 * Get the current setting of the decimal tabstop value. This value specifies the
 * position in widths of the character zero from the right of the destination 
 * rectangle where the decimal delimiter is positioned. This value is only relevant
 * in combination with the horizontal formatting option ALIGNMENT_RIGHT_DECIMAL.
 * 
 * @return The current decimal tabstop value.
 */
public final int getDecTabstop() {

	return decTabstop;
}
/**
 * Get the horizontal alignment constant.
 * 
 * @return hAlignment The constant specifying the horizontal alignment of the string.
 */
public final int getHAlignment() {

	return hAlignment;
}
/**
 * Count the number of lines the String was formatted to.
 * 
 * @return The number of lines a given string needs for proper formatting.
 */
public final int getLineCount() {

	return lineCount;
}
/**
 * Get the preferred height of a properly formatted string in pixels.
 * 
 * 
 * @return The height of the formatted String in pixels.
 */
public final int getPreferredHeight() {

	return preferredHeight;
}
/**
 * Get the preferred width of the formatted String.
 * 
 * @return int The number of pixels required to properly format the string.
 */
public final int getPreferredWidth() {

	return preferredWidth;
}
/**
 * Get the current tabstop settings.
 * 
 * @return The array of <code>int</code>s specifying the positions of the tabstops
 * in widths of the character zero of the current font.
 */
public final int[] getTabstops() {

	return tabstops;
}
/**
 * Get the vertical alignment constant.
 * 
 * @return vAlignment The constant specifying the vertical alignment of the string.
 */
public final int getVAlignment() {

	return vAlignment;
}
/**
 * 
 * @return <code>true</code> if this class supports string wrapping,
 * <code>false</code> otherwise.
 */
public boolean isWrappingFormatter() {
	
	return false;
}
/**
 * Find out the dimension of a string without actually drawing it. First call this
 * method and then query the properties by calling the respective methods of this
 * class (e.g. getPreferredHeight()).
 * 
 * @param m java.awt.FontMetrics
 * @param s java.lang.String
 * @param x int
 * @param y int
 * @param width int
 * @param height int
 */
public void measureString( FontMetrics m, String s, int x, int y, 
	int width, int height ) {

	formatString( null, m, s, x, y, width, height );
}
/**
 * Find the index of the next tab character in the string.
 * 
 * @return The index of the next tab character in the passed character array.
 * @param chars char[]
 * @param start int
 * @param len int
 */
private int nextTabIndex( char[] chars, int start, int len ) {

	int past = start+len;
	
	while (start<past) {
		if (chars[start] == '\t') break;
		start++;
	}

	return start;
}
/**
 * Turn on/off visual string clipping feedback. 
 * 
 * @param clip The new state of the 'clip' property.
 */
public void setClipEnabled( boolean clip ) {

	this.clip = clip;
}
/**
 * Set the decimal delimiter character.
 * 
 * @param c The new decimal delimiter character.
 */
public static void setDecimalDelimiter( char c ) {

	decimalDelimiter = c;
}
/**
 * Set the decimal tabstop position.
 * 
 * @param decTabStop The position of the decimal tabstop.
 */
public final void setDecTabstop( int decTabstop ) {

	this.decTabstop = decTabstop;
}
/**
 * Set the horizontal alignment constant.
 * 
 * @param hAlignment The constant specifying the horizontal alignment of the string.
 */
public final void setHAlignment( int hAlignment ) {

	this.hAlignment = hAlignment;
}
/**
 * Set the tabstop array.
 * 
 * @param tabs The array specifying the position of the tabstops in widths of the
 * character zero of the current font.
 */
public final void setTabstops( int[] tabs ) {

	tabstops = tabs;
}
/**
 * Set the vertical alignment constant.
 * 
 * @param vAlignment The constant specifying the vertical alignment of the string.
 */
public final void setVAlignment( int vAlignment ) {

	this.vAlignment = vAlignment;
}
/**
 * Find out whether the formatted string was clipped
 * 
 * @return <code>true</code> if the String was clipped because of a lack of space,
 * </code>false</code> otherwise.
 */
public final boolean wasClipped() {

	return wasClipped;
}
}
