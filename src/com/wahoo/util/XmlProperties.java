package com.wahoo.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A properties class with XML as backing store. XML Looks like this:
 * <properties> <head>text</head> <createTimeMillis>date</createTimeMillis>
 * <creator>creator</creator> <section name="section1"> <prop><name>p1</name><value>value
 * 1</value></prop> <prop><name>p2</name><value>value 2</value></prop>
 * </section> <section name="section2"> ... </section> ... </properties>
 */
// //
// CLASS XmlProperties
// //
public class XmlProperties
{
    //
    // Consts for XML tags.
    //
    static final String           _SECTION          = "section";
    static final String           _PROPERTIES       = "properties";
    static final String           _HEADER           = "header";
    static final String           _CREATOR          = "creator";
    static final String           _CREATE_TIME      = "createTimeMillis";
    static final String           _PROPERTY         = "prop";
    static final String           _NAME             = "name";
    static final String           _VALUE            = "value";

    Hashtable<String, Properties> _propertiesHash   = new Hashtable<String, Properties>();
    String                        _header           = "";
    String                        _creator          = "com.teloquent.util.XmlProperties";
    long                          _createTimeMillis = System.currentTimeMillis();

    // //
    // CONSTRUCTOR
    // //
    public XmlProperties()
    {
        return;
    }

    // //
    // CONSTRUCTOR
    // //
    public XmlProperties(String inSection, Properties inProperties)
    {
        _propertiesHash.put(inSection, inProperties);
        return;
    }

    // //
    // CONSTRUCTOR
    // //
    public XmlProperties(Properties inProperties)
    {
        _propertiesHash.put("global", inProperties);
        return;
    }

    // //
    // CONSTRUCTOR
    // //
    @SuppressWarnings("deprecation")
    public XmlProperties(String inXmlString)
    {
        java.io.StringBufferInputStream wkInputStream = null;
        try
        {
            wkInputStream = new java.io.StringBufferInputStream(inXmlString);
            load(wkInputStream);
        } finally
        {
            if (null != wkInputStream)
                try
                {
                    wkInputStream.close();
                } catch (Exception excp)
                {
                    ;
                }
        }
        return;
    }

    // //
    // METHOD: load
    // //
    public void load(InputStream inInputStream)
    {
        decode(inInputStream);
        return;
    }

    public void load(String inFileName) throws IOException
    {
        load(new File(inFileName));
        return;
    }

    // //
    // METHOD: load
    // //
    public void load(File inFile) throws IOException
    {
        BufferedInputStream wkInputStream = null;
        try
        {
            wkInputStream = new BufferedInputStream(new FileInputStream(inFile));
            load(wkInputStream);
            //_configFile = inFile;
        } finally
        {
            if (null != wkInputStream)
                try
                {
                    wkInputStream.close();
                } catch (Exception excp)
                {
                    ;
                }
        }
    }

    // //
    // METHOD: store
    // //
    public void store(OutputStream inOutStream, String inHeader) throws IOException
    {
        setHeader(inHeader);
        encode(inOutStream);
        return;
    }

    public void store(String inFileName, String inHeader) throws IOException
    {
        store(new File(inFileName), inHeader);
        return;
    }

    // //
    // METHOD: store
    // //
    public void store(File inFile, String inHeader) throws IOException
    {
        BufferedOutputStream wkOutputStream = null;
        try
        {
            wkOutputStream = new BufferedOutputStream(new FileOutputStream(inFile));
            store(wkOutputStream, inHeader);
        } finally
        {
            if (null != wkOutputStream)
                try
                {
                    wkOutputStream.close();
                } catch (Exception excp)
                {
                    ;
                }
        }

        return;
    }

    // //
    // METHOD: save
    // //
    public void save(OutputStream inOutputStream, String inHeader)
    {
        try
        {
            store(inOutputStream, inHeader);
        } catch (IOException excp)
        {
            ;
        }
        return;
    }

    // //
    // METHOD: save
    // //
    public void save(String inFileName, String inHeader)
    {
        try
        {
            store(inFileName, inHeader);
        } catch (IOException excp)
        {
            ;
        }
        return;
    }

    public void addSection(String inSection)
    {
        if (!_propertiesHash.containsKey(inSection))
            _propertiesHash.put(inSection, new Properties());
        return;
    }

    public void setSection(String inSection, Properties properties)
    {
        Object old = null;
        if (_propertiesHash.containsKey(inSection))
        {
            old = _propertiesHash.remove(inSection);
            if (old == null)
                System.out.println("huh?!?!?!?!");
        } else
        {
            System.out.println("New Key?!?!?!?!?!");
        }
        _propertiesHash.put(inSection, properties);
    }

    // //
    // METHOD: setProperty
    // //
    public void setProperty(String inKey, String inValue)
    {
        setProperty("global", inKey, inValue);
        return;
    }

    // //
    // METHOD: setProperty
    // //
    public void setProperty(String inSection, String inKey, String inValue)
    {
        addSection(inSection);
        Properties wkProperties = (Properties) _propertiesHash.get(inSection);
        wkProperties.setProperty(inKey, inValue);
        return;
    }

    // //
    // METHOD: getProperty
    // //
    public String getProperty(String inKey)
    {
        return getProperty("global", inKey);
    }

    // //
    // METHOD: getProperty
    // //
    public String getProperty(String inSection, String inKey)
    {
        Properties wkProperties = (Properties) _propertiesHash.get(inSection);
        if (null == wkProperties)
            return null;
        return (String) wkProperties.get(inKey);
    }

    // //
    // METHOD: decode
    // //
    protected void decode(InputStream inInputStream)
    {
        try
        {
            DocumentBuilderFactory wkDocBuilderFactory = DocumentBuilderFactory.newInstance(); // new
                                                                                                // com.sun.xml.parser.DocumentBuilderFactoryImpl();
            DocumentBuilder wkDocBuilder = wkDocBuilderFactory.newDocumentBuilder();
            Document wkDoc = wkDocBuilder.parse(inInputStream);
            Element wkTopElement = wkDoc.getDocumentElement();

            setHeader(XmlUtil.getElementValue(XmlUtil.getOneElement(wkTopElement, _HEADER)));
            _creator = XmlUtil.getElementValue(XmlUtil.getOneElement(wkTopElement, _CREATOR));
            String wkString = XmlUtil.getElementValue(XmlUtil.getOneElement(wkTopElement, _CREATE_TIME));
            if (null != wkString)
                _createTimeMillis = Long.valueOf(wkString).longValue();

            Element wkSectionElement = null;
            NodeList wkSectionNodeList = wkTopElement.getElementsByTagName(_SECTION);
            for (int ix = 0; ix < wkSectionNodeList.getLength(); ++ix)
            {
                wkSectionElement = (Element) wkSectionNodeList.item(ix);
                String wkSectionName = wkSectionElement.getAttribute(_NAME);
                if ("".equals(wkSectionName))
                    wkSectionName = "global";
                if (!_propertiesHash.containsKey(wkSectionName))
                    _propertiesHash.put(wkSectionName, new Properties());
                Properties wkProperties = (Properties) _propertiesHash.get(wkSectionName);
                NodeList wkPropNodeList = wkSectionElement.getElementsByTagName(_PROPERTY);
                for (int jx = 0; jx < wkPropNodeList.getLength(); ++jx)
                {
                    Element wkPropElement = (Element) wkPropNodeList.item(jx);
                    String wkName = wkPropElement.getAttribute(_NAME);
                    String wkValue = wkPropElement.getAttribute(_VALUE);
                    wkProperties.put(wkName, wkValue);
                }
            }
        } catch (Exception excp)
        {
            excp.printStackTrace();
        }

        return;
    }

    // //
    // METHOD: encode
    // //
    protected void encode(OutputStream inOutputStream)
    {
        try
        {
            StringBuffer wkBuff = new StringBuffer();
            wkBuff.append(Const.NEWLINE + "    " + XmlUtil.tag(_HEADER, _header) + Const.NEWLINE);
            wkBuff.append("    " + XmlUtil.tag(_CREATOR, _creator) + Const.NEWLINE);
            wkBuff.append("    " + XmlUtil.tag(_CREATE_TIME, _createTimeMillis + "") + Const.NEWLINE);
            Enumeration<String> wkSectionEnum = _propertiesHash.keys();
            while (wkSectionEnum.hasMoreElements())
            {
                String wkSection = (String) wkSectionEnum.nextElement();
                wkBuff.append("    " + XmlUtil.tag(_SECTION + " " + _NAME + "=\"" + wkSection + "\"") + Const.NEWLINE);
                Properties wkProperties = (Properties) _propertiesHash.get(wkSection);
                Enumeration<Object> wkPropEnum = wkProperties.keys();
                while (wkPropEnum.hasMoreElements())
                {
                    String wkName = (String) wkPropEnum.nextElement();
                    String wkValue = (String) wkProperties.get(wkName);
                    wkBuff.append("        "
                            + XmlUtil.tage(_PROPERTY + " " + _NAME + "=\"" + Misc.fill(wkName + "\"", 22, false, false)
                                    + " " + _VALUE + "=\"" + wkValue + "\"") + Const.NEWLINE);
                }
                wkBuff.append("    " + XmlUtil.etag(_SECTION) + Const.NEWLINE);
            }
            String wkString = XmlUtil.tag(_PROPERTIES, wkBuff.toString()) + Const.NEWLINE;
            BufferedWriter wkWriter = new BufferedWriter(new OutputStreamWriter(inOutputStream));
            wkWriter.write(wkString);
            wkWriter.flush();
        } catch (Exception excp)
        {
            excp.printStackTrace();
        }

        return;
    }

    // //
    // METHOD: setHeader
    // //
    public void setHeader(String inHeader)
    {
        _header = inHeader;
        return;
    }

    // //
    // METHOD: getHeader
    // //
    public String getHeader()
    {
        return _header;
    }

    // //
    // METHOD: getCreator
    // //
    public String getCreator()
    {
        return _creator;
    }

    // //
    // METHOD: getCreateTimeMillis(
    // //
    public long getCreateTimeMillis()
    {
        return _createTimeMillis;
    }

    // //
    // METHOD: getProperties
    // //
    public Properties getProperties(String inSection)
    {
        return (Properties) _propertiesHash.get(inSection);
    }

    // //
    // METHOD: getSections
    // //
    public Enumeration<String> getSections()
    {
        return _propertiesHash.keys();
    }

    // //
    // METHOD: toString
    // //
    public String toString()
    {
        StringBuffer wkBuff = new StringBuffer();
        Enumeration<String> wkKeysEnum = _propertiesHash.keys();
        while (wkKeysEnum.hasMoreElements())
        {
            String wkSection = wkKeysEnum.nextElement();
            wkBuff.append("=== " + wkSection + " ===" + Const.NEWLINE);
            Properties wkProperties = (Properties) _propertiesHash.get(wkSection);
            wkBuff.append(wkProperties.toString() + Const.NEWLINE);
        }

        return wkBuff.toString();
    }
}
