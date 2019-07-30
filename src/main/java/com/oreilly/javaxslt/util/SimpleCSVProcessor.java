package com.oreilly.javaxslt.util;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;

/**
 * Shows how to use the CSVXMLReader class. This is a command-line
 * utility that takes a CSV file and optionally an XSLT file as
 * command line parameters. A transformation is applied and the
 * output is sent to System.out.
 */
public class SimpleCSVProcessor {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java "
                    + SimpleCSVProcessor.class.getName()
                    + " <csvFile> [xsltFile]");
            System.err.println(" - csvFile is required");
            System.err.println(" - xsltFile is optional");
            System.exit(1);
        }

        String csvFileName = args[0];
        String xsltFileName = (args.length > 1) ? args[1] : null;

        TransformerFactory transFact = TransformerFactory.newInstance();
        if (transFact.getFeature(SAXTransformerFactory.FEATURE)) {
            SAXTransformerFactory saxTransFact =
                    (SAXTransformerFactory) transFact;
            TransformerHandler transHand = null;
            if (xsltFileName == null) {
                transHand = saxTransFact.newTransformerHandler();
            } else {
                transHand = saxTransFact.newTransformerHandler(
                        new StreamSource(new File(xsltFileName)));
            }

            // set the destination for the XSLT transformation
            transHand.setResult(new StreamResult(System.out));

            // hook the CSVXMLReader to the CSV file
            CSVXMLReader csvReader = new CSVXMLReader();
            InputSource csvInputSrc = new InputSource(
                    new FileReader(csvFileName));

            // attach the XSLT processor to the CSVXMLReader
            csvReader.setContentHandler(transHand);
            csvReader.parse(csvInputSrc);
        } else {
            System.err.println("SAXTransformerFactory is not supported.");
            System.exit(1);
        }
    }
}
