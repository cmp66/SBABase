package com.wahoo.util;
 
import org.apache.oro.text.regex.*;

public class PatternFinder
{
 
    Pattern _pattern = null;
    PatternMatcher _matcher = new Perl5Matcher();
    PatternMatcherInput _input = null;
    

    private PatternFinder()
    {}
    
    public PatternFinder (String inPatternString)
    {        
        if (!initPattern(inPatternString))
        {
        }
    }

    public boolean initPattern(String inPatternString)
    {
        boolean wkPatternOk = false;
        PatternCompiler wkCompiler = new Perl5Compiler();

        try 
        {
            _pattern = wkCompiler.compile(inPatternString);
            wkPatternOk = true;
        } 
        catch(MalformedPatternException e) 
        {
            System.out.println("Bad pattern.");
            System.out.println(e.getMessage());
        }
        
        return wkPatternOk;
    }
    
    public void setInputString(String inInputString)
    {
        _input = null;

        _input = new PatternMatcherInput(inInputString);
    }
    
    public boolean hasNext()
    {
        return _matcher.contains(_input, _pattern);
    }
    
    public String next()
    {
        return _matcher.getMatch().toString();
    }
        
    public void runMatch(String inSearchString)
    {
        MatchResult wkResult;

        while(_matcher.contains(_input, _pattern))
        {
            wkResult = _matcher.getMatch();  
            System.out.println(wkResult.toString());
            // Perform whatever processing on the result you want.
        }
            // Suppose we want to start searching from the beginning again with
            // a different pattern.
            // Just set the current offset to the begin offset.
            //wkInput.setCurrentOffset(wkInput.getBeginOffset());

            // Second search omitted

             // Suppose we're done with this input, but want to search another string.
            // There's no need to create another PatternMatcherInput instance.
            // We can just use the setInput() method.
            //wkInput.setInput(aNewInputString);
    }
}

 