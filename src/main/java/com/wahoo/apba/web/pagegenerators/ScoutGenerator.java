/*
 * RosterGenerator.java
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
import com.wahoo.apba.resourcemanagers.TeamManager;
import com.wahoo.apba.resourcemanagers.TransactionManager;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class ScoutGenerator implements IPageGenerator
{
	private TeamManager teamManager = null;
	private TransactionManager transactionManager = null;
    
    /** Creates a new instance of MainPageGenerator */
    public ScoutGenerator ()
    {
    }

    public void init ()
    {
        //String wkName = "/WEB_INF/xslt/scout.xsl";
        //URL wkURL = getServletContext().getResource(wkName);
    }
    
    public void setTeamManager (TeamManager inMgr)
    {
    	this.teamManager = inMgr;
    }
    
    public void setTransactionManager (TransactionManager inMgr)
    {
    	this.transactionManager = inMgr;
    }
    
    public void generatePage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
            int wkTeam = Integer.parseInt(inRequest.getParameter("team"));
            Document wkPageData = createPageDocument(wkTeam);
            XSLTRenderHelper.render(inServlet, wkPageData, "scout.xsl", inResponse);
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
            int wkTeam = Integer.parseInt(inRequest.getParameter("team"));
            Document wkPageData = createPageDocument(wkTeam);
            XSLTRenderHelper.render(inServlet, wkPageData, "scout.xsl", inResponse);
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
    private Document createPageDocument(int inTeam)
    {
        Element wkRoster = teamManager.getScoutRoster(inTeam, transactionManager.getCurrentTransactionsSeason());
        
        Document wkPage = new Document(wkRoster);
                    //XMLOutputter wkOut = new XMLOutputter();
            //wkOut.setNewlines(true);
            //System.out.println(wkOut.outputString(wkPage));
        return wkPage;
    }
    
}
