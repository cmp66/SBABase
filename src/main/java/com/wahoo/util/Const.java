package com.wahoo.util;

public interface Const
{
    String NEWLINE = System.getProperty( "line.separator" );
    String FILE_SEPARATOR = System.getProperty( "file.separator" );
    String PATH_SEPARATOR = System.getProperty( "path.separator" );

    String MIME_TYPE_PLAIN_TEXT     = "text/plain";
    String MIME_TYPE_XML_TEXT       = "text/xml";
    String MIME_TYPE_HTML_TEXT      = "text/html";

    String MIME_TYPE_URL_ENCODED    = "application/x-www-form-urlencoded";
    String MIME_TYPE_JAVA_CLASS     = "application/java-vm";
    String MIME_TYPE_OCTET_STREAM   = "application/octet-stream";

    String MIME_TYPE_GIF            = "image/gif";
    String MIME_TYPE_JPEG           = "image/jpeg";

    String MIME_TYPE_DEFAULT        = "default";
}
