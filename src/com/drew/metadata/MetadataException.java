/*
 * Created by dnoakes on 13-Nov-2002 18:10:23 using IntelliJ IDEA.
 */
package com.drew.metadata;

import com.drew.lang.CompoundException;

/**
 *
 */
public class MetadataException extends CompoundException
{
    public MetadataException(String msg)
    {
        super(msg);
    }

    public MetadataException(Throwable exception)
    {
        super(exception);
    }

    public MetadataException(String msg, Throwable innerException)
    {
        super(msg, innerException);
    }
}
