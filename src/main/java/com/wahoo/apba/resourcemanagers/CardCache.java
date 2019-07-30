/*
 * PlayerCache.java
 *
 * Created on February 1, 2003, 9:17 PM
 */

package com.wahoo.apba.resourcemanagers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import com.wahoo.apba.database.PlayerCard;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class CardCache
{
 
    private Hashtable<Integer, PlayerCard> _cardMap = null;
    private ArrayList<PlayerCard> _cardList = null;
    
    private String    cardfile = null;
    
	private Object LOCK = new Object();
	   
    public CardCache ()
    { 

    }

    
    public PlayerCard get(int inId)
    {
        synchronized (LOCK)
        {
            return (PlayerCard)_cardMap.get(Integer.valueOf(inId));
        }
    }


    public void put(PlayerCard inPlayerRecord)
    {
        synchronized (LOCK)
        {
            _cardMap.put( Integer.valueOf(inPlayerRecord.getId()), inPlayerRecord  );
        }
    }  


    public void remove(int inId)
    {
        synchronized (LOCK)
        {
            Integer wkId = Integer.valueOf(inId);
            _cardMap.remove(wkId);
        }
    } 

    public void dumpMap()
    {
        synchronized (LOCK)
        {
    	    Enumeration<Integer> e = _cardMap.keys();
    	    while (e.hasMoreElements())
    	    {
    		    Integer id = e.nextElement();
    		    System.out.println("        cardmap key: " + id);
    	    }
    	}
    }
        
        
    public void resetMap()
    {
        synchronized (LOCK)
        {
            _cardMap.clear();
            _cardMap = null;
            createMap();
        }
    }


    public void createMap()
    {
        PlayerCard wkCard = null;
        final int MY_BUFF_SIZE = 30000;
        BufferedReader wkReader = null;
        _cardMap = new Hashtable<Integer, PlayerCard>(600);
        _cardList = new ArrayList<PlayerCard>();
         
        try
        {
            //String wkName = (String) WebProperties.getWebProperties().get("Cardfile");//"../server/default/deploy/sba.war/2002cards.csv";
            System.out.println("Opening file " + cardfile);
            //URL wkURL = com.wahoo.apba.resourcemanagers.CardCache.class.getResource(wkName);
            //System.out.println(" Trying to open " + wkURL.toExternalForm());
            //URI wkURI = new URI(wkURL.toExternalForm());
            String wkRecord = null;
            
            wkReader = new BufferedReader( new FileReader( new File(cardfile) ), MY_BUFF_SIZE  );
            
            while (true)
            {
                wkRecord = wkReader.readLine();
                if (null == wkRecord)
                    break;
                //System.out.println("Loading card: " + wkRecord);
                wkCard = new PlayerCard(wkRecord);
                
                if (null != wkCard.getId() && !wkCard.getId().equals(" "))
                {
                    int wkId = Integer.parseInt(wkCard.getId());
                    if (wkId > 0)
                    {
                        //System.out.println("added " + wkCard.getFirstname() + " " + wkCard.getLastname());
                        _cardMap.put(Integer.valueOf(wkId), wkCard);
                    }
                }
                _cardList.add(wkCard);
            }
            
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
    
    public Iterator<PlayerCard> iterator()
    {
    	return _cardMap.values().iterator();
    }
    
    public Iterator<PlayerCard> getAllCardsIterator()
    {
    	return _cardList.iterator();
    }


	public String getCardfile()
	{
		return cardfile;
	}


	public void setCardfile(String cardfile)
	{
		this.cardfile = cardfile;
	}
   
}
