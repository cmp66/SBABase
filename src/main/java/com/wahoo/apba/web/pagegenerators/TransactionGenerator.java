/*
 * RosterGenerator.java
 *
 * Created on February 9, 2003, 1:38 PM
 */

package com.wahoo.apba.web.pagegenerators;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import com.oreilly.javaxslt.util.XSLTRenderHelper;
import com.wahoo.apba.resourcemanagers.CardedList;
import com.wahoo.apba.resourcemanagers.TeamCache;
import com.wahoo.apba.resourcemanagers.TransactionManager;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class TransactionGenerator implements IPageGenerator
{
	private TransactionManager transactionManager = null;
    private CardedList cardedList = null;
    
    /** Creates a new instance of MainPageGenerator */
    public TransactionGenerator ()
    {
    }

    public void init ()
    {
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
            String wkTeam1 = inRequest.getParameter("team1");
            String wkTeam2 = inRequest.getParameter("team2");
            String wkMode = inRequest.getParameter("mode");
            
            if (wkMode.equals("ShowForm"))
            {
                if (null == wkTeam1 || wkTeam1.equals("") || null == wkTeam2 || wkTeam2.equals(""))
                {}
                else
                {
                    wkPageData = createPageDocument(wkMode, wkTeam1, wkTeam2);
                    XSLTRenderHelper.render(inServlet, wkPageData, "tradeform.xsl", inResponse);
                }
            }
            else if (wkMode.equals("DropPlayers"))
            {
                 if (null == wkTeam1 || wkTeam1.equals(""))
                {}
                else
                {
                    wkPageData = createPageDocument(wkMode, wkTeam1, wkTeam2);
                    XSLTRenderHelper.render(inServlet, wkPageData, "DropPlayers.xsl", inResponse);
                }
            }
            else if (wkMode.equals("AddPlayers"))
            {
                 if (null == wkTeam1 || wkTeam1.equals(""))
                {}
                else
                {
                    wkPageData = createPageDocument(wkMode, wkTeam1, wkTeam2);
                    XSLTRenderHelper.render(inServlet, wkPageData, "AddPlayers.xsl", inResponse);
                }
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
            String wkMode = inRequest.getParameter("mode");
            String wkTeam1 = inRequest.getParameter("team1");
            String wkTeam2 = inRequest.getParameter("team2");
            
            if (wkMode.equals("ShowForm"))
            {
                if (null == wkTeam1 || wkTeam1.equals("") || null == wkTeam2 || wkTeam2.equals(""))
                {}
                else
                {
                    wkPageData = createPageDocument(wkMode, wkTeam1, wkTeam2);
                    XSLTRenderHelper.render(inServlet, wkPageData, "tradeform.xsl", inResponse);
                }
            }
            else if (wkMode.equals("ProcessTrade"))
            {
                HashMap<String, String> wkRecords = processParameters(inRequest);
                transactionManager.processTrade(wkRecords);
                inResponse.sendRedirect("Controller?page=management");
            }
            else if (wkMode.equals("CompleteDrop"))
            {
                HashMap<String, String> wkRecords = processParameters(inRequest);
                transactionManager.processDrops(wkRecords);
                inResponse.sendRedirect("Controller?page=management");
            }
            else if (wkMode.equals("CompleteAdd"))
            {
                HashMap<String, String> wkRecords = processParameters(inRequest);
                transactionManager.processAdds(wkRecords, Integer.parseInt(wkTeam1));
                inResponse.sendRedirect("Controller?page=management");
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
    private Document createPageDocument(String inMode, String inTeam1, String inTeam2)
    {
        Element wkRoot = new Element("Teams");
        
        if (inMode.equals("DropPlayers") || inMode.equals("ShowForm"))
        {
            wkRoot.addContent(transactionManager.getTeamTradables(Integer.parseInt(inTeam1))); 
        }
        if (inMode.equals("ShowForm"))
        {
            wkRoot.addContent(transactionManager.getTeamTradables(Integer.parseInt(inTeam2)));
        }
        if (inMode.equals("AddPlayers"))
        {
            wkRoot = cardedList.getCardedList();
            wkRoot.setAttribute("name", TeamCache.get(Integer.parseInt(inTeam1)).getNickname());
            wkRoot.setAttribute("id", inTeam1);
        }
        Document wkPage = new Document(wkRoot);
                   //XMLOutputter wkOut = new XMLOutputter();
            //wkOut.setNewlines(true);
            //System.out.println(wkOut.outputString(wkPage));
        return wkPage;
    }
    
    @SuppressWarnings("unchecked")
    private HashMap<String, String> processParameters(HttpServletRequest inRequest)
    {
        HashMap<String, String> wkRecords = new HashMap<String, String>(10);
        Enumeration wkParams = inRequest.getParameterNames();  
        
        while (wkParams.hasMoreElements())
        {
            String wkParamName = (String) wkParams.nextElement();
            String wkParamValue = inRequest.getParameter(wkParamName);
            //System.out.println("Processing Param: " + wkParamName + " with value: " + wkParamValue);
            
            if (wkParamValue.equals("true"))
            {
                wkRecords.put(wkParamName, wkParamValue);
            }
        }
        
        return wkRecords;
    }
    
}
