/*
 * DataAccessServlet.java
 *
 * Created on February 1, 2003, 2:22 PM
 */

package com.wahoo.apba.web.servlets;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wahoo.apba.database.Division;
import com.wahoo.apba.database.DraftOrder;
import com.wahoo.apba.database.DraftPick;
import com.wahoo.apba.database.GameResult;
import com.wahoo.apba.database.League;
import com.wahoo.apba.database.Member;
import com.wahoo.apba.database.Player;
import com.wahoo.apba.database.RosterAssign;
import com.wahoo.apba.database.Schedule;
import com.wahoo.apba.database.Team;
import com.wahoo.apba.database.TeamResult;
import com.wahoo.apba.database.util.DBKeyGen;
import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.apba.excel.TeamStat;
import com.wahoo.apba.resourcemanagers.PlayerManager;
import com.wahoo.apba.resourcemanagers.StatsManager;
import com.wahoo.apba.resourcemanagers.TeamManager;
import com.wahoo.apba.resourcemanagers.TransactionManager;
import com.wahoo.apba.web.util.WebUtil;
import com.wahoo.util.Email;

/**
 * Servlet used to access resource information within the APBA web applications.
 * Used primarily by the list applets embedded in the web pages
 * 
 * @author cphillips
 */
public class DataAccessServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	transient PlayerManager _playerManager = null;
	transient TeamManager _teamManager = null;
	transient TransactionManager _transactionManager = null;
	transient StatsManager _statsManager = null;

	transient DBKeyGen _keyGen = null;

	transient TeamStat _teamStat = null;

	transient WebUtil _util = null;

	String _tables[] = new String[] { "divisons", "emailaddresses", "gameresults", "leagues", "members", "players",
			"rosterassign", "schedules", "teams", "teamresults" };

	/** Creates a new instance of DataAccessServlet */
	public DataAccessServlet()
	{
	}

	/**
	 * Required servlet init method. As this servlet is started upon the web
	 * applications being loaded, the servlet will load the resource caches. It
	 * will also check for player record updates in the the players.xml config
	 * file
	 * 
	 * @param inConfig
	 *            Servlet configuration
	 */
	public void init(javax.servlet.ServletConfig inConfig)
	{
		try
		{
			super.init(inConfig);

			_util = new WebUtil();
			_util.setServletCtx(getServletContext());

			loadProperties(inConfig);

			_playerManager = (PlayerManager) _util.getBean("PlayerManager");
			_teamManager = (TeamManager) _util.getBean("TeamManager");
			_teamManager.init();
			_transactionManager = (TransactionManager) _util.getBean("TransactionManager");
			_transactionManager.init();

			_statsManager = (StatsManager) _util.getBean("StatsManager");

			_keyGen = new DBKeyGen();

			//[cmp 11-14-2007]
			//String wkFilesDir = inConfig.getServletContext().getRealPath("/files/export");
			//System.out.println("ExportFilesDir is " + wkFilesDir);
			//WebProperties.getWebProperties().put("ExportFilesDir", wkFilesDir);

			//wkFilesDir = inConfig.getServletContext().getRealPath("/files/import");
			//System.out.println("ImportFilesDir is " + wkFilesDir);
			//WebProperties.getWebProperties().put("ImportFilesDir", wkFilesDir);
			_teamStat = new TeamStat();

			// StatsManager.refreshYearlyStats();

			// clearLocalData();
			// copyData();
		}
		catch (ServletException wkSE)
		{
			wkSE.printStackTrace();
			Email.emailException(wkSE);
		}
		catch (Exception wkE)
		{
			wkE.printStackTrace();
			Email.emailException(wkE);
		}
	}

	/**
	 * 
	 */
	public void destroy()
	{
	}

	/**
	 * 
	 * @param inConfig
	 * @throws Exception
	 */
	private void loadProperties(javax.servlet.ServletConfig inConfig) throws Exception
	{
		////XmlProperties wkXmlProperties = new XmlProperties();
		////String wkPropFile = inConfig.getServletContext().getRealPath("/files/config/WebProperties.xml");
		////wkXmlProperties.load(new FileInputStream(new File(wkPropFile)));// com.wahoo.apba.web.servlets.DataAccessServlet.class.getResourceAsStream(
		// "WebProperties.xml"
		// ) );

		////Properties wkProps = wkXmlProperties.getProperties("WebApp");
		////WebProperties.setWebProperties(wkProps);

		// String wkCardFile = wkProps.getProperty( "Cardfile" );
		// System.out.println("Setting dir = " + wkCardFile);
		// String wkCardList = wkProps.getProperty( "CardedList" );
		// System.out.println("Setting List = " + wkCardList);
		// if ( null == wkCardFile )
		// throw new Exception( "Cardfile not specified in properties file." );
		// System.getProperties().put("Cardfile", wkCardFile );
		// System.getProperties().put("CardedList", wkCardList );
		// System.getProperties().put("CurrentStatsSeason",
		// wkProps.getProperty("CurrentStatsSeason"));
		// System.getProperties().put("CurrentStandingsSeason",
		// wkProps.getProperty("CurrentStandingsSeason"));
		// System.getProperties().put("CurrentTransactionsSeason",
		// wkProps.getProperty("CurrentTransactionsSeason"));
		// System.getProperties().put("PrimaryDataSource",
		// wkProps.getProperty("PrimaryDataSource"));
		// System.getProperties().put("AltDataSource",
		// wkProps.getProperty("AltDataSource"));
		// System.getProperties().put("IPQual", wkProps.getProperty("IPQual"));
		// System.getProperties().put("ABQual", wkProps.getProperty("ABQual"));
		// System.getProperties().put("EnterOtherStats",
		// wkProps.getProperty("EnterOtherStats"));
		// System.out.println("Get other stats is " +
		// (String)System.getProperties().get("EnterOtherStats"));
	}

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest inRequest, HttpServletResponse inResponse) throws ServletException,
			IOException
	{
		int wkTeamId = 0;
		int wkYear = 0;
		String wkType = null;
		Vector wkMembers = new Vector();

		inResponse.setContentType("application/x-java-vm");

		if (null != inRequest.getParameter("tid") && !inRequest.getParameter("tid").equals(""))
			wkTeamId = Integer.parseInt(inRequest.getParameter("tid"));

		if (null != inRequest.getParameter("year") && !inRequest.getParameter("year").equals(""))
			wkYear = Integer.parseInt(inRequest.getParameter("year"));

		if (null != inRequest.getParameter("action") && !inRequest.getParameter("action").equals(""))
			wkType = inRequest.getParameter("action");

		if (null != wkType)
		{
			if ("read".equals(wkType))
			{
				// wkMembers = PlayerManager.getPlayerList(wkTeamId);
				wkMembers = _statsManager.getPlayerListForYear(wkYear, wkTeamId);
			}
			else if ("saveTeam".equals(wkType))
			{
				wkYear = _transactionManager.getCurrentTransactionsSeason();
				String wkPlayersList = inRequest.getParameter("players");
				System.out.println("Saving team: " + wkTeamId + " with players: " + wkPlayersList + "for year "
						+ wkYear);
				_teamManager.updateTeam(wkTeamId, wkPlayersList, wkYear);
				wkMembers = new Vector(1, 1);
				wkMembers.add("OK");
			}
			else if ("playerstatslist".equals(wkType))
			{
				wkMembers = _statsManager.getPlayerListForYear(wkYear, wkTeamId);
			}
			else if ("playerlist".equals(wkType))
			{
				wkYear = _transactionManager.getCurrentTransactionsSeason();
				wkMembers = _playerManager.getPlayersList(wkTeamId, wkYear);
			}
			else if ("saveplayerstatslist".equals(wkType))
			{
				String wkPlayersList = inRequest.getParameter("players");
				System.out.println("Saving team: " + wkTeamId + " with players: " + wkPlayersList);
				_statsManager.updateTeamList(wkYear, wkTeamId, wkPlayersList);
				wkMembers = new Vector(1, 1);
				wkMembers.add("OK");
			}
		}
		else
		{
			wkMembers.addAll(_playerManager.getPlayersList(wkTeamId, _transactionManager.getCurrentTransactionsSeason()));
		}

		try
		{
			ObjectOutputStream wkObjectStream = new ObjectOutputStream(new BufferedOutputStream(inResponse
					.getOutputStream(), 60000));

			System.out.println("Sending objects of count " + wkMembers.size() + "");

			wkObjectStream.writeObject(wkMembers);

			wkObjectStream.flush();
		}
		catch (IOException wkIOException)
		{
			wkIOException.printStackTrace();
			Email.emailException(wkIOException);
		}
		catch (Exception wkException)
		{
			wkException.printStackTrace();
			Email.emailException(wkException);
		}

	}

	private void copyData()
	{
		Connection wkReadConn = null;
		Connection wkWriteConn = null;
		Statement wkReadStatement = null;
		try
		{
			wkReadConn = DBUtil.getAltDBConnection();
			wkWriteConn = DBUtil.getDBConnection();
			wkReadStatement = wkReadConn.createStatement();
			ResultSet wkResultSet = null;

			System.out.println("STARTING DATA COPY");

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

			wkResultSet = wkReadStatement.executeQuery("select * from schedules");
			System.out.println("....Schedules");
			while (wkResultSet.next())
			{
				Schedule wkObject = new Schedule(wkResultSet);
				wkObject.createRecord(wkWriteConn);
			}
			wkResultSet.close();

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

			wkResultSet = wkReadStatement.executeQuery("select * from idgen");
			System.out.println("....ID Gen");
			if (wkResultSet.next())
			{
				Statement wkStatement = wkWriteConn.createStatement();
				wkStatement.executeUpdate("UPDATE idgen set nextval = " + wkResultSet.getInt("NextVal") + "");
				wkStatement.close();
				
			}
			wkResultSet.close();
			wkReadStatement.close();
			wkReadStatement = null;
			System.out.println("FINISHED DATA COPY");
		}
		catch (SQLException wkException)
		{
			wkException.printStackTrace();
			Email.emailException(wkException);
		}
		finally
		{
			try
			{
			    if (null != wkReadStatement)
			        wkReadStatement.close();
				if (null != wkReadConn)
					wkReadConn.close();
			}
			catch (SQLException wkException)
			{
				wkException.printStackTrace();
				Email.emailException(wkException);
			}
			try
			{
				if (null != wkWriteConn)
					wkWriteConn.close();
			}
			catch (SQLException wkException)
			{
				wkException.printStackTrace();
				Email.emailException(wkException);
			}
		}

	}

	private void clearLocalData()
	{
		Connection wkWriteConn = null;
		try
		{
			wkWriteConn = DBUtil.getDBConnection();
			Statement wkReadStatement = wkWriteConn.createStatement();
			//ResultSet wkResultSet = null;

			System.out.println("STARTING DATA CLEAR");

			wkReadStatement.executeUpdate("delete  from teamresults");
			System.out.println("....Team Results");

			System.out.println("....Members");
			wkReadStatement.executeUpdate("delete  from members");

			wkReadStatement.executeUpdate("delete  from players");
			System.out.println("....Players");

			wkReadStatement.executeUpdate("delete  from schedules");
			;

			wkReadStatement.executeUpdate("delete  from divisions");
			System.out.println("....Divisions");

			wkReadStatement.executeUpdate("delete  from gameresults");
			System.out.println("....GameResults");

			wkReadStatement.executeUpdate("delete  from leagues");

			wkReadStatement.executeUpdate("delete  from rosterassign");
			System.out.println("....Roster Assigns");

			wkReadStatement.executeUpdate("delete  from teamresults");
			System.out.println("....Team Results");

			wkReadStatement.executeUpdate("delete  from teams");
			System.out.println("....Teams");

			wkReadStatement.close();

			System.out.println("FINISHED DATA DELETE");
		}
		catch (SQLException wkException)
		{
			wkException.printStackTrace();
			Email.emailException(wkException);
		}
		finally
		{
			try
			{
				if (wkWriteConn != null)
					wkWriteConn.close();
			}
			catch (SQLException wkException)
			{
				wkException.printStackTrace();
				Email.emailException(wkException);
			}
		}

	}

}
