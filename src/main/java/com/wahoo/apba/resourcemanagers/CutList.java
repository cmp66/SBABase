package com.wahoo.apba.resourcemanagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultReader;

import com.wahoo.apba.database.PlayerCut;
import com.wahoo.apba.database.PlayerCutRowMapper;

public class CutList
{
	
	private HashMap<Integer, HashMap<Integer, PlayerCut>> _teamCuts = null;
	private HashMap<Integer, PlayerCut> _cuts = null;
	
	private JdbcTemplate jdbcTemplate = null;
	
	private static final String CUTS_QUERY = "select * from playercuts";
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void init()
	{
		clearLists();
		createLists();
	}

	private void clearLists()
	{
		if (null != _teamCuts)
		{
			_teamCuts.clear();
			_teamCuts = null;
		}
		
		if (null != _cuts)
		{
			_cuts.clear();
			_cuts = null;
		}
	}
	
	public Collection<Integer> getCuts()
	{
		return _cuts.keySet();
	}
	
	@SuppressWarnings("unchecked")
	private void createLists()
	{
		List<PlayerCut> wkCuts = (List<PlayerCut>) jdbcTemplate.query(CUTS_QUERY,
				new RowMapperResultReader(new PlayerCutRowMapper()));
		
		createPlayerMap(wkCuts);
		createTeamMap(wkCuts);

	}
	
	public void createPlayerMap(List<PlayerCut> cuts)
	{
		Iterator<PlayerCut> iter = cuts.iterator();
		_cuts = new HashMap<Integer, PlayerCut>();
		
		while (iter.hasNext())
		{
			PlayerCut  cut = (PlayerCut) iter.next();
			
			_cuts.put(Integer.valueOf(cut.getPlayerId()), cut);
			
		}		
	}
	
	public void createTeamMap(List<PlayerCut> playerCuts)
	{
		Iterator<PlayerCut> iter = playerCuts.iterator();
		_teamCuts = new HashMap<Integer, HashMap<Integer, PlayerCut>>();
		
		while (iter.hasNext())
		{
			PlayerCut  cut = (PlayerCut) iter.next();
			
			Integer teamid = Integer.valueOf(cut.getTeamId());
			
			if (_teamCuts.containsKey(teamid))
			{
				HashMap<Integer, PlayerCut> cuts = _teamCuts.get(teamid);
				cuts.put(Integer.valueOf(cut.getPlayerId()), cut);
			}
			else
			{
				HashMap<Integer, PlayerCut> cuts = new HashMap<Integer, PlayerCut>();
				_teamCuts.put(teamid, cuts);
				cuts.put(Integer.valueOf(cut.getPlayerId()), cut);
			}
			
		}		
	}
	
	public int getCutTeam(int playerid)
	{
		int teamid = 0;
		
		PlayerCut cutRecord = (PlayerCut) _cuts.get(Integer.valueOf(playerid));
		
		if (null != cutRecord)
		{
			teamid = cutRecord.getTeamId();
		}
		
		return teamid;
		
	}
	
	public Collection<PlayerCut> getTeamCuts(int teamid)
	{
		Collection<PlayerCut> cuts = new ArrayList<PlayerCut>();
		HashMap<Integer, PlayerCut> teamCuts = _teamCuts.get(Integer.valueOf(teamid));
		
		if (null != teamCuts)
		{
			cuts = teamCuts.values();
		}
		
		return cuts;
		
	}
	
	public void addCut(int playerId, int teamId, int season)
	{
		Integer team = Integer.valueOf(teamId);
		PlayerCut playerCut = new PlayerCut();
		playerCut.setPlayerId(playerId);
		playerCut.setTeamId(teamId);
		playerCut.setSeason(season);
		
		_cuts.put(Integer.valueOf(playerId), playerCut);
		
		if (_teamCuts.containsKey(team))
		{
			HashMap<Integer, PlayerCut> cuts = _teamCuts.get(team);
			cuts.put(Integer.valueOf(playerId), playerCut);
		}
		else
		{
			HashMap<Integer, PlayerCut> cuts = new HashMap<Integer, PlayerCut>();
			_teamCuts.put(team, cuts);
			cuts.put(Integer.valueOf(playerId), playerCut);
		}
		
	}

	

}
