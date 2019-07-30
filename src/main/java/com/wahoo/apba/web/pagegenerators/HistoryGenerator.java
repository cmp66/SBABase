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
import com.wahoo.apba.resourcemanagers.TeamManager;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class HistoryGenerator implements IPageGenerator
{
	
    private TeamManager teamManager = null;
    
    /** Creates a new instance of MainPageGenerator */
    public HistoryGenerator ()
    {
    }

    public void init ()
    {
    }
    
    public void setTeamManager (TeamManager inMgr)
    {
    	this.teamManager = inMgr;
    }
    
    public void generatePage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
            Document wkPageData = createPageDocument();
            XSLTRenderHelper.render(inServlet, wkPageData, "history.xsl", inResponse);
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
        Element wkStandings = teamManager.getStandings();
        
        Document wkPage = new Document(wkStandings);
        
        return wkPage;
    }
    
    public void generatePostPage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
    }
    
}
