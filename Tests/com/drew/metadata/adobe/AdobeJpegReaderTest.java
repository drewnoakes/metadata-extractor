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

import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.tools.FileUtil;
import org.junit.Test;

import java.io.IOException;

import static com.drew.lang.Iterables.toList;
import static org.junit.Assert.*;

/** @author Drew Noakes http://drewnoakes.com */
public class AdobeJpegReaderTest
{
    @NotNull
    public static AdobeJpegDirectory processBytes(@NotNull String filePath) throws IOException
    {
        Metadata metadata = new Metadata();
        new AdobeJpegReader().extract(new SequentialByteArrayReader(FileUtil.readBytes(filePath)), metadata);

        AdobeJpegDirectory directory = metadata.getDirectory(AdobeJpegDirectory.class);
        assertNotNull(directory);
        return directory;
    }

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
        AdobeJpegDirectory directory = processBytes("Tests/data/adobeJpeg1.jpg.appe");

        assertFalse(directory.getErrors().toString(), directory.hasErrors());

        assertEquals(4, directory.getTagCount());

        assertEquals(1, directory.getInt(AdobeJpegDirectory.TAG_COLOR_TRANSFORM));
        assertEquals(25600, directory.getInt(AdobeJpegDirectory.TAG_DCT_ENCODE_VERSION));
        assertEquals(128, directory.getInt(AdobeJpegDirectory.TAG_APP14_FLAGS0));
        assertEquals(0, directory.getInt(AdobeJpegDirectory.TAG_APP14_FLAGS1));
    }
}
