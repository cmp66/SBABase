/*
 * CardAnalyzer.java
 *
 * Created on March 5, 2003, 9:26 PM
 */

package com.wahoo.apba.database;

import com.wahoo.apba.database.PlayerCard;
import java.util.StringTokenizer;
/**
 *
 * @author  cphillips
 */
public class CardAnalyzer
{
    PlayerCard _card;
    int _ones = 0;
    int _power = 0;
    int _sevens = 0;
    int _elevens = 0;
    int _eights = 0;
    int _nines = 0;
    int _tens = 0;
    int _twentytwo = 0;
    int _twentyfour = 0;
    int _twentysix = 0;
    int _twentyseven = 0;
    int _twentyeight = 0;
    int _walks = 0;
    
    /** Creates a new instance of CardAnalyzer */
    public CardAnalyzer (PlayerCard inCard)
    {
        _card = inCard;
        analyzeCard();
    }
    
    
    private void analyzeCard()
    {
        StringTokenizer wkNumbers = new StringTokenizer(_card.getFirstColumn(), ",");
        
        while (wkNumbers.hasMoreTokens())
        {
            String wkNumber = wkNumbers.nextToken();
            
            if (null == wkNumber)
                break;
            
            if (wkNumber.equals("1"))
                _ones++;
            if (wkNumber.equals("1") || 
                wkNumber.equals("2") || 
                wkNumber.equals("3") || 
                wkNumber.equals("4") || 
                wkNumber.equals("5")  ||
                wkNumber.equals("6") ||
                wkNumber.equals("0"))
                _power++;
            else if (wkNumber.equals("7"))
                _sevens++;
            else if (wkNumber.equals("11"))
                _elevens++;
            else if (wkNumber.equals("8"))
                _eights++;
            else if (wkNumber.equals("9"))
                _nines++;
            else if (wkNumber.equals("10"))
                _tens++;
            else if (wkNumber.equals("22"))
                _twentytwo++;
            else if (wkNumber.equals("24"))
                _twentyfour++;
            else if (wkNumber.equals("26"))
                _twentysix++;
            else if (wkNumber.equals("27"))
                _twentyseven++;
            else if (wkNumber.equals("28"))
                _twentyeight++;
            else if (wkNumber.equals("14"))
                _walks++;
        }
    }
    
    public int getOnes()
    {
        return _ones;
    }
    
    public int getPower()
    {
        return _power;
    }
    
    public int getSevens()
    {
        return _sevens;
    }
    
    public int getElevens()
    {
        return _elevens;
    }
    
    public int getEights()
    {
        return _eights;
    }
    
    public int getNines()
    {
        return _nines;
    }
    
    public int getTens()
    {
        return _tens;
    }
    
    public int getTwentytwo()
    {
        return _twentytwo;
    }
    
    public int getTwentyfour()
    {
        return _twentyfour;
    }
    
    public int getTwentysix()
    {
        return _twentysix;
    }
    
    public int getTwentyseven()
    {
        return _twentyseven;
    }
    
    public int getTwentyeight()
    {
        return _twentyeight;
    }
    
    public int getWalks()
    {
        return _walks;
    }
    
            
}
