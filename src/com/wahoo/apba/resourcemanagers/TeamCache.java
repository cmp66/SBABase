/*
 * PlayerCache.java
 *
 * Created on February 1, 2003, 9:17 PM
 */

package com.wahoo.apba.resourcemanagers;

import java.sql.*;
import java.util.Hashtable;
import java.util.Enumeration;
import com.wahoo.apba.database.Team;
import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class TeamCache
{
 
    private static Hashtable<Integer, Team> _teamIdMap = null;
    
    private static String COUNT_SQL = "select count(*) from teams";
    private static String GET_SQL = "select * from teams";
    
	private static Object LOCK = new Object();
	   
    public TeamCache ()
    { 
        createMap();
    }

    
    public static Team get(int inId)
    {
        synchronized (LOCK)
        {
            return (Team)_teamIdMap.get(Integer.valueOf(inId));
        }
    }


    public static void put(Team inRecord)
    {
        synchronized (LOCK)
        {
            _teamIdMap.put(Integer.valueOf(inRecord.getId()), inRecord  );
        }
    }  


    public static void remove(int inId)
    {
        synchronized (LOCK)
        {
            Integer wkId = Integer.valueOf(inId);
            _teamIdMap.remove(wkId);
        }
    } 

    public static void dumpMap()
    {
        synchronized (LOCK)
        {
    	    Enumeration<Integer> e = _teamIdMap.keys();
    	    while (e.hasMoreElements())
    	    {
    		    Integer id = e.nextElement();
    		    System.out.println("        teammap key: " + id);
    	    }
    	}
    }
        
        
    public static void resetMap()
    {
        synchronized (LOCK)
        {
            _teamIdMap.clear();
            _teamIdMap = null;
            createMap();
        }
    }


    public static void createMap()
    {
        Statement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        Team wkTeam = null;
         
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.createStatement();
            
            wkResultSet = wkStatement.executeQuery(COUNT_SQL);
            wkResultSet.next();
            
            int wkNum = wkResultSet.getInt(1);
            
            _teamIdMap = new Hashtable<Integer, Team>(wkNum*5/4);       
            
            wkResultSet.close();
         
            wkResultSet = wkStatement.executeQuery(GET_SQL);
            while (wkResultSet.next())
            {
                wkTeam = new Team(wkResultSet);
                _teamIdMap.put(Integer.valueOf(wkTeam.getId()), wkTeam);
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

    public static Enumeration<Integer> getAllIDs()
    {
        synchronized (LOCK)
        {
            return _teamIdMap.keys();
        }
    }


   
}
