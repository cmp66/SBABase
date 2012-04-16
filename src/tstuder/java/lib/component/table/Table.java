package tstuder.java.lib.component.table;

/*
 * The Table class implements a table (data grid) Java bean with an optional
 * titlebar, one or more data columns and support for horizontal and/or
 * vertical scrolling, row selection and checkmarking.
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
import java.util.Vector;
import tstuder.java.lib.graphics.*;
import tstuder.java.lib.util.*;
import java.awt.event.*;
import java.beans.*;

/**
 * The Table bean's main class. It inherits from Container.
 *
 * <p>Copyright (C) 1999 Thomas Studer<br>
 * mailto:tstuder@datacomm.ch<br>
 * http://www.datacomm.ch/tstuder
 *
 * <p>The Table bean is being released under the <a href="../../../license.txt">
 * Gnu Lesser General Public License</a>.
 *
 * <p><b>General Remarks</b></p>
 *
 * <p>To get an idea how to use the table's API, have a look at the source
 * code for the demo applets at my <a href="http://www.datacomm.ch/tstuder/resources">resources page</a>.
 *
 * <p>The table doesn't provide special methods to add or insert rows and only a few methods
 * to remove rows. Instead,
 * the table expects the client to manage the vector containing the rows from
 * outside the table. For that purpose, special notification methods are
 * available to allow the client to notify the table of any changes made to
 * the rows vector. An example:
 *
 * <p>Suppose you've initialized a table to display data rows defined by a
 * Vector named 'rows' in your source code. 
 * Also suppose the name of the Java object referring to
 * the initialized table is 'table'. Now, to remove, for example, the first row from the
 * rows Vector and to reflect that change in the table, write code similar to
 * this:
 * <pre>
 * &nbsp;   synchronized (rows) {         // Ensure exclusive access to rows Vector.
 * &nbsp;      rows.removeElementAt( 0 ); // Remove first row from rows Vector.
 * &nbsp;      table.rowsRemoved( 0, 1 ); // Notify the table that one row, at
 * &nbsp;   }                             // position 0, was removed.
 * </pre>
 * <p>As you've probably guessed, the method <code>rowsRemoved</code> (see last line in the above
 * code example) allows you to notify the table
 * of the removal of more than one row. They all have to be adjacent, though. If you want to
 * remove several non-consecutive rows, use one call to <code>rowsRemoved()</code> for each
 * consecutive sub-range.
 *
 * <p>Adding, inserting and updating rows works in a similar way and the notification
 * methods available are named <code>rowsAdded()</code>, <code>rowsInserted()</code> and 
 * <code>rowsUpdated()</code>, respectively.
 *
 * <p>The Table bean provides various formatting options. Have a look at the public methods (static and normal ones) 
 * in the classes <code>Table</code>, <code>Titlebar</code> and <code>DataArea</code> to get an idea
 * of the various table configuration possibilities.
 *
 * <p>Even though it wasn't planned in the original design, support for table cells showing
 * icons or images can easily be added. For that purpose, write your own StringFormatter subclass,
 * overriding the method <code>drawChars( Graphics g, FontMetrics m, char[] chars, int start, 
 * int len, int x, int y, int width )</code> to draw the desired graphic object (e.g. by interpreting
 * the string passed in the 'chars' argument as the name of a GIF file).
 *
 * <p>The implementation of the ItemSelectable interface is not great. It first was
 * only implemented for the DataArea. With version 1.0.1 it was moved to the
 * Table itself. That's why you'll find a fireItemEvent method in DataArea and
 * not in the Table. Putting it in the TableBean is probably easier for the
 * user however, as ItemListeners registered with the Table class will receive
 * the Table as the source of the event and not, as it was before, the DataArea.
 *
 * <p>In general, items selected programmatically don't fire an ItemEvent with
 * the exception of calls to <code>select( int )</code> or 
 * <code>select( int, boolean )</code>. Those do fire ItemEvents for Tables
 * with selection modes of <code>SELECT_ONE</CODE> or <code>SELECT_ZERO_OR_ONE</code>.
 *
 * <p><b>Class Diagram</b></p> 
 * <p><img src="TableClassDiagram.PNG"></img>
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * &nbsp; ts, 1999-07-05, v1.0.1   - Added method setTableData( Vector ).
 * &nbsp;                          - Added method scrollToRow( int ). 
 * &nbsp;                          - Improved selection handling for single selections.
 * &nbsp; ts, 1999-09-24, v1.0.2   - Moved ItemSelectable interface to Table. Item
 * &nbsp;                            listeners now have to be registered through the table. Along the same
 * &nbsp;                            lines, moved action listener registration from DataArea to Table.
 * &nbsp;                          - Added method getSelectedObject().
 * &nbsp;                          - Implemented setEnabled(boolean) support.
 * &nbsp;                          - Proper support for double clicks (fires ActionEvent).
 * &nbsp; ts, 2000-05-22, v1.1     - Added column sorting (by means of Titlebar class) and
 * &nbsp;                            support for automatic adjustment of column widths in order to
 * &nbsp;                            avoid the use of the horizontal scrollbar as much as possible
 * &nbsp;                            (adjustToFit property of Titlebar).
 * &nbsp;                          - Fixed a bug in the DataArea class that caused null pointer
 * &nbsp;                            exceptions under certain conditions.
 * &nbsp; ts, 2000-06-06, v1.1.1   - Fixed bug in paintComponents() triggered by a non-visible Titlebar with
 * &nbsp;                            adjustToFit set to true.
 * &nbsp;                          - Also replaced dataArea.getWidth() width dataArea.getSize().width in same method.
 * 
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class Table extends java.awt.Container implements PropertyChangeListener,
	AdjustmentListener, ItemSelectable, ColumnSortListener {

    private static final long serialVersionUID = 1L;

    /** Vertical scrollbar off at all times. */
	public static final int 	VERTICAL_SCROLLBAR_OFF = 0;

	/** Vertical scrollbar is shown as necessary. */
	public static final int		VERTICAL_SCROLLBAR_AUTOMATIC = 1;

	/** Vertical scrollbar on at all times */
	public static final int		VERTICAL_SCROLLBAR_ON = 2;

	/** Horizontal scrollbar off at all times. */
	public static final int		HORIZONTAL_SCROLLBAR_OFF = 0;

	/** Horizontal scrollbar is shown as necessary. */
	public static final int		HORIZONTAL_SCROLLBAR_AUTOMATIC = 1;
	
	/** Horizontal scrollbar is on at all times. */
	public static final int		HORIZONTAL_SCROLLBAR_ON = 2;

	private static Color			defaultBackground			= Color.white;
	private static Color			defaultForeground			= Color.black;

	private static Border		defaultBorder				= new BorderLowered2(); 
	private Border					border;

	private  		int			borderTop;
	private  		int			borderLeft;
	private  		int 			borderRight;
	private  		int			borderBottom;

	private Vector<Object>		rows;
	private Vector<ColumnDefinition> columns;

	private boolean				widthsInitialized			= false;

	private Titlebar				titlebar;
	private boolean				showTitlebar				= true;
	private int						titlebarSkip;

	private DataArea				dataArea;

	private Scrollbar 			horizontalScrollbar;
	private Scrollbar 			verticalScrollbar;

	private int 					horizontalScrollMode 	= HORIZONTAL_SCROLLBAR_AUTOMATIC;
	private int						verticalScrollMode 		= VERTICAL_SCROLLBAR_AUTOMATIC;

	private int						verticalScrollbarWidth;
	private int						verticalScrollbarSkip;
	private boolean				hasVerticalScrollbar;

	private int						horizontalScrollbarHeight;
	private int						horizontalScrollbarSkip;
	private boolean				hasHorizontalScrollbar;

	// Temporaries used during updates and dragging.
	int		w;							// Overall table width
	int		h;							// Overall table height
	boolean 	isDragging;				// True while titlebar columns are being dragged.

/**
 * Construct a Table.
 */
public Table() {

	columns = new Vector<ColumnDefinition>();
	columns.addElement( new ColumnDefinition( "Dummy Column Title" ) );
	rows = new Vector<Object>();
	rows.addElement( new SimpleTableRow( new String[] { "Dummy Row" } ));

	initialize();
}
/**
 * Construct a Table.
 * 
 * @param columnDefinitions A Vector of ColumnDefinition objects, one for each
 * table column.	  		
 */
public Table( Vector<ColumnDefinition> columnDefinitions ) {

	this.columns = columnDefinitions;
	this.rows = new Vector<Object>();
	rows.addElement( new SimpleTableRow( new String[] { "Dummy Row" } ));
	initialize();
}
/**
 * Construct a Table.
 * 
 * @param columnDefinitions A Vector of ColumnDefinition objects, one for each
 * table column.
 * @param entries The Vector containing the Table data. It contains objects
 * of classes implementing the TableEntry interface.	  	
 *
 */
public Table( Vector<ColumnDefinition> columnDefinitions, Vector<Object> rows ) {

	this.columns = columnDefinitions;
	this.rows = rows;
	initialize();
}
/**
 * Add a popup menu to the table's data area.
 * 
 * @param popup The PopupMenu to be added.
 */
public void add( PopupMenu popup ) {

	dataArea.add( popup );
}
/**
 * Add an ActionEvent listener if you want to receive ActionEvents if the user
 * presses the return or enterkey.
 *
 * <p>The event source of any action events delivered by the Table
 * are the Table's DataArea object (to which you can get a 
 * reference by calling getDataArea()).
 * 
 * @param listener The listener to receive ActionEvents.
 *  	
 */
public void addActionListener( ActionListener listener ) {

	dataArea.addActionListener( listener );
}
/**
 * Add an ItemEvent listener if you want to receive ItemEvents if the user
 * changes the current row selection.
 * 
 * @param listener The listener to receive ItemEvents.
 *  	
 */
public void addItemListener( ItemListener listener ) {

	dataArea.addItemListener( listener );
}
/**
 * Adds a new row to the end of the rows Vector and updates the DateArea and
 * scrollbars as necessary. Note: if you have to add many rows 
 * it is faster to get a lock on the rows Vector
 * (i.e. <code>synchronized (getRows()) {...}</code>),
 * add the
 * rows directly to the rows Vector and then call 
 * <code>rowsAdded( number_of_rows_added )</code>. 
 * This way the table is only updated in the <code>rowsAdded()</code>
 * call and not after the addition
 * of every single row.
 *
 * @param row The row to add.
 */
public void addRow( TableRow row ) {

	synchronized (rows) {
		rows.addElement( row );
		rowsAdded( 1 );
	}
}
/**
 * Adjust the width of a particular column based on its column adjustment constant
 * (see the constants defined in ColumnDefinition).
 *
 * @param columnIndex The zero-based index of the column to adjust.
 */
public void adjustColumnWidth( int columnIndex ) {
	
	int[] widths = titlebar.getColumnWidths();

	widths[columnIndex] = measureColumnWidth( columnIndex );
	setColumnWidths( widths );
}
/**
 * Adjust the widths of all columns based on their column adjustment constant
 * (see the constants defined in ColumnDefinition).
 *
 */
public void adjustColumnWidths() {
	
	int[] widths = titlebar.getColumnWidths();

	for (int i=0; i<widths.length; i++) {
		widths[i] = measureColumnWidth( i );
	}
	setColumnWidths( widths );
}
/**
 * Horizontal scrollbar tracking. Horizontal and vertical scrollbar tracking
 * for the table data is handled in the DataArea class. This method makes sure
 * that the titlebar is in sync with the data area's horizontal scrollbar
 * position.
 * 
 * @param e The Admustment event to respond to.
 */
public void adjustmentValueChanged( AdjustmentEvent e ) {

	if (((Scrollbar)e.getSource()).getOrientation() == Scrollbar.HORIZONTAL) {

		titlebar.setScrollPos( - e.getValue() );
		titlebar.paint( titlebar.getGraphics() );
	}
}
/**
 * Check one row of the table (if the checkmarks column is showing). 
 * 
 * @param index The zero-based index of the row to check.
 */
public void check( int index ) {
	
	check( index, true );
}
/**
 * Check or uncheck a row of the table (if the checkmarks column is showing).
 * 
 * @param index The zero-based index of the row to check/uncheck.
 * @param state <code>true</code> if the row should be checked, <code>false</code>
 * otherwise.
 *	  	
 */
public void check( int index, boolean state ) {

	synchronized( rows ) {
		dataArea.check( index, state );	
	}
}
/**
 * Check all rows
 *	  	
 */
public void checkAll() {

	synchronized( rows ) {
		dataArea.checkAll();
	}
}
/**
 * Checks the rows with row indexes as specified in the 'indexes' array.
 * Any other rows are unchecked.
 * 
 * @param indexes The zero-based indexes of the rows to check.
 * @exception IllegalArgumentException If the 'indexes' array contains one
 * or more illegal row indexes.
 */
public void checkIndexes( int[] indexes ) throws IllegalArgumentException {

	synchronized( rows ) {
		dataArea.setCheckedIndexes( indexes );
	}
}
/**
 * Figure out which scrollbars are to be shown. This depends on the 
 * scrollbar mode as determined by the user (on, off or automatic), the 
 * preferred width of the table in relation to the available width and the
 * number of entries to be shown in relation to the available height.
 *
 * <p>Note that the special case has to be handeled where vertical scrolling
 * becomes necessary because of the additional space taken by a horizontal scrollbar
 * and where horizontal scrolling becomes necessary because of the space taken
 * by the vertical scrollbar.
 * 
 */
private void checkScrollbars() {

	// Determine minimum width and height of data area. getMinimumSize returns
	// the preferred width as its minimum width and the height 
	// of one line as its minimum height.
	Dimension dataDim = dataArea.getMinimumSize();

	int insideWidth = getSize().width - borderLeft - borderRight;
	int insideHeight = getSize().height - borderTop - borderBottom;

	//
	// Figure out if a horizontal scrollbar is needed.
	//

	if (horizontalScrollMode == HORIZONTAL_SCROLLBAR_OFF) {

		hasHorizontalScrollbar = false;

	} else if (horizontalScrollMode == HORIZONTAL_SCROLLBAR_ON) {

		hasHorizontalScrollbar = true;

	} else if (dataDim.width <= (insideWidth - verticalScrollbarWidth)) {

		hasHorizontalScrollbar = false;

	} else if (dataDim.width > insideWidth) {

		hasHorizontalScrollbar = true;

	} else if (verticalScrollMode == VERTICAL_SCROLLBAR_ON) {

		hasHorizontalScrollbar = true;

	} else if (verticalScrollMode == VERTICAL_SCROLLBAR_OFF) {

		hasHorizontalScrollbar = false;

	} else {

		if ((rows.size() * dataDim.height) > (insideHeight - titlebarSkip)) {
	
			// Vertical scrollber needed. This reduces the available width and
			// a horizontal scrollbar is also needed.
			hasHorizontalScrollbar = true;

		} else {
	
			hasHorizontalScrollbar = false;
		}
	}

	horizontalScrollbarSkip = hasHorizontalScrollbar ? horizontalScrollbarHeight : 0;

	//
	// Figure out if a vertical scrollbar is needed
	//
	if (verticalScrollMode == VERTICAL_SCROLLBAR_OFF) {

		hasVerticalScrollbar = false;

	} else if (verticalScrollMode == VERTICAL_SCROLLBAR_ON) {

		hasVerticalScrollbar = true;

	} else if ((rows.size() * dataDim.height) > 
					(insideHeight - titlebarSkip - horizontalScrollbarSkip)) {

		hasVerticalScrollbar = true;

	} else {

		hasVerticalScrollbar = false;

	}

	verticalScrollbarSkip = hasVerticalScrollbar ? verticalScrollbarWidth : 0;
}
/**
 * Determine the height of the titlebar.
 * 
 */
private void checkTitlebar() {

	// titlebar.getPreferredSize() has to be called regardless of whether
	// it is shown or not.
	//
	Dimension titlebarSize = titlebar.getPreferredSize();
	
	if (showTitlebar) {
		// Set titlebarSkip to height of titlebar.
		titlebarSkip = titlebarSize.height;
	} else {
		titlebarSkip = 0;
	}
}
/**
 * Deselect a row of the table.
 * 
 * @param index The zero-based index of the row to be deselected.
 */
public void deselect( int index ) {
	
	select( index, false );
}
/**
 * Deselect all rows and repaint as necessary.
 * 
 */
public void deselectAll() {

	synchronized( rows ) {
		dataArea.deselectAll( true );
	}
}
/**
 * Get the table's background color.
 *
 * @return The table's background color.		
 */
public Color getBackground() {

	Color c = super.getBackground();

	if (c == null) return defaultBackground;
	else 				return c;
}
/**
 * Get the instance of the Border class that's used to draw the table's border.
 *
 * @return The table's current Border instance.		
 */
public Border getBorder() {

	return border;
}
/**
 *
 * @return The indexes of the currently checked rows. Returns <code>null</code>
 * if the checkmarks column is not showing.
 */
public int[] getCheckedIndexes() {

	synchronized( rows ) {
		return dataArea.getCheckedIndexes();
	}
}
/**
 * Get the current column widths (in pixels).
 *
 * @return An <code>int</code> array containing the widths of the table's columns
 * in pixels.
 */
public int[] getColumnWidths() {

	return titlebar.getColumnWidths();
}
/**
 * The table's data area is managed by an instance of DataArea. Get its reference.
 *
 * @return The DataArea object that manages and displays the table's data.		
 */
public DataArea getDataArea() {

	return dataArea;
}
/**
 * Get the table's default Border -- the instance of the class that is used to
 * draw the border if no explicit Border object is set.
 * 	  	
 * @return The instance of the default Border object used to draw the table's 
 * border.		
 *
 * @see #getBorder()
 */
public static Border getDefaultBorder() {

	return defaultBorder;
}
/**
 * Get the table's foreground color.
 * 
 * @return The table's foreground color.
 */
public Color getForeground() {

	Color c = super.getForeground();

	if (c == null) return defaultForeground;
	else 				return c;
}
/**
 * Return one of the horizontal scrollbar-mode constants. 
 * One of the horizontal scrollbar-mode constants.  
 * 
 */
public int getHorizontalScrollMode() {

	return horizontalScrollMode;
}
/**
 * Calculate and return the table's current minimum size.
 *
 * @return The table's current minimum size.		
 */
public Dimension getMinimumSize() {
	
	Dimension title = titlebar.getMinimumSize();
	Dimension data = dataArea.getMinimumSize();

	int titleHeight = showTitlebar ? title.height : 0;

	return new Dimension( title.width + verticalScrollbarWidth + 
								 borderLeft + borderRight,
								 titleHeight + data.height + horizontalScrollbarHeight +
								 borderTop + borderBottom );
}
/**
 * Calculate and return the table's preferred size.
 * 
 * @return The table's preferred size.		
 */
public Dimension getPreferredSize() {
	
	Dimension title = titlebar.getPreferredSize();
	Dimension data = dataArea.getPreferredSize();

	int titleHeight = showTitlebar ? title.height : 0;

	return new Dimension( 
		title.width + borderLeft + borderRight + verticalScrollbarWidth,
		titleHeight + data.height + borderTop + borderBottom + horizontalScrollbarHeight );
}
/**
 * Get the table data.
 * 
 * @return The Vector that holds the table's data.		
 */
public Vector<Object> getRows() {

	return rows;
}
/**
 * @return The zero-based index of the one selected row. 
 * If none or multiple are
 * selected, return -1.
 * 
 */
public int getSelectedIndex() {

	synchronized( rows ) {
		return dataArea.getSelectedIndex();
	}
}
/**
 *
 * @return The indexes of the currently selected rows.
 */
public int[] getSelectedIndexes() {

	synchronized( rows ) {
		return dataArea.getSelectedIndexes();
	}
}
/**
 * @return The currently selected row if exactly one row is selected.  
 * <code>null</code> otherwise.
 * 
 */
public Object getSelectedObject() {

	synchronized( rows ) {
		int i = dataArea.getSelectedIndex();
		if (i != -1) {
			return rows.elementAt( i );
		} else {
			return null;
		}
	}
}
/**
 *
 * @return The currently selected rows as an array of TableRow's.
 */
public Object[] getSelectedObjects() {

	return dataArea.getSelectedObjects();
}
/**
 * Get the active selection mode.
 * 
 * @return int The table's selection mode (see the selection mode 
 * constants defined as part of DataArea's class definition).
 */
public int getSelectionMode() {

	return dataArea.getSelectionMode();
}
/**
 * Find out whether the table shows a reference column with checkmarks.
 * 
 * @return <code>true</code> if the table has a checkmarks column, <code>false</code>
 * otherwise.
 */
public boolean getShowCheckmarks() {

	return dataArea.getShowCheckmarks();
}
/**
 * Find out if grid lines are painted.
 * 
 * @return <code>true</code> if grid lines are painted, <code>false</code> otherwise.		
 */
public boolean getShowGrid() {

	return dataArea.getShowGrid();
}
/**
 * Find out whether the table contains a reference column showing line numbers.
 * (The line numbers number table rows starting with one.)
 *
 * @return <code>true</code> if the table has a line number column, <code>false</code>
 * otherwise.
 *		
 */
public boolean getShowLineNumbers() {

	return dataArea.getShowLineNumbers();
}
/**
 * Find out whether the titlebar is visible.
 * 
 * @return <code>true</code> if the table's titlebar is visible, 
 * <code>false</code> otherwise.		
 */
public boolean getShowTitlebar() {

	return showTitlebar;
}
/**
 * Get the instance of the table's Titlebar object. This method always returns
 * a valid Titlebar object regardless of whether the titlebar is visible or not. 
 *
 * @return The table's Titlebar object.		
 */
public Titlebar getTitlebar() {

	return titlebar;
}
/**
 * 
 * @return The current vertical scrollbar-mode constant. See the
 * scrollbar-mode constants defined as part of Table's class
 * definition.
 */
public int getVerticalScrollMode() {

	return verticalScrollMode;
}
/**
 * Initialize the table. Called by the constructor.
 * 
 */
private void initialize() {

	setLayout( null );
	setBorder( defaultBorder );

	if (horizontalScrollMode != HORIZONTAL_SCROLLBAR_OFF) {
		horizontalScrollbar = new Scrollbar( Scrollbar.HORIZONTAL );
	}

	if (verticalScrollMode != VERTICAL_SCROLLBAR_OFF) {
		verticalScrollbar = new Scrollbar( Scrollbar.VERTICAL );
	}

	dataArea = new DataArea( this, verticalScrollbar, horizontalScrollbar,
		columns, rows );
	
	// Don't register AdjustmentEvents directly with the scrollbars!
	// The data area modifies the adjustment events reveiced from the scrollbars
	// to speed up scrolling (intermediate adjustmentValueChanged events are skipped).
	// Only by registering with the data area, correct position synchronization with the
	// data area is assured.
	dataArea.addAdjustmentListener( this );

	titlebar = new Titlebar( columns );
	if (rows instanceof ColumnSortableVector) {
		titlebar.setCanSort( true );
		titlebar.addColumnSortListener( this );
		((ColumnSortableVector)rows).addSwapListener( dataArea );
	}

	titlebar.addPropertyChangeListener( this );
	titlebar.setReferenceColumnWidth( dataArea.getReferenceColumnWidth() );

	add( titlebar );
	add( dataArea );
	if (horizontalScrollbar != null) add( horizontalScrollbar );
	if (verticalScrollbar != null) add( verticalScrollbar );
}
/**
 * Find out the height of the horizontal scrollbar and store it in an instance 
 * variable.
 * 
 */
private void initScrollbarHeight() {

	if (horizontalScrollbarHeight == 0 && horizontalScrollbar != null) {

		horizontalScrollbarHeight = horizontalScrollbar.getPreferredSize().height;
	}
}
/**
 * Find out the width of the vertical scrollbar and store it in an instance variable.
 * 
 */
private void initScrollbarWidth() {

	if (verticalScrollbarWidth == 0 && verticalScrollbar != null) {

		verticalScrollbarWidth = verticalScrollbar.getPreferredSize().width;
	}
}
/**
 * Find out whether a given row is currently checked.
 * 
 * @param The zero-based index of the row.	
 * @return <code>true</code> if the row with the passed index is checked,
 * <code>false</code> otherwise.		
 */
public boolean isChecked( int index ) {

	synchronized( rows ) {
		return dataArea.isChecked( index );
	}
}
/**
 * Find out whether a given row is currently selected.
 * 
 * @param The zero-based index of the row.	
 * @return <code>true</code> if the row with the passed index is selected, 
 * <code>false</code> otherwise.		
 */
public boolean isSelected( int index ) {

	synchronized( rows ) {
		return dataArea.isSelected( index );
	}
}
/**
 * Calculate the optimal width of a column based on the column width adjustment
 * option stored in the column's ColumnDefinition.
 *
 * @return The width of the column in pixels.
 * @param columnIndex The zero-based index of the column to measure.
 */
private int measureColumnWidth( int columnIndex ) {

	ColumnDefinition cd = (ColumnDefinition) columns.elementAt( columnIndex );
	int width = 0;

	switch (cd.getWidthAdjustment()) {

		case ColumnDefinition.WIDTH_FIXED_N_PIXELS:
			width = titlebar.actualColumnWidth( cd.getPreferredWidth( null, null ));
			break;

		case ColumnDefinition.WIDTH_FIXED_N_CHARS:
			width = titlebar.actualColumnWidth( cd.getPreferredWidth(
				getFontMetrics( dataArea.getFont() ), null ));
			break;

		case ColumnDefinition.WIDTH_AUTO_BY_TITLE:
			width = titlebar.actualColumnWidth( cd.getPreferredWidth(
				getFontMetrics( titlebar.getFont() ),
				Titlebar.getDefaultFormatter()) );
			break;

		default:
			StringFormatter formatter = cd.getDataFormatter();
			if (formatter == null) formatter = DataArea.getDefaultFormatter();

			Font f = cd.getDataFont();
			if (f == null) f = dataArea.getFont();

			FontMetrics fontMetrics = getFontMetrics( f );

			// Calculate the preferred width of the column's data.
			synchronized( rows ) {
				for (int i=0; i<rows.size(); i++) {
					String s = ((TableRow)rows.elementAt( i )).getStrings()[columnIndex];
					formatter.measureString( fontMetrics, s, 0, 0, Short.MAX_VALUE,
						Short.MAX_VALUE );
					if (formatter.getPreferredWidth() > width) {
						width = formatter.getPreferredWidth();
					}
				}
			}
			
			if (showTitlebar) {
				// Measure the column title with respect to the width calculated above.

				formatter = cd.getTitleFormatter();
				if (formatter == null) formatter = Titlebar.getDefaultFormatter();

				f = cd.getTitleFont();
				if (f == null) f = titlebar.getFont();

				fontMetrics = getFontMetrics( f );

				formatter.measureString( fontMetrics, cd.getTitle(), 0, 0, width,
					Short.MAX_VALUE );
				int w = formatter.getPreferredWidth();
				if (w > width) {
					width = titlebar.actualColumnWidth( w );
				} else {
					width = titlebar.actualColumnWidth( width );
				}
			} else {
				width = dataArea.actualColumnWidth( width );
			}
			break;
	}

	return width;
}
/**
 * The table's paint method.
 * 
 * @param g The Graphics object used for drawing. 	
 */
public void paint( Graphics g ) {

	if (isDragging) return;

	// Once paint() is called, we're sure the Scrollbar's peers have been
	// properly initialized. Hence we can safely query their width/height.
	initScrollbarHeight();
	initScrollbarWidth();

	synchronized( rows ) {
		// Position the components 
		sizeComponents();

		if (! widthsInitialized) {
			adjustColumnWidths();
			widthsInitialized = true;
		}

		readjustHorizontalScrolling( false );
		readjustVerticalScrolling();
		
		// Draw everything...
		paintComponents();
	}
		
	// ...and the border.
	border.paint( g, 0, 0, w, h );
}
/**
 * Paint the table's titlebar (if shown) and the data area.
 * 
 */
private void paintComponents() {

	if (showTitlebar) {
		if (! isDragging) titlebar.paint( titlebar.getGraphics() );
	} else {
		titlebar.adjustColumns( dataArea.getSize().width );
	}
		
	dataArea.paint( dataArea.getGraphics() );

	if (hasVerticalScrollbar && hasHorizontalScrollbar) {
		
		// Erase the little rectangle at the bottom right of the table

		Graphics g = getGraphics();
		g.setColor( getBackground() );
		g.fillRect( w - borderRight - verticalScrollbarWidth,
						h - borderBottom - horizontalScrollbarHeight,
						verticalScrollbarWidth,
						horizontalScrollbarHeight );
	}
}
/**
 * The method implementing the PropertyListener interface. The method is called
 * by the table's titlebar to respond to changes in the titlebar's 
 * column widths and dimension.
 * 
 * @param e The PropertyChangeEvent to handle.
 */
public void propertyChange( PropertyChangeEvent e ) {
	
	if (e.getSource() == titlebar) {

		synchronized( rows ) {
			
			if (e.getPropertyName() == Titlebar.columnWidthsChangedEvent) {

				dataArea.setColumnWidths( titlebar.getColumnWidths() );
				sizeComponents();
				paintComponents();

			} else if (e.getPropertyName() == Titlebar.draggingStateChangedEvent) {

				if (((Integer)e.getNewValue()).intValue() >= 0) {
					isDragging = true;
				} else {
					readjustHorizontalScrolling( true );
					paintComponents();
					isDragging = false;
				}
			}
		}
	}
}
/**
 * This method is called when the user stops dragging a column. It checks
 * if a gap between the right most column and the right edge of the
 * titlebar has been created as a result of the drag operation.
 *
 * <p>If that's the case, the titlebar and the data area have to be scrolled
 * to close the gap -- either by scrolling to position 0 (if the titlebar's horizontal
 * size now fits completely), or by scrolling to the right most position (if
 * the horizontal size is still too wide to fit).
 *
 * <p>To see this method in action, create a table with a horizontal scrollbar.
 * Make sure the horizontal scrollbar is scrollable by increasing the width of
 * the titlebar as necessary (by dragging some column to the right).
 * Then scroll to the right most position of the scrollbar; the right edge of the
 * right most column is now visible and can be dragged. Drag it to the left,
 * creating a gab between the column and the right edge of the table (or beginning
 * of the vertical scrollbar). If you now release the mouse button, stopping
 * the dragging, this method is called and the horizontal scroll position is
 * adjusted to close the gab (or, if this is not possible, to scroll to the
 * left most position).
 * 
 * @param repaint <code>true</code> if the table's titlebar and data area are to
 * be redrawn, <code>false</code> otherwise.
 *
 */
private void readjustHorizontalScrolling( boolean repaint ) {

	if (titlebar.getScrollPos() != 0) {

		int actualWidth = titlebar.getSize().width;
		int preferredWidth = titlebar.getPreferredSize().width;

		if (titlebar.getScrollPos() + preferredWidth < actualWidth) {

			int newValue;
			
			if (preferredWidth <= actualWidth) 	newValue = 0;
			else 											newValue = actualWidth - preferredWidth;
			
			titlebar.setScrollPos( newValue );
			dataArea.setHorizontalScrollPos( newValue );
			if (horizontalScrollbar != null) horizontalScrollbar.setValue( -newValue );
			if (repaint) paintComponents();
				
		}
	}
}
/**
 * Adjust the vertical scroll position after a change of the size of the components.
 *
 * @see #readjustHorizontalScrolling(boolean)
 *
 */
private void readjustVerticalScrolling() {

	if (dataArea.getVerticalScrollPos() != 0) {

		int lineCount = rows.size();
		int visible = dataArea.getCompletelyVisibleCount();

		if (dataArea.getVerticalScrollPos() + lineCount < visible) {

			int newValue;
			
			if (lineCount <= visible) 	newValue = 0;
			else 								newValue = visible - lineCount;
			
			dataArea.setVerticalScrollPos( newValue );
			if (verticalScrollbar != null) verticalScrollbar.setValue( -newValue );	
		}
	}
}
/**
 * Remove an ActionEvent listener.
 * 
 * @param listener The listener to remove.
 *  	
 */
public void removeActionListener( ActionListener listener ) {

	dataArea.removeActionListener( listener );
}
/**
 * Remove all rows from the table.
 *
 */
public void removeAllRows() {

	synchronized( rows ) {

		int count = rows.size();
		
		rows.removeAllElements();
		rowsRemoved( 0, count );
	}
}
/**
 * Remove an ItemEvent listener.
 * 
 * @param listener The listener to remove.
 *  	
 */
public void removeItemListener( ItemListener listener ) {

	dataArea.removeItemListener( listener );
}
/**
 * Remove the rows specified by their inexes in the passed int array.
 *
 * @param indexes The indexes of the rows to remove.
 */
public void removeRowsByIndex( int[] indexes ) {

	synchronized( rows ) {

		for (int i=indexes.length-1; i>=0; i--) {

			rows.removeElementAt( indexes[i] );
			dataArea.rowsRemoved( indexes[i], indexes[i] + 1 );
		}

		dataArea.updateVerticalScrollbar();

		boolean repaint = true;
		
		if (verticalScrollMode == VERTICAL_SCROLLBAR_AUTOMATIC) {
			boolean oldHasVerticalScrollbar = hasVerticalScrollbar;
			checkScrollbars();
			if (oldHasVerticalScrollbar != hasVerticalScrollbar) {
				sizeComponents();
				repaint = false;
			}
		}

		if (repaint) {

			dataArea.paintVisibleRows( dataArea.getGraphics() );
		}
	}
}
/**
 * Remove the currently selected rows from the table.
 */
public void removeSelectedRows() {

	synchronized( rows ) {
		removeRowsByIndex( dataArea.getSelectedIndexes() );
	}
}
/**
 * Override repaint to minimize flicker during drag operations.
 *   	
 */
public void repaint( long l, int x, int y, int w, int h ) {

	// Disable super.repaint() during dragging of columns to avoid flicker.

	if (! isDragging) {

		super.repaint( l, x, y, w, h );
	}
}
/**
 * Notify the table of the addition of zero or more consecutive rows to the
 * end of the rows Vector.
 * This method updates the scrollbars and repaints the data area as necessary.
 *
 * @param count The number of rows that have been added.
 */
public void rowsAdded( int count ) {

	synchronized( rows ) {
		rowsInserted( rows.size() - count, count );
	}
}
/**
 * Notify the table of the insertion of zero or more consecutive rows.
 * This method updates the scrollbars and repaints the data area as necessary.
 *
 * @param startEntry The index of the first row that was inserted. 
 * @param count The number of rows that were inserted. 
 */
public void rowsInserted( int startEntry, int count ) {

	synchronized (rows) {

		boolean repaint = true;
		int pastEntry = startEntry + count;
		
		dataArea.rowsInserted( startEntry, pastEntry );
			
		dataArea.updateVerticalScrollbar();
		
		if (verticalScrollMode == VERTICAL_SCROLLBAR_AUTOMATIC) {
			boolean oldHasVerticalScrollbar = hasVerticalScrollbar;
			checkScrollbars();
			if (oldHasVerticalScrollbar != hasVerticalScrollbar) {
				sizeComponents();
				repaint = false;
			}
		}

		if (repaint && startEntry >= -dataArea.getVerticalScrollPos() &&
			startEntry <= -dataArea.getVerticalScrollPos() + dataArea.getVisibleCount()) {
			dataArea.paintVisibleRows( dataArea.getGraphics() );
		}
	}
}
/**
 * Notify the table of the removal of zero or more consecutive rows from the
 * end of the rows Vector.
 * This method updates the scrollbars and repaints the data area as necessary.
 *
 * @param count The number of rows that have been removed.
 */
public void rowsRemoved( int count ) {

	synchronized( rows ) {
		rowsRemoved( rows.size(), count );
	}
}
/**
 * Notify the table of the removal of zero or more consecutive rows.
 * This method updates the scrollbars and repaints the data area as necessary.
 *
 * @param startEntry The index of the first row that was removed. 
 * @param count The number of rows that were removed.
 */
public void rowsRemoved( int startEntry, int count ) {

	synchronized (rows) {

		int pastEntry = startEntry + count;
		boolean repaint = true;

		dataArea.rowsRemoved( startEntry, pastEntry );
		
		dataArea.updateVerticalScrollbar();
		
		if (verticalScrollMode == VERTICAL_SCROLLBAR_AUTOMATIC) {
			boolean oldHasVerticalScrollbar = hasVerticalScrollbar;
			checkScrollbars();
			if (oldHasVerticalScrollbar != hasVerticalScrollbar) {
				sizeComponents();
				repaint = false;
			}
		}

		if (repaint && (pastEntry > - dataArea.getVerticalScrollPos()) &&
			 (startEntry <= (-dataArea.getVerticalScrollPos() + dataArea.getVisibleCount()))) {

			// System.out.println( "repaint" );
			dataArea.paintVisibleRows( dataArea.getGraphics() );
		}
	}
}
/**
 * Notify the table of an update of zero or more consecutive rows.
 * This method repaints the data area if the
 * updated rows are visible.
 *
 * @param startEntry The index of the first row that was updated. 
 * @param count The number of rows that were updated. 
 */
public void rowsUpdated( int startRow, int count ) {

	synchronized (rows) {

		int pastRow = startRow + count;
		if (! (pastRow <= - dataArea.getVerticalScrollPos() ||
			 startRow > (-dataArea.getVerticalScrollPos() + dataArea.getVisibleCount()))) {

			dataArea.paintVisibleRows( dataArea.getGraphics() );
		}
	}
}
/**
 * Scrolls the data area to a particular row. After scrolling, the specified
 * row will appear as the top-most visible row.
 *
 * @param row The zero-based index of the row to scroll to.
 */
public void scrollToRow( int row ) {

	dataArea.scrollToRow( row );
}
/**
 * Select one row of the table. 
 * 
 * @param index The zero-based index of the row to select.
 */
public void select( int index ) {

	select( index, true );
}
/**
 * Select or deselect a row of the table.
 * 
 * @param index The zero-based index of the row to select/deselect.
 * @param state <code>true</code> if the row should be selected, <code>false</code>
 * otherwise.
 *	  	
 */
public void select( int index, boolean state ) throws IllegalArgumentException {

	synchronized( rows ) {

		int mode = dataArea.getSelectionMode();

		if (state) {

			int curSelection = dataArea.getSelectedIndex();
			
			if (mode == DataArea.SELECT_ONE ||
				mode == DataArea.SELECT_ZERO_OR_ONE) {
				
				if (curSelection != index) {

					if (curSelection >= 0) dataArea.select( curSelection, false );
					dataArea.moveFocus( index );
					dataArea.select( index, state );

					dataArea.fireItemEvent(new ItemEvent(
						this, 
						ItemEvent.ITEM_STATE_CHANGED, 
						rows.elementAt(index), 
						ItemEvent.SELECTED ));
				}
			} else {
				dataArea.select( index, state );	
			}
		} else {
			if (mode != DataArea.SELECT_ONE) {
				dataArea.select( index, state );	

				if (mode == DataArea.SELECT_ZERO_OR_ONE) {
					
					dataArea.fireItemEvent(new ItemEvent(
						this, 
						ItemEvent.ITEM_STATE_CHANGED, 
						rows.elementAt(index), 
						ItemEvent.DESELECTED ));
				}
			}
		}
	}
}
/**
 * Select all rows
 *	  	
 */
public void selectAll() {

	synchronized( rows ) {
		dataArea.selectAll();	
	}
}
/**
 * Selects the rows with row indexes as specified in the 'indexes' array.
 * Any other rows are deselected.
 * 
 * @param indexes The zero-based indexes of the rows to select.
 * @exception IllegalArgumentException If the 'indexes' array contains one
 * or more illegal row indexes.
 */
public void selectIndexes( int[] indexes ) throws IllegalArgumentException {

	synchronized( rows ) {
		dataArea.setSelectedIndexes( indexes );
	}
}
/**
 * Set the Border object used to draw the table's border. If no explicit Border
 * object is set, the table draws its border using the static default border object.
 * <p>In order to draw custom borders, derive a class from Border that supplies
 * its own paint method. Then set the table's Border class to your class using this
 * method.
 * 
 * 
 * @param border The Border object used to draw the table border.	  	
 * @see #setDefaultBorder(Border)
 */
public void setBorder( Border border ) {

	this.border = border;

	Insets insets = border.getInsets();
	
	borderTop = insets.top;
	borderLeft = insets.left;
	borderRight = insets.right;
	borderBottom = insets.bottom;
}
/**
 * Checks the rows with row indexes as specified in the 'indexes' array.
 * Any other rows are unchecked.
 * 
 * @param indexes The zero-based indexes of the rows to check.
 * @exception IllegalArgumentException If the 'indexes' array contains one
 * or more illegal row indexes.
 */
public void setCheckedIndexes( int[] indexes ) throws IllegalArgumentException {

	synchronized( rows ) {
		dataArea.setCheckedIndexes( indexes );
	}
}
/**
 * Set new column widths for the titlebar and the data area and repaint the table.
 *
 * @param widths The array of <code>int</code>s containing the new widths of the
 * table's columns (in pixels).
 */
public void setColumnWidths( int[] widths ) {

	titlebar.setColumnWidths( widths );
	dataArea.setColumnWidths( widths );
	repaint();
}
/**
 * Set the Table's default border.
 * 
 * @param border The Border object used to draw the table's default border.
 * 
 * @see #setBorder(Border)
 */
public static void setDefaultBorder( Border border ) {

	defaultBorder = border;
}
/**
 * Enable or disable the table.
 *
 * @param enable boolean
 */
public void setEnabled( boolean enable ) {

	super.setEnabled( enable );
	
	if (horizontalScrollbar != null) horizontalScrollbar.setEnabled( enable );
	if (verticalScrollbar != null) verticalScrollbar.setEnabled( enable );
	if (titlebar != null) titlebar.setEnabled( enable );
	if (dataArea != null) dataArea.setEnabled( enable );
}
/**
 * Set the horizontal scroll mode to one of the predefined constants.
 * 
 * @param scrollMode The scroll-mode constant (one of the scroll-mode constants
 * defined as part of Table's class definition).
 */
public void setHorizontalScrollMode( int scrollMode ) {

	horizontalScrollMode = scrollMode;
}
/**
 * Set the table data.
 * 
 * @param data A Vector of objects implementing the TableRow interface.
 */
public void setRows( Vector<Object> data ) {

	synchronized( rows ) {

		Vector<Object> oldRows = rows;
		rows = data;
		dataArea.setRows( data );

		if (oldRows.size() < rows.size()) {
			rowsAdded( rows.size() - oldRows.size() );
		} else if (oldRows.size() > rows.size()) {
			rowsRemoved( oldRows.size() - rows.size() );
		} else {
			repaint();
		}
	}
}
/**
 * Set the table's active selection mode. Resets any selections. If the
 * selection mode equals SELECT_ONE and there area rows in the data Vector,
 * the method selects the first row.
 *
 * <p>Any selection mode constraints are only enforced on the GUI level and
 * not on the API level. Selecting, for example, two rows through a call to
 * setSelectedIndexes() with a current selection mode of SELECT_ONE
 * is possible but not consistent with the SELECT_ONE mode.
 * 
 * @param mode The selection mode (one of the four selection mode constants 
 * defined as part of DataArea's class declaration).
 */
public void setSelectionMode( int mode ) {

	synchronized( rows ) {
		dataArea.setSelectionMode( mode );
	}
}
/**
 * Enable/disable per-row checkmarks, displayed in a non-scrolling reference column.
 * 
 * @param b <code>true</code> to turn checkmarks on, <code>false</code> otherwise.
 * 
 */
public void setShowCheckmarks( boolean b ) {

	dataArea.setShowCheckmarks( b );
	titlebar.setReferenceColumnWidth( dataArea.getReferenceColumnWidth() );

}
/**
 * Turn grid lines on or off. Does not repaint the table.
 * 
 * @param b <code>true</code> to show grid lines, <code>false</code> otherwise.
 */
public void setShowGrid( boolean b ) {

	dataArea.setShowGrid( b );
}
/**
 * Turn line numbers on or off. The line numbers number the rows of the table starting
 * with 1. The numbers are displayed in a (horizontally) non-scrolling reference
 * column at the left hand side of the data area.
 * 
 * @param b <code>true</code> to show line numbers, <code>false</code> otherwise.
 */
public void setShowLineNumbers( boolean b ) {

	dataArea.setShowLineNumbers( b );
	titlebar.setReferenceColumnWidth( dataArea.getReferenceColumnWidth() );
}
/**
 * Show/hide the titlebar. 
 * 
 * @param b <code>true</code> if the titlebar is to be shown, <code>false</code> 
 * otherwise.
 */
public void setShowTitlebar( boolean b ) {

	if (showTitlebar != b) {

		showTitlebar = b;
		titlebar.setVisible( b );

		if (isVisible()) repaint();
	}
}
/**
 * 
 * @param column int
 * @param ascending boolean
 */
public void setSorting( int column, boolean ascending ) {

	titlebar.setSortColumn( column );
	titlebar.setSortAscending( ascending );
}
/**
 * Set the table's row data and column definition vectors. This method
 * is provided to allow the setting of the table's column headers and
 * data after using the Table's default constructor (e.g. if the
 * table was added by a visual bean composition editor).
 * Use setRows( Vector )
 * to set the table's row data.
 *
 * @param colums A Vector of ColumnDefinition objects.
 * @param data A Vector of objects implementing the TableRow interface.
 */
public void setTableData( Vector<ColumnDefinition> columns, Vector<Object> rows ) {

	titlebar.setColumns( columns, null );
	this.columns = columns;
	this.rows = rows;

	initialize();
}
/**
 * Set the vertical scroll-mode to one of the predefined constants.
 * 
 * @param scrollMode One of the vertical scroll-mode constants defined as part of
 * the Table' class definition.
 */
public void setVerticalScrollMode( int scrollMode ) {

	verticalScrollMode = scrollMode;
}
/**
 * Size and place the Table's components within the Table's display rectangle.
 * (The titlebar, the data area and,
 * if necessary the horizontal and vertical scrollbars).
 * 
 */
private void sizeComponents() {

	w = getSize().width;
	h = getSize().height;

	checkTitlebar();
	checkScrollbars();

	if (showTitlebar) {

		titlebar.setVisible( true );
		titlebar.setBounds(  borderLeft, 
									borderTop, 
									w - borderLeft - borderRight - verticalScrollbarSkip,
									titlebarSkip );
	} else {

		titlebar.setVisible( false );
	}

	if (horizontalScrollbar != null) {
		horizontalScrollbar.setBounds( borderLeft,
												 h - borderBottom - horizontalScrollbarHeight,
												 w - borderLeft - 
												    borderRight - verticalScrollbarSkip,
												 horizontalScrollbarHeight );
	}
	
	if (hasHorizontalScrollbar) 	horizontalScrollbar.setVisible( true );
	else 									horizontalScrollbar.setVisible( false );


	if (verticalScrollbar != null) {
		verticalScrollbar.setBounds( w - borderRight - verticalScrollbarWidth,
										  	  borderTop,
										     verticalScrollbarWidth,
										     h - borderTop - borderBottom - 
										     horizontalScrollbarSkip );
	}
	
	if (hasVerticalScrollbar) {

		dataArea.setBounds( 	borderLeft, borderTop + titlebarSkip,
			w - borderLeft - borderRight - verticalScrollbarSkip,
			h - borderTop - borderBottom - horizontalScrollbarSkip - titlebarSkip );

		verticalScrollbar.setVisible( true );

	} else {

		verticalScrollbar.setVisible( false );

		dataArea.setBounds( 	borderLeft, borderTop + titlebarSkip,
			w - borderLeft - borderRight - verticalScrollbarSkip,
			h - borderTop - borderBottom - horizontalScrollbarSkip - titlebarSkip );
	}
}
/**
 * Sort a column of the table in response to a ColumnSortEvent received
 * from the titlebar.
 *
 * @param e tstuder.java.lib.component.table.ColumnSortEvent
 */
public void sortColumn( ColumnSortEvent e ) {

	if (rows.size() > 1) {

		ColumnSortableVector v = (ColumnSortableVector) rows;

		v.setAscending( e.getAscending() );

		synchronized( v ) {
			v.sort( e.getColumnIndex(), (ColumnSortable) v.elementAt( 0 ) );
		}

		dataArea.repaint();
	}
}
/**
 * Uncheck a row's checkmark.
 * 
 * @param index The zero-based index of the row to be unchecked.
 */
public void uncheck( int index ) {
	
	check( index, false );
}
/**
 * Uncheck all rows. Repaints any changes immediately.
 * 
 */
public void uncheckAll() {

	synchronized( rows ) {
		dataArea.uncheckAll( true );
	}
}
}
