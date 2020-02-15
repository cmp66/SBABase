/*
 * Created on Mar 26, 2005
 *
 */
package com.wahoo.apba.database;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author cphillips
 *
 */
public class RotoPlayerRecord 
{
	
	JdbcTemplate jdbcTemplate  = null;
	
	public RotoPlayerRecord()
	{
		init();
	}
	
	public RotoPlayerRecord(String inName,
			                Calendar inDate,
							String inPosition,
							String inTeam,
							String inNews,
							String inComment)
	{
		_name = inName;
		_date = inDate;
		_position = inPosition;
		_team = inTeam;
		_news = inNews;
		_comment = inComment;
	}

	
	private String _name;
	private Calendar _date;
	private String _position;
	private String _news;
	private String _comment;
	private String _team;
	

	private static HashMap<String, String> _monthMap;
	static {
		_monthMap = new HashMap<String, String>();
		_monthMap.put(Calendar.JANUARY+"", "Jan");
		_monthMap.put(Calendar.FEBRUARY+"", "Feb");
		_monthMap.put(Calendar.MARCH+"", "Mar");
		_monthMap.put(Calendar.APRIL+"", "Apr");
		_monthMap.put(Calendar.MAY+"", "May");
		_monthMap.put(Calendar.JUNE+"", "Jun");
		_monthMap.put(Calendar.JULY+"", "Jul");
		_monthMap.put(Calendar.AUGUST+"", "Aug");
		_monthMap.put(Calendar.SEPTEMBER+"", "Sep");
		_monthMap.put(Calendar.OCTOBER+"", "Oct");
		_monthMap.put(Calendar.NOVEMBER+"", "Nov");
		_monthMap.put(Calendar.DECEMBER+"", "Dec");
	}
	
	private static final String PLAYER_LOOKUP = "select id from players where displayname = ?";
	private static final String DUPE_CHECK = "select player from rotowire where player = ? and  news = ? and comment = ?";
	private static final String INSERT_NEW = "insert into rotowire (player, mlbteam, reportdate, news, comment) values (?,?,?,?,?)";
	private static final String INSERT_MISSING = "insert into rotowiremissing (playername, mlbteam, reportdate, news, comment) values (?,?,?,?,?)";
	private static final String DUPE_MISSING_CHECK = "select playername from rotowiremissing where playername = ? and  news = ? and comment = ?";
	
	private static final String MISSING_DELETE = "delete from rotowiremissing where playername = ? and news = ? and comment = ?";
	

	private void init()
	{
		_name = "";
		_date  = Calendar.getInstance();
		_position = "";
		_news = "";
		_comment = "";
		_team = "";
		
		//_date.set(Calendar.YEAR, 2005);
	}

	public void setJdbcTemplate(JdbcTemplate inTemplate)
	{
		this.jdbcTemplate = inTemplate;
	}
	
	public String getPlayerName()
	{
		return this._name;
	}
	
	public String getPosition()
	{
		return this._position;
	}
	
	public String getNews()
	{
		return this._news;
	}
	
	public String getComment()
	{
		return this._comment;
	}
	
	public String getTeam()
	{
		return this._team;
	}
	
	public Calendar getDate()
	{
		return this._date;
	}
	
	public void parsePlayerName(String inLine)
	{
		//System.out.println("Parsing Player name " + inLine);
		StringTokenizer wkParser = new StringTokenizer(inLine, " ");
		
		String wkToken = wkParser.nextToken();
		while (!wkToken.startsWith("("))
		{
			_name += " " + wkToken;
			wkToken = wkParser.nextToken();
			
		}
		
		_name = _name.trim();
		_position = getPosition(wkToken);
		_team = wkParser.nextToken();
		
		// skip the -
		if (!_team.equals("-"))
			wkParser.nextToken();
		
		modifyMonth(wkParser.nextToken());
		modifyDate(wkParser.nextToken());
	}
	
	public String getPosition(String inPos)
	{
		String wkPos = "Unknown";
		
		if (inPos.equals("(P)"))
			wkPos = "P";
		else if (inPos.equals("(1B)"))
			wkPos = "1B";
		else if (inPos.equals("(2B)"))
			wkPos = "2B";
		else if (inPos.equals("(3B)"))
			wkPos = "3B";
		else if (inPos.equals("(SS)"))
			wkPos = "SS";
		else if (inPos.equals("(C)"))
			wkPos = "C";
		else if (inPos.equals("(OF)"))
			wkPos = "OF";
		else if (inPos.equals("(DH)"))
			wkPos = "DH";
		
		return wkPos;
			
	}
	
	public void modifyMonth(String inMonth)
	{
		if (inMonth.equals("Jan."))
			_date.set(Calendar.MONTH, Calendar.JANUARY);
		else if (inMonth.equals("Feb."))
			_date.set(Calendar.MONTH, Calendar.FEBRUARY);
		else if (inMonth.equals("Mar."))
			_date.set(Calendar.MONTH, Calendar.MARCH);
		else if (inMonth.equals("Apr."))
			_date.set(Calendar.MONTH, Calendar.APRIL);
		else if (inMonth.equals("May."))
			_date.set(Calendar.MONTH, Calendar.MAY);
		else if (inMonth.equals("Jun."))
			_date.set(Calendar.MONTH, Calendar.JUNE);
		else if (inMonth.equals("Jul."))
			_date.set(Calendar.MONTH, Calendar.JULY);
		else if (inMonth.equals("Aug."))
			_date.set(Calendar.MONTH, Calendar.AUGUST);
		else if (inMonth.equals("Sep."))
			_date.set(Calendar.MONTH, Calendar.SEPTEMBER);
		else if (inMonth.equals("Oct."))
			_date.set(Calendar.MONTH, Calendar.OCTOBER);
		else if (inMonth.equals("Nov."))
			_date.set(Calendar.MONTH, Calendar.NOVEMBER);
		else if (inMonth.equals("Dec."))
			_date.set(Calendar.MONTH, Calendar.DECEMBER);
		else
			System.out.println("Unknown Month " + inMonth);
	}
	
	public void modifyDate(String inDay)
	{
		int wkDay;
		
		try
		{
			wkDay = Integer.parseInt(inDay);
			_date.set(Calendar.DAY_OF_MONTH, wkDay);
		}
		catch (NumberFormatException e)
		{
			System.out.println("Invalid Day of Month: " + inDay);
		}
	}
	
	public void addNews(String inNews)
	{
		/*
		if ( !(_news.endsWith(" ") || inNews.startsWith(" ")) && !_news.equals("") )
		{
			_news = _news + " " + inNews;
		}
		else
		{
			_news += inNews;
		}
		*/
		_news += inNews;
		//_news = _news.trim();
	}
	
	public void addComment(String inComment)
	{
		/*
		if ( !(_comment.endsWith(" ") || inComment.startsWith(" ")) && !_comment.equals("") )
		{
			_comment = _comment + " " + inComment;
		}
		else
		{
			_comment += inComment;
		}
		*/
		
		_comment += inComment;
		
		//_comment = _comment + inComment;
		//_comment = _comment.trim(); 
	}
	
	public String toString()
	{
		StringBuffer wkBuffer = new StringBuffer(1024);
		wkBuffer.append(_date.getTime().toString()).append("\r\n");
		wkBuffer.append(_name).append("  ").append(_position).append("  ").append(_team).append("\r\n");
		wkBuffer.append("NEWS: ").append(_news).append("\r\n");
		wkBuffer.append("COMMENT: ").append(_comment).append("\r\n");
		
		return wkBuffer.toString();
	}
	
	public boolean insert()
	{
		boolean wkInsertedNew = false;
		
		try
		{
			Integer wkId = (Integer) jdbcTemplate.queryForObject(PLAYER_LOOKUP, 
											       new Object[] { _name },
												   Integer.class);
			System.out.println("Inserting New Record");
			wkInsertedNew = insertNewRecord(wkId.intValue());
		}
		catch (IncorrectResultSizeDataAccessException e)
		{
			System.out.println("Inserting Missing Record");
			insertMissingRecord();
		}
		return wkInsertedNew;
		
	}
	
	private boolean insertNewRecord(int inId)
	{
		boolean wkNew = false;
		
		try
		{
		
			Integer wkId = (Integer) jdbcTemplate.queryForObject(DUPE_CHECK, 
			       							       new Object[] { Integer.valueOf(inId),
									                              _news,
																  _comment},
												   Integer.class);
			if (null != wkId)
			{
				System.out.println("Duplicate entry for " + _name);
				wkNew = true;
			}
			else
			{
				System.out.println("unique new record");
				jdbcTemplate.update(INSERT_NEW,
							    new Object[] { Integer.valueOf(inId),
											   _team,
											   new Timestamp(_date.getTime().getTime()),
											   _news,
											   _comment });
				wkNew = true;
			}
		}
		catch (IncorrectResultSizeDataAccessException e)
		{
			// this will mean it found 0 occurences
			jdbcTemplate.update(INSERT_NEW,
				    new Object[] { Integer.valueOf(inId),
								   _team,
								   new Timestamp(_date.getTime().getTime()),
								   _news,
								   _comment });
			wkNew = true;
			
		}
		return wkNew;
	}
	
	private void insertMissingRecord()
	{
		try
		{
			String wkName = (String) jdbcTemplate.queryForObject(DUPE_MISSING_CHECK,
					                                new Object[] {_name, _news, _comment },
													String.class);
		
		
			if (null != wkName)
			{
				System.out.println("Duplicate Missing entry for " + _name);
			}
			else
			{
				System.out.println("unique missing record");
				jdbcTemplate.update(INSERT_MISSING,
								new Object[] { _name,
											   _team,
											   new Timestamp(_date.getTime().getTime()),
											   _news,
											   _comment});
			}
		}
		catch (IncorrectResultSizeDataAccessException e)
		{
			// this will mean it found 0 occurences
			jdbcTemplate.update(INSERT_MISSING,
					new Object[] { _name,
								   _team,
								   new Timestamp(_date.getTime().getTime()),
								   _news,
								   _comment});
			
		} 
	}
	
	public void deleteMissing()
	{
		jdbcTemplate.update(MISSING_DELETE,
						    new Object[] { _name, _news, _comment });
	}
	

}
