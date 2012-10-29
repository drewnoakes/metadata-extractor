/*
 * Copyright 2002-2012 Drew Noakes
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

package com.drew.metadata.adobe;

import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Metadata;
import org.junit.Assert;
import org.junit.Test;

/** @author Drew Noakes http://drewnoakes.com */
public class AdobeJpegReaderTest
{
    @Test
    public void testReadAdobeJpegMetadata1() throws Exception
    {
        JpegSegmentData segmentData = JpegSegmentReader.readSegments("Tests/com/drew/metadata/adobe/adobeJpeg1.jpg");
        byte[] bytes = segmentData.getSegment(JpegSegmentType.APPE);

        Assert.assertNotNull(bytes);

        final Metadata metadata = new Metadata();
        new AdobeJpegReader().extract(new ByteArrayReader(bytes), metadata);

        Assert.assertEquals(1, metadata.getDirectoryCount());
        AdobeJpegDirectory directory = metadata.getDirectory(AdobeJpegDirectory.class);
        Assert.assertNotNull(directory);
        Assert.assertFalse(directory.getErrors().toString(), directory.hasErrors());

        Assert.assertEquals(4, directory.getTagCount());

        Assert.assertEquals(1, directory.getInt(AdobeJpegDirectory.TAG_COLOR_TRANSFORM));
        Assert.assertEquals(25600, directory.getInt(AdobeJpegDirectory.TAG_DCT_ENCODE_VERSION));
        Assert.assertEquals(128, directory.getInt(AdobeJpegDirectory.TAG_APP14_FLAGS0));
        Assert.assertEquals(0, directory.getInt(AdobeJpegDirectory.TAG_APP14_FLAGS1));
    }
}
