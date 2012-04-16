package com.wahoo.apba.database;

import java.util.StringTokenizer;

public class PlayerCard implements java.io.Serializable 
{

	private static final long serialVersionUID = 1L;
	private String lastname;
    private String firstname;
    private String age;
    private String team;
    private String bat;
    private String throwHand;
    private String id;
    private String primaryPosition;
    private String catcherD;
    private String catcherThrow;
    private String catcherPB;
    private String firstD;
    private String secondD;
    private String thirdD;
    private String ssD;
    private String ofD;
    private String arm;
    private String pitchD;
    private String speedLetter;
    private String speed;
    private String ssn;
    private String platoon;
    private String games;
    private String AB;
    private String hits;
    private String doubles;
    private String triples;
    private String homeruns;
    private String sac;
    private String HBP;
    private String walks;
    private String strikouts;
    private String sb;
    private String cs;
    private String average;
    private String OBP;
    private String SLUG;
    private String OPS;
    private String startingGrade;
    private String startingFatique;
    private String reliefGrade;
    private String reliefFatique;
    private String KLetter;
    private String WLetter;
    private String HRRating;
    private String MFRating;
    private String WPRating;
    private String balkRating;
    private String pitcherHBP;
    private String GS;
    private String GP;
    private String Won;
    private String Lost;
    private String Save;
    private String pitchIP;
    private String pitchHits;
    private String pitchER;
    private String pitchBB;
    private String pitchK;
    private String pitchHBP;
    private String ERA;
    private String JFactor;
    private String PR;
    private String SF;
    private String MBF;
    private String firstColumn;
    private String secondColumn;

    public PlayerCard()
    {
    }
    

    public PlayerCard(String inLine) 
    {
        StringTokenizer inRecord = new StringTokenizer(inLine, ",");
        
            this.lastname = inRecord.nextToken();
            this.firstname = inRecord.nextToken();
            this.age = inRecord.nextToken();
            this.team = inRecord.nextToken();
            this.bat = inRecord.nextToken();
            this.throwHand = inRecord.nextToken();
            this.id = inRecord.nextToken();
            this.primaryPosition = inRecord.nextToken();
            this.catcherD = inRecord.nextToken();
            this.catcherThrow = inRecord.nextToken();
            this.catcherPB = inRecord.nextToken();
            this.firstD = inRecord.nextToken();
            this.secondD = inRecord.nextToken();
            this.thirdD = inRecord.nextToken();
            this.ssD = inRecord.nextToken();
            this.ofD = inRecord.nextToken();
            this.arm = inRecord.nextToken();
            this.pitchD = inRecord.nextToken();
            this.speedLetter = inRecord.nextToken();
            this.speed = inRecord.nextToken();
            
            
            int wkSpeed = Integer.parseInt(this.speed);
            if (wkSpeed < 7)
                this.speedLetter = "S";
            else if (wkSpeed > 14)
                this.speedLetter = "F";
            else
                this.speedLetter = "";
            
            this.ssn = inRecord.nextToken();
            this.platoon = inRecord.nextToken();
            this.games = inRecord.nextToken();
            this.AB = inRecord.nextToken();
            this.hits = inRecord.nextToken();
            this.doubles = inRecord.nextToken();
            this.triples = inRecord.nextToken();
            this.homeruns = inRecord.nextToken();
            this.sac = inRecord.nextToken();
            this.HBP = inRecord.nextToken();
            this.walks = inRecord.nextToken();
            this.strikouts = inRecord.nextToken();
            this.sb = inRecord.nextToken();
            this.cs = inRecord.nextToken();
            this.average = inRecord.nextToken();
            this.OBP = inRecord.nextToken();
            this.SLUG = inRecord.nextToken();
            this.OPS = inRecord.nextToken();
            this.startingGrade = inRecord.nextToken();
            this.startingFatique = inRecord.nextToken();
            this.reliefGrade = inRecord.nextToken();
            this.reliefFatique = inRecord.nextToken();
            this.KLetter = inRecord.nextToken();
            this.WLetter = inRecord.nextToken();
            this.HRRating = inRecord.nextToken();
            this.MFRating = inRecord.nextToken();
            this.WPRating = inRecord.nextToken();
            this.balkRating = inRecord.nextToken();
            this.pitcherHBP = inRecord.nextToken();
            this.GP = inRecord.nextToken();
            this.GS = inRecord.nextToken();
            this.Won = inRecord.nextToken();
            this.Lost = inRecord.nextToken();
            this.Save = inRecord.nextToken();
            this.pitchIP = inRecord.nextToken();
            this.pitchHits = inRecord.nextToken();
            this.pitchER = inRecord.nextToken();
            this.pitchBB = inRecord.nextToken();
            this.pitchK = inRecord.nextToken();
            this.pitchHBP = inRecord.nextToken();
            this.ERA = inRecord.nextToken();
            this.JFactor = inRecord.nextToken();
            this.PR = inRecord.nextToken();
            this.SF = inRecord.nextToken();
            this.MBF = inRecord.nextToken();
            
            if (lastname.equals("HAMMOND"))
            {
                System.out.println("HAMMOND START: " + startingGrade + "  REL: " + reliefGrade);
            }
 
            //System.out.println("Lastname: " + this.lastname);
            //if (this.primaryPosition.equals("1"))
                //System.out.println("MBF " + this.MBF);
            //else
                //System.out.println("PR " + this.PR); 
            
            StringBuffer wkTemp = new StringBuffer();
            wkTemp.append(inRecord.nextToken());
            for (int i = 0; i < 35; i++)
            {
                wkTemp.append(",").append(inRecord.nextToken());
            }
            this.firstColumn = wkTemp.toString();

            wkTemp = new StringBuffer();
            wkTemp.append(inRecord.nextToken());
            for (int i = 0; i < 35; i++)
            {
                wkTemp.append(",").append(inRecord.nextToken());
            }
            this.secondColumn = wkTemp.toString();
    }
    public String getLastname()
    {
        return this.lastname;
    }
    
    public String getFirstname()
    {
        return this.firstname;
    }
    
    public String getAge()
    {
        return this.age;
    }
    
    public String getTeam()
    {
        return this.team;
    }
    
    public String getBat()
    {
        return this.bat;
    }
    
    public String getThrowHand()
    {
        return this.throwHand;
    }
    
    public String getId()
    {
        return this.id;
    }
    
    public String getPrimaryPosition()
    {
        return this.primaryPosition;
    }
    
    public String getCatcherD()
    {
        return this.catcherD;
    }
    
    public String getCatcherThrow()
    {
        return this.catcherThrow;
    }
    
    public String getCatcherPB()
    {
        return this.catcherPB;
    }
    
    public String getFirstD()
    {
        return this.firstD;
    }
    
    public String getSecondD()
    {
        return this.secondD;
    }
    
    public String getThirdD()
    {
        return this.thirdD;
    }
    
    public String getSsD()
    {
        return this.ssD;
    }
    
    public String getOfD()
    {
        return this.ofD;
    }
    
    public String getArm()
    {
        return this.arm;
    }
    
    public String getPitchD()
    {
        return this.pitchD;
    }
    
    public String getSpeedLetter()
    {
        return this.speedLetter;
    }
    
    public String getSpeed()
    {
        return this.speed;
    }
    
    public String getSsn()
    {
        return this.ssn;
    }
    
    public String getPlatoon()
    {
        return this.platoon;
    }
    
    public String getGames()
    {
        return this.games;
    }
    
    public String getAB()
    {
        return this.AB;
    }
    
    public String getHits()
    {
        return this.hits;
    }
    
    public String getDoubles()
    {
        return this.doubles;
    }
    
    public String getTriples()
    {
        return this.triples;
    }
    
    public String getHomeruns()
    {
        return this.homeruns;
    }
    
    public String getSac()
    {
        return this.sac;
    }
    
    public String getHBP()
    {
        return this.HBP;
    }
    
    public String getWalks()
    {
        return this.walks;
    }
    
    public String getStrikouts()
    {
        return this.strikouts;
    }
    
    public String getSb()
    {
        return this.sb;
    }
    
    public String getCs()
    {
        return this.cs;
    }
    
    public String getAverage()
    {
        return this.average;
    }
    
    public String getOBP()
    {
        return this.OBP;
    }
    
    public String getSLUG()
    {
        return this.SLUG;
    }
    
    public String getOPS()
    {
        return this.OPS;
    }
    
    public String getStartingGrade()
    {
        return this.startingGrade;
    }
    
    public String getStartingFatique()
    {
        return this.startingFatique;
    }
    
    public String getReliefGrade()
    {
        return this.reliefGrade;
    }
    
    public String getReliefFatique()
    {
        return this.reliefFatique;
    }
    
    public String getKLetter()
    {
        return this.KLetter;
    }
    
    public String getWLetter()
    {
        return this.WLetter;
    }
    
    public String getHRRating()
    {
        return this.HRRating;
    }
    
    public String getMFRating()
    {
        return this.MFRating;
    }
    
    public String getWPRating()
    {
        return this.WPRating;
    }
    
    public String getBalkRating()
    {
        return this.balkRating;
    }
    
    public String getPitcherHBP()
    {
        return this.pitcherHBP;
    }
    
    public String getGS()
    {
        return this.GS;
    }
    
    public String getGP()
    {
        return this.GP;
    }
    
    public String getWon()
    {
        return this.Won;
    }
    
    public String getLost()
    {
        return this.Lost;
    }
    
    public String getSave()
    {
        return this.Save;
    }
    
    public String getPitchIP()
    {
        return this.pitchIP;
    }
    
    public String getPitchHits()
    {
        return this.pitchHits;
    }
    
    public String getPitchER()
    {
        return this.pitchER;
    }
    
    public String getPitchBB()
    {
        return this.pitchBB;
    }
    
    public String getPitchK()
    {
        return this.pitchK;
    }
    
    public String getPitchHBP()
    {
        return this.pitchHBP;
    }
    
    public String getERA()
    {
        return this.ERA;
    }

    public String getJFactor()
    {
        return this.JFactor;
    }
    public String getPR()
    {
        return this.PR;
    }
    
    public String getSF()
    {
        return this.SF;
    }
    
    public String getMBF()
    {
        return this.MBF;
    }
    
    public String getFirstColumn()
    {
        return this.firstColumn;
    }
    
    public String getSecondColumn()
    {
        return this.secondColumn;
    }
    
     public void setLastname(String inVal)
    {
        this.lastname = inVal;
    }
    
     public void setFirstname(String inVal)
    {
        this.firstname = inVal;
    }
    
     public void setAge(String inVal)
    {
        this.age = inVal;
    }
    
     public void setTeam(String inVal)
    {
        this.team = inVal;
    }
    
     public void setBat(String inVal)
    {
        this.bat = inVal;
    }
    
     public void setThrowHand(String inVal)
    {
        this.throwHand = inVal;
    }
    
     public void setId(String inVal)
    {
        this.id = inVal;
    }
    
     public void setPrimaryPosition(String inVal)
    {
        this.primaryPosition = inVal;
    }
    
     public void setCatcherD(String inVal)
    {
        this.catcherD = inVal;
    }
    
     public void setCatcherThrow(String inVal)
    {
        this.catcherThrow = inVal;
    }
    
     public void setCatcherPB(String inVal)
    {
        this.catcherPB = inVal;
    }
    
     public void setFirstD(String inVal)
    {
        this.firstD = inVal;
    }
    
     public void setSecondD(String inVal)
    {
        this.secondD = inVal;
    }
    
     public void setThirdD(String inVal)
    {
        this.thirdD = inVal;
    }
    
     public void setSsD(String inVal)
    {
        this.ssD = inVal;
    }
    
     public void setOfD(String inVal)
    {
        this.ofD = inVal;
    }
    
     public void setArm(String inVal)
    {
        this.arm = inVal;
    }
    
     public void setPitchD(String inVal)
    {
        this.pitchD = inVal;
    }
    
     public void setSpeedLetter(String inVal)
    {
        this.speedLetter = inVal;
    }
    
     public void setSpeed(String inVal)
    {
        this.speed = inVal;
    }
    
     public void setSsn(String inVal)
    {
        this.ssn = inVal;
    }
    
     public void setPlatoon(String inVal)
    {
        this.platoon = inVal;
    }
    
     public void setGames(String inVal)
    {
        this.games = inVal;
    }
    
     public void setAB(String inVal)
    {
        this.AB = inVal;
    }
    
     public void setHits(String inVal)
    {
        this.hits = inVal;
    }
    
     public void setDoubles(String inVal)
    {
        this.doubles = inVal;
    }
    
     public void setTriples(String inVal)
    {
        this.triples = inVal;
    }
    
     public void setHomeruns(String inVal)
    {
        this.homeruns = inVal;
    }
    
     public void setSac(String inVal)
    {
        this.sac = inVal;
    }
    
     public void setHBP(String inVal)
    {
        this.HBP = inVal;
    }
    
     public void setWalks(String inVal)
    {
        this.walks = inVal;
    }
    
     public void setStrikouts(String inVal)
    {
        this.strikouts = inVal;
    }
    
     public void setSb(String inVal)
    {
        this.sb = inVal;
    }
    
     public void setCs(String inVal)
    {
        this.cs = inVal;
    }
    
     public void setAverage(String inVal)
    {
        this.average = inVal;
    }
    
     public void setOBP(String inVal)
    {
        this.OBP = inVal;
    }
    
     public void setSLUG(String inVal)
    {
        this.SLUG = inVal;
    }
    
     public void setOPS(String inVal)
    {
        this.OPS = inVal;
    }
    
     public void setStartingGrade(String inVal)
    {
        this.startingGrade = inVal;
    }
    
     public void setStartingFatique(String inVal)
    {
        this.startingFatique = inVal;
    }
    
     public void setReliefGrade(String inVal)
    {
        this.reliefGrade = inVal;
    }
    
     public void setReliefFatique(String inVal)
    {
        this.reliefFatique = inVal;
    }
    
     public void setKLetter(String inVal)
    {
        this.KLetter = inVal;
    }
    
     public void setWLetter(String inVal)
    {
        this.WLetter = inVal;
    }
    
     public void setHRRating(String inVal)
    {
        this.HRRating = inVal;
    }
    
     public void setMFRating(String inVal)
    {
        this.MFRating = inVal;
    }
    
     public void setWPRating(String inVal)
    {
        this.WPRating = inVal;
    }
    
     public void setBalkRating(String inVal)
    {
        this.balkRating = inVal;
    }
    
     public void setPitcherHBP(String inVal)
    {
        this.pitcherHBP = inVal;
    }
    
     public void setGS(String inVal)
    {
        this.GS = inVal;
    }
    
     public void setGP(String inVal)
    {
        this.GP = inVal;
    }
    
     public void setWon(String inVal)
    {
        this.Won = inVal;
    }
    
     public void setLost(String inVal)
    {
        this.Lost = inVal;
    }
    
     public void setSave(String inVal)
    {
        this.Save = inVal;
    }
    
     public void setPitchIP(String inVal)
    {
        this.pitchIP = inVal;
    }
    
     public void setPitchHits(String inVal)
    {
        this.pitchHits = inVal;
    }
    
     public void setPitchER(String inVal)
    {
        this.pitchER = inVal;
    }
    
     public void setPitchBB(String inVal)
    {
        this.pitchBB = inVal;
    }
    
     public void setPitchK(String inVal)
    {
        this.pitchK = inVal;
    }
    
     public void setPitchHBP(String inVal)
    {
        this.pitchHBP = inVal;
    }
    
     public void setERA(String inVal)
    {
        this.ERA = inVal;
    }
    
     public void setPR(String inVal)
    {
        this.PR = inVal;
    }
    
     public void setSF(String inVal)
    {
        this.SF = inVal;
    }
    
     public void setMBF(String inVal)
    {
        this.MBF = inVal;
    }
    
     public void setFirstColumn(String inVal)
    {
        this.firstColumn = inVal;
    }
    
     public void setSecondColumn(String inVal)
    {
        this.secondColumn = inVal;
    }
     
     public String toString()
     {
     	StringBuffer wkOutput = new StringBuffer(1024);
     	
		wkOutput.append(this.lastname);
        outputValue(this.firstname,wkOutput);
        outputValue(this.age,wkOutput);
        outputValue(this.team,wkOutput);
        outputValue(this.bat,wkOutput);
        outputValue(this.throwHand,wkOutput); 
        outputValue(this.id,wkOutput);
        outputValue(this.primaryPosition,wkOutput); 
        outputValue(this.catcherD,wkOutput);
        outputValue(this.catcherThrow,wkOutput); 
        outputValue(this.catcherPB,wkOutput);
        outputValue(this.firstD,wkOutput);
        outputValue(this.secondD,wkOutput);
        outputValue(this.thirdD,wkOutput);
        outputValue(this.ssD,wkOutput);
        outputValue(this.ofD,wkOutput);
        outputValue(this.arm,wkOutput);
        outputValue(this.pitchD,wkOutput); 
        outputValue(this.speedLetter,wkOutput); 
        outputValue(this.speed,wkOutput);
        outputValue(this.ssn,wkOutput);
        outputValue(this.platoon,wkOutput);
        outputValue(this.games,wkOutput);
        outputValue(this.AB,wkOutput);
        outputValue(this.hits,wkOutput); 
        outputValue(this.doubles,wkOutput); 
        outputValue(this.triples,wkOutput);
        outputValue(this.homeruns,wkOutput);
        outputValue(this.sac,wkOutput);
        outputValue(this.HBP,wkOutput);
        outputValue(this.walks,wkOutput); 
        outputValue(this.strikouts,wkOutput); 
        outputValue(this.sb,wkOutput);
        outputValue(this.cs,wkOutput);
        outputValue(this.average,wkOutput); 
        outputValue(this.OBP,wkOutput);
        outputValue(this.SLUG,wkOutput);
        outputValue(this.OPS,wkOutput);
        outputValue(this.startingGrade,wkOutput); 
        outputValue(this.startingFatique,wkOutput); 
        outputValue(this.reliefGrade,wkOutput);
        outputValue(this.reliefFatique,wkOutput); 
        outputValue(this.KLetter,wkOutput);
        outputValue(this.WLetter,wkOutput);
        outputValue(this.HRRating,wkOutput);
        outputValue(this.MFRating,wkOutput);
        outputValue(this.WPRating,wkOutput);
        outputValue(this.balkRating,wkOutput); 
        outputValue(this.pitcherHBP,wkOutput);
        outputValue(this.GS,wkOutput);
        outputValue(this.GP,wkOutput);
        outputValue(this.Won,wkOutput);
        outputValue(this.Lost,wkOutput);
        outputValue(this.Save,wkOutput);
        outputValue(this.pitchIP,wkOutput); 
        outputValue(this.pitchHits,wkOutput); 
        outputValue(this.pitchER,wkOutput);
        outputValue(this.pitchBB,wkOutput);
        outputValue(this.pitchK,wkOutput);
        outputValue(this.pitchHBP,wkOutput); 
        outputValue(this.ERA,wkOutput);
        outputValue(this.JFactor,wkOutput); 
        outputValue(this.PR,wkOutput);
        outputValue(this.SF,wkOutput);
        outputValue(this.MBF,wkOutput);
        outputValue(this.firstColumn,wkOutput);
        outputValue(this.secondColumn,wkOutput);
        
        return wkOutput.toString();
     }
     
     private void outputValue(String inValue, StringBuffer inBuf)
     {
     	if (null == inValue || inValue.equals(""))
     	{
     		inValue = " ";
     	}
     	
     	inBuf.append(",").append(inValue);
     }
     
    
    
}
    