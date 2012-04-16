package tstuder.java.lib.component.table;

/*
 * The DataArea class implements a bare-bones data grid component with
 * no title bar or scrollbars.
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
import java.util.BitSet;
import tstuder.java.lib.graphics.*;
import tstuder.java.lib.util.DragScroller;
import tstuder.java.lib.util.Util;
import tstuder.java.lib.util.SwapListener;


/**
 * The DataArea class implements a bare-bones data grid component with no title
 * bar or scrollbars.
 * 
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version. 
 * &nbsp; ts, 1999-09-15, v1.0.1,  - Renamed scrollToLine() to scrollToRow().
 * &nbsp;                          - Removed bug re. tracking of current selection.
 * &nbsp;                          - Made sure offscreen image gets initialized.
 * &nbsp; ts, 1999-09-24, v1.0.2,  - Added <code>setRowHeightFactor</code> for
 * &nbsp;                            primitive line wrapping in cells.
 * &nbsp;                          - Moved ItemSelectable interface to Table. Item
 * &nbsp;                            listeners now have to be registered through the table. Along the same
 * &nbsp;                            lines, moved action listener registration from DataArea to Table.
 * &nbsp;                          - Implemented setEnabled(boolean) support.
 * &nbsp;                          - Proper support for double clicks (fires ActionEvent).
 * &nbsp; ts, 2000-03-13, v1.0.3,  - Fixed bug (exception) caused by paging down on a
 * &nbsp;                            table that doesn't fill its data area.
 * &nbsp; ts, 2000-05-24, v1.1.1   - Fixed minor bug in clickLineManyContinuous(...).
 * &nbsp;                          - removed update() (which was doing nothing). 
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class DataArea extends java.awt.Component implements MouseListener,
   KeyListener, MouseMotionListener, AdjustmentListener, FocusListener, SwapListener {
    
   private static final long serialVersionUID = 1L;
// Public default values
   private static StringFormatter   defaultFormatter     = new StringFormatter();
   private static StringFormatter   lineNumberFormatter  = new StringFormatter(
															StringFormatter.ALIGNMENT_RIGHT, 
															StringFormatter.ALIGNMENT_VCENTER );

   private static Color             gridLinesColor       = Color.lightGray;
   private static int               checkMarkColumnWidth = 22;
   private static int               lineNumberColumnWidth= 30;

   private static Color             referenceColumnForeground  = Color.black;
   private static Color             referenceColumnBackground  = Color.lightGray;
   private static Border            referenceColumnBorder;
   private static Font              referenceColumnFont; 
   static { 
	  setReferenceColumnBorder( new BorderRaised() );
	  setReferenceColumnFont( new Font( "SansSerif", Font.PLAIN, 10 )); 
   };

   // Four constants used for the selection mode.
   public static final int          SELECT_ZERO_OR_ONE 		= 0;
   public static final int          SELECT_ONE 					= 1;
   public static final int          SELECT_MANY 				= 2;
   public static final int          SELECT_MANY_CONTINUOUS 	= 3;

   private Table							parentTable;
   private Vector<ColumnDefinition> columns;
   private Vector<Object>           rows;

   private  int[]                   columnWidths;

   private boolean                  showGrid          = true;

   /** The row holding the focus */
   private int                      currentFocus		= 0;
   /** The current selection in the case that one row is selected or
	*  the current pivot row for selections using the shift key */
   private int                      currentSelection	= -1;
   /** The number of currently selected rows */
   private int                      selectionCount		= 0;

   private int                      lineHeight			= 1;
   /** Horizontal scroll position in pixels. Ranges from 0 (left most
	*  position to some negative value. */
   private int                      hScrollPos        = 0;
   /** Vertical scroll position in lines. Ranges from 0 (top most position
	*  to some negative value. */
   private int                      vScrollPos        = 0; // in lines

	/** See initialize() */
	private boolean						scrollingOptimization;

	private boolean						checkingSelects 	= false;

	/** The column colors used to draw a selected row. */
   private Color[]                  selectionBackground;
	/** The column colors used to draw a selected row. */
   private Color[]                  selectionForeground;

   private boolean                  showLineNumbers   = false;
   private boolean                  showCheckmarks    = false;
   /** The width in pixels of the non scrolling reference column containing
	*  the line numbers and checkmarks */
   private int                      referenceColumnWidth;

   /** The data area calculates the line height based on the font metrics
   * of the fonts used for a line's columns. By means of the
   * lineHeightFactor, the user is able to shrink or grow this
   * value by some factor. */
	private double							lineHeightFactor = 1.0;

	/** The preferred height (number of lines) of the data area. If this
   * is -1, the preferred height is determined based on the number of
   * rows in the rows vector. */
   private int								preferredHeight = -1;

   private boolean						hasFocus;

   private CheckMark                checkMark;

   // Manage our own little character buffer for the characters to be drawn. This
   // avoids excessive object creation during repaints. (By using String.getChars()
   // (passing charBuf) instead of using String.toCharArray(), the creation of temporary
   // character arrays is avoided. This improves performance).
   // 
   public final int                 DEFAULT_BUF_SIZE    = 80;
   private char[]                   charBuf             = new char[DEFAULT_BUF_SIZE];
   private int                      charBufResetCounter = 0; 

   private Image                    offScreen;
   private Graphics                 offScreenGraphics;

   private BitSet                   selections = new BitSet();
   private int                      selectionMode = SELECT_MANY;

   private BitSet                   checkmarks = new BitSet();

   private Scrollbar                vScrollbar;
   private Scrollbar                hScrollbar;

   private ActionListener				actionListener;
   private ItemListener					itemListener;
   private AdjustmentListener       adjustmentListener;
   private PopupMenu						popup;

   // Temporaries used while painting and dragging
   private Color                    fg;
   private Color                    bg;
   private Font                     f;
   private FontMetrics              fm;
   private int                      w;
   private int                      h;
   private int								lastDragLine;
   private DragScroller					scroller;
/**
 * Construct a DataArea object.
 *
 * @param vScrollbar A reference to the vertical scrollbar.
 * @param hScrollbar A reference to the horizontal scrollbar.
 * @param columnDefinitions The Vector with the ColumnDefinition objects defining
 * the DataArea's columns.
 * @param rows The Vector of objects implementing the TableRow interface
 * that represent the data to be displayed by the DataGrid.
 */
public DataArea( Table parent, Scrollbar vScrollbar, Scrollbar hScrollbar, 
        Vector<ColumnDefinition> columnDefinitions, Vector<Object> rows ) {

	parentTable = parent;
   setScrollbars( vScrollbar, hScrollbar );
   this.columns = columnDefinitions;
   this.rows = rows;
   initialize();
}
/**
 * Calculate the actual width of a column given the net width
 * for a particular data string.
 *
 * @param netWidth The width in pixels required for a given data cell's string.
 * @return The actual overall column width in pixels (adding the margins) 
 * required to get a net width as specified in 'netWidth'.
 */
int actualColumnWidth( int netWidth ) {

	return netWidth + Titlebar.LEFT_MARGIN + Titlebar.RIGHT_MARGIN;
}
/**
 * Add a popup menu to the DataArea.
 * 
 * @param popup The PopupMenu to be added.
 */
public void add( PopupMenu popup ) {

	this.popup = popup;
	super.add( popup );
}
/**
 * Add an ActionEvent listener if you want to receive ActionEvents if the user
 * presses the return or enterkey.
 * 
 * @param listener The listener to receive ActionEvents.
 *  	
 */
void addActionListener( ActionListener listener ) {

	actionListener = AWTEventMulticaster.add( actionListener, listener );
}
/**
 * Allow another object to receive adjustment events for the scrollbars 
 * associated with the DataArea.
 * 
 * @param l The listener to receive notifications of adjustment value 
 * changes of the scrollbars.
 *    
 */
public void addAdjustmentListener( AdjustmentListener l ) {

   adjustmentListener = AWTEventMulticaster.add( adjustmentListener, l );
}
/**
 * Add an ItemEvent listener if you want to receive ItemEvents if the user
 * changes the current row selection.
 * 
 * @param listener The listener to receive ItemEvents.
 *  	
 */
void addItemListener( ItemListener listener ) {

	itemListener = AWTEventMulticaster.add( itemListener, listener );
}
/**
 * React to adjustment events (scrollbar tracking).
 * 
 * @param e The adjustment event to respond to.
 */
public void adjustmentValueChanged( AdjustmentEvent e ) {

   int scrollTo;

   synchronized( rows ) {
	   
	   // If scrollingOptimization is true, collect any 
	   // adjustment events that have piled up in the event queue.
	   // This makes scrolling more responsive, as bigger jumps are taken
	   // (requiring fewer redraws) when the user scrolls quickly through
	   // a big range of values.
	   //
	   if (scrollingOptimization) {
		   scrollTo = e.getValue();

		   EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
		   AWTEvent queuedEvent = q.peekEvent();

		   while (queuedEvent != null && queuedEvent instanceof AdjustmentEvent) {

			  try { 
				 scrollTo = ((AdjustmentEvent)q.getNextEvent()).getValue();
			  } catch (InterruptedException ex) {
			  }
			  queuedEvent = q.peekEvent();
		   }
		   
	   } else {

	   	scrollTo = e.getValue();
	   }

		fireAdjustmentEvent( new AdjustmentEvent( (Adjustable)e.getSource(), e.getID(), 
			e.getAdjustmentType(), scrollTo ) );


	   // Adjust the scroll position to 'scrollTo'

	   Scrollbar s = (Scrollbar) e.getSource();

	   if (s == vScrollbar) {
		  
		  scrollTo( - scrollTo );

	   } else if (s == hScrollbar) {

		  setHorizontalScrollPos( - scrollTo );
		  paint( getGraphics() );
	   }
   }
}
/**
 * Check a particular row.
 * 
 * @param index The zero-based index of the row to check.
 */
void check( int index ) {
   
   check( index, true ); 
}
/**
 * Check or uncheck a particular row.
 * 
 * @param index The zero-based index of the row.
 * @param state <code>true</code> if the row should be checked, 
 * <code>false</code> if not.
 * 
 */
void check( int index, boolean state ) {

   if (checkmarks.get( index ) != state) {

	  if (state)  checkmarks.set( index );
	  else        checkmarks.clear( index );

	  if (index >= -vScrollPos && index <= -vScrollPos + getVisibleCount()) {
		 paintLine( index );
	  }
   }
}
/**
 * Check all rows.
 * 
 */
void checkAll() {

   for (int i=0; i<rows.size(); i++) check( i, true );
}
/**
 * Handle a mouse click (or a key event having the same effect as
 * a mouse click) on a line.  
 * 
 * @param line The line clicked on.
 * @param shift <code>true</code> if the shift key is pressed, <code>false</code>
 * otherwise.
 * @param control <code>true</code> if the control key is pressed, <code>false</code>
 * otherwise.
 * @param repaint A boolean that, if <code>false</code>, disables some of the
 * repainting in order to minimize flicker.
 */
private void clickLine(int line, boolean shift, boolean control, boolean repaint) {

	if (line < 0 || line >= rows.size()) return;
	
	switch (selectionMode) {

		case SELECT_ONE:
			clickLineOne( line, shift, control, repaint );
			break;
		case SELECT_ZERO_OR_ONE :
			clickLineZeroOrOne(line, shift, control, repaint);
			break;
		case SELECT_MANY :
			clickLineMany(line, shift, control, repaint);
			break;
		case SELECT_MANY_CONTINUOUS :
			clickLineManyContinuous(line, shift, control, repaint);
			break;
	}
}
/**
 * Handle a mouse click (or a key event having the same effect as
 * a mouse click) on a line. The 'Many' in the method name stands for
 * the selection Mode (SELECT_MANY). 
 * 
 * @param line The line clicked on.
 * @param shift <code>true</code> if the shift key is pressed, <code>false</code>
 * otherwise.
 * @param control <code>true</code> if the control key is pressed, <code>false</code>
 * otherwise.
 * @param repaint A boolean that, if <code>false</code>, disables some of the
 * repainting in order to minimize flicker.
 */
private void clickLineMany(int line, boolean shift, boolean control, boolean repaint) {

	if (control) {

		int what;
		
		if (shift) {

			// control and shift are pressed

			int start = currentSelection;
			if (start < 0)
				start = currentFocus;
			if (start < 0)
				start = 0;
			currentSelection = start;
			moveFocus(line, true, false);
			selectRange(start, line);
			what = ItemEvent.SELECTED;

		} else {
		
			moveFocus(line, true, false);
			if (!isSelected(line)) {
				currentSelection = line;
				select(line, true);
				what = ItemEvent.SELECTED;

			} else {
			
				select(line, false);
				what = ItemEvent.DESELECTED;
			}
		}

		fireItemEvent(new ItemEvent(
			parentTable, 
			ItemEvent.ITEM_STATE_CHANGED, 
			rows.elementAt(line), 
			what ));

	} else {
		clickLineManyContinuous(line, shift, control, repaint);
	}
}
/**
 * Handle a mouse click (or a key event having the same effect as
 * a mouse click) on a line. The 'ManyContinuous' in the method name stands for
 * the selection Mode (SELECT_MANY_CONTINUOUS). 
 * 
 * @param line The line clicked on.
 * @param shift <code>true</code> if the shift key is pressed, <code>false</code>
 * otherwise.
 * @param control <code>true</code> if the control key is pressed, <code>false</code>
 * otherwise.
 * @param repaint A boolean that, if <code>false</code>, disables some of the
 * repainting in order to minimize flicker.
 */
private void clickLineManyContinuous(int line, boolean shift, boolean control, boolean repaint) {

	if (shift) {
		int start = currentSelection;
		if (start < 0)
			start = currentFocus;
		if (start < 0)
			start = 0;
		moveFocus(line, true, true);
		selectRangeExclusive(start, line);
		currentSelection = start;
		
		fireItemEvent(new ItemEvent(
			parentTable, 
			ItemEvent.ITEM_STATE_CHANGED, 
			rows.elementAt(line), 
			ItemEvent.SELECTED));

	} else {
		if (selectionCount > 1) {
			deselectAll( true );
		}
		clickLineZeroOrOne(line, shift, control, repaint);
	}
}
/**
 * Handle a mouse click (or a key event having the same effect as
 * a mouse click) on a line. The 'One' in the method name stands for
 * the selection Mode (SELECT_ONE). 
 * 
 * @param line The line clicked on.
 * @param shift <code>true</code> if the shift key is pressed, <code>false</code>
 * otherwise.
 * @param control <code>true</code> if the control key is pressed, <code>false</code>
 * otherwise.
 * @param repaint A boolean that, if <code>false</code>, disables some of the
 * repainting in order to minimize flicker.
 */
private void clickLineOne(int line, boolean shift, boolean control, boolean repaint) {

	if (! isSelected(line)) {

		clickLineZeroOrOne( line, false, false, repaint );
	}
}
/**
 * Handle a mouse click (or a key event having the same effect as
 * a mouse click) on a line. The 'ZeroOrOne' in the method name stands for
 * the selection Mode (SELECT_ZERO_OR_ONE). 
 * 
 * @param line The line clicked on.
 * @param shift <code>true</code> if the shift key is pressed, <code>false</code>
 * otherwise.
 * @param control <code>true</code> if the control key is pressed, <code>false</code>
 * otherwise.
 * @param repaint A boolean that, if <code>false</code>, disables some of the
 * repainting in order to minimize flicker.
 */
private void clickLineZeroOrOne(int line, boolean shift, boolean control, boolean repaint) {

	if (isSelected(line)) {

		if (control) {
			
			// Current line is selected --> deselect
			moveFocus(line, true, false);
			if (currentSelection == line) currentSelection = -1;
			selectionCount--;
			selections.clear(line);
			if (repaint) paintLine(line);

			fireItemEvent( new ItemEvent(
				parentTable,
				ItemEvent.ITEM_STATE_CHANGED,
				rows.elementAt( line ),
				ItemEvent.DESELECTED ));

		} else {
			moveFocus(line, true, repaint);
			currentSelection = line;
		}
	} else { // Current line is not selected --> Select

		moveFocus(line, true, false);
		if (currentSelection >= 0) {
			deselect(currentSelection);
		}
		selections.set(line);
		currentSelection = line;
		selectionCount++;
		if (repaint) paintLine(line);

		fireItemEvent( new ItemEvent(
			parentTable,
			ItemEvent.ITEM_STATE_CHANGED,
			rows.elementAt( line ),
			ItemEvent.SELECTED ));
	}
}
/**
 * Deselect a row.
 * 
 * @param index The zero-based index of the row to deselect. 
 */
void deselect( int index ) {
   
   select( index, false );
}
/**
 * Deselect all rows.
 *
 * @param redraw <code>true</code> if the visible, deselected rows are
 * to be redrawn immediately, <code>false</code> otherwise.
 */
void deselectAll( boolean redraw ) {

	if (redraw) {
	   int firstIndex = -vScrollPos;
	   int pastIndex = firstIndex + getVisibleCount();

	   for (int i=firstIndex; i<pastIndex; i++) {

		   if (selections.get( i )) {
			   selections.clear( i );
			   paintLine( i );
		   }
	   }
	}

	selections = new BitSet();
	selectionCount = 0;
	currentSelection = -1;
}
/**
 * Erase the space between the last row currently visible and the bottom of the
 * component's screen rectangle.
 * 
 * @param g The Graphics object to paint to.
 * @param y The y coordinate defining the top edge of the area to erase.   
 */
private void eraseBottom( Graphics g, int y ) {

   if (y < h) {

	  g.setColor( bg );
	  g.fillRect( 0, y, w, h-y );
	  g.drawLine( 0, y, w, h );  // The fillRect call above seems to be
	  // buffered under some circumstances (Sun's JRE 1.1.7B) and isn't
	  // alsways performed. By calling the draw method after the fill,
	  // everything works fine!!!??
   }
}
/**
 * Finalize method -- flush the double buffer.
 * 
 */
public void finalize() throws Throwable {

   flush();
   super.finalize();
}
/**
 * Look up the row index based on the vertical click coordinate.
 *
 * @param y The Y coordinate for which the row has to be looked up.
 * @return int The zero-based row index of the row under the point with the passed
 * Y coordinate.
 *
 */
private int findLine( int y ) {

	int line = y / lineHeight - vScrollPos;

	if (line < 0 || line >= rows.size()) return -1;
	else											 return line;
}
/**
 * Fire an ActionEvent. Called if the user presses 'Enter'.
 *
 * @param e The ActionEvent.
 */
void fireActionEvent( ActionEvent e ) {
	
	if (actionListener != null) actionListener.actionPerformed( e );
}
/**
 * Notify any adjustment listeners.
 * 
 * @param e The adjustment event object.
 */
void fireAdjustmentEvent( AdjustmentEvent e ) {

   if (adjustmentListener != null) adjustmentListener.adjustmentValueChanged( e );
}
/**
 * Fire an ItemEvent. Called if the user changes the current row selection.
 *
 * @param e The ItemEvent.
 */
void fireItemEvent( ItemEvent e ) {
	
	if (itemListener != null) itemListener.itemStateChanged( e );
}
/**
 * Flush the off-screen double buffer.
 * 
 * 
 */
private void flush() {

   if (offScreen != null) {
	  offScreen.flush();
	  offScreen = null;
   }
}
/**
 * One of the methods implementing the FocusListener interface.
 * 
 * @param e The focus event object.
 */
public void focusGained( FocusEvent e ) {

	if (offScreenGraphics == null) return; // Wait for first call to paint()
	
	synchronized( rows ) {
		if (hasFocus == false) {

			hasFocus = true;
			if (rows.size() > 0) paintLine( currentFocus );
		}		
	}
}
/**
 * One of the methods implementing the FocusListener interface.
 * 
 * @param e The focus event object.
 */
public void focusLost( FocusEvent e ) {

	if (offScreenGraphics == null || getGraphics() == null) return; // Avoid doing anything before
	  // component is ready or after component has been torn down.
	
	synchronized( rows ) {
		if (hasFocus == true) {

			hasFocus = false;
			if (rows.size() > 0) paintLine( currentFocus );
		}		
	}
}
/**
 *
 * @return The indexes of the currently checked rows. Returns <code>null</code>
 * if there is no checkmark column.
 */
int[] getCheckedIndexes() {

	if (! showCheckmarks) return null;
	
	Vector<Integer> checked = new Vector<Integer>();

	for (int i=0; i<rows.size(); i++) {
		if (checkmarks.get( i )) {
			checked.addElement( Integer.valueOf( i ) );
		}
	}

	int[] indexes = new int[checked.size()];

	for (int i=0; i<indexes.length; i++) {
		indexes[i] = ((Integer)checked.elementAt( i )).intValue();
	}

	return indexes;
}
/**
 * 
 * @return <code>true</code> if the checking of a checkmark should select the
 * corresponding line. <code>false</code> otherwise.
 */
public boolean getCheckingSelects() {
	
	return checkingSelects;
}
/**
 * Get the array of <code>int</code>s defining the DataArea's column widths
 *
 * @return The DataArea's column widths in pixels.     
 */
int[] getColumnWidths() {

   return columnWidths;
}
/**
 * Calculate the number of rows that can be fully displayed.
 * 
 * @return The number of rows that can be fully displayed.     
 */
int getCompletelyVisibleCount() {

   return h / lineHeight;  // rounds down
}
/**
 * Get the default StringFormatter object.
 *
 * @return The default StringFormatter object that is used in case one or more
 * ColumnDefinition objects don't specify a custom StringFormatterr.     
 */
public static StringFormatter getDefaultFormatter() {

   return defaultFormatter;
}
/**
 * Get the color in which the DataArea's grid lines are painted.
 * 
 * @return The Color used to paint all DataArea object's grid lines (if grid lines
 * are enabled).     
 */
public static Color getGridLinesColor() {

   return gridLinesColor;
}
/**
 * Scrolling is done strictly by lines. If the dataArea's height divided by
 * the lineHeight has a non-zero remainder, this method returns true.
 * 
 * @return <code>true</code> if a partially visible row is being displayed in order
 * to fill the gap between the bottom of the DataArea and the bottom-most 
 * fully visible row, <code>false</code> if not.     
 */
boolean getHasFillerLine() {

   return (rows.size() * lineHeight > h && h % lineHeight != 0);
}
/**
 * Get the horizontal scroll position -- a value ranging from 0 (left most positiion)
 * to some negative value (right most position, actual value depends on the
 * preferred width of the DataArea).
 * 
 * @return The horizontal scroll position in pixels.
 */
int getHorizontalScrollPos() {

   return hScrollPos;
}
/**
 * Get the StringFormatter instance that is used by the DataArea to draw the line
 * numbers.
 *  
 * @return The StringFormatter instance responsible for drawing the line numbers.
 */
public static StringFormatter getLineNumberFormatter() {

   return lineNumberFormatter;
}
/**
 * Get the minimum size of the DataArea.
 * 
 * @return The minimum width and height of the DataArea.
 */
public Dimension getMinimumSize() {

   if (columns == null) return new Dimension( 10, 10 );

   int width = 0;
   lineHeight = 0; 

   // Init the widths of the columns to their preferred widths.
   if (columnWidths == null) setDefaultWidths();      
	  
   // calculate the total width of the data area by adding up the column widths
   for (int i=0; i<columnWidths.length; i++) {

	  width += columnWidths[i];
   }

   // Calculate the height of a line (the maximum height of the cells in a line,
   // determined by the data font)
   //
   for (int i=0; i<columns.size(); i++) {
	  int temp = ((ColumnDefinition)columns.elementAt( i )).
		 getPreferredDataLineHeight( getFontMetrics( getFont() )  );
	  if (temp > lineHeight) lineHeight = temp;
   }

   // New feature: growing or shrinking the calculated line height
   // by some factor. TS, 1999-09-20.
   lineHeight *= lineHeightFactor;
   
   lineHeight += Titlebar.TOP_MARGIN + Titlebar.BOTTOM_MARGIN;
   return new Dimension( width + getReferenceColumnWidth(), lineHeight );
}
/**
 * Get the preferred size of the DataArea.
 * 
 * @return The preferred width and height of the DataArea.
 */
public Dimension getPreferredSize() {

   Dimension d = getMinimumSize();

   if (preferredHeight == -1) {
	  	d.height = rows.size() * d.height;
   } else {
	  	d.height = preferredHeight * d.height;
   } 

   return d;
}
/**
 * Get the reference column's background color.
 * 
 * @return The Color used to paint the reference column's background.
 */
public static Color getReferenceColumnBackground() {

   return referenceColumnBackground;
}
/**
 * Get the Border instance used for painting the reference column's border.
 * 
 * @return The Border instance used to paint the reference column's border.
 */
public static Border getReferenceColumnBorder() {

   return referenceColumnBorder;
}
/**
 * Get the Font instance used for painting the line numbers.
 * 
 * @return The Font instance used to paint the line numbers.
 */
public static Font getReferenceColumnFont() {

   return referenceColumnFont;
}
/**
 * Get the reference column's foreground color.
 * 
 * @return The Color used to paint the reference column's foreground.
 */
public static Color getReferenceColumnForeground() {

   return referenceColumnForeground;
}
/**
 * Calculate and return the width of the reference column.
 * 
 * @return The width (in pixels) of the reference column.
 */
int getReferenceColumnWidth() {

   int width = 0;

   if (showLineNumbers) width += lineNumberColumnWidth;
   if (showCheckmarks) width += checkMarkColumnWidth;

   return width;
}
/**
 * @return The zero-based index of the one selected row. 
 * If none or multiple are
 * selected, return -1.
 * 
 */
int getSelectedIndex() {

	if (selectionCount == 1 && currentSelection >= 0) return currentSelection;
	else                                              return -1;
}
/**
 *
 * @return The indexes of the currently selected rows.
 */
int[] getSelectedIndexes() {

	int[] indexes = new int[selectionCount];
	int i = 0;
	int j = 0;

	while (i < rows.size()) {
		if (selections.get( i )) {
			indexes[j++] = i;
		}
		i++;
	}

	return indexes;
}
/**
 *
 * @return The currently selected rows as an array of Objects.
 */
public Object[] getSelectedObjects() {

	synchronized( rows ) {

		int[] indexes = getSelectedIndexes();

		Object[] objects = new Object[indexes.length];

		for (int i=0; i<indexes.length; i++) {
			objects[i] = rows.elementAt( indexes[i] );
		}

		return objects;
	}
}
/**
 * Get the active selection mode.
 * 
 * @return int The DataArea's selection mode (see the selection mode 
 * constants defined as part of DataArea's class definition).
 */
int getSelectionMode() {

   return selectionMode;
}
/**
 * Find out whether a reference column containing checkmarks is shown.
 * 
 * @return <code>true</code> if the reference column showing clickable
 * checkmarks is
 * displayed at the left edge of the DataArea, <code>false</code> if not.
 */
boolean getShowCheckmarks() {

   return showCheckmarks;
}
/**
 * Find out whether grid lines are shown.
 * 
 * @return <code>true</code> if grid lines are drawn, <code>false</code> otherwise.
 */
boolean getShowGrid() {

   return showGrid;
}
/**
 * Find out whether a reference column containing line numbers is shown.
 * 
 * @return <code>true</code> if the reference column showing line numbers is 
 * displayed at the left edge of the DataArea, <code>false</code> if not.
 */
boolean getShowLineNumbers() {

   return showLineNumbers;
}
/**
 * Get the DataArea's vertical scroll position. The scroll position is an integer
 * ranging from 0 (top-most position) to some negative value (bottom-most scroll
 * position, actual value depending on the total number of rows). 
 * 
 * @return The DataArea's vertical scroll position.
 */
int getVerticalScrollPos() {

   return vScrollPos;
}
/**
 * Get the number of visible rows (including a possibly partially visible row
 * at the bottom of the DataArea).
 * 
 * @return The number of at least partially visible rows.
 */
int getVisibleCount() {

   int c = h / lineHeight;  // rounds down
   
   if (c >= rows.size()) {

	  return rows.size();

   } else if (h % lineHeight != 0) {

	  return c + 1;  // add 1 if the division to initialize c rounded down

   } else {

	  return c;
   }
}
/**
 * DataArea initialization.
 * 
 */
private void initialize() {

	// Set the 'scrollingOptimization' flag (see adjustmentValueChanged()).
	try {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) sm.checkAwtEventQueueAccess();
		
		// Event queue access allowed, enable optimization.
		scrollingOptimization = true;
	} catch (Exception e) {
	
		// Event queue accesss not allowed, disable optimization.
		scrollingOptimization = false;
	}
	
	if (columns == null) {
	   selectionBackground = new Color[0];
	   selectionForeground = new Color[0];
	} else {
	   selectionBackground = new Color[columns.size()];
	   selectionForeground = new Color[columns.size()];
	}
	
   Color selBg = new Color( 0, 0, 128 );
   Color selFg = Color.white;
   
   for (int i=0; i<selectionBackground.length; i++) {

	  selectionBackground[i] = selBg;
	  selectionForeground[i] = selFg;
   }

   addMouseListener( this );
   addMouseMotionListener( this );
   addFocusListener( this );
   addKeyListener( this );
}
/**
 * Find out whether a particular row is checked.
 * 
 * @param index The zero-based index of the row in question.
 * @return <code>true</code> if the row is checked, <code>false</code> if not.
 */
final boolean isChecked( int index ) {
   
   return checkmarks.get( index );
}
/**
 * One of the methods implementing the FocusListener interface.
 *
 */
public boolean isFocusTraversable() {

	return true;
}
/**
 * Find out whether a particular row is selected.
 * 
 * @param index The zero-based index of the row in question.
 * @return <code>true</code> if the row is selected, <code>false</code> if not.
 */
final boolean isSelected( int index ) {
   
   return selections.get( index );
}
/**
 * Respond to a key down event.
 * 
 * @param e The event to respond to.
 * @see keyUp
 */
private void keyDown( KeyEvent e ) {

   //int code = e.getKeyCode();
   int next = 0;

   if (! isSelected( currentFocus ) && ! e.isShiftDown() && ! e.isControlDown()) {

	  clickLine( currentFocus, e.isShiftDown(), e.isControlDown(), true );

   } else {

	  next = Math.min( rows.size()-1, currentFocus + 1 );

	  int lastVisible = -vScrollPos + getCompletelyVisibleCount();

	  if (next >= lastVisible) {
		 if (e.isControlDown() && ! e.isShiftDown()) {
			moveFocus( next, true, false );
		 } else {
			clickLine( next, e.isShiftDown(), e.isControlDown(), false );
		 }
		 scrollTo( vScrollPos - 1 );
   	if (vScrollbar != null) vScrollbar.setValue( - (vScrollPos - 1) );
   	
	  } else {
		 if (e.isControlDown() && ! e.isShiftDown()) {
			moveFocus( next, true, true );
		 } else {
			clickLine( next, e.isShiftDown(), e.isControlDown(), true );
		 }
	  }
   }
}
/**
 * Respond to a page down key event.
 * 
 * @param e The event to respond to.
 * @see keyPageUp
 */
private void keyPageDown( KeyEvent e ) {

	int completelyVisibleCount = getCompletelyVisibleCount();

	if (completelyVisibleCount > rows.size()) completelyVisibleCount = rows.size();

   if (currentFocus < - vScrollPos + completelyVisibleCount - 1) {

	  if (! e.isShiftDown()) {
		 moveFocus( -vScrollPos + completelyVisibleCount - 1, true, true );
	  } else {
		 clickLine( -vScrollPos + completelyVisibleCount - 1, e.isShiftDown(), 
			 e.isControlDown(), true );
	  }

   } else {

	  int pos = vScrollPos - completelyVisibleCount;

	  if (pos < - rows.size() + completelyVisibleCount) {
		 pos = - rows.size() + completelyVisibleCount;
	  }

	  if (! e.isShiftDown()) {
		 moveFocus( Math.min( -pos + completelyVisibleCount - 1, 
			rows.size() - 1 ), true, false );
		 scrollTo( pos );
   	 if (vScrollbar != null) vScrollbar.setValue( -pos );
   	
	  } else {
		 clickLine( Math.min( -pos + completelyVisibleCount - 1, rows.size() - 1 ), 
			 e.isShiftDown(), e.isControlDown(), true );
		 scrollTo( pos );
   	 if (vScrollbar != null) vScrollbar.setValue( -pos );
	  }
   }
   
}
/**
 * Respond to a page up key event.
 * 
 * @param e The event to respond to.
 * @see keyPageDown
 */
private void keyPageUp( KeyEvent e ) {

   if (currentFocus > - vScrollPos) {

	  if (! e.isShiftDown()) {
		 moveFocus( -vScrollPos, true, true );
	  } else {
		 clickLine( -vScrollPos, e.isShiftDown(), e.isControlDown(), true );
	  }

   } else {

	  int pos = Math.min( vScrollPos + getCompletelyVisibleCount(), 0 );

	  if (! e.isShiftDown()) {
		 moveFocus( - pos, true, false );
		 scrollTo( pos );
   	 if (vScrollbar != null) vScrollbar.setValue( -pos );
	  } else {
		 clickLine( - pos, e.isShiftDown(), e.isControlDown(), true );
		 scrollTo( pos );
   	 if (vScrollbar != null) vScrollbar.setValue( -pos );		 
	  }
   }
}
/**
 * One of the methods implementing the KeyListener interface.
 */
public void keyPressed(KeyEvent e) {

	int code = e.getKeyCode();

	switch (code) {
		
		case KeyEvent.VK_SPACE:
			// Space bar simulates mouse click on the line with the focus.

			synchronized( rows ) {
	   		if (showCheckmarks) {
			 		check( currentFocus, ! isChecked( currentFocus ) );
	   		}

				clickLine(currentFocus, e.isShiftDown(), e.isControlDown(), true);
			}
			break;
			
		case KeyEvent.VK_ENTER:
			fireActionEvent(
				new ActionEvent( parentTable, ActionEvent.ACTION_PERFORMED, null ));
			break;

		case KeyEvent.VK_DOWN:
			synchronized( rows ) {
				keyDown(e);
			}
			break;
			
		case KeyEvent.VK_UP:
			synchronized( rows ) {	
				keyUp(e);
			}
			break;
			
		case KeyEvent.VK_PAGE_DOWN:
			synchronized( rows ) {
				keyPageDown(e);
			}
			break;
			
		case KeyEvent.VK_PAGE_UP:
			synchronized( rows ) {
				keyPageUp(e);
			}
			break;
	}
}
/**
 * One of the methods implementing the KeyListener interface.
 */
public void keyReleased( KeyEvent e ) {
}
/**
 * One of the methods implementing the KeyListener interface.
 */
public void keyTyped( KeyEvent e ) {
}
/**
 * Respond to a key up event.
 * 
 * @param e The event to respond to.
 * @see keyDown
 */
private void keyUp( KeyEvent e ) {

   //int code = e.getKeyCode();
   int next = 0;

   if (! isSelected( currentFocus ) && ! e.isShiftDown() && ! e.isControlDown()) {

	  clickLine( currentFocus, e.isShiftDown(), e.isControlDown(), true );
	  return;

   } else {

	  next = Math.max( 0, currentFocus - 1 );

	  int top = -vScrollPos;

	  if (next < top) {
		 if (e.isControlDown() && ! e.isShiftDown()) {
			moveFocus( next, true, false );
		 } else {
			clickLine( next, e.isShiftDown(), e.isControlDown(), false );
		 }
		 scrollTo( vScrollPos + 1 );
   	 if (vScrollbar != null) vScrollbar.setValue( -(vScrollPos + 1) );
	  } else {
		 if (e.isControlDown() && ! e.isShiftDown()) {
			moveFocus( next, true, true );
		 } else {
			clickLine( next, e.isShiftDown(), e.isControlDown(), true );
		 }
	  }
   }
}
/**
 * One of the methods implementing the MouseListener interface.
 */
public void mouseClicked(MouseEvent e) {
}
/**
 * One of the methods implementing the MouseMotionListener interface.
 */
public void mouseDragged( MouseEvent e ) {
		
	if (! isEnabled()) return;

	int y = e.getY();
	int leftInset = getReferenceColumnWidth();
	
	if (scroller == null) {
		Rectangle r = new Rectangle( leftInset, 0, 
			w - leftInset, getCompletelyVisibleCount() * lineHeight );
		scroller = new DragScroller( r, this, this );
	}

	scroller.drag( e.getX(), y, e.getModifiers() | InputEvent.SHIFT_MASK );

	if (e.getX() > leftInset && y >= 0 && y <= getCompletelyVisibleCount() * lineHeight) {

		synchronized( rows ) {
			int line = findLine( e.getY() );

			if (line >= 0 && line != lastDragLine) {

		   	clickLine( line, true, e.isControlDown(), true );
		   	lastDragLine = line;
			}
		}
	}
}
/**
 * One of the methods implementing the MouseMotionListener interface.
 */
public void mouseEntered( MouseEvent e ) {

}
/**
 * One of the methods implementing the MouseMotionListener interface.
 */
public void mouseExited( MouseEvent e ) {

}
/**
 * One of the methods implementing the MouseMotionListener interface.
 */
public void mouseMoved( MouseEvent e ) {

}
/**
 * One of the methods implementing the MouseListener interface.
 */
public void mousePressed( MouseEvent e ) {

	if (! isEnabled()) return;
	
	requestFocus();

	synchronized( rows ) {

		int line = findLine( e.getY() );

	   if (line == -1) return;

	   if (showCheckmarks) {

			if (e.getX() <= getReferenceColumnWidth()) {
	   
			 	check( line, ! isChecked( line ) );
			 	if (checkingSelects) {
					lastDragLine = line;
					clickLine( line, e.isShiftDown(), e.isControlDown(), true );
			 	} else {
				 	moveFocus( line, true, true );
			 	}
			 	return;
		  	}
	   }

		lastDragLine = line;
	   clickLine( line, e.isShiftDown(), e.isControlDown(), true );
	}
}
/**
 * One of the methods implementing the MouseListener interface.
 */
public void mouseReleased( MouseEvent e ) {

	if (! isEnabled()) return;

	if (scroller != null) {
		scroller = null;
	}
	
	// Double click handling
	
	if (e.getClickCount() == 2) {
		fireActionEvent( new ActionEvent( parentTable, ActionEvent.ACTION_PERFORMED, null ));
	}
}
/**
 * This method was created in VisualAge.
 * @param row int
 */
public void moveFocus( int row ) {

	moveFocus( row, true, true );
}
/** 
 * Move the focus (the dotted focus line) to a new row.
 *
 * @param line The new line to receive the focus.
 * @param redrawPrev <code>true</code> if the row holding the previous focus is
 * to be redrawn, <code>false</code> otherwise.
 * @param redrawNext <code>true</code> if the row receiving the new focus is to be
 * redrawn, <code>false</code> otherwise.
 */
private void moveFocus( int line, boolean redrawPrev, boolean redrawNext ) {

   if (currentFocus != line) {

	  int oldFocus = currentFocus;
	  currentFocus = line;
		 
	  if (redrawPrev) paintLine( oldFocus );
	  if (redrawNext) paintLine( line );
   }
}
/**
 * The main paint() method.
 * 
 * @param g The Graphics object to paint to.
 */
public void paint( Graphics g ) {

   Dimension d = getSize();
   w = d.width;
   h = d.height;

   prepareOffScreen( g );
   referenceColumnWidth = getReferenceColumnWidth();

   // Set up the default colors and font for quick access in paintLine().
   bg = getBackground();
   fg = getForeground();
   f = getFont();
   fm = getFontMetrics( f );

   if (showCheckmarks && checkMark == null) checkMark = new CheckMark( this, true );
   
	synchronized( rows ) {
	   updateVerticalScrollbar();
	   updateHorizontalScrollbar();
		paintVisibleRows( g );
	}

   // Reset charBuf in case it grew to an unreasonalby big size during painting
   if (charBufResetCounter % 4096 == 0) {
	   charBuf = new char[DEFAULT_BUF_SIZE];
	   charBufResetCounter = 0;
   } else {
	   charBufResetCounter++;
   } 
}
/**
 * Draw the dotted focus rectangle.
 * 
 * @param g The Graphics object to paint to.
 * @param right The X coordinate where (from the left) the focus rectangle ends (it
 * always starts just right of the reference column or at the left edge of the
 * DataArea if the reference column is disabled).
 */
private void paintFocus( Graphics g, int right ) {

	if (! isEnabled()) return;
	
	int left = getReferenceColumnWidth();
   int bottom = lineHeight - 1 - (showGrid ? 1 : 0);
   
   right -= 1;

   g.setColor( Color.white );
   g.setXORMode( Color.black );

   for (int x=left; x<right; x+=2) {
	  g.drawLine( x, 0, x, 0 );
   }
   for (int x=left; x<right; x+=2) {
	  g.drawLine( x, bottom, x, bottom );
   }

   for (int y=1; y<bottom; y+=2) {
	  g.drawLine( left, y, left, y );
   }

   for (int y=1; y<bottom; y+=2) {
	  g.drawLine( right, y, right, y );
   }

   g.setPaintMode();
}
/**
 * Paint one line.
 *
 * @param line The zero-based index of the line (row) to paint.
 */
private void paintLine( int line ) {

   paintLine( getGraphics(), line, 0, columns.size() );
}
/**
 * Paint the columns of one line, starting at column 'startCol' and ending at
 * (not including) 'pastCol'.
 * 
 * @param line The zero-based index of the line to paint.
 * @param startCol The index of the first column from the left to be painted.
 * @param pastCol The index of the column just right of the last one to be painted.
 */
private void paintLine( Graphics graphics, int line, int startCol, int pastCol ) {

   Graphics    g        = offScreenGraphics;
   Shape       oldClip  = g.getClip();
   int         x        = 0;
   String      s;
   boolean     selected = isSelected( line );
	TableRow  	row = null;

	row = (TableRow)rows.elementAt(line);

   String[] strings = row.getStrings();
   Color[] backgrounds;
   Color[] foregrounds;

   if (selected && isEnabled()) {
		backgrounds = selectionBackground;
	   foregrounds = selectionForeground;
   } else {
	  	backgrounds = row.getBackgrounds();
	   foregrounds = row.getForegrounds();
   }

   // Paint the line number and checkMark (if they are enabled) only if
   // the left most data column is drawn.
   if (startCol == 0) {
   
	  	Insets insets = referenceColumnBorder.getInsets();

	  	g.setColor( referenceColumnBackground );     
	  	g.fillRect( 0, 0, referenceColumnWidth, lineHeight );
	  	g.setColor( referenceColumnForeground );
	  	g.setFont( referenceColumnFont );

	  	if (showLineNumbers) {

		 	s = String.valueOf( line + 1 );
		 	s.getChars( 0, s.length(), charBuf, 0 );
	  
		 	lineNumberFormatter.formatString( g, g.getFontMetrics( referenceColumnFont ), 
				String.valueOf( line + 1 ), 
				insets.left, insets.top, 
				lineNumberColumnWidth - insets.right - insets.left - Titlebar.RIGHT_MARGIN,
				lineHeight - insets.top - insets.bottom );

		 	x = lineNumberColumnWidth;
	  	}

	  	if (showCheckmarks) {

		 	checkMark.paint( g, isChecked( line ), x, 0, checkMarkColumnWidth, lineHeight );
		 	x += checkMarkColumnWidth;
	  	}

	  	referenceColumnBorder.paint( g, 0, 0, referenceColumnWidth, lineHeight );

	  	g.clipRect( x, 0, w - x, lineHeight );
   }

   x += hScrollPos;
   //int focusLeft = x;

   // Paint current line's cells
   //
   for (int i=startCol; i<pastCol; i++) {

	  	ColumnDefinition currentCol = (ColumnDefinition)columns.elementAt( i );
	  	int colWidth = columnWidths[i];

	  	// Set the background color for the current cell
	  	if (backgrounds != null) {
		 	g.setColor( backgrounds[i] );
	 	} else {
		 	Color c = currentCol.getDataBackground();
		 	if (c == null) g.setColor( bg );
		 	else           g.setColor( c );
	  	}

	  	g.fillRect( x, 0, colWidth, lineHeight );
	  
	  	// Set the foreground color for the current cell
	  	if (foregrounds != null) {
		 	g.setColor( foregrounds[i] );
	  	} else {
		 	Color c = currentCol.getDataForeground();
		 	if (c == null) g.setColor( fg );
		 	else           g.setColor( c );
	  	}

	  	// Pick the StringFormatter to use
	  	StringFormatter formatter = currentCol.getDataFormatter();
	  	if (formatter == null) formatter = defaultFormatter;

	  	// Pick the font and font metrics to use
	  	Font curFont = currentCol.getDataFont();
	  	FontMetrics m;
	  	if (curFont == null) {
		 	g.setFont( f );
		 	m = fm;
	  	} else {
		 	g.setFont( curFont );
		 	m = currentCol.getDataFontMetrics();
	  	}

	  	s = strings[i]; // the string to be drawn

	  	if (currentCol.getWrapDataCells()) {

		  	// Draw wrapping data cells		  
		  	formatter.formatString( g, m, s,
			 	x + Titlebar.LEFT_MARGIN,
			 	Titlebar.TOP_MARGIN, 
			 	colWidth - Titlebar.LEFT_MARGIN - Titlebar.RIGHT_MARGIN,
			 	lineHeight - Titlebar.TOP_MARGIN - Titlebar.BOTTOM_MARGIN );

		} else {

	  	  	// Speed optimized drawing of non-wrapping data cells
	  
		  	// In order to avoid excessive creation of temporary objects just for
		  	// drawing, a little character buffer is maintained 
		  	// into which the characters of the
		  	// current string are copied. The reference to this character buffer is then
		  	// passed to drawChars().
		  	//
		  	// Increase the buffer size if its current size isn't big enough.
		  	if (s.length() > charBuf.length) charBuf = new char[s.length()];

		  	// Copy the string's chars into our own buffer.
		  	s.getChars( 0, s.length(), charBuf, 0 );

		  	// Draw the current string
			formatter.drawChars( g, m, charBuf, 0, s.length(), 
				x + Titlebar.LEFT_MARGIN, Titlebar.TOP_MARGIN + 
			 	formatter.calcBaselineOffset( m, lineHeight - Titlebar.TOP_MARGIN - Titlebar.BOTTOM_MARGIN ), 
			 	colWidth - Titlebar.LEFT_MARGIN - Titlebar.RIGHT_MARGIN );
		}

	  	x += colWidth;       

	  	if (showGrid) {
		 	g.setColor( gridLinesColor );
		 	g.drawLine( x - 1, 0, x - 1, lineHeight - 1 );
	  	}     
   }

   // Erase the space to the right of the right-most cell.
   if (x < w) {
	  	g.setColor( bg );
	  	g.fillRect( x, 0, w - x, lineHeight );
   }

   g.setClip( oldClip );

   // Draw the grid lines if necessary.
   if (showGrid) {
	  	g.setColor( gridLinesColor );    
	  	g.drawLine( referenceColumnWidth, lineHeight - 1, x - 1, lineHeight - 1 );
   }

   // Paint the dotted focus line if necessary.
   if (hasFocus && line == currentFocus) {
	  	paintFocus( g, Math.min( w, x ) );
   }

   // Copy the double buffer onto the the current line's rectangle
   // in the data area.
   //
   int destY = (vScrollPos + line) * lineHeight;
   graphics.drawImage( offScreen, 0, destY, w, destY + lineHeight, 
		0, 0, w, lineHeight, null );
}
/**
 * Paint a number of lines.
 * 
 * @param g The Graphcis object to paint to.
 * @param start The first line to paint.
 * @param past The line just below the last one to paint.
 */
private void paintLines( Graphics g, int start, int past ) {

   // Loop over visible lines and paint them.
   int lineCount = rows.size();
   
   if (past > lineCount) past = lineCount;

   for (int i=start; i<past; i++) {
	  
	  paintLine( g, i, 0, columns.size() );
   }

   // Erase Area below the last line
   if (past == lineCount) {
	   // System.out.println( "painting from: " + ((vScrollPos + past) * lineHeight) );
	   eraseBottom( g, (vScrollPos + past) * lineHeight );
	}
}
/**
 * Repaint all visible rows.
 * 
 * @param g The Graphics object to draw to.
 */
void paintVisibleRows( Graphics g ) {

   int firstIndex = -vScrollPos;
   int pastIndex = firstIndex + getVisibleCount();

   paintLines( g, firstIndex, pastIndex );
}
/**
 * Set up the off-screen graphics buffer.
 * 
 * @param g The DataArea's Graphics object.
 */
private void prepareOffScreen( Graphics g ) {

   if (offScreen == null) {

	  // No offscreen image exists. Create a new one.
	  offScreen = createImage( w, lineHeight );
	  
   } else if (offScreen.getWidth(null) != w || 
			  offScreen.getHeight(null) != lineHeight) {

	  // The allocated offscreen image's dimensions don't fit the 
	  // current size of the component. Flush it and create a new one.

	  offScreen.flush();
	  offScreen = createImage( w, lineHeight );
   }

   offScreenGraphics = offScreen.getGraphics();
}
/**
 * Low-level event handling method to test for popup menu trigger.
 *
 * @param e The MouseEvent we're dealing with.
 */
public void processMouseEvent( MouseEvent e ) {

	if (e.isPopupTrigger() && popup != null) {

		popup.show( this, e.getX(), e.getY() );

	} else {

		super.processMouseEvent( e );
	}
}
/**
 * Remove an ActionEvent listener.
 * 
 * @param listener The listener to remove.
 *  	
 */
void removeActionListener( ActionListener listener ) {

	actionListener = AWTEventMulticaster.remove( actionListener, listener );
}
/**
 * Remova an adjustment listener.
 * 
 * @param l The adjustment listener to remove.
 */
public void removeAdjustmentListenner( AdjustmentListener l ) {

   adjustmentListener = AWTEventMulticaster.remove( adjustmentListener, l );
}
/**
 * Remove an ItemEvent listener.
 * 
 * @param listener The listener to remove.
 *  	
 */
void removeItemListener( ItemListener listener ) {

	itemListener = AWTEventMulticaster.remove( itemListener, listener );
}
/**
 * Update the vertical scroll position and
 * the BitSets keeping track of checkmarks and row selections in the
 * case that a range of rows has been inserted into the rows Vector.
 *
 * @param startRow The index of the first row that was inserted. 
 * @param pastRow The index just past the last row that was inserted. 
 */
void rowsInserted( int startRow, int pastRow ) {

	int count = pastRow - startRow;
	
	if (showCheckmarks) {

		checkmarks = Util.insertBits( checkmarks, startRow, count );
	}

	selections = Util.insertBits( selections, startRow, count );

	if (startRow <= currentSelection) {
		if (currentSelection >= 0) currentSelection += count;
		currentFocus += count;
	}
	
	// Adjust the vertical scroll position
	if (rows.size() - count > getCompletelyVisibleCount()) {

		if (startRow <= -vScrollPos) {

			vScrollPos -= pastRow - startRow;

			int maxVScrollPos = -(rows.size() - getCompletelyVisibleCount());

			vScrollPos = Math.max( vScrollPos, maxVScrollPos );
		}
	}
}
/**
 * Update the vertical scrollbar and 
 * the BitSets keeping track of checkmarks and row selections in the
 * case that a range of rows has been removed from the rows Vector.
 *
 * @param startRow The index of the first row that was removed. 
 * @param pastRow The index just past the last row that was removed. 
 */
void rowsRemoved( int startRow, int pastRow ) {

	if (showCheckmarks) {

		checkmarks = Util.removeBits( checkmarks, startRow, pastRow );
	}

	if (currentSelection >= startRow && currentSelection < pastRow) {
		currentSelection = -1;
	} else if (currentSelection >= pastRow) {
		currentSelection -= pastRow - startRow;
	}
	if (currentSelection < 0) currentSelection = -1;

	if (currentFocus >= pastRow) {
		currentFocus -= pastRow - startRow;
	} else if (currentFocus >= rows.size()) {
		currentFocus = rows.size() - 1;
	}
	if (currentFocus < 0) currentFocus = 0;
	
	for (int i=startRow; i<pastRow; i++) {
		if (selections.get( i )) selectionCount--;
	}

	selections = Util.removeBits( selections, startRow, pastRow );

	if (startRow < -vScrollPos) {

		vScrollPos += pastRow - startRow;
	}

	if (vScrollPos < 0) {
		if (-vScrollPos > rows.size() - getCompletelyVisibleCount()) {
			vScrollPos = -(rows.size() - getCompletelyVisibleCount());
		}
	}
	if (vScrollPos > 0) vScrollPos = 0;
}
/**
 * Scroll the DataArea's contents to a particular line.
 * 
 * @param newScrollPos The index of the line to scroll to, that is, the index of the
 * top-most visible line.
 */
void scrollTo( int newScrollPos ) {

   int delta, remainingLine;
   Graphics g = getGraphics();
  	int visibleCount = getVisibleCount();

   if (newScrollPos < vScrollPos) {

	  	// scroll down

	  	delta = vScrollPos - newScrollPos;
	  	remainingLine = getHasFillerLine() ? 1 : 0;

	  	if (delta >= visibleCount) {

			vScrollPos = newScrollPos;
		 	paintLines( g, - vScrollPos, - vScrollPos + visibleCount );

	  	} else {
		 
		 	int topCopyLine = delta;
		 	int bottomCopyLine = visibleCount - remainingLine;

		 	g.copyArea( 0, topCopyLine * lineHeight, w, 
			 	(bottomCopyLine - topCopyLine) * lineHeight, 0, - delta * lineHeight );
		 	vScrollPos = newScrollPos;

		 	paintLines( g, - vScrollPos + bottomCopyLine - delta, - vScrollPos + 
			 	visibleCount );

		 	int bottom = (vScrollPos + rows.size()) * lineHeight;
		 	if (bottom < h) {
	 	  		eraseBottom( g, bottom );
			}
	  	}

   } else if (newScrollPos > vScrollPos) {

	  	// scroll up

	  	delta = newScrollPos - vScrollPos;
	  	remainingLine = getHasFillerLine() ? 1 : 0;

	  	if (delta >= visibleCount) {

		 	vScrollPos = newScrollPos;
		 	paintLines( g, - vScrollPos, - vScrollPos + visibleCount );

		} else {
		 
		 	//int topCopyLine = 0;
		 	//int bottomCopyLine = visibleCount - delta;

		 	g.copyArea( 0, 0, w, (visibleCount - delta) * lineHeight, 0, 
			 	delta * lineHeight );
		 	vScrollPos = newScrollPos;
		 	paintLines( g, - vScrollPos, - vScrollPos + delta );
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

	if (row >= - vScrollPos && row < - vScrollPos + getCompletelyVisibleCount()) return;
	
	int newScrollPos = Math.min( row, rows.size() - getCompletelyVisibleCount() );

	if (newScrollPos < 0) newScrollPos = 0;

	scrollTo( - newScrollPos );

   if (vScrollbar != null) vScrollbar.setValue( newScrollPos );
}
/**
 * Select a line.
 * 
 * @param index The zero-based index of the line to select.
 */
void select( int index ) {
   
   select( index, true );  
}
/**
 * Select or deselect a line.
 * 
 * @param index The zero-based index of the line to select/deselect.
 * @param state <code>true</code> if the specified line is to be selected,
 * <code>false</code> otherwise.
 */
void select( int index, boolean state ) {

   if (selections.get( index ) != state) {

	  	if (state)  {

			selectionCount++;
		 	selections.set( index );

	  	} else {

	 		selectionCount--;
	 		selections.clear( index );
	  	}
		 
		if (selectionCount == 0) currentSelection = -1;
		else if (selectionCount == 1) currentSelection = index;

		if (index >= -vScrollPos && index <= -vScrollPos + getVisibleCount()) {
		 	paintLine( index );
	   }
   }
}
/**
 * Select all rows.
 * 
 */
void selectAll() {

   for (int i=0; i<rows.size(); i++) select( i, true );
}
/**
 * Select a range of lines.
 * 
 * @param first The first line of the range (can be greater than <code>last</code>).
 * @param last The last line of the range (can be smaller than <code>first</code>).
 */
private void selectRange( int first, int last ) {

   int top;
   int bottom;

   if (first <= last) {
	  top = first;
	  bottom = last;
   } else {
	  top = last;
	  bottom = first;
   }

   for (int i=top; i<=bottom; i++) {

	  select( i, true );
   }

}
/**
 * Select a range of lines and deselect any lines not in the range.
 * 
 * @param first The first line of the range (can be greater than <code>last</code>).
 * @param last The last line of the range (can be smaller than <code>first</code>).
 */
private void selectRangeExclusive( int first, int last ) {

   int top;
   int bottom;

   if (first <= last) {
	  	top = first;
	  	bottom = last;
   } else {
	  	top = last;
	  	bottom = first;
   }

   int i;

   for (i=0; i<top; i++) select( i, false );

   for (i=top; i<=bottom; i++) select( i, true );

   for (i=bottom+1; i<rows.size(); i++) select( i, false );

}
/**
 * Checks the rows with row indexes as specified in the 'indexes' array.
 * Any other rows are unchecked.
 * 
 * @param indexes The zero-based indexes of the rows to check.
 * @exception IllegalArgumentException If the 'indexes' array contains one
 * or more illegal row indexes.
 */
void setCheckedIndexes( int[] indexes ) throws IllegalArgumentException {

	uncheckAll( false );

	try {

		for (int i=0; i<indexes.length; i++) {

			check( indexes[i], true );
		}

	} catch (Exception e) {

		throw new IllegalArgumentException();

	} finally {

		if (getGraphics() != null) paint( getGraphics() );
	}
}
/**
 * 
 * @param state Pass <code>true</code> if the checking of a checkmark should select the
 * corresponding line. <code>false</code> otherwise.
 */
public void setCheckingSelects( boolean state ) {
	
	checkingSelects = state;
}
/**
 * Set the width of the part of the reference column containing the checkmarks to
 * a specific width in pixels.
 * 
 * @param w The new width in pixels.
 */
public static void setCheckMarkColumnWidth( int w ) {

   checkMarkColumnWidth = w;
}
/**
 * Set the widths of the DataArea's columns (no redraw).
 * 
 * @param widths The array of <code>int</code>s containing the new column widths.
 * @exception IllegalArgumentException if widths == null || 
 * columns.size() != widths.length
 */
void setColumnWidths( int[] widths ) throws IllegalArgumentException {

   if (widths == null || columns.size() != widths.length) {
	  throw new IllegalArgumentException( "# of columns differs from # of widths." );
   }
   
   this.columnWidths = widths;
}
/**
 * Set the default StringFormatter object.
 * 
 * @param f The default StringFormatter.
 */
public static void setDefaultFormatter( StringFormatter f ) {

   defaultFormatter = f;
}
/**
 * Calculate and set the columns' initial width.
 */
private void setDefaultWidths() {

   columnWidths = new int[columns.size()];

   for (int i=0; i<columnWidths.length; i++) {
	  
	  columnWidths[i] = ((ColumnDefinition)columns.elementAt( i )).
		 getPreferredWidth( getFontMetrics( getFont() ), defaultFormatter );

	  columnWidths[i] += Titlebar.LEFT_MARGIN + Titlebar.RIGHT_MARGIN;
   }
}
/**
 * Enable or disable the component.
 *
 * @param enable boolean
 */
public void setEnabled( boolean enable ) {

	super.setEnabled( enable );

	paintVisibleRows( getGraphics() );
}
/**
 * Set the color in which the grid lines (if at all) are painted.
 * 
 * @param c The Color for the grid lines.
 */
public static void setGridLinesColor( Color c ) {

   gridLinesColor = c;
}
/**
 * Set the DataArea's horizontal scroll position (no redraw).
 * 
 * @param pos The new horizontal scroll position.
 */
void setHorizontalScrollPos( int pos ) {

   hScrollPos = pos;
}
/**
 * Set the width of the part of the reference column containing line numbers to
 * a specific width (pixels).
 * 
 * @param w The new width in pixels for the line number part of the reference column.
 */
public static void setLineNumberColumnWidth( int w ) {

   lineNumberColumnWidth = w;
}
/**
 * Set the StringFormatter instance used to format the line numbers.
 * 
 * @param f The StringFormatter to format the DataArea's line numbers.
 */
public static void setLineNumberFormatter( StringFormatter f ) {

   lineNumberFormatter = f;
}
/**
 * Set the preferred height of the data area to an explicit value.
 *
 * @param numberOfLines The preferred size in terms of number of rows.
 */
public void setPreferredHeight( int numberOfRows ) {

	preferredHeight = numberOfRows;
}
/**
 * Set the reference column's background color.
 * 
 * @param c The new background color for the reference column.
 */
public static void setReferenceColumnBackground( Color c ) {

   referenceColumnBackground = c;
}
/**
 * Set the reference column's Border instance.
 * 
 * @param b The Border instance used to draw the reference column's border.
 */
public static void setReferenceColumnBorder( Border b ) {

   b.setBackground( referenceColumnBackground );
   b.setForeground( referenceColumnForeground );

   referenceColumnBorder = b;
}
/**
 * Set the reference column's Font (the Font used to draw the line numbers). 
 * 
 * @param f The new reference column Font.
 */
public static void setReferenceColumnFont( Font f ) {

   referenceColumnFont = f;
}
/**
 * Set the Color used to paint the reference column's foreground.
 * 
 * @param c The new reference column foreground Color.
 */
public static void setReferenceColumnForeground( Color c ) {

   referenceColumnForeground = c;
}
/**
 * Allows to scale the calculated row height by some factor. The main use
 * of this is to make room for wrapping data cells. @see StringFormatter,
 * StringWrapFormatter.
 * 
 * @param factor The factor the calculated row height should be scaled with. 
 */
public void setRowHeightFactor( double factor ) {

	lineHeightFactor = factor;
}
/**
 * Pass a reference to the Vector containing the data to be displayed.
 * 
 * @param rows The new data Vector (of which each element is expected to implement
 * the TableRow interface).
 */
void setRows( Vector<Object> rows ) {

   this.rows = rows;
}
/**
 * Set the DataArea's scrollbars.
 * 
 * @param vScrollbar Vertical Scrollbar - can be null.
 * @param hScrollbar Horizontal Scrollbar - can be null.
 */
void setScrollbars( Scrollbar vScrollbar, Scrollbar hScrollbar ) {

   if (this.vScrollbar != null) {
	  this.vScrollbar.removeAdjustmentListener( this );
   }

   if (this.hScrollbar != null) {
	  this.hScrollbar.removeAdjustmentListener( this );
   }

   vScrollbar.addAdjustmentListener( this );
   hScrollbar.addAdjustmentListener( this );

   this.vScrollbar = vScrollbar;
   this.hScrollbar = hScrollbar;
}
/**
 * Selects the rows with row indexes as specified in the 'indexes' array.
 * Any other rows are deselected.
 * 
 * @param indexes The zero-based indexes of the rows to select.
 * @exception IllegalArgumentException If the 'indexes' array contains one
 * or more illegal row indexes.
 */
void setSelectedIndexes( int[] indexes ) throws IllegalArgumentException {

	deselectAll( false );

	try {

		for (int i=0; i<indexes.length; i++) {

			select( indexes[i], true );
		}

	} catch (Exception e) {

		throw new IllegalArgumentException();

	} finally {
		paintVisibleRows( getGraphics() );
	}
}
/**
 * Set the DataArea's active selection mode. Resets any Selections. If the
 * selection mode equals SELECT_ONE and there area rows in the data Vector,
 * the method selects the first row.
 *
 * <p>Any selection mode constraints are only enforced on the GUI level and
 * not on the API level. Selecting, for example, two rows through a call to
 * setSelectedIndexes() with a current selection mode of SELECT_ONE
 * is possible.
 * 
 * @param mode The selection mode (one of the four selection mode constants 
 * defined as part of DataArea's class declaration).
 */
void setSelectionMode( int mode ) {

	deselectAll( true );
   selectionMode = mode;

   if (mode == SELECT_ONE && rows.size() > 0){
	   selections.set( 0 );
	   currentSelection = 0;
	   currentFocus = 0;
	   selectionCount = 1;
   } else {
   	currentSelection = -1;
   	currentFocus = 0;
   	selectionCount = 0;
   }

   if (getGraphics() != null) paint( getGraphics() );
}
/**
 * Enable/disable the displaying of checkmarks. If checkmarks are enabled,
 * they appear as part of the DataArea's reference column at the left hand side
 * of the component.
 * 
 * @param <code>true</code> if checkmarks are to be shown, <code>false</code> 
 * otherwise.
 */
void setShowCheckmarks( boolean b ) {

   showCheckmarks = b;
}
/**
 * Show/hide grid lines.
 * 
 * @param b <code>true</code> if grid lines are to be shown, <code>false</code>
 * otherwise.
 */
void setShowGrid( boolean b ) {

   showGrid = b;
}
/**
 * Enable/disable the displaying of line numbers. If line numbers are enabled,
 * they appear as part of the DataArea's reference column at the left hand side
 * of the component.
 * <p>Line numbers number the lines of the DataGrid starting with '1'.
 * 
 * @param <code>true</code> if line numbers are to be shown, <code>false</code> 
 * otherwise.
 */
void setShowLineNumbers( boolean b ) {

   showLineNumbers = b;
}
/**
 * Set the vertical scroll position to a new value. 
 * 
 * @param pos The new vertical scroll position -- a value between 0 (top most
 * position) and some negative value (bottom most position). 
 */
void setVerticalScrollPos( int pos ) {

   vScrollPos = pos;
}
/**
 * A method of the SwapListener interface. It is called while the DataArea's rows 
 * are being sorted for each 'swap' performed by the sort algorithm.
 *
 * <p>This method makes sure that the BitSets storing the current selections
 * and row checkbox states are being sorted in sync with the DataArea's rows.
 */
public void swapped(int i, int j) {

	if (showCheckmarks) {
		boolean jIsSet = checkmarks.get( j );
		
		if (checkmarks.get( i )) checkmarks.set( j );
		else                     checkmarks.clear( j );
		
		if (jIsSet)              checkmarks.set( i );
		else                     checkmarks.clear( i );
	}

	boolean jIsSet = selections.get( j );
	
	if (selections.get( i )) selections.set( j );
	else                     selections.clear( j );
	
	if (jIsSet)              selections.set( i );
	else                     selections.clear( i );
}
/**
 * Clear the check box of a given line.
 * 
 * @param index The zero-based index of the line whose checkmark is to be cleared.
 */
void uncheck( int index ) {
   
   check( index, false );
}
/**
 * Uncheck all rows.
 *
 * @param redraw <code>true</code> if the visible, unchecked rows are
 * to be redrawn immediately, <code>false</code> otherwise.
 */
void uncheckAll( boolean redraw ) {

	if (redraw) {
	   int firstIndex = -vScrollPos;
	   int pastIndex = firstIndex + getVisibleCount();

	   for (int i=firstIndex; i<pastIndex; i++) {

		   if (checkmarks.get( i )) {
			   checkmarks.clear( i );
			   paintLine( i );
		   }
	   }
	}

	checkmarks = new BitSet();
}
/**
 * Adjust the horizontal scrollbar's values.
 * 
 */
void updateHorizontalScrollbar() {

   if (hScrollbar == null) return;

   int value = hScrollbar.getValue();
   int visible = getSize().width;
   int max = getPreferredSize().width;

   hScrollbar.setValues( value, visible, 0, max ); 
   hScrollbar.setBlockIncrement( visible );
   hScrollbar.setUnitIncrement( 50 );
}
/**
 * Adjust the vertical scrollbar's values.
 */
void updateVerticalScrollbar() {

   if (vScrollbar == null) return;

   int visible = getVisibleCount();
   int filler = getHasFillerLine() ? 1 : 0;   

   vScrollbar.setValues( -vScrollPos, visible, 0, rows.size() + filler );
   vScrollbar.setBlockIncrement( visible - filler );
}
}
