/*
 * Created on Mar 14, 2005
 *
 */
package com.wahoo.util;

import java.util.Properties;

/**
 * @author cphillips
 *
 */
public class WebProperties {
	
	static Properties _props = null;
	
	public static void setWebProperties(Properties inProps)
	{
		_props = inProps;
	}
	
	public static Properties getWebProperties()
	{
		return _props;
	}
	
    private void loadProperties() throws Exception
    {     

        
        //System.getProperties().put("CurrentStatsSeason", wkProps.getProperty("CurrentStatsSeason"));
        //System.getProperties().put("CurrentStandingsSeason", wkProps.getProperty("CurrentStandingsSeason"));
        //System.getProperties().put("CurrentTransactionsSeason", wkProps.getProperty("CurrentTransactionsSeason"));
        //System.getProperties().put("PrimaryDataSource", wkProps.getProperty("PrimaryDataSource"));
        //System.getProperties().put("AltDataSource", wkProps.getProperty("AltDataSource"));
        //System.getProperties().put("IPQual", wkProps.getProperty("IPQual"));
        //System.getProperties().put("ABQual", wkProps.getProperty("ABQual"));
        //System.getProperties().put("EnterOtherStats", wkProps.getProperty("EnterOtherStats"));
        //System.out.println("Get other stats is " + (String)System.getProperties().get("EnterOtherStats"));
    }

}
