/*
 * userManager.java
 *
 * Created on March 9, 2003, 3:39 PM
 */

package com.wahoo.apba.resourcemanagers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jdom.Element;

import com.wahoo.apba.database.Member;
import com.wahoo.apba.database.PhoneNumber;
import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class UserManager
{
	private EmailManager emailManager = null;
	

     private static String GET_OWNER_SQL = "select memberid from teams where id = ?";
     private static String GET_MEMBERS_SQL = "select * from members";
     private static String GET_MEMBER_SQL = "select * from members where id = ?";
     private static String GET_PHONE_FOR_OWNER = "select * from phonenumbers where memberid = ?";
     
    /** Creates a new instance of EmailManager */
    public UserManager ()
    {
    }


    public void setEmailManager(EmailManager inMgr)
    {
    	this.emailManager = inMgr;
    }
    
    public Element getTeamOwnerInfo(int inTeamId)
    {
        int wkOwnerId = getOwnerForTeam(inTeamId);
        ArrayList<com.wahoo.apba.database.Email> wkEmailAddresses = emailManager.getEmailAddresses(wkOwnerId);
        ArrayList<PhoneNumber> wkPhoneNumbers = getPhoneNumbers(wkOwnerId);
        Member wkOwner = getMember(wkOwnerId);
        
        Element wkElement = new Element("Owner");
        
        wkElement.setAttribute("name", wkOwner.getFirstname() + " " + wkOwner.getLastname());
        wkElement.setAttribute("streetaddress1", wkOwner.getStreetaddress1());
        String wkTemp = wkOwner.getStreetaddress2();
        if (null == wkTemp)
            wkTemp = "";
            
        wkElement.setAttribute("streetaddress2", wkTemp);
        wkElement.setAttribute("city", wkOwner.getCity());
        wkElement.setAttribute("state", wkOwner.getState());
        wkElement.setAttribute("zipcode", wkOwner.getZipcode());
        
        Element wkEmail = new Element("Email");
        wkElement.addContent(wkEmail);
        Iterator<com.wahoo.apba.database.Email> wkIter = wkEmailAddresses.iterator();
        while (wkIter.hasNext())
        {
            com.wahoo.apba.database.Email wkEAddress = (com.wahoo.apba.database.Email) wkIter.next();
            Element wkEmailElement = new Element("Address");
            wkEmailElement.setAttribute("address", wkEAddress.getAddress());
            wkEmail.addContent(wkEmailElement);
        }
        
        
        Element wkPhone = new Element("Phone");
        wkElement.addContent(wkPhone);
        Iterator<PhoneNumber> wkPhoneIter = wkPhoneNumbers.iterator();
        while (wkPhoneIter.hasNext())
        {
            PhoneNumber wkPhoneNum = wkPhoneIter.next();
            Element wkPhoneElement = new Element("PhoneNumber");
            wkPhoneElement.setAttribute("number", wkPhoneNum.getPhonenumber());
            wkPhone.addContent(wkPhoneElement);
        }
        
        return wkElement;
    }
    
    public HashMap<Integer, Member> getMembers()
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        HashMap<Integer, Member> wkMembers = new HashMap<Integer, Member>(20);
         
        try
        {
            wkConn = DBUtil.getDBConnection();
            wkStatement = wkConn.prepareStatement(GET_MEMBERS_SQL);
            
            ResultSet wkResults = wkStatement.executeQuery();
            while (wkResults.next())
            {
                int wkOwnerId = wkResults.getInt("id");
                wkMembers.put(Integer.valueOf(wkOwnerId), new Member(wkResults));
            }
            
            wkResults.close();
            wkStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }    
        
        return wkMembers;   
    }
    
    public Member getMember(int inMemberId)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        Member wkOwner = null;
         
        try
        {
            wkConn = DBUtil.getDBConnection();
            wkStatement = wkConn.prepareStatement(GET_MEMBER_SQL);
            wkStatement.setInt(1, inMemberId);
            
            ResultSet wkResults = wkStatement.executeQuery();
            if (wkResults.next())
            {
                wkOwner = new Member(wkResults);
            }
            
            wkResults.close();
            wkStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }    
        
        return wkOwner;   
    }           
        
        
    
    public int getOwnerForTeam(int inTeamId)
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        int wkOwner = 0;
         
        try
        {
            wkConn = DBUtil.getDBConnection();
            wkStatement = wkConn.prepareStatement(GET_OWNER_SQL);
            
            wkStatement.setInt(1, inTeamId);
            
            ResultSet wkResults = wkStatement.executeQuery();
            if (wkResults.next())
            {
                wkOwner = wkResults.getInt("memberid");
            }
            wkResults.close();
            wkStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }   
        
        System.out.println(wkOwner + "is the owner for team " + inTeamId); 
        
        return wkOwner;   
    }
    
    
    public ArrayList<PhoneNumber> getPhoneNumbers (int inId)
    {
        ArrayList<PhoneNumber> wkNumbers = new ArrayList<PhoneNumber>();
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
         
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.prepareStatement(GET_PHONE_FOR_OWNER);
            wkStatement.setInt(1, inId);
            
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next())
            {
                wkNumbers.add(new PhoneNumber(wkResultSet));
            }            
            wkResultSet.close();
            wkStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Email.emailException(e);
        }
        finally
        {
            try
            {
                if (null != wkConn)
                    wkConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }  
        
        return wkNumbers;
    }
    
    
    
    
}
