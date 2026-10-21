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
package com.drew.imaging.isobmff;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class IsoBoxWalkerTest
{
    @Test
    public void testStandardLargeAndUuidHeaders() throws Exception
    {
        Visitor visitor = walk(join(
            box("data", new byte[] {1}),
            join(header(1, "data"), longBytes(17), new byte[] {2}),
            box("uuid", join(new byte[16], new byte[] {3}))));

        assertTrue(visitor.errors.isEmpty());
        assertEquals(Arrays.asList("data", "data", "uuid"), visitor.types);
        assertArrayEquals(new byte[] {1}, visitor.payloads.get(0));
        assertArrayEquals(new byte[] {2}, visitor.payloads.get(1));
        assertArrayEquals(new byte[] {3}, visitor.payloads.get(2));
        assertArrayEquals(new byte[16], visitor.boxes.get(2).usertype);
    }

    @Test
    public void testCleanEofAndTruncatedHeaders() throws Exception
    {
        assertTrue(walk(new byte[0]).errors.isEmpty());
        byte[][] headers = {
            header(8, "data"),
            join(header(1, "data"), longBytes(16)),
            join(header(24, "uuid"), new byte[16])
        };
        for (byte[] header : headers) {
            for (int length = 1; length < header.length; length++)
                assertFalse("Truncated header length " + length,
                    walk(Arrays.copyOf(header, length)).errors.isEmpty());
        }
    }

    @Test
    public void testStreamFailureIsReported()
    {
        Visitor visitor = new Visitor();
        IsoBoxWalker.walk(new InputStream() {
            @Override
            public int read() throws IOException
            {
                throw new IOException("Read failed");
            }
        }, visitor);
        assertEquals(Arrays.asList("Read failed"), visitor.errors);
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
            Visitor visitor = walk(join(box("moov", child), box("data", new byte[] {42})));
            assertFalse(visitor.errors.isEmpty());
            assertEquals(Arrays.asList("data"), visitor.types);
            assertArrayEquals(new byte[] {42}, visitor.payloads.get(0));
        }
    }

    @Test
    public void testInvalidSizes() throws Exception
    {
        for (long size : new long[] {-1, Long.MIN_VALUE, 0, 7, 15}) {
            assertFalse(walk(join(header(1, "data"), longBytes(size))).errors.isEmpty());
        }
        assertFalse(walk(header(7, "data")).errors.isEmpty());
        assertFalse(walk(header(23, "uuid")).errors.isEmpty());
        assertFalse(walk(join(box("free", new byte[24]),
            header(1, "data"), longBytes(Long.MAX_VALUE))).errors.isEmpty());
    }

    @Test
    public void testDepthLimitPreservesFollowingSibling() throws Exception
    {
        byte[] allowed = box("data", new byte[] {42});
        for (int depth = 0; depth < 64; depth++)
            allowed = box("moov", allowed);
        Visitor visitor = walk(allowed);
        assertTrue(visitor.errors.isEmpty());
        assertEquals(Arrays.asList("data"), visitor.types);

        ByteArrayOutputStream nested = new ByteArrayOutputStream();
        for (int depth = 10000; depth > 0; depth--)
            nested.write(header(depth * 8, "moov"));
        visitor = walk(join(nested.toByteArray(), box("data", new byte[] {42})));
        assertEquals(Arrays.asList("Maximum box nesting depth exceeded"), visitor.errors);
        assertEquals(Arrays.asList("data"), visitor.types);
    }

    @Test
    public void testEofTerminatedPayloads() throws Exception
    {
        Visitor visitor = walk(join(header(0, "uuid"), new byte[16], new byte[] {42}));
        assertTrue(visitor.errors.isEmpty());
        assertArrayEquals(new byte[] {42}, visitor.payloads.get(0));

        visitor = walk(join(box("moov", join(header(0, "data"), new byte[] {1})),
            box("data", new byte[] {2})));
        assertTrue(visitor.errors.isEmpty());
        assertEquals(2, visitor.payloads.size());
        assertArrayEquals(new byte[] {1}, visitor.payloads.get(0));
        assertArrayEquals(new byte[] {2}, visitor.payloads.get(1));
    }

    @Test
    public void testEofBufferLimit() throws Exception
    {
        final byte[] header = header(0, "data");
        final int limit = 16 * 1024 * 1024;
        for (final int length : new int[] {limit, limit + 1}) {
            Visitor visitor = new Visitor();
            IsoBoxWalker.walk(new InputStream() {
                private int position;

                @Override
                public int read()
                {
                    if (position < header.length)
                        return header[position++] & 0xFF;
                    return position++ < header.length + length ? 0 : -1;
                }
            }, visitor);
            if (length == limit) {
                assertTrue(visitor.errors.isEmpty());
                assertEquals(limit, visitor.payloads.get(0).length);
            } else {
                assertEquals(Arrays.asList("EOF-terminated box payload exceeds the 16 MiB limit"), visitor.errors);
                assertTrue(visitor.types.isEmpty());
            }
        }
    }

    @Test
    public void testTruncatedPayloadIsReported() throws Exception
    {
        assertFalse(walk(join(header(10, "data"), new byte[] {1})).errors.isEmpty());
    }

    @Test
    public void testVisitorFailurePreservesFollowingSibling() throws Exception
    {
        Visitor visitor = walk(join(box("fail", new byte[0]), box("data", new byte[] {42})));
        assertEquals(Arrays.asList("data"), visitor.types);
        assertEquals(Arrays.asList("Error visiting box 'fail': Invalid payload"), visitor.errors);
    }

    private static Visitor walk(byte[] bytes)
    {
        Visitor visitor = new Visitor();
        IsoBoxWalker.walk(new ByteArrayInputStream(bytes), visitor);
        return visitor;
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

    private static class Visitor extends IsoBoxVisitor
    {
        final List<String> types = new ArrayList<String>();
        final List<IsoBox> boxes = new ArrayList<IsoBox>();
        final List<byte[]> payloads = new ArrayList<byte[]>();
        final List<String> errors = new ArrayList<String>();

        @Override
        protected boolean shouldRecurse(IsoBox box)
        {
            return box.type.equals("moov");
        }

        @Override
        protected IsoBoxVisitor processContainer(IsoBox box)
        {
            return this;
        }

        @Override
        protected boolean shouldVisit(IsoBox box)
        {
            return !box.type.equals("free");
        }

        @Override
        protected void visit(IsoBox box, byte[] payload) throws IOException
        {
            if (box.type.equals("fail"))
                throw new IOException("Invalid payload");
            types.add(box.type);
            boxes.add(box);
            payloads.add(payload);
        }

        @Override
        public void addError(String message)
        {
            errors.add(message);
        }
    }
}
