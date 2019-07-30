package com.wahoo.apba.database.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class RollYear
{
    // public static final String DRIVER_NAME =
    // "com.microsoft.jdbc.sqlserver.SQLServerDriver"; // Microsoft JDBC DRIVER
    public static final String  DRIVER_NAME = "org.postgresql.Driver";                     // PostgreSQL
                                                                                            // driver

    // public static final String DBNAME = "sba2";
    public static final String  DBNAME      = "NABL";
    public static final String  DBUSER      = "sbaapba";
    public static final String  DBPASSWORD  = "apba";
    // public static final String DBHOST = "localhost";
    public static final String  DBHOST      = "192.168.1.6";
    public static final String  DBPORT      = "5432";
    // public static final String DBPORT = "";

    public final static String  DB_URL      = "jdbc:postgresql://" + DBHOST + "/" + DBNAME;
    protected static final Properties PROPS = new Properties();

    static
    {
        try
        {

            // INET JDBC DRIVER:
            // DB_URL =
            // "jdbc:inetdae7:" +
            // System.getProperty( "TQ.DBHost" ) + ":" +
            // System.getProperty( "TQ.DBPort" ) + "?" +
            // "database=acd";

            // WEBLOGIC JDBC DRIVER:
            // DB_URL = "jdbc:microsoft:sqlserver://" + DBHOST +":" + DBPORT +
            // ";DatabaseName=" + DBNAME + "";

            System.out.println(DB_URL);
            PROPS.put("user", DBUSER);
            PROPS.put("password", DBPASSWORD);
        }
        catch (Exception excp)
        {
            excp.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException
    {
        try
        {
            final Driver DRIVER = (Driver) Class.forName(DRIVER_NAME).newInstance();

            Connection wkConn = DRIVER.connect(DB_URL, PROPS);
            try
            {
                wkConn.setAutoCommit(true);
            }
            catch (Exception excp)
            {
            }
            return wkConn;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
        
    }

    public static void main(String[] args)
    {
        rollPlayers();
    }

    private static void rollPlayers()
    {
        Connection wkConnection = null;
        try
        {
            String INSERT_SQL = "INSERT into rosterassign (playerid, teamid, year) VALUES(?, ?, ?);";
            wkConnection = getConnection();
            Statement wkStatement = wkConnection.createStatement();
            PreparedStatement wkWrite = wkConnection.prepareStatement(INSERT_SQL);
            ResultSet wkResults = wkStatement.executeQuery("Select * from rosterassign where year = 2010");

            while (wkResults.next())
            {
                wkWrite.setInt(1, wkResults.getInt("playerid"));
                wkWrite.setInt(2, wkResults.getInt("teamid"));
                wkWrite.setInt(3, 2011);

                wkWrite.executeUpdate();
            }

            wkResults.close();
            wkStatement.close();
            wkWrite.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != wkConnection)
                    wkConnection.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
            }
        }
    }
}
