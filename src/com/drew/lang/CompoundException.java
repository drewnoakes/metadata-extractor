package com.drew.lang;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Represents a compound exception, as modelled in JDK 1.4, but
 * unavailable in previous versions.  This class allows support
 * of these previous JDK versions.
 */
public class CompoundException extends Exception
{
    private Throwable _innnerException;

    public CompoundException(String msg)
    {
        this(msg, null);
    }

    public CompoundException(Throwable exception)
    {
        this(null, exception);
    }

    public CompoundException(String msg, Throwable innerException)
    {
        super(msg);
        _innnerException = innerException;
    }

    public Throwable getInnerException()
    {
        return _innnerException;
    }

    public String toString()
    {
        StringBuffer sbuffer = new StringBuffer();
        sbuffer.append(super.toString());
        if (_innnerException != null) {
            sbuffer.append("\n");
            sbuffer.append("--- inner exception ---");
            sbuffer.append("\n");
            sbuffer.append(_innnerException.toString());
        }
        return sbuffer.toString();
    }

    public void printStackTrace(PrintStream s)
    {
        super.printStackTrace(s);
        if (_innnerException != null) {
            s.println("--- inner exception ---");
            _innnerException.printStackTrace(s);
        }
    }

    public void printStackTrace(PrintWriter s)
    {
        super.printStackTrace(s);
        if (_innnerException != null) {
            s.println("--- inner exception ---");
            _innnerException.printStackTrace(s);
        }
    }

    public void printStackTrace()
    {
        super.printStackTrace();
        if (_innnerException != null) {
            System.err.println("--- inner exception ---");
            _innnerException.printStackTrace();
        }
    }
}
