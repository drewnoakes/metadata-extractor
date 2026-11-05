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
import com.drew.imaging.isobmff.IsoBoxIterator;
import com.drew.imaging.isobmff.IsoBoxReader;
import com.drew.imaging.isobmff.IsoBoxReaders;
import com.drew.lang.annotations.NotNull;
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
        extract(inputStream, metadata, compile(readers));
        return metadata;
    }

    public static void extract(@NotNull InputStream inputStream, @NotNull Metadata metadata)
    {
        extract(inputStream, metadata, DEFAULT_READERS);
    }

    /**
     * Reads {@code inputStream} into {@code metadata} with {@code readers}. Readers find the
     * {@link Cr3Directory} in the file context, under its class.
     */
    public static void extract(@NotNull InputStream inputStream, @NotNull Metadata metadata, @NotNull IsoBoxReaders readers)
    {
        Cr3Directory directory = new Cr3Directory();
        metadata.addDirectory(directory);
        IsoBoxContext root = IsoBoxContext.root(metadata, directory);
        root.put(Cr3Directory.class, directory);
        readers.read(new IsoBoxIterator(inputStream), root, MAX_SHARED_PAYLOAD);
    }

    /** The largest payload read into memory to share between several readers registered for the same box. */
    private static final long MAX_SHARED_PAYLOAD = 64L * 1024 * 1024;
}
