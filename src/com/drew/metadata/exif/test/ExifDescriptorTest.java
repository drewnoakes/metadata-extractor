/*
 * ExifReaderTest.java
 *
 * Test class written by Drew Noakes.
 *
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   drew@drewnoakes.com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 26-Oct-2002 19:15:16 using IntelliJ IDEA.
 */
package com.drew.metadata.exif.test;

import com.drew.lang.Rational;
import com.drew.metadata.exif.ExifDescriptor;
import com.drew.metadata.exif.ExifDirectory;
import junit.framework.TestCase;

/**
 * JUnit test case for class ExifDescriptor.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifDescriptorTest extends TestCase
{
    public ExifDescriptorTest(String s)
    {
        super(s);
    }

    public void testXResolution() throws Exception
    {
        ExifDirectory directory = new ExifDirectory();
        directory.setRational(ExifDirectory.TAG_X_RESOLUTION, new Rational(72, 1));
        // 2 is for 'Inch'
        directory.setInt(ExifDirectory.TAG_RESOLUTION_UNIT, 2);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("72 dots per inch", descriptor.getDescription(ExifDirectory.TAG_X_RESOLUTION));
    }

    public void testYResolution() throws Exception
    {
        ExifDirectory directory = new ExifDirectory();
        directory.setRational(ExifDirectory.TAG_Y_RESOLUTION, new Rational(50, 1));
        // 3 is for 'cm'
        directory.setInt(ExifDirectory.TAG_RESOLUTION_UNIT, 3);
        ExifDescriptor descriptor = new ExifDescriptor(directory);
        assertEquals("50 dots per cm", descriptor.getDescription(ExifDirectory.TAG_Y_RESOLUTION));
    }
}
