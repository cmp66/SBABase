
package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class TeamResult implements java.io.Serializable 
{

	private static final long serialVersionUID = 1L;
	private int teamid;
    private int divisionid;
    private int year;
    private int leagueid;
    private int won;
    private int lost;
    private int divisionwin;
    private int divisionloss;
    private int divisiontitle;
    private int worldseriesapp;
    private int worldserieswin;

    public TeamResult()
    {
    }
    
    public TeamResult(int teamid, int divisionid, int year, int leagueid, int won, int lost, int divisionwin, int divisionloss, int divisiontitle, int worldseriesapp, int worldserieswin) 
    {
        this.teamid = teamid;
        this.divisionid = divisionid;
        this.year = year;
        this.leagueid = leagueid;
        this.won = won;
        this.lost = lost;
        this.divisionwin = divisionwin;
        this.divisionloss = divisionloss;
        this.divisiontitle = divisiontitle;
        this.worldseriesapp = worldseriesapp;
        this.worldserieswin = worldserieswin;

    }

    public TeamResult(ResultSet inRecord) 
    {
        try
        {
            this.teamid = inRecord.getInt("teamid");
            this.divisionid = inRecord.getInt("divisionid");
            this.year = inRecord.getInt("year");
            this.leagueid = inRecord.getInt("leagueid");
            this.won = inRecord.getInt("won");
            this.lost = inRecord.getInt("lost");
            this.divisionwin = inRecord.getInt("divisionwin");
            this.divisionloss = inRecord.getInt("divisionloss");
            this.divisiontitle = inRecord.getInt("divisiontitle");
            this.worldseriesapp = inRecord.getInt("worldseriesapp");
            this.worldserieswin = inRecord.getInt("worldserieswin");

        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    
    public void createRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into teamresults (teamid, divisionid, year, leagueid, won, " +
                 " lost, divisionwin, divisionloss, divisiontitle, worldseriesapp, worldserieswin) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            
            wkStatement.setInt(1, teamid);
            wkStatement.setInt(2, divisionid);
            wkStatement.setInt(3, year);
            wkStatement.setInt(4, leagueid);
            wkStatement.setInt(5, won);
            wkStatement.setInt(6, lost);
            wkStatement.setInt(7, divisionwin);
            wkStatement.setInt(8, divisionloss);
            wkStatement.setInt(9, divisiontitle);
            wkStatement.setInt(10, worldseriesapp);
            wkStatement.setInt(11, worldserieswin);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }

    public void updateRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("UPDATE teamresults SET divisionid = ?, leagueid = ?, won = ?, " +
                 " lost = ?, divisionwin = ?, divisionloss = ?, divisiontitle = ?, worldseriesapp = ?, worldserieswin = ? WHERE teamid = ? and year = ?");
            
            wkStatement.setInt(10, teamid);
            wkStatement.setInt(1, divisionid);
            wkStatement.setInt(11, year);
            wkStatement.setInt(2, leagueid);
            wkStatement.setInt(3, won);
            wkStatement.setInt(4, lost);
            wkStatement.setInt(5, divisionwin);
            wkStatement.setInt(6, divisionloss);
            wkStatement.setInt(7, divisiontitle);
            wkStatement.setInt(8, worldseriesapp);
            wkStatement.setInt(9, worldserieswin);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }

    public void deleteRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE from teamresults WHERE teamid = ? and year = ?");
            
            wkStatement.setInt(1, teamid);
            wkStatement.setInt(2, year);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    public int getTeamid()
    {
        return this.teamid;
    }
    
    public int getDivisionid()
    {
        return this.divisionid;
    }
    
    public int getYear()
    {
        return this.year;
    }
    
    public int getLeagueid()
    {
        return this.leagueid;
    }
    
    public int getWon()
    {
        return this.won;
    }
    
    public int getLost()
    {
        return this.lost;
    }
    
    public int getDivisionwin()
    {
        return this.divisionwin;
    }
    
    public int getDivisionloss()
    {
        return this.divisionloss;
    }
    
    public int getDivisiontitle()
    {
        return this.divisiontitle;
    }
    
    public int getWorldseriesapp()
    {
        return this.worldseriesapp;
    }
    
    public int getWorldserieswin()
    {
        return this.worldserieswin;
    }
    
     public void setTeamid(int inVal)
    {
        this.teamid = inVal;
    }
    
     public void setDivisionid(int inVal)
    {
        this.divisionid = inVal;
    }
    
     public void setYear(int inVal)
    {
        this.year = inVal;
    }
    
     public void setLeagueid(int inVal)
    {
        this.leagueid = inVal;
    }
    
     public void setWon(int inVal)
    {
        this.won = inVal;
    }
    
     public void setLost(int inVal)
    {
        this.lost = inVal;
    }
    
     public void setDivisionwin(int inVal)
    {
        this.divisionwin = inVal;
    }
    
     public void setDivisionloss(int inVal)
    {
        this.divisionloss = inVal;
    }
    
     public void setDivisiontitle(int inVal)
    {
        this.divisiontitle = inVal;
    }
    
     public void setWorldseriesapp(int inVal)
    {
        this.worldseriesapp = inVal;
    }
    
     public void setWorldserieswin(int inVal)
    {
        this.worldserieswin = inVal;
    }
    
    
}
    