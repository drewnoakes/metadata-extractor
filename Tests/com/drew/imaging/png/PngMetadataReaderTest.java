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
 *    https://github.com/drewnoakes/metadata-extractor/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.png;

import com.drew.lang.Charsets;
import com.drew.lang.KeyValuePair;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.icc.IccDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.png.PngDirectory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngMetadataReaderTest
{
    private static final PngChunkType[] COMPRESSED_CHUNK_TYPES = {
        PngChunkType.iCCP, PngChunkType.zTXt, PngChunkType.iTXt
    };

    @NotNull
    private static Metadata processFile(@NotNull String filePath) throws PngProcessingException, IOException
    {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            return PngMetadataReader.readMetadata(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Test
    public void testGimpGreyscaleWithManyChunks() throws Exception
    {
        TimeZone defaultTimeZone = TimeZone.getDefault();

        try {
            TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

            Metadata metadata = processFile("Tests/Data/gimp-8x12-greyscale-alpha-time-background.png");
            Collection<PngDirectory> directories = metadata.getDirectoriesOfType(PngDirectory.class);

            assertNotNull(directories);
            assertEquals(6, directories.size());

            PngDirectory[] dirs = new PngDirectory[directories.size()];
            directories.toArray(dirs);

            assertEquals(PngChunkType.IHDR, dirs[0].getPngChunkType());
            assertEquals(8, dirs[0].getInt(PngDirectory.TAG_IMAGE_WIDTH));
            assertEquals(12, dirs[0].getInt(PngDirectory.TAG_IMAGE_HEIGHT));
            assertEquals(8, dirs[0].getInt(PngDirectory.TAG_BITS_PER_SAMPLE));
            assertEquals(4, dirs[0].getInt(PngDirectory.TAG_COLOR_TYPE));
            assertEquals(0, dirs[0].getInt(PngDirectory.TAG_COMPRESSION_TYPE));
            assertEquals(0, dirs[0].getInt(PngDirectory.TAG_FILTER_METHOD));
            assertEquals(0, dirs[0].getInt(PngDirectory.TAG_INTERLACE_METHOD));

            assertEquals(PngChunkType.gAMA, dirs[1].getPngChunkType());
            assertEquals(0.45455, dirs[1].getDouble(PngDirectory.TAG_GAMMA), 0.00001);

            assertEquals(PngChunkType.bKGD, dirs[2].getPngChunkType());
            assertArrayEquals(new byte[]{0, 52}, dirs[2].getByteArray(PngDirectory.TAG_BACKGROUND_COLOR));

            //noinspection ConstantConditions
            assertEquals(PngChunkType.pHYs, dirs[3].getPngChunkType());
            assertEquals(1, dirs[3].getInt(PngDirectory.TAG_UNIT_SPECIFIER));
            assertEquals(2835, dirs[3].getInt(PngDirectory.TAG_PIXELS_PER_UNIT_X));
            assertEquals(2835, dirs[3].getInt(PngDirectory.TAG_PIXELS_PER_UNIT_Y));

            assertEquals(PngChunkType.tIME, dirs[4].getPngChunkType());
            assertEquals("2013:01:01 04:08:30", dirs[4].getString(PngDirectory.TAG_LAST_MODIFICATION_TIME));

            java.util.Date modTime = dirs[4].getDate(PngDirectory.TAG_LAST_MODIFICATION_TIME);
            SimpleDateFormat formatter = new SimpleDateFormat("EE MMM DD HH:mm:ss z yyyy", Locale.US);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            assertEquals("Tue Jan 01 04:08:30 GMT 2013", formatter.format(modTime));
            assertNotNull(modTime);
            assertEquals(1357013310000L, modTime.getTime());

            assertEquals(PngChunkType.iTXt, dirs[5].getPngChunkType());
            @SuppressWarnings("unchecked")
            List<KeyValuePair> pairs = (List<KeyValuePair>)dirs[5].getObject(PngDirectory.TAG_TEXTUAL_DATA);
            assertNotNull(pairs);
            assertEquals(1, pairs.size());
            assertEquals("Comment", pairs.get(0).getKey().toString());
            assertEquals("Created with GIMP", pairs.get(0).getValue().toString());
        } finally {
            TimeZone.setDefault(defaultTimeZone);
        }
    }

    @Test
    public void testCompressedIccProfile() throws Exception
    {
        byte[] profile = new byte[IccDirectory.TAG_TAG_COUNT + 4];
        ByteBuffer buffer = ByteBuffer.wrap(profile);
        buffer.putInt(IccDirectory.TAG_PROFILE_BYTE_COUNT, profile.length);
        buffer.putShort(IccDirectory.TAG_PROFILE_DATETIME, (short)2026);
        buffer.putShort(IccDirectory.TAG_PROFILE_DATETIME + 2, (short)10);
        buffer.putShort(IccDirectory.TAG_PROFILE_DATETIME + 4, (short)20);
        buffer.putInt(IccDirectory.TAG_SIGNATURE, 0x61637370);

        Metadata metadata = readCompressedChunk(PngChunkType.iCCP, compress(profile));

        assertFalse(metadata.hasErrors());
        IccDirectory directory = metadata.getFirstDirectoryOfType(IccDirectory.class);
        assertNotNull(directory);
        assertEquals(profile.length, directory.getInt(IccDirectory.TAG_PROFILE_BYTE_COUNT));
        assertEquals("acsp", directory.getString(IccDirectory.TAG_SIGNATURE));
    }

    @Test
    public void testCompressedIccProfileRejectsOversizedDeclaredLength() throws Exception
    {
        byte[] profile = new byte[148];
        ByteBuffer buffer = ByteBuffer.wrap(profile);
        buffer.putInt(0, 0x10000010);
        buffer.putInt(128, 1);
        buffer.putInt(132, 0x74657374);
        buffer.putInt(136, 0x10000000);
        buffer.putInt(140, 16);

        Metadata metadata = readCompressedChunk(PngChunkType.iCCP, compress(profile));
        IccDirectory directory = metadata.getFirstDirectoryOfType(IccDirectory.class);
        assertNotNull(directory);
        assertTrue(directory.getErrors().iterator().next().contains("profile size"));
        assertFalse(directory.containsTag(0x74657374));
    }

    @Test
    public void testCompressedIccProfileRejectsOutOfRangeTag() throws Exception
    {
        byte[] profile = new byte[148];
        ByteBuffer buffer = ByteBuffer.wrap(profile);
        buffer.putInt(0, profile.length);
        buffer.putShort(24, (short)2026).putShort(26, (short)10).putShort(28, (short)20);
        buffer.putInt(128, 1);
        buffer.putInt(132, 0x74657374);
        buffer.putInt(136, 0x10000000);
        buffer.putInt(140, 16);

        Metadata metadata = readCompressedChunk(PngChunkType.iCCP, compress(profile));
        IccDirectory directory = metadata.getFirstDirectoryOfType(IccDirectory.class);
        assertNotNull(directory);
        assertTrue(directory.getErrors().iterator().next().contains("tag data exceeds profile size"));
        assertFalse(directory.containsTag(0x74657374));
    }

    @Test
    public void testCompressedIccProfileRejectsExcessiveTagCount() throws Exception
    {
        for (int length : new int[]{0, 1}) {
            byte[] profile = new byte[132 + 4097 * 12];
            ByteBuffer buffer = ByteBuffer.wrap(profile);
            buffer.putInt(0, profile.length);
            buffer.putShort(24, (short)2026).putShort(26, (short)10).putShort(28, (short)20);
            buffer.putInt(128, 4097);
            for (int i = 0; i < 4097; i++) {
                buffer.putInt(132 + 12 * i, 0x74657374);
                buffer.putInt(136 + 12 * i, i);
                buffer.putInt(140 + 12 * i, length);
            }

            Metadata metadata = readCompressedChunk(PngChunkType.iCCP, compress(profile));
            IccDirectory directory = metadata.getFirstDirectoryOfType(IccDirectory.class);
            assertNotNull(directory);
            assertTrue(directory.getErrors().iterator().next().contains("tag count exceeds supported limit"));
            assertFalse(directory.containsTag(0x74657374));
        }
    }

    @Test
    public void testCompressedText() throws Exception
    {
        assertCompressedText(PngChunkType.zTXt, "Compressed caf\u00e9");
        assertCompressedText(PngChunkType.iTXt, "Compressed \u03c0");
    }

    private static void assertCompressedText(PngChunkType chunkType, String text) throws Exception
    {
        byte[] bytes = text.getBytes(chunkType.equals(PngChunkType.iTXt) ? Charsets.UTF_8 : Charsets.ISO_8859_1);
        Metadata metadata = readCompressedChunk(chunkType, compress(bytes));

        assertFalse(chunkType.toString(), metadata.hasErrors());
        Collection<PngDirectory> directories = metadata.getDirectoriesOfType(PngDirectory.class);
        assertEquals(2, directories.size());
        PngDirectory directory = directories.toArray(new PngDirectory[directories.size()])[1];
        assertEquals(chunkType, directory.getPngChunkType());
        @SuppressWarnings("unchecked")
        List<KeyValuePair> pairs = (List<KeyValuePair>)directory.getObject(PngDirectory.TAG_TEXTUAL_DATA);
        assertNotNull(pairs);
        assertEquals(1, pairs.size());
        assertEquals("Comment", pairs.get(0).getKey());
        assertEquals(text, pairs.get(0).getValue().toString());
    }

    @Test
    public void testCorruptCompressedChunks() throws Exception
    {
        for (PngChunkType chunkType : COMPRESSED_CHUNK_TYPES) {
            Metadata metadata = readCompressedChunk(chunkType, new byte[]{0, 0, 0});
            assertTrue(chunkType.toString(), metadata.hasErrors());
        }
    }

    @Test
    public void testTruncatedCompressedChunks() throws Exception
    {
        for (PngChunkType chunkType : COMPRESSED_CHUNK_TYPES) {
            Metadata metadata = readCompressedChunk(chunkType, new byte[]{0x78, (byte)0x9c});
            assertTrue(chunkType.toString(), metadata.hasErrors());
        }
    }

    private static Metadata readCompressedChunk(PngChunkType chunkType, byte[] compressedBytes) throws Exception
    {
        ByteArrayOutputStream chunkBytes = new ByteArrayOutputStream();
        chunkBytes.write((chunkType.equals(PngChunkType.iCCP) ? "Profile" : "Comment").getBytes(Charsets.ASCII));
        chunkBytes.write(0);
        if (chunkType.equals(PngChunkType.iTXt)) {
            chunkBytes.write(1);
        }
        chunkBytes.write(0);
        if (chunkType.equals(PngChunkType.iTXt)) {
            chunkBytes.write(0);
            chunkBytes.write(0);
        }
        chunkBytes.write(compressedBytes);

        ByteArrayOutputStream pngBytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(pngBytes);
        output.write(new byte[]{(byte)0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a});
        writeChunk(output, PngChunkType.IHDR, new byte[]{0, 0, 0, 1, 0, 0, 0, 1, 8, 2, 0, 0, 0});
        writeChunk(output, chunkType, chunkBytes.toByteArray());
        writeChunk(output, PngChunkType.IDAT, compress(new byte[]{0, 0, 0, 0}));
        writeChunk(output, PngChunkType.IEND, new byte[0]);
        output.write(0x42);

        ByteArrayInputStream input = new ByteArrayInputStream(pngBytes.toByteArray()) {
            @Override
            public void close()
            {
                fail("The caller-owned PNG input stream must remain open");
            }
        };
        Metadata metadata = PngMetadataReader.readMetadata(input);
        assertEquals(0x42, input.read());
        return metadata;
    }

    private static byte[] compress(byte[] bytes) throws IOException
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (DeflaterOutputStream stream = new DeflaterOutputStream(output)) {
            stream.write(bytes);
        }
        return output.toByteArray();
    }

    private static void writeChunk(DataOutputStream output, PngChunkType chunkType, byte[] bytes) throws IOException
    {
        byte[] typeBytes = chunkType.getIdentifier().getBytes(Charsets.ASCII);
        output.writeInt(bytes.length);
        output.write(typeBytes);
        output.write(bytes);
        CRC32 crc = new CRC32();
        crc.update(typeBytes);
        crc.update(bytes);
        output.writeInt((int)crc.getValue());
    }

    @Test
    public void testIptcInTextChunk() throws Exception
    {
        Metadata metadata = processFile("Tests/Data/png-with-iptc-in-text-chunk.png");

        IptcDirectory iptcDirectory = metadata.getFirstDirectoryOfType(IptcDirectory.class);
        assertNotNull("Expected IPTC directory to be extracted from tEXt chunk", iptcDirectory);
        assertFalse(iptcDirectory.hasErrors());

        assertEquals("Test Image Name", iptcDirectory.getString(IptcDirectory.TAG_OBJECT_NAME));
        assertEquals("Test caption for the image", iptcDirectory.getString(IptcDirectory.TAG_CAPTION));

        String[] keywords = iptcDirectory.getStringArray(IptcDirectory.TAG_KEYWORDS);
        assertNotNull(keywords);
        assertEquals(2, keywords.length);
        assertEquals("keyword1", keywords[0]);
        assertEquals("keyword2", keywords[1]);
    }

    @Test
    public void testIptcInZtxtChunk() throws Exception
    {
        Metadata metadata = processFile("Tests/Data/png-with-iptc-in-ztxt-chunk.png");

        IptcDirectory iptcDirectory = metadata.getFirstDirectoryOfType(IptcDirectory.class);
        assertNotNull("Expected IPTC directory to be extracted from zTXt chunk", iptcDirectory);
        assertFalse(iptcDirectory.hasErrors());

        assertEquals("Test Image Name", iptcDirectory.getString(IptcDirectory.TAG_OBJECT_NAME));
        assertEquals("Test caption for the image", iptcDirectory.getString(IptcDirectory.TAG_CAPTION));

        String[] keywords = iptcDirectory.getStringArray(IptcDirectory.TAG_KEYWORDS);
        assertNotNull(keywords);
        assertEquals(2, keywords.length);
        assertEquals("keyword1", keywords[0]);
        assertEquals("keyword2", keywords[1]);
    }

    @Test
    public void testIptcInItxtChunk() throws Exception
    {
        Metadata metadata = processFile("Tests/Data/png-with-iptc-in-itxt-chunk.png");

        IptcDirectory iptcDirectory = metadata.getFirstDirectoryOfType(IptcDirectory.class);
        assertNotNull("Expected IPTC directory to be extracted from iTXt chunk", iptcDirectory);
        assertFalse(iptcDirectory.hasErrors());

        assertEquals("Test Image Name", iptcDirectory.getString(IptcDirectory.TAG_OBJECT_NAME));
        assertEquals("Test caption for the image", iptcDirectory.getString(IptcDirectory.TAG_CAPTION));

        String[] keywords = iptcDirectory.getStringArray(IptcDirectory.TAG_KEYWORDS);
        assertNotNull(keywords);
        assertEquals(2, keywords.length);
        assertEquals("keyword1", keywords[0]);
        assertEquals("keyword2", keywords[1]);
    }
}
