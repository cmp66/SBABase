/*
 * IPageGenerator.java
 *
 * Created on February 9, 2003, 1:02 PM
 */

package com.wahoo.apba.web.pagegenerators;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author  cphillips
 */
public interface IPageGenerator
{
    void init();
    void generatePage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse);
    void generatePostPage (HttpServlet inServlet, HttpServletRequest inRequest, HttpServletResponse inResponse);
    
}
