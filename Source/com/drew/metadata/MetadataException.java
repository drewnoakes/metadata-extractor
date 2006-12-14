/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 13-Nov-2002 18:10:23 using IntelliJ IDEA.
 */
package com.drew.metadata;

import com.drew.lang.CompoundException;

/**
 * Base class for all metadata specific exceptions.
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
