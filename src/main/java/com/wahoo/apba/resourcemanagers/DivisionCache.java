/*
 * PlayerCache.java
 *
 * Created on February 1, 2003, 9:17 PM
 */

package com.wahoo.apba.resourcemanagers;

import java.sql.*;
import java.util.Hashtable;
import java.util.Enumeration;
import com.wahoo.apba.database.Division;
import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class DivisionCache
{
 
    private Hashtable<Integer, Division> _divIdMap = null;
    
    private static String COUNT_SQL = "select count(*) from divisions";
    private static String GET_SQL = "select * from divisions";
    
	private Object LOCK = new Object();
	   
    public DivisionCache ()
    { 
        createMap();
    }

    
    public Division get(int inId)
    {
        synchronized (LOCK)
        {
            return (Division)_divIdMap.get(Integer.valueOf(inId));
        }
    }


    public void put(Division inRecord)
    {
        synchronized (LOCK)
        {
            _divIdMap.put( Integer.valueOf(inRecord.getId()), inRecord  );
        }
    }  


    public void remove(int inId)
    {
        synchronized (LOCK)
        {
            Integer wkId = Integer.valueOf(inId);
            _divIdMap.remove(wkId);
        }
    } 

    public void dumpMap()
    {
        synchronized (LOCK)
        {
    	    Enumeration<Integer> e = _divIdMap.keys();
    	    while (e.hasMoreElements())
    	    {
    		    Integer id = e.nextElement();
    		    System.out.println("        divisionmap key: " + id);
    	    }
    	}
    }
        
        
    public void resetMap()
    {
        synchronized (LOCK)
        {
            _divIdMap.clear();
            _divIdMap = null;
            createMap();
        }
    }


    public void createMap()
    {
        Statement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        Division wkDiv = null;
         
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.createStatement();
            
            wkResultSet = wkStatement.executeQuery(COUNT_SQL);
            wkResultSet.next();
            
            int wkNum = wkResultSet.getInt(1);
            
            _divIdMap = new Hashtable<Integer, Division>(wkNum*5/4);       
            
            wkResultSet.close();
         
            wkResultSet = wkStatement.executeQuery(GET_SQL);
            while (wkResultSet.next())
            {
                wkDiv = new Division(wkResultSet);
                _divIdMap.put(Integer.valueOf(wkDiv.getId()), wkDiv);
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
    }

    public Enumeration<Integer> getAllIDs()
    {
        synchronized (LOCK)
        {
            return _divIdMap.keys();
        }
    }


   
}
