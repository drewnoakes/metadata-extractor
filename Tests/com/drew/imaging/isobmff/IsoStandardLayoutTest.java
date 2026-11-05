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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IsoStandardLayoutTest
{
    @Test
    public void testChildBoxesAfterStandardHeadersAreFound() throws Exception
    {
        final List<String> reads = new ArrayList<String>();
        IsoBoxReader recorder = IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box) throws IOException
            {
                reads.add(box.path() + "=" + box.payload().getUInt8() + " " + IsoStandardLayout.handlerType(box));
            }
        }, "meta/hdlr", "meta/iinf/infe", "meta/iref/thmb", "moov/trak/mdia/minf/stbl/stsd/mp4a/esds",
            "moov/trak/mdia/minf/stbl/stsd/avc1/avcC");

        // meta and iref are FullBoxes; iinf version 0 has a 16-bit entry count, version 1 a 32-bit one
        byte[] meta = box("meta", new byte[4], box("hdlr", new byte[] {1}), box("iinf", new byte[] {0, 0, 0, 0, 0, 1}, box("infe", new byte[] {2})),
            box("iref", new byte[4], box("thmb", new byte[] {3})));
        // an audio sample entry has 28 bytes before its boxes, also in version 1 (in a stsd of
        // version 1); a visual one has 78
        byte[] soundEntry = box("mp4a", new byte[8], new byte[] {0, 1}, new byte[18], box("esds", new byte[] {4}));
        byte[] visualEntry = box("avc1", new byte[78], box("avcC", new byte[] {5}));
        byte[] file = join(meta, box("moov",
            trak("soun", box("stsd", new byte[] {1, 0, 0, 0}, int32(1), soundEntry)),
            trak("vide", box("stsd", new byte[4], int32(1), visualEntry))));

        List<String> errors = new ArrayList<String>();
        new IsoBoxReaders(IsoStandardLayout.INSTANCE, Collections.singletonList(recorder))
            .read(new ByteArrayInputStream(file), IsoBoxContext.root(new Metadata(), collect(errors)));

        assertEquals(Arrays.asList("meta/hdlr=1 null", "meta/iinf/infe=2 null", "meta/iref/thmb=3 null",
            "moov/trak/mdia/minf/stbl/stsd/mp4a/esds=4 soun", "moov/trak/mdia/minf/stbl/stsd/avc1/avcC=5 vide"), reads);
        assertTrue(errors.toString(), errors.isEmpty());
    }

    @Test
    public void testOnlySampleEntriesWithKnownFieldsAreEntered() throws Exception
    {
        final List<String> reads = new ArrayList<String>();
        IsoBoxReader recorder = IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box) throws IOException
            {
                reads.add(box.parent().box().type + "/" + box.box().type + "=" + box.payload().getUInt8());
            }
        }, "moov/trak/mdia/minf/stbl/stsd/*/btrt", "moov/trak/mdia/minf/stbl/stsd/*/keys");

        // a tx3g subtitle entry has 30 bytes of its own after the 8 every entry starts with; its
        // display flags of 0, read as a box header, would be a box of size 0
        byte[] textEntry = box("tx3g", new byte[38], box("btrt", new byte[] {1}));
        byte[] metadataEntry = box("mebx", new byte[8], box("keys", new byte[] {2}));
        byte[] imageEntry = box("hvc1", new byte[78], box("btrt", new byte[] {3}));
        byte[] file = box("moov",
            trak("sbtl", box("stsd", new byte[4], int32(1), textEntry)),
            trak("meta", box("stsd", new byte[4], int32(1), metadataEntry)),
            trak("pict", box("stsd", new byte[4], int32(1), imageEntry)));

        List<String> errors = new ArrayList<String>();
        new IsoBoxReaders(IsoStandardLayout.INSTANCE, Collections.singletonList(recorder))
            .read(new ByteArrayInputStream(file), IsoBoxContext.root(new Metadata(), collect(errors)));

        assertEquals(Arrays.asList("mebx/keys=2", "hvc1/btrt=3"), reads);
        assertTrue(errors.toString(), errors.isEmpty());
    }

    @Test
    public void testTheLayoutReadsOnlyWhereTheWalkGoesAnyway() throws Exception
    {
        final List<String> layoutReads = new ArrayList<String>();
        IsoStandardLayout layout = new IsoStandardLayout()
        {
            @Override
            public void readBox(@NotNull IsoBoxContext box) throws IOException
            {
                layoutReads.add(box.path());
                super.readBox(box);
            }
        };
        IsoBoxReader stsd = IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box)
            {
            }
        }, "moov/trak/mdia/minf/stbl/stsd");
        IsoBoxReader ftyp = IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box)
            {
            }
        }, "ftyp");
        byte[] file = join(box("ftyp", ascii("isom")), box("moov", trak("vide", box("stsd", new byte[4], int32(0)))));

        List<String> errors = new ArrayList<String>();
        new IsoBoxReaders(layout, Collections.singletonList(ftyp)).read(new ByteArrayInputStream(file), IsoBoxContext.root(new Metadata(), collect(errors)));
        assertTrue(layoutReads.isEmpty());
        new IsoBoxReaders(layout, Arrays.asList(ftyp, stsd)).read(new ByteArrayInputStream(file), IsoBoxContext.root(new Metadata(), collect(errors)));
        assertEquals(Collections.singletonList("moov/trak/mdia/hdlr"), layoutReads);
        assertTrue(errors.toString(), errors.isEmpty());
    }

    private static byte[] trak(String handler, byte[] stsd)
    {
        byte[] hdlr = box("hdlr", new byte[8], ascii(handler), new byte[13]);
        return box("trak", box("mdia", hdlr, box("minf", box("stbl", stsd))));
    }

    private static IsoBoxContext.ErrorHandler collect(final List<String> errors)
    {
        return new IsoBoxContext.ErrorHandler()
        {
            @Override
            public void error(@Nullable IsoBox box, @NotNull String message, @Nullable IOException cause)
            {
                errors.add(message);
            }
        };
    }

    private static byte[] box(String type, byte[]... payload)
    {
        byte[] content = join(payload);
        return join(int32(8 + content.length), ascii(type), content);
    }

    private static byte[] int32(int value)
    {
        return new byte[] {(byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value};
    }

    private static byte[] ascii(String value)
    {
        return value.getBytes(StandardCharsets.US_ASCII);
    }

    private static byte[] join(byte[]... parts)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] part : parts)
            out.write(part, 0, part.length);
        return out.toByteArray();
    }
}
