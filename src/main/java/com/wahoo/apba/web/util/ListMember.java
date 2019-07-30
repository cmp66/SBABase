package com.wahoo.apba.web.util;


import java.io.Serializable;
import java.util.Vector;


public class ListMember implements Serializable
{

	private static final long serialVersionUID = 1L;
	private Vector<String> _displayStrings;
    private String[] _strings = new String[20];
    int     index = 0;
    
    private boolean _selected;
    private int     _thresholdIdx;
    private String  _id;
    
    public ListMember()
    {
        _selected = false;
        _id = "";
        _displayStrings = new Vector<String>();
    }
    
    public void setSelected(boolean inState)
    {
        _selected = inState;
    }
    
    public boolean getSelected()
    {
        return _selected;
    }
    
    public void setId(String inId)
    {
        _id = inId;
    }
    
    public String getId()
    {
        return _id;
    }
    
    public Vector<String> getDisplayStrings()
    {
        return _displayStrings;
    }
    
    public String[] getStrings()
    {
        String[] copy = _strings.clone();
        return copy;
    }
    
    public void addDisplayString(String inColumnData)
    {
        _displayStrings.addElement(inColumnData);
        _strings[index] = inColumnData;
        index++;
    }
    
    public int getNumberofColumns()
    {
        return _displayStrings.size();
    }
}    

    
