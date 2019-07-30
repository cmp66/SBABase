package com.wahoo.apba.web.util;


import java.awt.Color;

import tstuder.java.lib.component.table.SimpleSortableTableRow;
import tstuder.java.lib.component.table.TableRow;
import tstuder.java.lib.util.ColumnSortable;


////
// CLASS: AlarmRow
////
public class MemberRow extends SimpleSortableTableRow implements TableRow
{

    protected static final Color    BACKGROUND_UNSELECTED_COLOR = new Color(212, 219, 219); // light grey
    protected static final Color    BACKGROUND_SELECTED_COLOR = new Color(104, 138, 175);	// dark blue

    protected Color[]  _backgroundColors;

    protected String[]      _strings = null;
    protected Color[]       _foregroundColors = null;

    public static final int      NAME_IX = 0;
    
    private String     _id = "";

    protected static final Color    NAME_COLOR = Color.black;
    protected static final Color    TEXT_UNSELECTED_COLOR = Color.black;
    protected static final Color    TEXT_SELECTED_COLOR = Color.white;

    ////
    // CONSTRUCTOR
    ////
    public MemberRow(String[] inStrings)
    {
        super(inStrings);
        _strings = inStrings.clone();
        return;
    }

    ////
    // CONSTRUCTOR
    ////
    public MemberRow(String[] inStrings, Color[] inColors)
    {
        super(inStrings);
        
        String[] stringCopy = inStrings.clone();
        Color[] colorCopy = inColors.clone();
        _strings = stringCopy;
        _foregroundColors = colorCopy;
        return;
    }

    ////
    // CONSTRUCTOR
    ////
    public MemberRow(ListMember inMember)
    {
        super(inMember.getStrings());
        int lclSize = inMember.getNumberofColumns();
        //ListMember lclMember = null;
        
        _strings = new String[ lclSize ];
        _foregroundColors = new Color[ lclSize ];
        
        _backgroundColors = new Color[ lclSize ];
        for (int ix = 0; ix < lclSize; ++ix)
            _backgroundColors[ ix ] = BACKGROUND_UNSELECTED_COLOR;
            
            // First column has to be name, others columns are variable based upon page content
        _foregroundColors[ NAME_IX ] = NAME_COLOR;
        
        _strings[ NAME_IX ] = (String) inMember.getDisplayStrings().elementAt(NAME_IX);
        
        for (int ix = 1; ix < lclSize; ++ix)
        {
            _foregroundColors[ ix ] = TEXT_UNSELECTED_COLOR;

            _strings[ ix ] = (String) inMember.getDisplayStrings().elementAt(ix);
        }
        
        _id = inMember.getId();
        return;
    }

    ////
    // METHOD: getBackgrounds
    ////
    public Color[] getBackgrounds()
    {
        Color[] copy = _backgroundColors.clone();
        
        return copy;
    }

    ////
    // METHOD: getForegrounds
    ////
    public Color[] getForegrounds()
    {
        Color[] copy = _foregroundColors.clone();
        
        return copy;
    }

    ////
    // METHOD: getStrings
    ////
    public String[] getStrings()
    {
        String[] copy = _strings.clone();
        
        return copy;
    }
    
    ////
    // METHOD: getId
    ////
    public String getId()
    {
        return _id;
    }  
    
    public void setSelected(boolean inSelected)
    {
        Color lclNewBackgroundColor = inSelected ? BACKGROUND_SELECTED_COLOR : BACKGROUND_UNSELECTED_COLOR;
        Color lclNewForegroundColor = inSelected ? TEXT_SELECTED_COLOR : TEXT_UNSELECTED_COLOR;
        
        for (int ix = 0; ix < _backgroundColors.length; ++ix)
            _backgroundColors[ ix ] = lclNewBackgroundColor;

        for (int ix = 0; ix < _foregroundColors.length; ++ix)
            _foregroundColors[ ix ] = lclNewForegroundColor;
    }
    
    public int mycompare(Object inRow1, Object inRow2, int inRow)
    {
        MemberRow lclMemberRow1 = (MemberRow) inRow1;
        MemberRow lclMemberRow2 = (MemberRow) inRow2;
        
        String lclRow1String = lclMemberRow1.getStrings()[inRow];
        String lclRow2String = lclMemberRow2.getStrings()[inRow];
        
        int lclCompareResult = lclRow1String.compareTo(lclRow2String);
        
        if (lclCompareResult == 0)
        {
            return ColumnSortable.EQUAL;
        }
        else if (lclCompareResult > 0)
        {
            return ColumnSortable.LESS_THAN;
        }
        else
        {
            return ColumnSortable.GREATER_THAN;
        }
        
    }        
}
