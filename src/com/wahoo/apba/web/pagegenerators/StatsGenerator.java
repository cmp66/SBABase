/*
 * RosterGenerator.java
 *
 * Created on February 9, 2003, 1:38 PM
 */

package com.wahoo.apba.web.pagegenerators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import com.oreilly.javaxslt.util.XSLTRenderHelper;
import com.wahoo.apba.database.GenericStatRecord;
import com.wahoo.apba.database.SeriesStatRecord;
import com.wahoo.apba.resourcemanagers.PlayerCache;
import com.wahoo.apba.resourcemanagers.StatsManager;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class StatsGenerator implements IPageGenerator
{
	private StatsManager statsManager = null;
	private PlayerCache playerCache = null;
	
    HashMap<String, String> _fieldName = new HashMap<String, String>(50);
    
    /** Creates a new instance of MainPageGenerator */
    public StatsGenerator ()
    {
    }

    public void init ()
    {
        _fieldName.put("games", "Games");
        _fieldName.put("bat_ab", "AB");
        _fieldName.put("bat_runs", "Runs");
        _fieldName.put("bat_hits", "Hits");
        _fieldName.put("bat_rbi", "RBI");
        _fieldName.put("bat_doubles", "Doubles");
        _fieldName.put("bat_triples", "Triples");
        _fieldName.put("bat_hr", "Bat HR");
        _fieldName.put("bat_walks", "Bat Walks");
        _fieldName.put("bat_strikeouts", "Bat Strikeouts");
        _fieldName.put("bat_sb", "SB");
        _fieldName.put("bat_cs", "CS");
        _fieldName.put("bat_hbp", "HBP");
        _fieldName.put("errors", "Errors");
        _fieldName.put("pitch_gp", "GP");
        _fieldName.put("pitch_gs", "GS");
        _fieldName.put("pitch_cg", "CG");
        _fieldName.put("pitch_sho", "SHO");
        _fieldName.put("pitch_wins", "Wins");
        _fieldName.put("pitch_loss", "Loss");
        _fieldName.put("pitch_save", "Save");
        _fieldName.put("pitch_ipfull", "IP");
        _fieldName.put("pitch_ipfract", "Fract IP");
        _fieldName.put("pitch_hits", "Pitch Hits");
        _fieldName.put("pitch_runs", "Pitch Runs");
        _fieldName.put("pitch_er", "Earned Runs");
        _fieldName.put("pitch_walks", "Pitch Walks");
        _fieldName.put("pitch_strikeouts", "Pitch Strikeouts");
        _fieldName.put("pitch_hr", "Pitch HR");
        
        
    }
    
    public void setStatsManager(StatsManager inMgr)
    {
    	this.statsManager = inMgr;
    }
    
    public void setPlayerCache(PlayerCache inCache)
    {
    	this.playerCache = inCache;
    }
    
    
    
    public void generatePage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
            Document wkPageData = null;
            String wkMode = inRequest.getParameter("mode");
            
            if (null == wkMode || wkMode.equals(""))
            {}
            // old Stats form
            //else if (wkMode.equals("Enter"))
            //{
            //    String wkTeam = inRequest.getParameter("team");
            //    wkPageData = createPageDocument(Integer.parseInt(wkTeam), TransactionManager.getCurrentStatsSeason());
            //    XSLTRenderHelper.render(inServlet, wkPageData, "StatsEntry.xsl", inResponse);
            //    TeamStat.createTeamStatSheet(2003, Integer.parseInt(wkTeam), TeamCache.get(Integer.parseInt(wkTeam)).getNickname());
            //}
            else if (wkMode.equals("Enter"))
            {
                String wkTeam = inRequest.getParameter("reportingteam");
                String wkYear = inRequest.getParameter("year");
                String wkSeries = inRequest.getParameter("series");
                String wkOtherTeam = inRequest.getParameter("otherteam");
                wkPageData = createPageDocument(Integer.parseInt(wkYear), Integer.parseInt(wkSeries), Integer.parseInt(wkTeam), Integer.parseInt(wkOtherTeam));
                //XSLTRenderHelper.render(inServlet, wkPageData, "StatsEntry.xsl", inResponse);
                XSLTRenderHelper.render(inServlet, wkPageData, "SeriesStatsEntry.xsl", inResponse);
                //TeamStat.createTeamSeriesStatSheet(Integer.parseInt(wkYear), Integer.parseInt(wkSeries), Integer.parseInt(wkTeam), Integer.parseInt(wkOtherTeam));
            }
            else if (wkMode.equals("ShowTeamStats"))
            {
                String wkTeams = inRequest.getParameter("teams");
                String wkYear = inRequest.getParameter("year");
                wkPageData = createStatsDisplayPageDocument(wkTeams, Integer.parseInt(wkYear));
                XSLTRenderHelper.render(inServlet, wkPageData, "ShowStats.xsl", inResponse);
            }
            else if (wkMode.equals("Leaders"))
            {
                String wkYear = inRequest.getParameter("year");
                wkPageData = createLeadersPageDocument(Integer.parseInt(wkYear));
                XSLTRenderHelper.render(inServlet, wkPageData, "Leaders.xsl", inResponse);
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
            String wkMode = inRequest.getParameter("mode");
            Document wkPageData = null;
            ArrayList<String> wkErrors = null;
            
            //if (wkMode.equals("ProcessStats"))
            //{
            //    String wkTeam = inRequest.getParameter("team");
            //    HashMap wkRecords = processParameters(inRequest);
            //    StatsManager.processStats(wkRecords);
                
            //    wkPageData = createPageDocument(Integer.parseInt(wkTeam), TransactionManager.getCurrentStatsSeason());
            //    XSLTRenderHelper.render(inServlet, wkPageData, "StatsEntry.xsl", inResponse);
            //}
            if (wkMode.equals("ProcessSeriesStats"))
            {
                int wkTeam = Integer.parseInt(inRequest.getParameter("reportingteam"));
                int wkYear = Integer.parseInt(inRequest.getParameter("year"));
                int wkSeries = Integer.parseInt(inRequest.getParameter("series"));
                int wkOtherTeam = Integer.parseInt(inRequest.getParameter("otherteam"));
                
                HashMap<Integer, GenericStatRecord> wkReportingTeamRecords = 
                	new HashMap<Integer, GenericStatRecord>(35);
                HashMap<Integer, GenericStatRecord> wkOtherTeamRecords = 
                	new HashMap<Integer, GenericStatRecord>(35);
                wkErrors = processParameters(inRequest, wkReportingTeamRecords, wkOtherTeamRecords);
                statsManager.processSeriesStats(wkReportingTeamRecords, wkOtherTeamRecords);
                
                statsManager.calculateTeamYearlyStats(wkYear, wkTeam);
                wkPageData = createPageDocument(wkYear, wkSeries, wkTeam, wkOtherTeam, wkErrors);
                XSLTRenderHelper.render(inServlet, wkPageData, "SeriesStatsEntry.xsl", inResponse);
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
    
    private Document createPageDocument(int inYear, int inSeries, int inReportTeam, int inOtherTeam)
    {
        Element wkRoot = statsManager.getTeamSeriesStats(inYear, inSeries, inReportTeam, inOtherTeam);
        
        Document wkPage = new Document(wkRoot);
                   // XMLOutputter wkOut = new XMLOutputter();
            //wkOut.setNewlines(true);
            //System.out.println(wkOut.outputString(wkPage));
        return wkPage;
    }
    
    private Document createPageDocument(int inYear, int inSeries, int inReportTeam, int inOtherTeam, ArrayList<String> inErrors)
    {
        Element wkRoot = statsManager.getTeamSeriesStats(inYear, inSeries, inReportTeam, inOtherTeam);
        Element wkErrors = statsManager.getErrors(inErrors);
        wkRoot.addContent(wkErrors);
                
        Document wkPage = new Document(wkRoot);
                   // XMLOutputter wkOut = new XMLOutputter();
            //wkOut.setNewlines(true);
            //System.out.println(wkOut.outputString(wkPage));
        return wkPage;
    }
    
    private Document createStatsDisplayPageDocument(String inTeams, int inYear)
    {
        StringTokenizer wkTeamList = new StringTokenizer(inTeams, ",");
        Element wkRoot = new Element("Stats");
        
        while (wkTeamList.hasMoreTokens())
        {
            int wkTeam = Integer.parseInt(wkTeamList.nextToken());
            wkRoot.addContent(statsManager.getTeamStats(inYear, wkTeam));
        }
        
        Document wkPage = new Document(wkRoot);
                   // XMLOutputter wkOut = new XMLOutputter();
            //wkOut.setNewlines(true);
            //System.out.println(wkOut.outputString(wkPage));
        return wkPage;
    }

    private Document createLeadersPageDocument(int inYear)
    {
        Element wkRoot = statsManager.getLeagueLeaders(inYear);
        
        Document wkPage = new Document(wkRoot);
                   // XMLOutputter wkOut = new XMLOutputter();
            //wkOut.setNewlines(true);
            //System.out.println(wkOut.outputString(wkPage));
        return wkPage;
    }
    
    
    @SuppressWarnings("unchecked")
    private ArrayList<String> processParameters(HttpServletRequest inRequest, HashMap<Integer, 
    		GenericStatRecord> wkTeamRecords, HashMap<Integer, GenericStatRecord> wkOtherRecords)
    {
        Enumeration wkParams = inRequest.getParameterNames();  
        int wkTeam = Integer.parseInt(inRequest.getParameter("reportingteam"));
        int wkYear = Integer.parseInt(inRequest.getParameter("year"));
        int wkSeries = Integer.parseInt(inRequest.getParameter("series"));
        int wkOtherTeam = Integer.parseInt(inRequest.getParameter("otherteam"));
        HashMap<Integer, GenericStatRecord> wkPlayerMap = new HashMap<Integer, GenericStatRecord>();;
        ArrayList<String> wkErrors = new ArrayList<String>();
        
                
        SeriesStatRecord wkRecord = null;
        
        while (wkParams.hasMoreElements())
        {
            String wkParamName = (String) wkParams.nextElement();
            StringTokenizer wkStatField = new StringTokenizer(wkParamName, "-");   
            String wkParamValue = inRequest.getParameter(wkParamName);
            //System.out.println("Processing Param: " + wkParamName + " with value: " + wkParamValue);
            Integer wkId = null;
            
            try
            {
                wkId = new Integer(wkStatField.nextToken());
            }
            catch (java.lang.NumberFormatException wkEx)
            {
                continue;
            }
            
                
            if (!wkPlayerMap.containsKey(wkId))
            {
                wkRecord = new SeriesStatRecord();
                wkRecord.setPlayerid(wkId.intValue());
                wkRecord.setSeries(wkSeries);
                wkRecord.setReportingTeam(wkTeam);
                //wkRecord.setTeamid(wkOwningTeam);
                wkRecord.setSeason(wkYear);
                wkPlayerMap.put(wkId, wkRecord);
            }
            else
            {
                wkRecord = (SeriesStatRecord) wkPlayerMap.get(wkId);
            }
            
            String wkField = wkStatField.nextToken();
            
            try
            {
                addStatToRecord(wkRecord, wkField, Integer.parseInt(wkParamValue));
            }
            catch (Exception e)
            {
                Email.emailException(e);
                wkErrors.add("Invalid value [" + wkParamValue + "] entered for field " + _fieldName.get(wkField) + " for player " + playerCache.get(wkRecord.getPlayerid()).getDisplayname());
            }        
        }
        
        Iterator<GenericStatRecord> wkIter = wkPlayerMap.values().iterator();
        
        while (wkIter.hasNext())
        {
            wkRecord = (SeriesStatRecord) wkIter.next();
        
            //int wkOwningTeam = TeamManager.getPlayerTeam(wkId.intValue(), wkYear);
            int wkOwningTeam = wkRecord.getTeamid();
            
            if (wkOwningTeam == wkTeam)
            {
                wkTeamRecords.put(Integer.valueOf(wkRecord.getPlayerid()), wkRecord);
            }
            else if (wkOwningTeam == wkOtherTeam)
            {
                wkOtherRecords.put(Integer.valueOf(wkRecord.getPlayerid()), wkRecord);
            }
            else
            {
                System.out.println("Could not find owner team for player " + wkRecord.getPlayerid());
                continue;
            }
        }
            
        return wkErrors;
    }
    
    private void addStatToRecord(SeriesStatRecord inRecord, String inFieldName, int inValue)
    {
        //if (inValue > 0)
        //    System.out.println("Setting " + inFieldName + "for " + PlayerCache.get(inRecord.getPlayerid()).getDisplayname() + " to " + inValue);
        try
        {             
        if (inFieldName.equals("games"))
        {
            inRecord.setGames(inValue);
        }
        else if (inFieldName.equals("teamid"))
        {
            inRecord.setTeamid(inValue);
        }
        else if (inFieldName.equals("season"))
        {
            inRecord.setSeason(inValue);
        }
        else if (inFieldName.equals("bat_ab"))
        {
            inRecord.setBat_ab(inValue);
        }
        else if (inFieldName.equals("bat_runs"))
        {
            inRecord.setBat_runs(inValue);
        }
        else if (inFieldName.equals("bat_hits"))
        {
            inRecord.setBat_hits(inValue);
        }
        else if (inFieldName.equals("bat_rbi"))
        {
            inRecord.setBat_rbi(inValue);
        }
        else if (inFieldName.equals("bat_doubles"))
        {
            inRecord.setBat_doubles(inValue);
        }
        else if (inFieldName.equals("bat_triples"))
        {
            inRecord.setBat_triples(inValue);
        }
        else if (inFieldName.equals("bat_hr"))
        {
            inRecord.setBat_hr(inValue);
        }
        else if (inFieldName.equals("bat_walks"))
        {
            inRecord.setBat_walks(inValue);
        }
        else if (inFieldName.equals("bat_strikeouts"))
        {
            inRecord.setBat_strikeouts(inValue);
        }
        else if (inFieldName.equals("bat_sb"))
        {
            inRecord.setBat_sb(inValue);
        }
        else if (inFieldName.equals("bat_cs"))
        {
            inRecord.setBat_cs(inValue);
        }
        else if (inFieldName.equals("bat_hbp"))
        {
            inRecord.setBat_hbp(inValue);
        }
        else if (inFieldName.equals("errors"))
        {
            inRecord.setErrors(inValue);
        }
        else if (inFieldName.equals("pitch_gp"))
        {
            inRecord.setPitch_gp(inValue);
        }
        else if (inFieldName.equals("pitch_gs"))
        {
            inRecord.setPitch_gs(inValue);
        }
        else if (inFieldName.equals("pitch_cg"))
        {
            inRecord.setPitch_cg(inValue);
        }
        else if (inFieldName.equals("pitch_sho"))
        {
            inRecord.setPitch_sho(inValue);
        }
        else if (inFieldName.equals("pitch_wins"))
        {
            inRecord.setPitch_wins(inValue);
        }
        else if (inFieldName.equals("pitch_loss"))
        {
            inRecord.setPitch_loss(inValue);
        }
        else if (inFieldName.equals("pitch_save"))
        {
            inRecord.setPitch_save(inValue);
        }
        else if (inFieldName.equals("pitch_ipfull"))
        {
            inRecord.setPitch_ipfull(inValue);
        }
        else if (inFieldName.equals("pitch_ipfract"))
        {
            inRecord.setPitch_ipfract(inValue);
        }
        else if (inFieldName.equals("pitch_hits"))
        {
            inRecord.setPitch_hits(inValue);
        }
        else if (inFieldName.equals("pitch_runs"))
        {
            inRecord.setPitch_runs(inValue);
        }
        else if (inFieldName.equals("pitch_er"))
        {
            inRecord.setPitch_er(inValue);
        }
        else if (inFieldName.equals("pitch_walks"))
        {
            inRecord.setPitch_walks(inValue);
        }
        else if (inFieldName.equals("pitch_strikeouts"))
        {
            inRecord.setPitch_strikeouts(inValue);
        }
        else if (inFieldName.equals("pitch_hr"))
        {
            inRecord.setPitch_hr(inValue);
        }
        }
        catch (Exception e)
        {
            Email.emailException(e);
        }
    }
}
