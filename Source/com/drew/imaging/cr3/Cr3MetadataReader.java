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
import com.drew.imaging.isobmff.IsoBoxReader;
import com.drew.imaging.isobmff.IsoBoxReaders;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.cr3.Cr3Directory;
import com.drew.metadata.file.FileSystemMetadataReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Reads metadata from Canon CR3 (Crx) raw image files.
 * <p>
 * CR3 uses the ISO Base Media File Format (ISOBMFF) as its container, with Canon-specific
 * {@code uuid} boxes that embed TIFF-formatted EXIF, makernote, and GPS data (CMT1–CMT4),
 * an XMP packet, and JPEG thumbnail/preview images.
 * <p>
 * Camera metadata is populated into the standard directories
 * ({@link com.drew.metadata.exif.ExifIFD0Directory},
 *  {@link com.drew.metadata.exif.ExifSubIFDDirectory},
 *  {@link com.drew.metadata.exif.makernotes.CanonMakernoteDirectory},
 *  {@link com.drew.metadata.exif.GpsDirectory}) by reusing the existing
 * {@link com.drew.metadata.exif.ExifTiffHandler} pipeline.
 *
 * @see <a href="https://github.com/lclevy/canon_cr3">lclevy/canon_cr3 format specification</a>
 */
public class Cr3MetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull final File file) throws IOException
    {
        InputStream inputStream = new FileInputStream(file);
        Metadata metadata;
        try {
            metadata = readMetadata(inputStream);
        } finally {
            inputStream.close();
        }
        new FileSystemMetadataReader().read(file, metadata);
        return metadata;
    }

    /**
     * The readers {@link #readMetadata(InputStream)} uses. Pass them, with readers of your own,
     * to {@link #readMetadata(InputStream, Iterable)} to read further boxes.
     */
    public static final List<IsoBoxReader> ALL_READERS = Cr3Readers.ALL_READERS;

    /** The reader in {@link #ALL_READERS} for the thumbnail record ({@code THMB}): its dimensions and location. */
    public static final IsoBoxReader THUMBNAIL_READER = Cr3Readers.THUMBNAIL_READER;

    /** The reader in {@link #ALL_READERS} for the preview record ({@code PRVW}): its dimensions and location. */
    public static final IsoBoxReader PREVIEW_READER = Cr3Readers.PREVIEW_READER;

    /** Receives the image data of a thumbnail or preview record. */
    public interface ImageCallback
    {
        /**
         * @param box   the record's box, with the file context for per-file state
         * @param image the image data as stored (JPEG in the files seen so far), limited to its
         *              length; whatever is not read is skipped
         */
        void image(@NotNull IsoBoxContext box, @NotNull SequentialReader image) throws IOException;
    }

    /**
     * A replacement for {@link #THUMBNAIL_READER} that also hands the image data to
     * {@code callback}, in the same pass and without holding it.
     */
    @NotNull
    public static IsoBoxReader thumbnailReader(@NotNull ImageCallback callback)
    {
        return Cr3Readers.imageReader(Cr3ImageRecord.THUMBNAIL, Cr3Readers.THUMBNAIL_PATH, callback);
    }

    /**
     * A replacement for {@link #PREVIEW_READER} that also hands the image data to
     * {@code callback}, in the same pass and without holding it.
     */
    @NotNull
    public static IsoBoxReader previewReader(@NotNull ImageCallback callback)
    {
        return Cr3Readers.imageReader(Cr3ImageRecord.PREVIEW, Cr3Readers.PREVIEW_PATH, callback);
    }

    private static final IsoBoxReaders DEFAULT_READERS = compile(ALL_READERS);

    /** Prepares a walk over the CR3 structure that calls {@code readers}; build it once and reuse it. */
    @NotNull
    public static IsoBoxReaders compile(@NotNull Iterable<? extends IsoBoxReader> readers)
    {
        return new IsoBoxReaders(Cr3Readers.LAYOUT, readers);
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream)
    {
        Metadata metadata = new Metadata();
        extract(inputStream, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream, @NotNull Iterable<? extends IsoBoxReader> readers)
    {
        Metadata metadata = new Metadata();
        compile(readers).read(inputStream, newRoot(metadata, null));
        return metadata;
    }

    public static void extract(@NotNull InputStream inputStream, @NotNull Metadata metadata)
    {
        DEFAULT_READERS.read(inputStream, newRoot(metadata, null));
    }

    /**
     * The file context for reading into {@code metadata}: it adds a {@link Cr3Directory} and
     * stores it under its class, for readers to find with {@code box.root().get(Cr3Directory.class)}.
     * Problems go to {@code errors}, or to the {@link Cr3Directory} when it is {@code null}.
     * Store state for your own readers in it before calling
     * {@link IsoBoxReaders#read(InputStream, IsoBoxContext)}.
     */
    @NotNull
    public static IsoBoxContext newRoot(@NotNull Metadata metadata, @Nullable IsoBoxContext.ErrorHandler errors)
    {
        Cr3Directory directory = new Cr3Directory();
        metadata.addDirectory(directory);
        IsoBoxContext root = errors == null ? IsoBoxContext.root(metadata, directory) : IsoBoxContext.root(metadata, errors);
        root.put(Cr3Directory.class, directory);
        return root;
    }
}
