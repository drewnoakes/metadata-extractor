package com.drew.metadata.icc;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessStreamReader;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;

public class IccReaderBoundsTest
{
    @Test
    public void rejectsInvalidProfileSizesWithoutReadingBeyondHeader() throws Exception
    {
        for (int size : new int[]{-1, 0, 131, 16 * 1024 * 1024 + 1, 0x7fffffff}) {
            byte[] bytes = profile(1, 4);
            ByteBuffer.wrap(bytes).putInt(0, size);
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            Metadata metadata = new Metadata();
            new IccReader().extract(new RandomAccessStreamReader(stream, 4), metadata);
            assertTrue(error(metadata).contains("profile size"));
            assertEquals(bytes.length - 4, stream.available());
        }
    }

    @Test
    public void rejectsInvalidTagCounts() throws Exception
    {
        for (int count : new int[]{-1, 2, 0x7fffffff}) {
            byte[] bytes = profile(1, 4);
            ByteBuffer.wrap(bytes).putInt(128, count);
            assertTrue(error(extract(bytes)).contains("tag table"));
        }
    }

    @Test
    public void rejectsExcessiveTagCountBeforeReadingEntries() throws Exception
    {
        for (int length : new int[]{0, 1}) {
            byte[] bytes = profile(4097, 0);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            for (int i = 0; i < 4097; i++) {
                buffer.putInt(132 + 12 * i, 0x74657374);
                buffer.putInt(136 + 12 * i, i);
                buffer.putInt(140 + 12 * i, length);
            }
            final int[] reads = {0};
            Metadata metadata = new Metadata();
            new IccReader().extract(new ByteArrayReader(bytes) {
                @Override
                public byte getByte(int offset) throws IOException
                {
                    assertTrue("Must reject before reading the tag table", offset < 132);
                    return super.getByte(offset);
                }

                @Override
                public byte[] getBytes(int offset, int count) throws IOException
                {
                    reads[0]++;
                    return super.getBytes(offset, count);
                }
            }, metadata);
            assertTrue(error(metadata).contains("tag count exceeds supported limit"));
            assertEquals(0, reads[0]);
            assertFalse(metadata.getFirstDirectoryOfType(IccDirectory.class).containsTag(0x74657374));
        }
    }

    @Test
    public void acceptsTagCountAtLimitWithDistinctSmallRanges() throws Exception
    {
        for (int length : new int[]{0, 1}) {
            byte[] bytes = profile(4096, 0);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            for (int i = 0; i < 4096; i++) {
                buffer.putInt(132 + 12 * i, 0x74657374);
                buffer.putInt(136 + 12 * i, i);
                buffer.putInt(140 + 12 * i, length);
            }
            Metadata metadata = extract(bytes);
            assertFalse(metadata.hasErrors());
            assertArrayEquals(Arrays.copyOfRange(bytes, 4095, 4095 + length),
                metadata.getFirstDirectoryOfType(IccDirectory.class).getByteArray(0x74657374));
        }
    }

    @Test
    public void rejectsInvalidTagRangesBeforeReadingData() throws Exception
    {
        for (int[] range : new int[][]{{-1, 4}, {144, -1}, {0x10000000, 16}, {0x7ffffff0, 32}, {145, 4}, {144, 0x7fffffff}}) {
            byte[] bytes = profile(1, 4);
            ByteBuffer.wrap(bytes).putInt(136, range[0]).putInt(140, range[1]);
            final int[] reads = {0};
            Metadata metadata = new Metadata();
            new IccReader().extract(new ByteArrayReader(bytes) {
                @Override
                public byte[] getBytes(int offset, int count) throws IOException
                {
                    reads[0]++;
                    return super.getBytes(offset, count);
                }
            }, metadata);
            assertTrue(error(metadata).contains("tag data exceeds profile size"));
            assertEquals(0, reads[0]);
        }
    }

    @Test
    public void reusesSharedTagData() throws Exception
    {
        byte[] bytes = profile(3, 4);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        for (int i = 0; i < 3; i++)
            buffer.putInt(136 + 12 * i, 168).putInt(140 + 12 * i, 4);
        buffer.putInt(168, 0x12345678);
        Metadata metadata = extract(bytes);
        assertFalse(metadata.hasErrors());
        IccDirectory directory = metadata.getFirstDirectoryOfType(IccDirectory.class);
        assertArrayEquals(new byte[]{0x12, 0x34, 0x56, 0x78}, directory.getByteArray(0x74616700));
        assertSame(directory.getByteArray(0x74616700), directory.getByteArray(0x74616701));
        assertSame(directory.getByteArray(0x74616701), directory.getByteArray(0x74616702));
    }

    @Test
    public void reusesRepeatedProfileRangesThroughPublicJpegApi() throws Exception
    {
        byte[] bytes = profile(256, 4);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        for (int i = 0; i < 256; i++)
            buffer.putInt(136 + 12 * i, 0).putInt(140 + 12 * i, bytes.length);
        ByteArrayOutputStream jpeg = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(jpeg);
        output.writeShort(0xffd8);
        output.writeShort(0xffe2);
        output.writeShort(bytes.length + 16);
        output.writeBytes("ICC_PROFILE");
        output.write(new byte[]{0, 1, 1});
        output.write(bytes);
        output.writeShort(0xffd9);

        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(jpeg.toByteArray()));
        assertFalse(metadata.hasErrors());
        IccDirectory directory = metadata.getFirstDirectoryOfType(IccDirectory.class);
        assertNotNull(directory);
        assertEquals(256, directory.getInt(IccDirectory.TAG_TAG_COUNT));
        assertArrayEquals(bytes, directory.getByteArray(0x74616700));
        for (int i = 1; i < 256; i++)
            assertSame(directory.getByteArray(0x74616700), directory.getByteArray(0x74616700 + i));
    }

    @Test
    public void boundsCopiesOfDistinctOverlappingRanges() throws Exception
    {
        byte[] bytes = profile(3, 4);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        for (int i = 0; i < 3; i++)
            buffer.putInt(136 + 12 * i, i).putInt(140 + 12 * i, bytes.length - 4);
        final int[] copied = {0};
        Metadata metadata = new Metadata();
        new IccReader().extract(new ByteArrayReader(bytes) {
            @Override
            public byte[] getBytes(int offset, int count) throws IOException
            {
                copied[0] += count;
                return super.getBytes(offset, count);
            }
        }, metadata);
        assertTrue(error(metadata).contains("copy budget"));
        assertEquals(bytes.length - 4, copied[0]);
    }

    @Test
    public void readsNonOverlappingTagPayloads() throws Exception
    {
        byte[] bytes = profile(2, 8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putInt(136, 156).putInt(140, 4);
        buffer.putInt(148, 160).putInt(152, 4);
        buffer.putInt(156, 0x12345678).putInt(160, 0x11223344);
        Metadata metadata = extract(bytes);
        assertFalse(metadata.hasErrors());
        IccDirectory directory = metadata.getFirstDirectoryOfType(IccDirectory.class);
        assertArrayEquals(Arrays.copyOfRange(bytes, 156, 160), directory.getByteArray(0x74616700));
        assertArrayEquals(Arrays.copyOfRange(bytes, 160, 164), directory.getByteArray(0x74616701));
    }

    @Test
    public void acceptsProfileSizeAtLimit() throws Exception
    {
        byte[] bytes = profile(0, 0);
        ByteBuffer.wrap(bytes).putInt(0, 16 * 1024 * 1024);
        assertFalse(extract(bytes).hasErrors());
    }

    @Test
    public void reportsTruncatedTagData() throws Exception
    {
        byte[] bytes = profile(1, 4);
        ByteBuffer.wrap(bytes).putInt(136, 144).putInt(140, 4);
        Metadata metadata = extract(Arrays.copyOf(bytes, 146));
        assertTrue(error(metadata).contains("Exception reading ICC profile"));
    }

    private static Metadata extract(byte[] bytes)
    {
        Metadata metadata = new Metadata();
        new IccReader().extract(new ByteArrayReader(bytes), metadata);
        return metadata;
    }

    private static String error(Metadata metadata)
    {
        IccDirectory directory = metadata.getFirstDirectoryOfType(IccDirectory.class);
        assertNotNull(directory);
        assertTrue(directory.hasErrors());
        return directory.getErrors().iterator().next();
    }

    private static byte[] profile(int tagCount, int payloadLength)
    {
        ByteBuffer buffer = ByteBuffer.allocate(132 + 12 * tagCount + payloadLength);
        buffer.putInt(0, buffer.capacity());
        buffer.putShort(24, (short)2026).putShort(26, (short)10).putShort(28, (short)20);
        buffer.putInt(36, 0x61637370).putInt(128, tagCount);
        for (int i = 0; i < tagCount; i++)
            buffer.putInt(132 + 12 * i, 0x74616700 + i);
        return buffer.array();
    }
}
