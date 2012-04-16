package com.wahoo.util;

public class ExceptionFormatter
{
    public ExceptionFormatter()
    {;}
    
    public static String getStackTrace(Exception e)
    {
        StringBuffer wkEx = new StringBuffer(1024);
        
        wkEx.append(e.toString()).append("\r\n");
        
        StackTraceElement[] wkNestings = e.getStackTrace();
        
        for (int i = 0; i < wkNestings.length; i++)
        {
            wkEx.append(wkNestings[i].toString()).append("\r\n");
        }
        
        return wkEx.toString();
    }
}
