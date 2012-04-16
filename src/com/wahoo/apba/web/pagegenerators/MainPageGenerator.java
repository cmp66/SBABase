/*
 * MainPageGenerator.java
 *
 * Created on February 9, 2003, 1:38 PM
 */

package com.wahoo.apba.web.pagegenerators;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import com.oreilly.javaxslt.util.XSLTRenderHelper;
import com.wahoo.apba.resourcemanagers.ScheduleManager;
import com.wahoo.apba.resourcemanagers.TeamManager;
import com.wahoo.apba.resourcemanagers.TransactionManager;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class MainPageGenerator implements IPageGenerator
{
	private TransactionManager transactionManager = null;
    private TeamManager teamManager = null;
    private ScheduleManager  scheduleManager = null;
    
    private int _numDisplayTrades = 5;
    private int _numDisplaySeries = 5;
    
    /** Creates a new instance of MainPageGenerator */
    public MainPageGenerator ()
    {
    }

    public void init ()
    {
        //String wkName = "/WEB_INF/xslt/standings.xsl";
        //URL wkURL = getServletContext().getResource(wkName);
    }
    
    public int getNumDisplaySeries()
	{
		return _numDisplaySeries;
	}

	public void setNumDisplaySeries(int displaySeries)
	{
		_numDisplaySeries = displaySeries;
	}

	public int getNumDisplayTrades()
	{
		return _numDisplayTrades;
	}

	public void setNumDisplayTrades(int displayTrades)
	{
		_numDisplayTrades = displayTrades;
	}

	public void setTransactionManager (TransactionManager inMgr)
    {
    	this.transactionManager = inMgr;
    }
    
    public void setTeamManager (TeamManager inMgr)
    {
    	this.teamManager = inMgr;
    }

    public void setScheduleManager (ScheduleManager inMgr)
    {
    	this.scheduleManager = inMgr;
    }
    public void generatePage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
        	String wkSeason = inRequest.getParameter("season");
            Document wkPageData = createPageDocument(wkSeason);
            XSLTRenderHelper.render(inServlet, wkPageData, "main.xsl", inResponse);
        }
        catch (ServletException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
         catch (IOException wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }
    
    private Document createPageDocument(String inSeason)
    {
    	int season = transactionManager.getCurrentStandingsSeason();
    	
    	if (inSeason != null)
    	{
    		season = Integer.parseInt(inSeason);
    	}
    	
        Element wkRoot = new Element("MainPage");
        Element wkStandings = teamManager.getStandings(season);
        Element wkLatestSeries = scheduleManager.getLatestSeries(_numDisplaySeries);
        Element wkTransactions = transactionManager.getLastestTrades(_numDisplayTrades);
        
        Document wkPage = new Document(wkRoot);
        wkRoot.addContent(wkStandings);
        wkRoot.addContent(wkLatestSeries);
        wkRoot.addContent(wkTransactions);
        
        return wkPage;
    }
    
    public void generatePostPage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
    }
    
}
