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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Element;

import com.wahoo.apba.database.GameResult;
import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.apba.excel.TeamStat;
import com.wahoo.apba.web.util.WebUtil;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class ScheduleManager
{
    //private static ScheduleManager _manager = null;
    private TransactionManager transactionManager = null;
    private PlayerCache playerCache = null;
    private TeamManager teamManager = null;
    private StatsManager statsManager = null;
    private TeamStat teamStat = null;
    
    private static Object _lock = new Object();
    
    private static String SCHEDULE_HOME_SQL = "select * from schedules where year = ? and hometeam = ? order by playmonth, monthidx,visitteam";
    private static String SCHEDULE_VISIT_SQL = "select * from schedules where year = ? and visitteam = ? order by playmonth, monthidx, hometeam";
    private static String LATESTSERIES_GET_SQL = "select * from schedules where year = ? and dateplayed > '2003-01-01 12:00:00' order by dateplayed desc";
    private static String SERIES_GET_SQL = "select * from schedules where seriesid = ?";
    private static String GAMERESULT_GET_SQL = "select * from gameresults where scheduleid = ? and gamenumber = ?";
    private static String GAMERESULT_CREATE = "INSERT into gameresults (scheduleid, gamenumber, homepitcher, visitpitcher, homeruns, visitruns, comment)" +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static String GAMERESULT_UPDATE = "UPDATE gameresults SET homepitcher = ?, visitpitcher = ?, homeruns = ?, visitruns = ?, comment = ? " +
                                              "WHERE scheduleid = ? and gamenumber = ?";
    private static String SCHEDULE_TEAM_HOME = "SELECT hometeam AS Team, SUM(homewins) AS SumW, SUM(visitwins) AS SumL FROM schedules where " +
                                                 "year = ? GROUP BY hometeam";
    private static String SCHEDULE_TEAM_DIV_HOME = "SELECT hometeam AS Team, SUM(homewins) AS SumW, SUM(visitwins) AS SumL FROM schedules where " +
                                                 "year = ? and visitteam in (?) GROUP BY hometeam";
    private static String SCHEDULE_TEAM_ROAD = "SELECT visitteam AS Team, SUM(homewins) AS SumL, SUM(visitwins) AS SumW FROM schedules where " +
                                                 "year = ? GROUP BY visitteam";
    private static String SCHEDULE_TEAM_DIV_ROAD = "SELECT visitteam AS Team, SUM(homewins) AS SumL, SUM(visitwins) AS SumW FROM schedules where " +
                                                 "year = ? and hometeam in (?) GROUP BY visitteam";
    private static String SCHEDULE_UPDATE = "UPDATE schedules set homewins = ?, visitwins = ?, dateplayed = ? WHERE seriesid = ?";
    private static String TEAM_RESULTS_UPDATE = "UPDATE teamresults set won = ?, lost = ? where teamid = ? and year = ?";
                                                 
    /** Creates a new instance of TeamManager */
    private ScheduleManager ()
    {
    }
    
    /*
    public static ScheduleManager getInstance()
    {
        if (null == _manager)
            _manager = new ScheduleManager();
            
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
    
    public void setStatsManager (StatsManager inMgr)
    {
        this.statsManager = inMgr;
    }
    
    public void setTeamStat (TeamStat inStat)
    {
    	this.teamStat = inStat;
    }
     
    public void setPlayerCache (PlayerCache inCache)
    {
    	this.playerCache = inCache;
    }
        
    public Element getSchedule(int inTeamId)
    {
    	return getSchedule(inTeamId, transactionManager.getCurrentStandingsSeason());
    }
    
    public Element getSchedule (int inTeamId, int inSeason)
    {
        System.out.println("Looking for schedule for team : " + inTeamId + "");
        String wkTeamName = TeamCache.get(inTeamId).getNickname();
        Element wkSchedule = new Element("schedule");
        wkSchedule.setAttribute("team", TeamCache.get(inTeamId).getCity() + " " + wkTeamName);
        wkSchedule.addContent(new Element("StatFileLink").setText("files/export/season/" + wkTeamName + ".xls"));
        wkSchedule.addContent(new Element("StatFileText").setText(wkTeamName + " Season Stats"));
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        final String wkColor1 = "#FFFFFF";
        final String wkColor2 = "#E3E8EE";
        int wkCount = 1;
        //int wkReportingTeam = 0;
         
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();
                      
            Element wkGroup = new Element("homegames");
            wkSchedule.addContent(wkGroup);

            wkStatement = wkConn.prepareStatement(SCHEDULE_HOME_SQL);
            wkStatement.setInt(1, inSeason);
            wkStatement.setInt(2, inTeamId);
            wkResultSet = wkStatement.executeQuery();
            while (wkResultSet.next())
            {                
                Element wkEntry = new Element("series");
                int wkVisitTeam = wkResultSet.getInt("visitteam");
                String wkOtherTeam = TeamCache.get(wkVisitTeam).getNickname();
                
                int wkSeriesId = wkResultSet.getInt("seriesid");
                String wkSeriesStatFilename = inSeason + "-" + wkSeriesId + "-" + inTeamId + ".xls";
                
                StatsManager.TeamSeriesSummary seriesstats = statsManager.validateSeriesStats(inSeason, wkSeriesId, inTeamId, wkVisitTeam);
                
                wkEntry.addContent(new Element("hometeam").setText(wkTeamName));
                wkEntry.addContent(new Element("seriesid").setText(wkSeriesId+""));
                wkEntry.addContent(new Element("visitteam").setText(wkOtherTeam));
                wkEntry.addContent(new Element("otherlink").setText("Controller?page=series&series=" + wkResultSet.getInt("seriesid") + ""));
                wkEntry.addContent(new Element("statslink").setText("Controller?page=stats&mode=Enter&reportingteam=" + inTeamId + 
                                                                    "&year=" + inSeason + "&series=" + wkResultSet.getInt("seriesid") + "&otherteam=" + wkVisitTeam));
                wkEntry.addContent(new Element("otherid").setText(wkVisitTeam+ ""));
                wkEntry.addContent(new Element("numgames").setText(wkResultSet.getInt("numgames")+""));
                if (wkResultSet.getInt("playmonth") != 0)
                    wkEntry.addContent(new Element("playmonth").setText(convertMonth(wkResultSet.getInt("playmonth"))));
                
                if (teamStat.checkForExportSeriesFile(wkSeriesStatFilename))
                {
                    wkEntry.addContent(new Element("SeriesStatFileLink").setText("files/export/series/" + wkSeriesStatFilename));
                    wkEntry.addContent(new Element("SeriesStatFileText").setText(wkSeriesStatFilename));
                }
                else
                {
                    wkEntry.addContent(new Element("SeriesStatFileLink").setText("Controller?page=schedule&mode=genseriesfile&team=" + inTeamId + 
                                                                                 "&year=" + inSeason + "&series=" + wkSeriesId + "&otherteam=" +wkVisitTeam));
                    wkEntry.addContent(new Element("SeriesStatFileText").setText("Generate File"));
                }
                wkEntry.addContent(new Element("SeriesStatDetailLink").setText("Controller?page=seriesstatsdetail&mode=ShowError&team=" + inTeamId + 
                        "&year=" + inSeason + "&series=" + wkSeriesId + "&otherteam=" +wkVisitTeam));
                
                String linkText = "Not Entered";
                
                if (seriesstats.isStatsEntered())
                {
                    if (!seriesstats.isValidStats())
                    {
                        linkText= "Error";
                    }
                    else
                    {
                        linkText= "Ok";;
                    }
                } 
                else if (seriesstats.isStatsIncomplete()) 
                {
                	linkText = "Incomplete";
                }
                
                wkEntry.addContent(new Element("SeriesStatDetailText").setText(linkText));
                
                if ( (wkCount-wkCount/2) == wkCount/2)
                {
                    wkEntry.addContent(new Element("color").setText(wkColor1));
                }
                else
                {
                    wkEntry.addContent(new Element("color").setText(wkColor2));
                }
                wkCount++;
                
                int wkHomeWins = wkResultSet.getInt("homewins");
                int wkVisitWins = wkResultSet.getInt("visitwins");
                if (wkHomeWins > wkVisitWins)
                {
                    wkEntry.addContent(new Element("result").setText(wkTeamName + " " + wkHomeWins + "-" + wkVisitWins));
                }
                else if (wkVisitWins > wkHomeWins)
                {
                    wkEntry.addContent(new Element("result").setText(wkOtherTeam + " " + wkVisitWins + "-" + wkHomeWins));
                }
                else if (wkVisitWins > 0)
                {
                    wkEntry.addContent(new Element("result").setText("Tied " + wkHomeWins + "-" + wkVisitWins));
                }
                else
                    wkEntry.addContent(new Element("result").setText("Unplayed"));
                
                wkGroup.addContent(wkEntry);               
            }
            wkResultSet.close();
            wkStatement.close();
            
            wkGroup = new Element("visitgames");
            wkSchedule.addContent(wkGroup);

            wkStatement = wkConn.prepareStatement(SCHEDULE_VISIT_SQL);
            wkStatement.setInt(1, inSeason);
            wkStatement.setInt(2, inTeamId);
            wkResultSet = wkStatement.executeQuery();
            wkCount=1;
            while (wkResultSet.next())
            {                
                Element wkEntry = new Element("series");
                int wkHomeTeam = wkResultSet.getInt("hometeam");
                String wkOtherTeam = TeamCache.get(wkHomeTeam).getNickname();
             
                int wkSeriesId = wkResultSet.getInt("seriesid");
                String wkSeriesStatFilename = inSeason + "-" + wkSeriesId + "-" + inTeamId + ".xls";
                   
                wkEntry.addContent(new Element("hometeam").setText(wkOtherTeam));
                wkEntry.addContent(new Element("visitteam").setText(wkTeamName));
                wkEntry.addContent(new Element("otherid").setText(wkHomeTeam+ ""));
                wkEntry.addContent(new Element("otherlink").setText("Controller?page=series&series=" + wkResultSet.getInt("seriesid") + ""));
                wkEntry.addContent(new Element("statslink").setText("Controller?page=stats&mode=Enter&reportingteam=" + inTeamId + 
                                                                    "&year=" + inSeason + "&series=" + wkResultSet.getInt("seriesid") + "&otherteam=" + wkHomeTeam));
                wkEntry.addContent(new Element("numgames").setText(wkResultSet.getInt("numgames")+""));
                if (wkResultSet.getInt("playmonth") != 0)
                    wkEntry.addContent(new Element("playmonth").setText(convertMonth(wkResultSet.getInt("playmonth"))));
                
                if (teamStat.checkForExportSeriesFile(wkSeriesStatFilename))
                {
                    wkEntry.addContent(new Element("SeriesStatFileLink").setText("files/export/series/" + wkSeriesStatFilename));
                    wkEntry.addContent(new Element("SeriesStatFileText").setText(wkSeriesStatFilename));
                }
                else
                {
                    wkEntry.addContent(new Element("SeriesStatFileLink").setText("Controller?page=schedule&mode=genseriesfile&team=" + inTeamId + 
                                                                                 "&year=" + inSeason + "&series=" + wkSeriesId + "&otherteam=" +wkHomeTeam));
                    wkEntry.addContent(new Element("SeriesStatFileText").setText("Generate File"));
                }
                
                if ( (wkCount-wkCount/2) == wkCount/2)
                {
                    wkEntry.addContent(new Element("color").setText(wkColor1));
                }
                else
                {
                    wkEntry.addContent(new Element("color").setText(wkColor2));
                }
                wkCount++;                
                int wkHomeWins = wkResultSet.getInt("homewins");
                int wkVisitWins = wkResultSet.getInt("visitwins");
                if (wkHomeWins > wkVisitWins)
                {
                    wkEntry.addContent(new Element("result").setText(wkOtherTeam + " " + wkHomeWins + "-" + wkVisitWins));
                }
                else if (wkVisitWins > wkHomeWins)
                {
                    wkEntry.addContent(new Element("result").setText(wkTeamName + " " + wkVisitWins + "-" + wkHomeWins));
                }
                else if (wkVisitWins > 0)
                {
                    wkEntry.addContent(new Element("result").setText("Tied " + wkHomeWins + "-" + wkVisitWins));
                }
                else
                    wkEntry.addContent(new Element("result").setText("Unplayed"));
                
                wkGroup.addContent(wkEntry);               
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
        
        return wkSchedule;
    }    


    public Element getLatestSeries(int inNum)
    {
        Element wkSeries = new Element("LatestSeries");
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        int wkCount = 0;
         
        try
        {            
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(LATESTSERIES_GET_SQL);
            wkStatement.setInt(1, transactionManager.getCurrentStandingsSeason());
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next() && wkCount < inNum)
            {  
                wkSeries.addContent(getSeries(wkResultSet.getInt("seriesid")));
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
        return wkSeries;
    }
    
    public Element getSeries (int inId)
    {
        Element wkSeries = new Element("Series");
        wkSeries.addContent(new Element("id").setText(inId+""));
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        //final String wkColor1 = "#FFFFFF";
        //final String wkColor2 = "#E3E8EE";
        //int wkCount = 1;
        
        int wkHomeTeam = 0;
        int wkVisitTeam = 0;
        int wkNumGames = 0;
        String wkHomeName = null;
        String wkVisitName = null;
        //int wkGameId = 0;
         
        try
        {            
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(SERIES_GET_SQL);
            wkStatement.setInt(1, inId);
            wkResultSet = wkStatement.executeQuery();
            
            if (wkResultSet.next())
            {  
                System.out.println("Found Series");
                wkHomeTeam = wkResultSet.getInt("hometeam");
                wkVisitTeam = wkResultSet.getInt("visitteam");
                wkNumGames = wkResultSet.getInt("numgames");
                wkHomeName = TeamCache.get(wkHomeTeam).getNickname();
                wkVisitName = TeamCache.get(wkVisitTeam).getNickname();
                wkSeries.addContent(new Element("HomeTeamId").setText(wkHomeTeam+""));
                wkSeries.addContent(new Element("VisitTeamId").setText(wkVisitTeam+""));
                wkSeries.addContent(new Element("HomeTeamName").setText(wkHomeName));
                wkSeries.addContent(new Element("VisitTeamName").setText(wkVisitName));
                
                Timestamp wkDate = wkResultSet.getTimestamp("dateplayed");
                String wkDateString = "";
                if (null != wkDate)
                    wkDateString = WebUtil.formatMsToDate(wkDate.getTime());
                System.out.println("Series played on " + wkDateString);
                wkSeries.addContent(new Element("DatePlayed").setText(wkDateString));
            }
            else
                return wkSeries;
            
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
        
        wkSeries = getGameResultElements(wkSeries, inId, wkHomeTeam, wkVisitTeam, wkNumGames);
        return wkSeries;
    }
    
    private Element getGameResultElements(Element inSeries, int inSeriesId, int inHomeId, int inVisitId, int inNumGames)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(GAMERESULT_GET_SQL);
            Element wkHomePitchers = teamManager.getRoster(inHomeId,transactionManager.getCurrentStatsSeason ()).getChild("pitchers");
            Element wkVisitPitchers = teamManager.getRoster(inVisitId,transactionManager.getCurrentStatsSeason ()).getChild("pitchers");
            
            wkHomePitchers.setName("HomePitchers");
            wkVisitPitchers.setName("VisitPitchers");
            
            for (int i=1; i<=inNumGames; i++)
            {
                wkStatement.setInt(1, inSeriesId);
                wkStatement.setInt(2, i);
                wkResultSet = wkStatement.executeQuery();
                int wkVisitPitcherId = 0;
                int wkHomePitcherId = 0;
                
                Element wkGame = new Element("GameResult");
                wkGame.addContent(new Element("GameId").setText(i+""));
                wkGame.addContent(new Element("VisitTeamName").setText(TeamCache.get(inVisitId).getNickname()));
                wkGame.addContent(new Element("HomeTeamName").setText(TeamCache.get(inHomeId).getNickname()));
                if (wkResultSet.next())
                {
                    
                    wkVisitPitcherId = wkResultSet.getInt("visitpitcher");
                    wkHomePitcherId = wkResultSet.getInt("homepitcher");
                    wkGame.addContent(new Element("VisitScore").setText(wkResultSet.getInt("visitruns")+""));
                    wkGame.addContent(new Element("HomeScore").setText(wkResultSet.getInt("homeruns")+""));
                    wkGame.addContent(new Element("VisitPitcherId").setText(wkVisitPitcherId+""));
                    
                    if (wkVisitPitcherId != 0)
                        wkGame.addContent(new Element("VisitPitcherName").setText(playerCache.get(wkVisitPitcherId).getDisplayname()));
                    else
                        wkGame.addContent(new Element("VisitPitcherName").setText("Unknown"));
                    
                    if (wkHomePitcherId != 0)                        
                        wkGame.addContent(new Element("HomePitcherName").setText(playerCache.get(wkHomePitcherId).getDisplayname()));
                    else
                        wkGame.addContent(new Element("HomePitcherName").setText("Unknown"));
                    
                    wkGame.addContent(new Element("HomePitcherId").setText(wkHomePitcherId+""));
                    wkGame.addContent(new Element("Comment").setText(wkResultSet.getString("comment")));
                }
                else
                {
                    wkGame.addContent(new Element("VisitScore").setText("0"));
                    wkGame.addContent(new Element("HomeScore").setText("0"));
                    wkGame.addContent(new Element("VisitPitcherId").setText("0"));
                    wkGame.addContent(new Element("HomePitcherId").setText("0"));
                    wkGame.addContent(new Element("Comment").setText(""));
                }
                
                Element wkPitcherList = (Element) wkVisitPitchers.clone();
                markPitcher(wkPitcherList, wkVisitPitcherId);
                wkGame.addContent(wkPitcherList);
                
                wkPitcherList = (Element) wkHomePitchers.clone();
                markPitcher(wkPitcherList, wkHomePitcherId);
                wkGame.addContent(wkPitcherList);
                
                inSeries.addContent(wkGame);
                
                wkResultSet.close();
                wkResultSet = null;
            }
            
            wkStatement.close();
            wkStatement = null;
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
        
        return inSeries;
    }

    @SuppressWarnings("unchecked")
    private void markPitcher(Element inList, int inId)
    {
        Iterator wkPitchers = inList.getChildren().iterator();
        
        while (wkPitchers.hasNext())
        {
            Element wkPitcher = (Element) wkPitchers.next();
            int id = Integer.parseInt(wkPitcher.getChildText("id"));
            if (id == inId)
            {
                wkPitcher.addContent(new Element("Selected").setText("true"));
            }
            else
            {
                wkPitcher.addContent(new Element("Selected").setText("false"));
            }
        }
    }
    
    public void updateSeries(HashMap<String, GameResult> inGameResults)
    {
        Iterator<GameResult> wkResults = inGameResults.values().iterator();
        int wkSeriesId = 0;
        int wkHomeWins = 0;
        int wkVisitWins = 0;
        
        while (wkResults.hasNext())
        {
            GameResult wkGame = wkResults.next();
            
            wkSeriesId = wkGame.getScheduleid();
            if (wkGame.getHomeruns() > wkGame.getVisitruns())
                wkHomeWins++;
            else if (wkGame.getVisitruns() > wkGame.getHomeruns())
                wkVisitWins++;
            
            if (gameRecordExists(wkGame))
            {
                updateGameRecord(wkGame);
            }
            else
            {
                createGameRecord(wkGame);
            }
            
            updateScheduleRecord(wkSeriesId, wkHomeWins, wkVisitWins);
        }
        
        updateTeamTotals();
    }
    
    private boolean gameRecordExists(GameResult inResult)
    {
        boolean wkFound = false;
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(GAMERESULT_GET_SQL);
 
            wkStatement.setInt(1, inResult.getScheduleid());
            wkStatement.setInt(2, inResult.getGamenumber());
            wkResultSet = wkStatement.executeQuery();
  
            if (wkResultSet.next())
            {
                wkFound = true;
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
        
        return wkFound;
    }

    private void updateGameRecord(GameResult inResult)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        try
        {
            wkConn = DBUtil.getDBConnection();

            wkStatement = wkConn.prepareStatement(GAMERESULT_UPDATE);
 
            wkStatement.setInt(6, inResult.getScheduleid());
            wkStatement.setInt(7, inResult.getGamenumber());
            wkStatement.setInt(1, inResult.getHomepitcher());
            wkStatement.setInt(2, inResult.getVisitpitcher());
            wkStatement.setInt(3, inResult.getHomeruns());
            wkStatement.setInt(4, inResult.getVisitruns());
            wkStatement.setString(5, inResult.getComment());
            
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

   private void createGameRecord(GameResult inResult)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        try
        {
            wkConn = DBUtil.getDBConnection();

            wkStatement = wkConn.prepareStatement(GAMERESULT_CREATE);
 
            wkStatement.setInt(1, inResult.getScheduleid());
            wkStatement.setInt(2, inResult.getGamenumber());
            wkStatement.setInt(3, inResult.getHomepitcher());
            wkStatement.setInt(4, inResult.getVisitpitcher());
            wkStatement.setInt(5, inResult.getHomeruns());
            wkStatement.setInt(6, inResult.getVisitruns());
            wkStatement.setString(7, inResult.getComment());
            
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

   private void updateScheduleRecord(int inSeriesId, int inHomeWins, int inVisitWins)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        try
        {
            wkConn = DBUtil.getDBConnection();

            wkStatement = wkConn.prepareStatement(SCHEDULE_UPDATE);
 
            wkStatement.setInt(1, inHomeWins);
            wkStatement.setInt(2, inVisitWins);
            wkStatement.setInt(4, inSeriesId);
            wkStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            
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

   private void updateTeamTotals()
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        HashMap<Integer, Integer> wkWins = new HashMap<Integer, Integer>(20);
        HashMap<Integer, Integer> wkLosses = new HashMap<Integer, Integer>(20);
        try
        {
            wkConn = DBUtil.getDBConnection();

            wkStatement = wkConn.prepareStatement(SCHEDULE_TEAM_HOME);
            wkStatement.setInt(1, transactionManager.getCurrentStandingsSeason());
            ResultSet wkResults = wkStatement.executeQuery();
            
            while (wkResults.next())
            {
                Integer wkTeam = Integer.valueOf(wkResults.getInt("Team"));
                
                wkWins.put(wkTeam, Integer.valueOf(wkResults.getInt("SumW")));
                wkLosses.put(wkTeam, Integer.valueOf(wkResults.getInt("SumL")));
            }
            
            wkStatement.close();
            wkResults.close();
            
            wkStatement = wkConn.prepareStatement(SCHEDULE_TEAM_ROAD);
            wkStatement.setInt(1, transactionManager.getCurrentStandingsSeason());
            wkResults = wkStatement.executeQuery();

            while (wkResults.next())
            {
                Integer wkTeam = Integer.valueOf(wkResults.getInt("Team"));
                int wkHomeWins = 0;
                int wkHomeLosses = 0;
                
                if (wkWins.containsKey(wkTeam))
                {
                    wkHomeWins = ((Integer)wkWins.get(wkTeam)).intValue();
                    wkHomeLosses = ((Integer)wkLosses.get(wkTeam)).intValue();
                }
                
                wkWins.put(wkTeam, Integer.valueOf(wkResults.getInt("SumW") + wkHomeWins));
                wkLosses.put(wkTeam, Integer.valueOf(wkResults.getInt("SumL") + wkHomeLosses));
            }
            
            wkResults.close();
            wkStatement.close();
            wkResults = null;
            wkStatement = null;
            
            updateTeamTotalsRecords(wkWins, wkLosses);
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
   
   private void updateTeamTotalsRecords(HashMap<Integer, Integer> inWins, HashMap<Integer, Integer> inLosses)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        try
        {
            wkConn = DBUtil.getDBConnection();

            wkStatement = wkConn.prepareStatement(TEAM_RESULTS_UPDATE);
            Iterator<Map.Entry<Integer,Integer>> wkTeams = inWins.entrySet().iterator();
            
            while (wkTeams.hasNext())
            {
                Map.Entry<Integer, Integer> entry = wkTeams.next();
                Integer wkTeam = entry.getKey();
 
                wkStatement.setInt(1, ((Integer)inWins.get(wkTeam)).intValue());
                wkStatement.setInt(2, ((Integer)inLosses.get(wkTeam)).intValue());
                wkStatement.setInt(3, wkTeam.intValue());
                wkStatement.setInt(4, transactionManager.getCurrentStandingsSeason());
            
                wkStatement.executeUpdate();
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
   
    public int getTeamDivisionWins(int inId, int inYear)
    {
        String wkOtherTeams = teamManager.getTeamsInDivision(inId, inYear);
        //System.out.println(wkOtherTeams + " are in the same division as " + inId + " for year " + inYear);
        Statement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResults = null;
        int wkWins = 0;
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.createStatement();
            wkResults = wkStatement.executeQuery("SELECT hometeam AS Team, SUM(homewins) AS SumW, SUM(visitwins) AS SumL FROM schedules where " +
                                                 "year = " + inYear + " and visitteam in (" + wkOtherTeams + ") and hometeam = " + inId + " GROUP BY hometeam");
            
            if (wkResults.next())
            {
                wkWins = wkResults.getInt("SumW");
            }
            
            wkResults.close();
            
            wkResults = wkStatement.executeQuery("SELECT visitteam AS Team, SUM(visitwins) AS SumW, SUM(homewins) AS SumL FROM schedules where " +
                                                 "year = " + inYear + " and hometeam in (" + wkOtherTeams + ") and visitteam = " + inId + " GROUP BY visitteam");
            
            
            if (wkResults.next())
            {
                wkWins += wkResults.getInt("SumW");
            }
            
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
        return wkWins;
   }

    public int getTeamDivisionLosses(int inId, int inYear)
    {
        String wkOtherTeams = teamManager.getTeamsInDivision(inId, inYear);
        Statement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResults = null;
        int wkLosses = 0;
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.createStatement();
            wkResults = wkStatement.executeQuery("SELECT hometeam AS Team, SUM(homewins) AS SumW, SUM(visitwins) AS SumL FROM schedules where " +
                                                 "year = " + inYear + " and visitteam in (" + wkOtherTeams + ") and hometeam = " + inId + " GROUP BY hometeam");
            
            if (wkResults.next())
            {
                wkLosses = wkResults.getInt("SumL");
            }
            
            wkResults.close();
            
            wkResults = wkStatement.executeQuery("SELECT visitteam AS Team, SUM(visitwins) AS SumW, SUM(homewins) AS SumL FROM schedules where " +
                                                 "year = " + inYear + " and hometeam in (" + wkOtherTeams + ") and visitteam = " + inId + " GROUP BY visitteam");
            
            
            if (wkResults.next())
            {
                wkLosses += wkResults.getInt("SumL");
            }
            
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
        return wkLosses;
    }
    
    private String convertMonth(int inMonth)
    {
        switch (inMonth)
        {
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "July";
            case 8:
                return "Aug";
            case 9:
                return "Sept";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            case 13:
                return "Jan";
        }
        return "";
    }
    

}
