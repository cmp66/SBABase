/*
 * TeamManager.java
 *
 * Created on February 9, 2003, 1:54 PM
 */

package com.wahoo.apba.resourcemanagers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.Element;

import com.wahoo.apba.database.DraftPick;
import com.wahoo.apba.database.GenericStatRecord;
import com.wahoo.apba.database.RosterMove;
import com.wahoo.apba.database.SeriesStatRecord;
import com.wahoo.apba.database.StatRecord;
import com.wahoo.apba.database.Transaction;
import com.wahoo.apba.database.util.DBKeyGen;
import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.util.Email;
/**
 *
 * @author  cphillips
 */
public class TransactionManager
{
    //static TransactionManager _manager = null;
	private PlayerCache playerCache = null;
	private CardedList cardedList = null;
	private TeamManager teamManager = null;
	private CutList cutList = null;
	
	private String currentStandSeason = null;
	
    
    static final String GET_DRAFT_ORDER = "select teamlist from draftorder where draftyear = ?";
    static final String GET_NUMBER_ROUNDS = "select max(round) from draftpicks where draftyear = ?";
    static final String GET_NUMBER_TEAMS = "SELECT COUNT(*) AS TeamCount FROM (SELECT slotteam AS Team FROM draftpicks " +
    "WHERE (draftyear = ?) GROUP BY slotteam) DERIVEDTBL";
    static final String GET_DRAFT_SLOT = "select * from draftpicks where sloteam = ?";
    static final String GET_DRAFT = "select * from draftpicks where draftyear = ?";
    static final String GET_TEAM_DRAFT_PICKS = "select * from draftpicks where ownerteam = ? and draftyear >= ? order by draftyear,round,slotteam";
    static final String GET_PICK_OWNER = "select ownerteam from draftpicks where draftyear = ? and round = ? and slotteam = ?";
    static final String GET_PICK_OWNER_BY_ID = "select ownerteam from draftpicks where pickid = ?";
    static final String GET_PICK = "select * from draftpicks where pickid = ?";
    
    static final String GET_CURRENT_SEASON = "select max(team1), max(transdate) from transactions where type = 5";
    static final String GET_CURRENT_IN_SEASON = "select max(team1) from transactions where type = 6";
    
    static final String GET_LATEST_TRADES = "select * from transactions WHERE type = 1 and transdate > ? order by transdate desc";
    static final String GET_LATEST_TRANSACTIONS = "select * from transactions order by transdate desc";
    static final String GET_ROSTER_MOVES = "select * from rostermove where transactionid = ? and teamid = ? and type = 2";
    static final String GET_ALL_ROSTER_MOVES = "select * from rostermove where transactionid = ? and teamid = ?";
    
    static final int TRANSACTION_TYPE_TRADE         = 1;
    static final int TRANSACTION_TYPE_DRAFT         = 2;
    static final int TRANSACTION_TYPE_ADD           = 3;
    static final int TRANSACTION_TYPE_REMOVE        = 4;
    static final int TRANSACTION_TYPE_YEAR_CHANGE   = 5;
    static final int TRANSACTION_TYPE_SEASON_START  = 6;
    
    static final int ROSTER_MOVE_ADD                = 1;
    static final int ROSTER_MOVE_DROP               = 2;
    
    static final int ROSTER_MOVE_PLAYER             = 1;
    static final int ROSTER_MOVE_PICK               = 2;
    
    private      int _currentActiveRound          = 0;
    private      int _currentActiveSlot           = 0;
    
    /** Creates a new instance of TeamManager */
    private TransactionManager ()
    {
    }
    
    /*
    public static TransactionManager getInstance ()
    {
        if (null == _manager)
            _manager = new TransactionManager ();
        
        return _manager;
    }
    */
    
    public void init ()
    {
        System.out.println ("Current Standings Season is " + getCurrentStandingsSeason () + "");
        System.out.println ("Current Stats Season is " + getCurrentStatsSeason () + "");
        System.out.println ("Current Transactions Season is " + getCurrentTransactionsSeason () + "");
        System.out.println ("Started playing games is " + isSeasonUnderway () + "");
        
        //Element wkDraft = getDraftGrid (getCurrentTransactionsSeason (), 0, false, false);
        
        System.out.println ("Current draft round is " + _currentActiveRound);
        System.out.println ("Current draft slot is " + _currentActiveSlot);
    }
    
    public void setPlayerCache (PlayerCache inCache)
    {
    	this.playerCache = inCache;
    }
    
    public void setCutList (CutList list)
    {
    	this.cutList = list;
    }
    
    public void setCardedList (CardedList inList)
    {
    	this.cardedList = inList;
    }
    
    public void setTeamManager (TeamManager inMgr)
    {
    	this.teamManager = inMgr;
    }
    
    public int getCurrentStandingsSeason ()
    {
        return Integer.parseInt (currentStandSeason);
    }
    
    public int getCurrentStatsSeason ()
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        int wkSeason = 0;
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection ();
            
            wkStatement = wkConn.prepareStatement (GET_CURRENT_IN_SEASON);
            ResultSet wkResults = wkStatement.executeQuery ();
            
            if (wkResults.next ())
            {
                wkSeason = wkResults.getInt (1);
            }
            
            
            wkResults.close ();
            wkStatement.close ();
        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkSeason;
    }
    
    public int getCurrentTransactionsSeason ()
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        int wkSeason = 0;
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection ();
            
            wkStatement = wkConn.prepareStatement (GET_CURRENT_SEASON);
            ResultSet wkResults = wkStatement.executeQuery ();
            
            if (wkResults.next ())
            {
                wkSeason = wkResults.getInt (1);
            }
            
            
            wkResults.close ();
            wkStatement.close ();
        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkSeason;
    }
    
    public Timestamp getSeasonSwitchDate ()
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        Timestamp wkDate = null;
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection ();
            
            wkStatement = wkConn.prepareStatement (GET_CURRENT_SEASON);
            ResultSet wkResults = wkStatement.executeQuery ();
            
            if (wkResults.next ())
            {
                wkDate = wkResults.getTimestamp (2);
            }
            
            
            wkResults.close ();
            wkStatement.close ();
        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkDate;
    }
    
    public int getCurrentSeason ()
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        int wkSeason = 0;
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection ();
            
            wkStatement = wkConn.prepareStatement (GET_CURRENT_SEASON);
            ResultSet wkResults = wkStatement.executeQuery ();
            
            if (wkResults.next ())
            {
                wkSeason = wkResults.getInt (1);
            }
            
            
            wkResults.close ();
            wkStatement.close ();
        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkSeason;
    }
    
    public boolean isSeasonUnderway ()
    {
        boolean wkUnderway = false;
        
        wkUnderway = (getCurrentStatsSeason () == getCurrentTransactionsSeason ());
        
        return wkUnderway;
    }
    
    
    
    public void createStatRecordIfMissing (int inYear, int inTeamId, int inPlayerId)
    {
        if (getCurrentTransactionsSeason() != getCurrentStatsSeason())
            return;
        
        GenericStatRecord wkRecord = StatRecord.getStatRecord (inPlayerId, inYear, inTeamId);

        
        
        if (null == wkRecord)
        {
            wkRecord = new StatRecord ();
            wkRecord.setSeason (inYear);
            wkRecord.setTeamid (inTeamId);
            wkRecord.setPlayerid (inPlayerId);
            System.out.println ("Creating record for player" + playerCache.get (inPlayerId).getDisplayname () +
            " for team " + TeamCache.get (inTeamId).getNickname () + " for year " + inYear + "");
            wkRecord.createRecord ();
        }
        
    }


    public void createSeriesStatRecordIfMissing (int inYear, int inSeries, int inReportingTeamId, int inStatsTeamId, int inPlayerId)
    {
        if (getCurrentTransactionsSeason() != getCurrentStatsSeason())
            return;
        
        SeriesStatRecord wkRecord = SeriesStatRecord.getSeriesStatRecord(inPlayerId, inYear, inSeries, inReportingTeamId, inStatsTeamId);
        
        if (null == wkRecord)
        {
            wkRecord = new SeriesStatRecord ();
            wkRecord.setSeason (inYear);
            wkRecord.setTeamid (inStatsTeamId);
            wkRecord.setPlayerid (inPlayerId);
            wkRecord.setSeries (inSeries);
            wkRecord.setReportingTeam (inReportingTeamId);
            //System.out.println ("Creating series record for player" + PlayerCache.get (inPlayerId).getDisplayname () +
            //" for team " + TeamCache.get (inStatsTeamId).getNickname () + " for year " + inYear + " as part of series" + inSeries + "-" + inReportingTeamId);
            wkRecord.createRecord ();
        }
        
    }    
    
    public Element getDraftGrid (int inYear, int inTeam, boolean inAdmin, boolean inTransform)
    {
        Element wkDraft = null;
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        
        try
        {
            HashMap<Integer, Integer> wkDraftOrder = null;
            int[] wkDraftOrderArray = new int[0];
            int[][] wkDraftGrid = null;
            int[][] wkDraftSelections = null;
            wkConn = DBUtil.getReadOnlyDBConnection ();
            
            wkStatement = wkConn.prepareStatement (GET_DRAFT_ORDER);
            wkStatement.setInt (1, inYear);
            ResultSet wkResults = wkStatement.executeQuery ();
            
            if (wkResults.next ())
            {
                wkDraftOrder = getDraftOrder (wkResults.getString ("teamlist"));
                wkDraftOrderArray = getDraftOrderArray (wkResults.getString ("teamlist"));
            }
            
            wkResults.close ();
            wkStatement.close ();
            
            wkStatement = wkConn.prepareStatement (GET_NUMBER_ROUNDS);
            wkStatement.setInt (1, inYear);
            wkResults = wkStatement.executeQuery ();
            wkResults.next ();
            int wkNumRounds = wkResults.getInt (1);
            wkResults.close ();
            wkStatement.close ();
            
            int wkNumTeams = getNumTeams (inYear);
            
            wkStatement = wkConn.prepareStatement (GET_DRAFT);
            wkStatement.setInt (1, inYear);
            wkResults = wkStatement.executeQuery ();
            wkDraftGrid = getDraftGrid (wkDraftOrder, wkNumRounds, wkNumTeams, wkResults, inAdmin);
            
            //do it again for the player list
            wkResults.close ();
            wkResults = wkStatement.executeQuery ();
            wkDraftSelections = getDraftSelections (wkDraftOrder, wkNumRounds, wkNumTeams, wkResults);
            
            wkResults.close ();
            wkStatement.close ();
            wkDraft = createDraftDoc (wkDraftGrid, wkDraftSelections, wkDraftOrderArray, inTeam, inAdmin, inTransform);
            wkDraft.setAttribute ("year", inYear+"");
            
        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkDraft;
    }
    
    public int[] getDraftArray (int inYear)
    {
        //Element wkDraft = null;
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        int[] wkDraftOrderArray = null;
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection ();
            
            wkStatement = wkConn.prepareStatement (GET_DRAFT_ORDER);
            wkStatement.setInt (1, inYear);
            ResultSet wkResults = wkStatement.executeQuery ();
            
            if (wkResults.next ())
            {
                wkDraftOrderArray = getDraftOrderArray (wkResults.getString ("teamlist"));
            }
            
            wkResults.close ();
            wkStatement.close ();
            
        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkDraftOrderArray;
    }
    
    private HashMap<Integer, Integer> getDraftOrder (String inTeamList)
    {
        StringTokenizer wkTeams = new StringTokenizer (inTeamList, ",");
        HashMap<Integer, Integer> wkDraftOrder = new HashMap<Integer, Integer> ();
        int index = 0;
        
        while (wkTeams.hasMoreTokens ())
        {
            Integer wkTeam = (Integer) Integer.valueOf(wkTeams.nextToken ());
            wkDraftOrder.put (wkTeam, Integer.valueOf(index));
            index++;
        }
        
        return wkDraftOrder;
    }
    
    private int[] getDraftOrderArray (String inTeamList)
    {
        StringTokenizer wkTeams = new StringTokenizer (inTeamList, ",");
        int[] wkDraftOrder = new int[wkTeams.countTokens ()];
        int index = 0;
        
        while (wkTeams.hasMoreTokens ())
        {
            int wkTeam = Integer.parseInt (wkTeams.nextToken ());
            wkDraftOrder[index] = wkTeam;
            index++;
        }
        
        return wkDraftOrder;
    }
    private  int[][] getDraftGrid (HashMap<Integer, Integer> inOrder, int inNumRounds, 
    		int inNumTeams, ResultSet inPicks, boolean inAdmin)
    {
        int[][] wkGrid = new int[inNumRounds][inNumTeams];
        try
        {
            while (inPicks.next ())
            {
                int wkSlotTeam = inPicks.getInt ("slotteam");
                int wkRound = inPicks.getInt ("round");
                int wkOwner = inPicks.getInt ("ownerteam");
                int wkSlot = ((Integer)inOrder.get (Integer.valueOf(wkSlotTeam))).intValue ();
                
                wkGrid[wkRound-1][wkSlot] = wkOwner;
            }
        }
        catch (SQLException wkEx)
        {
            wkEx.printStackTrace ();
            Email.emailException(wkEx);
        }
        
        return wkGrid;
        
    }
    
    private  int[][] getDraftSelections (HashMap<Integer, Integer> inOrder, int inNumRounds, 
    		int inNumTeams, ResultSet inPicks)
    {
        int[][] wkGrid = new int[inNumRounds][inNumTeams];
        try
        {
            while (inPicks.next ())
            {
                int wkSlotTeam = inPicks.getInt ("slotteam");
                int wkRound = inPicks.getInt ("round");
                int wkPlayer = inPicks.getInt ("playerid");
                int wkSlot = ((Integer)inOrder.get (Integer.valueOf (wkSlotTeam))).intValue ();
                
                wkGrid[wkRound-1][wkSlot] = wkPlayer;
            }
        }
        catch (SQLException wkEx)
        {
            wkEx.printStackTrace ();
            Email.emailException(wkEx);
        }
        
        return wkGrid;
        
    }
    
    private Element createDraftDoc (int[][] inDraftGrid, int [][]inDraftSelections, int[] inDraftOrder, int inTeam, boolean inAdmin, boolean inTransform)
    {
        Element wkDraft = new Element ("Draft");
        wkDraft.setAttribute("admin", inAdmin ? "Y" : "N");
        Element wkDraftOrder = new Element ("DraftOrder");
        int wkPercent = 100/(inDraftOrder.length+1);
        _currentActiveRound = 0;
        _currentActiveSlot = 0;
        String wkCurrentActiveTeam = "";
        
        wkDraft.addContent (wkDraftOrder);
        
        for (int i = 0; i < inDraftOrder.length; i++)
        {
            Element wkTeam = new Element ("Team");
            wkTeam.setAttribute ("name", TeamCache.get (inDraftOrder[i]).getNickname ());
            wkTeam.setAttribute ("width", wkPercent+"");
            wkDraftOrder.addContent (wkTeam);
        }
        
        int wkNumRounds = inDraftGrid.length;
        
        for (int i = 0; i < wkNumRounds; i++)
        {
            Element wkRound = new Element ("Round");
            wkRound.setAttribute ("id", (i+1)+"");
            wkDraft.addContent (wkRound);
            
            int[] wkRoundPicks = inDraftGrid[i];
            int[] wkRoundPlayers = inDraftSelections[i];
            
            for (int j = 0; j < wkRoundPicks.length; j++)
            {
                Element wkDraftPick = new Element ("DraftPick");
                int wkOwner = wkRoundPicks[j];
                int wkPlayer = wkRoundPlayers[j];
                
                if (0 == wkPlayer && 0 != wkOwner && wkCurrentActiveTeam.equals (""))
                {
                    wkCurrentActiveTeam = TeamCache.get (wkOwner).getNickname ();
                    _currentActiveSlot = j;
                    _currentActiveRound = i;
                }
                wkDraftPick.setAttribute ("OwnerId", wkOwner+"");
                if (0 != wkPlayer)
                    wkDraftPick.setAttribute ("Player", playerCache.get (wkPlayer).getDisplayname ());
                else if (_currentActiveSlot == (j) &&
                _currentActiveRound == (i))
                {
                    if (inAdmin)
                    {
                        wkDraftPick.setAttribute ("Current", "Y");
                        wkDraftPick.addContent (cardedList.getSortedList());
                    }
                    wkDraftPick.setAttribute ("Player", "Active");
                    
                }
                else
                    wkDraftPick.setAttribute ("Player", "Unselected");
                
                if (0 != wkOwner)
                    wkDraftPick.setAttribute ("team", TeamCache.get (wkOwner).getNickname ());
                else
                    wkDraftPick.setAttribute ("team", "Forfeit");
                
                wkRound.addContent (wkDraftPick);
            }
        }
        if (inTransform)
            wkDraft = transformDraftDoc(wkDraft, wkNumRounds, inDraftOrder);
        
        return wkDraft;
        
    }
    
    
    @SuppressWarnings("unchecked")
    public Element transformDraftDoc(Element inDraftDoc, int inNumRounds, int[] inDraftOrder)
    {
        Element wkTeamArray[] = new Element[inDraftOrder.length];
        Element wkDraft = new Element("Draft");
        
        Element wkRounds = new Element ("Rounds");
        wkDraft.addContent(wkRounds);
        for (int i = 0; i < inNumRounds; i++)
        {
            Element wkRound = new Element ("Round");
            wkRound.setAttribute ("id", i+1+"");
            wkRounds.addContent (wkRound);
        }
        
        for (int i = 0; i < inDraftOrder.length; i++)
        {
            Element wkTeam = new Element("OwnerTeam");
            wkTeam.setAttribute("name", TeamCache.get(inDraftOrder[i]).getNickname());
            wkTeamArray[i] = wkTeam;
            wkDraft.addContent(wkTeam);
        }
        
        ListIterator wkRoundIter = inDraftDoc.getChildren("Round").listIterator();
        
        while (wkRoundIter.hasNext())
        {
            int wkTeam = 0;
            Element wkRound = (Element) wkRoundIter.next();
            
            ListIterator wkPickIterator = wkRound.getChildren("DraftPick").listIterator();
            while (wkPickIterator.hasNext())
            {
                Element wkPick = (Element) wkPickIterator.next();
                Element wkNewPick = new Element("DraftPick");
                wkNewPick.setAttribute("OwnerId", wkPick.getAttribute("OwnerId").getValue());
                
                if (null != wkPick.getAttribute("Player"))
                    wkNewPick.setAttribute("Player", wkPick.getAttribute("Player").getValue());
                    
                if (null != wkPick.getAttribute("team"))
                    wkNewPick.setAttribute("team", wkPick.getAttribute("team").getValue());
               
                wkTeamArray[wkTeam].addContent(wkNewPick);
                wkTeam++;
            }
        }
        
        return wkDraft;
    }
    
    public int getNumTeams (int inYear)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        int wkNumTeams = 0;
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection ();
            
            wkStatement = wkConn.prepareStatement (GET_NUMBER_TEAMS);
            wkStatement.setInt (1, inYear);
            ResultSet wkResults = wkStatement.executeQuery ();
            
            if (wkResults.next ())
                wkNumTeams = wkResults.getInt ("TeamCount");
            
            wkResults.close ();
            wkStatement.close ();
        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkNumTeams;
    }
    
    public Element getTeamDraftPicks (int inTeam)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        Element wkDraftPicks = new Element ("draftpicks");
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection ();
            
            wkStatement = wkConn.prepareStatement (GET_TEAM_DRAFT_PICKS);
            wkStatement.setInt (1, inTeam);
            wkStatement.setInt(2, getCurrentTransactionsSeason());
            ResultSet wkResults = wkStatement.executeQuery ();
            
            while (wkResults.next ())
            {
                Element wkPick = new Element ("draftpick");
                int wkYear = wkResults.getInt ("draftyear");
                int wkSlotTeam = wkResults.getInt ("slotteam");
                int wkRound = wkResults.getInt ("round");
                
                wkPick.addContent (new Element ("round").setText (wkRound+""));
                wkPick.addContent (new Element ("year").setText (wkYear+""));
                wkPick.addContent (new Element ("slotteam").setText (wkSlotTeam+""));
                String wkPickName = wkYear + " " + TeamCache.get (wkSlotTeam).getNickname () +
                " #" + wkRound;
                wkPick.addContent (new Element ("displayname").setText (wkPickName));
                wkPick.addContent (new Element ("id").setText (wkResults.getInt ("pickid")+""));
                wkDraftPicks.addContent (wkPick);
            }
            
            
            wkResults.close ();
            wkStatement.close ();
        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkDraftPicks;
    }
    public Element getTeamTradables (int inTeam)
    {
        Element wkResources = teamManager.getRoster (inTeam, getCurrentTransactionsSeason ());
        wkResources.addContent (getTeamDraftPicks (inTeam));
        
        return wkResources;
    }
    
    public int getDraftPickOwner (int inYear, int inRound, int inSlot)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        int wkTeamId = 0;
        
        try
        {
            wkConn = DBUtil.getDBConnection ();
            wkStatement = wkConn.prepareStatement (GET_PICK_OWNER);
            wkStatement.setInt (1, inYear);
            wkStatement.setInt (2, inRound);
            wkStatement.setInt (3, inSlot);
            
            wkResultSet = wkStatement.executeQuery ();
            if (wkResultSet.next ())
            {
                wkTeamId = wkResultSet.getInt ("ownerteam");
            }
            wkResultSet.close ();
            wkStatement.close ();
        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkTeamId;
    }
    
    public DraftPick getDraftPickRecord (int inId)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        DraftPick wkPick = null;
        
        try
        {
            wkConn = DBUtil.getDBConnection ();
            wkStatement = wkConn.prepareStatement (GET_PICK);
            wkStatement.setInt (1, inId);
            
            wkResultSet = wkStatement.executeQuery ();
            if (wkResultSet.next ())
            {
                wkPick = new DraftPick (wkResultSet);
            }
            wkResultSet.close ();
            wkStatement.close ();
        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkPick;
    }
    
    public int getDraftPickOwner (int inId)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        int wkTeamId = 0;
        
        try
        {
            wkConn = DBUtil.getDBConnection ();
            wkStatement = wkConn.prepareStatement (GET_PICK_OWNER_BY_ID);
            wkStatement.setInt (1, inId);
            
            wkResultSet = wkStatement.executeQuery ();
            if (wkResultSet.next ())
            {
                wkTeamId = wkResultSet.getInt ("ownerteam");
            }
            wkResultSet.close ();
            wkStatement.close ();
        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkTeamId;
    }
    
    @SuppressWarnings("unchecked")
    public void processTrade (HashMap<String, String> inPlayers)
    {
        
        Iterator<String> wkPlayers = inPlayers.keySet ().iterator ();
        int wkTeam1 = 0;
        int wkTeam2 = 0;
        Vector[] wkTeamPlayers =
        {new Vector<String> (), new Vector<String> ()};
        Vector[] wkTeamPicks =
        {new Vector<String> (), new Vector<String> ()};
        Connection wkConn = null;
        
        while (wkPlayers.hasNext ())
        {
            StringTokenizer wkResource = new StringTokenizer (wkPlayers.next (), "-");
            String wkType = wkResource.nextToken ();
            if (wkType.equals ("player"))
            {
                int wkId = Integer.parseInt (wkResource.nextToken ());
                int wkTeamId = teamManager.getPlayerTeam (wkId, getCurrentTransactionsSeason());
                if (wkTeamId == wkTeam1)
                    wkTeamPlayers[0].add (wkId+"");
                else if (wkTeamId == wkTeam2)
                    wkTeamPlayers[1].add (wkId+"");
                else if (wkTeam1 == 0)
                {
                    wkTeam1 = wkTeamId;
                    wkTeamPlayers[0].add (wkId+"");
                }
                else if (wkTeam2 == 0)
                {
                    wkTeam2 = wkTeamId;
                    wkTeamPlayers[1].add (wkId+"");
                }
                else
                    System.out.println ("SCREWED UP for player " + wkId + " on team " + wkTeamId + "");
            }
            else if (wkType.equals ("pick"))
            {
                int wkId = Integer.parseInt (wkResource.nextToken ());
                int wkTeamId = getDraftPickOwner (wkId);
                if (wkTeamId == wkTeam1)
                    wkTeamPicks[0].add (wkId+"");
                else if (wkTeamId == wkTeam2)
                    wkTeamPicks[1].add (wkId+"");
                else if (wkTeam1 == 0)
                {
                    wkTeam1 = wkTeamId;
                    wkTeamPicks[0].add (wkId+"");
                }
                else if (wkTeam2 == 0)
                {
                    wkTeam2 = wkTeamId;
                    wkTeamPicks[1].add (wkId+"");
                }
                else
                    System.out.println ("SCREWED UP for pick " + wkId + "");
            }
        }
        //System.out.println ("Team1: " + wkTeam1);
        //System.out.println ("Team2: " + wkTeam2);
        if (wkTeam1 == 0 || wkTeam2 == 0)
            return;
        try
        {
            //wkTrans = DBUtil.createTransaction();
            com.wahoo.apba.database.Transaction wkTran = null;
            //wkTrans.begin();
            
            wkConn = DBUtil.getDBConnection ();
            wkTran = createTransactionRecord (TRANSACTION_TYPE_TRADE, wkTeam1, wkTeam2, wkConn);
            makeRosterChanges (wkTran.getId (), wkTeam1, wkTeam2, wkTeamPlayers, wkConn);
            makeDraftPickChanges (wkTran.getId (), wkTeam1, wkTeam2, wkTeamPicks, wkConn);
            //wkTrans.commit();
        }
        catch (Exception wkEx)
        {
            wkEx.printStackTrace ();
            Email.emailException(wkEx);
            //try
            //{
            //wkTrans.rollback();
            //}
            //catch (Exception e)
            //{
            //e.printStackTrace();
            //}
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
    }
    
    public void processDrops (HashMap<String, String> inPlayers)
    {
        
        Iterator<String> wkPlayers = inPlayers.keySet ().iterator ();
        int wkTeam = 0;
        Vector<String> wkTeamPlayers = new Vector<String> ();
        //UserTransaction wkTrans = null;
        Connection wkConn = null;
        
        while (wkPlayers.hasNext ())
        {
            StringTokenizer wkResource = new StringTokenizer (wkPlayers.next (), "-");
            String wkType = wkResource.nextToken ();
            if (wkType.equals ("player"))
            {
                int wkId = Integer.parseInt (wkResource.nextToken ());
                int wkTeamId = teamManager.getPlayerTeam (wkId, getCurrentTransactionsSeason());
                
                if (wkTeam == 0)
                    wkTeam = wkTeamId;
                else
                {
                    if (wkTeamId != wkTeam)
                        System.out.println ("MAJOR screwup, wanted team " + wkTeam + " and received team " + wkTeamId + " for player " + wkId + "");
                }
                
                wkTeamPlayers.add (wkId+"");
            }
        }
        //System.out.println ("Team1 " + wkTeam);
        try
        {
            //wkTrans = DBUtil.createTransaction();
            com.wahoo.apba.database.Transaction wkTran = null;
            //wkTrans.begin();
            
            wkConn = DBUtil.getDBConnection ();
            wkTran = createTransactionRecord (TRANSACTION_TYPE_REMOVE, wkTeam, 0, wkConn);
            makeDrops (wkTran.getId (), wkTeam, wkTeamPlayers, wkConn);
            //cardedList.setGetId("true");
            //cardedList.createList ();
            //wkTrans.commit();
        }
        catch (Exception wkEx)
        {
            wkEx.printStackTrace ();
            Email.emailException(wkEx);
            //try
            //{
            //wkTrans.rollback();
            //}
            //catch (Exception e)
            //{
            //e.printStackTrace();
            //}
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
    }
    
    public void processAdds (HashMap<String, String> inPlayers, int inTeam)
    {
        
        Iterator<String> wkPlayers = inPlayers.keySet ().iterator ();
        //UserTransaction wkTrans = null;
        Connection wkConn = null;
        
        try
        {
            wkConn = DBUtil.getDBConnection ();
            while (wkPlayers.hasNext ())
            {
                StringTokenizer wkResource = new StringTokenizer (wkPlayers.next (), "-");
                String wkType = wkResource.nextToken ();
                if (wkType.equals ("player"))
                {
                    int wkId = Integer.parseInt (wkResource.nextToken ());
                    System.out.println("Adding Player " + wkId + " to team " + inTeam);
                    Transaction wkTran = createTransactionRecord (TRANSACTION_TYPE_ADD, inTeam, 0, wkConn);
                    createRosterMove (ROSTER_MOVE_PLAYER, ROSTER_MOVE_ADD, wkTran.getId(), inTeam, wkId, wkConn);
                    createRosterAssign (wkId, inTeam, wkConn);
                }
            }
            //cardedList.setGetId("true");
            //cardedList.createList ();
        }
        catch (Exception wkEx)
        {
            wkEx.printStackTrace ();
            Email.emailException(wkEx);
            //try
            //{
            //wkTrans.rollback();
            //}
            //catch (Exception e)
            //{
            //e.printStackTrace();
            //}
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
    }
    
    
    public void draftPlayer (int inPlayerId)
    {
        
        //UserTransaction wkTrans = null;
        Connection wkConn = null;
        int wkTeamId = 0;
        int wkSlotTeamId = 0;
        
        if  (inPlayerId > 0)
        {
            int[] wkDraftOrder = getDraftArray (getCurrentTransactionsSeason ());
            wkSlotTeamId = wkDraftOrder[_currentActiveSlot];
            wkTeamId = getDraftPickOwner (getCurrentTransactionsSeason (), _currentActiveRound+1, wkSlotTeamId);
            //System.out.println ("Drafting " + PlayerCache.get (inPlayerId).getDisplayname () + " for team " + TeamCache.get (wkTeamId).getNickname ());
        }
        try
        {
            //wkTrans = DBUtil.createTransaction();
            com.wahoo.apba.database.Transaction wkTran = null;
            //wkTrans.begin();
            
            wkConn = DBUtil.getDBConnection ();
            wkTran = createTransactionRecord (TRANSACTION_TYPE_DRAFT, wkTeamId, 0, wkConn);
            makeDraftRosterChange (wkTran.getId (), wkTeamId, inPlayerId, wkConn);
            modifyDraftSlot (wkSlotTeamId, wkTeamId, inPlayerId, getCurrentTransactionsSeason (), _currentActiveRound+1, wkConn);
            //cardedList.setGetId("true");
            //cardedList.createList ();
            //wkTrans.commit();
        }
        catch (Exception wkEx)
        {
            wkEx.printStackTrace ();
            Email.emailException(wkEx);
            //try
            //{
            //wkTrans.rollback();
            //}
            //catch (Exception e)
            //{
            //e.printStackTrace();
            //}
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
    }
    
    public void addPlayer (int inPlayerId, int inTeamId)
    {
        
        Connection wkConn = null;
        
        try
        {
            
            wkConn = DBUtil.getDBConnection ();
            Transaction wkTran = createTransactionRecord (TRANSACTION_TYPE_ADD, inTeamId, 0, wkConn);
            createRosterMove (ROSTER_MOVE_PLAYER, ROSTER_MOVE_ADD, wkTran.getId(), inTeamId, inPlayerId, wkConn);
            createRosterAssign (inPlayerId, inTeamId, wkConn);

        }
        catch (Exception wkEx)
        {
            wkEx.printStackTrace ();
            Email.emailException(wkEx);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
    }
    
    private  com.wahoo.apba.database.Transaction createTransactionRecord (int inType, int inTeam1, int inTeam2, Connection inConn)
    throws SQLException
    {
        com.wahoo.apba.database.Transaction wkTran = new com.wahoo.apba.database.Transaction ();
        
        wkTran.setId (DBKeyGen.getKey ());
        wkTran.setType (inType);
        wkTran.setTeam1 (inTeam1);
        wkTran.setTeam2 (inTeam2);
        wkTran.setTransdate (new Timestamp (System.currentTimeMillis ()));
        wkTran.createRecord (inConn);
        
        return wkTran;
    }
    
    
    @SuppressWarnings("unchecked")
    private void makeRosterChanges (int inTranId, int inTeam1, int inTeam2, Vector[] inTeamPlayers, Connection inConn)
    throws SQLException
    {
        //String wkTeam1Name = TeamCache.get (inTeam1).getNickname ();
        //String wkTeam2Name = TeamCache.get (inTeam2).getNickname ();
        
        for (int i = 0; i < inTeamPlayers[0].size (); i++)
        {
            int wkPlayerId = Integer.parseInt ((String)(inTeamPlayers[0].get (i)));
            //String wkPlayerName = playerCache.get (wkPlayerId).getDisplayname ();
            //System.out.println ("Trading " + wkPlayerName + "from " + wkTeam1Name + "to " + wkTeam2Name);
            createRosterMove (ROSTER_MOVE_PLAYER, ROSTER_MOVE_ADD, inTranId, inTeam2, wkPlayerId, inConn);
            createRosterMove (ROSTER_MOVE_PLAYER, ROSTER_MOVE_DROP, inTranId, inTeam1, wkPlayerId, inConn);
            modifyRosterAssign (wkPlayerId, inTeam2, inConn);
            
            if (getCurrentStatsSeason () == getCurrentTransactionsSeason ())
                createStatRecordIfMissing (getCurrentStatsSeason (), inTeam2, wkPlayerId);
        }
        for (int i = 0; i < inTeamPlayers[1].size (); i++)
        {
            int wkPlayerId = Integer.parseInt ((String)(inTeamPlayers[1].get (i)));
            //String wkPlayerName = playerCache.get (wkPlayerId).getDisplayname ();
            //System.out.println ("Trading " + wkPlayerName + "from " + wkTeam2Name + "to " + wkTeam1Name);
            createRosterMove (ROSTER_MOVE_PLAYER, ROSTER_MOVE_ADD, inTranId, inTeam1, wkPlayerId, inConn);
            createRosterMove (ROSTER_MOVE_PLAYER, ROSTER_MOVE_DROP, inTranId, inTeam2, wkPlayerId, inConn);
            modifyRosterAssign (wkPlayerId, inTeam1, inConn);
            
            if (getCurrentStatsSeason () == getCurrentTransactionsSeason ())
                createStatRecordIfMissing (getCurrentStatsSeason (), inTeam1, wkPlayerId);
        }
    }
    
    private void makeDrops (int inTranId, int inTeam,  Vector<String> inTeamPlayers, Connection inConn)
    throws SQLException
    {
        //String wkTeamName = TeamCache.get (inTeam).getNickname ();
        
        for (int i = 0; i < inTeamPlayers.size (); i++)
        {
            int wkPlayerId = Integer.parseInt ((inTeamPlayers.get (i)));
            //String wkPlayerName = playerCache.get (wkPlayerId).getDisplayname ();
            //System.out.println ("Dropping " + wkPlayerName + "from " + wkTeamName);
            createRosterMove (ROSTER_MOVE_PLAYER, ROSTER_MOVE_DROP, inTranId, inTeam, wkPlayerId, inConn);
            createPlayerCut(wkPlayerId, inTeam, inConn);
            deleteRosterAssign (wkPlayerId, inTeam, inConn);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void makeDraftPickChanges (int inTranId, int inTeam1, int inTeam2, Vector[] inTeamPicks, Connection inConn)
    throws SQLException
    {
        //String wkTeam1Name = TeamCache.get (inTeam1).getNickname ();
        //String wkTeam2Name = TeamCache.get (inTeam2).getNickname ();
        
        for (int i = 0; i < inTeamPicks[0].size (); i++)
        {
            int wkPickId = Integer.parseInt ((String)(inTeamPicks[0].get (i)));
            createRosterMove (ROSTER_MOVE_PICK, ROSTER_MOVE_ADD, inTranId, inTeam2, wkPickId, inConn);
            createRosterMove (ROSTER_MOVE_PICK, ROSTER_MOVE_DROP, inTranId, inTeam1, wkPickId, inConn);
            modifyDraftPickOwner (wkPickId, inTeam2, inConn);
        }
        for (int i = 0; i < inTeamPicks[1].size (); i++)
        {
            int wkPickId = Integer.parseInt ((String)(inTeamPicks[1].get (i)));
            createRosterMove (ROSTER_MOVE_PICK, ROSTER_MOVE_ADD, inTranId, inTeam1, wkPickId, inConn);
            createRosterMove (ROSTER_MOVE_PICK, ROSTER_MOVE_DROP, inTranId, inTeam2, wkPickId, inConn);
            modifyDraftPickOwner (wkPickId, inTeam1, inConn);
        }
    }
    
    private void makeDraftRosterChange (int inTranId, int inTeam, int inPlayer, Connection inConn)
    throws SQLException
    {
        //String wkTeamName = TeamCache.get (inTeam).getNickname ();
        
        //String wkPlayerName = playerCache.get (inPlayer).getDisplayname ();
        //System.out.println ("Drafting " + wkPlayerName + " to " + wkTeamName);
        createRosterMove (ROSTER_MOVE_PLAYER, ROSTER_MOVE_ADD, inTranId, inTeam, inPlayer, inConn);
        createRosterAssign (inPlayer, inTeam, inConn);
        
        if (getCurrentStatsSeason () == getCurrentTransactionsSeason ())
        {
            createStatRecordIfMissing (getCurrentStatsSeason (), inTeam, inPlayer);
        }
    }
    
    private void createRosterMove (int inResourceType, int inMoveType, int inTranId, int inTeam, int inId, Connection inConn)
    throws SQLException
    {
        RosterMove wkMove = new RosterMove (inMoveType, inTeam, inTranId, inResourceType, inId);
        wkMove.createRecord (inConn);
    }
    
    private void modifyRosterAssign (int inPlayerId, int inTeamId, Connection inConn)
    throws SQLException
    {
        PreparedStatement wkStatement = inConn.prepareStatement ("UPDATE rosterassign SET teamid = ? WHERE playerid = ? and year = ?");
        wkStatement.setInt (1, inTeamId);
        wkStatement.setInt (2, inPlayerId);
        wkStatement.setInt (3, getCurrentTransactionsSeason ());
        
        wkStatement.executeUpdate ();
        
    }
    
    private  void deleteRosterAssign (int inPlayerId, int inTeamId, Connection inConn)
    throws SQLException
    {
        PreparedStatement wkStatement = inConn.prepareStatement ("DELETE from rosterassign WHERE teamid = ? AND playerid = ? and year = ?");
        wkStatement.setInt (1, inTeamId);
        wkStatement.setInt (2, inPlayerId);
        wkStatement.setInt (3, getCurrentTransactionsSeason ());
        
        wkStatement.executeUpdate ();
        
        wkStatement.close();
        
    }
    
    private  void createRosterAssign (int inPlayerId, int inTeamId, Connection inConn)
    throws SQLException
    {
        PreparedStatement wkStatement = inConn.prepareStatement ("INSERT into rosterassign (playerid, teamid, year) VALUES(?,?,?)");
        wkStatement.setInt (2, inTeamId);
        wkStatement.setInt (1, inPlayerId);
        wkStatement.setInt (3, getCurrentTransactionsSeason ());
        
        wkStatement.executeUpdate ();
        wkStatement.close();
        
        removePlayerCut(inPlayerId, inConn);
        
    }
    
    private void createPlayerCut (int inPlayerId, int inTeamId, Connection inConn)
    throws SQLException
    {
        PreparedStatement wkStatement = inConn.prepareStatement ("INSERT into playercuts (playerid, teamid, season) values (?,?,?)");
        wkStatement.setInt (1, inPlayerId);
        wkStatement.setInt (2, inTeamId);
        wkStatement.setInt(3, getCurrentTransactionsSeason());
        
        wkStatement.executeUpdate ();
        wkStatement.close();
        
    }
    
    private void removePlayerCut (int inPlayerId, Connection inConn)
    throws SQLException
    {
        PreparedStatement wkStatement = inConn.prepareStatement ("DELETE from playercuts where playerid = ?");
        wkStatement.setInt (1, inPlayerId);
        
        wkStatement.executeUpdate ();
        wkStatement.close();
        
    }
    
    private void modifyDraftPickOwner (int inPickId, int inTeamId, Connection inConn)
    throws SQLException
    {
        PreparedStatement wkStatement = inConn.prepareStatement ("UPDATE draftpicks SET ownerteam = ? WHERE pickid = ?");
        wkStatement.setInt (1, inTeamId);
        wkStatement.setInt (2, inPickId);
        
        wkStatement.executeUpdate ();
        wkStatement.close();
        
    }
    
    
    
    private void modifyDraftSlot (int inSlotId, int inTeamId, int inPlayerId, int inYear, int inRound, Connection inConn)
    throws SQLException
    {
        PreparedStatement wkStatement = inConn.prepareStatement ("UPDATE draftpicks SET playerid = ? WHERE draftyear = ? AND " +
        "slotteam = ? AND ownerteam = ? AND round = ?");
        wkStatement.setInt (1, inPlayerId);
        wkStatement.setInt (2, inYear);
        wkStatement.setInt (3, inSlotId);
        wkStatement.setInt (4, inTeamId);
        wkStatement.setInt (5, inRound);
        wkStatement.executeUpdate ();
        wkStatement.close();
        
    }
    
    
    public Element getLastestTrades (int inCount)
    {
        Element wkTranList = new Element ("transactions");
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        int wkCount = 0;
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection ();
            wkStatement = wkConn.prepareStatement (GET_LATEST_TRADES);
            wkStatement.setTimestamp (1, getSeasonSwitchDate ());
            
            wkResultSet = wkStatement.executeQuery ();
            
            while (wkResultSet.next ())
            {
                if (wkCount >= inCount)
                    break;
                Element wkTransaction = new Element ("transaction");
                
                Transaction wkTRecord = new Transaction (wkResultSet);
                int wkTranId = wkTRecord.getId ();
                int wkTeam1 = wkTRecord.getTeam1 ();
                int wkTeam2 = wkTRecord.getTeam2 ();
                
                PreparedStatement wkRosterMoves = wkConn.prepareStatement (GET_ROSTER_MOVES);
                wkRosterMoves.setInt (1, wkTranId);
                wkRosterMoves.setInt (2, wkTeam1);
                ResultSet wkMoves = wkRosterMoves.executeQuery ();
                
                Element wkTeam = new Element("Team");
                wkTeam.setAttribute("name", TeamCache.get(wkTeam1).getNickname());
                wkTransaction.addContent(wkTeam);
                while (wkMoves.next ())
                {
                    Element wkItem = new Element("Item");
                    wkTeam.addContent(wkItem);
                    RosterMove wkTranElement = new RosterMove (wkMoves);
                    if (wkTranElement.getResourcetype () == ROSTER_MOVE_PLAYER)
                    {
                        wkItem.setAttribute("name", playerCache.get(wkTranElement.getResourceid()).getDisplayname());
                    }
                    else if (wkTranElement.getResourcetype () == ROSTER_MOVE_PICK)
                    {
                        DraftPick wkPick = getDraftPickRecord (wkTranElement.getResourceid ());
                        wkItem.setAttribute("name", TeamCache.get(wkPick.getSlotteam()).getNickname() + " " + wkPick.getDraftyear() + " " + " Round " + wkPick.getRound());
                        
                    }
                }
                wkMoves.close ();
                wkRosterMoves.close ();
                wkMoves = null;
                wkRosterMoves = null;
                
                wkRosterMoves = wkConn.prepareStatement (GET_ROSTER_MOVES);
                wkRosterMoves.setInt (1, wkTranId);
                wkRosterMoves.setInt (2, wkTeam2);
                wkMoves = wkRosterMoves.executeQuery ();
                
                wkTeam = new Element("Team");
                wkTeam.setAttribute("name", TeamCache.get(wkTeam2).getNickname());
                wkTransaction.addContent(wkTeam);
                while (wkMoves.next ())
                {
                    Element wkItem = new Element("Item");
                    wkTeam.addContent(wkItem);
                    RosterMove wkTranElement = new RosterMove (wkMoves);
                    if (wkTranElement.getResourcetype () == ROSTER_MOVE_PLAYER)
                    {
                        wkItem.setAttribute("name", playerCache.get(wkTranElement.getResourceid()).getDisplayname());
                    }
                    else if (wkTranElement.getResourcetype () == ROSTER_MOVE_PICK)
                    {
                        DraftPick wkPick = getDraftPickRecord (wkTranElement.getResourceid ());
                        wkItem.setAttribute("name", TeamCache.get(wkPick.getSlotteam()).getNickname() + " " + wkPick.getDraftyear() + " " + " Round " + wkPick.getRound());
                    }
                }
                wkMoves.close ();
                wkRosterMoves.close ();
                wkMoves = null;
                wkRosterMoves = null;
                
                wkTranList.addContent (wkTransaction);
                
                wkCount++;
                
            }
            
            wkStatement.close();
            wkStatement = null;

        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (wkStatement != null)
                    wkStatement.close();
                if (wkConn != null)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkTranList;
    }
    
    
    public Element getLastestTransactions (int inCount)
    {
        Element wkTranList = new Element ("transactions");
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        int wkCount = 0;
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection ();
            wkStatement = wkConn.prepareStatement (GET_LATEST_TRANSACTIONS);
            
            wkResultSet = wkStatement.executeQuery ();
            
            while (wkResultSet.next ())
            {
                if (wkCount >= inCount)
                    break;
                Element wkTransaction = new Element ("transaction");
                
                Transaction wkTRecord = new Transaction (wkResultSet);
                wkTransaction.setAttribute("type", wkTRecord.getTransactionTypeString());
                int wkTranId = wkTRecord.getId ();
                int wkTeam1 = wkTRecord.getTeam1 ();
                int wkTeam2 = wkTRecord.getTeam2 ();
                
                
                PreparedStatement wkRosterMoves = null;
                
                if (wkTRecord.getType() == TRANSACTION_TYPE_TRADE)
                {
                    wkRosterMoves = wkConn.prepareStatement (GET_ROSTER_MOVES);
                }
                else
                {
                    wkRosterMoves = wkConn.prepareStatement(GET_ALL_ROSTER_MOVES);
                }
                wkRosterMoves.setInt (1, wkTranId);
                wkRosterMoves.setInt (2, wkTeam1);
                ResultSet wkMoves = wkRosterMoves.executeQuery ();
                
                Element wkTeam = new Element("Team");
                if (null != TeamCache.get(wkTeam1))
                {
                    wkTeam.setAttribute("name", TeamCache.get(wkTeam1).getNickname());
                }
                else
                {
                    wkTeam.setAttribute("name", wkTeam1+"");
                }
                wkTransaction.addContent(wkTeam);
                while (wkMoves.next ())
                {
                    Element wkItem = new Element("Item");
                    wkTeam.addContent(wkItem);
                    RosterMove wkTranElement = new RosterMove (wkMoves);
                    if (wkTranElement.getResourcetype () == ROSTER_MOVE_PLAYER)
                    {
                        wkItem.setAttribute("name", playerCache.get(wkTranElement.getResourceid()).getDisplayname());
                    }
                    else if (wkTranElement.getResourcetype () == ROSTER_MOVE_PICK)
                    {
                        DraftPick wkPick = getDraftPickRecord (wkTranElement.getResourceid ());
                        wkItem.setAttribute("name", TeamCache.get(wkPick.getSlotteam()).getNickname() + " " + wkPick.getDraftyear() + " " + " Round " + wkPick.getRound());
                        
                    }
                }
                wkMoves.close ();
                wkRosterMoves.close ();
                wkStatement.close();
                wkMoves = null;
                wkRosterMoves = null;
                wkStatement = null;
                
                wkRosterMoves = wkConn.prepareStatement (GET_ROSTER_MOVES);
                wkRosterMoves.setInt (1, wkTranId);
                wkRosterMoves.setInt (2, wkTeam2);
                wkMoves = wkRosterMoves.executeQuery ();
                
                wkTeam = new Element("Team");
                 if (null != TeamCache.get(wkTeam2))
                {
                    wkTeam.setAttribute("name", TeamCache.get(wkTeam2).getNickname());
                }
                else
                {
                    wkTeam.setAttribute("name", wkTeam2+"");
                }
                wkTransaction.addContent(wkTeam);
                while (wkMoves.next ())
                {
                    Element wkItem = new Element("Item");
                    wkTeam.addContent(wkItem);
                    RosterMove wkTranElement = new RosterMove (wkMoves);
                    if (wkTranElement.getResourcetype () == ROSTER_MOVE_PLAYER)
                    {
                        wkItem.setAttribute("name", playerCache.get(wkTranElement.getResourceid()).getDisplayname());
                    }
                    else if (wkTranElement.getResourcetype () == ROSTER_MOVE_PICK)
                    {
                        DraftPick wkPick = getDraftPickRecord (wkTranElement.getResourceid ());
                        wkItem.setAttribute("name", TeamCache.get(wkPick.getSlotteam()).getNickname() + " " + wkPick.getDraftyear() + " " + " Round " + wkPick.getRound());
                    }
                }
                wkMoves.close ();
                wkRosterMoves.close ();
                wkMoves = null;
                wkRosterMoves = null;
                
                wkTranList.addContent (wkTransaction);
                
                wkCount++;
                
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace ();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkStatement)
                    wkStatement.close();
                if (null != wkConn)
                    wkConn.close ();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace ();
                Email.emailException(wkException);
            }
        }
        
        return wkTranList;
    }

	public void setCurrentStandSeason(String currentStandingsSeason)
	{
		this.currentStandSeason = currentStandingsSeason;
	}
	
	public String getCurrentStandSeason()
	{
		return this.currentStandSeason;
	}
	
	
	public void resetLists()
	{
		System.out.println("reseting lists");
		playerCache.createMap();
        cardedList.setGetId("true");
        cardedList.createList ();
        cutList.init();
	}
}
