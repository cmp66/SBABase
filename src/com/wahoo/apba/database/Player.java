package com.wahoo.apba.database;

import java.sql.*;
import com.wahoo.util.Email;

public class Player implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;
	private int id;
    private String lastname;
    private String firstname;
    private String displayname;
    private int startyear;
    private int endyear;
    private String bbreflink;
    private String rotowireid;
    private String position;
    private String bats;
    private String throwhand;
    private String birthdate;

    public Player()
    {
    }
    
    public Player(int id, String lastname, String firstname, String displayname, int startyear, int endyear, String bbreflink, String rotowireid, String position, String bats, String throwhand, String birthdate) 
    {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.displayname = displayname;
        this.startyear = startyear;
        this.endyear = endyear;
        this.bbreflink = bbreflink;
        this.rotowireid = rotowireid;
        this.position = position;
        this.bats = bats;
        this.throwhand = throwhand;
        this.birthdate = birthdate;

    }

    public Player(ResultSet inRecord) 
    {
        try
        {
            this.id = inRecord.getInt("id");
            this.lastname = inRecord.getString("lastname");
            this.firstname = inRecord.getString("firstname");
            this.displayname = inRecord.getString("displayname");
            this.startyear = inRecord.getInt("startyear");
            this.endyear = inRecord.getInt("endyear");
            this.bbreflink = inRecord.getString("bbreflink");
            this.rotowireid = inRecord.getString("rotowireid");
            this.position = inRecord.getString("position");
            this.bats = inRecord.getString("bats");
            this.throwhand = inRecord.getString("throwhand");
            this.birthdate = inRecord.getString("birthdate");
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
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into players (id, lastname, firstname, displayname, startyear, " +
                " endyear, bbreflink, rotowireid, position, bats, throwhand, birthdate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
            
            wkStatement.setInt(1, id);
            wkStatement.setString(2, lastname);
            wkStatement.setString(3, firstname);
            wkStatement.setString(4, displayname);
            wkStatement.setInt(5, startyear);
            wkStatement.setInt(6, endyear);
            wkStatement.setString(7, bbreflink);
            wkStatement.setString(8, rotowireid);
            wkStatement.setString(9, position);
            wkStatement.setString(10,  bats);
            wkStatement.setString(11, throwhand);
            wkStatement.setString(12, birthdate);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            System.out.println("Exception trying to add player id " + id);
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }

    public void updateRecord(Connection inConn)
    {
        try
        {
            PreparedStatement wkStatement = inConn.prepareStatement("UPDATE players SET lastname = ?, firstname = ?, displayname = ?, startyear = ?, " +
                " endyear = ?, bbreflink = ?, rotowireid = ?, position = ?, bats = ?, throwhand = ?, birthdate = ? WHERE id = ?");
            
            wkStatement.setInt(12, id);
            wkStatement.setString(1, lastname);
            wkStatement.setString(2, firstname);
            wkStatement.setString(3, displayname);
            wkStatement.setInt(4, startyear);
            wkStatement.setInt(5, endyear);
            wkStatement.setString(6, bbreflink);
            wkStatement.setString(7, rotowireid);
            wkStatement.setString(8, position);
            wkStatement.setString(9,  bats);
            wkStatement.setString(10, throwhand);
            wkStatement.setString(11, birthdate);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE from players WHERE id = ?");
            
            wkStatement.setInt(1, id);
            
            wkStatement.executeUpdate();
            
            wkStatement.close();
        }
        catch (SQLException wkException)
        {
            wkException.printStackTrace();
            Email.emailException(wkException);
        }
    }
    public int getId()
    {
        return this.id;
    }
    
    public String getLastname()
    {
        return this.lastname;
    }
    
    public String getFirstname()
    {
        return this.firstname;
    }
    
    public String getDisplayname()
    {
        return this.displayname;
    }
    
    public int getStartyear()
    {
        return this.startyear;
    }
    
    public int getEndyear()
    {
        return this.endyear;
    }
    
    public String getBbreflink()
    {
        return this.bbreflink;
    }
    
    public String getRotowireid()
    {
        return this.rotowireid;
    }
    
    public String getPosition()
    {
        return this.position;
    }
    
    public String getBats()
    {
        return this.bats;
    }
    
    public String getThrowhand()
    {
        return this.throwhand;
    }
    
    public String getBirthdate()
    {
        return this.birthdate;
    }
    
     public void setId(int inVal)
    {
        this.id = inVal;
    }
    
     public void setLastname(String inVal)
    {
        this.lastname = inVal;
    }
    
     public void setFirstname(String inVal)
    {
        this.firstname = inVal;
    }
    
     public void setDisplayname(String inVal)
    {
        this.displayname = inVal;
    }
    
     public void setStartyear(int inVal)
    {
        this.startyear = inVal;
    }
    
     public void setEndyear(int inVal)
    {
        this.endyear = inVal;
    }
    
     public void setBbreflink(String inVal)
    {
        this.bbreflink = inVal;
    }
    
     public void setRotowireid(String inVal)
    {
        this.rotowireid = inVal;
    }
    
     public void setPosition(String inVal)
    {
        this.position = inVal;
    }
    
     public void setBats(String inVal)
    {
        this.bats = inVal;
    }
    
     public void setThrowhand(String inVal)
    {
        this.throwhand = inVal;
    }
    
     public void setBirthdate(String inVal)
    {
        this.birthdate = inVal;
    }
    
     public boolean isPitcher()
     {
     	return this.position.equals("P");
     }
    
}
    