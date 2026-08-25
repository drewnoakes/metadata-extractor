/*
 * Copyright 2002-2026 Drew Noakes and contributors
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
package com.drew.imaging.mp4;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4Directory;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class Mp4MetadataReaderTest
{
    private static final int FTYP_LENGTH = 16;
    private static final int MVHD_LENGTH = 108;
    private static final int NESTED_BOX_COUNT = 10000;
    private static final long TEST_TIME_SCALE = 600;
    private static final long TEST_DURATION = 1200;

    @Test
    public void testDeeplyNestedBoxesReportErrorRatherThanOverflowingStack() throws Exception
    {
        Metadata metadata = ImageMetadataReader.readMetadata(
            new ByteArrayInputStream(createDeeplyNestedBoxBytes()));

        Mp4Directory directory = metadata.getFirstDirectoryOfType(Mp4Directory.class);

        assertNotNull(directory);
        assertTrue(containsError(directory, "Box nesting too deep."));
    }

    @Test
    public void testNormallyNestedBoxesAreStillParsed() throws Exception
    {
        Metadata metadata = ImageMetadataReader.readMetadata(
            new ByteArrayInputStream(createMovieHeaderBytes()));

        Mp4Directory directory = metadata.getFirstDirectoryOfType(Mp4Directory.class);

        assertNotNull(directory);
        assertFalse(containsError(directory, "Box nesting too deep."));
        assertEquals(TEST_DURATION, (long)directory.getLong(Mp4Directory.TAG_DURATION));
        assertEquals(TEST_TIME_SCALE, (long)directory.getLong(Mp4Directory.TAG_TIME_SCALE));
    }

    /**
     * Builds a file in which each 'moov' box is nested within the previous one. Every box is
     * individually well formed, so only the nesting depth is abnormal.
     */
    private static byte[] createDeeplyNestedBoxBytes()
    {
        byte[] bytes = new byte[FTYP_LENGTH + NESTED_BOX_COUNT * 8];

        writeBoxHeader(bytes, 0, FTYP_LENGTH, "ftyp");
        writeString(bytes, 8, "isom");

        for (int i = 0; i < NESTED_BOX_COUNT; i++) {
            writeBoxHeader(bytes, FTYP_LENGTH + i * 8, (NESTED_BOX_COUNT - i) * 8, "moov");
        }

        return bytes;
    }

    /** Builds a conventionally nested file: 'ftyp', then 'moov' containing 'mvhd'. */
    private static byte[] createMovieHeaderBytes()
    {
        byte[] bytes = new byte[FTYP_LENGTH + 8 + MVHD_LENGTH];

        writeBoxHeader(bytes, 0, FTYP_LENGTH, "ftyp");
        writeString(bytes, 8, "isom");

        writeBoxHeader(bytes, FTYP_LENGTH, 8 + MVHD_LENGTH, "moov");
        writeBoxHeader(bytes, FTYP_LENGTH + 8, MVHD_LENGTH, "mvhd");

        // Version and flags occupy the first four payload bytes, then creation and modification
        // times, then the time scale and duration.
        writeUInt32(bytes, FTYP_LENGTH + 8 + 8 + 12, TEST_TIME_SCALE);
        writeUInt32(bytes, FTYP_LENGTH + 8 + 8 + 16, TEST_DURATION);

        return bytes;
    }

    /**
     * Both readers terminate their outermost loop by reading past the end of the stream, which
     * records an "End of data reached." error on every file, so tests check for the specific
     * error of interest rather than for the absence of all errors.
     */
    private static boolean containsError(Directory directory, String message)
    {
        for (String error : directory.getErrors()) {
            if (error.equals(message))
                return true;
        }
        return false;
    }

    private static void writeBoxHeader(byte[] bytes, int offset, long size, String type)
    {
        writeUInt32(bytes, offset, size);
        writeString(bytes, offset + 4, type);
    }

    private static void writeUInt32(byte[] bytes, int offset, long value)
    {
        bytes[offset] = (byte)(value >> 24);
        bytes[offset + 1] = (byte)(value >> 16);
        bytes[offset + 2] = (byte)(value >> 8);
        bytes[offset + 3] = (byte)value;
    }

    private static void writeString(byte[] bytes, int offset, String value)
    {
        for (int i = 0; i < value.length(); i++) {
            bytes[offset + i] = (byte)value.charAt(i);
        }
    }
}
