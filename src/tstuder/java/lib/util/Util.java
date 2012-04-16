package tstuder.java.lib.util;

/*
 * This is a class containing various static utility methods.
 *
 * Copyright (C) 1999 Thomas Studer
 * mailto:tstuder@datacomm.ch
 * http://www.datacomm.ch/tstuder
 *
 * This class is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This class is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this class; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.util.StringTokenizer;
import java.util.Calendar;
import java.util.BitSet;
import java.math.BigDecimal;

/**
 * A utility Class containing various static methods.
 *
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 * 
 */
public class Util {
/**
 * Concatenates two arrays.
 *
 * @return A new array containing the result from concatenating 'array1' to
 * 'array2'.
 *
 * @param array1 The first array.
 * @param array2 The second array.
 */
public static String[] concat( String[] array1, String[] array2 ) {
   String[] result = new String[ array1.length + array2.length ];
   System.arraycopy ( array1, 0, result, 0, array1.length ); 
   System.arraycopy ( array2, 0, result, array1.length, array2.length ); 
   return result;
}
/**
 * Format a BigDecimal, grouping thousands and rounding to a specified number
 * of decimal digits.
 * 
 * @return The formatted BigDecimal.
 *
 * @param value The BigDecimal to format.
 * @param thousandSep The separator for groups of thousands to be used.
 * @param decimalPoint The character for the decimal point to be used.
 * @param numDecimals The number of decimal places. Uses ROUND_HALF_UP if
 * necessary. If numDecimals is smaller than 0,
 * 'value' is output with the number of decimal places according to its scale.
 */
public static String formatBigDecimal( BigDecimal value, char thousandSep, 
   char decimalPoint, int numDecimals ) {

   if (numDecimals >= 0 && value.scale() != numDecimals) {
	  value = value.setScale( numDecimals, BigDecimal.ROUND_HALF_UP );
   }
   
   String stringValue = value.toString();
   int origLen = stringValue.length();
   int decPoint = stringValue.indexOf( '.' );
   int thousandSepCount;
   boolean hasDecPoint;
   boolean isNegative;
   int mag;
   
   if (decPoint < 0) {
	  decPoint = origLen;
	  hasDecPoint = false;
   } else {
	  // Eliminate trailing zeros
	  hasDecPoint = true;
	  if (numDecimals == -1) {
		 while (stringValue.charAt( origLen - 1 ) == '0') {
			origLen--;
		 }
		 if (stringValue.charAt( origLen - 1 ) == '.') {
			origLen--;
			hasDecPoint = false;
		 }  
	  }     
   }
   
   if (stringValue.charAt( 0 ) == '-') {
	  isNegative = true;      
	  thousandSepCount = (decPoint-2) / 3;
	  mag = decPoint - 2;
   } else {
	  isNegative = false;
	  thousandSepCount = (decPoint-1) / 3;
	  mag = decPoint - 1;
   }  
   
   StringBuffer buf = new StringBuffer( origLen + thousandSepCount );
   int srcIndex = 0;

   if (isNegative) { 
	  srcIndex++;
	  buf.append( '-' );
   }  
   
   // Kopiere Vorkommastellen und fuege Tausendertrenner ein wo noetig.
   while (srcIndex < decPoint) {
	  buf.append( stringValue.charAt( srcIndex ) );
	  srcIndex++;
	  if (mag > 0 && mag % 3 == 0) buf.append( thousandSep );
	  mag--;
   }
   
   // Kopiere Nachkommastellen
   if (hasDecPoint) {
	  srcIndex++;
	  buf.append( decimalPoint );
	  while (srcIndex < origLen) {
		 buf.append( stringValue.charAt( srcIndex ) );
		 srcIndex++;
	  }
   }

   return buf.toString();
}
/**
 * Calculate a hash code on a String a la Perl.
 *
 * @return The hash code.
 * @param s The String to hash.
 */
public static int hashString( String s ) {

	int hash = 1;

	for (int i=0; i<s.length(); i++) {
		hash *= 33 + s.charAt( i );
	}

	return hash;
}
/**
 * Insert a range of bit positions into a BitSet.
 *
 * @return A Copy of 'fromSet' with bits in the specified range
 * inserted (in cleared state).
 * @param fromSet The BitSet into which bit positions are to be inserted.
 * @param insertBefore The index above which the new bit positions are to
 * be created.
 * @param count The number of new bit positions to be created.
 */
public static BitSet insertBits( BitSet fromSet, int insertBefore, int count ) {

	BitSet newSet = new BitSet( fromSet.size() + count );

	for (int i=0; i<insertBefore; i++) {
		if (fromSet.get( i )) newSet.set( i );
	}

	for (int i=insertBefore; i<fromSet.size(); i++) {
		if (fromSet.get( i )) newSet.set( i + count );
	}

	return newSet;
}
/**
 * Test if a String is a valid date and, if it is, create a Calendar object 
 * corresponding to that date.
 *
 * @return A Calendar object if 's' contains a valid date, <code>null</code> otherwise. 
 * 
 * @param s A String containing a date formatted as <br>
 * 'dd.mm.yy' or 'dd.mm.yyyy' (i.e. German date notation) or <br> 
 * 'mm/dd/yy' or 'mm/dd/yyyy' (i.e. US date notation)
 *
 * <p>Note: if only two digits for the year are passed, the year is 
 * interpreted as follows:
 * If yy < 99  --> the year becomes 20yy.<br>
 * If yy == 99 --> the year becomes 1999.<br>
 * <b>Thus, two-digit year entry is only supported for the years 1999 through 2098.</b>
 */
public static Calendar parseGregorianDate( String s ) {

   final int   daysInMonth[] = new int[] 
		{ 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
   char        dateDelimiter = '?';
   String      tag, monat, jahr;
   int         t, m, j;

   try {
	  //
	  // Eigenes Datumsparsing weil SimpleDateFormat.parse() zum Teil
	  // ungueltig lange Jahrzahlen akzeptiert (d.h. sie faelschlicherweise als gueltig
	  // anerkannt).
	  // Auch hatten wir Probleme mit den set() Methoden von Calendar die offenbar
	  // (entgegen unserer JDK 1.1 Doku)
	  // keine IllegalArgumentExceptions werfen.
	  //

	  // Determine delimiter character
	  for (int i=0; i<s.length(); i++) {
		 if (! Character.isDigit( s.charAt( i ))) {
			dateDelimiter = s.charAt( i );
			break;
		 }
	  }
	  if (dateDelimiter != '.' && dateDelimiter != '/') return null;
			   
	  // Get date components.
	  StringTokenizer toks = new StringTokenizer( s, "" + dateDelimiter );
	  if (toks.countTokens() != 3) return null;
	  
	  if (dateDelimiter == '.') {
		 tag = toks.nextToken();
		 monat = toks.nextToken();
	  } else {
		 monat = toks.nextToken();
		 tag = toks.nextToken();
	  }
	  jahr = toks.nextToken();
   
	  // only 'yy' or 'yyyy' format for year allowed.
	  if ( ! (jahr.length() == 2 || jahr.length() == 4)) return null;

	  // Parse date components into integer values.
	  j = Integer.parseInt( jahr );
	  m = Integer.parseInt( monat );
	  t = Integer.parseInt( tag );
	  
	  // Expand two digit year entry to four digits
	  if (j < 99 && jahr.length() == 2)         j += 2000;
	  else if (j == 99 && jahr.length() == 2) j += 1900;

	  // Validate date
	  if (j < 1) return null;
	  if (m < 1 || m > 12) return null;
	  if (t < 1 || t > daysInMonth[m-1]) return null;
	  
	  if (m == 2 && t == 29) {
	  
		 // Leap-year handling
		 boolean leapYear = (j % 400 == 0) || (j % 100 != 0 && j % 4 == 0);
		 
		 if (! leapYear) return null;
	  }
	  
   } catch (Exception e) {
	  return null; 
   }     

   // Create and return a Calendar object representing the date.     
   Calendar c = Calendar.getInstance();
   c.set( j, m-1, t );
   return c;
}
/**
 * Remove a range of bit positions from a BitSet.
 *
 * @return A Copy of 'fromSet' with the bits in the specified range
 * removed.
 * @param fromSet The BitSet from which bit positions are to be removed.
 * @param startIndex The index of the first bit to remove.
 * @param pastIndex The index of the bit just below the last one to remove.
 */
public static BitSet removeBits( BitSet fromSet, int startIndex, int pastIndex ) {

	int skipCount = pastIndex - startIndex;
	BitSet newSet = new BitSet( fromSet.size() - skipCount );

	for (int i=0; i<startIndex; i++) {
		if (fromSet.get( i )) newSet.set( i );
	}

	for (int i=pastIndex; i<fromSet.size(); i++) {
		if (fromSet.get( i )) newSet.set( i - skipCount );
	}

	return newSet;
}
}
