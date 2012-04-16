package com.oreilly.javaxslt.util;


import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;


public class DOMUtil {
//    private static final String IDENTITY_XSLT =
//        "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform'"
//        + " version='1.0'>"
//        + "<xsl:template match='/'><xsl:copy-of select='.'/>"
//        + "</xsl:template></xsl:stylesheet>";

    public static void debugDocument(Document doc) {
        System.out.println("Document Summary");
        System.out.println("  obj = " + ((Object) doc).toString());
        System.out.println("  localName = '" + doc.getLocalName() + "'");
        System.out.println("  namespaceURI = '" + doc.getNamespaceURI() + "'");
        System.out.println("  documentElement = " + doc.getDocumentElement());
        System.out.println("  implementation = " + doc.getImplementation());
        System.out.println("  nodeType = " + doc.getNodeType());
        System.out.println("  ownerDocument = " + doc.getOwnerDocument());

        NodeList nl = doc.getChildNodes();
        int size = nl.getLength();
        for (int i=0; i<size; i++) {
            System.out.println("    : " + nl.item(i).getLocalName());

            Node curNode = nl.item(i);
            System.out.println("-------------");
            System.out.println(curNode.getLocalName());
            System.out.println("namespace URI: '" + curNode.getNamespaceURI() + "'");
            System.out.println(curNode.getNodeType());
            System.out.println(curNode.hasChildNodes());
            System.out.println("-------------");

            NodeList nl2 = nl.item(i).getChildNodes();
            for (int j=0; j<nl2.getLength(); j++) {
                System.out.println("    ... " + nl2.item(j).getLocalName());
            }
        }

    }

    /**
     * Convert a DOM tree into a String.
     */
    public static String domToString(Document domDoc)
            throws TransformerException, TransformerConfigurationException {
         TransformerFactory transFact = TransformerFactory.newInstance();
         Transformer trans = transFact.newTransformer();
         trans.setOutputProperty(OutputKeys.INDENT, "yes");
         StringWriter sw = new StringWriter();
         Result result = new StreamResult(sw);

         try {
         trans.transform(new DOMSource(domDoc), result);
         } catch (TransformerException te) {
            System.out.println(te.getMessageAndLocation());
            throw te;
         }
         return sw.toString();
    }

    private DOMUtil() {
    }
}