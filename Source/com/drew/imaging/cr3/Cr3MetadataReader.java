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

import com.drew.imaging.isobmff.IsoBoxWalker;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.cr3.Cr3Directory;
import com.drew.metadata.file.FileSystemMetadataReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream)
    {
        Metadata metadata = new Metadata();
        extract(inputStream, metadata);
        return metadata;
    }

    public static void extract(@NotNull InputStream inputStream, @NotNull Metadata metadata)
    {
        Cr3Directory directory = new Cr3Directory();
        metadata.addDirectory(directory);
        IsoBoxWalker.walk(inputStream, new Cr3Handler(metadata, directory));
    }
}
