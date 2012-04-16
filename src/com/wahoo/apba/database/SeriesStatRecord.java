/*
 * StatRecord.java
 *
 * Created on June 28, 2003, 1:13 PM
 */

package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.util.Email;
/**
 *
  * @author  cphillips
 */
public class SeriesStatRecord extends StatRecord implements GenericStatRecord
{

	private static final long serialVersionUID = 1L;

	private static String GET_PLAYER_RECORD = "select * from seriesstatrecords where playerid = ? and season = ? and series = ? and reportingteamid = ? and statsteamid = ?";

    private int series = 0;
    private int reportingteamid = 0;

    public SeriesStatRecord ()
    {
    }
    
    public SeriesStatRecord (int inPlayerid, int inSeason, int inSeries, int inReportingTeamId, int inStatsTeamId, int inTeamid, int inGames, int inBat_ab, int inBat_runs, int inBat_hits, int inBat_rbi, int inBat_hr, int inBat_doubles, int inBat_triples, int inBat_walks, int inBat_strikeouts, int inBat_sb, int inBat_cs, int inBat_hbp, int inErrors, int inPitch_gp, int inPitch_gs, int inPitch_cg, int inPitch_sho, int inPitch_wins, int inPitch_loss, int inPitch_save, int inPitch_ipfull, int inPitch_ipfract, int inPitch_hits, int inPitch_runs, int inPitch_er, int inPitch_walks, int inPitch_strikeouts, int inPitch_hr) 
    {
        playerid = inPlayerid;
        season = inSeason;
        series = inSeries;
        teamid = inStatsTeamId;
        reportingteamid = inReportingTeamId;
        games = inGames;
        bat_ab = inBat_ab;
        bat_runs = inBat_runs;
        bat_hits = inBat_hits;
        bat_rbi = inBat_rbi;
        bat_hr = inBat_hr;
        bat_doubles = inBat_doubles;
        bat_triples = inBat_triples;
        bat_walks = inBat_walks;
        bat_strikeouts = inBat_strikeouts;
        bat_sb = inBat_sb;
        bat_cs = inBat_cs;
        errors = inErrors;
        pitch_gs = inPitch_gs;
        pitch_cg = inPitch_cg;
        pitch_sho = inPitch_sho;
        pitch_wins = inPitch_wins;
        pitch_loss = inPitch_loss;
        pitch_save = inPitch_save;
        pitch_ipfull = inPitch_ipfull;
        pitch_ipfract = inPitch_ipfract;
        pitch_hits = inPitch_hits;
        pitch_er = inPitch_er;
        pitch_walks = inPitch_walks;
        pitch_strikeouts = inPitch_strikeouts;
        pitch_hr = inPitch_hr;

        bat_hbp = inBat_hbp;
        pitch_gp = inPitch_gp;
        pitch_runs = inPitch_runs;
    }

    
    public void createRecord()
    {
        Connection wkConn = null;
        try
        {
            wkConn = DBUtil.getDBConnection();
            PreparedStatement wkStatement = wkConn.prepareStatement("INSERT into seriesstatrecords (playerid, season, series, reportingteamid, statsteamid, games, bat_ab, bat_runs, " +
                                                                    "bat_hits, bat_rbi, bat_hr, bat_doubles, bat_triples, bat_walks, bat_strikeouts, bat_sb," +
                                                                    "bat_cs, bat_hbp, errors, pitch_gp, pitch_gs, pitch_cg, pitch_sho, pitch_wins, pitch_loss, pitch_save, " +
                                                                    "pitch_ipfull, pitch_ipfract, pitch_hits, pitch_runs, pitch_er, pitch_walks, pitch_strikeouts, pitch_hr) " + 
                                                                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            wkStatement.setInt(1, playerid);
            wkStatement.setInt(2, season);
            wkStatement.setInt(3, series);
            wkStatement.setInt(4, reportingteamid);
            wkStatement.setInt(5, teamid);
            wkStatement.setInt(6, games);
            wkStatement.setInt(7, bat_ab);
            wkStatement.setInt(8, bat_runs);
            wkStatement.setInt(9, bat_hits);
            wkStatement.setInt(10, bat_rbi);
            wkStatement.setInt(11, bat_hr);
            wkStatement.setInt(12, bat_doubles);
            wkStatement.setInt(13, bat_triples);
            wkStatement.setInt(14, bat_walks);
            wkStatement.setInt(15, bat_strikeouts);
            wkStatement.setInt(16, bat_sb);
            wkStatement.setInt(17, bat_cs);
            wkStatement.setInt(18, bat_hbp);
            wkStatement.setInt(19, errors);
            wkStatement.setInt(20, pitch_gp);
            wkStatement.setInt(21, pitch_gs);
            wkStatement.setInt(22, pitch_cg);
            wkStatement.setInt(23, pitch_sho);
            wkStatement.setInt(24, pitch_wins);
            wkStatement.setInt(25, pitch_loss);
            wkStatement.setInt(26, pitch_save);
            wkStatement.setInt(27, pitch_ipfract);
            wkStatement.setInt(28, pitch_ipfull);
            wkStatement.setInt(29, pitch_hits);
            wkStatement.setInt(30, pitch_runs);
            wkStatement.setInt(31, pitch_er);
            wkStatement.setInt(32, pitch_walks);
            wkStatement.setInt(33, pitch_strikeouts);
            wkStatement.setInt(34, pitch_hr);
            
            wkStatement.executeUpdate();
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
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
    
    public void createRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into seriesstatrecords (playerid, season, series, reportingteamid, statsteamid, games, bat_ab, bat_runs, " +
                                                                    "bat_hits, bat_rbi, bat_hr, bat_doubles, bat_triples, bat_walks, bat_strikeouts, bat_sb," +
                                                                    "bat_cs, bat_hbp, errors, pitch_gp, pitch_gs, pitch_cg, pitch_sho, pitch_wins, pitch_loss, pitch_save, " +
                                                                    "pitch_ipfull, pitch_ipfract, pitch_hits, pitch_runs, pitch_er, pitch_walks, pitch_strikeouts, pitch_hr) " + 
                                                                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            wkStatement.setInt(1, playerid);
            wkStatement.setInt(2, season);
            wkStatement.setInt(3, series);
            wkStatement.setInt(4, reportingteamid);
            wkStatement.setInt(5, teamid);
            wkStatement.setInt(6, games);
            wkStatement.setInt(7, bat_ab);
            wkStatement.setInt(8, bat_runs);
            wkStatement.setInt(9, bat_hits);
            wkStatement.setInt(10, bat_rbi);
            wkStatement.setInt(11, bat_hr);
            wkStatement.setInt(12, bat_doubles);
            wkStatement.setInt(13, bat_triples);
            wkStatement.setInt(14, bat_walks);
            wkStatement.setInt(15, bat_strikeouts);
            wkStatement.setInt(16, bat_sb);
            wkStatement.setInt(17, bat_cs);
            wkStatement.setInt(18, bat_hbp);
            wkStatement.setInt(19, errors);
            wkStatement.setInt(20, pitch_gp);
            wkStatement.setInt(21, pitch_gs);
            wkStatement.setInt(22, pitch_cg);
            wkStatement.setInt(23, pitch_sho);
            wkStatement.setInt(24, pitch_wins);
            wkStatement.setInt(25, pitch_loss);
            wkStatement.setInt(26, pitch_save);
            wkStatement.setInt(27, pitch_ipfract);
            wkStatement.setInt(28, pitch_ipfull);
            wkStatement.setInt(29, pitch_hits);
            wkStatement.setInt(30, pitch_runs);
            wkStatement.setInt(31, pitch_er);
            wkStatement.setInt(32, pitch_walks);
            wkStatement.setInt(33, pitch_strikeouts);
            wkStatement.setInt(34, pitch_hr);
            
            wkStatement.executeUpdate();
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }

    
    public void updateRecord()
    {
        Connection wkConn = null;
        try
        {
            wkConn = DBUtil.getDBConnection();
            PreparedStatement wkStatement = wkConn.prepareStatement("UPDATE seriesstatrecords set games=?, bat_ab=?, bat_runs=?, " +
                                                                    "bat_hits=?, bat_rbi=?, bat_hr=?, bat_doubles=?, bat_triples=?, bat_walks=?, bat_strikeouts=?, bat_sb=?," +
                                                                    "bat_cs=?, bat_hbp=?, errors=?, pitch_gp=?, pitch_gs=?, pitch_cg=?, pitch_sho=?, pitch_wins=?, pitch_loss=?, pitch_save=?, " +
                                                                    "pitch_ipfull=?, pitch_ipfract=?, pitch_hits=?, pitch_runs=?, pitch_er=?, pitch_walks=?, pitch_strikeouts=?, pitch_hr=? " + 
                                                                     "where playerid=? and season=? and series=? and reportingteamid=? and statsteamid=?");
            
            wkStatement.setInt(30, playerid);
            wkStatement.setInt(31, season);
            wkStatement.setInt(32, series);
            wkStatement.setInt(33, reportingteamid);
            wkStatement.setInt(34, teamid);
            wkStatement.setInt(1, games);
            wkStatement.setInt(2, bat_ab);
            wkStatement.setInt(3, bat_runs);
            wkStatement.setInt(4, bat_hits);
            wkStatement.setInt(5, bat_rbi);
            wkStatement.setInt(6, bat_hr);
            wkStatement.setInt(7, bat_doubles);
            wkStatement.setInt(8, bat_triples);
            wkStatement.setInt(9, bat_walks);
            wkStatement.setInt(10, bat_strikeouts);
            wkStatement.setInt(11, bat_sb);
            wkStatement.setInt(12, bat_cs);
            wkStatement.setInt(13, bat_hbp);
            wkStatement.setInt(14, errors);
            wkStatement.setInt(15, pitch_gp);
            wkStatement.setInt(16, pitch_gs);
            wkStatement.setInt(17, pitch_cg);
            wkStatement.setInt(18, pitch_sho);
            wkStatement.setInt(19, pitch_wins);
            wkStatement.setInt(20, pitch_loss);
            wkStatement.setInt(21, pitch_save);
            wkStatement.setInt(22, pitch_ipfull);
            wkStatement.setInt(23, pitch_ipfract);
            wkStatement.setInt(24, pitch_hits);
            wkStatement.setInt(25, pitch_runs);
            wkStatement.setInt(26, pitch_er);
            wkStatement.setInt(27, pitch_walks);
            wkStatement.setInt(28, pitch_strikeouts);
            wkStatement.setInt(29, pitch_hr);
   
            wkStatement.executeUpdate();
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
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
    
    public static SeriesStatRecord getSeriesStatRecord(int inPlayer, int inSeason, int inSeries, int inReportingTeam, int inStatsTeam)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        SeriesStatRecord wkRecord = null;
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(GET_PLAYER_RECORD);
            wkStatement.setInt(1, inPlayer);
            wkStatement.setInt(2, inSeason);
            wkStatement.setInt(3, inSeries);
            wkStatement.setInt(4, inReportingTeam);
            wkStatement.setInt(5, inStatsTeam);
            ResultSet wkResults = wkStatement.executeQuery();
            
            if (wkResults.next())
            {
                wkRecord = new SeriesStatRecord(wkResults);
            }
            
            
            wkResults.close();
            wkStatement.close();            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            com.wahoo.util.Email.emailException(e);
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
        
        return wkRecord;    
    }
    

    public SeriesStatRecord (ResultSet inRecord) 
    {
        try
        {
            playerid = inRecord.getInt("playerid");
            season = inRecord.getInt("season");
            series = inRecord.getInt("series");
            reportingteamid = inRecord.getInt("reportingteamid");
            teamid = inRecord.getInt("statsteamid");
            games = inRecord.getInt("games");
            bat_ab = inRecord.getInt("bat_ab");
            bat_runs = inRecord.getInt("bat_runs");
            bat_hits = inRecord.getInt("bat_hits");
            bat_rbi = inRecord.getInt("bat_rbi");
            bat_hr = inRecord.getInt("bat_hr");
            bat_doubles = inRecord.getInt("bat_doubles");
            bat_triples = inRecord.getInt("bat_triples");
            bat_walks = inRecord.getInt("bat_walks");
            bat_strikeouts = inRecord.getInt("bat_strikeouts");
            bat_sb = inRecord.getInt("bat_sb");
            bat_cs = inRecord.getInt("bat_cs");
            errors = inRecord.getInt("errors");
            pitch_gs = inRecord.getInt("pitch_gs");
            pitch_cg = inRecord.getInt("pitch_cg");
            pitch_sho = inRecord.getInt("pitch_sho");
            pitch_wins = inRecord.getInt("pitch_wins");
            pitch_loss = inRecord.getInt("pitch_loss");
            pitch_save = inRecord.getInt("pitch_save");
            pitch_ipfull = inRecord.getInt("pitch_ipfull");
            pitch_ipfract = inRecord.getInt("pitch_ipfract");
            pitch_hits = inRecord.getInt("pitch_hits");
            pitch_er = inRecord.getInt("pitch_er");
            pitch_walks = inRecord.getInt("pitch_walks");
            pitch_strikeouts = inRecord.getInt("pitch_strikeouts");
            pitch_hr = inRecord.getInt("pitch_hr");
            bat_hbp = inRecord.getInt("bat_hbp");
            pitch_gp = inRecord.getInt("pitch_gp");
            pitch_runs = inRecord.getInt("pitch_runs");

        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    
    public int getSeries()
    {
        return series;
    }
    
    public int getReportingTeamid()
    {
        return reportingteamid;
    }
    
    
     public void setSeries(int inVal)
     {
         series = inVal;
     }
     
    public void setReportingTeam(int inVal)
    { 
        reportingteamid = inVal;
    }
     
}
