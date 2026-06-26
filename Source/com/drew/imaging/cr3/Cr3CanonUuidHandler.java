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
package com.drew.imaging.cr3;

import com.drew.imaging.isobmff.IsoBox;
import com.drew.imaging.isobmff.IsoBoxVisitor;
import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.imaging.tiff.TiffReader;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.cr3.Cr3BoxTypes;
import com.drew.metadata.cr3.Cr3Directory;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifTiffHandler;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory;

import java.io.IOException;

/**
 * Visits the sub-boxes of the Canon main metadata UUID
 * ({@code 85c0b687-820f-11e0-8111-f4ce462b6a48}) inside the {@code moov} container.
 * <p>
 * Decodes:
 * <ul>
 *   <li>CMT1 — IFD0 data (Make, Model, DateTime, …) → {@link ExifIFD0Directory}</li>
 *   <li>CMT2 — Exif sub-IFD data (exposure, focal length, …) → {@link ExifSubIFDDirectory}</li>
 *   <li>CMT3 — Canon makernote data → {@link CanonMakernoteDirectory}</li>
 *   <li>CMT4 — GPS IFD data → {@link GpsDirectory}</li>
 *   <li>CNCV — Canon compressor version string → {@link Cr3Directory}</li>
 *   <li>THMB — Thumbnail dimensions → {@link Cr3Directory}</li>
 * </ul>
 * Each CMT blob is a self-contained TIFF byte stream (starts with {@code II}/{@code MM}
 * byte-order mark + 0x002A magic) and is decoded via the existing {@link ExifTiffHandler}
 * infrastructure to avoid duplicating any TIFF/IFD parsing logic.
 */
class Cr3CanonUuidHandler extends IsoBoxVisitor
{
    private final Metadata metadata;
    private final Cr3Directory directory;

    Cr3CanonUuidHandler(@NotNull Metadata metadata, @NotNull Cr3Directory directory)
    {
        this.metadata = metadata;
        this.directory = directory;
    }

    @Override
    protected boolean shouldRecurse(@NotNull IsoBox box)
    {
        return false; // no nested containers inside Canon main UUID
    }

    @Override
    @NotNull
    protected IsoBoxVisitor processContainer(@NotNull IsoBox box)
    {
        return this;
    }

    @Override
    protected boolean shouldVisit(@NotNull IsoBox box)
    {
        String t = box.type;
        return Cr3BoxTypes.BOX_CANON_TIFF_IFD0.equals(t)
            || Cr3BoxTypes.BOX_CANON_TIFF_EXIF.equals(t)
            || Cr3BoxTypes.BOX_CANON_TIFF_MAKERNOTE.equals(t)
            || Cr3BoxTypes.BOX_CANON_TIFF_GPS.equals(t)
            || Cr3BoxTypes.BOX_CANON_COMPRESSOR_VERSION.equals(t)
            || Cr3BoxTypes.BOX_THUMBNAIL.equals(t);
    }

    @Override
    protected void visit(@NotNull IsoBox box, @NotNull byte[] payload) throws IOException
    {
        String t = box.type;
        if (Cr3BoxTypes.BOX_CANON_TIFF_IFD0.equals(t)) {
            processTiff(payload, null /* standard ExifTiffHandler starts at IFD0 */);
        } else if (Cr3BoxTypes.BOX_CANON_TIFF_EXIF.equals(t)) {
            processTiff(payload, ExifSubIFDDirectory.class);
        } else if (Cr3BoxTypes.BOX_CANON_TIFF_MAKERNOTE.equals(t)) {
            processTiff(payload, CanonMakernoteDirectory.class);
        } else if (Cr3BoxTypes.BOX_CANON_TIFF_GPS.equals(t)) {
            processTiff(payload, GpsDirectory.class);
        } else if (Cr3BoxTypes.BOX_CANON_COMPRESSOR_VERSION.equals(t)) {
            processCompressorVersion(payload);
        } else if (Cr3BoxTypes.BOX_THUMBNAIL.equals(t)) {
            processThumbnail(payload);
        }
    }

    @Override
    public void addError(@NotNull String message)
    {
        directory.addError(message);
    }

    /**
     * Decodes a CMT TIFF blob.
     *
     * @param payload    raw TIFF bytes (starts with byte-order mark + 0x002A magic)
     * @param rootClass  directory class to push before processing, or {@code null} to use the
     *                   default ExifTiffHandler behaviour (which pushes ExifIFD0Directory)
     */
    private void processTiff(@NotNull byte[] payload,
                             @Nullable Class<? extends Directory> rootClass)
    {
        try {
            ByteArrayReader reader = new ByteArrayReader(payload);
            ExifTiffHandler handler = (rootClass == null)
                ? new ExifTiffHandler(metadata, null, 0)
                : new FixedRootExifTiffHandler(metadata, rootClass);
            new TiffReader().processTiff(reader, handler, 0);
        } catch (TiffProcessingException e) {
            directory.addError("CR3 TIFF processing failed: " + e.getMessage());
        } catch (IOException e) {
            directory.addError("CR3 TIFF I/O error: " + e.getMessage());
        }
    }

    private void processCompressorVersion(@NotNull byte[] payload)
    {
        // CNCV is a 30-byte ASCII string (may be NUL-padded)
        int len = payload.length;
        while (len > 0 && payload[len - 1] == 0)
            len--;
        directory.setString(Cr3Directory.TAG_COMPRESSOR_VERSION, new String(payload, 0, len));
    }

    /**
     * Parses THMB thumbnail header to extract dimensions.
     * <p>
     * THMB payload layout (big-endian):
     * <pre>
     *   uint32 version/flags
     *   uint16 width
     *   uint16 height
     *   uint32 jpeg_size
     *   uint32 unknown
     *   byte[] jpeg_data
     * </pre>
     */
    private void processThumbnail(@NotNull byte[] payload)
    {
        try {
            SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
            reader.skip(4); // version/flags uint32
            int width  = reader.getUInt16();
            int height = reader.getUInt16();
            directory.setInt(Cr3Directory.TAG_THUMBNAIL_WIDTH, width);
            directory.setInt(Cr3Directory.TAG_THUMBNAIL_HEIGHT, height);
        } catch (IOException e) {
            directory.addError("Error reading THMB dimensions: " + e.getMessage());
        }
    }

    /**
     * An {@link ExifTiffHandler} variant that starts IFD processing at a caller-specified
     * directory class rather than the default {@link ExifIFD0Directory}.
     * Used for CMT2 (ExifSubIFD), CMT3 (Canon makernote), and CMT4 (GPS).
     */
    private static final class FixedRootExifTiffHandler extends ExifTiffHandler
    {
        private final Class<? extends Directory> _rootClass;

        FixedRootExifTiffHandler(@NotNull Metadata metadata,
                                 @NotNull Class<? extends Directory> rootClass)
        {
            super(metadata, null, 0);
            _rootClass = rootClass;
        }

        @Override
        public void setTiffMarker(int marker) throws TiffProcessingException
        {
            pushDirectory(_rootClass);
        }
    }
}
