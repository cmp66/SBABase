
package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class RosterMove implements java.io.Serializable 
{

	private static final long serialVersionUID = 1L;
	private int type;
    private int teamid;
    private int transactionid;
    private int resourcetype;
    private int resourceid;

    public RosterMove()
    {
    }
    
    public RosterMove(int type, int teamid, int transactionid, int resourcetype, int resourceid) 
    {
        this.type = type;
        this.teamid = teamid;
        this.transactionid = transactionid;
        this.resourcetype = resourcetype;
        this.resourceid = resourceid;

    }

    public RosterMove(ResultSet inRecord) 
    {
        try
        {
            this.type = inRecord.getInt("type");
            this.teamid = inRecord.getInt("teamid");
            this.transactionid = inRecord.getInt("transactionid");
            this.resourcetype = inRecord.getInt("resourcetype");
            this.resourceid = inRecord.getInt("resourceid");

        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    
    public void createRecord(Connection inConn)
        throws SQLException
    {

        PreparedStatement wkStatement = inConn.prepareStatement("INSERT into rostermove (type, teamid, transactionid, resourcetype, resourceid) " +
                                                                    "VALUES (?,?,?,?,?)");
            
        wkStatement.setInt(1, this.type);
        wkStatement.setInt(2, this.teamid);
        wkStatement.setInt(3, this.transactionid);
        wkStatement.setInt(4, this.resourcetype);
        wkStatement.setInt(5, this.resourceid);
            
        wkStatement.executeUpdate();
            
        wkStatement.close();
    }
    
    public int getType()
    {
        return this.type;
    }
    
    public int getTeamid()
    {
        return this.teamid;
    }
    
    public int getTransactionid()
    {
        return this.transactionid;
    }
    
    public int getResourcetype()
    {
        return this.resourcetype;
    }
    
    public int getResourceid()
    {
        return this.resourceid;
    }
    
     public void setType(int inVal)
    {
        this.type = inVal;
    }
    
     public void setTeamid(int inVal)
    {
        this.teamid = inVal;
    }
    
     public void setTransactionid(int inVal)
    {
        this.transactionid = inVal;
    }
    
     public void setResourcetype(int inVal)
    {
        this.resourcetype = inVal;
    }
    
     public void setResourceid(int inVal)
    {
        this.resourceid = inVal;
    }
    
    
}
    