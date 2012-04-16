/*
 * $Id: UploadHandler.java,v 1.1.1.1 2005/03/10 03:27:15 cmp66 Exp $
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
 *
 * HISTORY
 *  first release ver 1.0 1998/07/14
 *		first working version
 *  revision 1.0a 1998/11/06
 * 		change Vector with Hashtable to enhance performance
 *  revision 1.0z 1999/03/06
 *		add work around of a bug
 *			in JSDK 2.0's javax.servlet.ServletInputStream.readLine() method
 *  revision 1.01 2001/11/19
 *		fix a bug, instance variables would be shared
 *		by all the servlet calls, so should be avoided
 *			thanks to IsmaelCRamos@aol.com
 *  revision 1.1 2002/02/23
 *      add multiple file support
 *      and handle characters escaped into integers (support filenames in unicode)
 *  revision 1.1a 2002/02/26
 *      fix some performance issues
 *  revisioin 1.1b 2002/06/11
 *      added open source license terms 
 *  revisioin 1.1c 2004/01/10
 *      fix textarea problem
 *      re-indented accordint to the Java Coding Convention rule (Tab : 8sp, Indent : 4sp)
 */


package yoonforh.upload;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Category;

/**
 * an upload request handler<br>
 * refer to  http://www.rfc.net/rfc1867.html
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2002-05-14 17:40:03
 * @author   Yoon Kyung Koo
 */

public class UploadHandler {
    private static Category logger = null;
    private final static int BUFFER_SIZE = 8192;

    private UploadWorker worker = null;
    private HashMap<String,UploadData> map = null;
    private Exception exception = null;

    static {
	logger = Category.getInstance("upload.handler");
    }

    public UploadHandler(UploadWorker worker) {
	this.worker = worker;
	map = new HashMap<String,UploadData>();
	exception = null;
    }

    @SuppressWarnings("unused")
    public void upload(HttpServletRequest request) throws ServletException {
	try {
	    worker.visitPrepareUpload(this, request);

	    long start = System.currentTimeMillis();
	    //String fileLocation = null;
	    //int contentLength = request.getContentLength();

	    // RFC 1867 
	    String contentType = request.getContentType();
	    if (contentType == null) {
		logger.info("Upload : content type missing");
		throw new UploadFailureException("content type missing");
	    }
	    int ind = contentType.indexOf("boundary=");
	    if (ind == -1) {
		logger.info("Upload : IND is less than 0");
		throw new UploadFailureException("IND is less than 0");
	    }
	    String boundary = contentType.substring(ind + 9);

	    if (boundary == null) {
		logger.info("Upload : boundary is null");
		throw new UploadFailureException("boundary is null");
	    }

	    String boundaryString = "--" + boundary;
	    ServletInputStream in = request.getInputStream();

	    byte[] buffer = new byte[BUFFER_SIZE];
	    //HashMap destFileMap = new HashMap();
	    //Vector threadVector = new Vector(3);

	    int result = in.readLine(buffer, 0, BUFFER_SIZE);

	outer:
	    while (true) {
		if (result <= 0) {
		    logger.info("Upload : stream truncated error.");
		    throw new UploadFailureException("Error. Stream truncated");
		}

		String line = new String(buffer, 0, result);
	
		if (!line.startsWith(boundaryString)) {
		    /*
		     * XXX : the XXXXing MSIE 5.0
		     * XXX : often forgot the boundary string which he gave in content type
		     * XXX : so, this is a work around
		     */
		    if (line.startsWith("--")) {
			logger.info("Upload : fucking MSIE 5 bug. multipart boundary missing. line = " + line
				    + ", while boundaryString = " + boundaryString);
			boundaryString = line.substring(0, boundaryString.length());
		    } else {
			logger.info("Upload : error. multipart boundary missing. line = " + line
				    + ", while boundaryString = " + boundaryString);
			throw new UploadFailureException("Error. multipart boundary missing.");
		    }
		}

		// check boundary end tag
		if (line.substring(boundaryString.length()).startsWith("--")) {
		    break;
		}

		result = in.readLine(buffer, 0, BUFFER_SIZE);
		if (result <= 0) {
		    logger.debug("Upload : may be end boundary which has no contents");
		    break;
		}

		line = new String(buffer, 0, result);
		StringTokenizer tokenizer = new StringTokenizer(line, ";\r\n");
		String token = tokenizer.nextToken();
		String upperToken = token.toUpperCase();
		if (!upperToken.startsWith("CONTENT-DISPOSITION")) {
		    logger.info("Upload : format error. Content-Disposition expected.");
		    throw new UploadFailureException("Format error. Content-Disposition expected.");
		}

		String disposition = upperToken.substring(21);
		if (!disposition.equals("FORM-DATA")) {
		    logger.info("Upload : I don't know how to handle ["
				+ disposition + "] disposition.");
		    throw new UploadFailureException("Sorry, I don't know how to handle ["
						     + disposition + "] disposition.");
		}
		if (tokenizer.hasMoreElements()) {
		    token = tokenizer.nextToken();
		} else {
		    logger.info("Upload : format error. NAME expected.");
		    throw new UploadFailureException("Format error. NAME expected.");
		}
		int nameStart = token.indexOf("name=\"");
		int nameEnd = token.indexOf("\"", nameStart + 7);
		if (nameStart < 0 || nameEnd < 0) {
		    logger.info("Upload : format error. NAME expected.");
		    throw new UploadFailureException("Format error. NAME expected.");
		}
		String name = token.substring(nameStart + 6, nameEnd);

		if (tokenizer.hasMoreElements()) {
		    String filename = null;
		    int fnStart, fnEnd;
		    //File tmpFile = null;
		    //File destFile = null;
		    String fileContentType = null;
		    OutputStream fout = null;
		    int size = 0;

		    fnStart = line.indexOf("filename=\"");
		    if (fnStart < 0) { // filename term missing
			logger.debug("NO FILENAME given.");
			result = in.readLine(buffer, 0, BUFFER_SIZE);
			continue;
		    }

		    fnEnd = line.indexOf("\"", fnStart + 11);
		    if (fnEnd < 0) {
			logger.debug("FILENAME is null.");
		    } else {
			filename = line.substring(fnStart + 10, fnEnd);
			int lastindex = -1;
			if ((lastindex = filename.lastIndexOf('/')) < 0) {
			    lastindex = filename.lastIndexOf('\\');
			}
			if (lastindex >= 0) {
			    filename=filename.substring(lastindex + 1);
			}
			filename = processEscape(filename);
		    }

		    if (filename != null) {
			worker.visitFileName(this, request, name, filename);
		    }

		    result = in.readLine(buffer, 0, BUFFER_SIZE);
		    if (result <= 0) {
			logger.info("Upload : error. stream truncated after reading name");
			throw new UploadFailureException("Error. Stream truncated");
		    }

		    fileContentType = new String(buffer, 0, result);
		    if (fileContentType.toUpperCase().startsWith("CONTENT-TYPE:")) {
			fileContentType = fileContentType.substring(13).trim();
		    } else {
			logger.debug("Upload : what should I read here ??? - result = " + result
				    + ", and read [" + new String(buffer, 0, result)
				    + "]");
		    }

		    try {
			byte[] tmpbuffer1 = buffer;
			byte[] tmpbuffer2 = new byte[BUFFER_SIZE];
			byte[] tmpbuffer = tmpbuffer2;
			int tmpbufferlen = 0;
			boolean isFirst = true;
			boolean odd = true;
			//Thread t = null;

		    inner:
			while ((result = in.readLine(buffer, 0, BUFFER_SIZE)) > 0) {
			    if (isFirst) { // ignore all proceeding \r\n
				if (result == 2 && buffer[0] == '\r' && buffer[1] == '\n') {
				    continue;
				}

				fout = worker.visitPrepareFileSave(this, request, name, filename, fileContentType);
			    }

			    if (bytesStartsWith(buffer, 0, result, boundaryString)) {
				if (!isFirst) {
				    size += tmpbufferlen - 2;
				    if (fout != null) {
					fout.write(tmpbuffer, 0, tmpbufferlen - 2);
				    }
				}

				worker.visitFileSaveFinished(this, request, name, filename, fileContentType, size);
				continue outer;
			    } else {
				if (!isFirst) {
				    size += tmpbufferlen;
				    if (fout != null) {
					fout.write(tmpbuffer, 0, tmpbufferlen);
				    }
				}
			    }

			    if (odd) {
				buffer = tmpbuffer2;
				tmpbuffer = tmpbuffer1;
			    } else {
				buffer = tmpbuffer1;
				tmpbuffer = tmpbuffer2;
			    }
			    odd = !odd;

			    tmpbufferlen = result;
			    isFirst = false;
			}
		    } catch (IOException ie) {
			logger.info("Upload : io exception - " + ie.getMessage(), ie);
			throw new UploadFailureException("IO Error while write to file : " + ie.toString());
		    } finally {
			logger.debug("Upload : size = " + size);
			if (size > 0) {
			    appendValue(name, filename, fileContentType, size);
			}

			if (fout != null) {
			    fout.close();
			}
		    }
		    result = in.readLine(buffer, 0, BUFFER_SIZE);
		    logger.debug("Upload : what should I read here? - result = " + result
				+ ", and read [" + new String(buffer, 0, result)
				+ "]");
		} else { // no more elements
		    result = in.readLine(buffer, 0, BUFFER_SIZE);
		    if (result <= 0) {
			logger.debug("Upload : error. stream truncated 2");
			throw new UploadFailureException("Error. Stream truncated");
		    }

		    StringBuffer valueBuffer = new StringBuffer();
		    while (true) {
			result = in.readLine(buffer, 0, BUFFER_SIZE);
			if (result <= 0) {
			    logger.debug("Upload : error. stream truncated 3");
			    throw new UploadFailureException("Error. Stream truncated");
			    // break outer;
			}

			if (bytesStartsWith(buffer, 0, result, boundaryString)) {
			    break;
			}
			valueBuffer.append(new String(buffer, 0, result));
		    }
		    valueBuffer.setLength(valueBuffer.length() - 2); // exclude last \r\n
		    appendValue(name, valueBuffer.toString());
		    continue;
		}

		result = in.readLine(buffer, 0, BUFFER_SIZE);
	    } // end of while

	    long end = System.currentTimeMillis();
	    logger.debug("Upload : uploading took " + (end - start) + " (ms)");
	} catch (IOException e) {
	    logger.info("Upload : exception occurred. " + e.getMessage(), e);
	    exception = e;
	} finally {
	    try {
		worker.visitUploadFinished(this, request);
	    } catch (IOException ie) {
		logger.info("Upload : exception occurred while finishing upload. " + ie.getMessage(), ie);
		exception = ie;
	    }
	}
    }

    /**
     * get exception which occurred during upload
     */
    public Exception getException() {
    	return exception;
    }

    /**
     * set exception which occurred during upload
     */
    public void setException(Exception exception) {
    	this.exception = exception;
    }

    /**
     * get parameter value
     *
     * @param name parameter name
     */
    public String getValue(String name) {
    	UploadData data = (UploadData) map.get(name);
    	if (data == null) {
	    return null;
	}
    	return data.getValue();
    }

    /**
     * get upload parameters map
     *
     * @return UploadData map of which keys are parameter names
     */
    public Map<String, UploadData> getUploadDataMap() {
	return map;
    }

    private boolean bytesStartsWith(byte[] bytes, int offset, int length, String toCompare) {
	boolean result = true;
	if (toCompare.length() > length) {
	    return false;
	}

	for (int i = toCompare.length() - 1; i >= 0; i--) {
	    if (toCompare.charAt(i) != bytes[offset + i]) {
		result = false;
		break;
	    }
	}

	return result;
    }

    private void appendValue(String name, String value, String contentType, int size) {
    	UploadData data = new UploadData(name, value, contentType, size, true);
    	map.put(name, data);
    }

    private void appendValue(String name, String value) {
    	UploadData data = new UploadData(name, value, null, 0, false);
    	map.put(name, data);
    }

    private final static int NORMAL = 0;
    private final static int AMPERSAND = 1;
    private final static int AMPERSHARP = 2;
    /**
     * process html escape characters (&#NNNN;)
     */
    private String processEscape(String string) {
	StringBuffer buffer = new StringBuffer(string.length());
	char[] chars = string.toCharArray();
	StringBuffer escaped = new StringBuffer(6);
	int status = NORMAL;

	for (int i = 0; i < string.length(); i++) {
	    switch (status) {
	    case NORMAL :
		if (chars[i] == '&') {
		    status = AMPERSAND;
		} else {
		    buffer.append(chars[i]);
		}
		break;

	    case AMPERSAND :
		if (chars[i] == '#') {
		    status = AMPERSHARP;
		} else {
		    status = NORMAL;
		    buffer.append('&');
		}
		break;

	    case AMPERSHARP :
		if (chars[i] == ';') {
		    try {
			buffer.append((char) Integer.parseInt(escaped.toString()));
		    } catch (NumberFormatException nfe) {
			// I don't handle other Entities
			buffer.append(escaped);
			buffer.append(';');
		    }
		    escaped.setLength(0);
		    status = NORMAL;
		} else {
		    escaped.append(chars[i]);
		}
		break;
	    }
	}

	if (escaped.length() > 0) {
	    buffer.append(escaped);
	}

	return buffer.toString();
    }

}
