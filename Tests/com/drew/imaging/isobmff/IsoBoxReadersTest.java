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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class IsoBoxReadersTest
{
    private static final IsoBoxLayout PLAIN = new IsoBoxLayout()
    {
        @Override
        public boolean enterContainer(@NotNull IsoBoxContext box)
        {
            return true;
        }
    };

    private static final String USERTYPE = "00112233-4455-6677-8899-aabbccddeeff";
    private static final byte[] USERTYPE_BYTES = {0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77,
        (byte) 0x88, (byte) 0x99, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff};

    @Test
    public void testOnlyRegisteredPathsAreRead() throws Exception
    {
        Run run = run(PLAIN, join(
            box("moov", join(box("trak", box("tkhd", new byte[] {1})), box("mvhd", new byte[] {2}))),
            box("mdat", new byte[1000]),
            box("free", new byte[] {3})),
            "moov/trak/tkhd", "free");
        assertEquals(Arrays.asList("moov/trak/tkhd=1", "free=3"), run.reads);
        assertTrue(run.errors.isEmpty());
    }

    @Test
    public void testWildcardMatchesAnyBox() throws Exception
    {
        Run run = run(PLAIN, box("ilst", join(box("Anam", box("data", new byte[] {1})), box("Aart", box("data", new byte[] {2})))),
            "ilst/*/data");
        assertEquals(Arrays.asList("ilst/Anam/data=1", "ilst/Aart/data=2"), run.reads);
    }

    @Test
    public void testUuidPathsMatchTheirUsertype() throws Exception
    {
        byte[] other = new byte[16];
        Run run = run(PLAIN, join(box("uuid", join(USERTYPE_BYTES, new byte[] {1})), box("uuid", join(other, new byte[] {2}))),
            "uuid:" + USERTYPE);
        assertEquals(Collections.singletonList("uuid=1"), run.reads);
        run = run(PLAIN, join(box("uuid", join(USERTYPE_BYTES, new byte[] {1})), box("uuid", join(other, new byte[] {2}))),
            "uuid");
        assertEquals(Arrays.asList("uuid=1", "uuid=2"), run.reads);
    }

    @Test
    public void testSeveralReadersEachReadTheWholePayloadInOrder() throws Exception
    {
        final List<String> reads = new ArrayList<String>();
        IsoBoxReader first = recorder("first", reads, "hdlr");
        IsoBoxReader second = recorder("second", reads, "hdlr");
        read(new IsoBoxReaders(PLAIN, Arrays.asList(first, second)), box("hdlr", new byte[] {7, 8}), new ArrayList<String>());
        assertEquals(Arrays.asList("first:hdlr=7", "second:hdlr=7"), reads);
    }

    @Test
    public void testPositionsAreTheSameWithOneReaderOrSeveral() throws Exception
    {
        for (int size : new int[] {100, 40 * 1024}) {
            final List<String> positions = new ArrayList<String>();
            IsoBoxReaders.Callback callback = new IsoBoxReaders.Callback()
            {
                @Override
                public void read(@NotNull IsoBoxContext box) throws IOException
                {
                    long before = box.payload().getPosition();
                    box.payload().getUInt32();
                    positions.add(before + "-" + box.payload().getPosition());
                }
            };
            byte[] file = join(box("free", new byte[12]), box("data", new byte[size]));
            IsoBoxReaders one = new IsoBoxReaders(PLAIN, Collections.singletonList(IsoBoxReaders.on(callback, "data")));
            IsoBoxReaders two = new IsoBoxReaders(PLAIN, Arrays.asList(IsoBoxReaders.on(callback, "data"), IsoBoxReaders.on(callback, "data")));
            for (IsoBoxReaders readers : Arrays.asList(one, two))
                readers.read(new IsoBoxIterator(new ByteArrayInputStream(file)), IsoBoxContext.root(new Metadata(), collect(new ArrayList<String>())));
            assertEquals(Arrays.asList("0-4", "0-4", "0-4"), positions);
        }
    }

    @Test
    public void testABoxCanBeReadAndDescendedInto() throws Exception
    {
        IsoBoxLayout header = new IsoBoxLayout()
        {
            @Override
            public boolean enterContainer(@NotNull IsoBoxContext box) throws IOException
            {
                if (box.box().type.equals("stsd"))
                    box.payload().skip(8);
                return true;
            }
        };
        Run run = run(header, box("stsd", join(new byte[] {0, 0, 0, 0, 0, 0, 0, 1}, box("mp4a", new byte[] {5}))),
            "stsd", "stsd/mp4a");
        assertEquals(Arrays.asList("stsd=0", "stsd/mp4a=5"), run.reads);
    }

    @Test
    public void testLayoutCanRefuseToDescend() throws Exception
    {
        IsoBoxLayout none = new IsoBoxLayout()
        {
            @Override
            public boolean enterContainer(@NotNull IsoBoxContext box)
            {
                return false;
            }
        };
        assertTrue(run(none, box("moov", box("mvhd", new byte[] {1})), "moov/mvhd").reads.isEmpty());
    }

    @Test
    public void testStateIsScopedToItsContainer() throws Exception
    {
        final List<String> seen = new ArrayList<String>();
        IsoBoxReader handler = IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box) throws IOException
            {
                box.ancestor("trak").put(String.class, "track" + box.payload().getByte());
            }
        }, "moov/trak/mdia/hdlr");
        IsoBoxReader sample = IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box)
            {
                seen.add(box.get(String.class));
            }
        }, "moov/trak/stbl/stsd", "moov/udta");
        byte[] file = box("moov", join(
            box("trak", join(box("mdia", box("hdlr", new byte[] {1})), box("stbl", box("stsd", new byte[0])))),
            box("trak", join(box("mdia", box("hdlr", new byte[] {2})), box("stbl", box("stsd", new byte[0])))),
            box("udta", new byte[0])));
        read(new IsoBoxReaders(PLAIN, Arrays.asList(handler, sample)), file, new ArrayList<String>());
        assertEquals(Arrays.asList("track1", "track2", null), seen);
    }

    @Test
    public void testAFailingReaderDoesNotStopTheWalk() throws Exception
    {
        IsoBoxReader failing = IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box) throws IOException
            {
                throw new IOException("bad");
            }
        }, "fail");
        final List<String> reads = new ArrayList<String>();
        List<String> errors = new ArrayList<String>();
        read(new IsoBoxReaders(PLAIN, Arrays.asList(failing, recorder("ok", reads, "next"))),
            join(box("fail", new byte[1]), box("next", new byte[] {4})), errors);
        assertEquals(Collections.singletonList("Error reading box 'fail': bad (IOException)"), errors);
        assertEquals(Collections.singletonList("ok:next=4"), reads);
    }

    @Test
    public void testTruncatedInputIsReportedOnceAndEndsTheWalk() throws Exception
    {
        List<String> errors = new ArrayList<String>();
        IsoBoxReader reader = IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box) throws IOException
            {
                box.payloadBytes();
            }
        }, "moov/trak/mdia/mdhd");
        byte[] file = join(header(8 + 8 + 8 + 8 + 100, "moov"), header(8 + 8 + 8 + 100, "trak"), header(8 + 8 + 100, "mdia"), header(8 + 100, "mdhd"), new byte[10]);
        read(new IsoBoxReaders(PLAIN, Collections.singletonList(reader)), file, errors);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0), errors.get(0).endsWith("(TruncatedInputException)"));
    }

    @Test
    public void testTruncatedSmallPayloadIsReportedOnce() throws Exception
    {
        List<String> errors = new ArrayList<String>();
        IsoBoxReader reader = IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box) throws IOException
            {
                box.payload().getUInt32();
                box.payload().getUInt32();
            }
        }, "moov/mvhd");
        // moov claims more than is read in one piece, so it is streamed and mvhd alone is read into memory
        read(new IsoBoxReaders(PLAIN, Collections.singletonList(reader)), join(header(8 + 8 + 100 + 20000, "moov"), header(8 + 100, "mvhd"), new byte[6]), errors);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0), errors.get(0).endsWith("(TruncatedInputException)"));
    }

    @Test
    public void testSharedPayloadsAboveTheLimitAreSkipped() throws Exception
    {
        final List<String> reads = new ArrayList<String>();
        List<String> errors = new ArrayList<String>();
        IsoBoxReaders readers = new IsoBoxReaders(PLAIN, Arrays.asList(recorder("a", reads, "data"), recorder("b", reads, "data")), 10);
        IsoBoxContext root = IsoBoxContext.root(new Metadata(), collect(errors));
        readers.read(new IsoBoxIterator(new ByteArrayInputStream(join(box("data", new byte[100]), box("data", new byte[] {1})))), root);
        assertEquals(Arrays.asList("a:data=1", "b:data=1"), reads);
        assertEquals(1, errors.size());
    }

    @Test
    public void testBoxesInsideASharedPayloadKeepInputOffsets() throws Exception
    {
        final List<Long> offsets = new ArrayList<Long>();
        IsoBoxReader offset = IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box)
            {
                offsets.add(box.box().offset);
            }
        }, "udta/meta");
        List<String> reads = new ArrayList<String>();
        read(new IsoBoxReaders(PLAIN, Arrays.asList(recorder("udta", reads, "udta"), offset)),
            join(box("free", new byte[4]), box("udta", join(box("Axyz", new byte[2]), box("meta", new byte[0])))), new ArrayList<String>());
        assertEquals(Collections.singletonList(12L + 8 + 10), offsets);
    }

    @Test
    public void testAWalkCanBeReusedForSeveralFiles() throws Exception
    {
        final List<String> reads = new ArrayList<String>();
        IsoBoxReaders readers = new IsoBoxReaders(PLAIN, Collections.singletonList(recorder("r", reads, "moov/mvhd")));
        read(readers, box("moov", box("mvhd", new byte[] {1})), new ArrayList<String>());
        read(readers, box("moov", box("mvhd", new byte[] {2})), new ArrayList<String>());
        assertEquals(Arrays.asList("r:moov/mvhd=1", "r:moov/mvhd=2"), reads);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPathsAreRejected()
    {
        new IsoBoxReaders(PLAIN, Collections.singletonList(recorder("r", new ArrayList<String>(), "moov/toolong")));
    }

    private static Run run(IsoBoxLayout layout, byte[] file, String... paths) throws IOException
    {
        Run run = new Run();
        IsoBoxReader reader = recorder(null, run.reads, paths);
        read(new IsoBoxReaders(layout, Collections.singletonList(reader)), file, run.errors);
        return run;
    }

    private static void read(IsoBoxReaders readers, byte[] file, List<String> errors)
    {
        IsoBoxContext root = IsoBoxContext.root(new Metadata(), collect(errors));
        readers.read(new IsoBoxIterator(new ByteArrayInputStream(file)), root);
    }

    private static IsoBoxContext.ErrorHandler collect(final List<String> errors)
    {
        return new IsoBoxContext.ErrorHandler()
        {
            @Override
            public void error(@NotNull String message, @Nullable IOException cause)
            {
                errors.add(message + (cause == null ? "" : " (" + cause.getClass().getSimpleName() + ")"));
            }
        };
    }

    /** Records the path and the first payload byte (or 0 for an empty payload) of each box it reads. */
    private static IsoBoxReader recorder(@Nullable final String name, final List<String> reads, String... paths)
    {
        return IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box) throws IOException
            {
                byte[] payload = box.payloadBytes();
                reads.add((name == null ? "" : name + ":") + box.path() + "=" + (payload.length == 0 ? 0 : payload[0]));
            }
        }, paths);
    }

    private static final class Run
    {
        final List<String> reads = new ArrayList<String>();
        final List<String> errors = new ArrayList<String>();
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
        out.write(type.getBytes("ISO-8859-1"));
        return bytes.toByteArray();
    }

    private static byte[] join(byte[]... parts) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (byte[] part : parts)
            bytes.write(part);
        return bytes.toByteArray();
    }
}
