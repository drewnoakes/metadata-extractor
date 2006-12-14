/*
 * ExifProcessingException.java
 *
 * This class is public domain software - that is, you can do whatever you want
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
 * Created on 29 April 2002, 00:33
 */
package com.drew.metadata.exif;

import com.drew.metadata.MetadataException;

/**
 * The exception type raised during reading of Exif data in the instance of
 * unexpected data conditions.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifProcessingException extends MetadataException
{
    /**
     * Constructs an instance of <code>ExifProcessingException</code> with the
     * specified detail message.
     * @param message the detail message
     */
    public ExifProcessingException(String message)
    {
        super(message);
    }

    /**
     * Constructs an instance of <code>ExifProcessingException</code> with the
     * specified detail message and inner exception.
     * @param message the detail message
     * @param cause an inner exception
     */
    public ExifProcessingException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
