/*
 * ImageProcessingException.java
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
 * Created by Darren Salomons
 */
package com.drew.imaging;

import com.drew.lang.CompoundException;

/**
 * An exception class thrown upon unexpected and fatal for generic image exception
 * 
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ImageProcessingException extends CompoundException
{
    public ImageProcessingException(String message)
    {
        super(message);
    }

    public ImageProcessingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ImageProcessingException(Throwable cause)
    {
        super(cause);
    }
}
