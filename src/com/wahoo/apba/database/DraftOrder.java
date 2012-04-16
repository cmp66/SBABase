
package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class DraftOrder implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;
	private int year;
    private String teamlist;

    public DraftOrder()
    {
    }
    
    public DraftOrder(int year, String teamlist) 
    {
        this.year = year;
        this.teamlist = teamlist;

    }

    public DraftOrder(ResultSet inRecord) 
    {
        try
        {
            this.year = inRecord.getInt("draftyear");
            this.teamlist = inRecord.getString("teamlist");

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
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into draftorder (draftyear, teamlist)" +
                                                                    " VALUES (?,?)");
            
            wkStatement.setInt(1, this.year);
            wkStatement.setString(2, this.teamlist);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("UPDATE draftorder SET draftyear = ?, teamlist = ? ");
            
            wkStatement.setInt(1, this.year);
            wkStatement.setString(2, this.teamlist);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE draftorder WHERE draftyear = ?");
            
            wkStatement.setInt(1, year);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    
    public int getYear()
    {
        return this.year;
    }
    
    public String getTeamlist()
    {
        return this.teamlist;
    }
    
     public void setYear(int inVal)
    {
        this.year = inVal;
    }
    
     public void setTeamlist(String inVal)
    {
        this.teamlist = inVal;
    }
    
    
}
    