package com.wahoo.apba.database.util;

import java.sql.*;
import com.wahoo.apba.database.*;


public class CopyData
{
    
    private void copyData()
    {
        Connection wkReadConn = null;
        Connection wkWriteConn = null;
        try
        {
            DBConnection wkReadSource = new DBConnection("com.microsoft.jdbc.sqlserver.SQLServerDriver", "jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=nabl", "sbaapba", "apba");
            DBConnection wkWriteSource = new DBConnection("org.postgresql.Driver", "jdbc:postgresql://192.168.0.199/NABL", "sbaapba", "apba");
            wkReadConn = wkReadSource.getConnection();
            wkWriteConn = wkWriteSource.getConnection();
            
            Statement wkReadStatement = wkReadConn.createStatement();
            ResultSet wkResultSet = null;
        
            System.out.println("STARTING DATA COPY");
            /*
            wkResultSet = wkReadStatement.executeQuery("select * from draftorder");
            System.out.println("....DraftOrder");
            while (wkResultSet.next())
            {
                DraftOrder wkObject = new DraftOrder(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }
            wkResultSet.close();
            
            wkResultSet = wkReadStatement.executeQuery("select * from draftpicks");
            System.out.println("....DraftPicks");
            while (wkResultSet.next())
            {
                DraftPick wkObject = new DraftPick(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }
            wkResultSet.close();
            
            wkResultSet = wkReadStatement.executeQuery("select * from teamresults");
            System.out.println("....Team Results");
            while (wkResultSet.next())
            {
                TeamResult wkObject = new TeamResult(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            } 
            wkResultSet.close();
            
            System.out.println("....Members");
            wkResultSet = wkReadStatement.executeQuery("select * from members");
            while (wkResultSet.next())
            {
                Member wkObject = new Member(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }
            wkResultSet.close();

            wkResultSet = wkReadStatement.executeQuery("select * from players");
            System.out.println("....Players");
            while (wkResultSet.next())
            {
                Player wkObject = new Player(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }        
            wkResultSet.close();
            */
            wkResultSet = wkReadStatement.executeQuery("select * from schedules");
            System.out.println("....Schedules");
            while (wkResultSet.next())
            {
                Schedule wkObject = new Schedule(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }
            wkResultSet.close();
            /*
            wkResultSet = wkReadStatement.executeQuery("select * from divisions");
            System.out.println("....Divisions");
            while (wkResultSet.next())
            {
                Division wkObject = new Division(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }
            wkResultSet.close();

            wkResultSet = wkReadStatement.executeQuery("select * from gameresults");
            System.out.println("....GameResults");
            while (wkResultSet.next())
            {
                GameResult wkObject = new GameResult(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }        
            wkResultSet.close();

            wkResultSet = wkReadStatement.executeQuery("select * from leagues");
            System.out.println("....Leagues");
            while (wkResultSet.next())
            {
                League wkObject = new League(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }
            wkResultSet.close();

            wkResultSet = wkReadStatement.executeQuery("select * from rosterassign");
            System.out.println("....Roster Assigns");
            while (wkResultSet.next())
            {
                RosterAssign wkObject = new RosterAssign(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }
            wkResultSet.close();

            wkResultSet = wkReadStatement.executeQuery("select * from teams");
            System.out.println("....Teams");
            while (wkResultSet.next())
            {
                Team wkObject = new Team(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }
            wkResultSet.close();
            
            wkResultSet = wkReadStatement.executeQuery("select * from transactions");
            System.out.println("....Transactions");
            while (wkResultSet.next())
            {
                Transaction wkObject = new Transaction(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }
            wkResultSet.close();
            
            wkResultSet = wkReadStatement.executeQuery("select * from rostermove");
            System.out.println("....RosterMove");
            while (wkResultSet.next())
            {
                RosterMove wkObject = new RosterMove(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }
            wkResultSet.close();
            
            
            wkResultSet = wkReadStatement.executeQuery("select * from seriesstatrecords");
            System.out.println("....SeriesStatRecords");
            while (wkResultSet.next())
            {
                SeriesStatRecord wkObject = new SeriesStatRecord(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }
            wkResultSet.close();
            
            wkResultSet = wkReadStatement.executeQuery("select * from statrecords");
            System.out.println("....StatRecords");
            while (wkResultSet.next())
            {
                StatRecord wkObject = new StatRecord(wkResultSet);
                wkObject.createRecord(wkWriteConn);
            }
            wkResultSet.close();
            
            wkResultSet = wkReadStatement.executeQuery("select * from idgen");
            System.out.println("....ID Gen");
            if (wkResultSet.next())
            {
                Statement wkStatement = wkWriteConn.createStatement();
                wkStatement.executeUpdate("UPDATE idgen set nextval = " + wkResultSet.getInt("NextVal") + "");
            }            
            wkResultSet.close();
            */
            wkReadStatement.close();
            System.out.println("FINISHED DATA COPY");
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
            try
            {
                if (null != wkWriteConn)
                    wkWriteConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
            }
        }

    }

  private void clearLocalData()
    {
        Connection wkWriteConn = null;
        try
        {
            DBConnection wkWriteSource = new DBConnection("org.postgresql.Driver", "jdbc:postgresql://192.168.0.199/NABL", "sbaapba", "apba");
            wkWriteConn = wkWriteSource.getConnection();
            Statement wkReadStatement = wkWriteConn.createStatement();
        
            System.out.println("STARTING DATA CLEAR");

            /*
            wkReadStatement.executeUpdate("delete  from draftorder");
            System.out.println("....DraftOrder");
            
            wkReadStatement.executeUpdate("delete  from draftpicks");
            System.out.println("....DraftPicks");            
            wkReadStatement.executeUpdate("delete  from teamresults");
            System.out.println("....Team Results");
            
            /*
            System.out.println("....Members");
            wkReadStatement.executeUpdate("delete  from members");

            wkReadStatement.executeUpdate("delete  from players");
            System.out.println("....Players");
            */
            wkReadStatement.executeUpdate("delete  from schedules");;
            System.out.println("....Schedules");
        
            /*
            wkReadStatement.executeUpdate("delete  from divisions");
            System.out.println("....Divisions");

            */
            wkReadStatement.executeUpdate("delete  from gameresults");
            System.out.println("....GameResults");

            /*
            wkReadStatement.executeUpdate("delete  from leagues");

            wkReadStatement.executeUpdate("delete  from rosterassign");
            System.out.println("....Roster Assigns");

            */
            wkReadStatement.executeUpdate("delete  from seriesstatrecords");
            System.out.println("....SeriesStatRecords");
            
            /*
            wkReadStatement.executeUpdate("delete  from transactions");
            System.out.println("....Transactions");
            
            wkReadStatement.executeUpdate("delete  from rostermove");
            System.out.println("....Roster Move");
            

            wkReadStatement.executeUpdate("delete  from teams");
            System.out.println("....Teams");
            */
            wkReadStatement.executeUpdate("delete  from statrecords");
            System.out.println("....StatRecords");
            
            wkReadStatement.close();
                      
            System.out.println("FINISHED DATA DELETE");
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != wkWriteConn)
                    wkWriteConn.close();
            }
            catch (SQLException wkException)
            {
                wkException.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args)
    {	
        CopyData wkCopier = new CopyData();
        wkCopier.clearLocalData();
		wkCopier.copyData();
	}
}

    