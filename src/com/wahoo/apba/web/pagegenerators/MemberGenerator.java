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
public class MemberGenerator implements IPageGenerator
{
	
    private TeamManager teamManager = null;
    
    /** Creates a new instance of MainPageGenerator */
    public MemberGenerator ()
    {
    }

    public void init ()
    {
        //String wkName = "/WEB_INF/xslt/members.xsl";
        //URL wkURL = getServletContext().getResource(wkName);
    }
    
    public void setTeamManager (TeamManager inMgr)
    {
    	this.teamManager = inMgr;
    }
    
    public void generatePage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
        	String wkSeason = inRequest.getParameter("season");
            Document wkPageData = createPageDocument(wkSeason);
            XSLTRenderHelper.render(inServlet, wkPageData, "members.xsl", inResponse);
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
        	String wkSeason = inRequest.getParameter("season");
            Document wkPageData = createPageDocument(wkSeason);
            XSLTRenderHelper.render(inServlet, wkPageData, "members.xsl", inResponse);
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
    	Element wkRoot = null;
    	
    	if (inSeason != null)
    	{
    		wkRoot = teamManager.getMasterTeamList(Integer.parseInt(inSeason));
    	}
    	else
    	{
    		wkRoot = teamManager.getMasterTeamList();
    	}
        
        Document wkPage = new Document(wkRoot);
        return wkPage;
    }
    
}
