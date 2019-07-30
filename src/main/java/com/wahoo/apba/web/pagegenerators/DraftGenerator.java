/*
 * RosterGenerator.java
 *
 * Created on February 9, 2003, 1:38 PM
 */

package com.wahoo.apba.web.pagegenerators;

import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import com.oreilly.javaxslt.util.XSLTRenderHelper;
import com.wahoo.apba.resourcemanagers.CardedList;
import com.wahoo.apba.resourcemanagers.TeamManager;
import com.wahoo.apba.resourcemanagers.TransactionManager;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class DraftGenerator implements IPageGenerator
{
	private TransactionManager transactionManager = null;
    private TeamManager teamManager = null;
    private CardedList cardedList = null;
    
    /** Creates a new instance of MainPageGenerator */
    public DraftGenerator ()
    {
    }

    public void init ()
    {
    }
    
    public void setTeamManager (TeamManager inMgr)
    {
    	this.teamManager = inMgr;
    }
    
    public void setTransactionManager (TransactionManager inMgr)
    {
    	this.transactionManager = inMgr;
    }
    
    public void setCardedList (CardedList inList)
    {
    	this.cardedList = inList;
    }
    
    public void generatePage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
            Document wkPageData = null;
            String wkYear = inRequest.getParameter("year");
            String wkMode = inRequest.getParameter("mode");
            String wkAdmin = inRequest.getParameter("admin");
            
            if (null == wkYear || wkYear.equals(""))
            {}
            else if (wkMode.equals("grid"))
            {
                wkPageData = createPageDocument(wkYear, wkMode, wkAdmin);
                XSLTRenderHelper.render(inServlet, wkPageData, "draftgrid.xsl", inResponse);
            }
            else if (wkMode.equals("list"))
            {
                wkPageData = createPageDocument(wkYear, wkMode, wkAdmin);
                XSLTRenderHelper.render(inServlet, wkPageData, "draftlist.xsl", inResponse);                
            }
            else if (wkMode.equals("status"))
            {
                wkPageData = createPageDocument(wkYear, wkMode, wkAdmin);
                XSLTRenderHelper.render(inServlet, wkPageData, "draftstatus.xsl", inResponse);                
            }
            else if (wkMode.equals("draftscout"))
            {
                wkPageData = createPageDocument(wkYear, wkMode, wkAdmin);
                XSLTRenderHelper.render(inServlet, wkPageData, "scout.xsl", inResponse);                
            }
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
            Document wkPageData = null;
            String wkYear = inRequest.getParameter("year");
            String wkMode = inRequest.getParameter("mode");
            String wkAdmin = inRequest.getParameter("admin");
            
            if (null == wkYear || wkYear.equals(""))
            {}
            else if (wkMode.equals("grid"))
            {
                wkPageData = createPageDocument(wkYear, wkMode, wkAdmin);
                XSLTRenderHelper.render(inServlet, wkPageData, "draftgrid.xsl", inResponse);
            }
            else if (wkMode.equals("list"))
            {
                wkPageData = createPageDocument(wkYear, wkMode, wkAdmin);
                XSLTRenderHelper.render(inServlet, wkPageData, "draftlist.xsl", inResponse);                
            }
            else if (wkMode.equals("status"))
            {
                processParameters(inRequest);  
                wkPageData = createPageDocument(wkYear, wkMode, wkAdmin);
                XSLTRenderHelper.render(inServlet, wkPageData, "draftstatus.xsl", inResponse);                
            }
            else if (wkMode.equals("draftscout"))
            {
                processParameters(inRequest);  
                wkPageData = createPageDocument(wkYear, wkMode, wkAdmin);
                XSLTRenderHelper.render(inServlet, wkPageData, "scout.xsl", inResponse);                
            }
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
    
    @SuppressWarnings ("unchecked")
    private void processParameters(HttpServletRequest inRequest)
    {
        Enumeration<String> wkParams = inRequest.getParameterNames();  
        int wkPlayerId = 0;
        
        while (wkParams.hasMoreElements())
        {
            String wkParamName = (String) wkParams.nextElement();
            //System.out.println("Processing Param: " + wkParamName + " with value: " + inRequest.getParameter(wkParamName));
            
            if (wkParamName.equals("DraftedPlayer"))
            {
                wkPlayerId = Integer.parseInt(inRequest.getParameter(wkParamName));
            }
        }
        
        //System.out.println("Drafted Player" + PlayerCache.get(wkPlayerId).getDisplayname());
        
        if (0 != wkPlayerId)
        {
            transactionManager.draftPlayer(wkPlayerId);
        }
        return;
    }
    private Document createPageDocument(String inTeamList, String inMode, String inAdmin)
    {
        Element wkRoot = null;
        StringTokenizer wkTeams = new StringTokenizer(inTeamList,",");
        
        if (inMode.equals("grid"))
        {
            wkRoot = new Element("Drafts");
            while (wkTeams.hasMoreTokens())
            {
                int wkYear = Integer.parseInt(wkTeams.nextToken());
                System.out.println("Getting draft grid for year " + wkYear);
                Element wkDraft = transactionManager.getDraftGrid(wkYear, 0, false, true);
                wkRoot.addContent(wkDraft);
            }
        }
        else if (inMode.equals("list"))
        {
            wkRoot = cardedList.getCardedList();
        }
        else if (inMode.equals("draftscout"))
        {
            wkRoot = teamManager.getDraftRoster(transactionManager.getCurrentTransactionsSeason());
        
        }
        else if (inMode.equals("status"))
        {
            wkRoot = new Element("Drafts");
            boolean wkAdmin = false;
            if (null != inAdmin && inAdmin.equals("Y"))
                wkAdmin = true;
            while (wkTeams.hasMoreTokens())
            {
                int wkYear = Integer.parseInt(wkTeams.nextToken());
                Element wkDraft = transactionManager.getDraftGrid(wkYear, 0, wkAdmin, false);
                wkRoot.addContent(wkDraft);
            }
        }
        Document wkPage = new Document(wkRoot);
                   //XMLOutputter wkOut = new XMLOutputter();
            //wkOut.setNewlines(true);
            //System.out.println(wkOut.outputString(wkPage));
        return wkPage;
    }
    
}
