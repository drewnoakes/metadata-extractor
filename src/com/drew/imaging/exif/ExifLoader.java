/*
 * ExifLoader.java
 *
 * This class is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  Similarly, I release this Java version under the
 * same license, though I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   drew.noakes@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created on 29 April 2002, 00:43
 * Modified 05 Aug 2002
 * - Made constructor private
 * - Layout of code updated and imports optimised
 */

package com.drew.imaging.exif;

import java.io.*;
import com.sun.image.codec.jpeg.*;

/**
 * Loads Exif data from files.  This helper class uses Sun's JAI to
 * create the JPEGDecodeParam object to be passed to ExifExtractor.
 * @author  Drew Noakes drew.noakes@drewnoakes.com
 */
public class ExifLoader
{
    /**
     * Private constructor as this class isn't meant for instantiation.
     */
    private ExifLoader()
    {
        //
    }

    /**
     * Creates in ImageInfo object for the specified file, provided the file
     * contains Exif data.
     */
    public static ImageInfo getImageInfo(File file) throws IOException, ExifProcessingException
    {
        InputStream in = new FileInputStream(file);
        JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);

        // ignore returned image
        decoder.decodeAsBufferedImage();

        JPEGDecodeParam param = decoder.getJPEGDecodeParam();
        return new ExifExtractor(param).extract();
    }
}
