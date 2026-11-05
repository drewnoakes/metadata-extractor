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

import com.drew.lang.Charsets;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;

/**
 * What ISO/IEC 14496-12 puts in front of the child boxes of its containers: the FullBox header
 * of {@code meta} and {@code iref}, the entry counts of {@code iinf}, {@code stsd} and
 * {@code dref}, and the fixed fields of sample entries, whose size depends on the track's
 * handler type: 78 bytes for video, 28 for sound (in both versions of the audio sample entry)
 * and 8 otherwise. It also reads each track's {@code hdlr} for that, so it is a reader too.
 * QuickTime's longer sound descriptions of version 1 and 2 are not part of the standard.
 * <p>
 * A format layout extends it with what the format does differently and calls
 * {@code super.enterContainer} for everything else.
 */
public class IsoStandardLayout implements IsoBoxLayout, IsoBoxReader
{
    /** The standard layout, for formats without deviations of their own. */
    public static final IsoStandardLayout INSTANCE = new IsoStandardLayout();

    private static final class HandlerType
    {
        final String value;

        HandlerType(String value)
        {
            this.value = value;
        }
    }

    /** The handler type of the track {@code box} belongs to, such as {@code vide}, {@code soun} or {@code meta}. */
    @Nullable
    public static String handlerType(@NotNull IsoBoxContext box)
    {
        HandlerType type = box.get(HandlerType.class);
        return type == null ? null : type.value;
    }

    @NotNull
    @Override
    public Iterable<String> getBoxPaths()
    {
        return Collections.singletonList("moov/trak/mdia/hdlr");
    }

    @Override
    public void readBox(@NotNull IsoBoxContext box) throws IOException
    {
        SequentialReader payload = box.payload();
        payload.skip(8);
        IsoBoxContext media = box.parent();
        if (media != null)
            media.put(HandlerType.class, new HandlerType(payload.getString(4, Charsets.ISO_8859_1)));
    }

    @Override
    public boolean enterContainer(@NotNull IsoBoxContext box) throws IOException
    {
        String type = box.box().type;
        IsoBoxContext parent = box.parent();
        String parentType = parent == null || parent.parent() == null ? "" : parent.box().type;
        SequentialReader payload = box.payload();
        if (parentType.equals("stsd")) {
            String handler = handlerType(box);
            payload.skip("vide".equals(handler) ? 78 : "soun".equals(handler) ? 28 : 8);
        } else if (type.equals("stsd") || type.equals("dref")) {
            payload.skip(8);
        } else if (type.equals("meta") || type.equals("iref")) {
            payload.skip(4);
        } else if (type.equals("iinf")) {
            int version = payload.getUInt8();
            payload.skip(version == 0 ? 5 : 7);
        }
        return true;
    }
}
