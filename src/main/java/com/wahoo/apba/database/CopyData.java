package com.wahoo.apba.database;

import java.io.*;
import java.net.*;
import com.wahoo.util.PatternFinder;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;


public class CopyData
{
    static final String URL_SEARCH_STRING = "[a-z][a-z][a-z][a-z][a-z][a-z][a-z].[0-9].shtml";
    static final String URL_SEARCH_STRING1 = "HREF=*.shtml";
    static final String DATE_SEARCH_STRING = "[1-2][0,9][0,6-9][0-9]-[1-2][0,9][0,8-9][0-9]";
    
    static final String PLAYER_SEARCH_STRING = "FONT SIZE=[+]2";
    static final String PLAYER_NAME_PRE = "<FONT SIZE=+2><B>";
    
    private String _inFilename = null;
    private String _outFilename = null;
    
    Document _players = null;
    
    public CopyData()
    {}
    
    
    public void init(String inInFileName, String inOutFileName)
    {
        _inFilename = inInFileName;
        _outFilename = inOutFileName;
        _players = createPlayerDocument();
    }
    
    private void writeXML()
    {
        if (null != _outFilename)
        {
            try
            {
                BufferedWriter wkWriter = new BufferedWriter( new FileWriter(_outFilename, false));
            
                XMLOutputter wkOutputter = new XMLOutputter("   ", true);
                wkOutputter.output(_players, wkWriter);
            
                //wkWriter.write(wkXMLString, 0, wkXMLString.length());
            
                wkWriter.flush();
                wkWriter.close();
            }
            catch (Exception wkEx)
            {
                wkEx.printStackTrace();
            }
        }
    }
        
    public void generatePlayerURLs()
    {
        final int           MY_BUFF_SIZE = 30000;
        PatternFinder wkURLFinder = new PatternFinder(URL_SEARCH_STRING);
        PatternFinder wkDateFinder = new PatternFinder(DATE_SEARCH_STRING);
        
        
        if (null != _inFilename && null != _outFilename)
        {
            String wkCurrentLine = null;
            
            try
            {
                BufferedReader wkReader = new BufferedReader( new FileReader( _inFilename ), MY_BUFF_SIZE  );
                
                while (null != (wkCurrentLine = wkReader.readLine()))
                {
                    wkURLFinder.setInputString(wkCurrentLine);
                    if (wkURLFinder.hasNext())
                    {
                        String wkPlayerExtension = wkURLFinder.next();
                    
                        wkDateFinder.setInputString(wkCurrentLine);
                        if (wkDateFinder.hasNext())
                        {
                            StringTokenizer wkCareerDates = new StringTokenizer(wkDateFinder.next(), "-");
                            try
                            {
                                int wkStartDate = Integer.parseInt(wkCareerDates.nextToken());
                                int wkStopDate = Integer.parseInt(wkCareerDates.nextToken());
                                if (wkStopDate > 1987)
                                {
                                    addPlayer(wkPlayerExtension, wkStartDate, wkStopDate);
                                }
                            }
                            catch (Exception wkException)
                            {
                                wkException.printStackTrace();
                            }
                        }
                    }
                }
                
                wkReader.close();
            }
            catch (Exception wkException)
            {
                wkException.printStackTrace();
            }
        }
        
        return;
    }

    public String generatePlayerXML(String inAddress, String inHTML, int inStartYear, int inStopYear)
    {
        //final int           MY_BUFF_SIZE = 30000;
        PatternFinder wkPlayerFinder = new PatternFinder(PLAYER_SEARCH_STRING);
        //PatternFinder wkDateFinder = new PatternFinder(DATE_SEARCH_STRING);
        
        
        if (null != _outFilename)
        {
            String wkCurrentLine = null;
            
            try
            {
                BufferedReader wkReader = new BufferedReader( new StringReader( inHTML ));
                
                while (null != (wkCurrentLine = wkReader.readLine()))
                {
                    wkPlayerFinder.setInputString(wkCurrentLine);
                    if (wkPlayerFinder.hasNext())
                    {
                        //System.out.println(wkCurrentLine);
                        int wkStartIndex = wkCurrentLine.indexOf(PLAYER_NAME_PRE) + PLAYER_NAME_PRE.length();
                        int wkEndIndex = wkCurrentLine.indexOf("</B></FONT>");
                        //System.out.println(wkCurrentLine);
                        String wkPlayerName = wkCurrentLine.substring(wkStartIndex, wkEndIndex);
                        String wkFirstName = wkPlayerName.substring(0, wkPlayerName.indexOf(" "));
                        String wkLastName = wkPlayerName.substring(wkPlayerName.indexOf(" ")+1, wkPlayerName.length());
                        //System.out.println("#" + wkFirstName + "#" + wkLastName + "#");
                        Element wkPlayerElement = new Element("Player");
                        
                        Element wkElement = new Element("full-name");
                        wkElement.setText(wkPlayerName);
                        wkPlayerElement.addContent(wkElement);
                        
                        wkElement = new Element("first-name");
                        wkElement.setText(wkFirstName);
                        wkPlayerElement.addContent(wkElement);
                        
                        wkElement = new Element("last-name");
                        wkElement.setText(wkLastName);
                        wkPlayerElement.addContent(wkElement);
                        
                        wkElement = new Element("first-year");
                        wkElement.setText(inStartYear +"");
                        wkPlayerElement.addContent(wkElement);
                        
                        wkElement = new Element("last-year");
                        wkElement.setText(inStopYear + "");
                        wkPlayerElement.addContent(wkElement);

                        wkElement = new Element("bbref-address");
                        wkElement.setText(inAddress);
                        wkPlayerElement.addContent(wkElement);                        
                        
                        _players.getRootElement().addContent(wkPlayerElement);
                    }
                }
                
                wkReader.close();
            }
            catch (Exception wkException)
            {
                wkException.printStackTrace();
            }
        }
        
        return "";
    }
        
    private void addPlayer(String inExtension, int inStartYear, int inStopYear)
    {
        String wkAddress = "http://www.baseball-reference.com/" + inExtension.charAt(0) + "/" + inExtension;

        try
        {     
            URL wkURL = new URL( wkAddress );
            HttpURLConnection wkConn = (HttpURLConnection) wkURL.openConnection();
            wkConn.setInstanceFollowRedirects( false );
            wkConn.setRequestMethod( "GET" );
            wkConn.setRequestProperty( "Host", wkURL.getHost() );
            wkConn.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0" );
            wkConn.setRequestProperty( "Accept" , "*/*" );
            
            //System.out.println( "ACTUAL URL: " + wkConn.getURL().toString() );
            wkConn.connect();
            //Thread.sleep( m_waitMillis );

            Object wkContent = wkConn.getContent();            
            BufferedReader wkReader = new BufferedReader( new InputStreamReader( (FilterInputStream) wkContent ) );
            String wkString = null;
            StringBuffer wkBuff = new StringBuffer();
            while ( null != ( wkString = wkReader.readLine() ) )
            {
                wkBuff.append( wkString );
            }    
            
            wkReader.close();
            //inWriter.write(wkAddress, 0, wkAddress.length());
            //inWriter.newLine();
            //inWriter.flush();
            //System.out.println(wkBuff.toString());
            generatePlayerXML(wkAddress, wkBuff.toString(), inStartYear, inStopYear);
        }
        catch (IOException wkException)
        {
            wkException.printStackTrace();
        }
    }
    
    private Document createPlayerDocument()
    {
        Element wkRoot = new Element("Players");

        Document wkDoc = new Document(wkRoot);
       
        return wkDoc;
    }        
     
    public static void main( String[] args )
    {
        
    }
} 
            
            