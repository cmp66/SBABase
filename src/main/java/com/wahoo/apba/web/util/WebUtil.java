package com.wahoo.apba.web.util;


import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


public class WebUtil
{

    private HttpServletRequest _request;    //reference to request object
    private boolean _saveFlag = false;	    //set to true internally if a save is needed
    private Hashtable<String, String> _queryParms;			//hashtable of querystrings extracted from request
    
    private WebApplicationContext _appCtx = null;
    private ServletContext _servletCtx = null;


    static private final String DATE_SEPARATOR = "/";
    static private final String TIME_SEPARATOR = ":";

    /**
     constructor for pages that don't have rowsets
     */
    public WebUtil(HttpServletRequest request)
    {
        thisInit(request);
        processParameters();
    }

    /**
     Default constructor
    */
    public WebUtil()
    {}
    
    public void setServletCtx(ServletContext _inCtx)
    {
    	this._servletCtx = _inCtx;
    }


    //process reqest parameters,
    //move form values into named rowsets,
    //add querystring parms to queryParms
    @SuppressWarnings("unchecked")
    private void processParameters()
    {
        Enumeration<String> names;
        String parmName = null;
        String value = null;

        names = _request.getParameterNames();

        //loop on parameters
        while (names.hasMoreElements())
        {
            parmName = (String) names.nextElement();

            //get the value array for this parameter,
            String[] values = _request.getParameterValues(parmName);

            //always grab the 0th element and ignore the others,
            //form field values can be "multivalued"
            value = values[0];
            value = value.trim();

            //if the value is empty, set to null
            if (value.equals(""))
                value = null;

 
            //these should be only query parms
            if (value == null)
                value = "";
            _queryParms.put(parmName, value);
        }

        return;
    }

    /**
     * Common initialization. For use by constructors only.
     */
    protected void thisInit( HttpServletRequest inRequest )
    {
        try
        {
            _request = inRequest;
            _queryParms = new Hashtable<String, String>();

            //HttpSession wkSession = _request.getSession(false/*don't create*/);

        }
        catch (Exception excp)
        {
            excp.printStackTrace();
        }

        return;
    }

    //
    //property gets
    //

    /**
     get all the querystring parms
     */
    public Hashtable<String, String> getQueryParms()
    {
        Hashtable<String, String> copy = new Hashtable<String, String>();
        
        copy.putAll(_queryParms);
        return copy;
    }

    /**
     return user logon name
     */
    public String getUser()
    {
        try
        {
            return "";//_userLogin.getLoginId();
        }
        catch (Exception excp)
        {
            excp.printStackTrace();
            return "";
        }
    }

    /**
     return user id
     */
    public Integer getUserId()
    {
        try
        {
            return Integer.valueOf(0 /*_userLogin.getId()*/);
        }
        catch (Exception excp)
        {
            excp.printStackTrace();
            return Integer.valueOf(-1);
        }
    }



    /**
     return user password
     */
    public String getUserPassword()
    {
        try
        {
            return "";//_userLogin.getPassword();
        }
        catch (Exception excp)
        {
            excp.printStackTrace();
            return "";
        }
    }


    /**
     return user password
     */
    public String getUserDisplayName(Integer inUserId)
    {
        return "";
    }


    /**
     return userlogin object
     */
    //public UserLogin getUserLogin()
    //{
    //    return _userLogin;
    //}

    /**
     return the save flag, indicates a posted form requiring a save
     */
    public boolean getSaveFlag()
    {
        return _saveFlag;
    }


    /**
     Helper functions
     */


    /**
     get a named querystring parm
     */
    public String getQueryParm(String parmName)
    {
        String retValue = "";

        if (_queryParms.containsKey(parmName))
            retValue = (String) _queryParms.get(parmName);

        return retValue;
    }

    //
    //date and time formatting methods, these are for use with Field objects where values are represented as Strings
    //

    /**
     format a numeric timestamp value as mm/dd/yyyy and optionally with time hh:mm:ss
     */
    public static String formatTimestamp(String timestamp, boolean showTime)
    {
        String formatTime = "";
        String dateFormat = "";
        String timeFormat = "";

        dateFormat = formatMsToDate(timestamp);

        //if the format wasn't successful, return the input
        if (dateFormat.equals(""))
            return timestamp;

        if (showTime)
        {
            timeFormat = formatMsToTime(timestamp);

            //if the format wasn't successful, return the input
            if (timeFormat.equals(""))
                return timestamp;
        }

        formatTime = dateFormat + " " + timeFormat;

        return formatTime;
    }


    /**
     format a numeric timestamp value as mm/dd/yyyy hh:mm:ss
     */
    public static String formatTimestamp(String timestamp)
    {
        return formatTimestamp(timestamp, true);
    }

    /**
     format a timestamp expressed in milliseconds (String) into a UI friendly date format (mm/dd/yyyy)
     */
    public static String formatMsToDate(String timeStamp)
    {
        String retDate = "";
        Long lTimeStamp;

        if (timeStamp == null)
            return retDate;

        if (timeStamp.trim().equals(""))
            return retDate;

            //try to format as a long
        try
        {
            lTimeStamp = new Long(timeStamp);
            retDate = formatMsToDate(lTimeStamp.longValue());
        }
        catch (java.lang.NumberFormatException nfe)
        {
            //just return the empty string
            //the value was junk
            return retDate;
        }

        return retDate;
    }

    /**
     format a timestamp expressed in milliseconds (String) into a UI friendly time format (hh:mm:ss)
     */
    public static String formatMsToTime(String timeStamp)
    {
        String retTime = "";
        Long lTimeStamp;

        if (timeStamp == null)
            return retTime;

        if (timeStamp.trim().equals(""))
            return retTime;

            //try to format as a long
        try
        {
            lTimeStamp = new Long(timeStamp);
            retTime = formatMsToTime(lTimeStamp.longValue());
        }
        catch (java.lang.NumberFormatException nfe)
        {
            //just return the empty string
            //the value was junk
            return retTime;
        }

        return retTime;
    }

    /**
     format a timestamp expressed in milliseconds (long) into a UI friendly date format (mm/dd/yyyy)
     */
    public static String formatMsToDate(long timeStamp)
    {
        String retDate = "";
        Calendar formatDate;
        int month;
        int date;
        int year;
        String sMonth;
        String sDate;
        String sYear;

        //create the calendar
        formatDate = Calendar.getInstance();
        formatDate.clear();
        formatDate.setTime(new java.util.Date(timeStamp));

        month = formatDate.get(Calendar.MONTH) + 1;
        date = formatDate.get(Calendar.DATE);
        year = formatDate.get(Calendar.YEAR);

        //add leading zeros
        if (date < 10)
            sDate = "0" + date;
        else
            sDate = "" + date;

        if (month < 10)
            sMonth = "0" + month;
        else
            sMonth = "" + month;

        if (year < 10)
            sYear = "0" + year;
        else
            sYear = "" + year;

        retDate = sMonth + DATE_SEPARATOR + sDate + DATE_SEPARATOR + sYear;

        return retDate;
    }

    /**
     format a timestamp expressed in milliseconds (long) into a UI friendly time format (hh:mm:ss)
     */
    public static String formatMsToTime(long timeStamp)
    {
        return formatMsToTime(timeStamp, false, false);
    }

    /**
     format a timestamp expressed in milliseconds (long) into a UI friendly time format (hh:mm:ss)
     */
    public static String formatMsToTime(long timeStamp, boolean twentryFourHour, boolean showSeconds)
    {
        String retTime = "";
        Calendar formatTime;
        int hour = 0;
        int minute = 0;
        int second = 0;
        String sHour;
        String sMinute;
        String sSecond;
        String amPM = "";

        //create the calendar
        formatTime = Calendar.getInstance();
        formatTime.clear();
        formatTime.setTime(new java.util.Date(timeStamp));

        minute = formatTime.get(Calendar.MINUTE);
        second = formatTime.get(Calendar.SECOND);

        if (!twentryFourHour)
            hour = (formatTime.get(Calendar.HOUR) == 0 ? 12 : formatTime.get(Calendar.HOUR));
        else
            hour = formatTime.get(Calendar.HOUR_OF_DAY);

        if (formatTime.get(Calendar.HOUR_OF_DAY) < 12)
            amPM = "AM";
        else
            amPM = "PM";

            //add leading zeros
        if (hour < 10)
            sHour = "0" + hour;
        else
            sHour = "" + hour;

        if (minute < 10)
            sMinute = "0" + minute;
        else
            sMinute = "" + minute;

        if (second < 10)
            sSecond = "0" + second;
        else
            sSecond = "" + second;

        if (!twentryFourHour)
            retTime = sHour + TIME_SEPARATOR + sMinute + (showSeconds ? TIME_SEPARATOR + sSecond : "") + " " + amPM;
        else
            retTime = sHour + TIME_SEPARATOR + sMinute + (showSeconds ? TIME_SEPARATOR + sSecond : "");

        return retTime;
    }

    /**
     format a UI friendly date format (mm/dd/yyyy) into a timeStamp expressed in milliseconds
     */
    public static String formatDateToMs(String inDate)
    {
        String retValue = null;
        StringTokenizer dateParts;
        String datePart = "";
        Calendar formatDate;
        int pos = 0;
        int month = 0;
        int date = 0;
        int year = 0;
        long timeValue;

        dateParts = new StringTokenizer(inDate, DATE_SEPARATOR);

        if (dateParts.countTokens() > 1)
        {

            try
            {
                //1st token is month, 2nd date, 3rd year
                while (dateParts.hasMoreTokens())
                {
                    datePart = dateParts.nextToken();

                    if (pos == 0) month = new Integer(datePart).intValue();
                    if (pos == 1) date = new Integer(datePart).intValue();
                    if (pos == 2) year = new Integer(datePart).intValue();

                    pos++;
                }

                formatDate = Calendar.getInstance();
                formatDate.clear();
                formatDate.set(year, (month - 1), date);
                timeValue = formatDate.getTime().getTime();
                retValue = Long.valueOf(timeValue).toString();
            }
            catch (java.lang.NumberFormatException nfe)
            {
                //return null for format exception
                retValue = null;
            }
        }

        return retValue;
    }



    /**
     set http headers common to all pages in the application
     */
    public void setHeaders(HttpServletResponse response)
    {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "0");
    }

    //
    //Debug methods
    //

    /**
     print all the request parameters in an html table
     */

    @SuppressWarnings("unchecked")
    public String printParameters()
    {
        Enumeration<String> names;
        String parmName;
        String results;

        names = _request.getParameterNames();

        results = "<table border='1'><th>Parameter Name</th><th>Parameter Value</th>";
        while (names.hasMoreElements())
        {
            results += "<tr>";
            parmName = names.nextElement();
            results += "<td>" + parmName + "</td>";
            String[] values = _request.getParameterValues(parmName);

            results += "<td>";
            for (int i = 0; i < values.length; i++)
            {
                results += values[i];
            }
            results += "</td>";
            results += "</tr>";
        }
        results += "</table>";

        return results;
    }

    @SuppressWarnings("unchecked")
    static public String printParameters(HttpServletRequest request)
    {
        Enumeration<String> names;
        String parmName;
        String results;

        names = request.getParameterNames();

        results = "<table border='1'><th>Parameter Name</th><th>Parameter Value</th>";
        while (names.hasMoreElements())
        {
            results += "<tr>";
            parmName = names.nextElement();
            results += "<td>" + parmName + "</td>";
            String[] values = request.getParameterValues(parmName);

            results += "<td>";
            for (int i = 0; i < values.length; i++)
            {
                results += values[i];
            }
            results += "</td>";
            results += "</tr>";
        }
        results += "</table>";

        return results;
    }

    /**
     print the query string value pairs in an html table
     */
    public String printQueryParms()
    {
        String results;
        Enumeration<String> enumeration;
        String valueName;

        results = "<table border='1'><th>Parm Name</th><th>Parm Value</th>";

        enumeration = _queryParms.keys();

        while (enumeration.hasMoreElements())
        {
            valueName = (String) enumeration.nextElement();

            results += "<tr>";
            results += "<td>" + valueName + "</td>";
            results += "<td>" + _queryParms.get(valueName) + "</td>";

            results += "</tr>";
        }

        results += "</table>";

        return results;
    }


    /**
     write a message to the system console
     */
    static public void println(String message)
    {
        System.out.println(message);
    }
    
    /*  Spring related stuff */
    
    public Object getBean(String inName)
    {
    	if (_appCtx == null)
    	{
    		_appCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(_servletCtx);
    	}
    	
    	return _appCtx.getBean(inName);
    }

}
