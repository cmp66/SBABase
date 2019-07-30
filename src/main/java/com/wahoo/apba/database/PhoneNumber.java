package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class PhoneNumber implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;
	private int memberid;
    private String phonenumber;
    private String workorhome;

    public PhoneNumber()
    {
    }
    
    public PhoneNumber(int memberid, String number, String work) 
    {
        this.memberid = memberid;
        this.phonenumber = number;
        this.workorhome = work;

    }

    public PhoneNumber(ResultSet inRecord) 
    {
        try
        {
            this.memberid = inRecord.getInt("memberid");
            this.phonenumber = inRecord.getString("phonenumber");
            this.workorhome = inRecord.getString("workorhome");
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
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into phonenumbers (memberid, phonenumber, workorhome) VALUES (?,?,?)");
            
            wkStatement.setInt(1, memberid);
            wkStatement.setString(2, phonenumber);
            wkStatement.setString(3, workorhome);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE from  phonenumbers WHERE memberid = ? and phonenumber = ?");
            
            wkStatement.setInt(1, memberid);
            wkStatement.setString(2, phonenumber);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    
    public int getMemberid()
    {
        return this.memberid;
    }
    
    public String getPhonenumber()
    {
        return this.phonenumber;
    }
    
    public String getWorkorhome()
    {
        return this.workorhome;
    }
    
     public void setMemberid(int inVal)
    {
        this.memberid = inVal;
    }
    
     public void setPhonenumber(String inVal)
    {
        this.phonenumber = inVal;
    }
    
     public void setWorkorhome(String inVal)
    {
        this.workorhome = inVal;
    }
    
    
}
    