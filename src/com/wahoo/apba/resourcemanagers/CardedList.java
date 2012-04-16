/*
 * PlayerCache.java
 *
 * Created on February 1, 2003, 9:17 PM
 */

package com.wahoo.apba.resourcemanagers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.jdom.Element;

import com.wahoo.apba.database.CardedPlayer;
import com.wahoo.apba.database.Player;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class CardedList
{
 
    private TreeMap<String, TreeMap<String, String>> _cardList = null;
    private HashMap<String, String> _allList = null;
    
    private PlayerManager playerManager = null;
    private TeamManager teamManager = null;
    private TransactionManager transactionManager = null;
    private PlayerCache playerCache = null;
    private CutList cutList = null;
    
    private String playerList = null;
    
    private String getId = null;
    	   
    public CardedList ()
    { 
        //createList(inGetId);
    }

    public void setPlayerManager(PlayerManager inManager)
    {
    	this.playerManager = inManager;
    }
    
    public void setTeamManager(TeamManager inManager)
    {
    	this.teamManager = inManager;
    }
    
    public void setCutList(CutList list)
    {
    	this.cutList = list;
    }    
    
    public void setTransactionManager (TransactionManager inMgr)
    {
    	this.transactionManager = inMgr;
    }
    
    
    public void setPlayerCache(PlayerCache inCache)
    {
    	this.playerCache = inCache;
    }
    
    public void setGetId(String inVal)
    {
    	this.getId = inVal;
    	System.out.println("GETID: " + inVal);
    }
   
    public Element getCardedList()
    {
        Element wkList = new Element("CardedPlayers");
        Iterator<String> wkIter = _cardList.keySet().iterator();
 
        while (wkIter.hasNext())
    	{
            String wkTeamName = wkIter.next();
            Element wkTeam = new Element("Team");
            wkTeam.addContent(new Element("Name").setText(wkTeamName));
            wkList.addContent(wkTeam);
            
            TreeMap<String, String> wkPlayerList = _cardList.get(wkTeamName);
            Iterator<String> wkPlayers = wkPlayerList.keySet().iterator();
            while (wkPlayers.hasNext())
            {
    		    String wkStringId = wkPlayers.next();
                Element wkPlayerElement = new Element("Player");
                wkTeam.addContent(wkPlayerElement);
                try
                {
                	String abbrev = "";
                    int wkId = Integer.parseInt(wkStringId);
                    Player wkPlayer = playerCache.get(wkId);
                    int teamid = cutList.getCutTeam(wkId);
                    
                    if (0 != teamid) 
                    {
                    	abbrev = "(" + TeamCache.get(teamid).getNickname() + ")";
                    }
                    
                    wkPlayerElement.addContent(new Element("id").setText(wkId+""));
                    wkPlayerElement.addContent(new Element("displayname").setText(wkPlayer.getDisplayname() + abbrev));
                    wkPlayerElement.addContent(new Element("bbreflink").setText(wkPlayer.getBbreflink()));
                }
                catch (NumberFormatException wkEx)
                {
                    wkPlayerElement.addContent(new Element("displayname").setText(wkStringId));;
                    wkPlayerElement.addContent(new Element("bbreflink").setText(wkStringId));
                }
    	    }
    	}
        
        Iterator<Integer>wkCutsIter = cutList.getCuts().iterator();
        Element wkTeam = new Element("Team");
        wkTeam.addContent(new Element("Name").setText("Uncarded"));
        wkList.addContent(wkTeam);
        ArrayList<String> wkCardArray = getCardedListArray();
        
        while(wkCutsIter.hasNext())
        {
        	Integer wkId = wkCutsIter.next();
        	if (!wkCardArray.contains(wkId.toString()))
        	{
        		Player wkPlayer = playerCache.get(wkId.intValue());
        		Element wkPlayerElement = new Element("Player");
        		wkTeam.addContent(wkPlayerElement);
        		
        		String abbrev = "";
                int teamid = cutList.getCutTeam(wkId.intValue());
                
                if (0 != teamid) 
                {
                	abbrev = "(" + TeamCache.get(teamid).getNickname() + ")";
                }
        		wkPlayerElement.addContent(new Element("id").setText(wkId.toString()));
                wkPlayerElement.addContent(new Element("displayname").setText(wkPlayer.getDisplayname() + abbrev));
                wkPlayerElement.addContent(new Element("bbreflink").setText(wkPlayer.getBbreflink()));
        		
        	}
        	
        }
        
        
        return wkList;
    }
    
    public ArrayList<String> getCardedListArray()
    {
        ArrayList<String> wkList = new ArrayList<String>();
        Iterator<String> wkIter = _cardList.keySet().iterator();
    	
        while (wkIter.hasNext())
    	{
            String wkTeamName = wkIter.next();
            
            TreeMap<String,String> wkPlayerList = _cardList.get(wkTeamName);
            Iterator<String> wkPlayers = wkPlayerList.keySet().iterator();
            while (wkPlayers.hasNext())
            {
    		    String wkStringId = (String)wkPlayers.next();
                wkList.add(wkStringId);
    	    }
    	}
        
        return wkList;
    }

    public Element getSortedList()
    {
        Element wkList = new Element("CardedPlayers");
        Iterator<String> wkIter = _cardList.keySet().iterator();
        TreeMap<String, Element> wkSortedList = new TreeMap<String, Element>();
    	
        while (wkIter.hasNext())
    	{
            String wkTeamName = (String) wkIter.next();
            
            TreeMap<String,String> wkPlayerList = _cardList.get(wkTeamName);
            Iterator<String> wkPlayers = wkPlayerList.keySet().iterator();
            while (wkPlayers.hasNext())
            {
    		    String wkStringId = wkPlayers.next();
                Element wkPlayerElement = new Element("Player");
                //wkList.addContent(wkPlayerElement);
                try
                {
                    int wkId = Integer.parseInt(wkStringId);
                    Player wkPlayer = playerCache.get(wkId);
                    wkPlayerElement.addContent(new Element("id").setText(wkId+""));
                    wkPlayerElement.addContent(new Element("displayname").setText(wkPlayer.getDisplayname()));
                    wkPlayerElement.addContent(new Element("bbreflink").setText(wkPlayer.getBbreflink()));
                    wkSortedList.put(wkPlayer.getDisplayname(), wkPlayerElement);
                }
                catch (NumberFormatException wkEx)
                {
                    wkPlayerElement.addContent(new Element("displayname").setText(wkStringId));;
                    wkPlayerElement.addContent(new Element("bbreflink").setText(wkStringId));
                    wkSortedList.put(wkStringId, wkPlayerElement);
                }
    	    }
    	}
        
        Iterator<Element> wkElementIter = wkSortedList.values().iterator();
        while (wkElementIter.hasNext())
        {
        	Element wkPlayerElement = wkElementIter.next();
        	wkList.addContent(wkPlayerElement);
        }
        
        return wkList;
    }

    public void oldcreateList()
    {
    	boolean inGetId = Boolean.valueOf(getId).booleanValue();
    	System.out.println("CREATE GETID " + inGetId);
        final int MY_BUFF_SIZE = 30000;
        BufferedReader wkReader = null;
        _cardList = new TreeMap<String, TreeMap<String, String>>();
        _allList = new HashMap<String, String>();
        TreeMap<String, String> wkTeamList = null;
         
        try
        {
            //String wkName = (String) WebProperties.getWebProperties().get("CardedList");//"../server/default/deploy/sba.war/2002cards.csv";
            //System.out.println("Opening file " + wkName);
            //URL wkURL = com.wahoo.apba.resourcemanagers.CardCache.class.getResource(wkName);
            //System.out.println(" Trying to open " + wkURL.toExternalForm());
            //URI wkURI = new URI(wkURL.toExternalForm());
            String wkRecord = null;
            
            wkReader = new BufferedReader( new FileReader( new File(playerList) ), MY_BUFF_SIZE  );
            
            while (true)
            {
                wkRecord = wkReader.readLine();
                if (null == wkRecord)
                    break;
                StringTokenizer wkLine = new StringTokenizer(wkRecord, ",");
                String wkTeam = wkLine.nextToken();
                
                if (_cardList.containsKey(wkTeam))
                {
                    wkTeamList = _cardList.get(wkTeam);
                }
                else
                {
                    wkTeamList = new TreeMap<String, String>();
                    _cardList.put(wkTeam, wkTeamList);
                }
                StringTokenizer wkPlayerName = new StringTokenizer(wkLine.nextToken(), "#");
                String wkLastName = wkPlayerName.nextToken().trim();
                String wkFirstName = wkPlayerName.nextToken().trim(); //"";
//                while (wkPlayerName.hasMoreTokens())
//                {
//                    if (!wkLastName.equals(""))
//                        wkLastName += " ";
//                    wkLastName = wkLastName + wkPlayerName.nextToken();
//                }
                
                String wkFullName = wkFirstName + " " + wkLastName;
                wkFullName = wkFullName.toLowerCase();
                //wkFullName.replaceAll("\"", "");
                if (_allList.containsKey(wkFullName))
                {
                	System.out.println("......Duplicate name in carded list file: " + wkFullName);
                }
                else
                {
                	//System.out.println("Adding " + wkFullName);
                	_allList.put(wkFullName, "CARDED");
                }
                
                int wkId = 0;
				if (inGetId)
				{
					wkId = playerManager.getPlayerId(wkLastName, wkFirstName);
				}
				//System.out.println("id is: " + wkId);
                
                if (wkId == 0)
                {
                	if (inGetId)
                		System.out.println("Carded Player not in DB: Firstname:" + wkFirstName + "  LastName: " + wkLastName);
                    //System.out.println(wkFirstName + " " + wkLastName + " DRAFTING");
                    wkTeamList.put(wkFirstName+" "+wkLastName, "CARDED");
                }
                else
                {
                    int wkTeamId = teamManager.getPlayerTeam(wkId, transactionManager.getCurrentTransactionsSeason());
                    if (0 == wkTeamId)
                    {
                       // System.out.println(wkFirstName + " " + wkLastName + " DRAFTING");
                        wkTeamList.put(wkId+"", "CARDED");
                    }
                    //else
                      //  System.out.println(wkFirstName + " " + wkLastName + " NOT DRAFTING");
                }
            }
            
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != wkReader)
                    wkReader.close();
            }
            catch (Exception wkException)
            {
                wkException.printStackTrace();
                Email.emailException(wkException);
            }
        }
    }
   
    public void createList()
    {
    	boolean inGetId = Boolean.valueOf(getId).booleanValue();
    	System.out.println("CREATE GETID " + inGetId);
        _cardList = new TreeMap<String, TreeMap<String, String>>();
        _allList = new HashMap<String, String>();
        TreeMap<String, String> wkTeamList = null;
         
            ArrayList<CardedPlayer> cardedPlayers = playerManager.getCardedPlayers();
            
            for (CardedPlayer cardedPlayer : cardedPlayers)
            {
                
                if (_cardList.containsKey(cardedPlayer.getMlbteam()))
                {
                    wkTeamList = _cardList.get(cardedPlayer.getMlbteam());
                }
                else
                {
                    wkTeamList = new TreeMap<String, String>();
                    _cardList.put(cardedPlayer.getMlbteam(), wkTeamList);
                }
                
                String wkFullName = cardedPlayer.getPlayername();
                wkFullName = wkFullName.toLowerCase();
                if (_allList.containsKey(wkFullName))
                {
                	System.out.println("......Duplicate name in carded list file: " + wkFullName);
                }
                else
                {
                	_allList.put(wkFullName, "CARDED");
                }
                
                int wkId = cardedPlayer.getPlayerid();
                
                int wkTeamId = teamManager.getPlayerTeam(wkId, transactionManager.getCurrentTransactionsSeason());
                if (0 == wkTeamId)
                {
                    wkTeamList.put(wkId+"", "CARDED");
                }
            }
            
    }

    
    public HashMap<String, String> getAllList()
    {
    	return _allList;
    }

	public String getPlayerList()
	{
		return playerList;
	}

	public void setPlayerList(String playerList)
	{
		this.playerList = playerList;
	}
   
}
