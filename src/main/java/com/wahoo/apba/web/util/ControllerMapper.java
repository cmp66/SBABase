package com.wahoo.apba.web.util;


import java.util.Map;

import com.wahoo.apba.web.pagegenerators.IPageGenerator;

/**
 * @author cphillips
 *
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
