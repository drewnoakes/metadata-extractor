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
package com.drew.imaging.isobmff;

import com.drew.lang.SequentialReader;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class IsoBoxIteratorTest
{
    @Test
    public void testStandardLargeAndUuidHeaders() throws Exception
    {
        Boxes boxes = read(join(
            box("data", new byte[] {1}),
            join(header(1, "data"), longBytes(17), new byte[] {2}),
            box("uuid", join(new byte[16], new byte[] {3}))));

        assertTrue(boxes.errors.isEmpty());
        assertEquals(Arrays.asList("data", "data", "uuid"), boxes.types);
        assertArrayEquals(new byte[] {1}, boxes.payloads.get(0));
        assertArrayEquals(new byte[] {2}, boxes.payloads.get(1));
        assertArrayEquals(new byte[] {3}, boxes.payloads.get(2));
        assertArrayEquals(new byte[16], boxes.boxes.get(2).usertype);
    }

    @Test
    public void testOffsets() throws Exception
    {
        Boxes boxes = read(join(
            box("data", new byte[] {1, 2}),
            header(1, "data"), longBytes(19), new byte[] {3, 4, 5},
            box("uuid", join(new byte[16], new byte[] {6}))));
        assertEquals(0, boxes.boxes.get(0).offset);
        assertEquals(8, boxes.boxes.get(0).payloadOffset);
        assertEquals(2, boxes.boxes.get(0).payloadSize);
        assertEquals(10, boxes.boxes.get(1).offset);
        assertEquals(26, boxes.boxes.get(1).payloadOffset);
        assertEquals(3, boxes.boxes.get(1).payloadSize);
        assertEquals(29, boxes.boxes.get(2).offset);
        assertEquals(53, boxes.boxes.get(2).payloadOffset);
        assertEquals(1, boxes.boxes.get(2).payloadSize);
    }

    @Test
    public void testCleanEofAndTruncatedHeaders() throws Exception
    {
        assertTrue(read(new byte[0]).errors.isEmpty());
        byte[][] headers = {
            header(8, "data"),
            join(header(1, "data"), longBytes(16)),
            join(header(24, "uuid"), new byte[16])
        };
        for (byte[] header : headers) {
            for (int length = 1; length < header.length; length++)
                assertFalse("Truncated header length " + length,
                    read(Arrays.copyOf(header, length)).errors.isEmpty());
        }
    }

    @Test
    public void testStreamFailureIsReported()
    {
        Boxes boxes = read(new InputStream() {
            @Override
            public int read() throws IOException
            {
                throw new IOException("Read failed");
            }
        });
        assertEquals(Arrays.asList("Read failed"), boxes.errors);
    }

    @Test
    public void testChildrenCannotConsumeFollowingSibling() throws Exception
    {
        byte[][] children = {
            header(16, "free"),
            header(16, "data"),
            header(16, "moov"),
            header(1, "data"),
            header(24, "uuid"),
            new byte[] {0, 0, 0},
            join(header(1, "uuid"), longBytes(32))
        };
        for (byte[] child : children) {
            Boxes boxes = read(join(box("moov", child), box("data", new byte[] {42})));
            assertFalse(boxes.errors.isEmpty());
            assertEquals(Arrays.asList("data"), boxes.types);
            assertArrayEquals(new byte[] {42}, boxes.payloads.get(0));
        }
    }

    @Test
    public void testInvalidSizes() throws Exception
    {
        for (long size : new long[] {-1, Long.MIN_VALUE, 0, 7, 15}) {
            assertFalse(read(join(header(1, "data"), longBytes(size))).errors.isEmpty());
        }
        assertFalse(read(header(7, "data")).errors.isEmpty());
        assertFalse(read(header(23, "uuid")).errors.isEmpty());
        assertFalse(read(join(box("free", new byte[24]),
            header(1, "data"), longBytes(Long.MAX_VALUE))).errors.isEmpty());
    }

    @Test
    public void testDepthLimitPreservesFollowingSibling() throws Exception
    {
        byte[] allowed = box("data", new byte[] {42});
        for (int depth = 0; depth < IsoBoxIterator.MAX_DEPTH; depth++)
            allowed = box("moov", allowed);
        Boxes boxes = read(allowed);
        assertTrue(boxes.errors.isEmpty());
        assertEquals(Arrays.asList("data"), boxes.types);

        ByteArrayOutputStream nested = new ByteArrayOutputStream();
        for (int depth = 10000; depth > 0; depth--)
            nested.write(header(depth * 8, "moov"));
        boxes = read(join(nested.toByteArray(), box("data", new byte[] {42})));
        assertEquals(Arrays.asList("Error reading box 'moov': Maximum box nesting depth exceeded"), boxes.errors);
        assertEquals(Arrays.asList("data"), boxes.types);
    }

    @Test
    public void testEofTerminatedPayloads() throws Exception
    {
        Boxes boxes = read(join(header(0, "uuid"), new byte[16], new byte[] {42}));
        assertTrue(boxes.errors.isEmpty());
        assertArrayEquals(new byte[] {42}, boxes.payloads.get(0));

        boxes = read(join(box("moov", join(header(0, "data"), new byte[] {1})),
            box("data", new byte[] {2})));
        assertTrue(boxes.errors.isEmpty());
        assertEquals(2, boxes.payloads.size());
        assertArrayEquals(new byte[] {1}, boxes.payloads.get(0));
        assertArrayEquals(new byte[] {2}, boxes.payloads.get(1));
    }

    @Test
    public void testEofBufferLimit() throws Exception
    {
        final byte[] header = header(0, "data");
        final int limit = 16 * 1024 * 1024;
        for (final int length : new int[] {limit, limit + 1}) {
            Boxes boxes = read(new InputStream() {
                private int position;

                @Override
                public int read()
                {
                    if (position < header.length)
                        return header[position++] & 0xFF;
                    return position++ < header.length + length ? 0 : -1;
                }
            });
            if (length == limit) {
                assertTrue(boxes.errors.isEmpty());
                assertEquals(limit, boxes.payloads.get(0).length);
            } else {
                assertEquals(Arrays.asList("Error reading box 'data': EOF-terminated box payload exceeds the 16 MiB limit"),
                    boxes.errors);
                assertTrue(boxes.types.isEmpty());
            }
        }
    }

    @Test
    public void testTruncatedPayloadIsReported() throws Exception
    {
        assertFalse(read(join(header(10, "data"), new byte[] {1})).errors.isEmpty());
    }

    @Test
    public void testFailurePreservesFollowingSibling() throws Exception
    {
        Boxes boxes = read(join(box("fail", new byte[0]), box("data", new byte[] {42})));
        assertEquals(Arrays.asList("data"), boxes.types);
        assertEquals(Arrays.asList("Error reading box 'fail': Invalid payload"), boxes.errors);
    }

    @Test
    public void testUnreadPayloadAndChildrenAreSkipped() throws Exception
    {
        IsoBoxIterator boxes = iterate(join(
            box("data", new byte[] {1, 2, 3}),
            box("moov", join(box("data", new byte[] {4}), box("data", new byte[] {5}))),
            box("data", new byte[] {6})));
        assertEquals("data", boxes.next().type);
        assertEquals(1, boxes.payload().getByte());
        assertEquals("moov", boxes.next().type);
        IsoBoxIterator children = boxes.children();
        assertEquals("data", children.next().type);
        assertEquals("data", boxes.next().type);
        assertArrayEquals(new byte[] {6}, boxes.payloadBytes());
        assertNull(boxes.next());
        assertNull(boxes.next());
    }

    @Test
    public void testPayloadReaderStopsAtTheBoxEnd() throws Exception
    {
        IsoBoxIterator boxes = iterate(join(box("data", new byte[] {1, 2}), box("data", new byte[] {3})));
        boxes.next();
        SequentialReader payload = boxes.payload();
        try {
            payload.getBytes(3);
            fail();
        } catch (IOException e) {
            assertEquals("Read of 3 bytes passes the end of the box payload", e.getMessage());
        }
        assertArrayEquals(new byte[] {1, 2}, payload.getBytes(2));
        boxes.next();
        assertArrayEquals(new byte[] {3}, boxes.payloadBytes());
    }

    @Test
    public void testChildrenAfterPartialRead() throws Exception
    {
        IsoBoxIterator boxes = iterate(join(
            box("wrap", join(new byte[] {9, 9}, box("data", new byte[] {1}), box("data", new byte[] {2}))),
            box("data", new byte[] {3})));
        boxes.next();
        boxes.payload().skip(2);
        IsoBoxIterator children = boxes.children();
        children.next();
        assertArrayEquals(new byte[] {1}, children.payloadBytes());
        children.next();
        assertArrayEquals(new byte[] {2}, children.payloadBytes());
        assertNull(children.next());
        boxes.next();
        assertArrayEquals(new byte[] {3}, boxes.payloadBytes());
    }

    @Test
    public void testLargePayloadIsReadCompletely() throws Exception
    {
        byte[] payload = new byte[300 * 1024 + 7];
        for (int i = 0; i < payload.length; i++)
            payload[i] = (byte) i;
        byte[] bytes = box("data", payload);
        assertArrayEquals(payload, read(bytes).payloads.get(0));
        Boxes boxes = read(new ByteArrayInputStream(bytes) {
            @Override
            public synchronized int available()
            {
                return 0;
            }
        });
        assertTrue(boxes.errors.isEmpty());
        assertArrayEquals(payload, boxes.payloads.get(0));
    }

    @Test
    public void testSizeZeroInsideAContainerIsResolved() throws Exception
    {
        IsoBoxIterator boxes = iterate(box("moov", join(header(0, "data"), new byte[] {1, 2})));
        boxes.next();
        IsoBoxIterator children = boxes.children();
        IsoBox box = children.next();
        assertEquals(10, box.size);
        assertEquals(2, box.payloadSize);
        assertArrayEquals(new byte[] {1, 2}, children.payloadBytes());
        assertNull(children.next());
    }

    @Test
    public void testTypeKeepsNonAsciiBytes() throws Exception
    {
        byte[] bytes = box("Axyz", new byte[] {1});
        bytes[4] = (byte) 0xA9;
        assertEquals("\u00A9xyz", iterate(bytes).next().type);
    }

    @Test
    public void testFalseSizeDoesNotAllocateTheClaimedSize() throws Exception
    {
        java.lang.management.ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        assumeTrue(bean instanceof com.sun.management.ThreadMXBean);
        com.sun.management.ThreadMXBean threads = (com.sun.management.ThreadMXBean) bean;
        byte[] bytes = join(header(1024 * 1024 * 1024, "data"), new byte[64]);
        long thread = Thread.currentThread().getId();
        long before = threads.getThreadAllocatedBytes(thread);
        Boxes boxes = read(bytes);
        long allocated = threads.getThreadAllocatedBytes(thread) - before;
        assertFalse(boxes.errors.isEmpty());
        assertTrue(boxes.payloads.isEmpty());
        assertTrue("allocated " + allocated + " bytes", allocated < 16 * 1024 * 1024);
    }

    @Test
    public void testPayloadPastTheEndOfInputNamesTheBox() throws Exception
    {
        IsoBoxIterator boxes = iterate(join(header(1000, "data"), new byte[] {1, 2}));
        boxes.next();
        try {
            boxes.payloadBytes();
            fail();
        } catch (EOFException e) {
            assertEquals("Box 'data' declares 992 bytes of payload, but the input ends before that", e.getMessage());
        }

        boxes = iterate(join(header(1000, "mdat"), new byte[] {1, 2}));
        boxes.next();
        try {
            boxes.next();
            fail();
        } catch (EOFException e) {
            assertEquals("Box 'mdat' declares 992 bytes of payload, but the input ends before that", e.getMessage());
        }
        assertNull(boxes.next());
    }

    @Test
    public void testByteArrayRange() throws Exception
    {
        byte[] bytes = join(new byte[] {9, 9, 9}, box("data", new byte[] {1}), box("free", new byte[0]), new byte[] {9});
        IsoBoxIterator boxes = new IsoBoxIterator(bytes, 3, bytes.length - 1);
        IsoBox box = boxes.next();
        assertEquals(3, box.offset);
        assertArrayEquals(new byte[] {1}, boxes.payloadBytes());
        assertEquals("free", boxes.next().type);
        assertNull(boxes.next());
    }

    @Test
    public void testTypeCode() throws Exception
    {
        byte[] bytes = join(header(9, "data"), new byte[] {1});
        bytes[4] = 0;
        bytes[5] = 0;
        bytes[6] = 1;
        bytes[7] = (byte) 0xA9;
        assertEquals(0x1A9, iterate(bytes).next().typeCode);
        assertEquals(0x64617461, iterate(box("data", new byte[0])).next().typeCode);
    }

    @Test(expected = IllegalStateException.class)
    public void testPayloadNeedsACurrentBox()
    {
        iterate(new byte[0]).payload();
    }

    private static IsoBoxIterator iterate(byte[] bytes)
    {
        return new IsoBoxIterator(new ByteArrayInputStream(bytes));
    }

    private static Boxes read(byte[] bytes)
    {
        return read(new ByteArrayInputStream(bytes));
    }

    private static Boxes read(InputStream inputStream)
    {
        Boxes boxes = new Boxes();
        boxes.read(new IsoBoxIterator(inputStream));
        return boxes;
    }

    private static byte[] box(String type, byte[] payload) throws IOException
    {
        return join(header(payload.length + 8, type), payload);
    }

    private static byte[] header(int size, String type) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        out.writeInt(size);
        out.writeBytes(type);
        return bytes.toByteArray();
    }

    private static byte[] longBytes(long value) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        new DataOutputStream(bytes).writeLong(value);
        return bytes.toByteArray();
    }

    private static byte[] join(byte[]... parts) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (byte[] part : parts)
            bytes.write(part);
        return bytes.toByteArray();
    }

    /**
     * Reads a box tree the way a format reader would: descends into {@code moov}, skips
     * {@code free}, fails on {@code fail} and collects every other payload.
     */
    private static class Boxes
    {
        final List<String> types = new ArrayList<String>();
        final List<IsoBox> boxes = new ArrayList<IsoBox>();
        final List<byte[]> payloads = new ArrayList<byte[]>();
        final List<String> errors = new ArrayList<String>();

        void read(IsoBoxIterator iterator)
        {
            try {
                for (IsoBox box = iterator.next(); box != null; box = iterator.next()) {
                    try {
                        if (box.type.equals("moov")) {
                            read(iterator.children());
                        } else if (box.type.equals("fail")) {
                            throw new IOException("Invalid payload");
                        } else if (!box.type.equals("free")) {
                            byte[] payload = iterator.payloadBytes();
                            types.add(box.type);
                            boxes.add(box);
                            payloads.add(payload);
                        }
                    } catch (EOFException e) {
                        throw e;
                    } catch (IOException e) {
                        errors.add("Error reading box '" + box.type + "': " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                errors.add(e.getMessage());
            }
        }
    }
}
