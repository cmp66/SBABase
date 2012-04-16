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
import com.wahoo.apba.resourcemanagers.StatsManager;
import com.wahoo.apba.resourcemanagers.TeamManager;
import com.wahoo.apba.resourcemanagers.TransactionManager;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class ManagementGenerator implements IPageGenerator
{
	private TransactionManager transactionManager = null;
    private TeamManager teamManager = null;
    private StatsManager  statsManager = null;
    
    /** Creates a new instance of MainPageGenerator */
    public ManagementGenerator ()
    {
    }

    public void init ()
    {
        //String wkName = "/WEB_INF/xslt/management.xsl";
        //URL wkURL = getServletContext().getResource(wkName);
    }
    
    public void setTransactionManager (TransactionManager inMgr)
    {
    	this.transactionManager = inMgr;
    }
    
    public void setTeamManager (TeamManager inMgr)
    {
    	this.teamManager = inMgr;
    }

    public void setStatsManager (StatsManager inMgr)
    {
    	this.statsManager = inMgr;
    }
    
    
    public void generatePage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
    	String wkMode = inRequest.getParameter("mode");
    	
    	if (null != wkMode && wkMode.equals("Reset"))
    	{
    		transactionManager.resetLists();
    	}
    	
        try
        {
            Document wkPageData = createPageDocument();
            XSLTRenderHelper.render(inServlet, wkPageData, "management.xsl", inResponse);
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

    public void generatePostPage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
            Document wkPageData = createPageDocument();
            XSLTRenderHelper.render(inServlet, wkPageData, "management.xsl", inResponse);
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
    
    private Document createPageDocument()
    {
        Element wkTeams = teamManager.getTeams();
        Element wkYears = statsManager.getYears();
        Element wkTrans = transactionManager.getLastestTransactions(10);
        Element wkRoot = new Element("Selections");
        wkRoot.addContent(wkTeams);
        wkRoot.addContent(wkYears);
        wkRoot.addContent(wkTrans);
        
        Document wkPage = new Document(wkRoot);
        return wkPage;
    }
    
}
