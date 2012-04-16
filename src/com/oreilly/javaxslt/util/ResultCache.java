package com.oreilly.javaxslt.util;

import java.io.*;
import java.util.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/**
 * A utility class that caches XSLT transformation results in memory.
 *
 * @author Eric M. Burke
 */
public class ResultCache {
    private static Map<MapKey, MapValue> cache = new HashMap<MapKey, MapValue>();

    /**
     * Flush all results from memory, emptying the cache.
     */
    public static synchronized void flushAll() {
        cache.clear();
    }

    /**
     * Perform a single XSLT transformation.
     */
    public static synchronized String transform(String xmlFileName,
            String xsltFileName) throws TransformerException {

        MapKey key = new MapKey(xmlFileName, xsltFileName);

        File xmlFile = new File(xmlFileName);
        File xsltFile = new File(xsltFileName);

        MapValue value = (MapValue) cache.get(key);
        if (value == null || value.isDirty(xmlFile, xsltFile)) {
            // this step performs the transformation
            value = new MapValue(xmlFile, xsltFile);
            cache.put(key, value);
        }

        return value.result;
    }

    // prevent instantiation of this class
    private ResultCache() {
    }

    /////////////////////////////////////////////////////////////////////
    // a helper class that represents a key in the cache map
    /////////////////////////////////////////////////////////////////////
    static class MapKey {
        String xmlFileName;
        String xsltFileName;

        MapKey(String xmlFileName, String xsltFileName) {
            this.xmlFileName = xmlFileName;
            this.xsltFileName = xsltFileName;
        }

        public boolean equals(Object obj) {
            if (obj instanceof MapKey) {
                MapKey rhs = (MapKey) obj;
                return this.xmlFileName.equals(rhs.xmlFileName)
                        && this.xsltFileName.equals(rhs.xsltFileName);
            }
            return false;
        }

        public int hashCode() {
            return this.xmlFileName.hashCode() ^ this.xsltFileName.hashCode();
        }
    }

    /////////////////////////////////////////////////////////////////////
    // a helper class that represents a value in the cache map
    /////////////////////////////////////////////////////////////////////
    static class MapValue {
        long xmlLastModified;  // when the XML file was modified
        long xsltLastModified;  // when the XSLT file was modified
        String result;

        MapValue(File xmlFile, File xsltFile) throws TransformerException {
            this.xmlLastModified = xmlFile.lastModified();
            this.xsltLastModified = xsltFile.lastModified();

            TransformerFactory transFact = TransformerFactory.newInstance();
            Transformer trans = transFact.newTransformer(
                    new StreamSource(xsltFile));

            StringWriter sw = new StringWriter();
            trans.transform(new StreamSource(xmlFile), new StreamResult(sw));

            this.result = sw.toString();
        }

        /**
         * @return true if either the XML or XSLT file has been
         * modified more recently than this cache entry.
         */
        boolean isDirty(File xmlFile, File xsltFile) {
            return this.xmlLastModified < xmlFile.lastModified()
                    || this.xsltLastModified < xsltFile.lastModified();
        }
    }
}
