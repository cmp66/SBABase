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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import org.jdom.Element;

import com.wahoo.apba.database.GenericStatRecord;
import com.wahoo.apba.database.Player;
import com.wahoo.apba.database.SeriesStatRecord;
import com.wahoo.apba.database.StatRecord;
import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.apba.excel.TeamStat;
import com.wahoo.apba.web.util.ListMember;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class StatsManager
{
    //static StatsManager _manager = null;
    private TransactionManager transactionManager = null;
    private TeamManager teamManager = null;
    private PlayerCache playerCache = null;
    private TeamStat teamStat = null;
    
    private String ipQual = null;
    private String abQual = null;
    
    static final int LEADER_COUNT = 10;
    static final int MIN_PA = 250;
    static final int MIN_IP = 80;

    static final String GET_SERIES_TEAM_STATS = "select * from seriesstatrecords where season = ? and series = ? and reportingteamid = ? and statsteamid = ?";
    static final String GET_SEASON_STATS_FROM_SERIES = "select * from seriesstatrecords where season = ? and reportingteamid = ? and statsteamid = ?";
    static final String GET_TEAM_STATS = "select * from statrecords where season = ? and teamid = ?";
    static final String GET_PLAYER_STATS = "select * from statrecords where playerid = ? order by season";
    
    static final String DELETE_TEAM_STATS = "delete from statrecords where season = ? and teamid = ?";
    
    private static final String AVERAGE_SQL = "select playerid, SUM(bat_ab) AS ab, SUM(bat_walks) as bb, SUM(bat_hits) as hits FROM statrecords where season = ? GROUP BY playerid " +
                                              "ORDER BY SUM(bat_hits)/(SUM(bat_ab)+0.1) desc";
    private static final String OBP_SQL =     "select playerid, SUM(bat_ab) AS ab, SUM(bat_walks) as bb, SUM(bat_hits) as hits FROM statrecords where season = ? GROUP BY playerid " +
                                              "ORDER BY (SUM(bat_hits)+SUM(bat_walks))/((SUM(bat_ab)+SUM(bat_walks)+0.1)) desc";
    private static final String SLUG_SQL =    "select playerid, SUM(bat_ab) AS ab, SUM(bat_walks) as bb, SUM(bat_hits) as hits,SUM(bat_doubles) as doubles, SUM(bat_triples) as triples," +
                                              "SUM(bat_hr) as hr FROM statrecords where season = ? GROUP BY playerid " +
                                              "ORDER BY (SUM(bat_hits)+SUM(bat_doubles)+2*SUM(bat_triples)+3*SUM(bat_hr))/(SUM(bat_ab)+0.1) desc";
//    private static final String DOUBLES_SQL =  "select playerid, SUM(?) AS ? FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(bat_doubles) desc";
//    private static final String TRIPLES_SQL = "select playerid, SUM(?) AS ? FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(?) desc";
//    private static final String HR_SQL =      "select playerid, SUM(bat_hr) AS ? FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(bat_hr) desc";
//    private static final String WALKS_SQL =   "select playerid, SUM(bat_walks) AS ? FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(bat_walks) desc";
//    private static final String STRIKEOUTS_SQL = "select playerid, SUM(bat_strikeouts) AS Strikeouts FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(bat_strikeouts) desc";
//    private static final String HITS_SQL =    "select playerid, SUM(bat_hits) AS Hits FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(bat_hits) desc";
//    private static final String SB_SQL =      "select playerid, SUM(bat_sb) AS SB FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(bat_sb) desc";
//    private static final String ERRORS_SQL =  "select playerid, SUM(errors) AS Errors FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(errors) desc";
//    private static final String RUNS_SQL =    "select playerid, SUM(bat_runs) AS Runs FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(bat_runs) desc";
//    private static final String RBI_SQL =    "select playerid, SUM(bat_rbi) AS RBI FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(bat_rbi) desc";
//    private static final String HBP_SQL =      "select playerid, SUM(bat_hpb) AS SB FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(bat_hbp) desc";
//    private static final String GP_SQL =     "select playerid, SUM(pitch_gp) AS gs FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(pitch_gp) desc";
//    private static final String GS_SQL =     "select playerid, SUM(pitch_gs) AS gs FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(pitch_gs) desc";
//    private static final String CG_SQL =     "select playerid, SUM(pitch_cg) AS cg FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(pitch_cg) desc";
//    private static final String SHO_SQL =     "select playerid, SUM(pitch_sho) AS sho FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(pitch_sho) desc";
//    private static final String WINS_SQL =    "select playerid, SUM(pitch_wins) AS wins FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(pitch_wins) desc";
//    private static final String LOSS_SQL =    "select playerid, SUM(pitch_loss) AS loss FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(pitch_loss) desc";
//    private static final String SAVE_SQL =    "select playerid, SUM(pitch_save) AS cg FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(pitch_save) desc";
//    private static final String IP_SQL =     "select playerid, SUM(pitch_ipfull) AS ip FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(pitch_ipfull) desc";
//    private static final String BBA_SQL =     "select playerid, SUM(pitch_walks) AS walks FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(pitch_walks) desc";
//    private static final String PSTRIKEOUTS_SQL = "select playerid, SUM(pitch_strikeouts) AS strikeouts FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(pitch_strikeouts) desc";
//    private static final String HRA_SQL =     "select playerid, SUM(pitch_hr) AS hr FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(pitch_hr) desc";
    private static final String ERA_SQL =     "select playerid, SUM(pitch_ipfull) AS ipfull, SUM(pitch_ipfract) as ipfract, SUM(pitch_er) as er " + 
                                              "FROM statrecords where season = ? GROUP BY playerid " +
                                              "ORDER BY (SUM(pitch_er)*9)/(SUM(pitch_ipfull) + SUM(pitch_ipfract)*0.3 + 0.001) asc";
//    private static final String SINGLE_VAL_SQL = "select playerid, SUM(?) AS ? FROM statrecords where season = ? GROUP BY playerid " +
//                                              "ORDER BY SUM(?) desc";
    
    private static final String SERIES_SUM_SQL = "select statsteamid, sum(bat_runs) as bat_runs, sum(bat_hits) as bat_hits, " +
                                              "sum(bat_walks) as bat_walks, sum(bat_strikeouts) as bat_so, sum(games) as games, sum(bat_rbi) as bat_rbi, " +
                                              "sum(bat_ab) as bat_ab, sum(bat_doubles) as bat_doubles, sum(bat_triples) as bat_triples, sum(bat_hr) as bat_hr, " +
                                              "sum(pitch_gs) as gs, sum(pitch_wins) as wins, sum(pitch_loss) as pitch_loss, sum (pitch_save) as pitch_save, " +
                                              "sum(pitch_cg) as pitch_cg, sum(pitch_sho) as pitch_sho, sum(pitch_ipfull) as pitch_ipfull, sum(pitch_ipfract) as pitch_ipfract, " +
                                              "sum(pitch_hits) as pitch_hits, sum(pitch_walks) as pitch_walks, sum(pitch_gp) as pitch_gp, " + 
                                              "sum(pitch_strikeouts) as pitch_so, sum(pitch_runs) as pitch_runs, sum(pitch_er) as pitch_er  " +
                                              "from seriesstatrecords where series = ? and statsteamid = ? group by series,statsteamid";
    /** Creates a new instance of TeamManager */
    private StatsManager ()
    {
    }
    
    /*
    public static StatsManager getInstance()
    {
        if (null == _manager)
            _manager = new StatsManager();
            
        return _manager;
    }
    
    */
    
    public void init()
    {
    }
    
    public void setTransactionManager (TransactionManager inMgr)
    {
    	this.transactionManager = inMgr;
    }
    
    public void setTeamManager (TeamManager inMgr)
    {
    	this.teamManager = inMgr;
    }
    
    public void setTeamStat (TeamStat inStat)
    {
    	this.teamStat = inStat;
    }
    
    public void setPlayerCache (PlayerCache inCache)
    {
    	this.playerCache = inCache;
    }
    
    public void setIpQual(String inVal)
    {
    	ipQual = inVal;
    }
    
    public void setabQual(String inVal)
    {
    	abQual = inVal;
    }
    
    

    public Element getYears()
    {
        int wkCurrentYear = transactionManager.getCurrentTransactionsSeason();
        Element wkYears = new Element("Years");
        
        for (int i = 1989; i<= wkCurrentYear; i++)
        {
            Element wkYear = new Element("Year").addContent(new Element("Value").setText(i+""));
            wkYears.addContent(wkYear);
        }
        
        return wkYears;
    }
    
    public Element getErrors(ArrayList<String> inErrors)
    {
        Element wkErrors = new Element("Errors");
        Iterator<String> wkIter = inErrors.iterator();
        
        while (wkIter.hasNext())
        {
            String wkErrorString = wkIter.next();
            Element wkError = new Element("Error").addContent(new Element("ErrorString").setText(wkErrorString));
            wkErrors.addContent(wkError);
        }
        
        return wkErrors;
    }
            
    

    
    public Element getTeamStats(int inYear, int inTeam)
    {
        Element wkTeamStats = null;
        Element wkBattingStats = null;
        Element wkPitchingStats = null;
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        TreeMap<String, GenericStatRecord> wkPlayers = new TreeMap<String, GenericStatRecord>();
        TreeMap<String, GenericStatRecord> wkPitchers = new TreeMap<String, GenericStatRecord>();
        
        try
        {
            wkTeamStats = new Element("TeamStats");
            wkTeamStats.addContent(new Element("year").setText(inYear+""));
            wkTeamStats.addContent(new Element("teamid").setText(inTeam+""));
            wkTeamStats.addContent(new Element("team").setText(TeamCache.get(inTeam).getNickname()));
            
            wkBattingStats = new Element("battingstats");
            wkPitchingStats = new Element("pitchingstats");
            wkTeamStats.addContent(wkBattingStats);
            wkTeamStats.addContent(wkPitchingStats);
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(GET_TEAM_STATS);
            wkStatement.setInt(1, inYear);
            wkStatement.setInt(2, inTeam);
            ResultSet wkResults = wkStatement.executeQuery();
            
            while (wkResults.next())
            {
                StatRecord wkRecord = new StatRecord(wkResults);
                String wkLastName = playerCache.get(wkRecord.getPlayerid()).getLastname() +
                                    playerCache.get(wkRecord.getPlayerid()).getFirstname();
                String wkPosition = playerCache.get(wkRecord.getPlayerid()).getPosition();
                boolean wkIsPitcher = (null != wkPosition && wkPosition.equals("P"));
                
                if (wkIsPitcher)
                    wkPitchers.put(wkLastName, wkRecord);
                else
                    wkPlayers.put(wkLastName, wkRecord);                
            }
            
            createBattingStats(wkPlayers, wkBattingStats, false);
            createBattingStats(wkPitchers, wkBattingStats, false);
            createPitchingStats(wkPitchers, wkPitchingStats, false);
            
            wkResults.close();
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
        return wkTeamStats;
    }
    
    public Element getPlayerCareerStats(int inId)
    {
        Element wkCareerStats = null;
        Element wkStats = null;
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        TreeMap<String, GenericStatRecord> wkStatRecords = new TreeMap<String, GenericStatRecord>();
        int wkCount = 0;
        
        try
        {
            wkCareerStats = new Element("PlayerStats");
            Player wkPlayer = playerCache.get(inId);

            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.prepareStatement(GET_PLAYER_STATS);
            wkStatement.setInt(1, inId);
            ResultSet wkResults = wkStatement.executeQuery();
            
            while (wkResults.next())
            {
                StatRecord wkRecord = new StatRecord(wkResults);
                
                wkStatRecords.put(wkRecord.getSeason()*10 + wkCount + "", wkRecord); 
                wkCount++;
            }
            
            
            if (!wkPlayer.isPitcher())
            {
            	wkStats = new Element("battingstats");
            	createBattingStats(wkStatRecords, wkStats, false);
            }
            else
            {
            	wkStats = new Element("pitchingstats");
            	createPitchingStats(wkStatRecords, wkStats, false);
            }
            
            wkCareerStats.addContent(wkStats);
            
            wkResults.close();
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
        return wkCareerStats;
    }
    
    public Element getTeamSeriesStatsSummary(int inYear, int inSeries, int ReportingTeam, int inOtherTeam)
    {
        return getTeamSeriesStatsInternal(inYear, inSeries, ReportingTeam, inOtherTeam, true, true);
    } 

    public Element getTeamSeriesStats(int inYear, int inSeries, int ReportingTeam, int inOtherTeam)
    {
        return getTeamSeriesStatsInternal(inYear, inSeries, ReportingTeam, inOtherTeam, false, false);
    }
    
    public Element getTeamSeriesStatsInternal(int inYear, int inSeries, int inReportingTeam, int inOtherTeam, boolean filterNoUse, boolean getOtherTeam)
    {
        Element wkTeamStats = null;
        Element wkSeriesStats = null;
        Element wkBattingStats = null;
        Element wkPitchingStats = null;
        Element wkTotalBattingStats = null;
        Element wkTotalPitchingStats = null;
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        TreeMap<String, GenericStatRecord> wkPlayers = new TreeMap<String, GenericStatRecord>();
        TreeMap<String, GenericStatRecord> wkPitchers = new TreeMap<String, GenericStatRecord>();
        
        //boolean wkGetOtherTeam = Boolean.getBoolean("EnterOtherStats");
        System.out.println("Get other team is " + getOtherTeam);
        //System.out.println("Processing year: " + inYear + "series: " + inSeries + " rteam: " + inReportingTeam + " oteam: " + inOtherTeam);
        
        
        try
        {
            TeamSeriesSummary summary = validateSeriesStats(inYear, inSeries, inReportingTeam, inOtherTeam);
            checkForSeriesStats(inYear, inSeries, inReportingTeam, inOtherTeam);
            
            wkSeriesStats = new Element("SeriesStats");
            wkSeriesStats.addContent(new Element("Series").setText(inSeries+""));
            wkSeriesStats.addContent(new Element("Year").setText(inYear+""));
            wkSeriesStats.addContent(new Element("OtherTeam").setText(inOtherTeam+""));
            wkSeriesStats.addContent(new Element("ReportingTeam").setText(inReportingTeam+""));
            wkSeriesStats.addContent(new Element("ReportName").setText(TeamCache.get(inReportingTeam).getNickname()));
            wkSeriesStats.addContent(new Element("OtherName").setText(TeamCache.get(inOtherTeam).getNickname()));
            
            Element wkErrors = new Element("Errors");
            wkSeriesStats.addContent(wkErrors);
            
            for (String errorString : summary.getStatus())
                wkErrors.addContent(new Element("ErrorString").setText(errorString));
         
            wkTeamStats = new Element("ReportingTeamStats");
            wkSeriesStats.addContent(wkTeamStats);
            wkTeamStats.addContent(new Element("year").setText(inYear+""));
            wkTeamStats.addContent(new Element("teamid").setText(inReportingTeam+""));
            wkTeamStats.addContent(new Element("team").setText(TeamCache.get(inReportingTeam).getNickname()));
            
            wkBattingStats = new Element("battingstats");
            wkPitchingStats = new Element("pitchingstats");
            wkTeamStats.addContent(wkBattingStats);
            wkTeamStats.addContent(wkPitchingStats);
            wkConn = DBUtil.getReadOnlyDBConnection();

            //Get full set of stats for reporting team
            wkStatement = wkConn.prepareStatement(GET_SERIES_TEAM_STATS);
            wkStatement.setInt(1, inYear);
            wkStatement.setInt(2, inSeries);
            wkStatement.setInt(3, inReportingTeam);
            wkStatement.setInt(4, inReportingTeam);
            ResultSet wkResults = wkStatement.executeQuery();
            
            while (wkResults.next())
            {
                SeriesStatRecord wkRecord = new SeriesStatRecord(wkResults);
                String wkLastName = playerCache.get(wkRecord.getPlayerid()).getLastname() +
                                    playerCache.get(wkRecord.getPlayerid()).getFirstname();
                String wkPosition = playerCache.get(wkRecord.getPlayerid()).getPosition();
                boolean wkIsPitcher = (null != wkPosition && wkPosition.equals("P"));
                
                if (wkIsPitcher)
                    wkPitchers.put(wkLastName, wkRecord);
                else
                    wkPlayers.put(wkLastName, wkRecord);                
            }
            
            createBattingStats(wkPlayers, wkBattingStats, filterNoUse);
            createBattingStats(wkPitchers, wkBattingStats, filterNoUse);
            createPitchingStats(wkPitchers, wkPitchingStats, filterNoUse);
            
            wkResults.close();
            wkStatement.close();

            wkPitchers.clear();
            wkPlayers.clear();
            wkPlayers.put("Team", summary.getHomeTeamStats());
            wkPitchers.put("Team", summary.getHomeTeamStats());
            wkTotalBattingStats = new Element("totalbattingstats");
            wkTotalPitchingStats = new Element("totalpitchingstats");
            wkTeamStats.addContent(wkTotalBattingStats);
            wkTeamStats.addContent(wkTotalPitchingStats);
            
            createBattingStats(wkPlayers, wkTotalBattingStats, false);
            createPitchingStats(wkPitchers, wkTotalPitchingStats, false);

            
            if (getOtherTeam)
            {
                //Get stats for the other team
                
                wkPlayers = new TreeMap<String, GenericStatRecord>();
                wkPitchers = new TreeMap<String, GenericStatRecord>();
        
                wkTeamStats = new Element("OtherTeamStats");
                wkSeriesStats.addContent(wkTeamStats);
                wkTeamStats.addContent(new Element("year").setText(inYear+""));
                wkTeamStats.addContent(new Element("teamid").setText(inOtherTeam+""));
                wkTeamStats.addContent(new Element("team").setText(TeamCache.get(inOtherTeam).getNickname()));
            
                wkBattingStats = new Element("battingstats");
                wkPitchingStats = new Element("pitchingstats");
                wkTeamStats.addContent(wkBattingStats);
                wkTeamStats.addContent(wkPitchingStats);

                //Get full set of stats for reporting team
                wkStatement = wkConn.prepareStatement(GET_SERIES_TEAM_STATS);
                wkStatement.setInt(1, inYear);
                wkStatement.setInt(2, inSeries);
                wkStatement.setInt(3, inOtherTeam);
                wkStatement.setInt(4, inOtherTeam);
                wkResults = wkStatement.executeQuery();
            
                while (wkResults.next())
                {
                    SeriesStatRecord wkRecord = new SeriesStatRecord(wkResults);
                    String wkLastName = playerCache.get(wkRecord.getPlayerid()).getLastname() +
                                        playerCache.get(wkRecord.getPlayerid()).getFirstname();
                    String wkPosition = playerCache.get(wkRecord.getPlayerid()).getPosition();
                    boolean wkIsPitcher = (null != wkPosition && wkPosition.equals("P"));
                    
                    if (wkIsPitcher)
                        wkPitchers.put(wkLastName, wkRecord);
                    else
                        wkPlayers.put(wkLastName, wkRecord);                
                }
            
                createBattingStats(wkPlayers, wkBattingStats, filterNoUse);
                createBattingStats(wkPitchers, wkBattingStats, filterNoUse);
                createPitchingStats(wkPitchers, wkPitchingStats, filterNoUse);
            
                wkPitchers.clear();
                wkPlayers.clear();
                wkPlayers.put("Team", summary.getVisitingTeamStats());
                wkPitchers.put("Team", summary.getVisitingTeamStats());
                wkTotalBattingStats = new Element("totalbattingstats");
                wkTotalPitchingStats = new Element("totalpitchingstats");
                wkTeamStats.addContent(wkTotalBattingStats);
                wkTeamStats.addContent(wkTotalPitchingStats);
                
                createBattingStats(wkPlayers, wkTotalBattingStats, false);
                createPitchingStats(wkPitchers, wkTotalPitchingStats, false);
                
                wkResults.close();
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
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }
        return wkSeriesStats;
    }


    public void calculateTeamYearlyStats(int inYear, int inTeam)
    {
    	StatsCalculator wkCalc = getStatsCalculator(inYear, inTeam);
    	wkCalc.start();
    }
        
    private  void checkForSeriesStats(int inYear, int inSeries, int inReportingTeam, int inOtherTeam)
    {
        
        HashMap<Integer, String> wkPlayers = getStatPlayerList(transactionManager.getCurrentTransactionsSeason(), inReportingTeam);
                
        Iterator<Integer> wkIter = wkPlayers.keySet().iterator();
                
        while(wkIter.hasNext())
        {
            int wkPlayer = (wkIter.next()).intValue();
            transactionManager.createSeriesStatRecordIfMissing (inYear, inSeries, inReportingTeam, inReportingTeam, wkPlayer);
        }
        
        wkPlayers = null;
        wkPlayers = getStatPlayerList(transactionManager.getCurrentTransactionsSeason(), inOtherTeam);
                
        wkIter = null;
        wkIter = wkPlayers.keySet().iterator();
                
        while(wkIter.hasNext())
        {
            int wkPlayer = ((Integer)wkIter.next()).intValue();
            transactionManager.createSeriesStatRecordIfMissing (inYear, inSeries, inReportingTeam, inOtherTeam, wkPlayer);
        }
    }
    
    public TeamSeriesSummary validateSeriesStats(int inYear, int inSeries, int inReportingTeam, int inOtherTeam)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        TeamSeriesSummary wkSummary = new TeamSeriesSummary();
        SeriesStatRecord wkHomeStats = new SeriesStatRecord();
        SeriesStatRecord wkVisitingStats = new SeriesStatRecord();
        
        boolean valid = true;
        ArrayList<String> errors = new ArrayList<String>();
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(SERIES_SUM_SQL);
            wkStatement.setInt(1, inSeries);
            wkStatement.setInt(2, inReportingTeam);
            ResultSet wkResults = wkStatement.executeQuery();
            
            if (wkResults.next())
            {
               wkHomeStats.setTeamid(inReportingTeam);
               //wkHomeStats.setPlayerid(inReportingTeam);
               wkHomeStats.setReportingTeam(inReportingTeam);
               wkHomeStats.setSeason(inYear);
               wkHomeStats.setSeries(inSeries);
               wkHomeStats.setBat_runs(wkResults.getInt("bat_runs")); 
               wkHomeStats.setBat_hits(wkResults.getInt("bat_hits"));
               wkHomeStats.setBat_walks(wkResults.getInt("bat_walks"));
               wkHomeStats.setBat_strikeouts(wkResults.getInt("bat_so"));
               wkHomeStats.setGames(wkResults.getInt("games"));
               wkHomeStats.setBat_ab(wkResults.getInt("bat_ab"));
               wkHomeStats.setBat_doubles(wkResults.getInt("bat_doubles"));
               wkHomeStats.setBat_triples(wkResults.getInt("bat_triples"));
               wkHomeStats.setBat_hr(wkResults.getInt("bat_hr"));
               wkHomeStats.setBat_rbi(wkResults.getInt("bat_rbi"));
               
               
               wkHomeStats.setPitch_gs(wkResults.getInt("gs"));
               wkHomeStats.setPitch_wins(wkResults.getInt("wins"));
               wkHomeStats.setPitch_loss(wkResults.getInt("pitch_loss"));
               wkHomeStats.setPitch_hits(wkResults.getInt("pitch_hits"));
               wkHomeStats.setPitch_walks(wkResults.getInt("pitch_walks"));
               wkHomeStats.setPitch_strikeouts(wkResults.getInt("pitch_so"));
               wkHomeStats.setPitch_runs(wkResults.getInt("pitch_runs"));
               wkHomeStats.setPitch_er(wkResults.getInt("pitch_er"));
               wkHomeStats.setPitch_gp(wkResults.getInt("pitch_gp"));
               wkHomeStats.setPitch_ipfract(wkResults.getInt("pitch_ipfract"));
               wkHomeStats.setPitch_ipfull(wkResults.getInt("pitch_ipfull"));
               
               while (wkHomeStats.getPitch_ipfract() > 2)
               {
                   int full = wkHomeStats.getPitch_ipfull();
                   int fract = wkHomeStats.getPitch_ipfract();
                   wkHomeStats.setPitch_ipfull(full + 1);
                   wkHomeStats.setPitch_ipfract(fract - 3);
               }
               
               wkSummary.setHomeTeamStats(wkHomeStats);
            }
            
            wkResults.close();

            wkStatement.setInt(2, inOtherTeam);
            wkResults = wkStatement.executeQuery();
            
            if (wkResults.next())
            {
                wkVisitingStats.setTeamid(inOtherTeam);
                //wkVisitingStats.setPlayerid(inReportingTeam);
                wkVisitingStats.setReportingTeam(inReportingTeam);
                wkVisitingStats.setSeason(inYear);
                wkVisitingStats.setSeries(inSeries);

                
                wkVisitingStats.setBat_runs(wkResults.getInt("bat_runs")); 
                wkVisitingStats.setBat_hits(wkResults.getInt("bat_hits"));
                wkVisitingStats.setBat_walks(wkResults.getInt("bat_walks"));
                wkVisitingStats.setBat_strikeouts(wkResults.getInt("bat_so"));
                wkVisitingStats.setGames(wkResults.getInt("games"));
                wkVisitingStats.setBat_ab(wkResults.getInt("bat_ab"));
                wkVisitingStats.setBat_doubles(wkResults.getInt("bat_doubles"));
                wkVisitingStats.setBat_triples(wkResults.getInt("bat_triples"));
                wkVisitingStats.setBat_hr(wkResults.getInt("bat_hr"));
                wkVisitingStats.setBat_rbi(wkResults.getInt("bat_rbi"));
                
                
                wkVisitingStats.setPitch_gs(wkResults.getInt("gs"));
                wkVisitingStats.setPitch_wins(wkResults.getInt("wins"));
                wkVisitingStats.setPitch_loss(wkResults.getInt("pitch_loss"));
                wkVisitingStats.setPitch_hits(wkResults.getInt("pitch_hits"));
                wkVisitingStats.setPitch_walks(wkResults.getInt("pitch_walks"));
                wkVisitingStats.setPitch_strikeouts(wkResults.getInt("pitch_so"));
                wkVisitingStats.setPitch_runs(wkResults.getInt("pitch_runs"));
                wkVisitingStats.setPitch_er(wkResults.getInt("pitch_er"));
                wkVisitingStats.setPitch_gp(wkResults.getInt("pitch_gp"));
                wkVisitingStats.setPitch_ipfract(wkResults.getInt("pitch_ipfract"));
                wkVisitingStats.setPitch_ipfull(wkResults.getInt("pitch_ipfull"));

                while (wkVisitingStats.getPitch_ipfract() > 2)
                {
                    int full = wkVisitingStats.getPitch_ipfull();
                    int fract = wkVisitingStats.getPitch_ipfract();
                    wkVisitingStats.setPitch_ipfull(full + 1);
                    wkVisitingStats.setPitch_ipfract(fract - 3);
                }
                
                wkSummary.setVisitingTeamStats(wkVisitingStats);
            }
            
            wkResults.close();
            
            if (wkHomeStats.getBat_runs() != wkVisitingStats.getPitch_runs()) errors.add("Home batting runs not equal to Visitor pitching runs");
            if (wkVisitingStats.getBat_runs() != wkHomeStats.getPitch_runs()) errors.add("Visitor batting runs not equal to Home pitching runs");
            if (wkHomeStats.getBat_hits() != wkVisitingStats.getPitch_hits()) errors.add("Home batting hits not equal to Visitor pitching hits");
            if (wkVisitingStats.getBat_hits() != wkHomeStats.getPitch_hits()) errors.add("Visitor batting hits not equal to Home pitching hits");
            if (wkHomeStats.getBat_walks() != wkVisitingStats.getPitch_walks()) errors.add("Home batting walks not equal to Visitor pitching walks");
            if (wkVisitingStats.getBat_walks() != wkHomeStats.getPitch_walks()) errors.add("Visitor batting walks not equal to Home pitching walks");
            if (wkHomeStats.getBat_strikeouts() != wkVisitingStats.getPitch_strikeouts()) errors.add("Home batting strikeouts not equal to Visitor pitching strikeouts");
            if (wkVisitingStats.getBat_strikeouts() != wkHomeStats.getPitch_strikeouts()) errors.add("Visitor batting strikeouts not equal to Home pitching strikeouts");
            if (wkHomeStats.getPitch_wins() != wkVisitingStats.getPitch_loss()) errors.add("Home Pitching wins not equal to Visitor pitching loss");
            if (wkHomeStats.getPitch_loss() != wkVisitingStats.getPitch_wins()) errors.add("Home Pitching loss not equal to Visitor pitching wins");
            if (wkHomeStats.getPitch_gs() != wkVisitingStats.getPitch_gs()) errors.add("Home Pitching starts not equal to Visitor pitcher starts");

            int homeXbh = wkHomeStats.getBat_doubles() + wkHomeStats.getBat_triples() + wkHomeStats.getBat_hr();
            int visitXbh = wkVisitingStats.getBat_doubles() + wkVisitingStats.getBat_triples() + wkVisitingStats.getBat_hr();

            if (wkHomeStats.getBat_hits() < homeXbh) errors.add("Home Extra Base Hits (D,T,HR) exceeds total number of hits.");
            if (wkVisitingStats.getBat_hits() < visitXbh) errors.add("Visiting Extra Base Hits (D,T,HR) exceeds total number of hits.");
            if (wkHomeStats.getBat_rbi() > wkHomeStats.getBat_runs()) errors.add("Home RBI's greater than home total runs");
            if (wkVisitingStats.getBat_rbi() > wkVisitingStats.getBat_runs()) errors.add("Visiting RBI's greater than visit total runs");
            if (wkHomeStats.getPitch_save() > wkHomeStats.getPitch_wins()) errors.add("Home saves greater than home wins");
            if (wkVisitingStats.getPitch_save() > wkVisitingStats.getPitch_wins()) errors.add("Visiting saves greater than visiting wins");

            if (wkHomeStats.getGames() > 0 &&
                wkVisitingStats.getGames() > 0 &&
                wkHomeStats.getGames() >= wkHomeStats.getPitch_gs()*9 && 
                wkVisitingStats.getGames()*9 >= wkVisitingStats.getPitch_gs()*9)
            {
                wkSummary.setStatsEntered(true);
                wkSummary.setIncomplete(false);
            }
            else if (wkHomeStats.getGames() > 0 ||
                    wkVisitingStats.getGames() > 0)
            {
            	wkSummary.setIncomplete(true);
            	wkSummary.setStatsEntered(false);
                errors.add(0,"Not enough games played entered for either the visiting or home team");
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
                if (null != wkStatement)
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

        if (errors.size() > 0)
            valid = false;
        else
            errors.add("Series stats validated ok");
        
        String[] errorStrings = new String[errors.size()];
        wkSummary.setStatus(errors.toArray(errorStrings));
        wkSummary.setValidStats(valid);

        
        return wkSummary;
    }
    

    public Vector<ListMember> getPlayerListForYear(int inYear, int inTeam)
    {
        Vector<ListMember> wkMembers = new Vector<ListMember>(250, 5);
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        HashMap<Integer, String> wkSelected = new HashMap<Integer, String>(50);
        boolean lclSelected = false;
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(GET_TEAM_STATS);
            wkStatement.setInt(1, inYear);
            wkStatement.setInt(2, inTeam);
            ResultSet wkResults = wkStatement.executeQuery();
            
            while (wkResults.next())
            {
                GenericStatRecord wkRecord = new StatRecord(wkResults);
                wkSelected.put(Integer.valueOf(wkRecord.getPlayerid()), "SELECTED");
            };

            Enumeration<Integer> wkPlayers = playerCache.getAllPlayerIDs();

            while (wkPlayers.hasMoreElements())
            {
                Integer wkPlayerId = (Integer) wkPlayers.nextElement();
                Player wkPlayer = playerCache.get(wkPlayerId.intValue());

                lclSelected = wkSelected.containsKey(wkPlayerId);

                ListMember wkNewMember = new ListMember();

                wkNewMember.setId(wkPlayerId.toString());
                wkNewMember.setSelected(lclSelected);
                wkNewMember.addDisplayString(wkPlayer.getLastname() + "," + wkPlayer.getFirstname());
                     
                wkMembers.add(wkNewMember);
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
                if (null != wkStatement)
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
        
        return wkMembers;
    }

    public static HashMap<Integer, String> getStatPlayerList(int inYear, int inTeam)
    {
        HashMap<Integer, String> wkMembers = new HashMap<Integer, String>(45);
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(GET_TEAM_STATS);
            wkStatement.setInt(1, inYear);
            wkStatement.setInt(2, inTeam);
            ResultSet wkResults = wkStatement.executeQuery();
            
            while (wkResults.next())
            {
                GenericStatRecord wkRecord = new StatRecord(wkResults);
                wkMembers.put(Integer.valueOf(wkRecord.getPlayerid()), "SELECTED");
            };          
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
                if (null != wkStatement)
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
        
        return wkMembers;
    }
    


    public void updateTeamList(int inYear, int inTeamId, String inPlayerList)
    {
        StringTokenizer wkPlayers = new StringTokenizer(inPlayerList, ",");
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        //System.out.println("For year: " + inYear + "  team: " + inTeamId + "  players: " + inPlayerList);
         
        try
        {
            wkConn = DBUtil.getDBConnection();
            wkStatement = wkConn.prepareStatement(DELETE_TEAM_STATS);
            
            wkStatement.setInt(1, inYear);
            wkStatement.setInt(2, inTeamId);
            
            wkStatement.executeUpdate();
            wkStatement.close();
            
            while (wkPlayers.hasMoreTokens())
            {
                int wkPlayer = Integer.parseInt(wkPlayers.nextToken());
                transactionManager.createStatRecordIfMissing(inYear, inTeamId, wkPlayer);
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
    
    
    public void createBattingStats(TreeMap<String, GenericStatRecord> inPlayers, Element inStats, boolean filterNoUse)
    {
        Iterator<String> wkIter = inPlayers.keySet().iterator();
        DecimalFormat wkFormatter = new DecimalFormat();
        double wkPct;
        int wkAB, wkHits, wkDoubles, wkTriples, wkHR, wkBB, wkHBP;
        
                
        wkFormatter.setMaximumFractionDigits(3);
        wkFormatter.setMinimumFractionDigits(3);
        
        while (wkIter.hasNext())
        {      
            String name = wkIter.next();
            GenericStatRecord wkRecord = inPlayers.get(name);

            if (filterNoUse && wkRecord.getGames() == 0)
            {
                continue;
            }
            
            Element wkStatEntry = new Element("batter");
            int wkTeamId = wkRecord.getTeamid();
            
            Element wkStat = new Element("teamid").setAttribute("DisplayName", "Team ID");
            wkStatEntry.addContent(wkStat.setText(wkTeamId+""));
            
            wkStat = new Element("teamname").setAttribute("DisplayName", "Team");
            wkStatEntry.addContent(wkStat.setText(TeamCache.get(wkTeamId).getNickname()));
            
            wkStat = new Element("season").setAttribute("DisplayName", "Year");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getSeason()+""));
            
            wkStat = new Element("id").setAttribute("DisplayName", "ID");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPlayerid()+""));
            
            wkStat = new Element("name").setAttribute("DisplayName", "Name");
            
            if (wkRecord.getPlayerid() != 0)
            {
                wkStatEntry.addContent(wkStat.setText(playerCache.get(wkRecord.getPlayerid()).getDisplayname()));
            }
            else
            {
                wkStatEntry.addContent(wkStat.setText(name));
            }
            
            wkStat = new Element("games").setAttribute("DisplayName", "Games");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getGames()+""));
            
            wkStat = new Element("bat_ab").setAttribute("DisplayName", "AB");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getBat_ab()+""));
            
            wkStat = new Element("bat_runs").setAttribute("DisplayName", "Run");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getBat_runs()+""));
            
            wkStat = new Element("bat_rbi").setAttribute("DisplayName", "RBI");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getBat_rbi()+""));
            
            wkStat = new Element("bat_hits").setAttribute("DisplayName", "Hits");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getBat_hits()+""));
            
            wkStat = new Element("bat_doubles").setAttribute("DisplayName", "Dbl");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getBat_doubles()+""));
            
            wkStat = new Element("bat_triples").setAttribute("DisplayName", "Tri");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getBat_triples()+""));
            
            wkStat = new Element("bat_hr").setAttribute("DisplayName", "HR");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getBat_hr()+""));
            
            wkStat = new Element("bat_walks").setAttribute("DisplayName", "BB");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getBat_walks()+""));
            
            wkStat = new Element("bat_strikeouts").setAttribute("DisplayName", "K");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getBat_strikeouts()+""));
            
            wkStat = new Element("bat_sb").setAttribute("DisplayName", "SB");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getBat_sb()+""));
            
            wkStat = new Element("bat_cs").setAttribute("DisplayName", "CS");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getBat_cs()+""));

            wkStat = new Element("bat_hbp").setAttribute("DisplayName", "HBP");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getBat_hbp()+""));
                        
            wkStat = new Element("errors").setAttribute("DisplayName", "ER");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getErrors()+""));
            
            wkAB = wkRecord.getBat_ab();
            wkHits = wkRecord.getBat_hits();
            wkDoubles = wkRecord.getBat_doubles();
            wkTriples = wkRecord.getBat_triples();
            wkHR = wkRecord.getBat_hr();
            wkBB = wkRecord.getBat_walks();
            wkHBP = wkRecord.getBat_hbp();
            
            if ( wkAB != 0)    
            {
                int wkSingles = wkHits - wkDoubles - wkTriples - wkHR;
                wkPct = (double)wkHits/(double)wkAB;
                wkStat = new Element("avg").setAttribute("DisplayName", "AVG");
                wkStatEntry.addContent(wkStat.setText(wkFormatter.format(wkPct)));
                wkPct = (double)(wkSingles + 2*wkDoubles + 3*wkTriples + 4*wkHR)/(double)wkAB;
                wkStat = new Element("slug").setAttribute("DisplayName", "SLUG");
                wkStatEntry.addContent(wkStat.setText(wkFormatter.format(wkPct)));
                wkStat = new Element("obp").setAttribute("DisplayName", "OBP");
                wkPct = (double)(wkHits+wkBB+wkHBP)/(double)(wkAB+wkBB+wkHBP);
                wkStatEntry.addContent(wkStat.setText(wkFormatter.format(wkPct)));
            }
            else if (wkBB != 0)
            {
                wkPct = 0.000;
                wkStat = new Element("avg").setAttribute("DisplayName", "AVG");
                wkStatEntry.addContent(wkStat.setText(wkFormatter.format(wkPct)));
                wkStat = new Element("slug").setAttribute("DisplayName", "SLUG");
                wkStatEntry.addContent(wkStat.setText(wkFormatter.format(wkPct)));
                wkPct = (double)(wkHits+wkBB)/(double)(wkAB+wkBB);
                wkStat = new Element("obp").setAttribute("DisplayName", "OBP");
                wkStatEntry.addContent(new Element("obp").setText(wkFormatter.format(wkPct)));
            }
            else
            {
                wkPct = 0.000;
                wkStat = new Element("avg").setAttribute("DisplayName", "AVG");
                wkStatEntry.addContent(wkStat.setText(wkFormatter.format(wkPct)));
                wkStat = new Element("slug").setAttribute("DisplayName", "SLUG");
                wkStatEntry.addContent(wkStat.setText(wkFormatter.format(wkPct)));
                wkStat = new Element("obp").setAttribute("DisplayName", "OBP");
                wkStatEntry.addContent(wkStat.setText(wkFormatter.format(wkPct)));
            }
            inStats.addContent(wkStatEntry);
        }
    }

    public void createPitchingStats(TreeMap<String, GenericStatRecord> inPlayers, Element inStats, boolean filterNoUse)
    {
        Iterator<String> wkIter = inPlayers.keySet().iterator();
        double wkERA;
        int wkIPFull, wkIPFract, wkER;
        DecimalFormat wkFormatter = new DecimalFormat();
        
                
        wkFormatter.setMaximumFractionDigits(2);
        wkFormatter.setMinimumFractionDigits(2);
        
        while (wkIter.hasNext())
        {
            String name = wkIter.next();
            GenericStatRecord wkRecord = inPlayers.get(name);
            int wkTeamId = wkRecord.getTeamid();
            
            if (filterNoUse && wkRecord.getPitch_gp() == 0)
            {
                continue;
            }
            
            Element wkStatEntry = new Element("pitcher");
            Element wkStat = new Element("id").setAttribute("DisplayName", "ID");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPlayerid()+""));
            wkStat = new Element("season").setAttribute("DisplayName", "Year");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getSeason()+""));
            wkStat = new Element("teamid").setAttribute("DisplayName", "Team ID");
            wkStatEntry.addContent(wkStat.setText(wkTeamId+""));
            wkStat = new Element("teamname").setAttribute("DisplayName", "Team");
            wkStatEntry.addContent(wkStat.setText(TeamCache.get(wkTeamId).getNickname()));
            wkStat = new Element("name").setAttribute("DisplayName", "Name");
            
            if (wkRecord.getPlayerid() != 0)
            {
                wkStatEntry.addContent(wkStat.setText(playerCache.get(wkRecord.getPlayerid()).getDisplayname()));
            }
            else
            {
                wkStatEntry.addContent(wkStat.setText(name));
            }

            wkStat = new Element("games").setAttribute("DisplayName", "Games");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getGames()+""));
            wkStat = new Element("pitch_gp").setAttribute("DisplayName", "GP");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_gp()+""));
            wkStat = new Element("pitch_gs").setAttribute("DisplayName", "GS");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_gs()+""));
            wkStat = new Element("pitch_cg").setAttribute("DisplayName", "CG");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_cg()+""));
            wkStat = new Element("pitch_sho").setAttribute("DisplayName", "SHO");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_sho()+""));
            wkStat = new Element("pitch_wins").setAttribute("DisplayName", "Win");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_wins()+""));
            wkStat = new Element("pitch_loss").setAttribute("DisplayName", "Loss");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_loss()+""));
            wkStat = new Element("pitch_save").setAttribute("DisplayName", "Save");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_save()+""));
            wkStat = new Element("pitch_ipfull").setAttribute("DisplayName", "IP Full");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_ipfull()+""));
            wkStat = new Element("pitch_ipfract").setAttribute("DisplayName", "IP Fract");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_ipfract()+""));
            wkStat = new Element("pitch_ip").setAttribute("DisplayName", "IP");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_ipfull()+"."+wkRecord.getPitch_ipfract()));
            wkStat = new Element("pitch_hits").setAttribute("DisplayName", "Hits");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_hits()+""));
            wkStat = new Element("pitch_runs").setAttribute("DisplayName", "Runs");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_runs()+""));
            wkStat = new Element("pitch_er").setAttribute("DisplayName", "ER");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_er()+""));
            wkStat = new Element("pitch_walks").setAttribute("DisplayName", "BB");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_walks()+""));
            wkStat = new Element("pitch_strikeouts").setAttribute("DisplayName", "SO");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_strikeouts()+""));
            wkStat = new Element("pitch_hr").setAttribute("DisplayName", "HR");
            wkStatEntry.addContent(wkStat.setText(wkRecord.getPitch_hr()+""));
            
            wkIPFull = wkRecord.getPitch_ipfull();
            wkIPFract = wkRecord.getPitch_ipfract();
            wkER = wkRecord.getPitch_er();
            
            if ( wkIPFull != 0 || wkIPFract != 0)    
            {
                double wkIP = (double)wkIPFull + ((double)wkIPFract)*0.333;
                wkERA = ((double)(wkER * 9))/wkIP;
                wkStat = new Element("era").setAttribute("DisplayName", "ERA");
                wkStatEntry.addContent(wkStat.setText(wkFormatter.format(wkERA)));
            }
            else
            {
                wkERA = 0.00;
                wkStat = new Element("era").setAttribute("DisplayName", "ERA");
                wkStatEntry.addContent(wkStat.setText(wkFormatter.format(wkERA)));            
            }
            
            inStats.addContent(wkStatEntry);
        }
    }
    
    public void processStats(HashMap<String, StatRecord> wkRecords)
    {
        Iterator<StatRecord> wkIter = wkRecords.values().iterator();
        
        while(wkIter.hasNext())
        {
            GenericStatRecord wkRecord = wkIter.next();
            wkRecord.updateRecord();
        }
    }


    public void processSeriesStats(HashMap<Integer, GenericStatRecord> wkTeamRecords,
    		HashMap<Integer, GenericStatRecord> wkOtherRecords)
    {
        Iterator<GenericStatRecord> wkIter = wkTeamRecords.values().iterator();
        
        while(wkIter.hasNext())
        {
            GenericStatRecord wkRecord = wkIter.next();
            wkRecord.updateRecord();
        }

        wkIter = null;
        
        wkIter = wkOtherRecords.values().iterator();
        while(wkIter.hasNext())
        {
            GenericStatRecord wkRecord = wkIter.next();
            wkRecord.updateRecord();
        }
    }
        
    public Element getAverageLeaders(int inYear, int inMinimum, int inCount)
    {
        Element wkLeaders = new Element("BattingAverage");
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        int wkCount = 0;
        DecimalFormat wkFormatter = new DecimalFormat();
        
                
        wkFormatter.setMaximumFractionDigits(3);
        wkFormatter.setMinimumFractionDigits(3);
         
        try
        {            
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(AVERAGE_SQL);
            wkStatement.setInt(1, inYear);
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next() && wkCount < inCount)
            {  
                int wkPlayerId = wkResultSet.getInt("playerId");
                int wkAB = wkResultSet.getInt("ab");
                int wkHits = wkResultSet.getInt("hits");
                int wkBB = wkResultSet.getInt("bb");
                
                if (wkAB+wkBB > inMinimum)
                {
                    double wkValue = ((double)wkHits)/((double)wkAB);
                    Element wkPlayer = new Element("Player");
                    wkPlayer.addContent(new Element("displayname").setText(playerCache.get(wkPlayerId).getDisplayname()));
                    wkPlayer.addContent(new Element("playerid").setText(wkPlayerId+""));
                    wkPlayer.addContent(new Element("value").setText(wkFormatter.format(wkValue)));
                    wkLeaders.addContent(wkPlayer);
                    
                    wkCount++;
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
        return wkLeaders;
    }

    public Element getOBPLeaders(int inYear, int inMinimum, int inCount)
    {
        Element wkLeaders = new Element("OBP");
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        int wkCount = 0;
        DecimalFormat wkFormatter = new DecimalFormat();
        
                
        wkFormatter.setMaximumFractionDigits(3);
        wkFormatter.setMinimumFractionDigits(3);
         
        try
        {            
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(OBP_SQL);
            wkStatement.setInt(1, inYear);
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next() && wkCount < inCount)
            {  
                int wkPlayerId = wkResultSet.getInt("playerId");
                int wkAB = wkResultSet.getInt("ab");
                int wkHits = wkResultSet.getInt("hits");
                int wkBB = wkResultSet.getInt("bb");
                
                if (wkAB+wkBB > inMinimum)
                {
                    double wkValue = ((double)wkHits + (double)wkBB)/((double)wkAB + (double)wkBB);
                    Element wkPlayer = new Element("Player");
                    wkPlayer.addContent(new Element("displayname").setText(playerCache.get(wkPlayerId).getDisplayname()));
                    wkPlayer.addContent(new Element("playerid").setText(wkPlayerId+""));
                    wkPlayer.addContent(new Element("value").setText(wkFormatter.format(wkValue)));
                    wkLeaders.addContent(wkPlayer);
                    
                    wkCount++;
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
        return wkLeaders;
    }

    public Element getSlugLeaders(int inYear, int inMinimum, int inCount)
    {
        Element wkLeaders = new Element("SLUG");
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        int wkCount = 0;
        DecimalFormat wkFormatter = new DecimalFormat();
        
                
        wkFormatter.setMaximumFractionDigits(3);
        wkFormatter.setMinimumFractionDigits(3);
         
        try
        {            
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(SLUG_SQL);
            wkStatement.setInt(1, inYear);
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next() && wkCount < inCount)
            {  
                int wkPlayerId = wkResultSet.getInt("playerId");
                int wkAB = wkResultSet.getInt("ab");
                int wkBB = wkResultSet.getInt("bb");
                double wkHits = (double)wkResultSet.getInt("hits");
                double wkDouble = (double)wkResultSet.getInt("doubles");
                double wkTriples = (double)wkResultSet.getInt("triples");
                double wkHR = (double)wkResultSet.getInt("hr");
                
                if (wkAB+wkBB > inMinimum)
                {
                    double wkValue = (wkHits + wkDouble + 2.0*wkTriples + 3.0*wkHR)/((double)wkAB);
                    Element wkPlayer = new Element("Player");
                    wkPlayer.addContent(new Element("displayname").setText(playerCache.get(wkPlayerId).getDisplayname()));
                    wkPlayer.addContent(new Element("playerid").setText(wkPlayerId+""));
                    wkPlayer.addContent(new Element("value").setText(wkFormatter.format(wkValue)));
                    wkLeaders.addContent(wkPlayer);
                    
                    wkCount++;
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
        return wkLeaders;
    }

    public Element getERALeaders(int inYear, int inMinimum, int inCount)
    {
        Element wkLeaders = new Element("ERA");
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        int wkCount = 0;
        DecimalFormat wkFormatter = new DecimalFormat();
        
                
        wkFormatter.setMaximumFractionDigits(2);
        wkFormatter.setMinimumFractionDigits(2);
         
        try
        {            
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(ERA_SQL);
            wkStatement.setInt(1, inYear);
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next() && wkCount < inCount)
            {  
                int wkPlayerId = wkResultSet.getInt("playerId");
                int wkIP = wkResultSet.getInt("ipfull");
                double wkIPFract = (double)wkResultSet.getInt("ipfract");
                double wkER = (double)wkResultSet.getInt("er");
                
                if (wkIP >= inMinimum)
                {
                    double wkValue = (9*wkER)/((double)wkIP + wkIPFract*0.33);
                    Element wkPlayer = new Element("Player");
                    wkPlayer.addContent(new Element("displayname").setText(playerCache.get(wkPlayerId).getDisplayname()));
                    wkPlayer.addContent(new Element("playerid").setText(wkPlayerId+""));
                    wkPlayer.addContent(new Element("value").setText(wkFormatter.format(wkValue)));
                    wkLeaders.addContent(wkPlayer);
                    
                    wkCount++;
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
        return wkLeaders;
    }
        
    public Element getStatValueLeaders(int inYear, int inCount, String inColumnName, String inDisplayName, String inOrder)
    {
        Element wkLeaders = new Element(inDisplayName);
        Statement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        int wkCount = 0;
        //System.out.println("Looking up " + inColumnName + " with display " + inDisplayName + " for year " + inYear + "");
        
        
        try
        {            
            wkConn = DBUtil.getReadOnlyDBConnection();
            String wkQuery = "select playerid, SUM(" + inColumnName + ") AS " + inDisplayName + " FROM statrecords where season = " +
                             inYear + " GROUP BY playerid ORDER BY SUM(" + inColumnName + ") " + inOrder;
            //System.out.println(wkQuery);
            wkStatement = wkConn.createStatement();
            //wkStatement.setString(1, inColumnName);
            //wkStatement.setString(2, inDisplayName);
            //wkStatement.setInt(3, inYear);
            //wkStatement.setString(4, inColumnName);
            wkResultSet = wkStatement.executeQuery(wkQuery);
            
            while (wkResultSet.next() && wkCount < inCount)
            {  
                int wkPlayerId = wkResultSet.getInt("playerId");
                int wkValue = wkResultSet.getInt(inDisplayName);
                
                if (wkValue <= 0)
                    continue;
                
                Element wkPlayer = new Element("Player");
                
                wkPlayer.addContent(new Element("displayname").setText(playerCache.get(wkPlayerId).getDisplayname()));
                wkPlayer.addContent(new Element("playerid").setText(wkPlayerId+""));
                wkPlayer.addContent(new Element("value").setText(wkValue+""));
                wkLeaders.addContent(wkPlayer);
                    
                wkCount++;
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
        return wkLeaders;
    }
    
    public Element getLeagueLeaders(int inYear)
    {
        Element wkLeaders = new Element("LeagueLeaders");
        
        wkLeaders.addContent(new Element("season").setText(inYear+""));
        
        int wkIPMin = Integer.parseInt(ipQual);
        int wkPAMin = Integer.parseInt(abQual);
        
        wkLeaders.addContent(getAverageLeaders(inYear, wkPAMin, LEADER_COUNT));
        wkLeaders.addContent(getOBPLeaders(inYear, wkPAMin, LEADER_COUNT));
        wkLeaders.addContent(getSlugLeaders(inYear, wkPAMin, LEADER_COUNT));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "bat_runs", "Runs" ,"desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "bat_rbi", "RBI","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "bat_hits", "Hits","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "bat_doubles", "Doubles","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "bat_triples", "Triples","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "bat_hr", "HomeRuns","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "bat_walks", "Walks","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "bat_strikeouts", "Whiffs","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "bat_sb", "StolenBases","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "errors", "Errors","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "pitch_gs", "GamesStarted","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "pitch_cg", "CompleteGames","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "pitch_sho", "Shutouts","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "pitch_wins", "Wins","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "pitch_loss", "Losses","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "pitch_save", "Saves","desc"));
        wkLeaders.addContent(getERALeaders(inYear, wkIPMin, LEADER_COUNT));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "pitch_ipfull", "InningsPitched","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "pitch_walks", "WalksAllowed","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "pitch_strikeouts", "Strikeouts","desc"));
        wkLeaders.addContent(getStatValueLeaders(inYear, LEADER_COUNT, "pitch_hr", "HomeRunsAllowed","desc"));
        
        return wkLeaders;
        
    }
    
    @SuppressWarnings ("unchecked")
    public void refreshYearlyStats()
    {
    	int wkYear = transactionManager.getCurrentStatsSeason();
        Element wkTeams = teamManager.getMasterTeamList(wkYear);
        System.out.println("Got master team list");
        Iterator wkDivisionIter = wkTeams.getChildren("division").iterator();
        while (wkDivisionIter.hasNext())
        {
            Element  wkDivision = (Element) wkDivisionIter.next();
            Iterator wkTeamIter = wkDivision.getChildren("team").iterator();
            while (wkTeamIter.hasNext())
            {
                Element wkTeam = (Element) wkTeamIter.next();
                int wkId = Integer.parseInt(wkTeam.getChild("id").getText());
                System.out.println("refreshing stats for " + wkTeam.getChild("teamname").getText() + "for year " + wkYear);
                calculateTeamYearlyStats(wkYear, wkId);
            }
        }
    }
    
    public StatsCalculator getStatsCalculator(int inYear, int inTeam)
    {
    	//return getInstance().new StatsCalculator(inYear, inTeam);
    	return new StatsCalculator(inYear, inTeam);
    }
    
    private class StatsCalculator extends Thread
	{
    	private int _team;
		private int _year;
    	
    	StatsCalculator(int inYear, int inTeam)
		{
    		_team = inTeam;
    		_year = inYear;
		}
    	   	
    	public void run()
    	{
    		PreparedStatement wkStatement = null;
    	    Connection wkConn = null;
    	    HashMap<String, StatRecord> wkPlayers = new HashMap<String, StatRecord>();
    	        
    	        
    	    System.out.println("Starting Calc");
    	    try
    	    {
    	    	wkConn = DBUtil.getDBConnection();

    	        //Get full set of stats for reporting team
    	        wkStatement = wkConn.prepareStatement(GET_TEAM_STATS);
    	        wkStatement.setInt(1, _year);
    	        wkStatement.setInt(2, _team);
    	        ResultSet wkResults = wkStatement.executeQuery();
    	            
    	        while (wkResults.next())
    	        {
    	        	StatRecord wkRecord = new StatRecord();
    	            wkRecord.setPlayerid(wkResults.getInt("playerid"));
    	            wkRecord.setSeason(wkResults.getInt("season"));
    	            wkRecord.setTeamid(wkResults.getInt("teamid"));
    	            wkPlayers.put(wkRecord.getPlayerid()+"", wkRecord);
    	        }
    	            
    	        wkResults.close();
    	        wkStatement.close();

    	        wkStatement = wkConn.prepareStatement(GET_SEASON_STATS_FROM_SERIES);
    	        wkStatement.setInt(1, _year);
    	        wkStatement.setInt(2, _team);
    	        wkStatement.setInt(3, _team);
    	        wkResults = wkStatement.executeQuery();            
    	        while (wkResults.next())
    	        {
    	            SeriesStatRecord wkSeriesRecord =  new SeriesStatRecord(wkResults);
    	            String wkId = wkSeriesRecord.getPlayerid() + "";
    	            GenericStatRecord wkRecord = (GenericStatRecord) wkPlayers.get(wkId);
    	            wkRecord.addSeriesToTotals(wkSeriesRecord);
    	        }
    	            
    	        wkResults.close();
    	        wkStatement.close();
    	        teamStat.createTeamStatSheet(_year, _team, TeamCache.get(_team).getNickname());
    	            
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
    	    System.out.println("Finished Calc");
    	}
	}
    
    public class TeamSeriesSummary
    {
        private String[] status;
        private SeriesStatRecord homeTeamStats;
        private SeriesStatRecord visitingTeamStats;
        private boolean validStats = false;
        private boolean statsEntered = false;
        private boolean incomplete = false;
        
        public boolean isStatsEntered()
        {
            return statsEntered;
        }

        public void setStatsEntered(boolean statsEntered)
        {
            this.statsEntered = statsEntered;
        }
        
        public void  setIncomplete(boolean incomplete) 
        {
        	this.incomplete = incomplete;
        }
        
        public boolean isStatsIncomplete() 
        {
        	return incomplete;
        }

        public String[] getStatus()
        {
            return status;
        }
        
        public void setStatus(String[] inStatus)
        {
            status = inStatus;
        }
        
        public void setValidStats(boolean state)
        {
            validStats = state;
        }
        
        public boolean isValidStats()
        {
            return validStats;
        }

        public SeriesStatRecord getHomeTeamStats()
        {
            return homeTeamStats;
        }

        public void setHomeTeamStats(SeriesStatRecord homeTeamStats)
        {
            this.homeTeamStats = homeTeamStats;
        }

        public SeriesStatRecord getVisitingTeamStats()
        {
            return visitingTeamStats;
        }

        public void setVisitingTeamStats(SeriesStatRecord visitingTeamStats)
        {
            this.visitingTeamStats = visitingTeamStats;
        }
        
        
        
        
        
        
    }
}
