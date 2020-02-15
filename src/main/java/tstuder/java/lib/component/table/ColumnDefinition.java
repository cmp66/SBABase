package tstuder.java.lib.component.table;

/*
 * A ColumnDefinition encapsulates the layout and formatting options of a
 * table column and its title.
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
import tstuder.java.lib.graphics.*;

/**
 * A ColumnDefinition Object encapsulates the the layout and formatting options 
 * of a table column and its title.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * &nbsp; ts, 1999-09-24, v1.0.2,  Added basic support for wrapping
 * &nbsp;                          data cells, including new method 
 * &nbsp;                          <code>getWrapDataCells()</code>.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class ColumnDefinition {

	/** Set the default width of the column to a fixed number of pixels. */
	public static final int	WIDTH_FIXED_N_PIXELS	= 0;

	/** Set the default width of the column to the with in pixels of the number passed
	*  times the width of the character zero of the column's data font
	*/
	public static final int WIDTH_FIXED_N_CHARS	= 1;

	/** Set the default width of the column as determined by the width of the column's
	*  title. 
	 */
	public static final int	WIDTH_AUTO_BY_TITLE	= 2; 

	/** Set the default width of the column as determined by the column's data
	*  such that all data rows are completely visible. If this width is too
	*  narrow to display the column title, wrap the title on two or more lines. If
	*  the title can not be wrapped to the width of the column data, the column
	*  width is determined by the smallest value the title can be wrapped to.
	 */
	public static final int WIDTH_AUTO				= 3; 


	private int					widthInChars				= 10;
	private int					widthInPixels				= 60;
	private int 				widthAdjustment			= WIDTH_AUTO_BY_TITLE;

	private Color				titleBackground;
	private Color				titleForeground;

	private Color				dataBackground;
	private Color				dataForeground;

	private String				title;

	private Font				titleFont;
	private Font				dataFont;
	private FontMetrics		titleFontMetrics;
	private FontMetrics		dataFontMetrics;
	private boolean			wrapDataCells;

	private StringFormatter	titleFormatter;
	private StringFormatter	dataFormatter;
/**
 * Construct a ColumnDefinition object.
 * 
 * @param title The column's title.
 */
public ColumnDefinition( String title ) {

	this( title, WIDTH_AUTO_BY_TITLE, null, null );
}
/**
 * Construct a ColumnDefinition object.
 * 
 * @param title The column's title.
 * @param widthAdjustment The column's width adjustment option (one of the constants
 * defined in this class).
 * @param titleFormatter The StringFormatter object used to render the column title.
 * Can be null, in which case the default StringFormatter is used.
 * @param dataFormatter The StringFormatter object used to render the column's 
 * data cells. Can be null, in which case the default StringFormatter is used.
 * 
 */
public ColumnDefinition( String title, int widthAdjustment,
	StringFormatter titleFormatter, StringFormatter dataFormatter ) {

	setTitleFormatter( titleFormatter );
	setDataFormatter( dataFormatter );
	
	this.title = title;
	this.widthAdjustment = widthAdjustment;
}
/**
 * Construct a ColumnDefinition object.
 * 
 * @param title The column's title.
 * @param titleFormatter The StringFormatter object used to render the column title.
 * Can be null, in which case the default StringFormatter is used.
 * @param dataFormatter The StringFormatter object used to render the column's 
 * data cells. Can be null, in which case the default StringFormatter is used.
 * 
 */
public ColumnDefinition( String title, StringFormatter titleFormatter,
	StringFormatter dataFormatter ) {

	this( title, WIDTH_AUTO_BY_TITLE, titleFormatter, dataFormatter );
}
/**
 * Get the color of the column's data background.
 * 
 * @return The column's data background color.
 */
public Color getDataBackground() {

	return dataBackground;
}
/**
 * Get the column's data font.
 * 
 * @return The font the column's data cells are drawn with.
 */
public Font getDataFont() {

	return dataFont;
}
/**
 * Get the column's data font metrics.
 * 
 * @return The FontMetrics object associated with the column's data font.
 */
public FontMetrics getDataFontMetrics() {

	return dataFontMetrics;
}
/**
 * Get the column data's default foreground color. 
 * 
 * @return The foreground color the column's data is drawn in.
 */
public Color getDataForeground() {

	return dataForeground;
}
/**
 * Get the StringFormatter object used to draw the column's data.
 * 
 * @return The StringFormatter object used to draw the column's data cells.
 */
public StringFormatter getDataFormatter() {

	return dataFormatter;
}
/**
 * Calculate and return the height of a data cell.
 * 
 * @param defaultFontMetrics The FontMetrics object used to calculate the height.
 * Can be <code>null</code> in which case the ColumnDefinition's default
 * FontMetrics object is used.
 * @return The height in pixels of one data cell.
 */
public int getPreferredDataLineHeight( FontMetrics defaultFontMetrics ) {

		FontMetrics m = (dataFontMetrics == null) ? defaultFontMetrics : dataFontMetrics;	

		return m.getHeight();
}
/**
 * Calculate and return the preferred height of the column's title.
 * 
 * @param defaultFontMetrics The FontMetrics object of the title font.
 * @param defaultFormatter The StringFormatter object of the title font.
 * @return The preferred height of the column's title.
 */
public int getPreferredTitleHeight( FontMetrics defaultFontMetrics, 
		StringFormatter defaultFormatter ) {

	return getPreferredTitleHeight( defaultFontMetrics, defaultFormatter, -1 );
}
/** 
 * Calculate and return the preferred height of the column's title.
 * 
 * @param defaultFontMetrics The FontMetrics object of the title font.
 * @param defaultFormatter The StringFormatter object of the title font.
 * @param wrapTo The width in pixels the title string should be wrapped to. Pass
 * -1 if no title wrapping should occur.
 * @return The preferred height of the column's title.
 */
public int getPreferredTitleHeight( FontMetrics defaultFontMetrics, 
		StringFormatter defaultFormatter, int wrapTo ) {

		StringFormatter f = (titleFormatter == null) ? 
			defaultFormatter : titleFormatter;
		FontMetrics m = (titleFontMetrics == null) ? 
			defaultFontMetrics : titleFontMetrics;	

		// Measure the title string's height (and other metrics). 
		// By passing MAX_VALUE as the width and height of the rectangle, measureString()
		// won't break the title.
		//
		// To measure a string, measureString() draws (without actually drawing)
		// the string into a rectangle (here (0, 0) to (MAX_VALUE, MAX_VALUE)) and
		// sets stores the actual width and height
		// of the string in the StringFormatter object.

		int width = (wrapTo > 0) ? wrapTo : Short.MAX_VALUE; 
		f.measureString( m, title, 0, 0, width, Short.MAX_VALUE );
		
		return f.getPreferredHeight();

}
/**
 * Calculate and return the preferred width of the column.
 *
 * @param defaultFontMetrics The FontMetrics object to be used when calculating
 * the column title width.
 * @param defaultFormatter The StringFormatter object to be used when calculating
 * the column title width.
 * @return The column's preferred width in pixels.
 */
public int getPreferredWidth( FontMetrics defaultFontMetrics, 
	StringFormatter defaultFormatter ) {

	FontMetrics m;	

	switch (widthAdjustment) {

		case WIDTH_FIXED_N_PIXELS:
			return widthInPixels;

		case WIDTH_FIXED_N_CHARS:
			m = (dataFontMetrics == null) ? defaultFontMetrics : dataFontMetrics;
			return widthInChars * m.charWidth( '0' );

		default:
			m = (titleFontMetrics == null) ? defaultFontMetrics : titleFontMetrics;
			StringFormatter f = (titleFormatter == null) ? 
				defaultFormatter : titleFormatter;

			// Measure the title string's width (and other metrics). 
			// By passing MAX_VALUE as the width and height of the rectangle, 
			// measureString() won't break the title.
			//
			// To measure a string, measureString() draws (without actually drawing)
			// the string into a rectangle (here (0, 0) to (MAX_VALUE, MAX_VALUE)) and
			// stores the actual width and height
			// of the string in the StringFormatter object.
			f.measureString( m, title, 0, 0, Short.MAX_VALUE, Short.MAX_VALUE );
			
			return f.getPreferredWidth();
	}
}
/**
 * Get the column title string.
 * 
 * @return The column's title.
 */
public String getTitle() {

	return title;
}
/**
 * Get the column title's background color.
 * 
 * @return The background color used for painting the column title.
 */
public Color getTitleBackground() {

	return titleBackground;
}
/**
 * Get the title's font.
 * 
 * @return The column title's font.
 */
public Font getTitleFont() {

	return titleFont;
}
/**
 * Get the FontMetrics object used for the column title.
 *
 * @return The column title's FontMetrics object.
 */
public FontMetrics getTitleFontMetrics() {

	return titleFontMetrics;
}
/**
 * Get the column title's foreground color.
 * 
 * @return The foreground color used to paint the column title.
 */
public Color getTitleForeground() {

	return titleForeground;
}
/**
 * Get the column title's StringFormatter object. 
 * 
 * @return The StringFormatter used to paint the column title.		
 */
public StringFormatter getTitleFormatter() {

	return titleFormatter;
}
/**
 * Get the current width adjustment property (one of the constants defined as part of
 * the class definition, e.g. WIDTH_FIXED_N_PIXELS).
 * 
 * @return The current column width adjustment constant.
 */
public int getWidthAdjustment() {

	return widthAdjustment;
}
/**
 * Get the number of characters used for the WIDTH_FIXED_N_CHARS width
 * adjustment property.
 * 
 * @return The number of widths of the character '0' (zero) used to determine the
 * width of the column.
 * @exception java.lang.IllegalArgumentException if widthAdjustment != 
 * WIDTH_FIXED_N_CHARS.
 */
public int getWidthInChars() throws IllegalArgumentException {

	if (widthAdjustment != WIDTH_FIXED_N_CHARS) throw new IllegalArgumentException();
	
	return widthInChars;	
}
/**
 * Get the column width in pixels for the width adjustment option WIDTH_FIXED_N_PIXELS.
 *
 * @return The width of the column in pixels if the width adjustment option
 * WIDTH_FIXED_N_PIXELS is used.
 * @exception java.lang.IllegalArgumentException if widthAdjustment != 
 * WIDTH_FIXED_N_PIXELS.
 */
public int getWidthInPixels() throws IllegalArgumentException {

	if (widthAdjustment != WIDTH_FIXED_N_PIXELS) throw new IllegalArgumentException();
	
	return widthInPixels;	
}
/**
 * Get the 'wrapDataCells' property.
 *
 * @return boolean The wrapDataCells property. This method is only
 * provided for performance improvement during line drawing in
 * the DataArea class. 'wrapDataCells' is true if the ColumnDefinition's
 * dataFormatter is an instance of StringWrapFormatter (or some other
 * wrapping string formatter class). Otherwise it is
 * false.
 */
boolean getWrapDataCells() {
	
	return wrapDataCells;
}
/**
 * Set the background color for the column's data cells.
 * 
 * @param background The Color used to paint the data cells' background.
 */
public void setDataBackground( Color background ) {
	
	dataBackground = background;
}
/**
 * Set the Font to be used to draw the column's data cells.
 * 
 * @param f The Font to draw the column data.
 */
@SuppressWarnings("deprecation")
public void setDataFont( Font f ) {

	dataFont = f;
	dataFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics( f );
}
/**
 * Set the foreground color for the column's data.
 * 
 * @param foreground The Color in which to draw the column's data.	  	
 */
public void setDataForeground( Color foreground ) {

	dataForeground = foreground;
}
/**
 * Set the StringFormatter object used to format the column's data.
 * 
 * @param formatter The StringFormatter to draw the column's data.
 */
public void setDataFormatter( StringFormatter formatter ) {

	dataFormatter = formatter;

	if (dataFormatter != null && dataFormatter.isWrappingFormatter()) {
		wrapDataCells = true;
	} else {
		wrapDataCells = false;
	}
}
/**
 * Set the column's title.
 * 
 * @param title The column title.	
 */
public void setTitle( String title ) {

	this.title = title;
}
/**
 * Set the background color used to paint the column title's background.
 * 
 * @param background The column title's background color.
 */
public void setTitleBackground( Color background ) {
	
	titleBackground = background;
}
/**
 * Set the Font for  the column's title.
 * 
 * @param f The Font used to draw the column title.	  	
 */
@SuppressWarnings("deprecation")
public void setTitleFont( Font f ) {

	titleFont = f;
	titleFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics( f );
}
/**
 * Set the column title's foreground color.
 * 
 * @param foreground The column title's foreground color.	  	
 */
public void setTitleForeground( Color foreground ) {

	titleForeground = foreground;
}
/**
 * Set the StringFormatter object to format the column title.
 * 
 * @param formatter The StringFormatter object used to paint the column title.
 */
public void setTitleFormatter( StringFormatter formatter ) {

	titleFormatter = formatter;
}
/**
 * Set the "widthAdjustment" property to one of the predefined WIDTH... constants.
 * 
 * @param adjustment One of the WIDTH... constants defined as part of the class.	
 */
public void setWidthAdjustment( int adjustment ) {

	widthAdjustment = adjustment;
}
/**
 * Set the width adjustment property to the value WIDTH_FIXED_N_CHARS and set
 * the number of characters (zeroes) to be used for this formatting option.
 * 
 * @param nZeroes The number of widths of the character zero (in the column's data
 * font) used to define the width of the column. 
 */
public void setWidthInChars( int nZeroes ) {

	widthAdjustment = WIDTH_FIXED_N_CHARS;
	widthInChars = nZeroes;
}
/**
 * Set the width adjustment property WIDTH_FIXED_N_PIXELS and set the number of
 * pixels to be used for this formatting option.
 * 
 * @param nPixels The number of pixels defining the width of the column.		
 */
public void setWidthInPixels( int nPixels ) {

	widthAdjustment = WIDTH_FIXED_N_PIXELS;
	widthInPixels = nPixels;
}
}
