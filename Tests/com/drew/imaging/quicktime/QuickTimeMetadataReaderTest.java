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
 *    https://github.com/drewnoakes/metadata-extractor/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.quicktime;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mov.media.QuickTimeVideoDirectory;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QuickTimeMetadataReaderTest
{
    private static final int FTYP_LENGTH = 16;
    private static final int MVHD_LENGTH = 108;
    private static final int NESTED_ATOM_COUNT = 10000;
    private static final long TEST_TIME_SCALE = 600;
    private static final long TEST_DURATION = 1200;
    private static final int MVHD_PAYLOAD_LENGTH = 100;
    private static final int FREE_LENGTH = 16;
    private static final int NESTING_LIMIT = 100;

    @Test
    public void testDeeplyNestedAtomsReportErrorRatherThanOverflowingStack() throws Exception
    {
        Metadata metadata = ImageMetadataReader.readMetadata(
            new ByteArrayInputStream(createDeeplyNestedAtomBytes()));

        QuickTimeDirectory directory = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);

        assertNotNull(directory);
        assertTrue(containsError(directory, "Atom nesting too deep."));
    }

    @Test
    public void testNormallyNestedAtomsAreStillParsed() throws Exception
    {
        Metadata metadata = ImageMetadataReader.readMetadata(
            new ByteArrayInputStream(createMovieHeaderBytes()));

        QuickTimeDirectory directory = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);

        assertNotNull(directory);
        assertFalse(containsError(directory, "Atom nesting too deep."));
        assertEquals(TEST_DURATION, (long)directory.getLong(QuickTimeDirectory.TAG_DURATION));
        assertEquals(TEST_TIME_SCALE, (long)directory.getLong(QuickTimeDirectory.TAG_TIME_SCALE));
    }

    @Test
    public void testExtendedSizeMovieHeaderIsParsed() throws Exception
    {
        // 'moov' holding an 'mvhd' with a 64-bit size, at the end of the file
        byte[] bytes = new byte[FTYP_LENGTH + 8 + 16 + MVHD_PAYLOAD_LENGTH];
        writeFileType(bytes);
        writeAtomHeader(bytes, FTYP_LENGTH, 8 + 16 + MVHD_PAYLOAD_LENGTH, "moov");
        writeExtendedHeader(bytes, FTYP_LENGTH + 8, 16 + MVHD_PAYLOAD_LENGTH, "mvhd");
        writeMovieHeaderPayload(bytes, FTYP_LENGTH + 8 + 16);

        assertMovieHeaderParsed(bytes);
    }

    @Test
    public void testParsingResumesAfterSkippedExtendedSizeContainer() throws Exception
    {
        // 100 nested 'moov' atoms around a 101st with a 64-bit size, which is skipped for depth,
        // then a 'free' atom and a regular 'moov'
        int chainLength = NESTING_LIMIT * 8 + 16;
        byte[] bytes = new byte[FTYP_LENGTH + chainLength + FREE_LENGTH + 8 + MVHD_LENGTH];
        writeFileType(bytes);
        int offset = FTYP_LENGTH;
        for (int i = 0; i < NESTING_LIMIT; i++, offset += 8) {
            writeAtomHeader(bytes, offset, chainLength - i * 8, "moov");
        }
        writeExtendedHeader(bytes, offset, 16, "moov");
        offset += 16;
        writeAtomHeader(bytes, offset, FREE_LENGTH, "free");
        offset += FREE_LENGTH;
        writeAtomHeader(bytes, offset, 8 + MVHD_LENGTH, "moov");
        writeAtomHeader(bytes, offset + 8, MVHD_LENGTH, "mvhd");
        writeMovieHeaderPayload(bytes, offset + 16);

        assertMovieHeaderParsed(bytes);
    }

    @Test
    public void testExtendedSizeUnknownAtomIsSkipped() throws Exception
    {
        // A 'free' atom with a 64-bit size, a regular 'free' atom, then a 'moov'
        byte[] bytes = new byte[FTYP_LENGTH + 16 + FREE_LENGTH + FREE_LENGTH + 8 + MVHD_LENGTH];
        writeFileType(bytes);
        int offset = FTYP_LENGTH;
        writeExtendedHeader(bytes, offset, 16 + FREE_LENGTH, "free");
        offset += 16 + FREE_LENGTH;
        writeAtomHeader(bytes, offset, FREE_LENGTH, "free");
        offset += FREE_LENGTH;
        writeAtomHeader(bytes, offset, 8 + MVHD_LENGTH, "moov");
        writeAtomHeader(bytes, offset + 8, MVHD_LENGTH, "mvhd");
        writeMovieHeaderPayload(bytes, offset + 16);

        assertMovieHeaderParsed(bytes);
    }

    @Test
    public void testSampleTableEntryCountIsNotUsedAsCapacity() throws Exception
    {
        // Each atom declares 0x7FFFFFFE entries but holds none
        for (String type : new String[]{"stts", "stsd"}) {
            QuickTimeVideoDirectory directory = readVideoDirectory(createVideoTrackBytes(type, 0x7FFFFFFE, new byte[0]));

            assertTrue(containsError(directory, "End of data reached."));
        }
    }

    @Test
    public void testVideoSampleDescriptionIsParsed() throws Exception
    {
        // Width and height follow 32 bytes of other fields in an 86-byte video sample description
        byte[] description = new byte[86];
        writeUInt32(description, 0, description.length);
        writeString(description, 4, "avc1");
        writeUInt32(description, 32, (640 << 16) | 480);

        QuickTimeVideoDirectory directory = readVideoDirectory(createVideoTrackBytes("stsd", 1, description));

        assertFalse(directory.hasErrors());
        assertEquals(640, directory.getInt(QuickTimeVideoDirectory.TAG_WIDTH));
        assertEquals(480, directory.getInt(QuickTimeVideoDirectory.TAG_HEIGHT));
    }

    /**
     * Builds a file in which each 'moov' atom is nested within the previous one. Every atom is
     * individually well formed, so only the nesting depth is abnormal.
     */
    private static byte[] createDeeplyNestedAtomBytes()
    {
        byte[] bytes = new byte[FTYP_LENGTH + NESTED_ATOM_COUNT * 8];

        writeAtomHeader(bytes, 0, FTYP_LENGTH, "ftyp");
        writeString(bytes, 8, "qt  ");

        for (int i = 0; i < NESTED_ATOM_COUNT; i++) {
            writeAtomHeader(bytes, FTYP_LENGTH + i * 8, (NESTED_ATOM_COUNT - i) * 8, "moov");
        }

        return bytes;
    }

    /** Builds a conventionally nested file: 'ftyp', then 'moov' containing 'mvhd'. */
    private static byte[] createMovieHeaderBytes()
    {
        byte[] bytes = new byte[FTYP_LENGTH + 8 + MVHD_LENGTH];

        writeAtomHeader(bytes, 0, FTYP_LENGTH, "ftyp");
        writeString(bytes, 8, "qt  ");

        writeAtomHeader(bytes, FTYP_LENGTH, 8 + MVHD_LENGTH, "moov");
        writeAtomHeader(bytes, FTYP_LENGTH + 8, MVHD_LENGTH, "mvhd");

        // Version and flags occupy the first four payload bytes, then creation and modification
        // times, then the time scale and duration.
        writeUInt32(bytes, FTYP_LENGTH + 8 + 8 + 12, TEST_TIME_SCALE);
        writeUInt32(bytes, FTYP_LENGTH + 8 + 8 + 16, TEST_DURATION);

        return bytes;
    }

    /**
     * Builds 'moov/trak/mdia' holding a 'vide' handler, then 'minf/stbl' holding one atom of the
     * given type with its entry count followed by {@code entries}.
     */
    private static byte[] createVideoTrackBytes(String type, long entryCount, byte[] entries)
    {
        int hdlrLength = 33;
        int tableLength = 16 + entries.length;
        int stblLength = 8 + tableLength;
        int minfLength = 8 + stblLength;
        int mdiaLength = 8 + hdlrLength + minfLength;
        int trakLength = 8 + mdiaLength;
        int moovLength = 8 + trakLength;

        byte[] bytes = new byte[FTYP_LENGTH + moovLength];
        writeFileType(bytes);
        int offset = FTYP_LENGTH;
        writeAtomHeader(bytes, offset, moovLength, "moov");
        writeAtomHeader(bytes, offset += 8, trakLength, "trak");
        writeAtomHeader(bytes, offset += 8, mdiaLength, "mdia");
        // Version and flags, component type and subtype, 12 reserved bytes, then an empty name
        writeAtomHeader(bytes, offset += 8, hdlrLength, "hdlr");
        writeString(bytes, offset + 12, "mhlr");
        writeString(bytes, offset + 16, "vide");
        writeAtomHeader(bytes, offset += hdlrLength, minfLength, "minf");
        writeAtomHeader(bytes, offset += 8, stblLength, "stbl");
        // Version and flags, then the entry count
        writeAtomHeader(bytes, offset += 8, tableLength, type);
        writeUInt32(bytes, offset + 12, entryCount);
        System.arraycopy(entries, 0, bytes, offset + 16, entries.length);

        return bytes;
    }

    private static QuickTimeVideoDirectory readVideoDirectory(byte[] bytes) throws Exception
    {
        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes));
        QuickTimeVideoDirectory directory = metadata.getFirstDirectoryOfType(QuickTimeVideoDirectory.class);

        assertNotNull(directory);
        return directory;
    }

    private static void assertMovieHeaderParsed(byte[] bytes) throws Exception
    {
        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes));
        QuickTimeDirectory directory = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);

        assertNotNull(directory);
        assertEquals(Long.valueOf(TEST_DURATION), directory.getLongObject(QuickTimeDirectory.TAG_DURATION));
        assertEquals(Long.valueOf(TEST_TIME_SCALE), directory.getLongObject(QuickTimeDirectory.TAG_TIME_SCALE));
    }

    private static void writeFileType(byte[] bytes)
    {
        writeAtomHeader(bytes, 0, FTYP_LENGTH, "ftyp");
        writeString(bytes, 8, "qt  ");
    }

    /** Writes the 'mvhd' payload that follows its header: version and flags, two times, then the time scale and duration. */
    private static void writeMovieHeaderPayload(byte[] bytes, int offset)
    {
        writeUInt32(bytes, offset + 12, TEST_TIME_SCALE);
        writeUInt32(bytes, offset + 16, TEST_DURATION);
    }

    /** Writes a header whose size field is 1, followed by the real size as 64 bits. */
    private static void writeExtendedHeader(byte[] bytes, int offset, long size, String type)
    {
        writeAtomHeader(bytes, offset, 1, type);
        writeUInt32(bytes, offset + 8, size >> 32);
        writeUInt32(bytes, offset + 12, size);
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

    private static void writeAtomHeader(byte[] bytes, int offset, long size, String type)
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
