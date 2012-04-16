package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class Team implements java.io.Serializable 
{

	private static final long serialVersionUID = 1L;
	private int id;
    private String city;
    private String nickname;
    private int memberid;
    private int predecessor;

    public Team()
    {
    }
    
    public Team(int id, String city, String nickname, int memberid, int predecessor) 
    {
        this.id = id;
        this.city = city;
        this.nickname = nickname;
        this.memberid = memberid;
        this.predecessor = predecessor;

    }

    public Team(ResultSet inRecord) 
    {
        try
        {
            this.id = inRecord.getInt("id");
            this.city = inRecord.getString("city");
            this.nickname = inRecord.getString("nickname");
            this.memberid = inRecord.getInt("memberid");
            this.predecessor = inRecord.getInt("predecessor");
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
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into teams (id, city, nickname, memberid, predecessor) VALUES (?,?,?,?,?)");
            
            wkStatement.setInt(1, id);
            wkStatement.setString(2, city);
            wkStatement.setString(3, nickname);
            wkStatement.setInt(4, memberid);
            wkStatement.setInt(5, predecessor);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("UPDATE teams SET city = ?, nickname = ?, memberid = ?, predecessor = ? WHERE id = ?");
            
            wkStatement.setInt(5, id);
            wkStatement.setString(1, city);
            wkStatement.setString(2, nickname);
            wkStatement.setInt(3, memberid);
            wkStatement.setInt(4, predecessor);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE from teams WHERE id = ?");
            
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
    
    public String getCity()
    {
        return this.city;
    }
    
    public String getNickname()
    {
        return this.nickname;
    }
    
    public int getMemberid()
    {
        return this.memberid;
    }
    
    public int getPredecessor()
    {
        return this.predecessor;
    }
    
     public void setId(int inVal)
    {
        this.id = inVal;
    }
    
     public void setCity(String inVal)
    {
        this.city = inVal;
    }
    
     public void setNickname(String inVal)
    {
        this.nickname = inVal;
    }
    
     public void setMemberid(int inVal)
    {
        this.memberid = inVal;
    }
    
     public void setPredecessor(int inVal)
    {
        this.predecessor = inVal;
    }
    
    
}
    