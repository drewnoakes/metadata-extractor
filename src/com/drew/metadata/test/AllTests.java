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
package com.drew.metadata.test;

import com.drew.imaging.jpeg.test.JpegMetadataReaderTest;
import com.drew.imaging.jpeg.test.JpegSegmentReaderTest;
import com.drew.lang.test.CompoundExceptionTest;
import com.drew.lang.test.RationalTest;
import com.drew.lang.test.NullOutputStreamTest;
import com.drew.metadata.exif.test.ExifDirectoryTest;
import com.drew.metadata.exif.test.ExifProcessorTest;
import com.drew.metadata.iptc.test.IptcReaderTest;
import com.drew.metadata.test.DirectoryTest;
import com.drew.metadata.test.MetadataTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * ExifReader test suite.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class AllTests extends TestSuite
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(CompoundExceptionTest.class);
        suite.addTestSuite(DirectoryTest.class);
        suite.addTestSuite(ExifDirectoryTest.class);
        suite.addTestSuite(ExifProcessorTest.class);
        suite.addTestSuite(IptcReaderTest.class);
        suite.addTestSuite(JpegMetadataReaderTest.class);
        suite.addTestSuite(JpegSegmentReaderTest.class);
        suite.addTestSuite(MetadataTest.class);
        suite.addTestSuite(NullOutputStreamTest.class);
        suite.addTestSuite(RationalTest.class);
        return suite;
    }
}
