package com.wahoo.apba.database;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/*
 * 
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author cphillips
 *
 */
public class PlayerCutRowMapper implements RowMapper 
{

	public Object mapRow(ResultSet rs, int index) throws SQLException
	{

		PlayerCut wkRecord = new PlayerCut();
		wkRecord.setPlayerId(rs.getInt("playerid"));
		wkRecord.setSeason(rs.getInt("season"));
		wkRecord.setTeamId(rs.getInt("teamid"));
		
		return wkRecord;
	}

}
