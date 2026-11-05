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
package com.drew.imaging.cr3;

import com.drew.imaging.isobmff.IsoBox;
import com.drew.imaging.isobmff.IsoBoxIterator;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.cr3.Cr3BoxTypes;
import com.drew.metadata.cr3.Cr3ContainerTypes;
import com.drew.metadata.cr3.Cr3Directory;
import com.drew.metadata.xmp.XmpReader;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Reads the top-level box sequence of Canon CR3 files.
 * <p>
 * Handles the top-level box sequence ({@code ftyp}, {@code moov}, {@code uuid}).
 * Delegates Canon-metadata UUID content to {@link Cr3CanonUuidHandler} and
 * preview UUID content to {@link Cr3PreviewUuidHandler}.
 */
class Cr3Handler
{
    /**
     * Canon main-metadata UUID: {@code 85c0b687-820f-11e0-8111-f4ce462b6a48}.
     * Resides inside {@code moov}; contains CMT1–CMT4, THMB, CNCV, …
     */
    static final byte[] UUID_CANON_MAIN = {
        (byte)0x85, (byte)0xc0, (byte)0xb6, (byte)0x87,
        (byte)0x82, (byte)0x0f, (byte)0x11, (byte)0xe0,
        (byte)0x81, (byte)0x11, (byte)0xf4, (byte)0xce,
        (byte)0x46, (byte)0x2b, (byte)0x6a, (byte)0x48
    };

    /**
     * Canon preview UUID: {@code eaf42b5e-1c98-4b88-b9fb-b7dc406e4d16}.
     * Top-level; contains the PRVW sub-box.
     */
    static final byte[] UUID_CANON_PREVIEW = {
        (byte)0xea, (byte)0xf4, (byte)0x2b, (byte)0x5e,
        (byte)0x1c, (byte)0x98, (byte)0x4b, (byte)0x88,
        (byte)0xb9, (byte)0xfb, (byte)0xb7, (byte)0xdc,
        (byte)0x40, (byte)0x6e, (byte)0x4d, (byte)0x16
    };

    /**
     * Canon XMP UUID: {@code be7acfcb-97a9-42e8-9c71-999491e3afac}.
     * Top-level; payload is a raw XMP packet.
     */
    static final byte[] UUID_CANON_XMP = {
        (byte)0xbe, (byte)0x7a, (byte)0xcf, (byte)0xcb,
        (byte)0x97, (byte)0xa9, (byte)0x42, (byte)0xe8,
        (byte)0x9c, (byte)0x71, (byte)0x99, (byte)0x94,
        (byte)0x91, (byte)0xe3, (byte)0xaf, (byte)0xac
    };

    final Metadata metadata;
    final Cr3Directory directory;

    Cr3Handler(@NotNull Metadata metadata, @NotNull Cr3Directory directory)
    {
        this.metadata = metadata;
        this.directory = directory;
    }

    /**
     * Reads the boxes of {@code boxes}, descending into {@code moov} and the Canon
     * main-metadata {@code uuid}. An unreadable box is reported and the walk continues
     * with its next sibling.
     */
    void process(@NotNull IsoBoxIterator boxes)
    {
        try {
            for (IsoBox box = boxes.next(); box != null; box = boxes.next()) {
                try {
                    if (Cr3ContainerTypes.BOX_MOVIE.equals(box.type)) {
                        process(boxes.children());
                    } else if (box.usertypeMatches(UUID_CANON_MAIN)) {
                        new Cr3CanonUuidHandler(metadata, directory).process(boxes.children());
                    } else if (Cr3BoxTypes.BOX_FILE_TYPE.equals(box.type)) {
                        processFtyp(boxes.payloadBytes());
                    } else if (box.usertypeMatches(UUID_CANON_XMP)) {
                        new XmpReader().extract(boxes.payloadBytes(), metadata);
                    } else if (box.usertypeMatches(UUID_CANON_PREVIEW)) {
                        new Cr3PreviewUuidHandler(directory).process(boxes);
                    }
                } catch (EOFException e) {
                    throw e;
                } catch (IOException e) {
                    directory.addError("Error reading box '" + box.type + "': " + e.getMessage());
                }
            }
        } catch (IOException e) {
            directory.addError(e.getMessage());
        }
    }

    private void processFtyp(byte[] payload) throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        String majorBrand = reader.getString(4);
        long minorVersion = reader.getUInt32();

        ArrayList<String> compatibleBrands = new ArrayList<String>();
        // ftyp payload = major(4) + minor(4) + compatible brands in groups of 4
        for (int i = 8; i < payload.length; i += 4) {
            compatibleBrands.add(reader.getString(4));
        }

        directory.setString(Cr3Directory.TAG_MAJOR_BRAND, majorBrand);
        directory.setLong(Cr3Directory.TAG_MINOR_VERSION, minorVersion);
        directory.setStringArray(Cr3Directory.TAG_COMPATIBLE_BRANDS,
            compatibleBrands.toArray(new String[0]));
    }
}
