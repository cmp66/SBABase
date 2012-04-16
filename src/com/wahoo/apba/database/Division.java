package com.wahoo.apba.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Division implements java.io.Serializable 
{

	private static final long serialVersionUID = 1L;
	private int id;
    private String name;
    private int leagueid;

    public Division()
    {
    }
    
    public Division(int id, String name, int leagueid) 
    {
        this.id = id;
        this.name = name;
        this.leagueid = leagueid;

    }

    public Division(ResultSet inRecord) 
    {
        try
        {
            this.id = inRecord.getInt("id");
            this.name = inRecord.getString("name");
            this.leagueid = inRecord.getInt("leagueid");

        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            com.wahoo.util.Email.emailException(wkException);
        }
    }
    
    public void createRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into divisions (id, name, leagueid) VALUES (?,?,?)");
            
            wkStatement.setInt(1, id);
            wkStatement.setString(2, name);
            wkStatement.setInt(3, leagueid);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            com.wahoo.util.Email.emailException(wkException);
        }
    }

    public void updateRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("UPDATE divisions SET name = ? , leagueid = ? WHERE id = ?");
            
            wkStatement.setInt(3, id);
            wkStatement.setString(1, name);
            wkStatement.setInt(2, leagueid);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            com.wahoo.util.Email.emailException(wkException);
        }
    }

    public void deleteRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE from  divisions WHERE id = ?");
            
            wkStatement.setInt(1, id);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            com.wahoo.util.Email.emailException(wkException);
        }
    }
    
    public int getId()
    {
        return this.id;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public int getLeagueid()
    {
        return this.leagueid;
    }
    
     public void setId(int inVal)
    {
        this.id = inVal;
    }
    
     public void setName(String inVal)
    {
        this.name = inVal;
    }
    
     public void setLeagueid(int inVal)
    {
        this.leagueid = inVal;
    }
    
    
}
    