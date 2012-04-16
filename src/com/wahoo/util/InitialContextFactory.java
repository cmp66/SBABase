package com.wahoo.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Context;


/**
 * @author Carl Phillips
 */
public class InitialContextFactory
{
    private InitialContextFactory ()
    {}
    
    /**
     * @return Initial Context for Jboss server
     *
     */    
    public static Context getAltJBossInitialContext ()
    {
        InitialContext lclContext = null;
        
        try
        {
            lclContext = new InitialContext ();
        }
        catch (NamingException ne)
        {
            ne.printStackTrace ();
        }
        
        return lclContext;
    }
    
    public static Context getJBossInitialContext ()
    {
        InitialContext lclInitContext = null;
        Context lclContext = null;
        
        try
        {
            lclInitContext = new InitialContext ();
            lclContext = (Context) lclInitContext.lookup("java:comp/env");
        }
        catch (NamingException ne)
        {
            ne.printStackTrace ();
        }
        
        return lclContext;
    }
}


