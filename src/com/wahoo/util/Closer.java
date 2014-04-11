/*
 * Closer.java
 *
 * Created on January 10, 2002, 10:02 PM
 */

package com.wahoo.util;

import java.util.*;
import java.lang.reflect.Method;

/**
 *
 * @author  Administrator
 */
@SuppressWarnings("rawtypes")
public class Closer
{
    String      _closeMethod = "close";
    Object[]    _closeArgs = {};
	Class[]     _closeTypes = {};
    Vector      _objectVec = new Vector();
    Vector      _exceptionVec = new Vector();
    boolean     _closeDone = false;

    /** Creates a new instance of Closer */
    public Closer()
    {
        return;
    }

    public Closer( String inCloseMethod, Class[] inCloseTypes, Object[] inCloseArgs )
    {
        _closeMethod = inCloseMethod;
        
        _closeTypes = new Class[inCloseTypes.length];
        
        for (int i = 0; i < inCloseTypes.length; i++)
        {
            _closeTypes[i] = inCloseTypes[i];
        }
        
        _closeArgs = new Object[inCloseArgs.length];
        
        for (int i = 0; i < inCloseArgs.length; i++)
        {
            _closeArgs[i] = inCloseArgs[i];
        }

        return;
    }

    @SuppressWarnings("unchecked")
	public synchronized void add( Object inObj )
    {
        if ( getCloseDone() )
            throw new IllegalStateException( "Add not valid in this state: close done." );

        if ( ! _objectVec.contains( inObj ) )
            _objectVec.add( inObj );

        return;
    }

    public synchronized void remove( Object inObj )
    {
        if ( getCloseDone() )
            throw new IllegalStateException( "Remove not valid in this state: close done." );

        if ( _objectVec.contains( inObj ) )
            _objectVec.remove( inObj );
        return;
    }

    @SuppressWarnings("unchecked")
	public synchronized void close()
    {
        if ( getCloseDone() )
            return;

        for ( int ix = _objectVec.size() - 1; ix >= 0; --ix )
        {
            if ( null != _objectVec.elementAt( ix ) )
            {
                try
                {
                    Method wkMethod = _objectVec.elementAt( ix ).getClass().getMethod( _closeMethod, _closeTypes );
                    wkMethod.invoke( _objectVec.elementAt( ix ), _closeArgs );
                }
                catch ( Throwable excp )
                {
                    _exceptionVec.addElement( excp );
                }
            }
        }
        _closeDone = true;

        return;
    }

    public String getCloseMethod()
    {
        return _closeMethod;
    }

    public Class[] getCloseTypes()
    {
        return _closeTypes.clone();
    }

    public Object[] getCloseArgs()
    {
        return _closeArgs.clone();
    }

    @SuppressWarnings("unchecked")
	public Throwable[] getExceptions()
    {
        return (Throwable[]) _exceptionVec.toArray( new Throwable[] {} );
    }

    public int getExceptionCount()
    {
        return _exceptionVec.size();
    }

    public Object[] getObjects()
    {
        return _objectVec.toArray();
    }

    public boolean getCloseDone()
    {
        return _closeDone;
    }
}
