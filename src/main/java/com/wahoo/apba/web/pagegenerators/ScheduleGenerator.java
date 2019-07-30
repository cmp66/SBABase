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
import com.wahoo.apba.excel.TeamStat;
import com.wahoo.apba.resourcemanagers.ScheduleManager;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class ScheduleGenerator implements IPageGenerator
{
	private TeamStat teamStat = null;
	private ScheduleManager scheduleManager = null;
    
    /** Creates a new instance of MainPageGenerator */
    public ScheduleGenerator ()
    {
    }
    
    public void setTeamStat(TeamStat inStat)
    {
    	this.teamStat = inStat;
    }
    
    public void setScheduleManager(ScheduleManager inMgr)
    {
    	this.scheduleManager = inMgr;
    }

    public void init ()
    {
        //String wkName = "/WEB_INF/xslt/teamschedule.xsl";
        //URL wkURL = getServletContext().getResource(wkName);
    }
    
    public void generatePage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
        	String wkSeason = inRequest.getParameter("season");
            int wkTeam = Integer.parseInt(inRequest.getParameter("team"));
            String wkMode = inRequest.getParameter("mode");
            
            if (null != wkMode && wkMode.equals("genseriesfile"))
            {
                int wkYear = Integer.parseInt(inRequest.getParameter("year"));
                int wkSeries = Integer.parseInt(inRequest.getParameter("series"));
                int wkOtherTeam = Integer.parseInt(inRequest.getParameter("otherteam"));
                
                teamStat.createTeamSeriesStatSheet(wkYear, wkSeries, wkTeam, wkOtherTeam);
            }
            
                
                
            Document wkPageData = createPageDocument(wkTeam, wkSeason);
            XSLTRenderHelper.render(inServlet, wkPageData, "teamschedule.xsl", inResponse);
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
    
    private Document createPageDocument(int inTeam, String inSeason)
    {
    	Element wkTeams = null;
    	
    	if (inSeason != null)
    	{
    		wkTeams = scheduleManager.getSchedule(inTeam, Integer.parseInt(inSeason));
    	}
    	else
    	{
    		wkTeams = scheduleManager.getSchedule(inTeam);
    	}
        
        Document wkPage = new Document(wkTeams);
        return wkPage;
    }
    
    public void generatePostPage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
    }
    
}
