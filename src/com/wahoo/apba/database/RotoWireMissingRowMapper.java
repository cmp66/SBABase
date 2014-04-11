package com.wahoo.apba.database;


import org.springframework.jdbc.core.*;
import java.sql.ResultSet;
import java.util.Calendar;
import java.sql.Date;
import java.sql.SQLException;

/*
 * Created on Nov 8, 2005
 *
 */

/**
 * @author cphillips
 *
 */
public class RotoWireMissingRowMapper implements RowMapper 
{

	public Object mapRow(ResultSet rs, int index) throws SQLException
	{
		Calendar wkCalendar = Calendar.getInstance();
		wkCalendar.setTime(new Date(rs.getTimestamp("reportdate").getTime()));
		RotoPlayerRecord wkRecord = new RotoPlayerRecord(rs.getString("playername"),
				                                         wkCalendar,
														 "",
														 "",
														 rs.getString("news"),
														 rs.getString("comment")
														 );
		
		return wkRecord;
	}

}
