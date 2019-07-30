package com.wahoo.apba.web.util;


import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

import tstuder.java.lib.component.table.*;
import tstuder.java.lib.graphics.*;
import tstuder.java.lib.util.ColumnSortableVector;
import tstuder.java.lib.component.table.Titlebar;

import com.oreilly.servlet.HttpMessage;
import com.wahoo.apba.web.util.ListMember;
import com.wahoo.apba.web.util.MemberRow;
import com.wahoo.util.Misc;


////
// CLASS AlarmNotificationApplet
////
public class SelectionListApplet extends Applet implements ItemListener, ActionListener
{
    /**
	 * 
	 */
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

    
    Vector<ColumnDefinition> _columnDefs = new Vector<ColumnDefinition>(1, 1);
    @SuppressWarnings("unchecked")
    Vector _tableRows = new ColumnSortableVector(10, 10);
    
    Table  _table = null;

    boolean _isStarted = false;
    boolean _membersPreloaded = false;
    
    int     _sortColumn = 0;
    boolean _sortAscending = true;
    
    // Various input parameters to the applet
    private String   _columnName = null;
    public int       _teamid = 1;
    public int       _year = 2003;
    private String   _action = null;
    
    Vector<ListMember>_memberList = null;
    Vector<String>  _columnHeadings = null;
    Hashtable<String, ListMember> _idHashTable = null;
    String          _memberListString = null;

    StringBuffer    _textBuff = new StringBuffer();

    String          _filter = "";
        
    ////
    // CONSTRUCTOR
    ////
    public SelectionListApplet()
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
        _year = Integer.parseInt(getParameter("Year"));
        _columnName = getParameter("ColumnName");
        _action = getParameter("action");
        
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
            // If showing all the members, tell the associated applet showing only the selected items
            // to add this item to its list.
            MemberRow lclRow = (MemberRow) _table.getRows().elementAt(lclSelectedRow);

            lclRow.setSelected(true);
            ListMember lclMember = (ListMember) _idHashTable.get(lclRow.getId());             

            ((SelectedListApplet) (getAppletContext().getApplet("SelectedApplet"))).addSelected(lclMember);
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
            // If showing all the members, tell the associated applet showing only the selected items
            // to add this item to its list.                
            MemberRow lclRow = (MemberRow) _table.getRows().elementAt(lclSelectedRow);
            ListMember lclMember = (ListMember) _idHashTable.get(lclRow.getId());                

            ((SelectedListApplet) (getAppletContext().getApplet("SelectedApplet"))).addSelected(lclMember);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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

            setSelectedStatus();       
        }

        synchronized (this)
        {
            // If the page has already called setMemberList(), then tell the selected applet about the 
            // members which are already selected. 
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
    
    public void sendSelectedToApplet()
    {
        StringTokenizer lclMemberTokens = new StringTokenizer(_memberListString, ",");
        
        while (null == _idHashTable)
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException lclException)
            {}
            
        }
                
        try
        {
            while (lclMemberTokens.hasMoreElements())
            {
                String lclNextId = (String) lclMemberTokens.nextElement();
            
                ((SelectedListApplet) (getAppletContext().getApplet("SelectedApplet"))).addSelected((ListMember) _idHashTable.get(lclNextId));
            
            }
        }
        catch (NullPointerException lclEx)
        {}
    }
        
    public void reload(String inYear, String inTeam)
    {
        _teamid = Integer.parseInt(inTeam);
        _year = Integer.parseInt(inYear);
        
        System.out.println("clearing table");
        clearTable();
        
        ((SelectedListApplet) (getAppletContext().getApplet("SelectedApplet"))).clearSelectedList();
        getMemberListFromServer();
        displayMemberList();
        setSelectedStatus();     
        sendSelectedToApplet();
    }
    
    public void saveSelectedList()
    {        
        String wkPlayerList = ((SelectedListApplet) (getAppletContext().getApplet("SelectedApplet"))).getSelectedList();
        String wkURLString = getPlayerSaveURL(wkPlayerList);
        try
        {
            URL wkUrl = new URL(wkURLString);
            // get memberlist to populate the table
            HttpMessage wkMsg = new HttpMessage(wkUrl);
            wkMsg.sendGetMessage();
        }
        catch (Exception lclException)
        {
            lclException.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void setSelectedStatus()
    {    
        StringTokenizer lclMemberTokens = new StringTokenizer(_memberListString, ",");
        Vector lclTableRows = _table.getRows();

        while (lclMemberTokens.hasMoreElements())
        {
            String lclNextId = (String) lclMemberTokens.nextElement();

            Enumeration lclMembers = lclTableRows.elements();
            
            MemberRow lclRow = null;

            while (lclMembers.hasMoreElements())
            {
                lclRow = (MemberRow) lclMembers.nextElement();
                if (lclRow.getId().equals(lclNextId))
                {
                    lclRow.setSelected(true);
                    break;
                }
            }
        }    
        refreshListDisplay();
    }
    
    @SuppressWarnings("unchecked")
    public void deSelect(String inId)
    {
        Enumeration lclMembers = _table.getRows().elements();
            
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
    
    public void setFilter(String inFilter)
    {
        // simple algorithm, remove all entries in display list and reapply filter to master list and redisplay.
        _filter = inFilter;
        _table.getRows().removeAllElements();
        _table.removeAllRows();
        displayMemberList();
        _memberListString = ((SelectedListApplet) (getAppletContext().getApplet("SelectedApplet"))).getSelectedList();
        setSelectedStatus();
    }
        
    ////
    // METHOD: getMemberListFromServer
    ////
    @SuppressWarnings("unchecked")
    public void getMemberListFromServer()
    {
        URL lclUrl = null;
        String lclUrlString = createURLStringForListServlet();

        //  Only the applet containing all the available members gets the master list from the server.  The server
        //  which is called will send two serialized objects.  The first contains a vector of strings which are used
        //  as column headings.  The second is a Vector of ListMembers of all possible resources of the desired type
        //  which the user is allowed to view.
        try
        {
            _memberListString = null;
            lclUrl = new URL(lclUrlString);
            // get memberlist to populate the table
            HttpMessage lclMsg = new HttpMessage(lclUrl);
            InputStream lclIn = lclMsg.sendGetMessage();
            ObjectInputStream lclResult = new ObjectInputStream(new BufferedInputStream (lclIn, 6000));
 
            _columnHeadings = new Vector<String>();
            _columnHeadings.addElement(_columnName);
            //_columnHeadings = (Vector) lclResult.readObject();
            
     
            _memberList = (Vector) lclResult.readObject();
                
            Enumeration lclMemberEnum = _memberList.elements();
            int lclHashTableSize = ((_memberList.size() * 3) / 4) + 1;

            _idHashTable = new Hashtable<String, ListMember>(lclHashTableSize);
                
            StringBuffer wkTemp = new StringBuffer(128);
            while (lclMemberEnum.hasMoreElements())
            {
                ListMember lclMember = (ListMember) lclMemberEnum.nextElement();

                
                if (lclMember.getSelected())
                {
                    if (0 == wkTemp.length())
                    {
                        wkTemp.append(lclMember.getId());
                    }
                    else
                    {
                        wkTemp.append(",").append(lclMember.getId());
                    }
                }

                _idHashTable.put(lclMember.getId(), lclMember);
            }
            _memberListString = wkTemp.toString();
            System.out.println("MemberListString is " + _memberListString);
        }
        catch (Exception lclException)
        {
            lclException.printStackTrace();
        }
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
        boolean lclApplyFilter = !((null == _filter) || _filter.equals(""));
        
        if (_table == null)
        {
            getTable();
        }       
        while (lclMemberEnum.hasMoreElements())
        {       

            synchronized (this)
            {
                lclMemberRow = new MemberRow((ListMember) lclMemberEnum.nextElement());
                if (!lclApplyFilter || (lclApplyFilter && applyFilter(lclMemberRow.getStrings()[MemberRow.NAME_IX], _filter)))//lclMemberRow.getStrings()[MemberRow.NAME_IX].startsWith(_filter)))
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
        _table.getDataArea().setCheckingSelects(true);
        DataArea.setReferenceColumnFont(_dataFont);
        _table.setShowGrid(true);
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

        lclNewStr = Misc.makeUrl(lclURL, "DataServlet?action=playerlist&tid=" + _teamid + "&year=" + _year + "");

        return lclNewStr;
    }

    private String getPlayerSaveURL(String inPlayers)
    {
        URL lclURL = getDocumentBase();
        String lclNewStr = "";

        lclNewStr = Misc.makeUrl(lclURL, "DataServlet?action=" + _action + "&tid=" + _teamid + "&players=" + inPlayers);

        return lclNewStr;
    }
    public boolean applyFilter(String inString, String inFilter)
    {
        StringTokenizer lclParser = new StringTokenizer(inFilter, "*?", true);
        String lclWorking = inString;
        boolean lclPassedFilter = true;
        boolean lclWildcardActive = false;
        String lclToken = null;
        
        try
        {
            while (lclParser.hasMoreTokens())
            {
                lclToken = lclParser.nextToken();
                if (lclToken.equals("?"))
                {
                    lclWorking = lclWorking.substring(1);
                    lclWildcardActive = false;
                }
                else if (lclToken.equals("*"))
                {
                    lclWildcardActive = true;
                }
                else
                {
                    if (lclWildcardActive)
                    {
                        int lclIndex = lclWorking.indexOf(lclToken);

                        if (lclIndex == -1)
                        {
                            lclPassedFilter = false;
                            break;
                        }
                        else
                        {
                            lclWorking = lclWorking.substring(lclIndex);
                            lclWorking = lclWorking.substring(lclToken.length());
                        }
                        lclWildcardActive = false;
                    }
                    else
                    {
                        if (!lclWorking.startsWith(lclToken))
                        {
                            lclPassedFilter = false;
                            break;
                        }
                        else
                        {
                            lclWorking = lclWorking.substring(lclToken.length());
                        }
                    }
                }
            }
            
            if (lclWorking.length() > 0 && !lclToken.equals("*"))
                lclPassedFilter = false;
        }
        catch (Exception e)
        {
            if ((lclParser.countTokens() > 1) || !((lclParser.countTokens() == 1) && lclParser.nextToken().equals("*")))
            {
                lclPassedFilter = false;
            }
            e.printStackTrace();
        }
        
        return lclPassedFilter;
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
