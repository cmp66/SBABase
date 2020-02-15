/*
 * PlayerManager.java
 *
 * Created on February 1, 2003, 9:15 PM
 */

package com.wahoo.apba.resourcemanagers;

import com.wahoo.apba.database.CardedPlayer;
import com.wahoo.apba.database.Player;
import com.wahoo.apba.database.PlayerCard;
import com.wahoo.apba.database.util.DBUtil;
import com.wahoo.apba.web.util.ListMember;
import com.wahoo.util.Email;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author  cphillips
 */
public class PlayerManager
{
    private PlayerCache playerCache = null;
    //private static PlayerManager _manager = null;
    private RotoPlayerManager rotoPlayerManager = null;
    private TeamManager teamManager = null;
    private StatsManager statsManager = null;
    
    private JdbcTemplate jdbcTemplate = null;
    
    private Object _lock = new Object();
    
    private static String ADD_SQL = "INSERT into players (firstname, lastname, displayname, startyear, endyear, bbreflink, rotowireid, position, " +
                                    "bats, throwhand, birthdate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static String CARDED_LIST_SQL = "SELECT * from cardedplayers where season = ?";
    
    /** Creates a new instance of PlayerManager */
    private PlayerManager ()
    {
    	init();
    }
    
    public void init()
    {
    	//_manager = this;
        //verifyPlayerDB();
    }
    
    public void setJdbcTemplate(JdbcTemplate inTemplate)
    {
    	this.jdbcTemplate = inTemplate;
    }
    
    public JdbcTemplate getJdbcTemplate()
    {
    	return this.jdbcTemplate;
    }
    
    public void setPlayerCache(PlayerCache inCache)
    {
    	this.playerCache = inCache;
    }
    
    public PlayerCache getPlayerCache()
    {
    	return this.playerCache;
    }
    
    public void setTeamManager (TeamManager inMgr)
    {
    	this.teamManager = inMgr;
    }
    
    public void setStatsManager (StatsManager inMgr)
    {
    	this.statsManager = inMgr;
    }
    
    public void setRotoPlayerManager (RotoPlayerManager inMgr)
    {
    	this.rotoPlayerManager = inMgr;
    }
    /*
    public static PlayerManager getInstance()
    {
        synchronized(_lock)
        {
            if (null == _manager)
            {
                _manager = new PlayerManager();
                //_manager.init();
            }
        }
        
        return _manager;
    }
    */
    
    @SuppressWarnings ("unchecked")
    private void verifyPlayerDB()
    {
	    Document wkDoc = null;
        SAXBuilder wkBuilder = new SAXBuilder();
	    try
	    {
	        wkDoc = wkBuilder.build(com.wahoo.apba.resourcemanagers.PlayerManager.class.getResourceAsStream("playerxml.xml"));
            Element wkRoot = wkDoc.getRootElement();
            Iterator<Element> wkPlayers = wkRoot.getChildren().iterator();
            
            while (wkPlayers.hasNext())
            {
                Element wkPlayer = wkPlayers.next();
                String wkBBref = wkPlayer.getChild("bbref-address").getTextTrim();
                
                Player wkPlayerRecord = playerCache.get(wkBBref);
                if (null == wkPlayerRecord)
                {
                    wkPlayerRecord = new Player();
                    wkPlayerRecord.setBats("");
                    wkPlayerRecord.setBbreflink(wkBBref);
                    wkPlayerRecord.setBirthdate("");
                    wkPlayerRecord.setDisplayname(wkPlayer.getChild("full-name").getTextTrim());
                    wkPlayerRecord.setEndyear(Integer.parseInt(wkPlayer.getChild("last-year").getTextTrim()));
                    wkPlayerRecord.setFirstname(wkPlayer.getChild("first-name").getTextTrim());
                    wkPlayerRecord.setId(0);
                    wkPlayerRecord.setLastname(wkPlayer.getChild("last-name").getTextTrim());
                    wkPlayerRecord.setPosition("");
                    wkPlayerRecord.setRotowireid("");
                    wkPlayerRecord.setStartyear(Integer.parseInt(wkPlayer.getChild("first-year").getTextTrim()));
                    wkPlayerRecord.setThrowhand("");
                    
                    addPlayer(wkPlayerRecord);
                }
            }
            
            
        }
        catch (JDOMException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
            return;
        }                
    }
    
    private void addPlayer(Player inPlayer)
    {
    	Object[] wkParams = new Object[] {
    			inPlayer.getFirstname(),
                inPlayer.getLastname(),
                inPlayer.getDisplayname(),
                Integer.valueOf(inPlayer.getStartyear()),
                Integer.valueOf(inPlayer.getEndyear()),
                inPlayer.getBbreflink(),
                inPlayer.getRotowireid(),
                inPlayer.getPosition(),
                inPlayer.getBats(),
                inPlayer.getThrowhand(),
                new Timestamp(System.currentTimeMillis()) };
            
    	jdbcTemplate.update(ADD_SQL, wkParams);
 
        playerCache.resetMap();
    }
    
   public Vector<ListMember> getPlayersList(int inTeamId, int inYear)
    {
        HashMap<Integer, String> wkTeamMembers = teamManager.getPlayerIndexesForTeam(inTeamId, inYear);
        Vector<ListMember> wkMembers = new Vector<ListMember>(250, 5);

        try
        {
            boolean lclSelected = false;

            Enumeration<Integer> wkPlayers = playerCache.getAllPlayerIDs();

            while (wkPlayers.hasMoreElements())
            {
                Integer wkPlayerId = wkPlayers.nextElement();
                Player wkPlayer = playerCache.get(wkPlayerId.intValue());

                lclSelected = wkTeamMembers.containsKey(wkPlayerId);

                ListMember wkNewMember = new ListMember();

                wkNewMember.setId(wkPlayerId.toString());
                wkNewMember.setSelected(lclSelected);
                wkNewMember.addDisplayString(wkPlayer.getLastname() + "," + wkPlayer.getFirstname());
                     
                wkMembers.add(wkNewMember);
            }
        }
        catch (Exception wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }

        return wkMembers;
    }
    public String getStartingGrade(PlayerCard inCard)
    {
        StringBuffer wkText = new StringBuffer(20);
        
        if (null == inCard.getStartingGrade() || inCard.getStartingGrade().equals(" "))
            return " ";
        
        int wkGrade = Integer.parseInt(inCard.getStartingGrade());
        
        if (wkGrade > 24)
            wkText.append("A&B");
        else if (wkGrade > 19)
            wkText.append("A&C");
        else if (wkGrade > 14)
            wkText.append("A");
        else if (wkGrade > 9)
            wkText.append("B");
        else if (wkGrade > 4)
            wkText.append("C");
        else
            wkText.append("D");
        
        if (null != inCard.getKLetter() && !inCard.getKLetter().equals(" "))
            wkText.append(inCard.getKLetter().toLowerCase());

        if (null != inCard.getWLetter() && !inCard.getWLetter().equals(" "))
            wkText.append(inCard.getWLetter().toLowerCase());
        
        return wkText.toString();
    }

    public String getReliefGrade(PlayerCard inCard)
    {
        StringBuffer wkText = new StringBuffer(20);
        
        if (null == inCard.getReliefGrade() || inCard.getReliefGrade().equals(" "))
            return " ";
        
        int wkGrade = Integer.parseInt(inCard.getReliefGrade());
        
        if (wkGrade > 24)
            wkText.append("A&B");
        else if (wkGrade > 19)
            wkText.append("A&C");
        else if (wkGrade > 14)
            wkText.append("A");
        else if (wkGrade > 9)
            wkText.append("B");
        else if (wkGrade > 4)
            wkText.append("C");
        else
            wkText.append("D");
        
        if (null != inCard.getKLetter() && !inCard.getKLetter().equals(" "))
            wkText.append(inCard.getKLetter().toLowerCase());

        if (null != inCard.getWLetter() && !inCard.getWLetter().equals(" "))
            wkText.append(inCard.getWLetter().toLowerCase());
        
        return wkText.toString();
    }
        
        
    public String convertPosition(String inNumber)
    {
        String wkPosition = "Ukwn";
       
        if (inNumber.equals("8") ||
            inNumber.equals("7") ||
            inNumber.equals("9"))
            wkPosition = "OF";
        else if (inNumber.equals("1"))
            wkPosition = "P";
        else if (inNumber.equals("2"))
            wkPosition = "C";
        else if (inNumber.equals("3"))
            wkPosition = "1B";
        else if (inNumber.equals("4"))
            wkPosition = "2B";
        else if (inNumber.equals("5"))
            wkPosition = "3B";
        else if (inNumber.equals("6"))
            wkPosition = "SS";
        
        return wkPosition;
    }
    
    public String getPositionList(PlayerCard inCard)
    {
        StringBuffer wkList = new StringBuffer(80);
        
        if (null != inCard.getPitchD() && !inCard.getPitchD().equals("0"))
            wkList.append(" P-").append(inCard.getPitchD());

        if (null != inCard.getCatcherD() && !inCard.getCatcherD().equals("0"))
            wkList.append(" C-").append(inCard.getCatcherD());
        
        if (null != inCard.getFirstD() && !inCard.getFirstD().equals("0"))
            wkList.append(" 1B-").append(inCard.getFirstD());
        
        if (null != inCard.getSecondD() && !inCard.getSecondD().equals("0"))
            wkList.append(" 2B-").append(inCard.getSecondD());
        
        if (null != inCard.getThirdD() && !inCard.getThirdD().equals("0"))
            wkList.append(" 3B-").append(inCard.getThirdD());
        
        if (null != inCard.getSsD() && !inCard.getSsD().equals("0"))
            wkList.append(" SS-").append(inCard.getSsD());
        
        if (null != inCard.getOfD() && !inCard.getOfD().equals("0"))
            wkList.append(" OF-").append(inCard.getOfD());
        
        return wkList.toString();
        
    }
            
    public int getPlayerId(String inLastName, String inFirstName)
    {
    	String wkQuery = "select id from players where lastname = ? and firstname = ?";
    	int result = 0;
 
    	try
    	{
    		result = jdbcTemplate.queryForInt(wkQuery,
    						new Object[] {inLastName, inFirstName},
    						new int[] { Types.VARCHAR, Types.VARCHAR });
    	}
    	catch (IncorrectResultSizeDataAccessException e)
    	{
    		System.out.println("No Player found for " + inFirstName + " " + inLastName);
    	}
    	
    	return result;
    }
    	
 
    
    public Element getPlayerInfo(int inId)
    {
    	Element wkPlayer = new Element("Player");
    	Player wkPlayerRecord = playerCache.get(inId);
    	
    	wkPlayer.addContent(new Element("FirstName").setText(wkPlayerRecord.getFirstname()));
    	wkPlayer.addContent(new Element("LastName").setText(wkPlayerRecord.getLastname()));
    	wkPlayer.addContent(new Element("DisplayName").setText(wkPlayerRecord.getDisplayname()));
    	wkPlayer.addContent(new Element("BBRefLink").setText(wkPlayerRecord.getBbreflink()));
    	
    	wkPlayer.addContent(statsManager.getPlayerCareerStats(inId));
    	
    	wkPlayer.addContent(rotoPlayerManager.getPlayerNews(inId));
    	
    	return wkPlayer;
    }
    
    public ArrayList<CardedPlayer> getCardedPlayers()
    {
        PreparedStatement wkStatement = null;
        Connection wkConn = null;
        ResultSet wkResultSet = null;
        ArrayList<CardedPlayer> cardedPlayers = new ArrayList<CardedPlayer>(); 
        try
        {
            wkConn = DBUtil.getReadOnlyDBConnection();
            wkStatement = wkConn.prepareStatement(CARDED_LIST_SQL);
            
            wkStatement.setInt(1, 2019);
            
            wkResultSet = wkStatement.executeQuery();
            
            while (wkResultSet.next()) 
            {
            	CardedPlayer player = new CardedPlayer(wkResultSet);
            	cardedPlayers.add(player);
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
            }
        }
        
        return cardedPlayers;
    }
}
