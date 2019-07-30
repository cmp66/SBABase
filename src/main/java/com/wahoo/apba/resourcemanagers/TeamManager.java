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
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.jdom.Element;

import com.wahoo.apba.database.CardAnalyzer;
import com.wahoo.apba.database.Player;
import com.wahoo.apba.database.PlayerCard;
import com.wahoo.apba.database.PlayerCut;
import com.wahoo.apba.database.Team;
import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.util.Email;
/**
 *
 * @author  cphillips
 */
public class TeamManager
{
    //private static TeamManager _manager = null;
    
    private PlayerCache playerCache = null;
    private DivisionCache divisionCache = null;
    private CardCache cardCache = null;
    private CardedList cardedList = null;
    private CutList cutList = null;
    private TransactionManager transactionManager = null;
    private PlayerManager playerManager = null;
    private ScheduleManager scheduleManager = null;
    private UserManager userManager = null;
    
    
    private static final String STANDINGS_SQL = "select * from teamresults where year = ? order by divisionid, (won/(won+lost + 0.1)) desc";
    private static final String TEAMLIST_SQL = "select * from teamresults where year = ? order by divisionid";
    private static final String TEAMS_SQL = "select id from teams order by nickname";
    private static final String PLAYER_ASSIGNS_SQL = "select playerid from rosterassign where teamid = ? and year = ?";
    private static final String ROSTER_ASSIGNS_SQL = "select playerid from rosterslot where rosterid = ?";
    private static final String TEAM_ROSTERS_SQL = "select * from rosters where teamid = ? and season = ?";
    private static final String ROSTER_NAME_SQL = "select name from rosters where id = ?";
    private static final String GET_PLAYER_TEAM_SQL = "select teamid from rosterassign where playerid = ? and year = ?";
    private static final String PLAYER_ASSIGNS_DELETE_SQL = "delete from rosterassign where teamid = ? and year=?";
    private static final String PLAYER_ASSIGNS_CHECK_SQL = "select * from rosterassign where teamid = ? and year = ? and playerid = ?";
    private static final String PLAYER_ASSIGNS_ADD_SQL = "insert into rosterassign (playerid, teamid, year) VALUES (?, ?, ?)";    
    private static final String GET_TEAMS_IN_DIVISION_SQL = "select teamid from teamresults where divisionid in (select divisionid from teamresults where teamid = ? and year = ?)" +
                                                          "and year = ?";
    private static final String INSERT_ROSTER_SQL = "insert into rosters (teamid, season, name) values (?,?,?)";
    /** Creates a new instance of TeamManager */
    private TeamManager ()
    {
    }
    
    /*
    public static TeamManager getInstance()
    {
        if (null == _manager)
            _manager = new TeamManager();
            
        return _manager;
    }
    */
    
    public void init()
    {
        //createMaps();
        initTeamStats();
    }
    
    public void setTransactionManager (TransactionManager inMgr)
    {
    	this.transactionManager = inMgr;
    }
    
    public void setScheduleManager (ScheduleManager inMgr)
    {
    	this.scheduleManager = inMgr;
    }
    
    public void setPlayerManager (PlayerManager inMgr)
    {
    	this.playerManager = inMgr;
    }
    
    public void setUserManager (UserManager inMgr)
    {
    	this.userManager = inMgr;
    }
    
    public void setCutList(CutList list)
    {
    	this.cutList = list;
    }
    
    public void setCardCache (CardCache inCache)
    {
    	this.cardCache = inCache;
    }
    
    public void setCardedList (CardedList inList)
    {
    	this.cardedList = inList;
    }
    
    
    public void setPlayerCache (PlayerCache inCache)
    {
    	this.playerCache = inCache;
    }
    
    public void setDivisionCache (DivisionCache inCache)
    {
    	this.divisionCache = inCache;
    }
     
    public void initTeamStats()
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
         
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();
            int wkCurrentYear = transactionManager.getCurrentStatsSeason();
            wkStatement = wkConn.prepareStatement(STANDINGS_SQL);
            wkStatement.setInt(1, wkCurrentYear);
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next())
            {
                int wkTeamId = wkResultSet.getInt("teamid");
                HashMap<Integer, String> wkPlayers = getPlayerIndexesForTeam(wkTeamId, transactionManager.getCurrentTransactionsSeason());
                Iterator<Integer> wkIter = wkPlayers.keySet().iterator();
                
                while(wkIter.hasNext())
                {
                    int wkPlayer = ((Integer)wkIter.next()).intValue();
                    transactionManager.createStatRecordIfMissing(wkCurrentYear, wkTeamId, wkPlayer);
                }
 
            }            
            wkResultSet.close();
            wkStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }  
    }
    
    public Element getStandings()
    {
    	return getStandings(transactionManager.getCurrentStandingsSeason());
    }
    
    public Element getStandings (int inSeason)
    {
        Element wkStandings = new Element("standings");
        Element wkCurrentDivision = null;
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        int wkDivNum = 0;
        int wkTopWins = 0;
        int wkTopLosses = 0;
         
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.prepareStatement(STANDINGS_SQL);
            wkStatement.setInt(1, inSeason);
            
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next())
            {
                int wkThisDiv = wkResultSet.getInt("divisionid");
                if (wkDivNum != wkThisDiv)
                {
                    wkCurrentDivision = new Element("division");
                    wkCurrentDivision = wkCurrentDivision.setAttribute("divname", divisionCache.get(wkThisDiv).getName());
                    wkStandings.addContent(wkCurrentDivision);
                    wkDivNum = wkThisDiv;
                    wkTopWins = wkResultSet.getInt("won");
                    wkTopLosses = wkResultSet.getInt("lost");
                }
                
                Element wkTeamEntry = new Element("teamentry");
                int wkTeamId = wkResultSet.getInt("teamid");
                int wkWon = wkResultSet.getInt("won");
                int wkLost = wkResultSet.getInt("lost");
                double wkGB = ((double)(wkTopWins - wkWon + wkLost - wkTopLosses)) / 2.0;
                String wkLink = "Controller?page=schedule&team=" + TeamCache.get(wkTeamId).getId() + "&season=" + inSeason +"";
                DecimalFormat wkFormatter = new DecimalFormat();
                double wkPct;
                
                if ( (wkWon + wkLost) != 0)    
                    wkPct = (double)wkWon/((double)wkWon + (double)wkLost);
                else
                    wkPct = 0.000;
                
                wkFormatter.setMaximumFractionDigits(3);
                wkFormatter.setMinimumFractionDigits(3);
                
                wkTeamEntry = wkTeamEntry.setAttribute("link", wkLink);
                wkTeamEntry.addContent(new Element("teamname").setText(TeamCache.get(wkTeamId).getNickname()));
                wkTeamEntry.addContent(new Element("wins").setText(wkWon + ""));
                wkTeamEntry.addContent(new Element("losses").setText(wkLost+ ""));
                wkTeamEntry.addContent(new Element("pct").setText(wkFormatter.format(wkPct)));
                
                wkFormatter.setMaximumFractionDigits(1);
                wkFormatter.setMinimumFractionDigits(1);
                wkTeamEntry.addContent(new Element("gb").setText(wkFormatter.format(wkGB)));
                wkTeamEntry.addContent(new Element("divwin").setText(scheduleManager.getTeamDivisionWins(wkTeamId, transactionManager.getCurrentStandingsSeason())+""));
                wkTeamEntry.addContent(new Element("divloss").setText(scheduleManager.getTeamDivisionLosses(wkTeamId, transactionManager.getCurrentStandingsSeason())+""));
                
                wkCurrentDivision.addContent(wkTeamEntry);
            }            
            wkResultSet.close();
            wkStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }  
        
        return wkStandings;
    }

    public Element getMasterTeamList()
    {
    	return getMasterTeamList(transactionManager.getCurrentStandingsSeason());
    }
    public Element getMasterTeamList (int inSeason)
    {
        Element wkTeamList = new Element("leagueteams");
        Element wkCurrentDivision = null;
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        final String wkColor1 = "#FFFFFF";
        final String wkColor2 = "#E3E8EE";
        int wkCount = 1;
        int wkDivNum = 0;
         
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.prepareStatement(TEAMLIST_SQL);
            wkStatement.setInt(1, inSeason);
            
            wkResultSet = wkStatement.executeQuery();
            
            
            while (wkResultSet.next())
            {
                int wkThisDiv = wkResultSet.getInt("divisionid");
                if (wkDivNum != wkThisDiv)
                {
                    wkCurrentDivision = new Element("division");
                    wkCurrentDivision = wkCurrentDivision.setAttribute("divname", divisionCache.get(wkThisDiv).getName());
                    wkTeamList.addContent(wkCurrentDivision);
                    wkDivNum = wkThisDiv;
                }
                
                Element wkTeamEntry = new Element("team");
                int wkTeamId = wkResultSet.getInt("teamid");
                String wkScheduleLink = "Controller?page=schedule&team=" + TeamCache.get(wkTeamId).getId() + "&season=" + inSeason +"";
                String wkRosterLink = "Controller?page=roster&team=" + TeamCache.get(wkTeamId).getId() + "&season=" + inSeason +"";
                String wkScoutLink = "Controller?page=scout&team=" + TeamCache.get(wkTeamId).getId() + "";
                //String wkStatsLink = "Controller?page=stats&mode=Enter&team=" + teamCache.get(wkTeamId).getId() + ""; 
                Team wkTeam = TeamCache.get(wkTeamId);
                
                wkTeamEntry.addContent(new Element("teamname").setText(wkTeam.getNickname()));
                wkTeamEntry.addContent(new Element("fullteamname").setText(wkTeam.getCity() + " " + wkTeam.getNickname()));
                wkTeamEntry.addContent(new Element("schedulelink").setText(wkScheduleLink));
                wkTeamEntry.addContent(new Element("rosterlink").setText(wkRosterLink));
                wkTeamEntry.addContent(new Element("scoutlink").setText(wkScoutLink));
                wkTeamEntry.addContent(new Element("id").setText(wkTeamId+""));
                wkTeamEntry.addContent(userManager.getTeamOwnerInfo(wkTeamId));
                //wkTeamEntry.addContent(new Element("statslink").setText(wkStatsLink));
                
                if ( (wkCount-wkCount/2) == wkCount/2)
                {
                    wkTeamEntry.addContent(new Element("color").setText(wkColor1));
                }
                else
                {
                    wkTeamEntry.addContent(new Element("color").setText(wkColor2));
                }
                wkCount++;      

                wkCurrentDivision.addContent(wkTeamEntry);
            }            
            wkResultSet.close();
            wkStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }  
        
        return wkTeamList;
    }
    
    public Element getTeams ()
    {
        Element wkTeams = new Element("teams");
        Statement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
         
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.createStatement();
            
            wkResultSet = wkStatement.executeQuery(TEAMS_SQL);
            
            while (wkResultSet.next())
            {
                Element wkTeamEntry = new Element("team");
                int wkTeamId = wkResultSet.getInt("id");
                
                Team wkTeam = TeamCache.get(wkTeamId);
                wkTeamEntry.addContent(new Element("nickname").setText(wkTeam.getNickname()));
                wkTeamEntry.addContent(new Element("id").setText(wkTeamId + ""));
                wkTeamEntry.addContent(new Element("city").setText(wkTeam.getCity()));
                wkTeamEntry.addContent(new Element("memberid").setText(wkTeam.getMemberid()+""));
                
          
                wkTeams.addContent(wkTeamEntry);
                
            }            
            wkResultSet.close();
            wkStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }  
        
        return wkTeams;
    }
    
   public HashMap<Integer, String> getPlayerIndexesForTeam(int inTeamId, int inYear)
   {
        HashMap<Integer, String> wkPlayers = new HashMap<Integer, String>(50);
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
         
        try
        {
            System.out.println("Looking up indexes for team: " + inTeamId + "  for year " + inYear);
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.prepareStatement(PLAYER_ASSIGNS_SQL);
            
            wkStatement.setInt(1, inTeamId);
            wkStatement.setInt(2, inYear);
            
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next())
            {
                int wkPlayerId = wkResultSet.getInt("playerid");
                wkPlayers.put(Integer.valueOf(wkPlayerId), "Selected");                
            }            
            
            wkResultSet.close();
            wkStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }  
        
        return wkPlayers;   
    }
   
   	public Element getInstructionRosters(int inTeamId, int inYear, int inRoster)
   	{
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        Element wkRosters = new Element("rosters");                 
        try
		{
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.prepareStatement(TEAM_ROSTERS_SQL);          
            wkStatement.setInt(1, inTeamId);
            wkStatement.setInt(2, inYear);
            
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next())
            {
            	Element wkRoster = new Element("roster");
            	wkRosters.addContent(wkRoster);
            	wkRoster.addContent(new Element("season").setText(wkResultSet.getInt("season")+""));
            	wkRoster.addContent(new Element("name").setText(wkResultSet.getString("name")));
            	wkRoster.addContent(new Element("id").setText(wkResultSet.getInt("id")+""));
            	wkRoster.addContent(new Element("teamid").setText(wkResultSet.getInt("teamid")+""));
            	int wkRosterId = wkResultSet.getInt("id");
            	if (wkRosterId == inRoster)
            	{
            		wkRoster.addContent(new Element("Selected").setText("Y"));
            	}               
            }            
            
            wkResultSet.close();
            wkStatement.close();
             
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }  
        
        return wkRosters;   
    }
   	
   	public String getRosterName(int inRoster)
   	{
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        String wkName = null;                 
        try
		{
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.prepareStatement(ROSTER_NAME_SQL);          
            wkStatement.setInt(1, inRoster);
            
            wkResultSet = wkStatement.executeQuery();
            
            if (wkResultSet.next())
            {
            	wkName = wkResultSet.getString("name");
            }            
            
            wkResultSet.close();
            wkStatement.close();
             
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }  
        
        return wkName;   
    }

   public Element getRoster(int inTeamId, int inYear)
   {
   		return getRoster(inTeamId, inYear, 0);
   }

   public Element getRoster(int inTeamId, int inYear, int inRosterId)
   {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        Element wkRoster = new Element("roster");
        wkRoster.setAttribute("teamname", TeamCache.get(inTeamId).getNickname());
        Element wkPlayerElement = new Element("players");
        Element wkPitcherElement = new Element("pitchers");
        Element wkCutElement = new Element("cuts");
        TreeMap<String, Player> wkPlayers = new TreeMap<String, Player>();
        TreeMap<String, Player> wkPitchers = new TreeMap<String, Player>();
        TreeMap<String, Player> wkCuts = new TreeMap<String, Player>();
        HashMap<Integer, String> wkRosterPlayers = null;
        
        if (0 != inRosterId)
        {
        	wkRosterPlayers = getAssignedPlayers(inRosterId);
        	wkRoster.addContent(new Element("id").setText(inRosterId+""));
        	wkRoster.addContent(new Element("name").setText(getRosterName(inRosterId)));
        }
        
                 
        try
        {
            wkRoster.addContent(wkPitcherElement);
            wkRoster.addContent(wkPlayerElement);
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.prepareStatement(PLAYER_ASSIGNS_SQL);
            
            wkStatement.setInt(1, inTeamId);
            wkStatement.setInt(2, inYear);
            
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next())
            {
                int wkPlayerId = wkResultSet.getInt("playerid");
                Player wkPlayer = playerCache.get(wkPlayerId);
                String wkPlayerName = wkPlayer.getLastname() + "," + wkPlayer.getFirstname();
                String wkPosition = wkPlayer.getPosition();
                if (null != wkPosition && wkPlayer.getPosition().equals("P"))
                    wkPitchers.put(wkPlayerName, wkPlayer);
                else
                    wkPlayers.put(wkPlayerName, wkPlayer);
                    
                                
            }            
            
            Collection<PlayerCut> wkTeamCuts = cutList.getTeamCuts(inTeamId);
            
            if (null != wkTeamCuts &&
            	inYear == transactionManager.getCurrentTransactionsSeason() &&
            	wkTeamCuts.size() > 0)
            {
            	wkRoster.addContent(wkCutElement);
            	Iterator<PlayerCut> wkIter = wkTeamCuts.iterator();
            	while (wkIter.hasNext())
            	{
            		PlayerCut cut = (PlayerCut) wkIter.next();
            		Player wkPlayer = playerCache.get(cut.getPlayerId());
            		String wkPlayerName = wkPlayer.getLastname() + "," + wkPlayer.getFirstname();
            		wkCuts.put(wkPlayerName, wkPlayer);
                    
                                
            	}
            }
            
            wkResultSet.close();
            wkStatement.close();
            
            addPlayerElements(wkPlayerElement, wkPlayers, wkRosterPlayers);
            addPlayerElements(wkPitcherElement, wkPitchers, wkRosterPlayers);
            addPlayerElements(wkCutElement, wkCuts, null);
             
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }  
        
        return wkRoster;   
    }
   
   	private HashMap<Integer, String> getAssignedPlayers(int inRosterId)
   	{
   		PreparedStatement wkStatement = null;
   		Connection wkConn = null;
   		ResultSet wkResultSet = null;
   		HashMap<Integer, String> wkAssignedPlayers = new HashMap<Integer, String>();
   		
   		try
		{
   			wkConn = DBUtil.getReadOnlyDBConnection();
   			wkStatement = wkConn.prepareStatement(ROSTER_ASSIGNS_SQL);
   			wkStatement.setInt(1, inRosterId);
   			wkResultSet = wkStatement.executeQuery();
   			while (wkResultSet.next())
   			{
   				Integer wkId = Integer.valueOf(wkResultSet.getInt("PlayerId"));
   				wkAssignedPlayers.put(wkId, "Assigned");
   			}
   			
   			wkResultSet.close();
   			wkStatement.close();
   		}
   		catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }
        
        return wkAssignedPlayers;
   	
   	}

   
   public Element getScoutRoster(int inTeamId, int inYear)
   {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        Element wkRoster = new Element("roster");
        Element wkPlayerElement = new Element("positionplayers");
        Element wkSPElement = new Element("startingpitchers");
        Element wkRPElement = new Element("reliefpitchers");
        TreeMap<String, Player> wkPlayers = new TreeMap<String, Player>();
        TreeMap<String, Player> wkSPitchers = new TreeMap<String, Player>();
        TreeMap<String, Player> wkRPitchers = new TreeMap<String, Player>();
                 
        try
        {
            wkRoster.addContent(wkSPElement);
            wkRoster.addContent(wkRPElement);
            wkRoster.addContent(wkPlayerElement);
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.prepareStatement(PLAYER_ASSIGNS_SQL);
            
            wkStatement.setInt(1, inTeamId);
            wkStatement.setInt(2, inYear);
            
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next())
            {
                int wkPlayerId = wkResultSet.getInt("playerid");
                Player wkPlayer = playerCache.get(wkPlayerId);
                PlayerCard wkCard = cardCache.get(wkPlayerId);
                String wkPlayerName = wkPlayer.getLastname() + "," + wkPlayer.getFirstname();
                //System.out.println ("Looking at player: " + wkPlayerName);
                String wkPosition = wkPlayer.getPosition();
                if (null != wkPosition && wkPlayer.getPosition().equals("P"))
                {
                    if (isStartingPitcher(wkCard))
                        wkSPitchers.put(wkPlayerName, wkPlayer);
                    else
                        wkRPitchers.put(wkPlayerName, wkPlayer);
                }
                else
                    wkPlayers.put(wkPlayerName, wkPlayer);
                    
                                
            }            
            
            wkResultSet.close();
            wkStatement.close();
            
            addPlayerElements(wkPlayerElement, wkPlayers);
            addPlayerElements(wkSPElement, wkSPitchers);
            addPlayerElements(wkRPElement, wkRPitchers);
             
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }  
        
        return wkRoster;   
    }
    
    public  Element getDraftRoster(int inYear)
   {
        Element wkRoster = new Element("roster");
        Element wkPlayerElement = new Element("positionplayers");
        Element wkSPElement = new Element("startingpitchers");
        Element wkRPElement = new Element("reliefpitchers");
        TreeMap<String, Player> wkPlayers = new TreeMap<String, Player>();
        TreeMap<String, Player> wkSPitchers = new TreeMap<String, Player>();
        TreeMap<String, Player> wkRPitchers = new TreeMap<String, Player>();
                 
        try
        {
            wkRoster.addContent(wkSPElement);
            wkRoster.addContent(wkRPElement);
            wkRoster.addContent(wkPlayerElement);

            Iterator<String> wkIter = cardedList.getCardedListArray().iterator();
            
            while (wkIter.hasNext())
            {
                int wkPlayerId = Integer.parseInt(wkIter.next());
                Player wkPlayer = playerCache.get(wkPlayerId);
                PlayerCard wkCard = cardCache.get(wkPlayerId);
                String wkPlayerName = wkPlayer.getLastname() + "," + wkPlayer.getFirstname();
                System.out.println ("Looking at player: " + wkPlayerName);
                String wkPosition = wkPlayer.getPosition();
                if (null != wkPosition && wkPlayer.getPosition().equals("P"))
                {
                    if (isStartingPitcher(wkCard))
                        wkSPitchers.put(wkPlayerName, wkPlayer);
                    else
                        wkRPitchers.put(wkPlayerName, wkPlayer);
                }
                else
                    wkPlayers.put(wkPlayerName, wkPlayer);
                    
                                
            }            
            
            
            addPlayerElements(wkPlayerElement, wkPlayers);
            addPlayerElements(wkSPElement, wkSPitchers);
            addPlayerElements(wkRPElement, wkRPitchers);
             
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        
        return wkRoster;   
    }
   
    private void addPlayerElements(Element inTopElement, TreeMap<String, Player> inPlayers)
    {
    	addPlayerElements(inTopElement, inPlayers, null);
    }
    private void addPlayerElements(Element inTopElement, TreeMap<String, Player> inPlayers, 
    		HashMap<Integer, String> inAssignments)
    {
        Iterator<Player> wkIter = inPlayers.values().iterator();
        final String wkColor1 = "#FFFFFF";
        final String wkColor2 = "#E3E8EE";
        int wkCount = 1;
        PlayerCard wkCard = null;
        boolean wkCheckAssigns = (null != inAssignments);
        
        while (wkIter.hasNext())
        {
            Player wkPlayer = (Player) wkIter.next();
            wkCard = cardCache.get(wkPlayer.getId());
            
            Element wkPlayerElement = new Element("player");
            wkPlayerElement.addContent(new Element("displayname").setText(wkPlayer.getDisplayname()));
            wkPlayerElement.addContent(new Element("bbreflink").setText(wkPlayer.getBbreflink()));
            wkPlayerElement.addContent(new Element("id").setText(wkPlayer.getId()+""));
            
            if (wkCheckAssigns)
            {
            	if (inAssignments.containsKey(Integer.valueOf(wkPlayer.getId())))
            		wkPlayerElement.addContent(new Element("assigned").setText("Y"));
            	else
            		wkPlayerElement.addContent(new Element("assigned").setText("N"));
            }
            if ( (wkCount-wkCount/2) == wkCount/2)
            {
                wkPlayerElement.addContent(new Element("color").setText(wkColor1));
            }
            else
            {
                wkPlayerElement.addContent(new Element("color").setText(wkColor2));
            }
            
            if (null != wkCard)
            {
                wkPlayerElement.addContent(new Element("startinggrade").setText(playerManager.getStartingGrade(wkCard)));
                wkPlayerElement.addContent(new Element("reliefgrade").setText(playerManager.getReliefGrade(wkCard)));
                wkPlayerElement.addContent(new Element("starts").setText(wkCard.getGS()));
                wkPlayerElement.addContent(new Element("ip").setText(wkCard.getPitchIP()));
                wkPlayerElement.addContent(new Element("primary").setText(playerManager.convertPosition(wkCard.getPrimaryPosition())));
                wkPlayerElement.addContent(new Element("def-ratings").setText(playerManager.getPositionList(wkCard)));
                wkPlayerElement.addContent(new Element("speed").setText(wkCard.getSpeedLetter()));
                
                CardAnalyzer wkAnalyzer = new CardAnalyzer(wkCard);
                
                wkPlayerElement.addContent(new Element("one").setText(wkAnalyzer.getOnes()+""));
                wkPlayerElement.addContent(new Element("power").setText(wkAnalyzer.getPower()+""));
                wkPlayerElement.addContent(new Element("walk").setText(wkAnalyzer.getWalks()+""));
                wkPlayerElement.addContent(new Element("seven").setText(wkAnalyzer.getSevens()+""));
                wkPlayerElement.addContent(new Element("eleven").setText(wkAnalyzer.getElevens()+""));
                wkPlayerElement.addContent(new Element("eight").setText(wkAnalyzer.getEights()+""));
                wkPlayerElement.addContent(new Element("nine").setText(wkAnalyzer.getNines()+""));
                wkPlayerElement.addContent(new Element("ten").setText(wkAnalyzer.getTens()+""));
                wkPlayerElement.addContent(new Element("twentytwo").setText(wkAnalyzer.getTwentytwo()+""));
                wkPlayerElement.addContent(new Element("twentyfour").setText(wkAnalyzer.getTwentyfour()+""));
                wkPlayerElement.addContent(new Element("twentysix").setText(wkAnalyzer.getTwentysix()+""));
                wkPlayerElement.addContent(new Element("twentyseven").setText(wkAnalyzer.getTwentyseven()+""));
                wkPlayerElement.addContent(new Element("twentyeight").setText(wkAnalyzer.getTwentyeight()+""));
            }
                
            wkCount++;  
            
            inTopElement.addContent(wkPlayerElement);
        }
    }
    
    public boolean isStartingPitcher(PlayerCard inCard)
    {
        boolean wkSP = false;
        
        try
        {
            int wkSPGrade = Integer.parseInt(inCard.getStartingGrade());
            if (wkSPGrade > 0)
                wkSP = true;
        }
        catch (NumberFormatException wkEx) {;}
        catch (NullPointerException wkEx) {;}
        
        return wkSP;
    }

    @SuppressWarnings ("unused")
    public boolean isReliefPitcher(PlayerCard inCard)
    {
        boolean wkRP = false;
        
        try
        {
            Integer.parseInt(inCard.getStartingGrade());
            wkRP = true;
        }
        catch (NumberFormatException wkEx) {;}
        catch (NullPointerException wkEx) {;}
        
        return wkRP;
    }
    
    public  void updateTeam(int inTeamId, String inPlayerList, int inYear)
    {
        StringTokenizer wkPlayers = new StringTokenizer(inPlayerList, ",");
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        //System.out.println("For team: " + inTeamId + "  players: " + inPlayerList);
         
        try
        {
            
            wkConn = DBUtil.getDBConnection();
            wkStatement = wkConn.prepareStatement(PLAYER_ASSIGNS_CHECK_SQL);
            
            wkStatement.setInt(1, inTeamId);
            wkStatement.setInt(2, inYear);
            
            /*
            wkStatement.executeUpdate();
            wkStatement.close();
            
            wkStatement = wkConn.prepareStatement(PLAYER_ASSIGNS_ADD_SQL);
            wkStatement.setInt(2, inTeamId);
            wkStatement.setInt(3, inYear);
            
            */
            while (wkPlayers.hasMoreTokens())
            {
                int wkPlayer = Integer.parseInt(wkPlayers.nextToken());
                wkStatement.setInt(3, wkPlayer);
                
                ResultSet wkResults = wkStatement.executeQuery();
                if (!wkResults.next())
                {
                    transactionManager.addPlayer(wkPlayer, inTeamId);
                }
                
                wkResults.close();
            }            
            wkStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }         
    }

    public String getTeamsInDivision(int inTeamId, int inYear)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        StringBuffer wkTeamList = new StringBuffer(40);
         
        try
        {
            wkConn = DBUtil.getDBConnection();
            wkStatement = wkConn.prepareStatement(GET_TEAMS_IN_DIVISION_SQL);
            wkStatement.setInt(1, inTeamId);
            wkStatement.setInt(2, inYear);
            wkStatement.setInt(3, inYear);
            
            wkResultSet = wkStatement.executeQuery();
            if (wkResultSet.next())
            {
                wkTeamList.append(wkResultSet.getInt("teamid")+"");
                while(wkResultSet.next())
                {
                    wkTeamList.append(",").append(wkResultSet.getInt("teamid")+"");
                }
                wkResultSet.close();
                wkStatement.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null!= wkStatement)
                    wkStatement.close();
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }    
        
        return wkTeamList.toString();
    }
    
    
    private void createMaps()
    {
        //teamCache = new TeamCache();
        //divisionCache = new DivisionCache();
    }
        
    
    public int getPlayerTeam(int inPlayerId, int inYear)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        int wkTeamId = 0;
         
        try
        {
            wkConn = DBUtil.getDBConnection();
            wkStatement = wkConn.prepareStatement(GET_PLAYER_TEAM_SQL);
            wkStatement.setInt(1, inPlayerId);
            wkStatement.setInt(2, inYear);
            
            wkResultSet = wkStatement.executeQuery();
            if (wkResultSet.next())
            {
                wkTeamId = wkResultSet.getInt("teamid");
            }
            wkResultSet.close();
            wkStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }
        
        return wkTeamId;
    }
  

    public void processAddNewInstructionRoster(String inTeam, String inRoster, String inYear, HashMap<String, String> inRecords)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
         
        try
        {
            wkConn = DBUtil.getDBConnection();
            wkStatement = wkConn.prepareStatement(INSERT_ROSTER_SQL);
            wkStatement.setInt(1, Integer.parseInt(inTeam));
            wkStatement.setInt(2, Integer.parseInt(inYear));
            wkStatement.setString(3, inRoster);
            
            wkStatement.executeUpdate();
            wkStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }
    }
    
    public  void processEditInstructionRoster(String wkRoster, HashMap<String, String> inRecords)
    {
    	
    }
       
}
