package com.wahoo.apba.web.util;


import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import tstuder.java.lib.component.table.ColumnDefinition;
import tstuder.java.lib.component.table.ColumnSortEvent;
import tstuder.java.lib.component.table.DataArea;
import tstuder.java.lib.component.table.Table;
import tstuder.java.lib.component.table.Titlebar;
import tstuder.java.lib.graphics.BorderBottomRight;
import tstuder.java.lib.graphics.StringFormatter;
import tstuder.java.lib.graphics.StringWrapFormatter;
import tstuder.java.lib.util.ColumnSortableVector;

import com.wahoo.util.Misc;


////
// CLASS AlarmNotificationApplet
////
public class SelectedListApplet extends Applet implements ItemListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	private static int MAX_ROWS = 100;
    private static int TRY_SESSION = 10;    // number of times to try to attach to the running session

    private static Color TEXT_COLOR = Color.black;
    protected static final Color    BACKGROUND_COLOR = new Color(212, 219, 219); // light grey    

    private static Color  _dataAreaColor = new Color(212, 219, 219);	// light grey
    private static Color  _headingColor = new Color(104, 138, 175);	// dark blue
    private static Font	  _headingFont = new Font("SansSerif", Font.BOLD, 12);
    private static Font	  _dataFont = new Font("SansSerif", Font.PLAIN, 11);
    
    // Column widths: all are stdcol width, except the message column.
    private static int STDCOL_WIDTH = 60;
    private static int SELECTCOL_WIDTH = 10;
    
    private static String _SELECTED = "Selected";

    Vector<ColumnDefinition> _columnDefs = new Vector<ColumnDefinition>(1, 1);
    @SuppressWarnings("unchecked")
    Vector _tableRows = new ColumnSortableVector(10, 10);
    
    Table  _table = null;

    boolean _isStarted = false;
    boolean _membersPreloaded = false;
    
    int     _sortColumn = 0;
    boolean _sortAscending = true;
    
    // Various input parameters to the applet
    private int      _teamid = 0;
    private String   _columnName = null;
    
    Vector<ListMember>_memberList = null;
    Vector<String>   _columnHeadings = null;
    //Hashtable       _idHashTable = null;
    String          _memberListString = null;

    StringBuffer    _textBuff = new StringBuffer();

        
    ////
    // CONSTRUCTOR
    ////
    public SelectedListApplet()
    {   
        return;
    }

    ////
    // METHOD: init
    ////
    public void init()
    {
        // Get the parameters
        getparams();

        _isStarted = false;
           
        return;

    }

    ////
    // METHOD: getparams
    ////
    protected void getparams()
    {

        _teamid = Integer.parseInt(getParameter("TeamId"));
        _columnName = getParameter("ColumnName");
        return;
    }

    /**
     * Item listener needs to implement itemStateChanged; will tell us when
     * an item has been selected.
     */
    public void itemStateChanged(ItemEvent e)
    {

        int lclSelectedRow = _table.getSelectedIndex();  
        
        try
        {
            // If we are showing only selected items, clicking on an item removes it.
            MemberRow lclRow = (MemberRow) _table.getRows().elementAt(lclSelectedRow);
            String lclId = lclRow.getId();
            Enumeration<ListMember> lclMemberEnum = _memberList.elements();
            //boolean lclFound = false;
        
            while (lclMemberEnum.hasMoreElements())
            {
                ListMember lclSelectedEntry = lclMemberEnum.nextElement();

                if (lclId.equals(lclSelectedEntry.getId()))
                {
                    _memberList.removeElement(lclSelectedEntry);
                    ((SelectionListApplet) (getAppletContext().getApplet("ResourceSelectApplet"))).deSelect(lclId);
                }
            }
            
            _table.removeSelectedRows();
            _table.deselectAll();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Action Listener needs to implement actionPerformed; will tell us when
     * other actions happen.
     */
    public void actionPerformed(ActionEvent e)
    {

        int lclSelectedRow = _table.getSelectedIndex();
        
        try
        {

            // If we are showing only selected items, clicking on an item removes it.
            MemberRow lclRow = (MemberRow) _table.getRows().elementAt(lclSelectedRow);
            String lclId = lclRow.getId();
            Enumeration<ListMember> lclMemberEnum = _memberList.elements();
            //boolean lclFound = false;
        
            while (lclMemberEnum.hasMoreElements())
            {
                ListMember lclSelectedEntry = (ListMember) lclMemberEnum.nextElement();

                if (lclId.equals(lclSelectedEntry.getId()))
                {
                    ((SelectionListApplet) (getAppletContext().getApplet("ResourceSelectApplet"))).deSelect(lclId);
                    _memberList.removeElement(lclSelectedEntry);
                }
            }
            _table.removeSelectedRows();
            _table.deselectAll();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public String getSelectedList()
    {
        StringBuffer lclSelectedList = new StringBuffer(80);
        
        // If this applet is showing selected items, construct a comma seperated list of all the id's of the 
        // selected items. 
        Enumeration<ListMember> lclSelectedMembers = _memberList.elements();
            
        if (lclSelectedMembers.hasMoreElements())
            lclSelectedList.append(((ListMember) lclSelectedMembers.nextElement()).getId());
                
        while (lclSelectedMembers.hasMoreElements())
        {
            lclSelectedList.append(",").append(((ListMember) lclSelectedMembers.nextElement()).getId());
        }
        
        return lclSelectedList.toString();
    }
    
    ////
    // METHOD: start
    ////
    public void start()
    {
        if (_isStarted == false)
        {
            getMemberListFromServer();
            displayMemberList();

            _isStarted = true;            
        }

        synchronized (this)
        {           
            _membersPreloaded = false;
        }              

        return;
    }

    ////
    // METHOD: stop
    ////
    public void stop()
    {        
        _isStarted = false;
                
        return;
    }

    ////
    // METHOD: destroy
    ////
    public void destroy()
    {
        return;
    }
    

    public void showSelectedItems()
    { 
        System.out.println("GOT IT");

        ((SelectionListApplet) (getAppletContext().getApplet("ResourceSelectApplet"))).sendSelectedToApplet();
    }
        
    
    @SuppressWarnings("unused")
	private void deSelect(String inId)
    {
        Enumeration<Object> lclMembers = _table.getRows().elements();
            
        MemberRow lclRow = null;

        while (lclMembers.hasMoreElements())
        {
            lclRow = (MemberRow) lclMembers.nextElement();
            if (lclRow.getId().equals(inId))
            {
                lclRow.setSelected(false);
                break;
            }
        }        
        refreshListDisplay();       
    }
        
    ////
    // METHOD: getMemberListFromServer
    ////
    public void getMemberListFromServer()
    {
        //URL lclUrl = null;
        //String lclUrlString = createURLStringForListServlet();

        //  Only the applet containing all the available members gets the master list from the server.  The server
        //  which is called will send two serialized objects.  The first contains a vector of strings which are used
        //  as column headings.  The second is a Vector of ListMembers of all possible resources of the desired type
        //  which the user is allowed to view.
        try
        {
            //lclUrl = new URL(lclUrlString);
            // get memberlist to populate the table
            //HttpMessage lclMsg = new HttpMessage(lclUrl);
            //InputStream lclIn = lclMsg.sendGetMessage();
            //ObjectInputStream lclResult = new ObjectInputStream(new BufferedInputStream (lclIn, 6000));
            
            _columnHeadings = new Vector<String>();
            _columnHeadings.addElement(_columnName);
            //_columnHeadings = (Vector) lclResult.readObject();
            
            if (_memberList == null)
            {
                _memberList = new Vector<ListMember>(10, 1);
            }
        }
        catch (Exception lclException)
        {
            lclException.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void addSelected(ListMember inNewRow)
    {
        // This function add a member to a selected list of items.  A check is first made to see if the item is already in 
        // in the list.
        if (_memberList == null)
            _memberList = new Vector<ListMember>(1, 1);
            
        Enumeration<ListMember> lclMemberEnum = _memberList.elements();
        boolean lclFound = false;
        int lclIdx = Integer.parseInt(inNewRow.getId());
        
        while (lclMemberEnum.hasMoreElements())
        {
            ListMember lclSelectedEntry = lclMemberEnum.nextElement();

            if (Integer.parseInt(lclSelectedEntry.getId()) != lclIdx)
            {
                continue;
            }
            
            lclFound = true;
            break;
        }
        
        if (!lclFound)
        {   
            _memberList.addElement(inNewRow);
            MemberRow lclMemberRow = new MemberRow(inNewRow);

            _table.getRows().addElement(lclMemberRow);
            _table.rowsUpdated(0, _table.getRows().size());
            
        }
    }

    public void clearSelectedList()
    {
        Enumeration<ListMember> lclMemberEnum = _memberList.elements();
        
        while (lclMemberEnum.hasMoreElements())
        {
            ListMember lclSelectedEntry = (ListMember) lclMemberEnum.nextElement();
 
            String lclId = lclSelectedEntry.getId();
            ((SelectionListApplet) (getAppletContext().getApplet("ResourceSelectApplet"))).deSelect(lclId);            
            
            _memberList.removeAllElements();
        }          
        
        clearTable();
        displayMemberList();
    }
    
    public void refreshListDisplay()
    {
        _table.sortColumn(new ColumnSortEvent(_table.getTitlebar(), _sortColumn, _table.getTitlebar().getSortAscending()));   
        validate();
        invalidate();
        
        paint(getGraphics());
    }
    
    ////
    // METHOD: displayMemberList
    ////
    @SuppressWarnings("unchecked")
    public void displayMemberList()
    {
        Enumeration<ListMember> lclMemberEnum = _memberList.elements();
        MemberRow lclMemberRow = null;
        
        if (_table == null)
        {
            getTable();
        }       
        while (lclMemberEnum.hasMoreElements())
        {       

            synchronized (this)
            {
                lclMemberRow = new MemberRow((ListMember) lclMemberEnum.nextElement());
                if (true)
                {
                    _table.getRows().addElement(lclMemberRow);
                }
                        
            }
        }

        _table.rowsUpdated(0, _table.getRows().size());
        _table.sortColumn(new ColumnSortEvent(_table.getTitlebar(), _sortColumn, _sortAscending));
        validate();
        invalidate();

        setVisible(true);
        
        paint(getGraphics());
    }

    /**
     * Create the AWT Table class to display the alarms.
     */
    @SuppressWarnings("unchecked")
    private void getTable()
    {

        StringWrapFormatter fLeft = new StringWrapFormatter(StringFormatter.ALIGNMENT_LEFT, StringFormatter.ALIGNMENT_VCENTER);

        fLeft.setWrapEnabled(true);
        boolean[] lclSortFlags = new boolean[_columnHeadings.size()];

        // Get the headings for each column
        for (int i = 0; i < _columnHeadings.size(); i++)
        {
            ColumnDefinition lclColDef = new ColumnDefinition((String) _columnHeadings.elementAt(i),
                    ColumnDefinition.WIDTH_FIXED_N_CHARS, fLeft, fLeft);

            lclColDef.setTitleFont(_headingFont);
            lclColDef.setDataFont(_dataFont);
            lclColDef.setTitleBackground(_headingColor);
            lclColDef.setTitleForeground(Color.white);
            lclColDef.setWidthInChars(STDCOL_WIDTH);
	        
            lclSortFlags[i] = true;

            _columnDefs.addElement(lclColDef);
        }

        // Init the table, passing the columnDefinition vector and the (empty) data vector
        _table = new Table(_columnDefs, _tableRows);
        _table.getDataArea().setBackground(BACKGROUND_COLOR);
        _table.setSelectionMode(DataArea.SELECT_ONE);
        _table.setShowCheckmarks(false);
        _table.setHorizontalScrollMode(Table.HORIZONTAL_SCROLLBAR_ON);
        DataArea.setReferenceColumnBorder(new BorderBottomRight());
        DataArea.setReferenceColumnBackground(_dataAreaColor);
        DataArea.setReferenceColumnFont(_dataFont);
        //_table.getDataArea().setReferenceColumnBorder(new BorderBottomRight());
        //_table.getDataArea().setReferenceColumnBackground(_dataAreaColor);
        _table.getDataArea().setCheckingSelects(true);
        //_table.getDataArea().setReferenceColumnFont(_dataFont);
        //_table.setShowGrid(true);
        setLayout(new GridLayout());
        add(_table);
        _table.addItemListener(this);
        _table.addActionListener(this);
        setSortColumn(_sortColumn);
        setSortAscending(_sortAscending);
        Titlebar lclTitle = _table.getTitlebar();

        lclTitle.setColumnSortableFlags(lclSortFlags);
        lclTitle.setCanSort(true);
        lclTitle.setSortColumn(_sortColumn);
        lclTitle.setSortAscending(_sortAscending);
	    
    }

    public void clearTable()
    {
        if (null != _table)
        {
            //int lclSize = _table.getRows().size();

            _table.getRows().removeAllElements();
        }
        
    }
    
    ////
    // METHOD: getTopFrame
    ////
    Frame getTopFrame()
    {
        Component prev = this;
        Component curr = prev.getParent();

        while (null != curr)
        {
            prev = curr;
            curr = prev.getParent();
        }

        return (Frame) prev;
    }

    private String createURLStringForListServlet()
    {
        URL lclURL = getDocumentBase();
        String lclNewStr = "";

        lclNewStr = Misc.makeUrl(lclURL, "DataServlet?tid=" + _teamid + "");

        return lclNewStr;
    }


                
    public void setMemberList(Vector<ListMember> inList)
    {
        _memberList = inList;
    }
    
    public Vector<ListMember> getMemberList()
    {
        return _memberList;
    }              
    
    public void setColumnHeadings(Vector<String> inNames)
    {
        _columnHeadings = inNames;
    }
    
    public Vector<String> getColumnHeadings()
    {
        return _columnHeadings;
    }
    
    public void setTeamId(int inId)
    {
        _teamid = inId;
    }
    
    public int getTeamId()
    {
        return _teamid;
    }

    public Table getActiveTable()
    {
        return _table;
    }    
    
    public void setSortColumn(int inColumn)
    {
        _sortColumn = inColumn;
    }
    
    public void setSortAscending(boolean inFlag)
    {
        _sortAscending = inFlag;
    }
}
