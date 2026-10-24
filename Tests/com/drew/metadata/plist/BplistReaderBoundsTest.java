package com.drew.metadata.plist;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.Charsets;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.makernotes.AppleMakernoteDirectory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class BplistReaderBoundsTest
{
    @Test
    public void rejectsObjectCountsThatDoNotFitTheOffsetTable() throws Exception
    {
        for (long count : new long[]{0, -1, Long.MIN_VALUE, 0x7ffffff0L, 0x100000001L, Long.MAX_VALUE})
            assertInvalid(plist(count, 0, 10, 1), "object count");
    }

    @Test
    public void rejectsInvalidTopObjects() throws Exception
    {
        for (long top : new long[]{-1, 1, 0x100000000L, Long.MAX_VALUE})
            assertInvalid(plist(1, top, 10, 1), "top object");
    }

    @Test
    public void rejectsInvalidOffsetTables() throws Exception
    {
        for (long offset : new long[]{-1, 7, 12, 0x10000000aL, Long.MAX_VALUE})
            assertInvalid(plist(1, 0, offset, 1), "offset table");
        for (int size : new int[]{0, 3, 16, 255})
            assertInvalid(plist(1, 0, 10, size), "integer size");
    }

    @Test
    public void rejectsTruncatedTrailer() throws Exception
    {
        assertInvalid("bplist00".getBytes(Charsets.ASCII), "Truncated");
    }

    @Test
    public void rejectsUnsupportedObjectSizesWithIOException() throws Exception
    {
        assertInvalid(singleObject(new byte[]{0x1f}), "integer size");
        assertInvalid(singleObject(new byte[]{0x4f, 0x50}), "Invalid size marker");
        assertInvalid(singleObject(new byte[]{0x4f, 0x12}), "data size width");
    }

    @Test
    public void readsUnsignedOneByteDataLength() throws Exception
    {
        byte[] object = new byte[131];
        object[0] = 0x4f;
        object[1] = 0x10;
        object[2] = (byte)128;
        object[3] = 42;
        byte[] data = (byte[])BplistReader.parse(singleObject(object)).getObjects().get(0);
        assertEquals(128, data.length);
        assertEquals(42, data[0]);
    }

    @Test
    public void readsAllSupportedOffsetWidths() throws Exception
    {
        for (int size : new int[]{1, 2, 4, 8}) {
            BplistReader.PropertyListResults result = BplistReader.parse(plist(1, 0, 10, size));
            assertEquals(1, result.getObjects().size());
            assertEquals(Byte.valueOf((byte)42), result.getObjects().get(0));
        }
    }

    @Test
    public void readsUnsignedOneByteOffset() throws Exception
    {
        byte[] bytes = new byte[235];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put("bplist00".getBytes(Charsets.ASCII));
        buffer.put(200, (byte)0x10).put(201, (byte)42).put(202, (byte)200);
        buffer.put(209, (byte)1).put(210, (byte)1);
        buffer.putLong(211, 1).putLong(219, 0).putLong(227, 202);
        assertEquals(Byte.valueOf((byte)42), BplistReader.parse(bytes).getObjects().get(0));
    }

    @Test
    public void readsOffsetTableIndependentlyOfTopObject() throws Exception
    {
        byte[] bytes = new byte[46];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put("bplist00".getBytes(Charsets.ASCII));
        buffer.put(new byte[]{0x10, 42, 0x10, 43, 8, 10});
        buffer.put(20, (byte)1).put(21, (byte)1);
        buffer.putLong(22, 2).putLong(30, 1).putLong(38, 12);
        assertEquals(Byte.valueOf((byte)43), BplistReader.parse(bytes).getObjects().get(1));
    }

    @Test
    public void rejectsObjectOffsetsOutsideObjectData() throws Exception
    {
        for (int offset : new int[]{0, 7, 10, 255}) {
            byte[] bytes = plist(1, 0, 10, 1);
            bytes[10] = (byte)offset;
            assertInvalid(bytes, "object offset");
        }
        byte[] bytes = plist(1, 0, 10, 8);
        ByteBuffer.wrap(bytes).putLong(10, Long.MIN_VALUE);
        assertInvalid(bytes, "object offset");
    }

    @Test
    public void reportsInvalidRunTimeThroughPublicTiffApi() throws Exception
    {
        for (long count : new long[]{0x7ffffff0L, -1, 0x100000001L}) {
            byte[] plist = plist(count, 0, 10, 1);
            ByteBuffer buffer = ByteBuffer.allocate(76 + plist.length);
            buffer.putShort((short)0x4d4d).putShort((short)42).putInt(8);
            buffer.putShort((short)1).putShort((short)0x8769).putShort((short)4).putInt(1).putInt(26).putInt(0);
            buffer.putShort((short)1).putShort((short)0x927c).putShort((short)7).putInt(32 + plist.length).putInt(44).putInt(0);
            buffer.put("Apple iOS\0".getBytes(Charsets.ASCII)).putInt(0);
            buffer.putShort((short)1).putShort((short)3).putShort((short)7).putInt(plist.length).putInt(32).putInt(0);
            buffer.put(plist);
            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(buffer.array()));
            AppleMakernoteDirectory directory = metadata.getFirstDirectoryOfType(AppleMakernoteDirectory.class);
            assertNotNull(directory);
            assertTrue(directory.getErrors().iterator().next().contains("Invalid bplist object count"));
        }
    }

    private static byte[] plist(long count, long topObject, long tableOffset, int offsetSize)
    {
        int tableSize = offsetSize == 1 || offsetSize == 2 || offsetSize == 4 || offsetSize == 8 ? offsetSize : 1;
        ByteBuffer buffer = ByteBuffer.allocate(10 + tableSize + 32);
        buffer.put("bplist00".getBytes(Charsets.ASCII)).put((byte)0x10).put((byte)42);
        for (int i = 1; i < tableSize; i++)
            buffer.put((byte)0);
        buffer.put((byte)8);
        buffer.put(new byte[6]).put((byte)offsetSize).put((byte)1);
        buffer.putLong(count).putLong(topObject).putLong(tableOffset);
        return buffer.array();
    }

    private static byte[] singleObject(byte[] object)
    {
        ByteBuffer buffer = ByteBuffer.allocate(8 + object.length + 1 + 32);
        buffer.put("bplist00".getBytes(Charsets.ASCII)).put(object).put((byte)8);
        buffer.put(new byte[6]).put((byte)1).put((byte)1);
        buffer.putLong(1).putLong(0).putLong(8 + object.length);
        return buffer.array();
    }

    private static void assertInvalid(byte[] bytes, String message) throws Exception
    {
        try {
            BplistReader.parse(bytes);
            fail("Expected IOException containing: " + message);
        } catch (IOException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(message));
        }
    }
}
