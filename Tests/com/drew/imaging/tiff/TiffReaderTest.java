/*
 * Copyright 2002-2024 Drew Noakes and contributors
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
package com.drew.imaging.tiff;

import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link TiffReader}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class TiffReaderTest
{
    @Test
    public void testReadBigTiff() throws Exception
    {
        // A synthetic BigTIFF DNG: IFD0 with a small uncompressed thumbnail and two
        // SubIFDs (raw data and a JPEG preview), using 8-byte offsets and counts
        Metadata metadata = TiffMetadataReader.readMetadata(new File("Tests/Data/bigtiff.dng"));

        ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        assertNotNull(ifd0);
        assertEquals("PENTAX", ifd0.getString(ExifDirectoryBase.TAG_MAKE));
        assertEquals("PENTAX K-x", ifd0.getString(ExifDirectoryBase.TAG_MODEL));
        assertEquals(16, ifd0.getInt(ExifDirectoryBase.TAG_IMAGE_WIDTH));
        assertEquals(12, ifd0.getInt(ExifDirectoryBase.TAG_IMAGE_HEIGHT));

        // Both SubIFDs referenced from IFD0's SubIFDs tag must be entered
        Collection<ExifSubIFDDirectory> subIfds = metadata.getDirectoriesOfType(ExifSubIFDDirectory.class);
        assertEquals(2, subIfds.size());

        boolean previewSeen = false;
        boolean rawSeen = false;
        for (ExifSubIFDDirectory subIfd : subIfds) {
            Integer width = subIfd.getInteger(ExifDirectoryBase.TAG_IMAGE_WIDTH);
            assertNotNull(width);
            if (width == 64) {
                previewSeen = true;
                assertEquals(48, subIfd.getInt(ExifDirectoryBase.TAG_IMAGE_HEIGHT));
            } else if (width == 8) {
                rawSeen = true;
            }
        }
        assertTrue("preview SubIFD not found", previewSeen);
        assertTrue("raw SubIFD not found", rawSeen);
    }
}
