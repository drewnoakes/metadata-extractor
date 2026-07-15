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
package com.drew.imaging;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class FileTypeDetectorTest
{
    /** Builds an ISO base media file header: a 4-byte big-endian box size followed by a 4-byte box type. */
    private static byte[] box(int size, String type, String remainder)
    {
        byte[] tail = remainder.getBytes(com.drew.lang.Charsets.ISO_8859_1);
        byte[] bytes = new byte[8 + tail.length];
        bytes[0] = (byte) (size >>> 24);
        bytes[1] = (byte) (size >>> 16);
        bytes[2] = (byte) (size >>> 8);
        bytes[3] = (byte) size;
        System.arraycopy(type.getBytes(com.drew.lang.Charsets.ISO_8859_1), 0, bytes, 4, 4);
        System.arraycopy(tail, 0, bytes, 8, tail.length);
        return bytes;
    }

    private static FileType detect(byte[] bytes) throws IOException
    {
        return FileTypeDetector.detectFileType(new BufferedInputStream(new ByteArrayInputStream(bytes)));
    }

    @Test
    public void testDetectQuickTimeWithFtypBrand() throws Exception
    {
        // Branded QuickTime, e.g. produced by tools that write a leading 'ftyp' box.
        assertEquals(FileType.QuickTime, detect(box(0x14, "ftyp", "qt  \0\0\0\0")));
    }

    @Test
    public void testDetectMp4AndHeifByFtypBrand() throws Exception
    {
        // The 'ftyp' branch must keep disambiguating MP4 and HEIF by major brand.
        assertEquals(FileType.Mp4, detect(box(0x18, "ftyp", "isom\0\0\0\0")));
        assertEquals(FileType.Heif, detect(box(0x18, "ftyp", "heic\0\0\0\0")));
    }

    @Test
    public void testDetectQuickTimeWithoutFtypBox() throws Exception
    {
        // Classic QuickTime .mov recordings (e.g. Apple Live Photo videos) omit the
        // 'ftyp' box and begin with a top-level atom instead. These must not be
        // reported as Unknown, otherwise ImageMetadataReader refuses to parse them.
        assertEquals(FileType.QuickTime, detect(box(0x08, "wide", "\0\0\0\0mdat")));
        assertEquals(FileType.QuickTime, detect(box(0x08, "mdat", "\0\0\0\0moov")));
        assertEquals(FileType.QuickTime, detect(box(0x6c, "moov", "\0\0\0\0mvhd")));
        assertEquals(FileType.QuickTime, detect(box(0x08, "free", "\0\0\0\0mdat")));
        assertEquals(FileType.QuickTime, detect(box(0x08, "skip", "\0\0\0\0mdat")));
        assertEquals(FileType.QuickTime, detect(box(0x08, "pnot", "\0\0\0\0mdat")));
    }

    @Test
    public void testUnknownWhenNoRecognisedHeader() throws Exception
    {
        assertEquals(FileType.Unknown, detect(box(0x08, "junk", "\0\0\0\0junk")));
    }
}
