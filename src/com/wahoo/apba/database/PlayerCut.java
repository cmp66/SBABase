package com.wahoo.apba.database;

public class PlayerCut
{

	private int teamId = 0;
	private int playerId = 0;
	private int season = 0;
	
	public int getPlayerId()
	{
		return playerId;
	}
	public void setPlayerId(int playerId)
	{
		this.playerId = playerId;
	}
	public int getSeason()
	{
		return season;
	}
	public void setSeason(int season)
	{
		this.season = season;
	}
	public int getTeamId()
	{
		return teamId;
	}
	public void setTeamId(int teamId)
	{
		this.teamId = teamId;
	}
	
	
}
