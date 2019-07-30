/*
 * DataAccessServlet.java
 *
 * Created on February 1, 2003, 2:22 PM
 */

package com.wahoo.apba.web.servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.javaxslt.util.StylesheetCache;
import com.wahoo.apba.web.pagegenerators.IPageGenerator;
import com.wahoo.apba.web.util.ControllerMapper;
import com.wahoo.apba.web.util.WebUtil;
import com.wahoo.util.Email;

/** Servlet used to access resource information within the APBA web applications.  Used primarily by the list applets embedded in the web pages
 * @author cphillips
 */
public class ControllerServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	Map<String, IPageGenerator> _generatorMap = null;
    
    transient WebUtil _util = null;
 
    public ControllerServlet ()
    {
    }
 
    
    /** Required servlet init method.  As this servlet is started upon the web applications being loaded, the servlet will load the resource caches.  It will also check for player record updates in the the players.xml config file
     * @param inConfig Servlet configuration
     */    
    public void init(javax.servlet.ServletConfig inConfig)
    {
		try
        {
            super.init(inConfig);
            
            _util = new WebUtil();
            _util.setServletCtx(getServletContext());
            loadGenerators();
        }
        catch (ServletException wkSE)
        {
            wkSE.printStackTrace();
            Email.emailException(wkSE);
        }	
        catch (Exception wkE)
        {
            wkE.printStackTrace();
            Email.emailException(wkE);
        }	
    }
    

    
    
    private void loadGenerators()
    {
    	ControllerMapper wkMapper = (ControllerMapper) _util.getBean("controllerMappings");
    	_generatorMap = wkMapper.getPageMap();
 
        try
        {
            Email.sendMail("cmp1166@gmail.com", "cmp1166@gmail.com", "Server started", "Server started");
        }
        catch (Exception wkEx)
        {
            wkEx.printStackTrace();
            Email.emailException(wkEx);
        }
    }
        

    public void destroy()
    {
        StylesheetCache.flushAll();
    }
    
    
    
    public void doGet(HttpServletRequest inRequest, HttpServletResponse inResponse)
        throws ServletException, IOException
    {
        String wkRequestedPage = inRequest.getParameter("page");
        System.out.println("Request page is " + wkRequestedPage);
        IPageGenerator wkGen = _generatorMap.get(wkRequestedPage);
        wkGen.generatePage(this, inRequest, inResponse);        
    }
    
    public void doPost(HttpServletRequest inRequest, HttpServletResponse inResponse)
        throws ServletException, IOException
    {
        String wkRequestedPage = inRequest.getParameter("page");
        IPageGenerator wkGen =  _generatorMap.get(wkRequestedPage);
        wkGen.generatePostPage(this, inRequest, inResponse);        
    }
}
