package com.oreilly.javaxslt.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * A utility class that uses the Servlet 2.3 Filtering API to apply
 * an XSLT stylesheet to a servlet response.
 *
 * @author Eric M. Burke
 */
public class StylesheetFilter implements Filter {
    //private FilterConfig filterConfig;
    private String xsltFileName;

    /**
     * This method is called once when the filter is first loaded.
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        //this.filterConfig = filterConfig;

        // xsltPath should be something like "/WEB-INF/xslt/a.xslt"
        String xsltPath = filterConfig.getInitParameter("xsltPath");
        if (xsltPath == null) {
            throw new UnavailableException(
                    "xsltPath is a required parameter. Please "
                    + "check the deployment descriptor.");
        }

        // convert the context-relative path to a physical path name
        this.xsltFileName = filterConfig.getServletContext()
                .getRealPath(xsltPath);

        // verify that the file exists
        if (this.xsltFileName == null ||
                !new File(this.xsltFileName).exists()) {
            throw new UnavailableException(
                    "Unable to locate stylesheet: " + this.xsltFileName, 30);
        }
    }

    public void doFilter (ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {

        if (!(res instanceof HttpServletResponse)) {
            throw new ServletException("This filter only supports HTTP");
        }

        BufferedHttpResponseWrapper responseWrapper =
                new BufferedHttpResponseWrapper((HttpServletResponse) res);
        chain.doFilter(req, responseWrapper);

        // Tomcat 4.0 reuses instances of its HttpServletResponse
        // implementation class in some scenarios. For instance, hitting
        // reload() repeatedly on a web browser will cause this to happen.
        // Unfortunately, when this occurs, output is never written to the
        // BufferedHttpResponseWrapper's OutputStream. This means that the
        // XML output array is empty when this happens. The following
        // code is a workaround:
        byte[] origXML = responseWrapper.getBuffer();
        if (origXML == null || origXML.length == 0) {
            // just let Tomcat deliver its cached data back to the client
            chain.doFilter(req, res);
            return;
        }

        try {
            // do the XSLT transformation
            Transformer trans = StylesheetCache.newTransformer(
                    this.xsltFileName);

            ByteArrayInputStream origXMLIn = new ByteArrayInputStream(origXML);
            Source xmlSource = new StreamSource(origXMLIn);

            ByteArrayOutputStream resultBuf = new ByteArrayOutputStream();
            trans.transform(xmlSource, new StreamResult(resultBuf));

            res.setContentLength(resultBuf.size());
            res.setContentType("text/html");
            res.getOutputStream().write(resultBuf.toByteArray());
            res.flushBuffer();
        } catch (TransformerException te) {
            throw new ServletException(te);
        }
    }

    /**
     * The counterpart to the init() method.
     */
    public void destroy() {
        //this.filterConfig = null;
    }
}
