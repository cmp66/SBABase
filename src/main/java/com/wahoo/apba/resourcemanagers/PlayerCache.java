/*
 * PlayerCache.java
 *
 * Created on February 1, 2003, 9:17 PM
 */

package com.wahoo.apba.resourcemanagers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;

import com.wahoo.apba.database.Player;
import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class PlayerCache
{
 
    private Hashtable<Integer, Player> _playerIdMap = null;
    private Hashtable<String, Player> _playerURLMap = null;
    
    private static String COUNT_SQL = "select count(*) from players";
    private static String GET_PLAYERS = "select * from players order by lastname, firstname";
    
	private Object LOCK = new Object();
	   
    public PlayerCache()
    { 
        createMap();
    }

    
    public Player get(int inId)
    {
        synchronized (LOCK)
        {
            return (Player)_playerIdMap.get(Integer.valueOf(inId));
        }
    }

    public Player get(String inURL)
    {
        synchronized (LOCK)
        {
            return (Player)_playerURLMap.get(inURL);
        }
    }

    public void put(Player inPlayerRecord)
    {
        synchronized (LOCK)
        {
            _playerIdMap.put( Integer.valueOf(inPlayerRecord.getId()), inPlayerRecord  );
            _playerURLMap.put( inPlayerRecord.getBbreflink(), inPlayerRecord  );
        }
    }  


    public void remove(int inId)
    {
        synchronized (LOCK)
        {
            Integer wkId = Integer.valueOf(inId);
            String wkUrl = ((Player)_playerIdMap.get(wkId)).getBbreflink();
            _playerIdMap.remove(wkId);
            _playerURLMap.remove(wkUrl);
        }
    } 

    public void dumpMap()
    {
        synchronized (LOCK)
        {
    	    Enumeration<Integer> e = _playerIdMap.keys();
    	    while (e.hasMoreElements())
    	    {
    		    Integer id = e.nextElement();
    		    System.out.println("        playermap key: " + id);
    	    }
    	}
    }
        
        
    public void resetMap()
    {
        synchronized (LOCK)
        {
            _playerIdMap.clear();
            _playerURLMap.clear();
            _playerIdMap = null;
            _playerURLMap = null;
            createMap();
        }
    }


    public void createMap()
    {
        Statement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        Player wkPlayer = null;
         
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.createStatement();
            
            wkResultSet = wkStatement.executeQuery(COUNT_SQL);
            wkResultSet.next();
            
            int wkNumPlayers = wkResultSet.getInt(1);
            
            _playerIdMap = new Hashtable<Integer, Player>(wkNumPlayers*4/3);
            _playerURLMap = new Hashtable<String, Player>(wkNumPlayers*4/3);            
            
            wkResultSet.close();
         
            wkResultSet = wkStatement.executeQuery(GET_PLAYERS);
            while (wkResultSet.next())
            {
                wkPlayer = new Player(wkResultSet);
                _playerIdMap.put(Integer.valueOf(wkPlayer.getId()), wkPlayer);
                _playerURLMap.put(wkPlayer.getBbreflink(), wkPlayer);
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

    public Enumeration<Integer> getAllPlayerIDs()
    {
        synchronized (LOCK)
        {
            return _playerIdMap.keys();
        }
    }


   
}
