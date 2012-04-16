package com.wahoo.apba.database.util;

import java.util.HashMap;


public class Test
{
    
    private Test()
    {

    }

    public void test()
    {
        String before = "before";
        
        HashMap<String, String> tester = new HashMap<String, String>();
        tester.put("test", before);        
        modify(tester);
        
        before = (String) tester.get("test");
        
        System.out.println(before);
    }
    
    public void modify(HashMap<String, String> inval)
    {
        String tested = (String) inval.get("test");
        tested = "after";
        inval.put("test", tested);
    }
    


    
    public static void main(String[] args)
    {	
        Test copy = new Test();
        copy.test();
        
	}
}

    