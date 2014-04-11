/*
 * MainPageGenerator.java
 *
 * Created on February 9, 2003, 1:38 PM
 */

package com.wahoo.apba.web.pagegenerators;

import java.io.IOException;
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
import com.wahoo.apba.database.GameResult;
import com.wahoo.apba.resourcemanagers.EmailManager;
import com.wahoo.apba.resourcemanagers.ScheduleManager;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class SeriesGenerator implements IPageGenerator
{
	private ScheduleManager scheduleManager = null;
	private EmailManager emailManager = null;
	
    /** Creates a new instance of MainPageGenerator */
    public SeriesGenerator ()
    {
    }

    public void init ()
    {
    }
    
    public void setScheduleManager(ScheduleManager inMgr)
    {
    	this.scheduleManager = inMgr;
    }
    
    public void setEmailManager(EmailManager inMgr)
    {
    	this.emailManager = inMgr;
    }
    
    public void generatePage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
            int wkSeries = Integer.parseInt(inRequest.getParameter("series"));
            Document wkPageData = createPageDocument(wkSeries);
            //XMLOutputter wkOut = new XMLOutputter();
            //wkOut.setNewlines(true);
            //System.out.println(wkOut.outputString(wkPageData));
            XSLTRenderHelper.render(inServlet, wkPageData, "seriesresult.xsl", inResponse);
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
    
    private Document createPageDocument(int inSeries)
    {
        Element wkTeams = scheduleManager.getSeries(inSeries);
        
        Document wkPage = new Document(wkTeams);
        return wkPage;
    }
    
    @SuppressWarnings ("unchecked")
    public void generatePostPage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
            HashMap wkRecords = processParameters(inRequest);
        
            scheduleManager.updateSeries(wkRecords);
            
            int wkSeries = Integer.parseInt(inRequest.getParameter("id"));
            Document wkPageData= createPageDocument(wkSeries);
            mailSeriesResult(inServlet, wkPageData);
            XSLTRenderHelper.render(inServlet, wkPageData, "seriesresult.xsl", inResponse);
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
    
    private void mailSeriesResult(HttpServlet inServlet, Document inSeries)
    {
        String wkMailList = null;
        try
        {
            String wkSeriesText = XSLTRenderHelper.render(inServlet, inSeries, "SeriesResultEmail.xsl");
            wkMailList = emailManager.getMemberAddresses();
            Email.sendMail("cmp1166@gmail.com", wkMailList, "Series Report", wkSeriesText);
        }
        catch (Exception wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }
    
    @SuppressWarnings("unchecked")
    private HashMap<String, GameResult> processParameters(HttpServletRequest inRequest)
    {
        HashMap<String, GameResult> wkRecords = new HashMap<String, GameResult>(10);
        Enumeration wkParams = inRequest.getParameterNames();  
        GameResult wkResult = null;
        int wkScheduleId = 0;
        
        while (wkParams.hasMoreElements())
        {
            String wkParamName = (String) wkParams.nextElement();
            //System.out.println("Processing Param: " + wkParamName + " with value: " + inRequest.getParameter(wkParamName));
            
            if (wkParamName.equals("id"))
            {
                wkScheduleId = Integer.parseInt(inRequest.getParameter(wkParamName));
            }
            else
            {
                StringTokenizer wkTokens = new StringTokenizer(wkParamName,"-");
                if (wkTokens.countTokens() == 2)
                {
                    String wkId = wkTokens.nextToken();
                    if (wkRecords.containsKey(wkId))
                    {
                        wkResult = (GameResult) wkRecords.get(wkId);
                    }
                    else
                    {
                        wkResult = new GameResult();
                        wkResult.setGamenumber(Integer.parseInt(wkId));
                        wkRecords.put(wkId, wkResult);
                    }
                    addFieldToRecord(wkTokens.nextToken(), wkResult, inRequest.getParameter(wkParamName));
                }
            }
        }
        
        Iterator<GameResult> wkAllGames = wkRecords.values().iterator();
        while(wkAllGames.hasNext())
        {
            wkResult = wkAllGames.next();
            wkResult.setScheduleid(wkScheduleId);
        }
        
        return wkRecords;
    }
    
    private void addFieldToRecord(String inName, GameResult inResult, String inValue)
    {
        
        if (null == inValue || inValue.equals(""))
            inValue="0";
        
        //System.out.println("Value is #" + inValue + "#");
        
        if ("homescore".equals(inName))
        {
            inResult.setHomeruns(Integer.parseInt(inValue));
        }
        else if ("visitscore".equals(inName))
        {
            inResult.setVisitruns(Integer.parseInt(inValue));
        }
        else if ("visitpitcher".equals(inName))
        {
            inResult.setVisitpitcher(Integer.parseInt(inValue));
        }
        else if ("homepitcher".equals(inName))
        {
            inResult.setHomepitcher(Integer.parseInt(inValue));
        }
        else if ("comment".equals(inName))
        {
            inResult.setComment(inValue);
        }
    }
}
