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
import com.wahoo.apba.resourcemanagers.TeamManager;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class InstructionsRosterGenerator implements IPageGenerator
{
    private TeamManager teamManager = null;
    
    public InstructionsRosterGenerator ()
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
            Document wkPageData = null;
            String wkRoster = inRequest.getParameter("rosterid");
            String wkTeam = inRequest.getParameter("teamid");
            String wkYear = inRequest.getParameter("season");
            String wkMode = inRequest.getParameter("mode");
            
            if (wkMode.equals("edit"))
            {
                if ( (wkYear == null || wkYear.equals("")))
                {}
                else if (wkTeam == null || wkTeam.equals(""))
				{}
                else
                {
                	if (wkRoster == null || wkRoster.equals(""))
                	{
                		wkRoster = "0";
                	}
                    wkPageData = createPageDocument(wkMode, wkTeam, wkRoster, wkYear);
                    XSLTRenderHelper.render(inServlet, wkPageData, "InstructionsRosterEdit.xsl", inResponse);
                }
            }
            else if (wkMode.equals("add"))
            {
                if (wkYear == null || wkYear.equals(""))
                {}
                else if (wkTeam == null || wkTeam.equals(""))
                {}
                else
                {
                	wkPageData = createPageDocument(wkMode, wkTeam, wkRoster, wkYear);
                    XSLTRenderHelper.render(inServlet, wkPageData, "InstructionsRosterAdd.xsl", inResponse);
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
            String wkRoster = inRequest.getParameter("rostername");
            String wkTeam = inRequest.getParameter("teamid");
            String wkYear = inRequest.getParameter("season");
            String wkMode = inRequest.getParameter("mode");
            HashMap<String, String> wkRecords = processParameters(inRequest);
            
            if (wkMode.equals("add"))
            {
            	if (wkYear == null || wkYear.equals(""))
                {}
                else if (wkTeam == null || wkTeam.equals(""))
                {}
                else
                {
                    teamManager.processAddNewInstructionRoster(wkTeam, wkRoster, wkYear, wkRecords);
                    wkMode = "edit";
                	wkPageData = createPageDocument(wkMode, wkTeam, wkRoster, wkYear);
                    XSLTRenderHelper.render(inServlet, wkPageData, "InstructionsRosterEdit.xsl", inResponse);
                }
            }
            if (wkMode.equals("edit"))
            {
            	if ( (wkYear == null || wkYear.equals("")))
                	{}
                        else if ( (wkTeam == null || wkTeam.equals("")) ||
                        		  (wkRoster == null || wkRoster.equals("")))
        				{}
                else
                {
                    teamManager.processEditInstructionRoster(wkRoster, wkRecords);
                	wkPageData = createPageDocument(wkMode, wkTeam, wkRoster, wkYear);
                    XSLTRenderHelper.render(inServlet, wkPageData, "InstructionsRosterEdit.xsl", inResponse);
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
    private Document createPageDocument(String inMode, String inTeam, String inRoster, String inYear)
    {
        Element wkRoot = new Element("Team");
        
        if (inMode.equals("add"))
        {
        	wkRoot.addContent(teamManager.getInstructionRosters(Integer.parseInt(inTeam), Integer.parseInt(inYear), Integer.parseInt(inRoster)));
        	wkRoot.addContent(teamManager.getRoster(Integer.parseInt(inTeam), Integer.parseInt(inYear), 0));
        }
        if (inMode.equals("edit"))
        {
        	wkRoot.addContent(teamManager.getInstructionRosters(Integer.parseInt(inTeam), Integer.parseInt(inYear), Integer.parseInt(inRoster)));
        	wkRoot.addContent(teamManager.getRoster(Integer.parseInt(inTeam), Integer.parseInt(inYear), Integer.parseInt(inRoster)));
        }
        Document wkPage = new Document(wkRoot);
                   //XMLOutputter wkOut = new XMLOutputter();
            //wkOut.setNewlines(true);
            //System.out.println(wkOut.outputString(wkPage));
        return wkPage;
    }
    
    @SuppressWarnings ("unchecked")
    private HashMap<String, String> processParameters(HttpServletRequest inRequest)
    {
        HashMap<String, String> wkRecords = new HashMap<String, String>(10);
        Enumeration<String> wkParams = inRequest.getParameterNames();  
        
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
