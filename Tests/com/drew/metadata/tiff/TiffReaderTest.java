/*
 * Copyright 2002-2015 Drew Noakes
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

package com.drew.metadata.tiff;

import com.drew.imaging.tiff.TiffMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Xinwei Xie
 */
public class TiffReaderTest
{
    @Test
    public void testTiffMetadata() throws Exception
    {
        InputStream stream = new FileInputStream("Tests/Data/multi_page_document.tiff");
        Metadata metadata = TiffMetadataReader.readMetadata(stream);
        stream.close();

        assertEquals(10, metadata.getDirectoryCount());

        ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        assertNotNull(exifIFD0Directory);
        assertEquals(600, exifIFD0Directory.getInt(ExifIFD0Directory.TAG_IMAGE_HEIGHT));
        assertEquals(800, exifIFD0Directory.getInt(ExifIFD0Directory.TAG_IMAGE_WIDTH));
        assertEquals(96, exifIFD0Directory.getInt(ExifIFD0Directory.TAG_X_RESOLUTION));
        assertEquals(96, exifIFD0Directory.getInt(ExifIFD0Directory.TAG_Y_RESOLUTION));
    }
}
