
package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class CardedPlayer implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;
	private int id;
    private String playername;
    private String mlbteam;
    private int playerid;

    public CardedPlayer()
    {
    }
    
    public CardedPlayer(int id, String playername, String mlbteam, int playerid) 
    {
        this.id = id;
        this.playername = playername;
        this.mlbteam = mlbteam;
        this.playerid = playerid;

    }

    public CardedPlayer(ResultSet inRecord) 
    {
        try
        {
            this.id = inRecord.getInt("id");
            this.playername = inRecord.getString("playername");
            this.mlbteam = inRecord.getString("mlbteam");
            this.playerid = inRecord.getInt("playerid");

        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
        }
    }
    
    public int getId()
    {
        return this.id;
    }

	public String getPlayername() {
		return playername;
	}

	public void setPlayername(String playername) {
		this.playername = playername;
	}

	public String getMlbteam() {
		return mlbteam;
	}

	public void setMlbteam(String mlbteam) {
		this.mlbteam = mlbteam;
	}

	public int getPlayerid() {
		return playerid;
	}

	public void setPlayerid(int playerid) {
		this.playerid = playerid;
	}
    
    
    
}
    