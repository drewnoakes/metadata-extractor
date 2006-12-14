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
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 26-Oct-2002 16:29:44 using IntelliJ IDEA.
 * - First collection of basic unit tests, to compile against JUnit
 * - Doesn't yet cover all classes
 */
package com.drew.metadata.test;

import com.drew.imaging.jpeg.test.JpegMetadataReaderTest;
import com.drew.imaging.jpeg.test.JpegSegmentDataTest;
import com.drew.imaging.jpeg.test.JpegSegmentReaderTest;
import com.drew.lang.test.CompoundExceptionTest;
import com.drew.lang.test.NullOutputStreamTest;
import com.drew.lang.test.RationalTest;
import com.drew.metadata.exif.test.*;
import com.drew.metadata.iptc.test.IptcReaderTest;
import com.drew.metadata.jpeg.test.JpegComponentTest;
import com.drew.metadata.jpeg.test.JpegDescriptorTest;
import com.drew.metadata.jpeg.test.JpegDirectoryTest;
import com.drew.metadata.jpeg.test.JpegReaderTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The complete test suite for the metadata-extractor library.
 * @author  Drew Noakes http://drewnoakes.com
 */
public class AllTests extends TestSuite
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(DirectoryTest.class);
        suite.addTestSuite(ExifDirectoryTest.class);
        suite.addTestSuite(ExifReaderTest.class);
        suite.addTestSuite(ExifDescriptorTest.class);
        suite.addTestSuite(IptcReaderTest.class);
        suite.addTestSuite(MetadataTest.class);
        suite.addTestSuite(JpegReaderTest.class);
        suite.addTestSuite(JpegSegmentDataTest.class);
        suite.addTestSuite(JpegDirectoryTest.class);
        suite.addTestSuite(JpegComponentTest.class);
        suite.addTestSuite(JpegDescriptorTest.class);
        suite.addTestSuite(NikonType1MakernoteTest.class);
        suite.addTestSuite(NikonType2MakernoteTest1.class);
        suite.addTestSuite(NikonType2MakernoteTest2.class);
        suite.addTestSuite(CanonMakernoteDescriptorTest.class);

        suite.addTestSuite(CompoundExceptionTest.class);
        suite.addTestSuite(NullOutputStreamTest.class);
        suite.addTestSuite(RationalTest.class);

        suite.addTestSuite(JpegMetadataReaderTest.class);
        suite.addTestSuite(JpegSegmentReaderTest.class);

        return suite;
    }
}
