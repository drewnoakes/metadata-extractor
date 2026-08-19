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
package com.drew.imaging.tiff;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifTiffHandler;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link TiffReader}.
 *
 * @author Dominik Schmidt
 */
public class TiffReaderTest
{
    /**
     * Asserts the structure of the synthetic BigTIFF DNG fixtures: IFD0 with a small
     * uncompressed thumbnail and two SubIFDs (raw data and a 64x48 JPEG preview),
     * all addressed with 8-byte offsets and counts.
     */
    private static void assertBigTiffDng(Metadata metadata) throws MetadataException
    {
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

    @Test
    public void testReadBigTiffLittleEndian() throws Exception
    {
        Metadata metadata = TiffMetadataReader.readMetadata(new File("Tests/Data/bigtiff.dng"));
        assertFalse(metadata.hasErrors());
        assertBigTiffDng(metadata);
    }

    @Test
    public void testReadBigTiffBigEndian() throws Exception
    {
        // Same structure as bigtiff.dng but with Motorola (MM) byte order, exercising the
        // big-endian paths of every multi-byte BigTIFF read (8-byte counts and offsets)
        Metadata metadata = TiffMetadataReader.readMetadata(new File("Tests/Data/bigtiff_be.dng"));
        assertFalse(metadata.hasErrors());
        assertBigTiffDng(metadata);
    }

    @Test
    public void testBigTiffThroughImageMetadataReader() throws Exception
    {
        // A BigTIFF file must be recognised by FileTypeDetector and routed through the
        // TIFF reader by the library's primary entry point
        Metadata metadata = ImageMetadataReader.readMetadata(new File("Tests/Data/bigtiff.dng"));
        assertBigTiffDng(metadata);
    }

    @Test
    public void testUnsupportedBigTiffOffsetByteSize() throws Exception
    {
        // A BigTIFF header declaring an offset byte size other than 8 must be reported
        // gracefully as an error, not throw and not read any tags
        byte[] bytes = new byte[]{
            'I', 'I', 0x2B, 0x00,   // little-endian BigTIFF marker
            0x04, 0x00,             // offset byte size = 4 (unsupported; must be 8)
            0x00, 0x00,             // reserved
            0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00  // first IFD offset
        };
        Metadata metadata = new Metadata();
        new TiffReader().processTiff(new ByteArrayReader(bytes),
            new ExifTiffHandler(metadata, null, 0), 0);

        assertTrue("expected a recorded error", metadata.hasErrors());
        // the marker pushes an (empty) IFD0; it must carry the error and no tags
        ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        assertNotNull(ifd0);
        assertEquals(0, ifd0.getTagCount());
        boolean sawOffsetSizeError = false;
        for (String error : ifd0.getErrors()) {
            if (error.contains("offset byte size")) {
                sawOffsetSizeError = true;
            }
        }
        assertTrue("expected offset-byte-size error", sawOffsetSizeError);
    }
}
