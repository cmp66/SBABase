package com.wahoo.apba.database.util;

import java.sql.*;
import com.wahoo.apba.database.*;
import java.util.HashMap;


public class VerifyPlayerNames
{
    public VerifyPlayerNames()
    {}
    
    private void readData(String inDBHost, String inDBName)
    {
        Connection wkReadConn = null;
        HashMap<String, Player> dnameMap = new HashMap<String, Player>();
        HashMap<String, Player> nameMap = new HashMap<String, Player>();
        
        try
        {
        	String wkURL = "jdbc:postgresql://192.168.1.6/NABL";
        	System.out.println(wkURL);
            DBConnection wkReadSource = new DBConnection("org.postgresql.Driver", wkURL, "sbaapba", "apba");
            wkReadConn = wkReadSource.getConnection();
            
            Statement wkReadStatement = wkReadConn.createStatement();
            ResultSet wkResultSet = null;
        
            System.out.println("STARTING PLAYER VERIFY");

            wkResultSet = wkReadStatement.executeQuery("select * from players");
            while (wkResultSet.next())
            {
                Player wkObject = new Player(wkResultSet);
                String wkName = wkObject.getFirstname() + " " + wkObject.getLastname();
                String wkDName = wkObject.getDisplayname();
                if (dnameMap.containsKey(wkDName))
                {
                	System.out.println("dupliate displayname for " + wkDName);
                }
                else
                {
                	dnameMap.put(wkDName, wkObject);
                }
                if (nameMap.containsKey(wkName))
                {
                	System.out.println("dupliate name for " + wkName);
                }
                else
                {
                	nameMap.put(wkName, wkObject);
                }
                
            }        
 
            wkResultSet.close();
            wkReadStatement.close();
            System.out.println("FINISHED PLAYER VERIFY");
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != wkReadConn)
                    wkReadConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args)
    {	
        VerifyPlayerNames wkVerify = new VerifyPlayerNames();
        wkVerify.readData(args[0], args[1]);
	}
}

    