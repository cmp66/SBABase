/*
 * EmailManager.java
 *
 * Created on March 9, 2003, 3:39 PM
 */

package com.wahoo.apba.resourcemanagers;

import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.util.Email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
/**
 *
 * @author  cphillips
 */
public class EmailManager
{

	//FIXME  Remove hardcoded address in query
     private static String GET_PRIM_ADDRESS_SQL = "select * from emailaddresses where primaryaddress = 'Y' and memberid in " +
         "(select t.memberid from teams as t join teamresults as tr on tr.teamid = t.id where tr.year=2019)";
     private static String GET_EMAIL_FOR_OWNER = "select * from emailaddresses where memberid = ? and primaryaddress = 'Y'"; 
     
    /** Creates a new instance of EmailManager */
    public EmailManager ()
    {
    }
    
    public String getMemberAddresses ()
    {
        StringBuffer wkAddresses = new StringBuffer(200);
        Statement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
         
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.createStatement();
            
            wkResultSet = wkStatement.executeQuery(GET_PRIM_ADDRESS_SQL);
            
            while (wkResultSet.next())
            {
                if (wkAddresses.length() > 0)
                    wkAddresses.append(",");
                wkAddresses.append(wkResultSet.getString("address"));
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
        
        return wkAddresses.toString();
    }
    
    public ArrayList<com.wahoo.apba.database.Email> getEmailAddresses (int inId)
    {
        ArrayList<com.wahoo.apba.database.Email> wkAddress = new ArrayList<com.wahoo.apba.database.Email>();
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
         
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.prepareStatement(GET_EMAIL_FOR_OWNER);
            wkStatement.setInt(1, inId);
            
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next())
            {
                wkAddress.add(new com.wahoo.apba.database.Email(wkResultSet));
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
        
        return wkAddress;
    }
    
    
    
}
