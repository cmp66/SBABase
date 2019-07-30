
package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class Schedule implements java.io.Serializable 
{

	private static final long serialVersionUID = 1L;
	private int seriesid;
    private int year;
    private int hometeam;
    private int visitteam;
    private int homewins;
    private int visitwins;
    private int numgames;
    private int playmonth;
    private int monthidx;
    private Timestamp dateplayed;

    public Schedule()
    {
    }
    
    public Schedule(int seriesid, int year, int hometeam, int visitteam, int homewins, int visitwins, int numgames, Timestamp dateplayed, int playmonth, int monthidx) 
    {
        this.seriesid = seriesid;
        this.year = year;
        this.hometeam = hometeam;
        this.visitteam = visitteam;
        this.homewins = homewins;
        this.visitwins = visitwins;
        this.numgames = numgames;
        this.dateplayed = dateplayed;
        this.playmonth = playmonth;
        this.monthidx = monthidx;

    }

    public Schedule(ResultSet inRecord) 
    {
        try
        {
            this.seriesid = inRecord.getInt("seriesid");
            this.year = inRecord.getInt("year");
            this.hometeam = inRecord.getInt("hometeam");
            this.visitteam = inRecord.getInt("visitteam");
            this.homewins = inRecord.getInt("homewins");
            this.visitwins = inRecord.getInt("visitwins");
            this.numgames = inRecord.getInt("numgames");
            this.dateplayed = inRecord.getTimestamp("dateplayed");
            this.playmonth = inRecord.getInt("playmonth");
            this.monthidx = inRecord.getInt("monthidx");

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
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into schedules (seriesid, year, hometeam, visitteam, homewins, " +
                  " visitwins, numgames, dateplayed, playmonth, monthidx) VALUES (?,?,?,?,?,?,?,?,?,?)");
            
            wkStatement.setInt(1, seriesid);
            wkStatement.setInt(2, year);
            wkStatement.setInt(3, hometeam);
            wkStatement.setInt(4, visitteam);
            wkStatement.setInt(5, homewins);
            wkStatement.setInt(6, visitwins);
            wkStatement.setInt(7, numgames);
            wkStatement.setTimestamp(8, dateplayed);
            wkStatement.setInt(9, playmonth);
            wkStatement.setInt(10, monthidx);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("UPDATE schedules SET year = ?, hometeam = ?, visitteam = ?, homewins = ?, " +
                  " visitwins = ?, numgames = ?, dateplayed = ?, playmonth = ?, monthidx = ? WHERE seriesid = ?");
            
            wkStatement.setInt(10, seriesid);
            wkStatement.setInt(1, year);
            wkStatement.setInt(2, hometeam);
            wkStatement.setInt(3, visitteam);
            wkStatement.setInt(4, homewins);
            wkStatement.setInt(5, visitwins);
            wkStatement.setInt(6, numgames);
            wkStatement.setTimestamp(7, dateplayed);
            wkStatement.setInt(8, playmonth);
            wkStatement.setInt(9, monthidx);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE from schedules WHERE seriesid = ?");
            
            wkStatement.setInt(1, seriesid);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    
    public int getSeriesid()
    {
        return this.seriesid;
    }
    
    public int getYear()
    {
        return this.year;
    }
    
    public int getHometeam()
    {
        return this.hometeam;
    }
    
    public int getVisitteam()
    {
        return this.visitteam;
    }
    
    public int getHomewins()
    {
        return this.homewins;
    }
    
    public int getVisitwins()
    {
        return this.visitwins;
    }
    
    public int getNumgames()
    {
        return this.numgames;
    }
    
    public Timestamp getDateplayed()
    {
        return this.dateplayed;
    }
    
    public int getPlaymonth()
    {
        return this.playmonth;
    }
    
    public int getMonthIdx()
    {
        return this.monthidx;
    }
    
    
     public void setSeriesid(int inVal)
    {
        this.seriesid = inVal;
    }
    
     public void setYear(int inVal)
    {
        this.year = inVal;
    }
    
     public void setHometeam(int inVal)
    {
        this.hometeam = inVal;
    }
    
     public void setVisitteam(int inVal)
    {
        this.visitteam = inVal;
    }
    
     public void setHomewins(int inVal)
    {
        this.homewins = inVal;
    }
    
     public void setVisitwins(int inVal)
    {
        this.visitwins = inVal;
    }
    
     public void setNumgames(int inVal)
    {
        this.numgames = inVal;
    }
    
    public void setPlaymonth(int inVal)
    {
        this.playmonth = inVal;
    }
    
    public void setMonthIdx(int inVal)
    {
        this.monthidx = inVal;
    }
    
    
     public void setDateplayed(Timestamp inVal)
    {
        this.dateplayed = inVal;
    }
    
    
    
    
}
    