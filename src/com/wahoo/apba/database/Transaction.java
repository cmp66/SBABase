
package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class Transaction implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;
	private int id;
    private int type;
    private Timestamp transdate;
    private int team1;
    private int team2;

    public Transaction()
    {
    }
    
    public Transaction(int id, int type, Timestamp transdate, int team1, int team2) 
    {
        this.id = id;
        this.type = type;
        this.transdate = transdate;
        this.team1 = team1;
        this.team2 = team2;

    }

    public Transaction(ResultSet inRecord) 
    {
        try
        {
            this.id = inRecord.getInt("id");
            this.type = inRecord.getInt("type");
            this.transdate = inRecord.getTimestamp("transdate");
            this.team1 = inRecord.getInt("team1");
            this.team2 = inRecord.getInt("team2");

        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }

    public void createRecord(Connection inConn) throws SQLException
    {
        PreparedStatement wkStatement = inConn.prepareStatement("INSERT into transactions (id, type, transdate, team1, team2) " +
                                                                    "VALUES (?,?,?,?,?)");
            
        wkStatement.setInt(1, id);
        wkStatement.setInt(2, type);
        wkStatement.setTimestamp(3, transdate);
        wkStatement.setInt(4, team1);
        wkStatement.setInt(5, team2);
            
        wkStatement.executeUpdate();
            
        wkStatement.close();
    }    
    
    public void updateRecord(Connection inConn) throws SQLException
    {
        PreparedStatement wkStatement = inConn.prepareStatement("UPDATE transactions (id, SET type = ?, transdate=?, team1=?, team2=? " +
                                                                    "WHERE id = ?");
            
        wkStatement.setInt(5, id);
        wkStatement.setInt(1, type);
        wkStatement.setTimestamp(2, transdate);
        wkStatement.setInt(3, team1);
        wkStatement.setInt(4, team2);
            
        wkStatement.executeUpdate();
            
        wkStatement.close();
    }   
    
    
    public int getId()
    {
        return this.id;
    }
    
    public int getType()
    {
        return this.type;
    }
    
    public Timestamp getTransdate()
    {
        return this.transdate;
    }
    
    public int getTeam1()
    {
        return this.team1;
    }
    
    public int getTeam2()
    {
        return this.team2;
    }
    
     public void setId(int inVal)
    {
        this.id = inVal;
    }
    
     public void setType(int inVal)
    {
        this.type = inVal;
    }
    
     public void setTransdate(Timestamp inVal)
    {
        this.transdate = inVal;
    }
    
     public void setTeam1(int inVal)
    {
        this.team1 = inVal;
    }
    
     public void setTeam2(int inVal)
    {
        this.team2 = inVal;
    }
    
    public String getTransactionTypeString()
    {
        String wkType = "Unknown";
        
        if (this.type == 1)
            wkType="Trade";
        if (this.type == 2)
            wkType="Draft";
        if (this.type == 3)
            wkType="Add";
        if (this.type == 4)
            wkType="Remove";
        if (this.type == 5)
            wkType="Trans Year Start";
        if (this.type == 6)
            wkType="Stats Start";
            
        return wkType;
    }
            
    
    
}
    