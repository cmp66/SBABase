/*
 * $Id: UploadServlet.java,v 1.1.1.1 2005/03/10 03:27:15 cmp66 Exp $
 *
 * Copyright (c) 1998-2002 Yoon Kyung Koo(yoonforh@yahoo.com). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL YOON KYUNG KOO OR THE OTHER
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */


package yoonforh.upload.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;

import yoonforh.upload.UploadData;
import yoonforh.upload.UploadHandler;
import yoonforh.upload.UploadWorker;

import com.wahoo.apba.excel.TeamStat;
import com.wahoo.apba.web.util.WebUtil;


/**
 * a servlet handles upload request.
 * use upload action instead.
 * refer to  http://www.rfc.net/rfc1867.html
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2002-02-20 01:16:46
 * @author   Yoon Kyung Koo
 * @deprecated
 */

public class UploadServlet extends HttpServlet {

    private static final long serialVersionUID = -995017596005029391L;
    private transient Category logger = null;
    private final static int BUFFER_SIZE = 2048;
    private final static String PATH_KEY = "path";
    private transient WebUtil _util = null;

    public void init() {
	logger = Category.getInstance("upload.test");
	_util = new WebUtil();
	_util.setServletCtx(getServletContext());
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
        doPost( req, res );
    }

    public void doPost (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	//long start = System.currentTimeMillis();
	UploadHandler handler = new UploadHandler(new UploadServletWorker(res));
	try {
	    handler.upload(req);
	} catch (ServletException e) {
	    e.printStackTrace();
	}
    }


    public String getServletInfo() {
	return "A servlet that uploads files";
    }

    /**
     * worker class
     */
    class UploadServletWorker implements UploadWorker {
	HttpServletResponse response = null;
	PrintWriter writer = null;

	UploadServletWorker(HttpServletResponse response) {
	    this.response = response;
	}

	/**
	 * called before upload start
	 */
	public void visitPrepareUpload(UploadHandler handler, HttpServletRequest request)
	    throws IOException, ServletException {
	    response.setContentType("text/html;charset=UTF8");
	    writer = response.getWriter();
	}

	public void visitFileName(UploadHandler handler, HttpServletRequest request, String name, String fileName)
	    throws IOException, ServletException {
	}

	public OutputStream visitPrepareFileSave(UploadHandler handler, HttpServletRequest request, String name, String fileName, String contentType) 
	    throws IOException, ServletException {
	    OutputStream out = null;
	    File file = null;

	    if (fileName != null) {
		String path = handler.getValue(PATH_KEY);

		file = new File(path);
		if (path != null && file.exists() && file.isDirectory()) {
		    file = new File(path, fileName);
		}
	    }

	    if (file != null) {
		out = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
	    }
	    return out;
	}

	public void visitFileSaveFinished(UploadHandler handler, HttpServletRequest request, String name, String fileName, String contentType, int size)
	    throws IOException, ServletException {
	}

	public void visitUploadFinished(UploadHandler handler, HttpServletRequest request)
	    throws IOException, ServletException {
	    try {
		// check if error occurred
		Exception e = handler.getException();
		if (e != null) {
		    printError(e);
		} else {
		    printResult(handler.getUploadDataMap());
		    TeamStat wkStat = (TeamStat) _util.getBean("TeamStat");
		    wkStat.processImportStatsFiles();  
		    this.response.sendRedirect("Controller?page=teamoverview");
		    return;

		}
	    } finally {
		writer.close();
	    }
	}

	private void printError(Exception e) throws IOException {
	    writer.println("<HTML><HEAD>");
	    writer.println("<TITLE>Upload Error</TITLE>");
	    writer.println("</HEAD><BODY>"); 
	    writer.println("<H1>Upload Error</H1>");
	    writer.println("<pre>");
	    e.printStackTrace(writer);
	    writer.println("</pre>");
	    writer.println("</BODY></HTML>");
	}

	private void printResult(Map<String, UploadData> map) throws IOException {
	    Iterator<UploadData> itr = map.values().iterator();

	    writer.println("<HTML><HEAD>");
	    writer.println("<TITLE>Upload Result</TITLE>");
	    writer.println("</HEAD><BODY>"); 
	    writer.println("<H1>Upload Result</H1>");
	    writer.println("<TABLE>");
	    writer.println("<TR><TH>NAME</TH><TH>VALUE</TH><TH>CONTENT TYPE</TH><TH>SIZE</TH><TH>FILE</TH></TR>");
	    while (itr.hasNext()) {
		UploadData data = (UploadData) itr.next();
		writer.println("<TR>");
		writer.println("<TD>" + (data.getName() == null ? "" : data.getName()) + "</TD>");
		writer.println("<TD>" + (data.getValue() == null ? "" : data.getValue()) + "</TD>");
		writer.println("<TD>" + (data.getContentType() == null ? "" : data.getContentType()) + "</TD>");
		writer.println("<TD>" + (data.isFile() ? String.valueOf(data.getSize()) : "") + "</TD>");
		writer.println("<TD>" + (data.isFile() ? "file" : "") + "</TD>");
		writer.println("</TR>");
	    }
	    writer.println("<TR>,<TD><a href='/NABL/Controller?page=main'><IMG bgcolor='#000000' SRC='images/btn_return.gif' align='center'/></a></TD></TR>");
	    writer.println("</TABLE>");
	    writer.println("</BODY></HTML>");
	}

    }

}

