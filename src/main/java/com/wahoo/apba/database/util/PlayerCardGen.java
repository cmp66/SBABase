package com.wahoo.apba.database.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import com.wahoo.apba.database.Player;
import com.wahoo.apba.database.PlayerCard;
import com.wahoo.apba.resourcemanagers.CardCache;
import com.wahoo.apba.resourcemanagers.CardedList;
import com.wahoo.util.WebProperties;

public class PlayerCardGen
{
    CardCache                     _cards          = null;
    CardedList                    _cardedList     = null;
    HashMap<String, List<Player>> lastNameMap     = null;
    HashMap<String, Player>       nameMap         = null;
    HashMap<String, String>       cardedListMap   = null;
    HashMap<String, Player>       linkMap         = null;
    HashMap<String, String>       rotoMissingList = null;

    public PlayerCardGen()
    {
        lastNameMap = new HashMap<String, List<Player>>();
        nameMap = new HashMap<String, Player>();
        linkMap = new HashMap<String, Player>();
        cardedListMap = null;

        // Properties wkProps = wkXmlProperties.getProperties("WebApp");
        Properties wkProps = new Properties();
        WebProperties.setWebProperties(wkProps);
        WebProperties.getWebProperties().put("Cardfile", "D:/Projects/eclipse/workspace/NABL/WebContent/2009CardsMaster.csv");
        WebProperties.getWebProperties().put("CardedList",
                "D:/Projects/eclipse/workspace/NABL/WebContent/CardedList2010.csv");
        // WebProperties.getWebProperties().put("Cardfile",
        // "/home/cphillips/dev/tomcat/jakarta-tomcat-5.5.7/webapps/NABL/2005cards.csv");
        // WebProperties.getWebProperties().put("CardedList",
        // "/home/cphillips/dev/tomcat/jakarta-tomcat-5.5.7/webapps/NABL/CardedList.csv");
    }

    private void readPlayerData(String inDBHost, String inDBName)
    {
        Connection wkReadConn = null;

        try
        {
            String wkURL = "jdbc:postgresql://192.168.1.6/NABL";
            System.out.println(wkURL);
            DBConnection wkReadSource = new DBConnection("org.postgresql.Driver", wkURL, "sbaapba", "apba");
            wkReadConn = wkReadSource.getConnection();

            Statement wkReadStatement = wkReadConn.createStatement();
            ResultSet wkResultSet = null;

            // System.out.println("STARTING PLAYER CARD GEN");
            // System.out.println("...Reading Players from DB");

            wkResultSet = wkReadStatement.executeQuery("select * from players");
            while (wkResultSet.next())
            {
                Player wkObject = new Player(wkResultSet);
                String wkName = wkObject.getFirstname() + " " + wkObject.getLastname();
                wkName = wkName.toLowerCase();
                if (nameMap.containsKey(wkName))
                {
                    System.out.println("dupliate name for " + wkName);
                }
                else
                {
                    nameMap.put(wkName, wkObject);
                }
                if (lastNameMap.containsKey(wkObject.getLastname().toLowerCase()))
                {
                    List<Player> nameList = lastNameMap.get(wkObject.getLastname().toLowerCase());
                    nameList.add(wkObject);
                }
                else
                {
                    ArrayList<Player> nameList = new ArrayList<Player>();
                    lastNameMap.put(wkObject.getLastname().toLowerCase(), nameList);
                    nameList.add(wkObject);
                }

            }

            wkResultSet.close();
            wkReadStatement.close();

            // Get the missing name from the rotonews list

            rotoMissingList = new HashMap<String, String>();
            wkReadStatement = wkReadConn.createStatement();
            wkResultSet = wkReadStatement.executeQuery("Select * from rotowiremissing");

            while (wkResultSet.next())
            {
                String name = wkResultSet.getString("playername");
                rotoMissingList.put(name, "Missing");
            }

            wkResultSet.close();
            wkReadStatement.close();
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

    private void getBBLinks(String inDBHost, String inDBName)
    {
        Connection wkReadConn = null;

        try
        {
            String wkURL = "jdbc:postgresql://192.168.1.6/NABL";
            System.out.println(wkURL);
            DBConnection wkReadSource = new DBConnection("org.postgresql.Driver", wkURL, "sbaapba", "apba");
            wkReadConn = wkReadSource.getConnection();

            Statement wkReadStatement = wkReadConn.createStatement();
            ResultSet wkResultSet = null;

            // System.out.println("STARTING PLAYER CARD GEN");
            // System.out.println("...Reading Players from DB");

            wkResultSet = wkReadStatement.executeQuery("select * from players");
            while (wkResultSet.next())
            {
                Player wkObject = new Player(wkResultSet);
                String wkName = wkObject.getFirstname() + " " + wkObject.getLastname();
                wkName = wkName.toLowerCase();
                if (linkMap.containsKey(wkName))
                {
                    System.out.println("dupliate name for " + wkName);
                }
                else
                {
                    linkMap.put(wkName, wkObject);
                }

            }

            wkResultSet.close();
            wkReadStatement.close();

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

    private void getCards()
    {
        // System.out.println("...Getting Current Player Cards");
        _cards = new CardCache();
        _cards.setCardfile((String) WebProperties.getWebProperties().get("Cardfile"));
        _cards.createMap();
    }

    private void correctPlayerCardName()
    {
        Iterator<PlayerCard> wkIter = _cards.getAllCardsIterator();
        HashMap<String, PlayerCard> wkCardMap = new HashMap<String, PlayerCard>(2000);

        while (wkIter.hasNext())
        {
            PlayerCard wkCard = (PlayerCard) wkIter.next();
            String wkName = wkCard.getFirstname() + " " + wkCard.getLastname();
            // String wkLastname = wkCard.getLastname().substring(0,1) +
            // wkCard.getLastname().substring(1).toLowerCase();
            wkName = wkName.toLowerCase();
            wkCardMap.put(wkName, wkCard);
        }

        Iterator<String> wkNameIter = cardedListMap.keySet().iterator();
        while (wkNameIter.hasNext())
        {
            String wkName = wkNameIter.next();
            if (!wkCardMap.containsKey(wkName))
            {
                System.out.println("Carded player does not have card: " + wkName);
            }
        }
    }

    private void verifyCardedList(boolean checkForRoto)
    {
        Iterator<String> wkIter = null;

        wkIter = cardedListMap.keySet().iterator();
        System.out.println("checking on " + cardedListMap.size() + " players");
        while (wkIter.hasNext())
        {
            String wkName = (String) wkIter.next();
            if (!nameMap.containsKey(wkName))
            {
                StringTokenizer tokens = new StringTokenizer(wkName);
                String lastname = null;
                String firstname = null;
                String realLastName = null;

                while (tokens.hasMoreTokens())
                {
                    String name = tokens.nextToken();
                    if (null == firstname)
                    {
                        firstname = name.substring(0, 1).toUpperCase() + name.substring(1);
                    }
                    else
                    {
                        if (null == lastname)
                        {
                            lastname = name;
                            realLastName = name.substring(0, 1).toUpperCase() + name.substring(1);
                        }
                        else
                        {
                            lastname += " " + name;
                            realLastName += " " + name.substring(0, 1).toUpperCase() + name.substring(1);
                        }
                    }
                }

                StringBuffer sql = new StringBuffer(1024);

                sql
                        .append("INSERT into players (firstname, lastname, displayname, startyear, endyear, bbreflink, position) VALUES ('");
                sql.append(firstname).append("','").append(realLastName).append("','").append(
                        firstname + " " + realLastName);
                sql.append("',2002,2002,'' , 'X');");
                System.out.println(sql.toString());
                System.out.println("Carded Player name not in DB: " + wkName);
                if (lastNameMap.containsKey(lastname))
                {
                    List<Player> nameList = lastNameMap.get(lastname);
                    Iterator<Player> iter = nameList.iterator();
                    while (iter.hasNext())
                    {
                        Player player = iter.next();
                        System.out.println("......Possible Match: " + player.getFirstname() + " "
                                + player.getLastname());
                    }
                }
            }
            else
            {
             // See if I can get a BBRef link from other DB
                String nameForLookup = getLookupname(wkName);
                Player linkplayer = (Player) linkMap.get(nameForLookup);
                StringBuffer sql = new StringBuffer(1024);

                if (linkplayer != null)
                {
                    String bblink = linkplayer.getBbreflink();

                    if (bblink == null || bblink.equals("") || bblink.equals(" "))
                    {
                        System.out.println("No BB ref link for " + linkplayer.getDisplayname());
                        
                        String requestURI = "http://www.baseball-reference.com/player_search.cgi?search=" + 
                        getLookupFirstname(wkName) + "+" + getLookupLastname(wkName) + "&Search+for+Player.x=0&Search+for+Player.y=0";
                        
                        HttpClient client = new HttpClient();
                        GetMethod method = new GetMethod(requestURI);
                        
                        try
                        {
                            client.executeMethod(method);
                            sql.append("UPDATE players set bbreflink = '" + method.getURI() + "' where id = " + linkplayer.getId() + ";");
                            System.out.println(sql.toString());
                        }
                        catch(HttpException e)
                        {
                            e.printStackTrace();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        
                        
                    }
                }
                else
                {
                    System.out.println("No player object for : " + nameForLookup);
                }
            }
        }

        if (checkForRoto)
        {

            System.out.println("Rotowire missing");
            wkIter = rotoMissingList.keySet().iterator();
            while (wkIter.hasNext())
            {
                String missingname = (String) wkIter.next();
                System.out.println("rotowire missing: " + missingname);
                StringTokenizer tokens = new StringTokenizer(missingname);

                String lastname = null;
                String firstname = null;
                String realLastName = null;

                while (tokens.hasMoreTokens())
                {
                    String name = tokens.nextToken();
                    if (null == firstname)
                    {
                        firstname = name.substring(0, 1).toUpperCase() + name.substring(1);
                    }
                    else
                    {
                        if (null == lastname)
                        {
                            lastname = name;
                            realLastName = name.substring(0, 1).toUpperCase() + name.substring(1);
                        }
                        else
                        {
                            lastname += " " + name;
                            realLastName += " " + name.substring(0, 1).toUpperCase() + name.substring(1);
                        }
                    }
                }
                StringBuffer sql = new StringBuffer(1024);
                sql
                        .append("INSERT into players (firstname, lastname, displayname, startyear, endyear, bbreflink, position) VALUES ('");
                sql.append(firstname).append("','").append(realLastName).append("','").append(missingname);
                sql.append("',2002,2002,' ' , 'X');");
                System.out.println(sql.toString());
                checkForMatchingLastName(realLastName);
            }
        }
    }
    
    private String getLookupLastname(String wkName)
    {
        StringTokenizer tokens = new StringTokenizer(wkName);
        String lastname = null;
        String firstname = null;
        String realLastName = null;

        while (tokens.hasMoreTokens())
        {
            String name = tokens.nextToken();
            if (null == firstname)
            {
                firstname = name.substring(0, 1).toUpperCase() + name.substring(1);
            }
            else
            {
                if (null == lastname)
                {
                    lastname = name;
                    realLastName = name.substring(0, 1).toUpperCase() + name.substring(1);
                }
                else
                {
                    lastname += " " + name;
                    realLastName += " " + name.substring(0, 1).toUpperCase() + name.substring(1);
                }
            }
        }
        
        return realLastName;

    }
    
    
    private String getLookupname(String wkName)
    {
        StringTokenizer tokens = new StringTokenizer(wkName);
        String lastname = null;
        String firstname = null;
        String realLastName = null;

        while (tokens.hasMoreTokens())
        {
            String name = tokens.nextToken();
            if (null == firstname)
            {
                firstname = name.substring(0, 1).toUpperCase() + name.substring(1);
            }
            else
            {
                if (null == lastname)
                {
                    lastname = name;
                    realLastName = name.substring(0, 1).toUpperCase() + name.substring(1);
                }
                else
                {
                    lastname += " " + name;
                    realLastName += " " + name.substring(0, 1).toUpperCase() + name.substring(1);
                }
            }
        }
        
        String nameForLookup = firstname.toLowerCase() + " " + realLastName.toLowerCase();
        
        return nameForLookup;

    }
    
    private String getLookupFirstname(String wkName)
    {
        StringTokenizer tokens = new StringTokenizer(wkName);
        String firstname = null;

        while (tokens.hasMoreTokens())
        {
            String name = tokens.nextToken();
            if (null == firstname)
            {
                firstname = name.substring(0, 1).toUpperCase() + name.substring(1);
            }
        }
        
        return firstname;

    }

    private void checkForMatchingLastName(String lastName)
    {
        if (this.lastNameMap.containsKey(lastName.toLowerCase()))
        {
            List<Player> players = lastNameMap.get(lastName.toLowerCase());
            Iterator<Player> playerIter = players.iterator();
            while (playerIter.hasNext())
            {
                Player player = playerIter.next();
                System.out.println("Possible match with display name" + player.getDisplayname());
            }
        }
    }

    private void correctIds()
    {

        Iterator<PlayerCard> wkIter = _cards.getAllCardsIterator();
        // System.out.println("...correcting IDs");
        int index = 3511;
        while (wkIter.hasNext())
        {
            PlayerCard wkCard = (PlayerCard) wkIter.next();
            String wkName = wkCard.getFirstname() + " " + wkCard.getLastname();
            String wkLastname = wkCard.getLastname().substring(0, 1) + wkCard.getLastname().substring(1).toLowerCase();
            wkName = wkName.toLowerCase();
            // System.out.println("Looking for " + wkName);
            if (cardedListMap.containsKey(wkName))
            {
                if (nameMap.containsKey(wkName))
                {
                    Player wkPlayer = (Player) nameMap.get(wkName);
                    wkCard.setId(wkPlayer.getId() + "");
                }
                else
                {
                    index++;
                    System.out
                            .println("INSERT into players (id, firstname, lastname, displayname, startyear, endyear, bbreflink, position) VALUES ("
                                    + index
                                    + ", '"
                                    + wkCard.getFirstname()
                                    + "', '"
                                    + wkLastname
                                    + "', '"
                                    + wkCard.getFirstname()
                                    + " "
                                    + wkLastname
                                    + "', 2004, 2004, 'http://www.baseball-reference.com/b/bentzch01.shtml', 'P');");
                }
                System.out.println(wkCard.toString());
            }
            else
            {
                System.out.println("Player not carded: " + wkName);
            }
        }
    }

    private void getCardedList()
    {
        // System.out.println("...Getting Carded List");
        _cardedList = new CardedList();
        _cardedList.setPlayerList((String) WebProperties.getWebProperties().get("CardedList"));
        _cardedList.setGetId("false");
        _cardedList.createList();
        cardedListMap = _cardedList.getAllList();
        // System.out.println("Carded list has " + cardedListMap.size());
    }

    public static void main(String[] args)
    {
        PlayerCardGen wkGen = new PlayerCardGen();
        System.out.println("STARTING PLAYER CARD GEN");
        wkGen.readPlayerData(args[0], args[1]);
        wkGen.getBBLinks(args[0], args[1]);
        wkGen.getCards();
        wkGen.getCardedList();
        System.out.println("...Verifying cards have correct names");
        //wkGen.correctPlayerCardName();
        //wkGen.correctIds();
        wkGen.verifyCardedList(false);

    }
}
