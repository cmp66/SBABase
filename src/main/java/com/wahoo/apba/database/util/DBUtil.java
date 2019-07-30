/*
 * DBUtil.java
 *
 * Created on February 1, 2003, 1:59 PM
 */

package com.wahoo.apba.database.util;

import java.sql.Connection;

import javax.naming.Context;
import javax.sql.DataSource;

import com.wahoo.util.Email;
import com.wahoo.util.InitialContextFactory;

/** General collection of database access helper methods
 * @author cphillips
 */
public class DBUtil 
    {    
    
    /** Creates a new instance of DBUtil */
    public DBUtil() 
    {
    }
    
    
	/** A method to get a connection from the JBoss connection pool
     * @returns a java.sql.Connection object if a connection could be obtained otherwise null.
     * @return Connection object to the MSSQLDS database
     */
	public static Connection getAltDBConnection()
	{	
	    Connection wkConnection = null;
	    
	    try
	    {
            DataSource wkDataSource = getDataSource("jdbc/MSSQLDS");
            //DataSource wkDataSource = getDataSource((String)WebProperties.getWebProperties().get("AltDataSource"));
	        wkConnection = wkDataSource.getConnection();
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        Email.emailException(e);
	    }
	    return wkConnection;
	}

	public static Connection getDBConnection()
	{	
	    Connection wkConnection = null;
	    
	    try
	    {
            DataSource wkDataSource = getDataSource("jdbc/POSTGRESDSNABL");
            //DataSource wkDataSource = getDataSource((String)WebProperties.getWebProperties().get("PrimaryDataSource"));
            wkConnection = wkDataSource.getConnection();
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        Email.emailException(e);
	    }
	    return wkConnection;
	}
    
     /** A method to get a Read Only connection from the JBoss connection pool
     * @returns a java.sql.Connection object if a connection could be obtained otherwise null.
     * @return Connection object to the MSSQLDS database
     */
	public static Connection getReadOnlyDBConnection()
	{	
	    Connection wkConn = null;
	    
	    try
	    {
            DataSource wkDataSource = getDataSource("jdbc/POSTGRESDSNABL");
            //DataSource wkDataSource = getDataSource((String)WebProperties.getWebProperties().get("PrimaryDataSource"));
	        wkConn = wkDataSource.getConnection();
            //wkConn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        Email.emailException(e);
	    }
	    return wkConn;
	}
    
    /** Obtains a Datasource object to the named datasource.  Works for JBOSS server
     * @param dataSourceName name of target datasource
     * @return Datasource Object
     */    
    public static  DataSource getDataSource(String dataSourceName) 
    {
        DataSource wkDataSource = null;
        try 
        {
        	
            Context context = InitialContextFactory.getJBossInitialContext();
            wkDataSource = (DataSource)context.lookup(dataSourceName);
         } 
         catch (Exception e)
         {
            e.printStackTrace();
            Email.emailException(e);
         }   
      return wkDataSource;
   }	
    
    /*
   public static UserTransaction createTransaction()
   {
       UserTransaction wkTrans = null; 
       try
       {
            Context wkCtx = InitialContextFactory.getJBossInitialContext();
            wkTrans = (UserTransaction) wkCtx.lookup("javax.transaction.UserTransaction");
       }
       catch (Exception wkEx)
       {
           wkEx.printStackTrace();
           Email.emailException(wkEx);
       }
       
       return wkTrans;
   }
    */   
}