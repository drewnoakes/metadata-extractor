/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */

package com.drew.metadata.icc;

import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Metadata;
import com.drew.testing.TestHelper;
import com.drew.tools.FileUtil;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ConstantConditions")
public class IccReaderTest
{
    // TODO add a test with well-formed ICC data and assert output values are correct

    @Test
    public void testExtract_InvalidData() throws Exception
    {
        byte[] app2Bytes = FileUtil.readBytes("Tests/Data/iccDataInvalid1.jpg.app2");

        // When in an APP2 segment, ICC data starts after a 14-byte preamble
        byte[] icc = TestHelper.skipBytes(app2Bytes, 14);

        Metadata metadata = new Metadata();
        new IccReader().extract(new ByteArrayReader(icc), metadata);

        IccDirectory directory = metadata.getFirstDirectoryOfType(IccDirectory.class);

        assertNotNull(directory);
        assertTrue(directory.hasErrors());
    }

    @Test
    public void testReadJpegSegments_InvalidData() throws Exception
    {
        byte[] app2Bytes = FileUtil.readBytes("Tests/Data/iccDataInvalid1.jpg.app2");

        Metadata metadata = new Metadata();
        new IccReader().readJpegSegments(Arrays.asList(app2Bytes), metadata, JpegSegmentType.APP2);

        IccDirectory directory = metadata.getFirstDirectoryOfType(IccDirectory.class);

        assertNotNull(directory);
        assertTrue(directory.hasErrors());
    }

    @Test
    public void testExtract_ProfileDateTime() throws Exception
    {
        byte[] app2Bytes = FileUtil.readBytes("Tests/Data/withExifAndIptc.jpg.app2");

        Metadata metadata = new Metadata();
        new IccReader().readJpegSegments(Arrays.asList(app2Bytes), metadata, JpegSegmentType.APP2);

        IccDirectory directory = metadata.getFirstDirectoryOfType(IccDirectory.class);

        assertNotNull(directory);
        assertEquals("1998:02:09 06:49:00", directory.getString(IccDirectory.TAG_PROFILE_DATETIME));
        assertEquals(887006940000L, directory.getDate(IccDirectory.TAG_PROFILE_DATETIME).getTime());
    }
}
