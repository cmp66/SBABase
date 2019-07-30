/*
 * Created on Nov 7, 2005
 *
 */
package com.wahoo.apba.resourcemanagers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.springframework.jdbc.core.*;

import com.wahoo.apba.database.RotoPlayerRecord;
import com.wahoo.apba.database.RotoWireMissingRowMapper;
import com.wahoo.apba.database.RotoWireRowMapper;

/**
 * @author cphillips
 * 
 */
public class RotoPlayerManager
{

	private JdbcTemplate jdbcTemplate = null;

	private static final String MISSING_GET = "select * from rotowiremissing";
	private static final String PLAYER_NEWS_CHECK = "select * from rotowire where player = ? order by reportdate desc";

	private static HashMap<String, String> _monthMap;
	static
	{
		_monthMap = new HashMap<String, String>();
		_monthMap.put(Calendar.JANUARY + "", "Jan");
		_monthMap.put(Calendar.FEBRUARY + "", "Feb");
		_monthMap.put(Calendar.MARCH + "", "Mar");
		_monthMap.put(Calendar.APRIL + "", "Apr");
		_monthMap.put(Calendar.MAY + "", "May");
		_monthMap.put(Calendar.JUNE + "", "Jun");
		_monthMap.put(Calendar.JULY + "", "Jul");
		_monthMap.put(Calendar.AUGUST + "", "Aug");
		_monthMap.put(Calendar.SEPTEMBER + "", "Sep");
		_monthMap.put(Calendar.OCTOBER + "", "Oct");
		_monthMap.put(Calendar.NOVEMBER + "", "Nov");
		_monthMap.put(Calendar.DECEMBER + "", "Dec");
	}

	public RotoPlayerManager()
	{
	}

	public void setJdbcTemplate(JdbcTemplate inTemplate)
	{
		this.jdbcTemplate = inTemplate;
	}

	@SuppressWarnings("unchecked")
	public void updateMissing()
	{
		List wkMissing = jdbcTemplate.query(MISSING_GET, new RowMapperResultReader(new RotoWireMissingRowMapper()));
		Iterator wkIter = wkMissing.iterator();

		while (wkIter.hasNext())
		{
			RotoPlayerRecord wkRecord = (RotoPlayerRecord) wkIter.next();
			wkRecord.setJdbcTemplate(jdbcTemplate);
			if (wkRecord.insert())
			{
				wkRecord.deleteMissing();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Element getPlayerNews(int inId)
	{
		Element wkNews = new Element("PlayerNews");
		
		List wkMissing = jdbcTemplate.query(PLAYER_NEWS_CHECK,
											new Object[] {Integer.valueOf(inId)},
											new RowMapperResultReader(new RotoWireRowMapper()));
		Iterator wkIter = wkMissing.iterator();
		
		int wkCount = 1;	
		while (wkCount < 6 && wkIter.hasNext())
		{
			RotoPlayerRecord wkRecord = (RotoPlayerRecord) wkIter.next();
			Element wkEntry = new Element("NewsEntry");
			wkNews.addContent(wkEntry);
			wkEntry.addContent(new Element("name").setText(wkRecord.getPlayerName()));
			wkEntry.addContent(new Element("team").setText(wkRecord.getTeam()));
				
			Calendar wkDate = wkRecord.getDate();
			wkEntry.addContent(new Element("date").setText((String)_monthMap.get(wkDate.get(Calendar.MONTH)+"") 
					                                               + " " + wkDate.get(Calendar.DAY_OF_MONTH) 
					                                               + " " + wkDate.get(Calendar.YEAR)));
			wkEntry.addContent(new Element("news").setText(wkRecord.getNews()));
			wkEntry.addContent(new Element("comment").setText(wkRecord.getComment()));				

		}
		
		return wkNews;

	}}
