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
public class StatRecord implements java.io.Serializable, GenericStatRecord 
{

	private static final long serialVersionUID = 1L;

	private static String GET_PLAYER_RECORD = "select * from statrecords where playerid = ? and season = ? and teamid = ?";

    protected int playerid = 0;
    protected int season = 0;
    protected int teamid = 0;
    protected int games = 0;
    protected int bat_ab = 0;
    protected int bat_runs= 0;
    protected int bat_hits = 0;
    protected int bat_rbi = 0;
    protected int bat_hr = 0;
    protected int bat_doubles = 0;
    protected int bat_triples = 0;
    protected int bat_walks = 0;
    protected int bat_strikeouts = 0;
    protected int bat_sb = 0;
    protected int bat_cs = 0;
    protected int bat_hbp = 0;
    protected int errors = 0;
    protected int pitch_gs = 0;
    protected int pitch_cg = 0;
    protected int pitch_sho = 0;
    protected int pitch_wins = 0;
    protected int pitch_loss = 0;
    protected int pitch_save = 0;
    protected int pitch_ipfull = 0;
    protected int pitch_ipfract = 0;
    protected int pitch_hits = 0;
    protected int pitch_runs = 0;
    protected int pitch_er = 0;
    protected int pitch_walks = 0;
    protected int pitch_strikeouts = 0;
    protected int pitch_hr = 0;
    protected int pitch_gp = 0;

    public StatRecord()
    {
    }
    
    public StatRecord(int playerid, int season, int teamid, int games, int bat_ab, int bat_runs, int bat_hits, int bat_rbi, int bat_hr, int bat_doubles, int bat_triples, int bat_walks, int bat_strikeouts, int bat_sb, int bat_cs, int bat_hbp, int errors, int pitch_gp, int pitch_gs, int pitch_cg, int pitch_sho, int pitch_wins, int pitch_loss, int pitch_save, int pitch_ipfull, int pitch_ipfract, int pitch_hits, int pitch_runs, int pitch_er, int pitch_walks, int pitch_strikeouts, int pitch_hr) 
    {
        this.playerid = playerid;
        this.season = season;
        this.teamid = teamid;
        this.games = games;
        this.bat_ab = bat_ab;
        this.bat_runs = bat_runs;
        this.bat_hits = bat_hits;
        this.bat_rbi = bat_rbi;
        this.bat_hr = bat_hr;
        this.bat_doubles = bat_doubles;
        this.bat_triples = bat_triples;
        this.bat_walks = bat_walks;
        this.bat_strikeouts = bat_strikeouts;
        this.bat_sb = bat_sb;
        this.bat_cs = bat_cs;
        this.bat_hbp = bat_hbp;
        this.errors = errors;
        this.pitch_gp = pitch_gp;
        this.pitch_gs = pitch_gs;
        this.pitch_cg = pitch_cg;
        this.pitch_sho = pitch_sho;
        this.pitch_wins = pitch_wins;
        this.pitch_loss = pitch_loss;
        this.pitch_save = pitch_save;
        this.pitch_ipfull = pitch_ipfull;
        this.pitch_ipfract = pitch_ipfract;
        this.pitch_hits = pitch_hits;
        this.pitch_er = pitch_er;
        this.pitch_runs = pitch_runs;
        this.pitch_walks = pitch_walks;
        this.pitch_strikeouts = pitch_strikeouts;
        this.pitch_hr = pitch_hr;

    }

    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#createRecord()
	 */
    public void createRecord()
    {
        Connection wkConn = null;
        try
        {
            wkConn = DBUtil.getDBConnection();
            PreparedStatement wkStatement = wkConn.prepareStatement("INSERT into statrecords (playerid, season, teamid, games, bat_ab, bat_runs, " +
                                                                    "bat_hits, bat_rbi, bat_hr, bat_doubles, bat_triples, bat_walks, bat_strikeouts, bat_hbp, bat_sb," +
                                                                    "bat_cs, errors, pitch_gp, pitch_gs, pitch_cg, pitch_sho, pitch_wins, pitch_loss, pitch_save, " +
                                                                    "pitch_ipfull, pitch_ipfract, pitch_hits, pitch_runs, pitch_er, pitch_walks, pitch_strikeouts, pitch_hr) " + 
                                                                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            wkStatement.setInt(1, this.playerid);
            wkStatement.setInt(2, this.season);
            wkStatement.setInt(3, this.teamid);
            wkStatement.setInt(4, this.games);
            wkStatement.setInt(5, this.bat_ab);
            wkStatement.setInt(6, this.bat_runs);
            wkStatement.setInt(7, this.bat_hits);
            wkStatement.setInt(8, this.bat_rbi);
            wkStatement.setInt(9, this.bat_hr);
            wkStatement.setInt(10, this.bat_doubles);
            wkStatement.setInt(11, this.bat_triples);
            wkStatement.setInt(12, this.bat_walks);
            wkStatement.setInt(13, this.bat_strikeouts);
            wkStatement.setInt(14, this.bat_hbp);
            wkStatement.setInt(15, this.bat_sb);
            wkStatement.setInt(16, this.bat_cs);
            wkStatement.setInt(17, this.errors);
            wkStatement.setInt(18, this.pitch_gp);
            wkStatement.setInt(19, this.pitch_gs);
            wkStatement.setInt(20, this.pitch_cg);
            wkStatement.setInt(21, this.pitch_sho);
            wkStatement.setInt(22, this.pitch_wins);
            wkStatement.setInt(23, this.pitch_loss);
            wkStatement.setInt(24, this.pitch_save);
            wkStatement.setInt(25, this.pitch_ipfract);
            wkStatement.setInt(26, this.pitch_ipfull);
            wkStatement.setInt(27, this.pitch_hits);
            wkStatement.setInt(28, this.pitch_runs);
            wkStatement.setInt(29, this.pitch_er);
            wkStatement.setInt(30, this.pitch_walks);
            wkStatement.setInt(31, this.pitch_strikeouts);
            wkStatement.setInt(32, this.pitch_hr);
            
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

    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#createRecord(java.sql.Connection)
	 */
    public void createRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into statrecords (playerid, season, teamid, games, bat_ab, bat_runs, " +
                                                                    "bat_hits, bat_rbi, bat_hr, bat_doubles, bat_triples, bat_walks, bat_strikeouts, bat_hbp, bat_sb," +
                                                                    "bat_cs, errors, pitch_gp, pitch_gs, pitch_cg, pitch_sho, pitch_wins, pitch_loss, pitch_save, " +
                                                                    "pitch_ipfull, pitch_ipfract, pitch_hits, pitch_runs, pitch_er, pitch_walks, pitch_strikeouts, pitch_hr) " + 
                                                                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            wkStatement.setInt(1, this.playerid);
            wkStatement.setInt(2, this.season);
            wkStatement.setInt(3, this.teamid);
            wkStatement.setInt(4, this.games);
            wkStatement.setInt(5, this.bat_ab);
            wkStatement.setInt(6, this.bat_runs);
            wkStatement.setInt(7, this.bat_hits);
            wkStatement.setInt(8, this.bat_rbi);
            wkStatement.setInt(9, this.bat_hr);
            wkStatement.setInt(10, this.bat_doubles);
            wkStatement.setInt(11, this.bat_triples);
            wkStatement.setInt(12, this.bat_walks);
            wkStatement.setInt(13, this.bat_strikeouts);
            wkStatement.setInt(14, this.bat_hbp);
            wkStatement.setInt(15, this.bat_sb);
            wkStatement.setInt(16, this.bat_cs);
            wkStatement.setInt(17, this.errors);
            wkStatement.setInt(18, this.pitch_gp);
            wkStatement.setInt(19, this.pitch_gs);
            wkStatement.setInt(20, this.pitch_cg);
            wkStatement.setInt(21, this.pitch_sho);
            wkStatement.setInt(22, this.pitch_wins);
            wkStatement.setInt(23, this.pitch_loss);
            wkStatement.setInt(24, this.pitch_save);
            wkStatement.setInt(25, this.pitch_ipfract);
            wkStatement.setInt(26, this.pitch_ipfull);
            wkStatement.setInt(27, this.pitch_hits);
            wkStatement.setInt(28, this.pitch_runs);
            wkStatement.setInt(29, this.pitch_er);
            wkStatement.setInt(30, this.pitch_walks);
            wkStatement.setInt(31, this.pitch_strikeouts);
            wkStatement.setInt(32, this.pitch_hr);
            
            wkStatement.executeUpdate();
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#updateRecord()
	 */
    public void updateRecord()
    {
        Connection wkConn = null;
        try
        {
            wkConn = DBUtil.getDBConnection();
            PreparedStatement wkStatement = wkConn.prepareStatement("UPDATE statrecords set games=?, bat_ab=?, bat_runs=?, " +
                                                                    "bat_hits=?, bat_rbi=?, bat_hr=?, bat_doubles=?, bat_triples=?, bat_walks=?, bat_strikeouts=?, bat_sb=?," +
                                                                    "bat_cs=?, bat_hbp=?, errors=?, pitch_gp=?, pitch_gs=?, pitch_cg=?, pitch_sho=?, pitch_wins=?, pitch_loss=?, pitch_save=?, " +
                                                                    "pitch_ipfull=?, pitch_ipfract=?, pitch_hits=?, pitch_runs=?, pitch_er=?, pitch_walks=?, pitch_strikeouts=?, pitch_hr=? " + 
                                                                     "where playerid=? and season=? and teamid=?");
            
            wkStatement.setInt(30, this.playerid);
            wkStatement.setInt(31, this.season);
            wkStatement.setInt(32, this.teamid);
            wkStatement.setInt(1, this.games);
            wkStatement.setInt(2, this.bat_ab);
            wkStatement.setInt(3, this.bat_runs);
            wkStatement.setInt(4, this.bat_hits);
            wkStatement.setInt(5, this.bat_rbi);
            wkStatement.setInt(6, this.bat_hr);
            wkStatement.setInt(7, this.bat_doubles);
            wkStatement.setInt(8, this.bat_triples);
            wkStatement.setInt(9, this.bat_walks);
            wkStatement.setInt(10, this.bat_strikeouts);
            wkStatement.setInt(11, this.bat_sb);
            wkStatement.setInt(12, this.bat_cs);
            wkStatement.setInt(13, this.bat_hbp);
            wkStatement.setInt(14, this.errors);
            wkStatement.setInt(15, this.pitch_gp);
            wkStatement.setInt(16, this.pitch_gs);
            wkStatement.setInt(17, this.pitch_cg);
            wkStatement.setInt(18, this.pitch_sho);
            wkStatement.setInt(19, this.pitch_wins);
            wkStatement.setInt(20, this.pitch_loss);
            wkStatement.setInt(21, this.pitch_save);
            wkStatement.setInt(22, this.pitch_ipfull);
            wkStatement.setInt(23, this.pitch_ipfract);
            wkStatement.setInt(24, this.pitch_hits);
            wkStatement.setInt(25, this.pitch_runs);
            wkStatement.setInt(26, this.pitch_er);
            wkStatement.setInt(27, this.pitch_walks);
            wkStatement.setInt(28, this.pitch_strikeouts);
            wkStatement.setInt(29, this.pitch_hr);
            
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
    
    public static GenericStatRecord getStatRecord(int inPlayer, int inSeason, int inTeam)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        GenericStatRecord wkRecord = null;
        
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();

            wkStatement = wkConn.prepareStatement(GET_PLAYER_RECORD);
            wkStatement.setInt(1, inPlayer);
            wkStatement.setInt(2, inSeason);
            wkStatement.setInt(3, inTeam);
            ResultSet wkResults = wkStatement.executeQuery();
            
            if (wkResults.next())
            {
                wkRecord = new StatRecord(wkResults);
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
        
        return wkRecord;    
    }
    

    public StatRecord(ResultSet inRecord) 
    {
        try
        {
            this.playerid = inRecord.getInt("playerid");
            this.season = inRecord.getInt("season");
            this.teamid = inRecord.getInt("teamid");
            this.games = inRecord.getInt("games");
            this.bat_ab = inRecord.getInt("bat_ab");
            this.bat_runs = inRecord.getInt("bat_runs");
            this.bat_hits = inRecord.getInt("bat_hits");
            this.bat_rbi = inRecord.getInt("bat_rbi");
            this.bat_hr = inRecord.getInt("bat_hr");
            this.bat_doubles = inRecord.getInt("bat_doubles");
            this.bat_triples = inRecord.getInt("bat_triples");
            this.bat_walks = inRecord.getInt("bat_walks");
            this.bat_strikeouts = inRecord.getInt("bat_strikeouts");
            this.bat_sb = inRecord.getInt("bat_sb");
            this.bat_cs = inRecord.getInt("bat_cs");
            this.errors = inRecord.getInt("errors");
            this.pitch_gs = inRecord.getInt("pitch_gs");
            this.pitch_cg = inRecord.getInt("pitch_cg");
            this.pitch_sho = inRecord.getInt("pitch_sho");
            this.pitch_wins = inRecord.getInt("pitch_wins");
            this.pitch_loss = inRecord.getInt("pitch_loss");
            this.pitch_save = inRecord.getInt("pitch_save");
            this.pitch_ipfull = inRecord.getInt("pitch_ipfull");
            this.pitch_ipfract = inRecord.getInt("pitch_ipfract");
            this.pitch_hits = inRecord.getInt("pitch_hits");
            this.pitch_er = inRecord.getInt("pitch_er");
            this.pitch_walks = inRecord.getInt("pitch_walks");
            this.pitch_strikeouts = inRecord.getInt("pitch_strikeouts");
            this.pitch_hr = inRecord.getInt("pitch_hr");
            this.bat_hbp = inRecord.getInt("bat_hbp");
            this.pitch_gp = inRecord.getInt("pitch_gp");
            this.pitch_runs = inRecord.getInt("pitch_runs");
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#addSeriesToTotals(com.wahoo.apba.database.SeriesStatRecord)
	 */
    public void addSeriesToTotals(SeriesStatRecord inRecord)
    {
        this.games += inRecord.getGames();
        this.bat_ab += inRecord.getBat_ab();
        this.bat_runs += inRecord.getBat_runs();
        this.bat_hits += inRecord.getBat_hits();
        this.bat_rbi += inRecord.getBat_rbi();
        this.bat_hr += inRecord.getBat_hr();
        this.bat_doubles += inRecord.getBat_doubles();
        this.bat_triples += inRecord.getBat_triples();
        this.bat_walks += inRecord.getBat_walks();
        this.bat_strikeouts += inRecord.getBat_strikeouts();
        this.bat_sb += inRecord.getBat_sb();
        this.bat_cs += inRecord.getBat_cs();
        this.errors += inRecord.getErrors();
        this.bat_hbp += inRecord.getBat_hbp();
        this.pitch_gp += inRecord.getPitch_gp();
        this.pitch_gs += inRecord.getPitch_gs();
        this.pitch_cg += inRecord.getPitch_cg();
        this.pitch_sho += inRecord.getPitch_sho();
        this.pitch_wins += inRecord.getPitch_wins();
        this.pitch_loss += inRecord.getPitch_loss();
        this.pitch_save += inRecord.getPitch_save();
        this.pitch_hits += inRecord.getPitch_hits();
        this.pitch_er += inRecord.getPitch_er();
        this.pitch_walks += inRecord.getPitch_walks();
        this.pitch_strikeouts += inRecord.getPitch_strikeouts();
        this.pitch_hr += inRecord.getPitch_hr();
        this.pitch_runs += inRecord.getPitch_runs();
        
        this.pitch_ipfull += inRecord.getPitch_ipfull();
        this.pitch_ipfract += inRecord.getPitch_ipfract();
        //Deal with fractional  totals
        if (this.pitch_ipfract > 2)
        {
            this.pitch_ipfract -= 3;
            this.pitch_ipfull++;
        }
        
        updateRecord();
    }
    
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPlayerid()
	 */
    public int getPlayerid()
    {
        return this.playerid;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getSeason()
	 */
    public int getSeason()
    {
        return this.season;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getTeamid()
	 */
    public int getTeamid()
    {
        return this.teamid;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getGames()
	 */
    public int getGames()
    {
        return this.games;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getBat_ab()
	 */
    public int getBat_ab()
    {
        return this.bat_ab;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getBat_runs()
	 */
    public int getBat_runs()
    {
        return this.bat_runs;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getBat_hits()
	 */
    public int getBat_hits()
    {
        return this.bat_hits;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getBat_rbi()
	 */
    public int getBat_rbi()
    {
        return this.bat_rbi;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getBat_hr()
	 */
    public int getBat_hr()
    {
        return this.bat_hr;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getBat_doubles()
	 */
    public int getBat_doubles()
    {
        return this.bat_doubles;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getBat_triples()
	 */
    public int getBat_triples()
    {
        return this.bat_triples;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getBat_walks()
	 */
    public int getBat_walks()
    {
        return this.bat_walks;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getBat_strikeouts()
	 */
    public int getBat_strikeouts()
    {
        return this.bat_strikeouts;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getBat_sb()
	 */
    public int getBat_sb()
    {
        return this.bat_sb;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getBat_cs()
	 */
    public int getBat_cs()
    {
        return this.bat_cs;
    }

    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getBat_hbp()
	 */
    public int getBat_hbp()
    {
        return this.bat_hbp;
    }
        
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getErrors()
	 */
    public int getErrors()
    {
        return this.errors;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_gp()
	 */
    public int getPitch_gp()
    {
        return this.pitch_gp;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_gs()
	 */
    public int getPitch_gs()
    {
        return this.pitch_gs;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_cg()
	 */
    public int getPitch_cg()
    {
        return this.pitch_cg;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_sho()
	 */
    public int getPitch_sho()
    {
        return this.pitch_sho;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_wins()
	 */
    public int getPitch_wins()
    {
        return this.pitch_wins;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_loss()
	 */
    public int getPitch_loss()
    {
        return this.pitch_loss;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_save()
	 */
    public int getPitch_save()
    {
        return this.pitch_save;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_ipfull()
	 */
    public int getPitch_ipfull()
    {
        return this.pitch_ipfull;
    }

    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_ipfract()
	 */
    public int getPitch_ipfract()
    {
        return this.pitch_ipfract;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_hits()
	 */
    public int getPitch_hits()
    {
        return this.pitch_hits;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_runs()
	 */
    public int getPitch_runs()
    {
        return this.pitch_runs;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_er()
	 */
    public int getPitch_er()
    {
        return this.pitch_er;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_walks()
	 */
    public int getPitch_walks()
    {
        return this.pitch_walks;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_strikeouts()
	 */
    public int getPitch_strikeouts()
    {
        return this.pitch_strikeouts;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#getPitch_hr()
	 */
    public int getPitch_hr()
    {
        return this.pitch_hr;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPlayerid(int)
	 */
    public void setPlayerid(int inVal)
    {
        this.playerid = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setSeason(int)
	 */
    public void setSeason(int inVal)
    {
        this.season = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setTeamid(int)
	 */
    public void setTeamid(int inVal)
    {
        this.teamid = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setGames(int)
	 */
    public void setGames(int inVal)
    {
        this.games = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setBat_ab(int)
	 */
    public void setBat_ab(int inVal)
    {
        this.bat_ab = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setBat_runs(int)
	 */
    public void setBat_runs(int inVal)
    {
        this.bat_runs = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setBat_hits(int)
	 */
    public void setBat_hits(int inVal)
    {
        this.bat_hits = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setBat_rbi(int)
	 */
    public void setBat_rbi(int inVal)
    {
        this.bat_rbi = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setBat_hr(int)
	 */
    public void setBat_hr(int inVal)
    {
        this.bat_hr = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setBat_doubles(int)
	 */
    public void setBat_doubles(int inVal)
    {
        this.bat_doubles = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setBat_triples(int)
	 */
    public void setBat_triples(int inVal)
    {
        this.bat_triples = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setBat_walks(int)
	 */
    public void setBat_walks(int inVal)
    {
        this.bat_walks = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setBat_strikeouts(int)
	 */
    public void setBat_strikeouts(int inVal)
    {
        this.bat_strikeouts = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setBat_sb(int)
	 */
    public void setBat_sb(int inVal)
    {
        this.bat_sb = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setBat_cs(int)
	 */
    public void setBat_cs(int inVal)
    {
        this.bat_cs = inVal;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setBat_hbp(int)
	 */
    public void setBat_hbp(int inVal)
    {
        this.bat_hbp = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setErrors(int)
	 */
    public void setErrors(int inVal)
    {
        this.errors = inVal;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_gp(int)
	 */
    public void setPitch_gp(int inVal)
    {
        this.pitch_gp = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_gs(int)
	 */
    public void setPitch_gs(int inVal)
    {
        this.pitch_gs = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_cg(int)
	 */
    public void setPitch_cg(int inVal)
    {
        this.pitch_cg = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_sho(int)
	 */
    public void setPitch_sho(int inVal)
    {
        this.pitch_sho = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_wins(int)
	 */
    public void setPitch_wins(int inVal)
    {
        this.pitch_wins = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_loss(int)
	 */
    public void setPitch_loss(int inVal)
    {
        this.pitch_loss = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_save(int)
	 */
    public void setPitch_save(int inVal)
    {
        this.pitch_save = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_ipfull(int)
	 */
    public void setPitch_ipfull(int inVal)
    {
        this.pitch_ipfull = inVal;
    }
 
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_ipfract(int)
	 */
    public void setPitch_ipfract(int inVal)
    {
        this.pitch_ipfract = inVal;
    }
     
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_hits(int)
	 */
    public void setPitch_hits(int inVal)
    {
        this.pitch_hits = inVal;
    }
    
    /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_runs(int)
	 */
    public void setPitch_runs(int inVal)
    {
        this.pitch_runs = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_er(int)
	 */
    public void setPitch_er(int inVal)
    {
        this.pitch_er = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_walks(int)
	 */
    public void setPitch_walks(int inVal)
    {
        this.pitch_walks = inVal;
    }
    
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_strikeouts(int)
	 */
    public void setPitch_strikeouts(int inVal)
    {
        this.pitch_strikeouts = inVal;
    }
     
     /* (non-Javadoc)
	 * @see com.wahoo.apba.database.GenericStatRecord#setPitch_hr(int)
	 */
    public void setPitch_hr(int inVal)
    {
        this.pitch_hr = inVal;
    }
     
}
