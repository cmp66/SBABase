package tstuder.java.lib.component.table;

/*
 * This class implements a general purpuse titlebar Java bean. The titlebar supports
 * a number of column titles arranged on a horizontal bar. The individual headers
 * support custom borders, fonts, foreground and background colors and string 
 * formatting (including line breaks). 
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
import java.awt.event.*;
import java.util.Vector;
import tstuder.java.lib.graphics.*;
import java.beans.*;

/**
 * This class implements a general purpuse titlebar Java bean. The titlebar supports
 * a number of column titles arranged on a horizontal bar. The individual headers
 * support custom borders, fonts, foreground and background colors and string 
 * formatting (including line breaks). 
 *
 * <p>The titlebar also supports horizontal scrolling and column width adjustments
 * by means of mouse dragging of the titlebar's column divider lines.
 *
 * <p>The class extends the java.awt.Component class and can thus be easily added
 * to component containers.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * &nbsp; ts, 1999-09-24, v1.0.1,  Implemented setEnabled(boolean) support.
 * &nbsp; ts, 1999-11-18, v1.0.2,  - Added setStretchToFit( boolean ) and support for
 * &nbsp;                          non-resizeable columns. 
 * &nbsp;                          @see setColumnResizeableFlags(boolean[])
 * &nbsp; ts, 1999-12-04, v1.0.3,  - Added support for column-wise sorting.
 * &nbsp;                          @see setColumnSortableFlags(boolean[])
 * &nbsp; ts, 2000-05-22, v1.1,    - Renamed setStretchToFit to setAdjustToFit and improved it
 * &nbsp;                          to also support automatic shrinking of columns such that they
 * &nbsp;                          fit into the available space.
 * &nbsp; ts, 2000-05-25, v1.1,    - Fixed a bug in setColumns(..) (the columns weren't really set).
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class Titlebar extends Component implements MouseListener, 
	MouseMotionListener {

    private static final long serialVersionUID = 1L;

    /** The titlebar can notify property change listeners of changes of the
	 *  titlebar's preferred width and/or height and its column heights. 
	 *  <p>This is the string passed in the property change event to notify any
	*  listeners of the fact that the titlebar's preferred dimension changed.
	 */
	public static final String		dimensionChangedEvent 		= "DimensionChanged";

	/** The titlebar can notify property change listeners of changes of the
	 *  titlebar's preferred width and/or height and its column heights. 
	 *  <p>This is the string passed in the property change event to notify any
	*  listeners of the fact that one ore more of the titlebar's columns changed
	*  in width.
	 */
	public static final String		columnWidthsChangedEvent	= "ColumnWidthsChanged";

	/** This property change event is posted when dragging of a colum starts and when it
	 *  stops. This allows, for example, the parent of the component to avoid
	 *  unnecessary repaints during dragging.
	*/
	public static final String		draggingStateChangedEvent	= "draggingStateChanged";

	/** NOT_DEFINED can be used for column widths (field 'widths') and
	 *  and the overall bar width (field 'barWidth' if
	 *  these values should be calculated dynamically.
	 */
	public static  final int		NOT_DEFINED = -1;

	/** Left column heading padding */
	public static final int 		LEFT_MARGIN 	= 4;
	/** Right column heading padding */
	public static final int			RIGHT_MARGIN 	= 4;
	/** Top column heading padding */
	public static final int			TOP_MARGIN 		= 1;
	/** Bottom column heading padding */
	public static final int			BOTTOM_MARGIN 	= 1;

	public static final int       DRAG_CURSOR_RANGE = 5;

	private static 		Border	border; // The Border shared by all titlebar objects.
	
	private static 		int		borderTop;		
	private static 		int		borderLeft;
	private static 		int 		borderRight;
	private static 		int		borderBottom;

	static {
		setBorder( new BorderRaisedWithLine() );
	};

	private Vector<ColumnDefinition>columns						= null;
	private int[]					columnWidths				= null;
	private boolean[]				columnResizeableFlags 	= null;
	private boolean[]				columnAdjustableFlags 	= null;
	private boolean[]				columnSortableFlags 		= null;
	private int                sortColumn              = -1;
	private boolean            sortAscending           = true;
	private boolean            canSort                 = false;
	private boolean				adjustToFit					= true;
	private int						scrollPos					= 0;
	private boolean				doubleBuffer				= true;
	private Image					offScreen					= null;
	private int         			minimumColumnWidth      = 5;
	private int                adjustmentIndex         = 0;

	// The width the bar should have as a component. If it is not defined,
	// the value is calculated dynamically based on the 
	// title strings and their formatting.
	//
	private int							 barWidth					= NOT_DEFINED;

	// Various static fields used across all Titlebar instances.
   //
	private static Color				 defaultBackground		= Color.lightGray;
	private static Color				 defaultForeground		= Color.black;
	private static Font				 defaultFont				= 
												 new Font( "SansSerif", Font.PLAIN, 10 );

	private static StringFormatter defaultFormatter		= 
												 new StringFormatter();

	private static SortIcon			 sortIcon;
	
	private int							 referenceColumnWidth;

	// Temporaries used during paint();
	private int							 w;
	private int							 h;

	//
	// Temporary variables used during dragging of columns
	//

	// Mouse x-coordinate of previous drag event.
	//
	private int							 lastDragX;
	private int							 savePreferredWidth;
	private int							 savePreferredHeight;
	
	// A valid column index while dragging takes place, -1 otherwise.
	//
	private int							 dragIndex				= -1;

	private int                    mouseOverIndex      = -1;

	private boolean                mouseOverSortFeedback = true;

	// A utility class used when dragging columns.
	// 
	private DragConstraints 		 dragConstraints;

	private Cursor						 savedCursor;

	// Dragging a column increases or decreases the column's width and may cause
	// wrapping/unwrapping of the column title which, in turn, changes the number 
	// of lines to print that title. This generally has an impact on the preferred
	// height of the titlebar. The bean implements PropertyChange support to
	// notify any listeners of such a change.
	//
	private PropertyChangeSupport propertyChange = new PropertyChangeSupport(this);

	private ColumnSortListener    columnSortListener = null;
/**
 * Construct a Titlebar.
 */
public Titlebar() {

	initialize();
}
/**
 * Construct a Titlebar, passing a Vector of ColumnDefinition objects.
 * 
 * @param columns A Vector of ColumnDefinition objects defining the Titlebar's titles.  */
public Titlebar( Vector<ColumnDefinition> columns ) {

	this.columns = columns;
	this.columnAdjustableFlags = new boolean[columns.size()];

	initialize();
}
/**
 * Calculate the actual width of a column given the desired net width
 * for the column title.
 *
 * @param netWidth The width in pixels available for the column title string.
 * @return The actual column width in pixels (adding margins and the width
 * of the border).
 */
int actualColumnWidth( int netWidth ) {

	return netWidth + LEFT_MARGIN + RIGHT_MARGIN + borderLeft + borderRight;
}
/**
 * Add a ColumnSortListener. Important: The current implementation only
 * supports one listener at a time.
 *
 * @param listener The listener to receive events of type ColumnSortEvent
 *
 */
public void addColumnSortListener( ColumnSortListener listener ) {

	columnSortListener = listener;
}
/**
 * Add a property change listener if you want to be notified of changes of 
 * the titlebar's preferred width/height and/or the widths of its headers.
 * 
 * @param listener The listener to receive property change events.
 *  	
 */
public void addPropertyChangeListener( PropertyChangeListener listener ) {

	propertyChange.addPropertyChangeListener( listener );
}
/**
 * Try to fill the availabe space with the available columns by growing or shirinking
 * individual columns as necessary.
 */
void adjustColumns( int w ) {

	if (! adjustToFit) return;

	int startColumn = dragIndex + 1;
		
	int availableWidth = w - getReferenceColumnWidth();
	int preferredWidth = 0;
	int adjustableColumnCount = columnWidths.length - startColumn;
	
	for (int i=0; i<startColumn; i++) {
		availableWidth -= columnWidths[i];
		columnAdjustableFlags[i] = false;
	}
	for (int i=startColumn; i<columnWidths.length; i++) {
		preferredWidth += columnWidths[i];
		if (columnResizeableFlags != null && columnResizeableFlags[i] == false) {
			columnAdjustableFlags[i] = false;
			adjustableColumnCount--;
		} else {
			columnAdjustableFlags[i] = true;
		}
	}
	
	if (adjustableColumnCount == 0) return;

	if (preferredWidth < availableWidth) {

		// Stretch all resizeable columns evenly such that all available horizontal
		// space is used

		int stretchAmount = (availableWidth - preferredWidth) / adjustableColumnCount;
		int remainder = (availableWidth - preferredWidth) % adjustableColumnCount;
	
		for (int i=startColumn; i<columnWidths.length; i++) {

			if (columnAdjustableFlags[i] == true) {
				columnWidths[i] += stretchAmount;
				if (remainder > 0) {
					columnWidths[i] += remainder;
					remainder = 0;
				}
			}
		}
		
	} else if (preferredWidth > availableWidth) {

		// Reduce all resizeable columns evenly such that all columns fit into
		// the available space.

		int widthToReduce = preferredWidth - availableWidth;
		int n = columnWidths.length - startColumn;
		int shrinkAmount = widthToReduce / adjustableColumnCount;
		int remainder = widthToReduce % adjustableColumnCount;

		while (widthToReduce > 0 && adjustableColumnCount > 0) {

			int currentCol = (adjustmentIndex % n) + startColumn;
				
			if (columnAdjustableFlags[currentCol] == true) {

				// current column can be adjusted, i.e. is resizable and has not reached its
				// minimum width yet --> try to schrink the column as necessary

				if (columnWidths[currentCol] - remainder >= minimumColumnWidth) {
					widthToReduce -= remainder;
					columnWidths[currentCol] -= remainder;
					remainder = 0;
					if (columnWidths[currentCol] - shrinkAmount >= minimumColumnWidth) {
						widthToReduce -= shrinkAmount;
						columnWidths[currentCol] -= shrinkAmount;
					} else {
						widthToReduce -= columnWidths[currentCol] - minimumColumnWidth;
						remainder += shrinkAmount - (columnWidths[currentCol] - minimumColumnWidth);
						columnWidths[currentCol] = minimumColumnWidth;
					}
				} else {
					widthToReduce -= columnWidths[currentCol] - minimumColumnWidth;
					remainder -= (columnWidths[currentCol] - minimumColumnWidth);
					columnWidths[currentCol] = minimumColumnWidth;
				}

				if (columnWidths[currentCol] == minimumColumnWidth) {
					adjustableColumnCount--;
					columnAdjustableFlags[currentCol] = false;
				}
			}

			adjustmentIndex++;
		}
	}
}
/**
 * Drag column 'col' by 'dragX' pixels.
 * 
 * @param col The zero-based index of the column to be dragged.
 * @param dragX The number of pixels to drag the specified column. 
 */
private void dragColumn( int col, int dragX ) {

	columnWidths[col] += dragX;

	paint( getGraphics() );

	propertyChange.firePropertyChange( columnWidthsChangedEvent, null, null );
}
/**
 * Erase the area between the right most column title and the right edge of the
 * component by drawing an empty title cell with no border.
 * 
 * @param g The graphics context on which to paint.
 * @param x The left x coordinate of the point where painting starts.
 */
private void eraseRight( Graphics g, int x ) {

	g.setColor( getBackground() );
	g.fillRect( x, 0, w-x, h );
}
/**
 * Make sure the allocated off-screen buffer gets disposed of properly.
 * 
 * 
 */
public void finalize() throws Throwable {

	flush();
	super.finalize();
}
/**
 * Given mouse coordinate x, checks whether the mouse is at a location where
 * column dragging can be started. 
 *
 * @param x Current mouse X.
 * @return The zero based index of the column that will be dragged or -1 if the
 * mouse is not positioned to drag any column or positioned on a non-dragable
 * column.
 */
private int findDragIndex( int x ) {

	int lastIndex = -1;

	int localDragXPos = scrollPos + referenceColumnWidth;
	
	for (int i=0; i<columnWidths.length; i++) {

		localDragXPos += columnWidths[i];

		if (columnResizeableFlags == null ||
			 columnResizeableFlags[i] == true) {
			if (x > localDragXPos - DRAG_CURSOR_RANGE && x < localDragXPos + DRAG_CURSOR_RANGE) {
				lastIndex = i;
			}
		}
	}

	return lastIndex;
}
/**
 * Given horizontal mouse coordinate x, checks whether the mouse is at a location where
 * column sorting can be turned on or off. 
 *
 * @param x Current mouse X.
 * @return The zero based index of the sortable column the mouse is over or -1.
 */
private int findSortIndex( int x ) {

	int pos = scrollPos + referenceColumnWidth;
	
	for (int i=0; i<columnWidths.length; i++) {

		if (x > pos + DRAG_CURSOR_RANGE && 
			 x < pos + columnWidths[i] - DRAG_CURSOR_RANGE) {

			if (columnSortableFlags == null || columnSortableFlags[i]) {
				return i;
			} else {
				return -1;
			}
		}
		pos += columnWidths[i];
	}

	return -1;
}
/**
 * Flush the off-screen graphics buffer.
 * 
 */
public void flush() {

	if (offScreen != null) {
		offScreen.flush();
		offScreen = null;
	}
}
/**
 * Get the <code>adjustToFit</code> property.
 * @see #setAdjustToFit(boolean)
 *
 * @return boolean
 */
public boolean getAdjustToFit() {
	
	return adjustToFit;
}
/**
 * Get the background color property.
 *
 * @return The component's background color.		
 */
public Color getBackground() {

	Color c = super.getBackground();

	if (c == null) return defaultBackground;
	else 				return c;
}
/**
 * The width of the titlebar can be set to a specific width. This method returns
 * the current width setting or NOT_DEFINED if the width is being calculated based
 * on the titlebar's column headings.
 * 
 * @return The titlebar's explicitly set width in pixels or NOT_DEFINED.
 */
public int getBarWidth() {

	return barWidth;
}
/**
 *
 * @return <code>true</code> if the Titlebar allows sorting by column, 
 * <code>false</code> otherwise.
 */
public boolean getCanSort() {

	return canSort;
}
/**
 * Get the <code>columnResizeableFlags</code> array.
 *
 * @see #setColumnResizeableFlags(boolean[])
 * 
 */
public boolean[] getColumnResizeableFlags() {

	return columnResizeableFlags;
}
/**
 * Get the <code>columnSortableFlags</code> array.
 *
 * @see #setColumnSortableFlags
 * 
 */
public boolean[] getColumnSortableFlags() {

	return columnSortableFlags;
}
/**
 * Get the current width of the titlebar's columns.
 * 
 * @return The widths in pixels of the titlebar's columns.		
 */
public int[] getColumnWidths() {

	return columnWidths;
}
/**
 * Get the Titlebar class' default background color.
 * 
 * @return The default background color.		
 */
public static Color getDefaultBackground() {
	
	return defaultBackground;
}
/**
 * Get the Titlebar class' default font. This font is used if super.getFont()
 * returns null.
 *
 * @return The default font used to draw the column headings.
 */
public static Font getDefaultFont() {

	return defaultFont;
}
/**
 * Get the Titlebar class' default foreground color.
 * 
 * @return The default foreground color.		
 */
public static Color getDefaultForeground() {
	
	return defaultForeground;
}
/**
 * Get the Titlebar class' default StringFormatter object.
 * 
 * @return The default StringFormatter.		
 */
public static StringFormatter getDefaultFormatter() {

	return defaultFormatter;
}
/**
 * Get the current state of the 'doubleBuffer' property. Double buffering of the
 * titlebar's painting methods can be turned on or off at any time.
 * 
 * @return <code>true</code> if double buffering is enabled, <code>false</code>
 * otherwise.
 */
public boolean getDoubleBuffer() {

	return doubleBuffer;
}
/**
 * Return the current font for this component.
 * <p>If there is no current font setting for this component or the
 * components in the component's hierarchy, initialize to the class' default font.
 *
 * @return The titlebar's current font. 
 */
public Font getFont() {

	Font f = super.getFont();

	if (f == null) return defaultFont;
	else 				return f;
}
/**
 * Get the titlebar's foreground color or, if it is null, the class' default
 * foreground color.
 * 
 * @return The titlebar's foreground color.		
 */
public Color getForeground() {

	Color c = super.getForeground();

	if (c == null) return defaultForeground;
	else 				return c;
}
/**
 * 
 * 
 * @return int
 */
public int getMinimumColumnWidth() {

	return minimumColumnWidth;
}
/**
 * Calculate and return the titlebar's minimum size.
 * 
 * @return The titlebar's minimum size.	
 */
public Dimension getMinimumSize() {

	int width, height;

	if (barWidth == NOT_DEFINED) {
		width = LEFT_MARGIN + RIGHT_MARGIN + borderLeft + 
				  borderRight + referenceColumnWidth;
	} else {
		width = barWidth;
	}

	height = TOP_MARGIN + BOTTOM_MARGIN + borderTop + borderBottom;
	
	return new Dimension( width, height );
}
/**
 * If the 'mouseOverSortFeedback' property is <code>true</code>, the sort
 * icon is painted in the titlebar as the mouse passes over the column headings
 * of sortable columns to indicate to the user whether sorting will be done 
 * (and in which direction) by clicking the mouse.
 *
 * @return boolean
 */
public boolean getMouseOverSortFeedback() {
	
	return mouseOverSortFeedback;
}
/**
 * Calculate and return the titlebar's preferred size.
 * 	
 * @return The titlebar's preferred width and height.
 */
public Dimension getPreferredSize() {

	if (columns == null || columns.size() == 0) {
		return getMinimumSize();
	} else {

		int preferredHeight = 0;
		int preferredWidth = 0;

		// Init the widths of the columns to their preferred widths.
		if (columnWidths == null) setDefaultWidths();		
		
		// calculate the total width of the titlebar by adding up the column widths
		for (int i=0; i<columnWidths.length; i++) {

			preferredWidth += columnWidths[i];
		}

		// calcualte the height of the highest column heading
		for (int i=0; i<columns.size(); i++) {
			int temp = ((ColumnDefinition)columns.elementAt( i )).
				getPreferredTitleHeight( getFontMetrics( getFont() ), defaultFormatter,
					columnWidths[i] - borderLeft - borderRight - 
					LEFT_MARGIN - RIGHT_MARGIN );
			if (temp > preferredHeight) preferredHeight = temp;
		}

		preferredHeight += borderTop + borderBottom + TOP_MARGIN + BOTTOM_MARGIN;
				
		if (barWidth == NOT_DEFINED) {
			return new Dimension( preferredWidth + referenceColumnWidth, preferredHeight );
		} else {
			// barWidth, if defined, overrules preferredWidth.
			return new Dimension( barWidth, preferredHeight );
		}
	}
}
/**
 * Get the width of the reference column. The reference column is the non-scrolling,
 * untitled column to the left of the titlebar. It can be used to display line
 * numbers and the like by components positioned just below the titlebar.
 * 
 * @return The width in pixels of the titlebar's reference column.
 */
public int getReferenceColumnWidth() {

	return referenceColumnWidth;
}
/**
 * Get the titlebar's current scroll position. A value of 0 means the titlebar is
 * at its left most scroll position. A negative value means the titlebar is currently
 * scrolled to the right.
 * <p>The scroll position is the pixel value relative to the component's left origin 
 * (generally 0) where painting starts.
 *  	
 * @return The current scroll position of the titlebar.
 */
public int getScrollPos() {

	return scrollPos;
}
/**
 *
 * @return <code>true</code> if the current sort column is being sorted in
 * ascending order. <code>false</code> otherwise.
 */
public boolean getSortAscending() {

	return sortAscending;
}
/**
 * The index of the column that is the current sort column (denoted by the
 * little triangle in the column heading).
 *
 * @return The index of the current sort column or -1 if it is not defined.
 */
public int getSortColumn() {

	return sortColumn;
}
/**
 * @return The instance of the SortIcon class to paint the sort icon.
 */
protected SortIcon getSortIconInstance() {

	if (sortIcon == null) sortIcon = new SortIcon( this );
	
	return sortIcon;
}
/**
 * Do some initialization.
 * 
 */
private void initialize() {

	addMouseListener( this );
	addMouseMotionListener( this );
}
/**
 * A method that's part of the MouseListener interface.
 * 
 */
public void mouseClicked( MouseEvent e ) {
}
/**
 * A method that's part of the MouseMotionListener interface.
 * 
 */
public void mouseDragged( MouseEvent e ) {

	if (dragIndex >= 0) {
		int x = dragConstraints.dragHorizontally( e.getX() );
		dragColumn( dragIndex, x - lastDragX );
		lastDragX = x;
	}	
}
/**
 * A method that's part of the MouseMotionListener interface.
 * 
 */
public void mouseEntered( MouseEvent e ) {

	savedCursor = getCursor();
}
/**
 * A method that's part of the MouseMotionListener interface.
 * 
 */
public void mouseExited( MouseEvent e ) {

	if (savedCursor != null) setCursor( savedCursor );

	if (canSort) {
		mouseOverIndex = -1;
		paint( getGraphics() );
	}
}
/**
 * A method that's part of the MouseMotionListener interface.
 * 
 */
public void mouseMoved( MouseEvent e ) {

	if (! isEnabled()) return;

	int mouseX = e.getX();

	if (canSort && mouseOverSortFeedback) {
		int oldMouseOverIndex = mouseOverIndex;
		mouseOverIndex = findSortIndex( mouseX );
		if (oldMouseOverIndex != mouseOverIndex) paint( getGraphics() );
	}
	
	if (findDragIndex( mouseX ) >= 0) {
		setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ));
	} else {
		setCursor( Cursor.getDefaultCursor() );
	}
}
/**
 * A method that's part of the MouseListener interface.
 * 
 */
public void mousePressed( MouseEvent e ) {

	if (! isEnabled()) return;

	int mouseX = e.getX();

	Integer oldDragIndex = Integer.valueOf( dragIndex );
	dragIndex = findDragIndex( mouseX );

	// Initiate Dragging ?
	if (dragIndex >= 0) {

		Dimension d = getPreferredSize();
		savePreferredWidth = d.width;
		savePreferredHeight = d.height;

		int i;
		int maxLeft = scrollPos + referenceColumnWidth;
		
		for (i=0; i<dragIndex; i++) {
			maxLeft += columnWidths[i];
		}

		dragConstraints = new DragConstraints( maxLeft + minimumColumnWidth, Integer.MAX_VALUE, 
			Integer.MAX_VALUE, Integer.MAX_VALUE );

		lastDragX = dragConstraints.dragHorizontally( maxLeft + columnWidths[i] );

		propertyChange.firePropertyChange( draggingStateChangedEvent, 
			oldDragIndex, Integer.valueOf( dragIndex ) );
	
	} else if (canSort && columnSortListener != null) {
	
		int sortIndex = findSortIndex( mouseX );

		if (sortIndex >= 0) {

			if (sortIndex == sortColumn) {
				sortAscending = ! sortAscending;
			} else {
				sortColumn = sortIndex;
				sortAscending = true;
			}

			setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

			columnSortListener.sortColumn( new ColumnSortEvent( this,
				sortColumn, sortAscending ) );

			//repaint();
			
			setCursor( Cursor.getDefaultCursor() );
		}
	}
}
/**
 * A method that's part of the MouseListener interface.
 * 
 */
public void mouseReleased( MouseEvent e ) {

	if (dragIndex != -1) {
		
		// stop dragging.
		Integer oldDragIndex = Integer.valueOf( dragIndex );
		dragIndex = -1;
		
		// Notify any listeners of the fact that dragging stopped.

		propertyChange.firePropertyChange( draggingStateChangedEvent, 
			oldDragIndex, Integer.valueOf( dragIndex ) );

		// If the preferred height and/or width of the titlebar changed during
		// dragging, notify any property change listeners of the fact.

		Dimension d = getPreferredSize();

		if (savePreferredWidth != d.width ||
			 savePreferredHeight != d.height) {

			// Notify any listeners of the fact that the preferred size of the titlebar 
			// changed.

			propertyChange.firePropertyChange( dimensionChangedEvent,
				new Dimension( savePreferredWidth, savePreferredHeight ), d );

		}
	}

	paint( getGraphics() );
	if (adjustToFit) {
		propertyChange.firePropertyChange( columnWidthsChangedEvent, null, null );
	}
}
/**
 * The titlebar's main paint method.
 * 
 * @param The Graphics object on which to paint.	  	
 */
public void paint( Graphics graphics ) {

	w = getSize().width;
	h = getSize().height;
	Graphics g = prepareOffScreen( graphics );

	int x = scrollPos + referenceColumnWidth;

	if (columns != null && columns.size() > 0) {

		if (columnWidths == null) setDefaultWidths();

		adjustColumns( w );

		for (int i=0; i<columns.size(); i++) {

			ColumnDefinition cd = (ColumnDefinition)columns.elementAt( i );

			paintOneTitle( g, i, x, cd );				

			x += columnWidths[i];
		}		
	}

	paintReferenceColumn( g );


	// Erase area to the right of the right most title if necessary.
	if (x < w) {
		eraseRight( g, x );
	}
	
	if (doubleBuffer) {

		graphics.drawImage( offScreen, 0, 0, w, h, 0, 0, w, h, null );
	}

}
/**
 * Paint one column title.
 * 
 * @param g The graphics context on which to paint.
 * @param column The column to be painted (zero-based column index).
 * @param x The left x coordinate of the point where painting for this column starts.
 * @param ColumnDefinition The column definition for this column.
 */
private void paintOneTitle( Graphics g, int column, int x, ColumnDefinition cd ) {

	int insideX;
	int insideY;
	int insideWidth = columnWidths[column];
	int insideHeight = h;
	
	if (insideWidth == 0) return;

	Color bg = cd.getTitleBackground();
	if (bg == null) bg = getBackground();

	border.setBackground( bg );
	border.paint( g, x, 0, insideWidth, insideHeight );

	insideX = x + borderLeft;
	insideY = borderTop;
	insideWidth -= borderLeft + borderRight;
	insideHeight -= borderTop + borderBottom;
	
	g.setColor( bg );	
	g.fillRect( x + borderLeft, borderTop, insideWidth, insideHeight );

	insideX += LEFT_MARGIN;
	insideY += TOP_MARGIN;
	insideWidth -= LEFT_MARGIN + RIGHT_MARGIN;
	insideHeight -= TOP_MARGIN + BOTTOM_MARGIN;

	if ((insideX + insideWidth) >= 0 && insideX < w) {

		if (canSort && (columnSortableFlags == null || columnSortableFlags[column]) &&
			(column == sortColumn || column == mouseOverIndex)) {

			paintOneTitleString( g, cd, insideX, insideY, insideWidth -
				SortIcon.SIZE, insideHeight );

			if (insideWidth > SortIcon.SIZE) {
				if (column == sortColumn) {
					boolean direction = (column == mouseOverIndex) ? ! sortAscending : sortAscending;
						getSortIconInstance().paint( g, direction, insideX + insideWidth - SortIcon.SIZE,
							0, SortIcon.SIZE, h );
				} else {
					getSortIconInstance().paint( g, true, insideX + insideWidth - SortIcon.SIZE,
						0, SortIcon.SIZE, h );
				}
			}

		} else {

			paintOneTitleString( g, cd, insideX, insideY, insideWidth, insideHeight );
		}
	}
}
/**
 * Paint one column title (paints only the string -- no border, background, etc.).
 * 
 * @param g The graphics context on which to paint.
 * @param x The left x coordinate of the point where painting for this column starts.
 * @param y The top coordinate of the point where painting for this column starts.
 * @param width The available width to paint the column title string.
 * @param height The available height to paint the column title string.
 * @param cd The column definition for this column.
 */
private void paintOneTitleString( Graphics g, ColumnDefinition cd, 
	int x, int y, int width, int height ) {

	// Set Font and Font Metrics:
	// Try to use the Font defined for the current column. If no special one is defined,
	// set the default title font for this titlebar.
	//
	Font 		f = cd.getTitleFont();
	FontMetrics m;
	
	if (f == null) {
		f = getFont();
		m = getFontMetrics( f );
	} else {
		m = cd.getTitleFontMetrics();
	}
	
	Shape	oldClip = g.getClip();

	int left = Math.max( x, 0 );
	int right = Math.min( x + width, w );
	g.setClip( left, y, right - left, height );

	g.setFont( f );

	Color c = cd.getTitleForeground();
	if (c == null) c = getForeground();
	g.setColor( c );

	StringFormatter formatter = cd.getTitleFormatter();
	if (formatter == null) formatter = defaultFormatter;

	formatter.formatString( g, m, cd.getTitle(),
		x, y, width, height );

	g.setClip( oldClip );	
}
/**
 * Paint the titlebar's (empty) reference column heading.
 *
 * @param g The Graphics object on which to paint.
 */
private void paintReferenceColumn( Graphics g ) {

	if (referenceColumnWidth == 0) return;

	int insideWidth = referenceColumnWidth;
	int insideHeight = h;
	
	Color bg = getBackground();
	border.setBackground( bg );
	border.paint( g, 0, 0, insideWidth, insideHeight );

	insideWidth -= borderLeft + borderRight;
	insideHeight -= borderTop + borderBottom;
	
	g.setColor( bg );	
	g.fillRect( borderLeft, borderTop, insideWidth, insideHeight );
}
/**
 * Set up the off-screen double buffer.
 * 
 * @param The titlebar's Graphics object.
 * @return The titlebar's Graphics object, if double buffering is disabled, or the
 * Graphics object of the off-screen buffer if double buffering is enabled.	  	
 * 
 */
private Graphics prepareOffScreen( Graphics g ) {

	if (doubleBuffer && w > 0 && h > 0) {

		if (offScreen == null) {

			// No offscreen image exists. Create a new one.
			offScreen = createImage( w, h );
			
		} else if (offScreen.getWidth(null) != w || 
			 offScreen.getHeight(null) != h) {

			// The allocated offscreen image's dimensions don't fit the 
			// current size of the component. Flush it and create a new one.

			offScreen.flush();
			offScreen = createImage( w, h );
		}

		return offScreen.getGraphics();

	} else {
	
		return g;
	}	
}
/**
 * A method for event listener management. See the 
 * comment for the 'propertyChange' field.
 * 
 * @param listener The listener to be removed.
 *  	
 */
public synchronized void removePropertyChangeListener( PropertyChangeListener listener ) {
	
	propertyChange.removePropertyChangeListener( listener );
}
/**
 * Set the <code>adjustToFit</code> property
 *
 * <p>A TitleBar with the <code>adjustToFit</code> property set to <code>true</code>
 * will evenly shrink or expand all resizeable columns as necessary such that the
 * right border of the right most column coincides with the right margin of the
 * TitleBar. 
 *
 */
public void setAdjustToFit( boolean b ) {

	adjustToFit = b; 
}
/**
 * Set the titlebar's width to an explicit pixel value.
 * 
 * @param explicitWidth The titlebar's new width in pixels or NOT_DEFINED if
 * the titlebar's width should be calculated dynamically.	  	
 */
public void setBarWidth( int explicitWidth ) {

	barWidth = explicitWidth;
}
/**
 * Set the titlebar's border.
 * 
 * @param The custom Border object to be used to draw the titlebar's border.	  	
 */
public static void setBorder( Border border ) {

	Titlebar.border = border;

	Insets insets = border.getInsets();
	
	borderTop = insets.top;
	borderLeft = insets.left;
	borderRight = insets.right;
	borderBottom = insets.bottom;
}
/**
 *
 * @param canSort <code>true</code> if the Titlebar allows column sorting,
 * <code>false</code> otherwise.
 */
public void setCanSort( boolean canSort ) {

	this.canSort = canSort;
}
/**
 * Set the column resizeable flags.
 * 
 * @param flags An array containing a boolean flag for each column that
 * defines whether the column is resizable (by dragging the divider in
 * the TitleBar) or not. Can be null, in which case all columns are
 * resizeable.
 *
 * @exception IllegalArgumentException if flags != null && 
 * columns.size() != flags.length
 * 
 */
public void setColumnResizeableFlags( boolean[] flags ) throws IllegalArgumentException {

	if (flags == null || columns.size() != flags.length) {
		throw new IllegalArgumentException( "# of columns differs from # of resizeable flags." );
	}
	
	this.columnResizeableFlags = flags;
}
/**
 * Set the titlebar's ColumnDefinition Vector and the column widths.
 * 
 * @param columns A Vector of ColumnDefinition objects.
 * @param widths An array defining the widths (in pixels) for the columns. Can be null, 
 * in which case the columns' default widths are used.
 * @exception IllegalArgumentException if widths != null && 
 * columns.size() != widths.length
 * 
 */
public void setColumns( Vector<ColumnDefinition> columns, int[] widths ) throws IllegalArgumentException {

	if (widths != null && columns.size() != widths.length) {
		throw new IllegalArgumentException( "# of columns differs from # of widths." );
	}

	this.columns = columns;
	this.columnWidths = widths;
	this.columnAdjustableFlags = new boolean[columns.size()];
}
/**
 * Set the column sortable flags.
 * 
 * @param flags An array containing a boolean flag for each column that
 * defines whether the column is sortable (by clicking on the little triangle
 * or not. Can be null, in which case all columns are
 * sortable.
 *
 * @exception IllegalArgumentException if flags != null && 
 * columns.size() != flags.length
 * 
 */
public void setColumnSortableFlags( boolean[] flags ) throws IllegalArgumentException {

	if (flags == null || columns.size() != flags.length) {
		throw new IllegalArgumentException( "# of columns differs from # of sortable flags." );
	}
	
	this.columnSortableFlags = flags;
}
/**
 * Set the column widths.
 * 
 * @param widths An array defining the widths (in pixels) for the columns. Can be null, 
 * in which case the columns' default widths are used.
 * @exception IllegalArgumentException if widths != null && 
 * columns.size() != widths.length
 * 
 */
public void setColumnWidths( int[] widths ) throws IllegalArgumentException {

	if (widths == null || columns.size() != widths.length) {
		throw new IllegalArgumentException( "# of columns differs from # of widths." );
	}
	
	this.columnWidths = widths;
}
/**
 * Set the titlebar's default background color.
 * 
 * @param c The new default background color.	  	
 */
public static void setDefaultBackground( Color c ) {

	defaultBackground = c;
}
/**
 * Set the titlebar's default font.
 * 
 * @param f The new default font.	  	
 */
public static void setDefaultFont( Font f ) {

	defaultFont = f;
}
/**
 * Set the titlebar's default foreground color.
 * 
 * @param c The new default foreground color.	  	
 */
public static void setDefaultForeground( Color c ) {

	defaultForeground = c;
}
/**
 * Set the titlebar's default StringFormatter.
 * 
 * @param formatter The new default StringFormatter object. 	
 */
public static void setDefaultFormatter( StringFormatter formatter ) {

	defaultFormatter = formatter;
}
/**
 * Calculate the column widths based on the column titles.
 * 
 */
protected void setDefaultWidths() {

	columnWidths = new int[columns.size()];

	for (int i=0; i<columnWidths.length; i++) {
		
		columnWidths[i] = ((ColumnDefinition)columns.elementAt( i )).
			getPreferredWidth( getFontMetrics( getFont() ), defaultFormatter );

		columnWidths[i] = actualColumnWidth( columnWidths[i] );
	}
}
/**
 * Enable/disable double buffering.
 * 
 * @param newState <code>true</code> to turn on double buffered painting, 
 * <code>false</code> to turn if off.	  	
 */
public void setDoubleBuffer( boolean newState ) {

	doubleBuffer = newState;

	if (newState == false) flush();
}
/**
 * 
 * 
 * @param newMinimumColumnWidth int
 */
public void setMinimumColumnWidth(int newMinimumColumnWidth) {

	minimumColumnWidth = newMinimumColumnWidth;
}
/**
 * If the 'mouseOverSortFeedback' property is <code>true</code>, the sort
 * icon is painted in the titlebar as the mouse passes over the column headings
 * of sortable columns to indicate to the user whether sorting will be done 
 * (and in which direction) by clicking the mouse.
 *
 * @param sortFeedback The new value for the 'mouseOverSortFeedback' property.
 */
public void setMouseOverSortFeedback( boolean sortFeedback) {
	
	mouseOverSortFeedback = sortFeedback;
}
/**
 * Set the width in pixels of the non-scrolling, untitled reference column
 * heading.
 * 
 * @param width The width in pixels of the titlebar's reference column.	  	
 */
public void setReferenceColumnWidth( int width ) {

	referenceColumnWidth = width;
}
/**
 * Set the titlebar's current scroll position.
 * 
 * @param pos The new scroll position. 0 to reset the scroll position to its left 
 * most position, a negative value to scroll the titlebar to the right.
 *	  	
 */
public void setScrollPos( int pos ) {

	scrollPos = pos;
}
/**
 *
 * @param sortAscending <code>true</code> if the current sort column is sorted 
 * in ascending order.
 */
public void setSortAscending( boolean sortAscending ) {

	this.sortAscending = sortAscending;
}
/**
 *
 * @param sortColumn The index of the currently sorted column.
 */
public void setSortColumn( int sortColumn ) {

	this.sortColumn = sortColumn;
}
}
