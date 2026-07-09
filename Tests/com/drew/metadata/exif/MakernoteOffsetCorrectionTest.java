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
package com.drew.metadata.exif;

import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.Assert.*;

/**
 * Tests detection and correction of incorrect makernote offsets.
 *
 * Some cameras write makernote tag value offsets relative to the makernote's own start
 * position rather than the TIFF header. The {@link ExifTiffHandler} attempts to detect
 * this situation and adjusts the base offset accordingly.
 */
public class MakernoteOffsetCorrectionTest
{
    /**
     * Builds a synthetic EXIF APP1 byte array containing a Canon makernote.
     * <p>
     * Layout (all offsets relative to the start of the returned byte array):
     * <pre>
     *  [0..5]    "Exif\0\0"           JPEG EXIF preamble
     *  [6..13]   TIFF header           "II", magic 0x002A, IFD0 offset = 8
     *  [14..43]  IFD0                  2 tags: Make, ExifSubIFD
     *  [44..50]  Make string           "Canon\0\0" (7 bytes)
     *  [51]      (padding)
     *  [52..69]  ExifSubIFD            1 tag: Makernote
     *  [70..99]  Canon Makernote IFD   2 tags: ImageType, FirmwareVersion
     *  [100..119] ImageType string     "Canon EOS 5D\0\0\0\0\0\0\0\0" (20 bytes)
     *  [120..135] FirmwareVersion str  "Firmware 1.0.0\0\0" (16 bytes)
     * </pre>
     *
     * @param incorrectOffsets if {@code true}, makernote tag value offsets are written
     *                         relative to the makernote start (incorrect); if {@code false},
     *                         they are written relative to the TIFF header (correct)
     */
    private static byte[] buildCanonExif(boolean incorrectOffsets)
    {
        // tiffHeaderOffset = 6
        // makernoteOffset  = 70
        final int tiffHeaderOffset = 6;
        final int makernoteOffset  = 70;

        // Absolute positions of the two external data strings
        final int imageTypeAbsPos    = 100;
        final int firmwareAbsPos     = 120;

        // Raw offsets for the makernote IFD tags
        final int imageTypeOffset;
        final int firmwareOffset;
        if (incorrectOffsets) {
            // Offsets relative to makernoteOffset (incorrect for Canon)
            imageTypeOffset = imageTypeAbsPos - makernoteOffset;  // = 30
            firmwareOffset  = firmwareAbsPos  - makernoteOffset;  // = 50
        } else {
            // Offsets relative to tiffHeaderOffset (correct for Canon)
            imageTypeOffset = imageTypeAbsPos - tiffHeaderOffset;  // = 94
            firmwareOffset  = firmwareAbsPos  - tiffHeaderOffset;  // = 114
        }

        ByteBuffer buf = ByteBuffer.allocate(136);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        // [0..5] EXIF preamble
        buf.put(new byte[]{'E', 'x', 'i', 'f', 0, 0});

        // [6..13] TIFF header: II, magic, IFD0 offset = 8 (relative to tiffHeaderOffset)
        buf.put(new byte[]{0x49, 0x49});   // "II" = little-endian
        buf.putShort((short) 0x002A);       // TIFF magic
        buf.putInt(8);                      // IFD0 at tiffHeaderOffset + 8 = 14

        // [14..43] IFD0: 2 tags
        buf.putShort((short) 2);  // tag count

        // Tag 0: Make (0x010F), ASCII, 7 bytes, offset to "Canon\0\0"
        buf.putShort((short) 0x010F);                      // tag id
        buf.putShort((short) 2);                           // type = ASCII
        buf.putInt(7);                                     // count
        buf.putInt(44 - tiffHeaderOffset);                 // offset = 38

        // Tag 1: ExifSubIFD (0x8769), LONG, 1, offset to ExifSubIFD
        buf.putShort((short) 0x8769);                      // tag id
        buf.putShort((short) 4);                           // type = LONG
        buf.putInt(1);                                     // count
        buf.putInt(52 - tiffHeaderOffset);                 // offset = 46

        // [40..43] No next IFD
        buf.putInt(0);

        // [44..50] Make = "Canon\0\0"
        buf.put(new byte[]{'C', 'a', 'n', 'o', 'n', 0, 0});

        // [51] padding
        buf.put((byte) 0);

        // [52..69] ExifSubIFD: 1 tag
        buf.putShort((short) 1);  // tag count

        // Makernote tag (0x927C), UNDEFINED, count = 66 bytes, offset to makernote
        buf.putShort((short) 0x927C);                      // tag id
        buf.putShort((short) 7);                           // type = UNDEFINED
        buf.putInt(66);                                    // count (makernote size)
        buf.putInt(makernoteOffset - tiffHeaderOffset);    // offset = 64

        // [66..69] No next IFD
        buf.putInt(0);

        // [70..99] Canon Makernote IFD: 2 tags
        buf.putShort((short) 2);  // tag count

        // Tag 0: ImageType (0x0006), ASCII, 20 bytes
        buf.putShort((short) 0x0006);                      // tag id
        buf.putShort((short) 2);                           // type = ASCII
        buf.putInt(20);                                    // count
        buf.putInt(imageTypeOffset);                       // raw offset

        // Tag 1: FirmwareVersion (0x0007), ASCII, 16 bytes
        buf.putShort((short) 0x0007);                      // tag id
        buf.putShort((short) 2);                           // type = ASCII
        buf.putInt(16);                                    // count
        buf.putInt(firmwareOffset);                        // raw offset

        // [96..99] No next IFD
        buf.putInt(0);

        // [100..119] ImageType string
        buf.put("Canon EOS 5D\0\0\0\0\0\0\0\0".getBytes());

        // [120..135] FirmwareVersion string
        buf.put("Firmware 1.0.0\0\0".getBytes());

        assertEquals(136, buf.position());
        return buf.array();
    }

    @Test
    public void testCorrectOffsetsAreAcceptedWithoutWarning() throws IOException
    {
        byte[] exifData = buildCanonExif(false);

        Metadata metadata = new Metadata();
        new ExifReader().extract(new ByteArrayReader(exifData), metadata, ExifReader.JPEG_SEGMENT_PREAMBLE.length());

        CanonMakernoteDirectory canon = metadata.getFirstDirectoryOfType(CanonMakernoteDirectory.class);
        assertNotNull(canon);

        // No offset correction warning should be present
        for (String error : canon.getErrors()) {
            assertFalse("Unexpected offset warning: " + error, error.contains("Possibly incorrect makernote offsets"));
        }

        // Tags are readable
        assertEquals("Canon EOS 5D", canon.getString(CanonMakernoteDirectory.TAG_CANON_IMAGE_TYPE).trim().replace("\0", ""));
        assertEquals("Firmware 1.0.0", canon.getString(CanonMakernoteDirectory.TAG_CANON_FIRMWARE_VERSION).trim().replace("\0", ""));
    }

    @Test
    public void testIncorrectOffsetsAreDetectedAndCorrected() throws IOException
    {
        byte[] exifData = buildCanonExif(true);

        Metadata metadata = new Metadata();
        new ExifReader().extract(new ByteArrayReader(exifData), metadata, ExifReader.JPEG_SEGMENT_PREAMBLE.length());

        CanonMakernoteDirectory canon = metadata.getFirstDirectoryOfType(CanonMakernoteDirectory.class);
        assertNotNull(canon);

        // A warning about the offset correction must be present
        assertTrue("Expected offset correction warning", canon.hasErrors());
        boolean foundOffsetWarning = false;
        for (String error : canon.getErrors()) {
            if (error.contains("Possibly incorrect makernote offsets")) {
                foundOffsetWarning = true;
                break;
            }
        }
        assertTrue("Expected a 'Possibly incorrect makernote offsets' warning", foundOffsetWarning);

        // Despite the incorrect offsets, the correction allows reading the tags
        assertEquals("Canon EOS 5D", canon.getString(CanonMakernoteDirectory.TAG_CANON_IMAGE_TYPE).trim().replace("\0", ""));
        assertEquals("Firmware 1.0.0", canon.getString(CanonMakernoteDirectory.TAG_CANON_FIRMWARE_VERSION).trim().replace("\0", ""));
    }
}
