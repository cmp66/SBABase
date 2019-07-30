
package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class DraftPick implements java.io.Serializable 
{

	private static final long serialVersionUID = 1L;
	private int draftyear;
    private int slotteam;
    private int ownerteam;
    private int playerid;
    private int round;
    private int pickid;

    public DraftPick()
    {
    }
    
    public DraftPick(int id, int draftyear, int slotteam, int ownerteam, int playerid, int round) 
    {
        this.draftyear = draftyear;
        this.slotteam = slotteam;
        this.ownerteam = ownerteam;
        this.playerid = playerid;
        this.round = round;
        this.pickid = id;

    }

    public DraftPick(ResultSet inRecord) 
    {
        try
        {
            this.draftyear = inRecord.getInt("draftyear");
            this.slotteam = inRecord.getInt("slotteam");
            this.ownerteam = inRecord.getInt("ownerteam");
            this.playerid = inRecord.getInt("playerid");
            this.round = inRecord.getInt("round");
            this.pickid = inRecord.getInt("pickid");
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
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into draftpicks (pickid, draftyear, slotteam, ownerteam, playerid, round) " +
                                                                    "VALUES (?,?,?,?,?,?)");
            
            wkStatement.setInt(1, this.pickid);
            wkStatement.setInt(2, this.draftyear);
            wkStatement.setInt(3, this.slotteam);
            wkStatement.setInt(4, this.ownerteam);
            wkStatement.setInt(5, this.playerid);
            wkStatement.setInt(6, this.round);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("UPDATE draftpicks SET draftyear = ?, slotteam = ? " +
                                                                    "ownerteam = ?, playerid = ?, round = ? WHERE pickid=?");
            
            wkStatement.setInt(1, this.draftyear);
            wkStatement.setInt(2, this.slotteam);
            wkStatement.setInt(3, this.ownerteam);
            wkStatement.setInt(4, this.playerid);
            wkStatement.setInt(5, this.round);
            wkStatement.setInt(6, this.pickid);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE from draftpicks WHERE draftyear = ? and slotteam = ? and round = ?");
            
            wkStatement.setInt(1, this.draftyear);
            wkStatement.setInt(2, this.slotteam);
            wkStatement.setInt(3, this.round);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    
    public int getDraftyear()
    {
        return this.draftyear;
    }
    
    public int getSlotteam()
    {
        return this.slotteam;
    }
    
    public int getOwnerteam()
    {
        return this.ownerteam;
    }
    
    public int getPlayerid()
    {
        return this.playerid;
    }
    
    public int getRound()
    {
        return this.round;
    }
    
    public int getId()
    {
        return this.pickid;
    }
    
     public void setDraftyear(int inVal)
    {
        this.draftyear = inVal;
    }
    
     public void setSlotteam(int inVal)
    {
        this.slotteam = inVal;
    }
    
     public void setOwnerteam(int inVal)
    {
        this.ownerteam = inVal;
    }
    
    
}
    