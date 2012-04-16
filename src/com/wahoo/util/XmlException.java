package com.wahoo.util;

////
// CLASS: XmlException
////
public class XmlException extends Exception
{
	private static final long serialVersionUID = 1L;

	////
    // CONSTRUCTOR
    ////
    public XmlException()
    {
        super();
        return;
    }

    ////
    // CONSTRUCTOR
    ////
    public XmlException( String inString )
    {
        super( inString );
        return;
    }

    ////
    // CONSTRUCTOR
    ////
    public XmlException( String inExpected, String inFound )
    {
        super( "Expected: \"" + inExpected + "\" Found: \"" + inFound + "\"" );
        return;
    }
}
