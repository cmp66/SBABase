
package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class League implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;
	private int id;
    private String name;

    public League()
    {
    }
    
    public League(int id, String name) 
    {
        this.id = id;
        this.name = name;

    }

    public League(ResultSet inRecord) 
    {
        try
        {
            this.id = inRecord.getInt("id");
            this.name = inRecord.getString("name");

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
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into leagues (id, name) VALUES (?,?)");
            
            wkStatement.setInt(1, id);
            wkStatement.setString(2, name);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("UPDATE leagues SET name = ? WHERE id = ?");
            
            wkStatement.setInt(2, id);
            wkStatement.setString(1, name);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE from leagues WHERE id = ?");
            
            wkStatement.setInt(1, id);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
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
    
     public void setId(int inVal)
    {
        this.id = inVal;
    }
    
     public void setName(String inVal)
    {
        this.name = inVal;
    }
    
    
}
    