/*
 * JpegProcessingException.java
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
 * Created by dnoakes on 04-Nov-2002 19:31:29 using IntelliJ IDEA.
 */
package com.drew.imaging.jpeg;

import com.drew.imaging.ImageProcessingException;

/**
 * An exception class thrown upon unexpected and fatal conditions while processing
 * a Jpeg file.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class JpegProcessingException extends ImageProcessingException
{
    public JpegProcessingException(String message)
    {
        super(message);
    }

    public JpegProcessingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public JpegProcessingException(Throwable cause)
    {
        super(cause);
    }
}
