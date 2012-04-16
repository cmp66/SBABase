package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class GameResult implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;
	private int scheduleid;
    private int gamenumber;
    private int homepitcher;
    private int visitpitcher;
    private int homeruns;
    private int visitruns;
    private String comment;

    public GameResult()
    {
    }
    
    public GameResult(int scheduleid, int gamenumber, int homepitcher, int visitpitcher, int homeruns, int visitruns, String comment) 
    {
        this.scheduleid = scheduleid;
        this.gamenumber = gamenumber;
        this.homepitcher = homepitcher;
        this.visitpitcher = visitpitcher;
        this.homeruns = homeruns;
        this.visitruns = visitruns;
        this.comment = comment;

    }

    public GameResult(ResultSet inRecord) 
    {
        try
        {
            this.scheduleid = inRecord.getInt("scheduleid");
            this.gamenumber = inRecord.getInt("gamenumber");
            this.homepitcher = inRecord.getInt("homepitcher");
            this.visitpitcher = inRecord.getInt("visitpitcher");
            this.homeruns = inRecord.getInt("homeruns");
            this.visitruns = inRecord.getInt("visitruns");
            this.comment = inRecord.getString("comment");

        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    
    public void createRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into gameresults (scheduleid, gamenumber, homepitcher, visitpitcher, " +
                                                                    "homeruns, visitruns, comment) VALUES (?,?,?,?,?,?,?)");
            
            wkStatement.setInt(1, scheduleid);
            wkStatement.setInt(2, gamenumber);
            wkStatement.setInt(3, homepitcher);
            wkStatement.setInt(4, visitpitcher);
            wkStatement.setInt(5, homeruns);
            wkStatement.setInt(6, visitruns);
            wkStatement.setString(7, comment);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }

    public void updateRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("UPDATE gameresults SET homepitcher = ?, visitpitcher = ? " +
                                                                    "homeruns = ?, visitruns = ?, comment = ? where scheduleid = ? and gamenumber = ?");
            
            wkStatement.setInt(6, scheduleid);
            wkStatement.setInt(7, gamenumber);
            wkStatement.setInt(1, homepitcher);
            wkStatement.setInt(2, visitpitcher);
            wkStatement.setInt(3, homeruns);
            wkStatement.setInt(4, visitruns);
            wkStatement.setString(5, comment);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }

    public void deleteRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE from  gameresults WHERE scheduleid = ? and gamenumber = ?");
            
            wkStatement.setInt(1, scheduleid);
            wkStatement.setInt(2, gamenumber);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }

    
    
    public int getScheduleid()
    {
        return this.scheduleid;
    }
    
    public int getGamenumber()
    {
        return this.gamenumber;
    }
    
    public int getHomepitcher()
    {
        return this.homepitcher;
    }
    
    public int getVisitpitcher()
    {
        return this.visitpitcher;
    }
    
    public int getHomeruns()
    {
        return this.homeruns;
    }
    
    public int getVisitruns()
    {
        return this.visitruns;
    }
    
    public String getComment()
    {
        return this.comment;
    }
    
     public void setScheduleid(int inVal)
    {
        this.scheduleid = inVal;
    }
    
     public void setGamenumber(int inVal)
    {
        this.gamenumber = inVal;
    }
    
     public void setHomepitcher(int inVal)
    {
        this.homepitcher = inVal;
    }
    
     public void setVisitpitcher(int inVal)
    {
        this.visitpitcher = inVal;
    }
    
     public void setHomeruns(int inVal)
    {
        this.homeruns = inVal;
    }
    
     public void setVisitruns(int inVal)
    {
        this.visitruns = inVal;
    }
    
     public void setComment(String inVal)
    {
        this.comment = inVal;
    }    
}
    