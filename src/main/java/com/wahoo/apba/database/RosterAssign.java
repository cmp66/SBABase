
package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class RosterAssign implements java.io.Serializable 
{

	private static final long serialVersionUID = 1L;
	private int playerid;
    private int teamid;
    private int year;

    public RosterAssign()
    {
    }
    
    public RosterAssign(int playerid, int teamid, int year) 
    {
        this.playerid = playerid;
        this.teamid = teamid;
        this.year = year;

    }

    public RosterAssign(ResultSet inRecord) 
    {
        try
        {
            this.playerid = inRecord.getInt("playerid");
            this.teamid = inRecord.getInt("teamid");
            this.year = inRecord.getInt("year");

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
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into rosterassign (playerid, teamid, year) VALUES (?,?,?)");
            
            wkStatement.setInt(1, this.playerid);
            wkStatement.setInt(2, this.teamid);
            wkStatement.setInt(3, this.year);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE from rosterassign WHERE playerid = ? and teamid = ? and year = ?");
            
            wkStatement.setInt(1, this.playerid);
            wkStatement.setInt(2, this.teamid);
            wkStatement.setInt(3, this.year);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    public int getPlayerid()
    {
        return this.playerid;
    }
    
    public int getTeamid()
    {
        return this.teamid;
    }
    
    public int getYear()
    {
        return this.year;
    }
    
     public void setPlayerid(int inVal)
    {
        this.playerid = inVal;
    }
    
     public void setTeamid(int inVal)
    {
        this.teamid = inVal;
    }
    
     public void setYear(int inVal)
    {
        this.year = inVal;
    }
    
    
}
    