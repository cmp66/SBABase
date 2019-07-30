package com.wahoo.apba.database;


import java.sql.*;
import com.wahoo.util.Email;

public class Member implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;
	private int id;
    private String lastname;
    private String firstname;
    private String streetaddress1;
    private String streetaddress2;
    private String city;
    private String state;
    private String zipcode;

    public Member()
    {
    }
    
    public Member(int id, String lastname, String firstname, String streetaddress1, String streetaddress2, String city, String state, String zipcode) 
    {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.streetaddress1 = streetaddress1;
        this.streetaddress2 = streetaddress2;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;

    }

    public Member(ResultSet inRecord) 
    {
        try
        {
            this.id = inRecord.getInt("id");
            this.lastname = inRecord.getString("lastname");
            this.firstname = inRecord.getString("firstname");
            this.streetaddress1 = inRecord.getString("streetaddress1");
            this.streetaddress2 = inRecord.getString("streetaddress2");
            this.city = inRecord.getString("city");
            this.state = inRecord.getString("state");
            this.zipcode = inRecord.getString("zipcode");

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
            PreparedStatement wkStatement = inConn.prepareStatement("INSERT into members (id, lastname, firstname, streetaddress1, " +
                       " streetaddress2, city, state, zipcode) VALUES (?,?,?,?,?,?,?,?)");
            
            wkStatement.setInt(1, id);
            wkStatement.setString(2, lastname);
            wkStatement.setString(3, firstname);
            wkStatement.setString(4, streetaddress1);
            wkStatement.setString(5, streetaddress2);
            wkStatement.setString(6, city);
            wkStatement.setString(7, state);
            wkStatement.setString(8, zipcode);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("UPDATE members SET lastname = ?, firstname = ?, streetaddress1= ?, " +
                       " streetaddress2 = ?, city = ?, state = ?, zipcode = ? WHERE id = ?");
            
            wkStatement.setInt(8, id);
            wkStatement.setString(1, lastname);
            wkStatement.setString(2, firstname);
            wkStatement.setString(3, streetaddress1);
            wkStatement.setString(4, streetaddress2);
            wkStatement.setString(5, city);
            wkStatement.setString(6, state);
            wkStatement.setString(7, zipcode);
            
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
            PreparedStatement wkStatement = inConn.prepareStatement("DELETE from members WHERE id = ?");
            
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
    
    public String getStreetaddress1()
    {
        return this.streetaddress1;
    }
    
    public String getStreetaddress2()
    {
        return this.streetaddress2;
    }
    
    public String getCity()
    {
        return this.city;
    }
    
    public String getState()
    {
        return this.state;
    }
    
    public String getZipcode()
    {
        return this.zipcode;
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
    
     public void setStreetaddress1(String inVal)
    {
        this.streetaddress1 = inVal;
    }
    
     public void setStreetaddress2(String inVal)
    {
        this.streetaddress2 = inVal;
    }
    
     public void setCity(String inVal)
    {
        this.city = inVal;
    }
    
     public void setState(String inVal)
    {
        this.state = inVal;
    }
    
     public void setZipcode(String inVal)
    {
        this.zipcode = inVal;
    }
    
    
}
    
    