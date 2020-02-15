package tstuder.java.applet.gui.tabledemo;

/*
 * This class demonstrates the capabilities and usage (by means of its
 * source code) of the Table Bean tstuder.java.lib.component.table.Table.
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

import tstuder.java.lib.component.table.*;
import tstuder.java.lib.graphics.*;
import tstuder.java.lib.util.ColumnSortableVector;
import java.applet.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

/**
 * This class demonstrates the capabilities and usage (by means of its
 * source code) of the Java bean tstuder.java.lib.component.table.Table.
 *
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * &nbsp; ts, 2000-05-22, v1.1,    Added demo "SortingAndResizing" (requires Table Bean version 1.1).
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
@SuppressWarnings("unchecked")
public class TableDemo extends Applet {

 
    private static final long serialVersionUID = 1L;
Table 	table;
   Vector 	rows  = new Vector();
  	Label 	statusText = new Label( "" );
  	boolean  synchronizationAndScrollingDemo = false;
   TableUpdater updater = null;

   /** Applet parameter name. */
   static final String DEMO_KIND 									= "demo_kind";
   /** Applet parameter value for the single selection list demo. */
   static final String SINGLE_SELECTION_DEMO 					= "SingleSelection";
   /** Applet parameter value for the checkmarks demo. */
   static final String CHECKMARKS_DEMO 							= "CheckMarks";
   /** Applet parameter value for the "madness" demo. */
   static final String MADNESS_DEMO		 							= "Madness";
   /** Applet parameter value for the string formatting demo. */
   static final String STRING_FORMATTING_DEMO					= "StringFormatting";
   /** Applet parameter value for the column width adjustment demo. */
   static final String COLUMN_WIDTH_ADJUSTMENT_DEMO   		= "ColumnWidthAdjustment";
   /** Applet parameter value for the synchronization and scrolling demo. */
   static final String SYNCHRONIZATION_AND_SCROLLING_DEMO   = "SynchronizationAndScrolling";
   /** Applet parameter value for the API test demo. */
   static final String API_TEST_DEMO                        = "ApiTest";
   /** Applet parameter value for the sorting and column resizing test demo. */
   static final String SORTING_AND_RESIZING_DEMO            = "SortingAndResizing";



   /**
	 * TableUpdater is a thread that adds/removes/updates random table rows.
	 *
	 * Its purpose is to demonstrate and test the multi-thread safety
	 * of the Table bean.
	 */
   static class TableUpdater extends Thread {

	   public boolean running = true;

		// The arrays of Strings below are used to generate ficticious address entries
		// for the demo table.
	   
		String[] firstNames = new String[] {
			"Jim", 		"Joe", 		"Adele", 		"Michael",
			"Andrew",	"Tom",		"Marc",			"Heidi",
			"Martin",	"Steve",		"Niguel",		"Simone",
			"Andrea",	"Jennifer",	"Catherine",	"Trudy" };

		String[] lastNames = new String[] {
			"Pascoe",	"Wurlitzer", "Tannenbaum",	"Winter",
			"Summers",	"Villeroy",	 "Schuster",	"Adlescott",
			"Simpson",	"Jaeger",	 "Eggert",		"Manson",
			"Mason",		"Garland",	 "Foster",		"Heinemann" };

		String[] streets = new String[] {
			"12 Wilbor Road",     "555 Jackson Avenue",  "34 Robineau Road", "22 Plum Lane",
			"1200 Central Drive", "990 West Street",     "1 Foster Square",  "40 Corporate Drive",
			"887 Park Drive",     "5500 Seaside Avenue", "76 Wilcox Lane",   "6 University Place",
			"3 Willow Lane",      "88 Harbor Road",      "69 Hoch Street",   "90 Destination Avenue" };

		String[] cities = new String[] {
			"Los Angeles",        "Syracuse",            "New York",         "Westmoore",
			"Springfield",        "Sunnyvale",           "Rosenberg",        "Annaville",
			"Madison",            "Summerville",         "Winterberg",       "New Basel",
			"Cold Harbor",        "Jacksontown",			"South City",       "Duarte" };

		String[] phoneNumbers = new String[] {
			"567-345-5645",  "567-123-4563", "987-345-5431", "432-536-4425",
			"776-233-4563",  "114-542-7777", "875-556-4653", "998-455-1122",
			"435-765-9944",  "277-994-3330", "657-336-9982", "154-336-2346",
			"874-345-0909",  "546-336-9987", "654-342-6654", "752-889-4454" };

		String[][] entries = new String[][] { firstNames, lastNames, streets, cities, phoneNumbers };

		
	   
	   /** The selection list that tells TableUpdater what
	    *  update mode it is supposed to be running in. */
		final Table 			choiceList;

		/** The Table the TableUpdater is working on. */
		final Table				table;

		/** Current mode of operation: 0 - do nothing, 1 - add, 2 - remove, 3 - update */
		int						mode = 0;

	   
		TableUpdater( Table t, Table choice ) {

			table = t;
			choiceList = choice;

			choiceList.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent e ) {

					mode = choiceList.getSelectedIndex();
				}
			} );
		};
	   
	   public void run() {

		   while (running) {

		   	Vector rows = table.getRows();
		   	int oldCount, index;

				switch (mode) {

				   case 0: // do nothing
				   	try { Thread.sleep( 500 ); } catch (InterruptedException e) {}
				   	break;

				   case 1: // insert a random table row

				   	synchronized (rows) {
					   	oldCount = rows.size();
					   	index = (int) (Math.random() * oldCount);
					   	rows.insertElementAt( generateTableLine(), index );
				   		table.rowsInserted( index, 1 );
				   	}
				   	try { Thread.sleep( 500 ); } catch (InterruptedException e) {}

				   	break;

				   case 2: // remove a random table row

				   	synchronized (rows) {
					   	
					   	oldCount = rows.size();
					   	if (oldCount > 0) {
	
					   		index = (int) (Math.random() * oldCount);
					   		rows.removeElementAt( index );
					   		table.rowsRemoved( index, 1 );
					   	}
				   	}
				   	try { Thread.sleep( 500 ); } catch (InterruptedException e) {}
				   	
				   	break;
				   	
				   case 3: // update a random table row

				   	synchronized (rows) {
					   	oldCount = rows.size();
					   	if (oldCount > 0) {
	
					   		index = (int) (Math.random() * oldCount);
					   		updateTableRow( (SimpleTableRow)rows.elementAt( index ) );
					  			table.rowsUpdated( index, 1 );
					   	}
				   	}
				   	try { Thread.sleep( 400 ); } catch (InterruptedException e) {}
				   	
				   	break;
			   }
		   }

	   }

	   /**
	    * Select a random string from a passed String array.
	    */
	   private String selectRandomString( String[] strings ) {

		   return strings[(int) (Math.random() * strings.length)];
	   }

	   /**
	    * Update a table row.
	    */
	   private void updateTableRow( SimpleTableRow row ) {

		   for (int i=0; i<row.getStrings().length; i++) {
			   if (Math.random() < 0.5) {
				   row.getStrings()[i] = selectRandomString( entries[i] );
			   }
		   }
	   }

	   /**
	    * Generate a table row.
	    */
	   private SimpleTableRow generateTableLine() {

		   String[] newRow = new String[5];
		   for (int i=0; i<5; i++) {
				newRow[i] = selectRandomString( entries[i] );
		   }

		   return new SimpleTableRow( newRow );
	   }
   }

   /**
	 * A class handling action events for the API Test Demo
	 */
   static class ApiTester implements ActionListener {

	   /** The table to work on */
	   private Table table;
	   
	   public ApiTester( Table table ) {
		   //this.table = table;
	   };
	   
		public void actionPerformed( ActionEvent e ) {
   	};
   }

/**
 * 
 */
public void destroy() {

	if (updater != null && updater.isAlive()) updater.running = false;
}
/**
 * Get the applet info text.
 */
public String getAppletInfo() {
	return "An applet to demonstrate the Java bean " + 
		"'tstuder.java.lib.component.table.Table' -- " +
		"a high performance data grid bean. " +
		"See http://www.datacomm.ch/tstuder/resources";
}
/**
 * Get the applet's parameter info.
 */
public String[][] getParameterInfo() {
   
	return new String[][] {
		 { DEMO_KIND, "String", "one of { '" + SINGLE_SELECTION_DEMO + "', '" +
			 												CHECKMARKS_DEMO + "', '" +
			 												MADNESS_DEMO + "', '" +
			 												STRING_FORMATTING_DEMO + "', '" +
			 												COLUMN_WIDTH_ADJUSTMENT_DEMO + "', '" +
			 												SYNCHRONIZATION_AND_SCROLLING_DEMO + "', '" +
			 												API_TEST_DEMO + "' }" } };
}
/**
 * Init the table demo applet.
 */
public void init() {

	setLayout( new BorderLayout() );

	init( getParameter( DEMO_KIND ) );
}
private void init( String p) {

	if (updater != null && updater.isAlive()) updater.running = false;

	if (p.equals( SINGLE_SELECTION_DEMO )) {
  
		initSingleSelection();
		
	} else if (p.equals( CHECKMARKS_DEMO )) {

		initCheckMarks();
		
	} else if (p.equals( MADNESS_DEMO )) {

		initMadness();

	} else if (p.equals( STRING_FORMATTING_DEMO )) {

		initStringFormatting();

	} else if (p.equals( COLUMN_WIDTH_ADJUSTMENT_DEMO )) {

		initColumnWidthAdjustment();

	} else if (p.equals( API_TEST_DEMO )) {

		initApiTest();

	} else if (p.equals( SYNCHRONIZATION_AND_SCROLLING_DEMO )) {

		initSynchronizationAndScrolling();
		
	} else if (p.equals( SORTING_AND_RESIZING_DEMO )) {

		initSortingAndResizing();
	}
}
/**
 * Code to initialize a table excercising the table API. It also shows
 * how to set up a context menu for the table.
 * 
 */
private void initApiTest() {

	// Init the column definitions vector and add a single ColumnDefinition.
	Vector colDefs = new Vector();
	ColumnDefinition col = new ColumnDefinition( "A Single Column" );
	col.setWidthInPixels( 2000 );
	colDefs.addElement( col );

	// Init the table, passing the columnDefinition vector and the (empty) data vector
	table = new Table( colDefs, rows );
	table.getDataArea().setBackground( Color.white );
	table.setSelectionMode( DataArea.SELECT_MANY );
	table.setShowCheckmarks( true );
	table.setHorizontalScrollMode( Table.HORIZONTAL_SCROLLBAR_OFF );
	DataArea.setReferenceColumnBorder( new BorderBottomRight() );
	DataArea.setReferenceColumnBackground( Color.white );
	table.getDataArea().setCheckingSelects( true );

	// Init the popup menu items and add appropriate action listeners.
	
	MenuItem item1 = new MenuItem( "Add 1 row" );
	item1.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			synchronized( rows ) {
				rows.addElement( new SimpleTableRow( new String[] { "row " +
					rows.size() } ));
				table.rowsAdded( 1 );
			}
			statusText.setText( "Done" );
		}
	} );
	MenuItem item2 = new MenuItem( "Add 50 rows" );
	item2.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			synchronized( rows ) {
				for (int i=0; i<50; i++) {
					rows.addElement( new SimpleTableRow( new String[] { "row " +
						rows.size() } ));
				}
				table.rowsAdded( 50 );
			}
			statusText.setText( "Done" );
		}
	} );	
	MenuItem item3 = new MenuItem( "Insert row above current selection" );
	item3.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			synchronized( rows ) {
				int i = table.getSelectedIndex();
				if (i < 0) { 
					statusText.setText( "No single current selection" );
				} else {
					rows.insertElementAt( new SimpleTableRow( new String[] { "row " +
						i } ), i );
					table.rowsInserted( i, 1 );
					statusText.setText( "Done" );
				}
			}
		}
	} );
	MenuItem item4 = new MenuItem( "Insert row below current selection" );
	item4.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			synchronized( rows ) {
				int i = table.getSelectedIndex();
				if (i < 0) { 
					statusText.setText( "No single current selection" );
				} else {
					rows.insertElementAt( new SimpleTableRow( new String[] { "row " +
						(i + 1) } ), i + 1 );
					table.rowsInserted( i + 1, 1 );
					statusText.setText( "Done" );
				}
			}
		}
	} );
	MenuItem item5 = new MenuItem( "Remove row above current selection" );
	item5.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			synchronized( rows ) {
				int i = table.getSelectedIndex();
				if (i < 0) { 
					statusText.setText( "No single current selection" );
				} else if (i == 0) {
					statusText.setText( "No such row" );
				} else {
					rows.removeElementAt( i - 1 );
					table.rowsRemoved( i - 1, 1 );
					statusText.setText( "Done" );
				}
			}
		}
	} );
	MenuItem item6 = new MenuItem( "Remove current selection" );
	item6.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			synchronized( rows ) {
				int i = table.getSelectedIndex();
				if (i < 0) { 
					statusText.setText( "No single current selection" );
				} else {
					rows.removeElementAt( i );
					table.rowsRemoved( i, 1 );
					statusText.setText( "Done" );
				}
			}
		}
	} );
	MenuItem item7 = new MenuItem( "Remove row below current selection" );
	item7.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			synchronized( rows ) {
				int i = table.getSelectedIndex();
				if (i < 0) { 
					statusText.setText( "No single current selection" );
				} else if (i + 1 >= rows.size()) {
					statusText.setText( "No such row" );
				} else {
					rows.removeElementAt( i + 1 );
					table.rowsRemoved( i + 1, 1 );
					statusText.setText( "Done" );
				}
			}
		}
	} );
	MenuItem item8 = new MenuItem( "Remove all" );
	item8.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			table.removeAllRows();
			statusText.setText( "Done" );
		}
	} );
	MenuItem item9 = new MenuItem( "Select all" );
	item9.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			table.selectAll();
			statusText.setText( "Done" );
		}
	} );
	MenuItem item10 = new MenuItem( "Deselect all" );
	item10.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			table.deselectAll();
			statusText.setText( "Done" );
		}
	} );
	MenuItem item11 = new MenuItem( "Check all" );
	item11.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			table.checkAll();
			statusText.setText( "Done" );
		}
	} );
	MenuItem item12 = new MenuItem( "Uncheck all" );
	item12.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			table.uncheckAll();
			statusText.setText( "Done" );
		}
	} );
	MenuItem item13 = new MenuItem( "Check selected rows" );
	item13.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			try {
				table.checkIndexes( table.getSelectedIndexes() );
				statusText.setText( "Done" );
			} catch (IllegalArgumentException ex) {
				statusText.setText( "IllegalArgumentException caught" );
			}	
		}
	} );
	MenuItem item14 = new MenuItem( "Select checked rows" );
	item14.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			try {
				table.selectIndexes( table.getCheckedIndexes() );
				statusText.setText( "Done" );
			} catch (IllegalArgumentException ex) {
				statusText.setText( "IllegalArgumentException caught" );
			}	
		}
	} );

	// Init the popup menu with the menu items created above.
	PopupMenu popup = new PopupMenu();
	popup.add( item1 );
	popup.add( item2 );
	popup.addSeparator();
	popup.add( item3 );
	popup.add( item4 );
	popup.add( item5 );
	popup.add( item6 );
	popup.add( item7 );
	popup.add( item8 );
	popup.addSeparator();
	popup.add( item9 );
	popup.add( item10 );
	popup.add( item11 );
	popup.add( item12 );
	popup.addSeparator();
	popup.add( item13 );
	popup.add( item14 );
	table.add( popup );

	// Add the table and labels to the applet
  	add( "Center", table );
  	add( "North", new Label( "Use the context menu commands (right mouse click) to test the API." ));
  	add( "South", statusText );

  	setBackground( Color.lightGray );
}
/**
 * Code to initialize a selection list with checkmarks.
 *
 * <p>This demo shows a table with two columns -- one containing a check mark
 * and the other one containing a string. This kind of table is often used
 * to select option settings.
 * 
 */
private void initCheckMarks() {

	// Init a ColumnDefinition for the table's one column.
	ColumnDefinition c = new ColumnDefinition( "Available Options" ); 	
	c.setWidthInPixels( 400 );	 // width of options column

	// Init the column definitions vector and add the ColumnDefinition defined above.
	Vector colDefs = new Vector();
	colDefs.addElement( c );

	// Init the data to be displayed by the table
	Vector lines = new Vector();
	for (int i=1; i<=5; ++i) {
		lines.addElement( new SimpleTableRow( new String[] { "Option " + i } ) );
	}

	// Set applet background (will be used by the table as the default background).
	setBackground( Color.white );

	// Init the table, passing the columnDefinition vector and the data vector
	table = new Table( colDefs, lines );

	// Set some table options to get the desired appearance.
	table.getTitlebar().setBackground( Color.lightGray );
	table.setHorizontalScrollMode( Table.HORIZONTAL_SCROLLBAR_OFF );
	table.setVerticalScrollMode( Table.VERTICAL_SCROLLBAR_ON );
	table.setShowCheckmarks( true );
	DataArea.setGridLinesColor( Color.darkGray );
	table.setSelectionMode( DataArea.SELECT_ZERO_OR_ONE );
	DataArea.setReferenceColumnBorder( 
		new tstuder.java.lib.graphics.BorderBottomRight() );
	DataArea.setReferenceColumnBackground( Color.white );
	
	
	// Add the table to the current container (the applet)
	
  	add( "Center", table );
}
/**
 * Code to initialize a table demonstrating the available column width adjustment
 * options. 
 * 
 */
private void initColumnWidthAdjustment() {

	// Initialize and populate the table.

	// First, instantiate the StringFormatter object. Will use a StringWrapFormatter
	// to demonstrate the automatic wrapping of column titles (column 5 in the table).
	//
	StringWrapFormatter formatter = new StringWrapFormatter( 
		StringFormatter.ALIGNMENT_LEFT, StringFormatter.ALIGNMENT_VCENTER );

	// Instantiate the five column definition objects.
	//
	ColumnDefinition cFixedPixels = new ColumnDefinition( "120 Pixels Wide",
		ColumnDefinition.WIDTH_FIXED_N_PIXELS, formatter, formatter );
	cFixedPixels.setWidthInPixels( 120 );

	ColumnDefinition cFixedZeroes = new ColumnDefinition( "The Width of 20 Zeroes",
		ColumnDefinition.WIDTH_FIXED_N_CHARS, formatter, formatter );
	cFixedZeroes.setWidthInChars( 20 );

	ColumnDefinition cAutoByTitle = new ColumnDefinition( "Width of Title",
		ColumnDefinition.WIDTH_AUTO_BY_TITLE, formatter, formatter );

	ColumnDefinition cAuto = new ColumnDefinition( "Width Automatic",
		ColumnDefinition.WIDTH_AUTO, formatter, formatter );

	// Init the column definitions vector and add the ColumnDefinitions defined above.
	Vector colDefs = new Vector();
	colDefs.addElement( cFixedPixels );
	colDefs.addElement( cFixedZeroes );
	colDefs.addElement( cAutoByTitle );
	colDefs.addElement( cAuto );
	colDefs.addElement( cAuto );

	// Init the data to be displayed by the table
	Vector lines = new Vector();
	lines.addElement( new SimpleTableRow( new String[] {
		"Cell 1,1", "Cell 1,2", "Cell 1,3", "Cell 1,4", "Cell 1,5" } ));
	lines.addElement( new SimpleTableRow( new String[] {
		"Cell 2,1", "12345678901234567890", "Cell 2,3", "Cell 2,4", 
		"A Rather Wide Cell" } ));	

	// Set applet background (will be used by the table as the default background).
	setBackground( new Color( 255, 255, 0xE0 ) );   // light yellow
	setForeground( new Color( 0xa5, 0x2a, 0x2a ) ); // brown

	// Init the table, passing the columnDefinition vector and the data vector
	table = new Table( colDefs, lines );

	// Set some table options to get the desired appearance.
	table.getTitlebar().setBackground( new Color( 0xff, 0xd7, 0x00 )); // gold
	table.getTitlebar().setForeground( Color.blue );

	table.setShowGrid( true );
	DataArea.setGridLinesColor( new Color( 0xa5, 0x2a, 0x2a ) );
	table.getTitlebar().setAdjustToFit( false );

	// Add the table and an adjustment button to the current container (the applet)

  	add( "Center", table );

	Button adjustmentButton = new Button( "Reset Column Widths" );
	add( "South", adjustmentButton );

	// Set the adjustment button's action listener.

	adjustmentButton.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			table.adjustColumnWidths();
			table.repaint();
		}} );
}
/**
 * Code to initialize a multi-colored, three column table with line numbers.
 *
 * <p>The table demonstrates the use of colors, fonts,
 * horizontal and vertical alignment, line numbers and
 * multiple selection support.
 *
 */
private void initMadness() {
	
	// Initialize the StringFormatters for the three columns (one each for the
	// column title and the other one for the column data).
	
	StringFormatter columnOneTitleFormatter = new StringWrapFormatter(
		StringFormatter.ALIGNMENT_LEFT, StringFormatter.ALIGNMENT_TOP );
	StringFormatter columnOneDataFormatter = new StringFormatter(
		StringFormatter.ALIGNMENT_RIGHT, StringFormatter.ALIGNMENT_BOTTOM );

	StringFormatter columnTwoTitleFormatter = new StringWrapFormatter(
		StringFormatter.ALIGNMENT_CENTER, StringFormatter.ALIGNMENT_VCENTER );
	StringFormatter columnTwoDataFormatter = new StringFormatter(
		StringFormatter.ALIGNMENT_CENTER, StringFormatter.ALIGNMENT_VCENTER );
	
	StringFormatter columnThreeTitleFormatter = new StringWrapFormatter(
		StringFormatter.ALIGNMENT_RIGHT, StringFormatter.ALIGNMENT_BOTTOM );
	StringFormatter columnThreeDataFormatter = new StringFormatter(
		StringFormatter.ALIGNMENT_LEFT, StringFormatter.ALIGNMENT_TOP );


	// Initialize the ColumnDefinition's for the three columns.
	
	ColumnDefinition column1 = new ColumnDefinition( "Left Column",
		columnOneTitleFormatter, columnOneDataFormatter );
	column1.setTitleFont( new Font( "Serif", Font.PLAIN, 14 ) );
	column1.setDataFont( new Font( "Monospaced", Font.PLAIN, 12) );
	column1.setTitleForeground( Color.red );
	column1.setTitleBackground( new Color( 255, 255, 200 ) );
	column1.setDataForeground( Color.blue );
	column1.setDataBackground( new Color( 255, 255, 200 ) );
	column1.setWidthInPixels( 160 ); 

	ColumnDefinition column2 = new ColumnDefinition( "Center Column",
		columnTwoTitleFormatter, columnTwoDataFormatter );
	column2.setTitleFont( new Font( "Serif", Font.ITALIC, 14 ) );
	column2.setDataFont( new Font( "Serif", Font.PLAIN, 16 ) );
	column2.setWidthInPixels( 160 ); 

	ColumnDefinition column3 = new ColumnDefinition( "Right Column",
		columnThreeTitleFormatter, columnThreeDataFormatter );
	column3.setTitleFont( new Font( "Serif", Font.BOLD, 14 ) );
	column3.setDataFont( new Font( "SansSerif", Font.PLAIN, 24 ) );
	column3.setTitleForeground( Color.blue );
	column3.setTitleBackground( new Color( 255, 200, 255 ) );
	column3.setDataForeground( Color.green );
	column3.setDataBackground( new Color( 255, 200, 255 ) );
	column3.setWidthInPixels( 160 ); 

	
	// Init the column definitions vector and add the ColumnDefinition defined above.
	Vector colDefs = new Vector();
	colDefs.addElement( column1 );
	colDefs.addElement( column2 );
	colDefs.addElement( column3 );

	// Init the data to be displayed by the table
	Vector lines = new Vector();
	for (int i=1; i<=20; ++i) {
		if (i % 2 == 0) {
			lines.addElement( new ColorTableRow( new String[] { "Custom Color Field " + i,
				"Custom Color Entry " + i,
				"Custom Color Cell " + i } ) );
		} else {
			lines.addElement( new SimpleTableRow( new String[] { "Default Color Field " + i,
				"Default Color Entry " + i,
				"Default Color Cell " + i } ) );
		}		
	}

	// Init the table, passing the columnDefinition vector and the data vector
	table = new Table( colDefs, lines );

	// Set some table options to get the desired appearance.
	table.getTitlebar().setBackground( Color.lightGray );
	table.setShowLineNumbers( true );
	table.getDataArea().setBackground( Color.lightGray );
	DataArea.setGridLinesColor( Color.darkGray );
	table.setSelectionMode( DataArea.SELECT_MANY_CONTINUOUS );
	table.setBackground( Color.lightGray );
	
	
	// Add the table to the current container (the applet)
	
  	add( "Center", table );

	Button enableToggleButton = new Button( "Toggle enabled state" );
	add( "South", enableToggleButton );

	// Set the adjustment button's action listener.

	enableToggleButton.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			table.setEnabled( ! table.isEnabled() );
		}} 
	);
}
/**
 * Code to initialize a simple selection list.
 *
 * <p>This demo shows a table with a single column on a white background.
 * Only one line can be selected at a time.
 * 
 */
private void initSingleSelection() {

	// Init a ColumnDefinition for the table's one column.
	ColumnDefinition c = new ColumnDefinition( "" ); 	// no title
	c.setWidthInPixels( 200 );									// width of column

	// Init the column definitions vector and add the ColumnDefinition defined above.
	Vector colDefs = new Vector();
	colDefs.addElement( c );

	// Init the data to be displayed by the table
	Vector lines = new Vector();
	for (int i=1; i<=50; ++i) {
		lines.addElement( new SimpleTableRow( new String[] { "Choice " + i } ) );
	}

	// Set applet background (will be used by the table as the default background).
	setBackground( Color.white );

	// Init the table, passing the columnDefinition vector and the data vector
	table = new Table( colDefs, lines );

	// Set some table options to get the desired appearance.
	table.setBorder( new tstuder.java.lib.graphics.BorderSingle() );
	table.setShowTitlebar( false );
	table.setHorizontalScrollMode( Table.HORIZONTAL_SCROLLBAR_OFF );
	table.setShowGrid( false );
	table.setSelectionMode( DataArea.SELECT_ONE );

	// Add the table to the current container (the applet)
	
  	add( "Center", table );
}
/**
 * Code to initialize a table demonstrating the available string
 * formatting options.
 * 
 */
private void initSortingAndResizing() {

	// Instantiate the StringFormatter objects.
	//
	StringFormatter fLeft = new StringFormatter( 
		StringFormatter.ALIGNMENT_LEFT, StringFormatter.ALIGNMENT_VCENTER );

	StringFormatter fCenter = new StringFormatter( 
		StringFormatter.ALIGNMENT_CENTER, StringFormatter.ALIGNMENT_VCENTER );

	StringFormatter fRight = new StringFormatter( 
		StringFormatter.ALIGNMENT_RIGHT, StringFormatter.ALIGNMENT_VCENTER );

	// Instantiate the four column definition objects.
	//
	ColumnDefinition cLeft = new ColumnDefinition( "Sort and resize",
		fLeft, fLeft );

	ColumnDefinition cCenter = new ColumnDefinition( "Sort and resize",
		fCenter, fCenter );

	ColumnDefinition cRight = new ColumnDefinition( "No resizing",
		fRight, fRight );

	ColumnDefinition cNoSort = new ColumnDefinition( "No sorting",
		fLeft, fLeft );

	// Init the column definitions vector and add the ColumnDefinitions defined above.
	Vector colDefs = new Vector();
	colDefs.addElement( cLeft );
	colDefs.addElement( cCenter );
	colDefs.addElement( cRight );
	colDefs.addElement( cNoSort );

	// Init the data to be displayed by the table
	Vector lines = new ColumnSortableVector();
	lines.addElement( new SimpleSortableTableRow( new String[] {
		"1", "c", "Peter", "active" } ));
	lines.addElement( new SimpleSortableTableRow( new String[] {
		"2", "a", "Alice", "active" } ));
	lines.addElement( new SimpleSortableTableRow( new String[] {
		"3", "b", "Sara", "active" } ));

	// cmp commented out for now to fix compile error
	//lines.addElement( new SimpleSortableTableRow( new String[] {
	//	"4", "�", "Quentin", "active" } ));

	// Set applet background (will be used by the table as the default background).
	setBackground( Color.white );

	// Init the table, passing the columnDefinition vector and the data vector
	table = new Table( colDefs, lines );

	// Set some table options to get the desired appearance.
	table.setShowCheckmarks( true );
	table.setShowLineNumbers( true );
	table.setSelectionMode( DataArea.SELECT_MANY );
	table.getTitlebar().setBackground( Color.lightGray );
	table.getTitlebar().setColumnSortableFlags( new boolean[] {
		true, true, true, false } );
	table.getTitlebar().setColumnResizeableFlags( new boolean[] {
		true, true, false, true } );

	// Add the table to the current container (the applet)
	
  	add( "Center", table );
}
/**
 * Code to initialize a table demonstrating the available string
 * formatting options.
 * 
 */
private void initStringFormatting() {

	// Instantiate the StringFormatter object for each of the five columns of the table.
	//
	StringWrapFormatter fLeft = new StringWrapFormatter( 
		StringFormatter.ALIGNMENT_LEFT, StringFormatter.ALIGNMENT_VCENTER );

	StringWrapFormatter fCenter = new StringWrapFormatter( 
		StringFormatter.ALIGNMENT_CENTER, StringFormatter.ALIGNMENT_VCENTER );

	StringWrapFormatter fRight = new StringWrapFormatter( 
		StringFormatter.ALIGNMENT_RIGHT, StringFormatter.ALIGNMENT_VCENTER );

	StringWrapFormatter fTabs = new StringWrapFormatter( 
		StringFormatter.ALIGNMENT_LEFT_WITH_TABS, StringFormatter.ALIGNMENT_VCENTER,
		new int[] { 7, 7 }, 0 );

	StringWrapFormatter fDecimal = new StringWrapFormatter( 
		StringFormatter.ALIGNMENT_RIGHT_DECIMAL, StringFormatter.ALIGNMENT_VCENTER, null, 3 );

	// Instantiate the five column definition objects.
	//
	ColumnDefinition cLeft = new ColumnDefinition( "Left aligned text",
		ColumnDefinition.WIDTH_FIXED_N_CHARS, fLeft, fLeft );
	cLeft.setWidthInChars( 12 );

	ColumnDefinition cCenter = new ColumnDefinition( "Centered text",
		ColumnDefinition.WIDTH_FIXED_N_CHARS, fCenter, fCenter );
	cCenter.setWidthInChars( 12 );

	ColumnDefinition cRight = new ColumnDefinition( "Right aligned text",
		ColumnDefinition.WIDTH_FIXED_N_CHARS, fRight, fRight );
	cRight.setWidthInChars( 12 );

	ColumnDefinition cTabs = new ColumnDefinition( "Text with tabs",
		ColumnDefinition.WIDTH_FIXED_N_CHARS, fLeft, fTabs );
	cTabs.setWidthInChars( 20 );

	ColumnDefinition cDecimal = new ColumnDefinition( "Decimal aligned numbers",
		ColumnDefinition.WIDTH_FIXED_N_CHARS, fLeft, fDecimal );
	cDecimal.setWidthInChars( 12 );

	// Init the column definitions vector and add the ColumnDefinitions defined above.
	Vector colDefs = new Vector();
	colDefs.addElement( cLeft );
	colDefs.addElement( cCenter );
	colDefs.addElement( cRight );
	colDefs.addElement( cTabs );
	colDefs.addElement( cDecimal );

	// Init the data to be displayed by the table
	Vector lines = new Vector();
	lines.addElement( new SimpleTableRow( new String[] {
		"left", "center", "right", "iii\twww\t123", "0" } ));
	lines.addElement( new SimpleTableRow( new String[] {
		"left", "center", "right", "www\tiii\t456", "1.5" } ));
	lines.addElement( new SimpleTableRow( new String[] {
		"left", "center", "right", "a\tb\tc", "1234.142" } ));
	lines.addElement( new SimpleTableRow( new String[] {
		"left", "center", "right", "a\tb\tc", "1234.142333" } ));

	// Set applet background (will be used by the table as the default background).
	setBackground( Color.lightGray );

	// Init the table, passing the columnDefinition vector and the data vector
	table = new Table( colDefs, lines );

	// Set some table options to get the desired appearance.
	//table.getTitlebar().setBackground( Color.darkGray );
	table.getTitlebar().setFont( new Font( "SansSerif", Font.PLAIN, 11 ) );

	table.setShowGrid( true );
	DataArea.setGridLinesColor( Color.white );
	table.getDataArea().setFont( new Font( "SansSerif", Font.PLAIN, 11 ) );
	table.setSelectionMode( DataArea.SELECT_MANY );
	table.setVerticalScrollMode( Table.VERTICAL_SCROLLBAR_ON );
	table.setHorizontalScrollMode( Table.HORIZONTAL_SCROLLBAR_ON );
	table.getTitlebar().setAdjustToFit( false );

	// Add the table to the current container (the applet)
	
  	add( "Center", table );
}
/**
 * Code to initialize a table demonstrating the table's support for multi-threaded
 * updates to table data. It also demonstrates how to register
 * an ActionListener on the table and how to use the table to initialize
 * a single selection choice list with an ItemListener.
 * 
 */
private void initSynchronizationAndScrolling() {

	// Init the ColumnDefinition objects passing the column titles in the
	// constructor call.
	ColumnDefinition c1 = new ColumnDefinition( "First Name" );
	ColumnDefinition c2 = new ColumnDefinition( "Last Name" );
	ColumnDefinition c3 = new ColumnDefinition( "Street Address" );
	ColumnDefinition c4 = new ColumnDefinition( "City" );
	ColumnDefinition c5 = new ColumnDefinition( "Phone" );

	// Set the City and Phone columns to the width of 12 characters (widths of 
	// '0' (zero), actually).
	c4.setWidthInChars( 12 );
	c5.setWidthInChars( 12 );
	
	// Init the column definitions vector and add the ColumnDefinitions defined above.
	Vector colDefs = new Vector();
	colDefs.addElement( c1 );
	colDefs.addElement( c2 );	
	colDefs.addElement( c3 );	
	colDefs.addElement( c4 );	
	colDefs.addElement( c5 );	

	// Create an action listener object to delete the currently selected rows.
	ActionListener removeRowsListener = new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			table.removeSelectedRows();
		}
	};

	// Init the table, passing the columnDefinition vector and the data vector
	table = new Table( colDefs, rows );
	table.getDataArea().setBackground( Color.white );
	table.setSelectionMode( DataArea.SELECT_MANY );
	table.setShowCheckmarks( true );
	DataArea.setReferenceColumnBorder( new BorderRaisedWithLine() );
	table.addActionListener( removeRowsListener );
	table.setBackground( Color.lightGray );

	// Init the popup menu and add it to the table.
	MenuItem removeRowsItem = new MenuItem( "Remove selected rows" );
	removeRowsItem.addActionListener( removeRowsListener );
	PopupMenu popup = new PopupMenu();
	popup.add( removeRowsItem );
	table.add( popup );

  	add( "Center", table );
  	add( "North", new Label( "Use Enter key or context menu to remove selected rows." ));

  	// Initialize the selection list (also a Table) with the four modes
  	// of operation for the background updater thread.

  	// The list plus a title will be added to the applet in a separate panel
  	Panel p = new Panel( new BorderLayout() );

  	// Add the label
  	p.add( "North", new Label( "Background thread is..." ) );

  	// Initialize the list with the four modes of operation
	Vector choiceColumnVector = new Vector();
	ColumnDefinition choiceColumn	= new ColumnDefinition( "" );
	choiceColumn.setWidthInPixels( 2048 );
	choiceColumnVector.addElement( choiceColumn );

	// The four background thread modes (each making up one row of the list)
	Vector modes = new Vector();
	modes.addElement( new SimpleTableRow( new String[] { "doing nothing" } ));
	modes.addElement( new SimpleTableRow( new String[] { "inserting rows randomly" } ));
	modes.addElement( new SimpleTableRow( new String[] { "removing rows randomly" } ));
	modes.addElement( new SimpleTableRow( new String[] { "updating rows randomly" } ));

	Table threadModeChoices = new Table( choiceColumnVector, modes );
	threadModeChoices.setShowTitlebar( false );
	threadModeChoices.setSelectionMode( DataArea.SELECT_ONE );
	threadModeChoices.setBackground( Color.white );
	threadModeChoices.setHorizontalScrollMode( Table.HORIZONTAL_SCROLLBAR_OFF );
	threadModeChoices.setShowGrid( false );

	p.add( "South", threadModeChoices );
	
	// Instantiate the updater thread passing the target table for the updates
	// and the selection list with the update modes.

	updater = new TableUpdater( table, threadModeChoices );
	updater.start();
	
	add( "South", p );

	setBackground( Color.lightGray );
}
/**
 * Table demo's main() (if not run as an applet). Provide one of the "DemoKind"
 * applet parameter values on the command line.
 */
public static void main(String args[]) {

	TableDemo demo = new TableDemo();
	demo.setLayout( new BorderLayout() );

	if (args.length > 0) {
		demo.init( args[0] );
	} else {
		demo.init( SINGLE_SELECTION_DEMO );
	}

	final Frame f = new Frame();

	Button closeButton = new Button( "Close" );
	
	closeButton.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			f.dispose();
		}} );
	
	f.setLayout( new BorderLayout() );
	f.setSize( 600, 500 );
	f.add( "Center", demo );

	f.add( "South", closeButton );

	f.setVisible( true );
}
}
