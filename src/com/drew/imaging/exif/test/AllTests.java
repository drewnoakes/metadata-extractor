/*
 * AllTests.java
 *
 * Test suite class written by Drew Noakes.
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
 * Created by dnoakes on 26-Oct-2002 16:29:44 using IntelliJ IDEA.
 * - First collection of basic unit tests, to compile against JUnit
 * - Doesn't yet cover all classes
 */
package com.drew.imaging.exif.test;

import com.drew.imaging.jpeg.test.JpegSegmentReaderTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * ExifExtractor test suite.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class AllTests extends TestSuite
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(RationalTest.class);
        suite.addTestSuite(ImageInfoTest.class);
        suite.addTestSuite(ExifExtractorTest.class);
        suite.addTestSuite(JpegSegmentReaderTest.class);
        return suite;
    }
}
