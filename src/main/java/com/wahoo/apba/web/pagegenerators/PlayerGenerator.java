/*
 * TeamOverviewGenerator.java
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
import com.wahoo.apba.resourcemanagers.PlayerManager;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class PlayerGenerator implements IPageGenerator
{
	PlayerManager playerManager  = null;
    
    /** Creates a new instance of MainPageGenerator */
    public PlayerGenerator ()
    {
    }

    public void init ()
    {
        //String wkName = "/WEB_INF/xslt/Player.xsl";
        //URL wkURL = getServletContext().getResource(wkName);
    }
    
    public void setPlayerManager(PlayerManager inMgr)
    {
    	this.playerManager = inMgr;
    }
    
    public void generatePage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
        	int wkId = Integer.parseInt(inRequest.getParameter("id"));
            Document wkPageData = createPageDocument(wkId);
            XSLTRenderHelper.render(inServlet, wkPageData, "Player.xsl", inResponse);
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
        	int wkId = Integer.parseInt(inRequest.getParameter("id"));
            Document wkPageData = createPageDocument(wkId);
            XSLTRenderHelper.render(inServlet, wkPageData, "Player.xsl", inResponse);
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
    
    private Document createPageDocument(int inId)
    {
        Element wkPlayer = playerManager.getPlayerInfo(inId);
        
        Document wkPage = new Document(wkPlayer);
        return wkPage;
    }
    
}
