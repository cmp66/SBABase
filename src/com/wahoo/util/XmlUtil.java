package com.wahoo.util;

import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtil
{
    ////
    // METHOD: tag
    ////
    public static String tag( String inString )
    {
        return "<" + inString + ">";
    }

    ////
    // METHOD: tag
    ////
    public static String tag( String inElement, String inString )
    {
        StringTokenizer wkTok = new StringTokenizer( inElement, " " );
        String wkElementOnly = inElement;
        wkElementOnly = wkTok.nextToken();
        return tag( inElement ) + inString + etag( wkElementOnly );
    }

    ////
    // METHOD: etag
    ////
    public static String etag( String inString )
    {
        return "</" + inString + ">";
    }

    ////
    // METHOD: tage
    ////
    public static String tage( String inString )
    {
        return "<" + inString + "/>";
    }

    ////
    // METHOD: checkElement
    ////
    public static void checkElement( String inExpected, String inXmlString )
        throws XmlException
    {
        if ( ! inXmlString.startsWith( "<" + inExpected ) )
            throw new XmlException( inExpected, inXmlString );
        return;
    }

    /**
    * Get one element by tag name.
    * @return An Element object or null if no matching element.
    */
    ////
    // METHOD: getOneElement
    ////
    public static Element getOneElement( Element inParentElement, String inTagName )
    {
        Element retElement = null;
        try
        {
            NodeList wkNodeList = inParentElement.getElementsByTagName( inTagName );
            if ( wkNodeList.getLength() > 0 )
                retElement = (Element) wkNodeList.item( 0 );
        }
        catch ( Exception excp ) { ; }

        return retElement;
    }

    /**
    * Get one element by tag name and attribute value.
    * @return An Element object or null if no matching element.
    */
    ////
    // METHOD: getOneElement
    ////
    public static Element getOneElement(
        Element inParentElement, String inTagName, String inAttrName, String inAttrValue )
    {
        Element retElement = null;
        Element wkElement = null;
        try
        {
            NodeList wkNodeList = inParentElement.getElementsByTagName( inTagName );
            for ( int ix=0; ix < wkNodeList.getLength(); ++ix )
            {
                wkElement = (Element) wkNodeList.item( ix );
                if ( inAttrValue.equals( wkElement.getAttribute( inAttrName ) ) )
                {
                    retElement = wkElement;
                    break;
                }
            }
        }
        catch ( Exception excp ) { ; }

        return retElement;
    }

    ////
    // METHOD: getElementValue
    ////
    public static String getElementValue( Element inElement )
    {
        if ( null == inElement )
            return null;
        Node wkFirstChild = inElement.getFirstChild();
        if ( null == wkFirstChild )
            return "";
        else
            return wkFirstChild.getNodeValue();
    }

    /**
    * Wrap a string in CDATA. You need to do this if the string has characters that have special meaning to XML (such as <, >, or ;).
    * CDATA tells XML not to interpret anything contained within.
    */
    private static final String CDATA_BEGIN = "<![CDATA[";
    private static final String CDATA_END = "]]>";

    private static Vector<String> CDATA_FROM = new Vector<String>();
    private static Vector<String> CDATA_TO = new Vector<String>();
    static
    {
        CDATA_FROM.addElement( CDATA_END );
        CDATA_TO.addElement( "]]" + CDATA_END + CDATA_BEGIN + ">" + CDATA_END + CDATA_BEGIN );
    }

    ////
    // METHOD: cdata
    ////
    public static String cdata( String inString )
    {
        try { return CDATA_BEGIN + Misc.substitute( inString, CDATA_FROM, CDATA_TO ) + CDATA_END; }
        catch ( Exception excp ) { return null; }
    }


    private static Vector<String> XML_FROM = new Vector<String>();
    private static Vector<String> XML_TO = new Vector<String>();
    static
    {
        XML_FROM.addElement( "<" );
        XML_TO.addElement(   "&lt;" );

        XML_FROM.addElement( ">" );
        XML_TO.addElement(   "&gt;" );

        XML_FROM.addElement( "&" );
        XML_TO.addElement(   "&amp;" );

        XML_FROM.addElement( "'" );
        XML_TO.addElement(   "&apos;" );

        XML_FROM.addElement( "\"" );
        XML_TO.addElement(   "&quot;" );
    }

    ////
    // METHOD:
    ////
    static public String toXMLSafeText( String inString )
    {
        try { return Misc.substitute( inString, XML_FROM, XML_TO ); }
        catch ( Exception excp ) { return null; }
    }
}
