package com.wahoo.apba.database.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.jdom.Element;
import org.springframework.jdbc.core.JdbcTemplate;

import com.wahoo.apba.database.RotoPlayerRecord;
import com.wahoo.apba.resourcemanagers.RotoPlayerManager;

public class RotoWire
{
    
    final static int MY_BUFF_SIZE = 30000;
	
    private JdbcTemplate jdbcTemplate = null;
    private RotoPlayerManager rotoPlayerManager = null;
     
    public RotoWire()
    {;}
 
    public RotoWire(RotoPlayerManager inManager)
    {
    	this.rotoPlayerManager = inManager;
    }
 
    
    public void setJdbcTemplate(JdbcTemplate inTemplate)
    {
    	this.jdbcTemplate = inTemplate;
    }
    
    public void setRotoPlayerManager(RotoPlayerManager manager)
    {
    	this.rotoPlayerManager = manager;
    }
    
    
    private void processFiles(String inDir)
    {
        File wkDir = new File(inDir);
        try{
        //System.out.println(wkDir.getCanonicalPath() + "\r\n exists " + wkDir.exists() + "\r\nis directory " + wkDir.isDirectory());
        } catch(Exception e) {;}
        File[] wkFiles = wkDir.listFiles();
        
        for (int i = 0; i < wkFiles.length; i++)
        {
        	File wkFile = wkFiles[i];
        	if (!wkFile.isDirectory())
        	{
        		parseReport(wkFile);
        		//System.out.println("File name is " + wkFile.getName());
        		final boolean succeeded = wkFile.renameTo(new File(inDir + "/processed/" + wkFile.getName()));
        		
        		if (!succeeded)
        		{
        		    System.out.println("Error moving processed file to : " + inDir + "/processed/" + wkFile.getName());
        		}
        	}
        }
        
        //rotoPlayerManager.updateMissing();
    }
    
	public static void main(String[] args)
    {	
		RotoWire wkRoto = new RotoWire();
		wkRoto.processFiles(args[0]);
	}
	
	public void parseReport(File inFile)
	{
		
		try
		{
			
			BufferedReader wkReader = new BufferedReader( new FileReader( inFile ), MY_BUFF_SIZE  );
			String wkLine = null;
            
            while (true)
            {
            	//System.out.println("Checking for player");
            	wkLine = readLine(wkReader);
                //wkLine = wkReader.readLine();
                //System.out.println("Line: " + wkLine);
                if (null == wkLine)
                {
                	//System.out.println("returning");
					return;
                }
                if (!checkForNextPlayer(wkLine))
                	continue;
                
                //System.out.println("Found player");
                RotoPlayerRecord wkRecord = new RotoPlayerRecord();
                wkRecord.setJdbcTemplate(jdbcTemplate);
                wkRecord.parsePlayerName(wkLine);
                
                //read the rotowire link
                wkLine = readLine(wkReader);

				wkRecord.addNews(readLine(wkReader));
				wkRecord.addComment(readLine(wkReader));

				System.out.println(wkRecord.toString());
                wkRecord.insert();
                //System.out.println(wkRecord.toString());
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void parseReport(String inReport)
	{
		BufferedReader wkReader = null;
		try
		{
			wkReader = new BufferedReader( new StringReader( inReport ), MY_BUFF_SIZE  );
			String wkLine = null;
            
            while (true) {
				//System.out.println("Checking for player");
				wkLine = readLine(wkReader);
				//System.out.println("Line: " + wkLine);
				if (null == wkLine) {
					//System.out.println("returning");
					//rotoPlayerManager.updateMissing();
					return;
				}
				if (!checkForNextPlayer(wkLine))
					continue;

				//System.out.println("Found player");
				RotoPlayerRecord wkRecord = new RotoPlayerRecord();
				wkRecord.setJdbcTemplate(jdbcTemplate);
				wkRecord.parsePlayerName(wkLine);

				//read rotowire link
				wkLine = readLine(wkReader);
				// read blank line
				wkRecord.addNews(readLine(wkReader));

				wkRecord.addComment(readLine(wkReader));

				System.out.println(wkRecord.toString());
				wkRecord.insert();
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (null != wkReader)
			{
				try
				{
					wkReader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	private boolean checkForNextPlayer(String inLine)
	{
		int wkStart = inLine.indexOf("(");
		if (wkStart > 0)
		{
			try
			{
				String wkPos = inLine.substring(wkStart+1, wkStart+3);
				if (null != getPosition(wkPos))
					return true;
				else
					return false;
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		return false;
	}

	private String readLine(BufferedReader reader)
	{
		String filteredLine = null;
		
		try
		{
			filteredLine = reader.readLine();
			if (null != filteredLine)
			{
				if (!filteredLine.endsWith("="))
				{
					filteredLine += " ";
				}
				filteredLine = filteredLine.replaceAll("=2E", ".");
				//filteredLine = filteredLine.replaceAll("= ", "");
				filteredLine = filteredLine.replaceAll("=\r\n", "");
				filteredLine = filteredLine.replaceAll("=\n\r", "");
				filteredLine = filteredLine.replaceAll("=\r", "");
				filteredLine = filteredLine.replaceAll("=\n", "");
				filteredLine = filteredLine.replaceAll("=", "");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return filteredLine;
	}
	private String getPosition(String inLine)
	{
		if (inLine.equals("OF") ||
			inLine.equals("1B") ||
			inLine.equals("2B") ||
			inLine.equals("3B") ||
			inLine.equals("3B") ||
			inLine.equals("SS") ||
			inLine.equals("DH"))
			return inLine;
		else if (inLine.equals("P)"))
		    return ("P");
		else if (inLine.equals("C)"))
			return ("C");
		
		return null;
	}
	
	public Element getLastUpdate()
	{
		Element wkRoto = new Element("Rotowire");
		wkRoto.addContent("LastUpdate").setText("Today");
		return wkRoto;
	}
}
