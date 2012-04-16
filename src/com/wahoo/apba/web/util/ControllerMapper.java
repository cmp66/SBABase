package com.wahoo.apba.web.util;


import java.util.Map;

import com.wahoo.apba.web.pagegenerators.IPageGenerator;

/**
 * @author cphillips
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ControllerMapper {

	public Map<String, IPageGenerator> pageMap = null;
	
	public void setPageMap(Map<String, IPageGenerator> inMap)
	{
		pageMap = inMap;
	}
	
	public Map<String, IPageGenerator> getPageMap()
	{
		return pageMap;
	}
	
}
