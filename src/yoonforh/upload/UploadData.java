/*
 * $Id: UploadData.java,v 1.1.1.1 2005/03/10 03:27:15 cmp66 Exp $
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


package yoonforh.upload;


/**
 * upload data class
 *
 * @version  $Revision: 1.1.1.1 $<br>
 *           created at 2002-05-14 18:15:46
 * @author   Yoon Kyung Koo
 */

public class UploadData {
    String name;
    String value;
    String contentType;
    int size;
    boolean isFile;

    public UploadData(String name, String value, String contentType, int size, boolean isFile) {
	this.name = name;
	this.value = value;
	this.contentType = contentType;
	this.size = size;
	this.isFile = isFile;
    }

    public String getName() {
	return name;
    }

    public String getValue() {
	return value;
    }

    public String getContentType() {
	return contentType;
    }

    public int getSize() {
	return size;
    }

    public boolean isFile() {
	return isFile;
    }
}
