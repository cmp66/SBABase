/*
 * DBKeyGen.java
 *
 * Created on February 9, 2003, 12:31 AM
 */

package com.wahoo.apba.database.util;

import java.sql.*;
import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.util.Email;
/**
 *
 * @author  cphillips
 */
public class DBKeyGen
{
    private static String GET_SQL = "SELECT NextVal from idgen";  
    private static String MOD_SQL = "UPDATE idgen set NextVal = ";
    
    private static Object _lock = new Object();
    
    /** Creates a new instance of DBKeyGen */
    public DBKeyGen ()
    {
    }
 
    public static int getKey()
    {
        Connection wkConn = null;
        Statement wkStatement = null;
        ResultSet wkResult = null;
        int wkKey = 0;
        
        synchronized(_lock)
        {
            try
            {
                wkConn = DBUtil.getDBConnection();
                wkStatement = wkConn.createStatement();
                int wkNextKey = 0;
            
                wkResult = wkStatement.executeQuery(GET_SQL);
            
                if (wkResult.next())
                {
                    wkKey = wkResult.getInt("NextVal");
                }
                
                wkResult.close();
                wkNextKey = wkKey + 1;
                
                wkStatement.executeUpdate(MOD_SQL + wkNextKey + "");
                
                wkStatement.close();
            }
            catch (SQLException wkEx)
            {
                wkEx.printStackTrace();
                Email.emailException(wkEx);
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
        }
        
        return wkKey;
    }
    
}
