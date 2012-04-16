package com.wahoo.util;

import java.util.StringTokenizer;
import java.util.Vector;
import java.net.URL;
import java.io.*;

public class Misc
{
    static public final String BLANKS = "                                                         ";

    ////
    // METHOD: concatDir
    ////
    public static String concatDir( String inDir, String inRest )
    {
        if ( inDir.lastIndexOf( Const.FILE_SEPARATOR ) == inDir.length() - 1 )
            return inDir + inRest;
        else
            return inDir + Const.FILE_SEPARATOR + inRest;
    }

    ////
    // METHOD: fill
    ////
    static public String fill(
        String inString, int inLen, boolean inLeftFill, boolean inTruncate, String inFill )
    {
        String retString;

        if ( inString.length() >= inLen )
        {
            if ( inTruncate )
                retString = inString.substring( 0, inLen );
            else
                retString = inString;
        }
        else
        {
            // Fill.
            String wkFill = makeFill( inLen, inFill );
            if ( inLeftFill )
                retString = wkFill.substring( 0, inLen - inString.length() ) + inString;
            else
                retString = inString + wkFill.substring( 0, inLen - inString.length() );
        }

        return retString;
    }

    ////
    // METHOD: fill
    ////
    static public String fill( String inString, int inLen, boolean inLeftFill, boolean inTruncate )
    {
        return fill( inString, inLen, inLeftFill, inTruncate, BLANKS );
    }

    ////
    // METHOD: fill
    ////
    static public String fill( String inString, int inLen, boolean inLeftFill )
    {
        return fill( inString, inLen, inLeftFill, true /*truncate*/, BLANKS );
    }

    ////
    // METHOD: fill
    ////
    static public String fill( String inString, int inLen, String inFill )
    {
        return fill( inString, inLen, false /*leftFill*/, true /*truncate*/, inFill );
    }

    ////
    // METHOD: makeFill
    ////
    static public String makeFill( int inLen, String inFill )
    {
        String retFill = inFill;

        if ( inLen > retFill.length() )
        {
            StringBuffer wkBuff = new StringBuffer( inFill );
            char wkFillChar = inFill.charAt( inFill.length() - 1 );
            for ( int wkCount = wkBuff.length(); wkCount <= inLen; ++wkCount )
                wkBuff.append( wkFillChar );
            retFill = wkBuff.toString();
        }

        return retFill;
    }

    ////
    // METHOD: setStringBuffer
    ////
    public static void setStringBuffer( StringBuffer inBuff, String inString )
    {
        inBuff.setLength( 0 );
        inBuff.append( inString );
        return;
    }

    ////
    // METHOD: boxedString
    ////
    public static String boxedString( String inString )
    {
        StringBuffer wkBuff = new StringBuffer();
        int wkLen = inString.length();
        String wkHorizontal = fill( "", wkLen + (3 * 2), true, true, "=====" ) + Const.NEWLINE ;
        wkBuff.append( wkHorizontal );
        wkBuff.append( "== " + inString + " ==" + Const.NEWLINE );
        wkBuff.append( wkHorizontal );

        return wkBuff.toString();
    }

    ////
    // METHOD: boxedString
    ////
    public static String boxedString( String[] inStrings )
    {
        StringBuffer wkBuff = new StringBuffer();
        int wkLen = 0;
        for ( int ix = 0; ix < inStrings.length; ++ix )
        {
            if ( inStrings[ ix ].length() > wkLen )
                wkLen = inStrings[ ix ].length();
        }
        String wkHorizontal = fill( "", wkLen + (3 * 2), true, true, "=====" ) + Const.NEWLINE;

        wkBuff.append( wkHorizontal );
        for ( int ix = 0; ix < inStrings.length; ++ix )
        {
            wkBuff.append(
                "== " + fill( inStrings[ ix ], wkLen, false, false ) + " ==" + Const.NEWLINE );
        }
        wkBuff.append( wkHorizontal );

        return wkBuff.toString();
    }

    ////
    // METHOD: quote
    ////
    public static String quote( String inString )
    {
    	return "\"" + inString + "\"";
    }

    ////
    // METHOD: parseHttpStatus
    ////
    public static int parseHttpStatus( String inHttpResponse )
    {
        int wkStatus = -1;
        StringTokenizer wkTok = new StringTokenizer( inHttpResponse, " \r\n" );
        try
        {
            wkTok.nextToken();
            wkStatus = Integer.parseInt( wkTok.nextToken() );
        }
        catch ( Exception excp ) { ; };
        return wkStatus;
    }

    ////
    // METHOD: trim
    ////
    public static String trim( String inString )
    {
        if ( null == inString )
            return null;
        return inString.trim();
    }

    /**
    * Make a full URL string from a URL string relative to a base.
    */
    ////
    // METHOD: makeUrl
    ////
    public static String makeUrl( URL inBaseUrl, String inUrl )
    {
        URL wkUrl = null;
        try
        {
            wkUrl = new URL( inBaseUrl, inUrl );
        }
        catch ( Exception excp )
        {
            return null;
        }

        return ( null == wkUrl ) ? null : wkUrl.toString();

        /*
        StringBuffer wkUrl = new StringBuffer();
        if ( 0 == inUrl.indexOf( "http://" ) )
            wkUrl.append( inUrl );
        else
        {
            wkUrl.append(
                wkUrl.getProtocol() + "://" +
                wkUrl.getHost() + ":" +
                wkUrl.getPort() );
            if ( '/' == inUrl.charAt( 0 ) )
                wkUrl.append( inUrl );
            else if ( 0 == inUrl.indexOf( "./" ) )
                wkUrl.append( inUrl.substring( 1 ) );
            else
                wkUrl.append( wkPrefix + inUrl );
        }
        return wkUrl.toString();
        */
    }

    /**
    * Insertion sort of a String Array.
    */
    ////
    // METHOD: StringArraySort
    ////
    public static String[] stringArraySort( String inArray[] )
    {
        for ( int ix1 = 1; ix1 < inArray.length; ++ix1 )
        {
            // a[0..i-1] is sorted
            // insert a[i] in the proper place
            String wkString = inArray[ ix1 ];
            int ix2;
            for ( ix2 = ix1 - 1; ix2 >= 0; --ix2 )
            {
                if ( inArray[ ix2 ].compareTo( wkString ) <= 0 )
                    break;
                inArray[ ix2 + 1 ] = inArray[ ix2 ];
            }
            // now a[0..j] are all <= wkString
            // and a[j+2..i] are > wkString
            inArray[ ix2 + 1 ] = wkString;
        }
        return inArray;
    }

    ////
    // METHOD: substitute
    ////
    public static String substitute( String inString, Vector<String> inFrom, Vector<String> inTo )
        throws Exception
    {
        return substitute(
            inString,
            (String[]) inFrom.toArray( new String[ inFrom.size() ] ),
            (String[]) inTo.toArray( new String[ inTo.size() ] ) );
    }

    ////
    // METHOD: substitute
    ////
    public static String substitute( String inString, String[] inFrom, String[] inTo )
        throws IllegalArgumentException
    {
        if ( null == inString || 0 == inFrom.length || 0 == inTo.length )
            return inString;

        if ( inFrom.length != inTo.length )
            throw new IllegalArgumentException( "From not same length as to." );

        int wkFirst = Integer.MAX_VALUE;
        int wkLast = -1;
        int wkCurr = -1;
        int ix = 0;
        for ( ix = 0; ix < inFrom.length; ++ix ) // Find first and last matches, if any.
        {
            if ( -1 != ( wkCurr = inString.indexOf( inFrom[ ix ] ) ) )
            {
                if ( wkCurr < wkFirst )
                    wkFirst = wkCurr;
                wkCurr = inString.lastIndexOf( inFrom[ ix ] );
                if ( wkCurr > wkLast )
                    wkLast = wkCurr;
            }
        }
        if ( -1 == wkLast ) // If no matches.
            return inString;

        StringBuffer wkResultBuffer = new StringBuffer();

        wkResultBuffer.append( inString.substring( 0, wkFirst ) );
        next:
        for ( ix = wkFirst; ix <= wkLast; /*nop*/ )
        {
            String wkCurrent = inString.substring( ix );
            for ( int jx = 0; jx < inFrom.length; ++jx )
            {
                String wkFrom = inFrom[ jx ];
                if ( 0 == wkFrom.length() )
                    break;
                if ( wkCurrent.startsWith( wkFrom ) ) // If match.
                {
                    wkResultBuffer.append( inTo[ jx ] );
                    ix += ( (String) inFrom[ jx ] ).length();
                    continue next;
                }
            }
            wkResultBuffer.append( wkCurrent.charAt( 0 ) ); // No match.
            ++ix;
        }
        wkResultBuffer.append( inString.substring( ix ) );

        return wkResultBuffer.toString();
    }

    ////
    // METHOD: addArrayToVector
    ////
    public static void addArrayToVector( Object[] inArray, Vector<Object> inVec )
    {
        if ( null == inArray || null == inVec )
            throw new IllegalArgumentException( "Array and vector must be non-null." );

        for ( int ix = 0; ix < inArray.length; ++ix )
            inVec.addElement( inArray[ ix ] );

        return;
    }

    public static String getString( Object inObject )
    {
        return getString( inObject, "" );
    }

    public static String getString( Object inObject, String inDefault )
    {
        return ( null == inObject ) ? inDefault : inObject.toString();
    }

    public static Integer stringToInteger( String inString ) throws NumberFormatException
    {
        return Integer.valueOf( Long.decode( inString ).intValue() );
    }

    public static String[] stringToArray( String inString, String inSeperator )
    {
        StringTokenizer     wkTokenizer = new StringTokenizer( inString, inSeperator );
        String[]            wkArray = new String[ wkTokenizer.countTokens() ];
        int ix = 0;
        while ( wkTokenizer.hasMoreTokens() )
        {
            wkArray[ ix++ ] = wkTokenizer.nextToken();
        }
        return wkArray;
    }

    public static long fileCopy( File inFrom, File inTo ) throws IOException
    {
        Closer wkCloser = new Closer();
        long wkTotal = 0;
        try
        {
            BufferedReader wkReader = new BufferedReader( new FileReader( inFrom ) );
            wkCloser.add( wkReader );
            BufferedWriter wkWriter = new BufferedWriter( new FileWriter( inTo ) );
            wkCloser.add( wkWriter );
            char[] wkBuff = new char[ 65 * 1000 ];
            int wkLen = -1;
            while ( -1 < ( wkLen = wkReader.read( wkBuff ) ) )
            {
                wkWriter.write( wkBuff, 0, wkLen );
                wkWriter.flush();
                wkTotal += wkLen;
            }
        }
        finally
        {
            wkCloser.close();
        }
        return wkTotal;
    }

    public static int substringCount( String pString, String pSubstring )
    {
        if ( null == pString || 0 == pString.length()
        || null == pSubstring || 0 == pSubstring.length() )
            throw new IllegalArgumentException( "Both string and substring must be non-null and non-zero length." );

        int wkSubLen = pSubstring.length();
        int wkCount = -1;
        int ix = 0;
        do
        {
            ix = pString.indexOf( pSubstring, ix ) + wkSubLen;
            ++wkCount;
        }
        while ( ix >= wkSubLen );
        return wkCount;
    }
    
	/**
	 *  Calls Integer.parseInt(String),  but catches NumberFormatException
	 */
    public static int safeStringToInt(String inString)  
    {
        if (inString == null) return 0;
        inString = inString.trim();
        if (inString.length() == 0)	return 0;


    	int wkIntVal = 0;        
 		try
 		{
 			wkIntVal = Integer.parseInt(inString);
 		}
 		catch (NumberFormatException nfe)
 		{
 			// no-op, just return wkIntVal initialized with zero	
 		}
 		return wkIntVal;
    } 
 
	/**
	 *  Calls Integer.valueOf(String),  but catches NumberFormatException
	 */
    public static Integer safeStringToInteger(String inString)  
    {
    	Integer wkIntVal = Integer.valueOf(0);   
        
        if (null == inString) return wkIntVal;
        
        inString = inString.trim();
        if (0 == inString.length())	return wkIntVal;
     
 		try
 		{
 			wkIntVal = Integer.valueOf(inString);
 		}
 		catch (NumberFormatException nfe)
 		{ 
 			// no-op, just return wkIntVal initialized with zero
 		}
 		return wkIntVal;
    }  
        
    
	/**
	 *  Calls Long.parseLong(String),  but catches NumberFormatException
	 */
    public static long safeStringTolong(String inString)  
    {
        if (null == inString) return 0;
        inString = inString.trim();
        if (0 == inString.length())	return 0;


    	long wkLongVal = 0;        
 		try
 		{
 			wkLongVal = Long.parseLong(inString);
 		}
 		catch (NumberFormatException nfe)
 		{
 			// no-op, just return wkLongVal initialized with zero	
 		}
 		return wkLongVal;
    } 
 
	/**
	 *  Calls Long.valueOf(String),  but catches NumberFormatException
	 */
    public static Long safeStringToLong(String inString)  
    {
    	Long wkLongVal = Long.valueOf(0);   
        
        if (null == inString) return wkLongVal;
        
        inString = inString.trim();
        if (0 == inString.length())	return wkLongVal;
     
 		try
 		{
 			wkLongVal = Long.valueOf(inString);
 		}
 		catch (NumberFormatException nfe)
 		{ 
 			// no-op, just return wkLongVal initialized with zero
 		}
 		return wkLongVal;
    }     
}
