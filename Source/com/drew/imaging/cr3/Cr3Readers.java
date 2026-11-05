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

import com.drew.imaging.isobmff.IsoBoxContext;
import com.drew.imaging.isobmff.IsoBoxLayout;
import com.drew.imaging.isobmff.IsoBoxReader;
import com.drew.imaging.isobmff.IsoBoxReaders;
import com.drew.imaging.isobmff.IsoStandardLayout;
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
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifTiffHandler;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory;
import com.drew.metadata.xmp.XmpReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The box readers and layout of Canon CR3 files.
 * <ul>
 *   <li>{@code ftyp}: brands</li>
 *   <li>{@code moov/uuid:85c0b687-820f-11e0-8111-f4ce462b6a48}: the Canon metadata, with TIFF blobs in
 *       {@code CMT1} to {@code CMT4}, the compressor version in {@code CNCV} and the thumbnail in {@code THMB}</li>
 *   <li>{@code uuid:be7acfcb-97a9-42e8-9c71-999491e3afac}: an XMP packet</li>
 *   <li>{@code uuid:eaf42b5e-1c98-4b88-b9fb-b7dc406e4d16}: an 8-byte preamble, then the {@code PRVW} preview</li>
 * </ul>
 */
final class Cr3Readers
{
    static final String UUID_CANON_MAIN = "uuid:85c0b687-820f-11e0-8111-f4ce462b6a48";
    static final String UUID_CANON_XMP = "uuid:be7acfcb-97a9-42e8-9c71-999491e3afac";
    static final String UUID_CANON_PREVIEW = "uuid:eaf42b5e-1c98-4b88-b9fb-b7dc406e4d16";

    static final byte[] USERTYPE_CANON_MAIN = {
        (byte)0x85, (byte)0xc0, (byte)0xb6, (byte)0x87, (byte)0x82, (byte)0x0f, (byte)0x11, (byte)0xe0,
        (byte)0x81, (byte)0x11, (byte)0xf4, (byte)0xce, (byte)0x46, (byte)0x2b, (byte)0x6a, (byte)0x48
    };
    static final byte[] USERTYPE_CANON_XMP = {
        (byte)0xbe, (byte)0x7a, (byte)0xcf, (byte)0xcb, (byte)0x97, (byte)0xa9, (byte)0x42, (byte)0xe8,
        (byte)0x9c, (byte)0x71, (byte)0x99, (byte)0x94, (byte)0x91, (byte)0xe3, (byte)0xaf, (byte)0xac
    };
    static final byte[] USERTYPE_CANON_PREVIEW = {
        (byte)0xea, (byte)0xf4, (byte)0x2b, (byte)0x5e, (byte)0x1c, (byte)0x98, (byte)0x4b, (byte)0x88,
        (byte)0xb9, (byte)0xfb, (byte)0xb7, (byte)0xdc, (byte)0x40, (byte)0x6e, (byte)0x4d, (byte)0x16
    };

    private static final String CANON = "moov/" + UUID_CANON_MAIN + "/";
    private static final int PREVIEW_PREAMBLE = 8;

    /** The standard layout, with the preamble in front of the preview's {@code PRVW} box. */
    static final IsoBoxLayout LAYOUT = new IsoStandardLayout()
    {
        @Override
        public boolean enterContainer(@NotNull IsoBoxContext box) throws IOException
        {
            if (box.box().usertypeMatches(USERTYPE_CANON_PREVIEW)) {
                box.payload().skip(PREVIEW_PREAMBLE);
                return true;
            }
            return super.enterContainer(box);
        }
    };

    static final String THUMBNAIL_PATH = CANON + Cr3BoxTypes.BOX_THUMBNAIL;
    static final String PREVIEW_PATH = UUID_CANON_PREVIEW + "/" + Cr3BoxTypes.BOX_PREVIEW;

    static final IsoBoxReader THUMBNAIL_READER = imageReader(Cr3ImageRecord.THUMBNAIL, THUMBNAIL_PATH, null);
    static final IsoBoxReader PREVIEW_READER = imageReader(Cr3ImageRecord.PREVIEW, PREVIEW_PATH, null);

    static final IsoBoxReader XMP_READER = IsoBoxReaders.on(new IsoBoxReaders.Callback()
    {
        @Override
        public void read(@NotNull IsoBoxContext box) throws IOException
        {
            new XmpReader().extract(box.payloadBytes(), box.metadata());
        }
    }, UUID_CANON_XMP);

    static final List<IsoBoxReader> ALL_READERS = Collections.unmodifiableList(Arrays.asList(
        IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box) throws IOException
            {
                readFileType(box);
            }
        }, Cr3BoxTypes.BOX_FILE_TYPE),
        tiff(CANON + Cr3BoxTypes.BOX_CANON_TIFF_IFD0, null),
        tiff(CANON + Cr3BoxTypes.BOX_CANON_TIFF_EXIF, ExifSubIFDDirectory.class),
        tiff(CANON + Cr3BoxTypes.BOX_CANON_TIFF_MAKERNOTE, CanonMakernoteDirectory.class),
        tiff(CANON + Cr3BoxTypes.BOX_CANON_TIFF_GPS, GpsDirectory.class),
        IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box) throws IOException
            {
                byte[] payload = box.payloadBytes();
                int length = payload.length;
                while (length > 0 && payload[length - 1] == 0)
                    length--;
                directory(box).setString(Cr3Directory.TAG_COMPRESSOR_VERSION, new String(payload, 0, length));
            }
        }, CANON + Cr3BoxTypes.BOX_CANON_COMPRESSOR_VERSION),
        THUMBNAIL_READER,
        XMP_READER,
        PREVIEW_READER));

    private Cr3Readers() {}

    @NotNull
    static IsoBoxReader imageReader(@NotNull final Cr3ImageRecord record, @NotNull String path,
                                    @Nullable final Cr3MetadataReader.ImageCallback callback)
    {
        return IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box) throws IOException
            {
                record.read(box, directory(box), callback);
            }
        }, path);
    }

    @NotNull
    private static Cr3Directory directory(@NotNull IsoBoxContext box)
    {
        return box.root().get(Cr3Directory.class);
    }

    private static void readFileType(@NotNull IsoBoxContext box) throws IOException
    {
        byte[] payload = box.payloadBytes();
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        String majorBrand = reader.getString(4);
        long minorVersion = reader.getUInt32();
        List<String> compatibleBrands = new ArrayList<String>();
        for (int i = 8; i < payload.length; i += 4)
            compatibleBrands.add(reader.getString(4));
        Cr3Directory directory = directory(box);
        directory.setString(Cr3Directory.TAG_MAJOR_BRAND, majorBrand);
        directory.setLong(Cr3Directory.TAG_MINOR_VERSION, minorVersion);
        directory.setStringArray(Cr3Directory.TAG_COMPATIBLE_BRANDS, compatibleBrands.toArray(new String[0]));
    }

    /**
     * A reader for a CMT TIFF blob, decoded into {@code rootClass}, or with the standard
     * handler (which starts at IFD0) when it is {@code null}.
     */
    private static IsoBoxReader tiff(@NotNull String path, @Nullable final Class<? extends Directory> rootClass)
    {
        return IsoBoxReaders.on(new IsoBoxReaders.Callback()
        {
            @Override
            public void read(@NotNull IsoBoxContext box) throws IOException
            {
                byte[] payload = box.payloadBytes();
                Metadata metadata = box.metadata();
                ExifTiffHandler handler = rootClass == null
                    ? new ExifTiffHandler(metadata, null, 0)
                    : new FixedRootExifTiffHandler(metadata, rootClass);
                try {
                    new TiffReader().processTiff(new ByteArrayReader(payload), handler, 0);
                } catch (TiffProcessingException e) {
                    box.addError("CR3 TIFF processing failed: " + e.getMessage());
                } catch (IOException e) {
                    box.addError("CR3 TIFF I/O error: " + e.getMessage());
                }
            }
        }, path);
    }

    /** Pushes a fixed directory class at the TIFF marker instead of {@code ExifIFD0Directory}. */
    private static final class FixedRootExifTiffHandler extends ExifTiffHandler
    {
        private final Class<? extends Directory> _rootClass;

        FixedRootExifTiffHandler(@NotNull Metadata metadata, @NotNull Class<? extends Directory> rootClass)
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
