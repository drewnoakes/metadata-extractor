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
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.StreamReader;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

import static com.drew.lang.Iterables.toList;
import static org.junit.Assert.*;

/** @author Drew Noakes http://drewnoakes.com */
public class AdobeJpegReaderTest
{
    @Test
    public void testSegmentTypes() throws Exception
    {
        AdobeJpegReader reader = new AdobeJpegReader();

        assertEquals(1, toList(reader.getSegmentTypes()).size());
        assertEquals(JpegSegmentType.APPE, toList(reader.getSegmentTypes()).get(0));
    }

    @Test
    public void testReadAdobeJpegMetadata1() throws Exception
    {
        InputStream inputStream = new FileInputStream("Tests/com/drew/metadata/adobe/adobeJpeg1.jpg");

        AdobeJpegReader adobeJpegReader = new AdobeJpegReader();
        JpegSegmentData segmentData = JpegSegmentReader.readSegments(new StreamReader(inputStream), adobeJpegReader.getSegmentTypes());

        inputStream.close();

        byte[] bytes = segmentData.getSegment(JpegSegmentType.APPE);

        assertNotNull(bytes);

        Metadata metadata = new Metadata();
        adobeJpegReader.extract(new SequentialByteArrayReader(bytes), metadata);

        assertEquals(1, metadata.getDirectoryCount());
        AdobeJpegDirectory directory = metadata.getDirectory(AdobeJpegDirectory.class);
        assertNotNull(directory);
        assertFalse(directory.getErrors().toString(), directory.hasErrors());

        assertEquals(4, directory.getTagCount());

        assertEquals(1, directory.getInt(AdobeJpegDirectory.TAG_COLOR_TRANSFORM));
        assertEquals(25600, directory.getInt(AdobeJpegDirectory.TAG_DCT_ENCODE_VERSION));
        assertEquals(128, directory.getInt(AdobeJpegDirectory.TAG_APP14_FLAGS0));
        assertEquals(0, directory.getInt(AdobeJpegDirectory.TAG_APP14_FLAGS1));
    }
}
