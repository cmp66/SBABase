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
import com.wahoo.apba.database.util.RotoWire;
import com.wahoo.util.Email;

/**
 *
 * @author  cphillips
 */
public class RotoWireGenerator implements IPageGenerator
{
	private RotoWire rotoWire = null;
    
    /** Creates a new instance of MainPageGenerator */
    public RotoWireGenerator ()
    {
    }

    
    public void setRotoWire(RotoWire inWire)
    {
    	this.rotoWire = inWire;
    }
    
    public void init ()
    {
    }
    
    public void generatePage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
            Document wkPageData = createPageDocument();
            //XMLOutputter wkOut = new XMLOutputter();
            //wkOut.setNewlines(true);
            //System.out.println(wkOut.outputString(wkPageData));
            XSLTRenderHelper.render(inServlet, wkPageData, "rotowire.xsl", inResponse);
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
    	//RotoWire wkRoto = new RotoWire();
    	
        Element wkLastUpdate = rotoWire.getLastUpdate();
        
        Document wkPage = new Document(wkLastUpdate);
        return wkPage;
    }
    
    public void generatePostPage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse)
    {
        try
        {
            String wkNews = processParameters(inRequest);

            wkNews = wkNews.replaceAll("<p>", "\n");
            wkNews = wkNews.replaceAll("<b>" , "");
            wkNews = wkNews.replaceAll("</b>", "");
            wkNews = wkNews.replaceAll("<br>" , "\n");

            System.out.println("FULL REPORT");
            System.out.println(wkNews);
            System.out.println("_________________________");

        
            //RotoWire wkRoto = new RotoWire();
            rotoWire.parseReport(wkNews);
            
            Document wkPageData= createPageDocument();
            XSLTRenderHelper.render(inServlet, wkPageData, "rotowire.xsl", inResponse);
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
    
 
    private String processParameters(HttpServletRequest inRequest)
    {
        return inRequest.getParameter("rotowire");
 
    }
    
 
}
