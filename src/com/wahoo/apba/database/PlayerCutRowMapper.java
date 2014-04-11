package com.wahoo.apba.database;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;



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
