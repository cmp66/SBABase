package com.oreilly.javaxslt.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/**
 * A helper class that makes rendering of XSLT easier. This
 * eliminates the need to duplicate a lot of code for each
 * of the web pages in this app.
 */
public class XSLTRenderHelper {
    private static Map<String, String> filenameCache = new HashMap<String, String>();

    /**
     * Perform an XSLT transformation.
     *
     * @param servlet provides access to the ServletContext so
     *                the XSLT directory can be determined.
     * @param xmlJDOMData JDOM data for the XML Document.
     * @param xsltBaseName the name of the stylesheet without a directory.
     * @param response the Servlet response to write output to.
     */
    public static void render(HttpServlet servlet, Document xmlJDOMData,
            String xsltBaseName, HttpServletResponse response)
            throws ServletException, IOException {

        String xsltFileName = null;
        try {
            // figure out the complete XSLT stylesheet file name
            synchronized (filenameCache) {
                xsltFileName = (String) filenameCache.get(xsltBaseName);
                if (xsltFileName == null) {
                    ServletContext ctx = servlet.getServletContext();
                    xsltFileName = ctx.getRealPath(
                            "/WEB-INF/xslt/" + xsltBaseName);
                    filenameCache.put(xsltBaseName, xsltFileName);
                }
            }

            // write the JDOM data to a StringWriter
            StringWriter sw = new StringWriter();
            XMLOutputter xmlOut = new XMLOutputter("", false, "UTF-8");
            xmlOut.output(xmlJDOMData, sw);

            response.setContentType("text/html");
            Transformer trans = StylesheetCache.newTransformer(xsltFileName);

            // pass a parameter to the XSLT stylesheet
            //trans.setParameter("rootDir", "/forum/");
            
            
            StringWriter stringWriter = new StringWriter();
            StreamResult outputStreamResult = new StreamResult(stringWriter);

            trans.transform(new StreamSource(new StringReader(sw.toString())),
                            outputStreamResult);
            
            String unescapedString = StringEscapeUtils.unescapeHtml(stringWriter.toString());
            
            response.getOutputStream().write(unescapedString.getBytes());
            
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
    
    public static String render(HttpServlet servlet, Document xmlJDOMData, String xsltBaseName)
            throws IOException, TransformerException {

        String xsltFileName = null;
        StringWriter outSW = new StringWriter();
        try {
            // figure out the complete XSLT stylesheet file name
            synchronized (filenameCache) {
                xsltFileName = (String) filenameCache.get(xsltBaseName);
                if (xsltFileName == null) {
                    ServletContext ctx = servlet.getServletContext();
                    xsltFileName = ctx.getRealPath(
                            "/WEB-INF/xslt/" + xsltBaseName);
                    filenameCache.put(xsltBaseName, xsltFileName);
                }
            }

            // write the JDOM data to a StringWriter
            StringWriter sw = new StringWriter();
            XMLOutputter xmlOut = new XMLOutputter("", false, "UTF-8");
            xmlOut.output(xmlJDOMData, sw);

            Transformer trans = StylesheetCache.newTransformer(xsltFileName);

            // pass a parameter to the XSLT stylesheet
            //trans.setParameter("rootDir", "/forum/");

            trans.transform(new StreamSource(new StringReader(sw.toString())),
                            new StreamResult(outSW));
        } catch (IOException ioe) {
            throw ioe;
        }
        
        return outSW.toString();
    }

    private XSLTRenderHelper() {
    }
}
