package com.wahoo.apba.database;

import java.sql.Connection;

public interface GenericStatRecord {

	public abstract void createRecord();

	public abstract void createRecord(Connection inConn);

	public abstract void updateRecord();

	public abstract void addSeriesToTotals(SeriesStatRecord inRecord);

	public abstract int getPlayerid();

	public abstract int getSeason();

	public abstract int getTeamid();

	public abstract int getGames();

	public abstract int getBat_ab();

	public abstract int getBat_runs();

	public abstract int getBat_hits();

	public abstract int getBat_rbi();

	public abstract int getBat_hr();

	public abstract int getBat_doubles();

	public abstract int getBat_triples();

	public abstract int getBat_walks();

	public abstract int getBat_strikeouts();

	public abstract int getBat_sb();

	public abstract int getBat_cs();

	public abstract int getBat_hbp();

	public abstract int getErrors();

	public abstract int getPitch_gp();

	public abstract int getPitch_gs();

	public abstract int getPitch_cg();

	public abstract int getPitch_sho();

	public abstract int getPitch_wins();

	public abstract int getPitch_loss();

	public abstract int getPitch_save();

	public abstract int getPitch_ipfull();

	public abstract int getPitch_ipfract();

	public abstract int getPitch_hits();

	public abstract int getPitch_runs();

	public abstract int getPitch_er();

	public abstract int getPitch_walks();

	public abstract int getPitch_strikeouts();

	public abstract int getPitch_hr();

	public abstract void setPlayerid(int inVal);

	public abstract void setSeason(int inVal);

	public abstract void setTeamid(int inVal);

	public abstract void setGames(int inVal);

	public abstract void setBat_ab(int inVal);

	public abstract void setBat_runs(int inVal);

	public abstract void setBat_hits(int inVal);

	public abstract void setBat_rbi(int inVal);

	public abstract void setBat_hr(int inVal);

	public abstract void setBat_doubles(int inVal);

	public abstract void setBat_triples(int inVal);

	public abstract void setBat_walks(int inVal);

	public abstract void setBat_strikeouts(int inVal);

	public abstract void setBat_sb(int inVal);

	public abstract void setBat_cs(int inVal);

	public abstract void setBat_hbp(int inVal);

	public abstract void setErrors(int inVal);

	public abstract void setPitch_gp(int inVal);

	public abstract void setPitch_gs(int inVal);

	public abstract void setPitch_cg(int inVal);

	public abstract void setPitch_sho(int inVal);

	public abstract void setPitch_wins(int inVal);

	public abstract void setPitch_loss(int inVal);

	public abstract void setPitch_save(int inVal);

	public abstract void setPitch_ipfull(int inVal);

	public abstract void setPitch_ipfract(int inVal);

	public abstract void setPitch_hits(int inVal);

	public abstract void setPitch_runs(int inVal);

	public abstract void setPitch_er(int inVal);

	public abstract void setPitch_walks(int inVal);

	public abstract void setPitch_strikeouts(int inVal);

	public abstract void setPitch_hr(int inVal);

}