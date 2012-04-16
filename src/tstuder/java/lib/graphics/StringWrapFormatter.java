package tstuder.java.lib.graphics;

/*
 * This class extends StringFormatter. It implements methods to paint 
 * a string (possibly wrapped to multiple lines) within some destination rectangle.
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

import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * <p>Extends the class "StringFormatter" to support the drawing of wrapped Text.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * &nbsp; ts, 1999-09-24, v1.0.2,  Added isWrappingFormatter() to let
 * &nbsp;                          clients of this class figure out if this
 * &nbsp;                          class supports string wrapping. Mainly used
 * &nbsp;                          for performance optimization in class
 * &nbsp;                          ...table.DataArea.
 * &nbsp; ts, 2000-04-06, v1.0.3,  <code>startIndexes</code> and <code>pastIndexes</code>
 * &nbsp;                          are now initialized upon constructing the class and not
 * &nbsp;                          anymore in the <code>wrap(..)</code> method. This puts
 * &nbsp;                          less stress on the memory manager as fewer short-lived
 * &nbsp;                          objects are being created during paints.
 * &nbsp; ts, 2000-05-24, v1.0.3,  - Improved temporary memory handling.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class StringWrapFormatter extends StringFormatter {

	/** An array of characters where wrapping is performed without drawing
	 *  the wrap character.
	 */
	protected char[] 		normalWrapCharacters			= { ' ', '\n', '_' };

	/** An array of characters where wrapping is performed and the wrap
	 *  character is drawn.
	 */
	protected  char[] 	persistentWrapCharacters	= { '-', '/' };
		
	/** <code>true</code> if line wrapping is enabled. If 'wrap' is <code>false</code>,
	 *  this class still breaks lines at '\n' but doesn't do any
	 *  line wrapping based on String width anymore.
	 */
	protected boolean		wrap								= true;

	protected static final int MAX_CLEAR_COUNTER = 1024;
	protected int              clearCounter = 0;
	protected int[]            startIndexes;
	protected int[]		      pastIndexes;
	protected char[]           charBuf;
	protected int			      lineCount;
	protected boolean		      normalWrap;
/**
 * Construct a StringWrapFormatter.
 */
public StringWrapFormatter() {

}
/**
 * Construct a StringWrapFormatter.
 * 
 * @param hAlignment One of the horizontal alignment options defined in
 * StringFormatter.
 * @param vAlignment One of the vertical alignment options defined in
 * StringFormatter.
 */
public StringWrapFormatter( int hAlignment, int vAlignment ) {

	super( hAlignment, vAlignment );
}
/**
 * Construct a StringWrapFormatter.
 * 
 * @param	  	
 * @return		
 * @exception	
 * 
 * @param hAlignment One of the horizontal alignment options defined in
 * StringFormatter.
 * @param vAlignment One of the vertical alignment options defined in
 * StringFormatter.
 * @param tabstops An array of <code>int</code>s specifying the tabstops in 
 * widths of the character zero of the current font.
 * @param decTabstop The position of the decimal point in widths of the character
 * zero of the current font (from the right edge of the destination rectangle). 
 * Only relevant for the alignment option ALIGNMENT_RIGHT_DECIMAL.
 */
public StringWrapFormatter( int hAlignment, int vAlignment, int[] tabstops, int decTabstop ) {

	super( hAlignment, vAlignment, tabstops, decTabstop );
}
/**
 * @see StringFormatter.formatString
 */
public void formatString( Graphics g, FontMetrics m, String s, int x, int y, 
	int width, int height ) {

	setUpBuffers( s );
	s.getChars( 0, s.length(), charBuf, 0 );

	// Calcluate the indexes into 'chars' at which to wrap.
	//
	wrap( m, charBuf, 0, s.length(), width );

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
		drawChars( g, m, charBuf, startIndexes[i], pastIndexes[i]-startIndexes[i],
			x, curBaseline, width );
		curBaseline += fontHeight;
		if (preferredWidth > maxWidth) maxWidth = preferredWidth;
	}

	preferredHeight = blockHeight;
	preferredWidth = maxWidth;
}
/**
 * Get the array of "normal" wrap characters (the ones (like space) that are removed
 * from the wrapped text).
 *
 * @return A <code>char</code> array containing the current wrap characters.
 */
public char[] getNormalWrapCharacters() {

	return normalWrapCharacters;
}
/**
 * Get the array of "persistent" wrap characters (the ones (like '-')
 * that are <em>not</em> removed from the wrapped text).
 *
 * @return A <code>char</code> array containing the current wrap characters.
 */
public char[] getPersistentWrapCharacters() {

	return persistentWrapCharacters;
}
/**
 * Get the 'wrap' property. 
 * 
 * @return <code>true</code> if String wrapping is turned on, <code>false</code>
 * if not (note that a new-line character always breaks a String, regardless of 
 * the state of this property).
 *
 */
public boolean getWrap() {

	return wrap;
}
/**
 * Find out if a given character is part of the array of "normal" wrap characters.
 * 
 * @param c The character of interest.
 * @see getNormalWrapCharacters
 * 
 */
private boolean isNormalWrapCharacter( char c ) {

	for (int i=0; i<normalWrapCharacters.length; i++) {
		if (normalWrapCharacters[i] == c) return true;
	}

	return false;
}
/**
 * Find out if a given character is part of the array of "persistent" wrap characters.
 * 
 * @param c The character of interest.
 * @see getPersistentWrapCharacters
 */
private boolean isPersistentWrapCharacter( char c ) {

	for (int i=0; i<persistentWrapCharacters.length; i++) {
		if (persistentWrapCharacters[i] == c) return true;
	}

	return false;
}
/**
 * 
 * @return <code>true</code> if this class supports string wrapping,
 * <code>false</code> otherwise. This method returns true regardless of the
 * state of the 'wrapEnabled' property because new lines break a string
 * in any case which is considered wrapping in the context of this call.
 */
public boolean isWrappingFormatter() {
	
	return true; 
}
/**
 * Locates the position within a character array where a new line starts.
 * Usually called with the result from a previous call to 'nextWrapIndex()' 
 * as the 'start' argument. Advances 'start' to the beginning of the next
 * word/line.
 * 
 * @param chars The characters to scan.
 * @param start The index of the first character to look at.
 * @param past The index of the character one past the last one to look at.
 * @return The index of the next lines's first character.
 */
private int nextStartIndex( char[] chars, int start, int past ) {

	if (start < past && chars[start] == '\n') {
		return start + 1;			// consecutive new-lines are not skipped.
	} else {
		while (start < past && isNormalWrapCharacter( chars[start] )) {
			start++;
		}
		while (start < past && isPersistentWrapCharacter( chars[start] )) {
			start++;
		}
	}

	return start;
}
/**
 * Returns the index of the next character where wrapping can take place.
 * The index returned points to one character past the last one to be printed, i.e.
 * on the current line, print up to but not including the character with the
 * returned index.
 * 
 * @param chars The character array to scan.
 * @param start The index of the character where scanning starts.
 * @param past Scan up to the character just before the one with this index.
 * @return The index of the next possible wrap position.
 */
private int nextWrapIndex( char[] chars, int start, int past ) {

	if (wrap) {

		// Wrap on any of the defined wrapping characters.

		while (start < past) {
			if (isPersistentWrapCharacter( chars[start] )) {
				normalWrap = false;
				return start+1;
			} else if (isNormalWrapCharacter( chars[start] )) {
				normalWrap = true;
				return start;
			}
			start++;
		}

	} else {

		// Only wrap on new lines.

		while (start < past) {
			if (chars[start] == '\n') return start;
			start++;
		}
	}

	return past;
}
/**
 * Store the relevant indexes for one line of wrapped text for future access.
 * Maintains its own dynamic vectors of <code>int</code>s.
 * 
 * @param line The zero-based line index.
 * @param start The start index to store.
 * @param past The past index to store.
 */
private void setLineIndexes( int line, int start, int past ) {

	if (line >= startIndexes.length) {

		// Increase the size of the index arrays
		// I don't want to use <code>Vector</code>s because I would have to store
		// Integer objects to hold the indexes instead of using plain <code>int</code>s. 
		// Moreover, casting Vector elements to <code>Integer</code> would be necessary
		// which is always a bit ugly.
		// 
		int[] newStartIndexes = new int[2*startIndexes.length];
		int[] newPastIndexes = new int[2*pastIndexes.length];
		
		System.arraycopy( startIndexes, 0, newStartIndexes, 0, startIndexes.length );
		System.arraycopy( pastIndexes, 0, newPastIndexes, 0, pastIndexes.length );

		startIndexes = newStartIndexes;
		pastIndexes = newPastIndexes;
	}

	startIndexes[line] = start;
	pastIndexes[line] = past;
}
/**
 * Set the array of "normal" wrap characters.
 * 
 * @param chars The array to set. 	
 * @see getNormalWrapCharacters
 */
public void setNormalWrapCharacters( char[] chars ) {

	normalWrapCharacters = chars;
}
/**
 * Set the array of "persistent" wrap characters.
 * 
 * @param chars The array to set. 	
 * @see getPersistentWrapCharacters
 */
public void setPersistentWrapCharacters( char[] chars ) {

	persistentWrapCharacters = chars;
}
/**
 * 
 * 
 * @param s java.lang.String
 */
protected final void setUpBuffers( String s ) {

	if (clearCounter++ % MAX_CLEAR_COUNTER == 0) {
		startIndexes = new int[8];
		pastIndexes = new int[8];
		charBuf = new char[s.length()];
	}

	if (charBuf.length < s.length()) charBuf = new char[s.length()];
}
/**
 * Set the 'wrap' property to a specified state.
 * 
 * @param wrap <code>true</code> to turn on wrapping, <code>false</code> to turn it
 * off. Note that a new-line character breaks a String regardless of the state of
 * this property. 
 */
public void setWrapEnabled( boolean wrap ) {

	this.wrap = wrap;
}
/**
 * Wrap a String. The result of a call to this method are line-start and
 * end indexes which get stored in two
 * member arrays of this class. 
 * 
 * @param m The FontMetrics object used to measure runs of characters.
 * @param chars The array of characters to wrap.
 * @param start The index into 'chars' where wrapping starts.
 * @param past One past the index into 'chars' where wrapping stops. 
 * @param width The width (in pixels) determining the maximum width of the wrapped
 * text.
 */
protected final void wrap( FontMetrics m, char[] chars, int start, int past, int width ) {
	
	lineCount = 0;	

	int wordStart = nextStartIndex( chars, start, past );
	int curLineStart = wordStart;
	int wordPast = nextWrapIndex( chars, wordStart, past );
	int curLinePast = wordPast;
	int curLineWidth = 0;
	int wordWidth = m.charsWidth( chars, wordStart, wordPast-wordStart );
	boolean forceBreak = false;

	while (true) {

		// Add words to the current line
		while (wordPast < past && curLineWidth + wordWidth <= width && ! forceBreak) {

			// Increment size of current line			
			curLineWidth += wordWidth;
			if (normalWrap) curLineWidth += m.charWidth( chars[wordPast] );
			curLinePast = wordPast;

			if (chars[wordPast] == '\n') forceBreak = true;

			// Advance to next word
			wordStart = nextStartIndex( chars, wordPast, past );
			wordPast = nextWrapIndex( chars, wordStart, past );
			wordWidth = m.charsWidth( chars, wordStart, wordPast-wordStart );
		}

		// There's a forced break ('\n) or the current word doesn't fit --> 
		//	   break the current line.
		if (forceBreak || curLineWidth + wordWidth > width) {

			forceBreak = false;
			if (curLineWidth == 0) {
				setLineIndexes( lineCount++, wordStart, wordPast );
				if (wordPast >= past) return;
				wordStart = nextStartIndex( chars, wordPast, past );
				curLineStart = wordStart;
				wordPast = nextWrapIndex( chars, wordStart, past );
				curLinePast = wordPast;
			} else {
				setLineIndexes( lineCount++, curLineStart, curLinePast );
				curLineStart = wordStart;
				curLinePast = wordPast;
			}
			curLineWidth = 0;
		}

		// Last word reached
		if (wordPast >= past) {

			setLineIndexes( lineCount++, curLineStart, past );
			break;
		}
	}
}
}
