package com.wahoo.apba.database;

import java.sql.*;

public class Email implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;
	private int memberid;
    private String address;
    private String primary;

    public Email()
    {
    }
    
    public Email(int memberid, String address, String primary) 
    {
        this.memberid = memberid;
        this.address = address;
        this.primary = primary;

    }

    public Email(ResultSet inRecord) 
    {
        try
        {
            this.memberid = inRecord.getInt("memberid");
            this.address = inRecord.getString("address");
            this.primary = inRecord.getString("primaryaddress");
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
        }
        
    }
    
    public void createRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into emailaddresses (memberid, address, primaryaddress) VALUES (?,?,?)");
            
            wkStatement.setInt(1, memberid);
            wkStatement.setString(2, address);
            wkStatement.setString(3, primary);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
        }
    }

    public void deleteRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE from  emailaddresses WHERE memberid = ? and address = ?");
            
            wkStatement.setInt(1, memberid);
            wkStatement.setString(2, address);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
        }
    }
    
    public int getMemberid()
    {
        return this.memberid;
    }
    
    public String getAddress()
    {
        return this.address;
    }
    
    public String getPrimary()
    {
        return this.primary;
    }
    
     public void setMemberid(int inVal)
    {
        this.memberid = inVal;
    }
    
     public void setAddress(String inVal)
    {
        this.address = inVal;
    }
    
     public void setPrimary(String inVal)
    {
        this.primary = inVal;
    }
    
    
}
    