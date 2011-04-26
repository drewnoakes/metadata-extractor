/*
 * Copyright 2002-2011 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
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
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class AllTests extends TestSuite
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(CompoundExceptionTest.class);
        suite.addTestSuite(DirectoryTest.class);
        suite.addTestSuite(ExifDescriptorTest.class);
        suite.addTestSuite(ExifDirectoryTest.class);
        suite.addTestSuite(ExifReaderTest.class);
        suite.addTestSuite(IptcReaderTest.class);
        suite.addTestSuite(JpegComponentTest.class);
        suite.addTestSuite(JpegDescriptorTest.class);
        suite.addTestSuite(JpegDirectoryTest.class);
        suite.addTestSuite(JpegMetadataReaderTest.class);
        suite.addTestSuite(JpegReaderTest.class);
        suite.addTestSuite(JpegSegmentDataTest.class);
        suite.addTestSuite(JpegSegmentReaderTest.class);
        suite.addTestSuite(MetadataTest.class);
        suite.addTestSuite(NullOutputStreamTest.class);
        suite.addTestSuite(RationalTest.class);

        suite.addTestSuite(NikonType1MakernoteTest.class);
        suite.addTestSuite(NikonType2MakernoteTest1.class);
        suite.addTestSuite(NikonType2MakernoteTest2.class);
        suite.addTestSuite(CanonMakernoteDescriptorTest.class);

        return suite;
    }
}
