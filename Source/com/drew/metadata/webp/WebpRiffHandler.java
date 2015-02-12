/*
 * Copyright 2002-2015 Drew Noakes
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
package com.drew.metadata.webp;

import com.drew.imaging.riff.RiffHandler;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.xmp.XmpReader;

import java.io.IOException;

/**
 * Implementation of {@link RiffHandler} specialising in WebP support.
 *
 * Extracts data from chunk types:
 *
 * <ul>
 *     <li><code>"VP8X"</code>: width, height, is animation, has alpha</li>
 *     <li><code>"EXIF"</code>: full Exif data</li>
 *     <li><code>"ICCP"</code>: full ICC profile</li>
 *     <li><code>"XMP "</code>: full XMP data</li>
 * </ul>
 */
public class WebpRiffHandler implements RiffHandler
{
    @NotNull
    private final Metadata _metadata;

    public WebpRiffHandler(@NotNull Metadata metadata)
    {
        _metadata = metadata;
    }

    public boolean shouldAcceptRiffIdentifier(@NotNull String identifier)
    {
        return identifier.equals("WEBP");
    }

    public boolean shouldAcceptChunk(@NotNull String fourCC)
    {
        return fourCC.equals("VP8X")
            || fourCC.equals("EXIF")
            || fourCC.equals("ICCP")
            || fourCC.equals("XMP ");
    }

    public void processChunk(@NotNull String fourCC, @NotNull byte[] payload)
    {
//        System.out.println("Chunk " + fourCC + " " + payload.length + " bytes");

        if (fourCC.equals("EXIF")) {
            new ExifReader().extract(new ByteArrayReader(payload), _metadata);
        } else if (fourCC.equals("ICCP")) {
            new IccReader().extract(new ByteArrayReader(payload), _metadata);
        } else if (fourCC.equals("XMP ")) {
            new XmpReader().extract(payload, _metadata);
        } else if (fourCC.equals("VP8X") && payload.length == 10) {
            RandomAccessReader reader = new ByteArrayReader(payload);
            reader.setMotorolaByteOrder(false);

            try {
                // Flags
//                boolean hasFragments = reader.getBit(0);
                boolean isAnimation = reader.getBit(1);
//                boolean hasXmp = reader.getBit(2);
//                boolean hasExif = reader.getBit(3);
                boolean hasAlpha = reader.getBit(4);
//                boolean hasIcc = reader.getBit(5);

                // Image size
                int widthMinusOne = reader.getInt24(4);
                int heightMinusOne = reader.getInt24(7);

                WebpDirectory directory = new WebpDirectory();
                directory.setInt(WebpDirectory.TAG_IMAGE_WIDTH, widthMinusOne + 1);
                directory.setInt(WebpDirectory.TAG_IMAGE_HEIGHT, heightMinusOne + 1);
                directory.setBoolean(WebpDirectory.TAG_HAS_ALPHA, hasAlpha);
                directory.setBoolean(WebpDirectory.TAG_IS_ANIMATION, isAnimation);

                _metadata.addDirectory(directory);

            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }
}
