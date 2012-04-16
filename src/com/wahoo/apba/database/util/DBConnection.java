package com.wahoo.apba.database.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;


////
// CLASS: UserLoginConnection
////
public class DBConnection
{
	// public static final String  DRIVER_NAME = "com.inet.tds.TdsDriver";  // INET JDBC DRIVER
	public String  DRIVER_NAME;
    protected Driver     DRIVER;
    public String        DB_URL;
    protected Properties PROPS;

   

    ////
    // CONSTRUCTOR
    ////
    public DBConnection(String inDriver, String inURL, String inUser, String inPassword)
    {
        try
        {
            DRIVER_NAME = inDriver;
            
            DRIVER = (Driver) Class.forName( DRIVER_NAME ).newInstance();
            
			// INET JDBC DRIVER:
            // DB_URL =
            //    "jdbc:inetdae7:" +                
            //    System.getProperty( "TQ.DBHost" ) + ":" +
            //    System.getProperty( "TQ.DBPort" ) + "?" +
            //    "database=acd";

			// WEBLOGIC JDBC DRIVER:
            //DB_URL =
            //    "jdbc:weblogic:mssqlserver4:" + 
            //    System.getProperty( "TQ.DBName" ) + "@" +
            //    System.getProperty( "TQ.DBHost" ) + ":" +
            //    System.getProperty( "TQ.DBPort" ) + "?" ;
              
            DB_URL = inURL;
              

            PROPS = new Properties();
            PROPS.put( "user", inUser );
            PROPS.put( "password", inPassword );
        }
        catch ( Exception excp )
        {
            //Email.emailException(excp);
            excp.printStackTrace();
        }
    }

    ////
    // METHOD: getConnection
    ////
    public Connection getConnection() throws SQLException
    {
        Connection wkConn = DRIVER.connect( DB_URL, PROPS );
        try { wkConn.setAutoCommit( true ); } catch ( Exception excp ) { ; }
        return wkConn;
    }
}
