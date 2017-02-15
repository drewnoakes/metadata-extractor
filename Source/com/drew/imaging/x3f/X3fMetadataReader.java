/*
 * Copyright 2002-2016 Drew Noakes
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
package com.drew.imaging.x3f;

import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.imaging.tiff.TiffReader;
import com.drew.lang.RandomAccessFileReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifTiffHandler;
import com.drew.metadata.file.FileMetadataReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Obtains all available metadata from TIFF formatted files.  Note that TIFF files include many digital camera RAW
 * formats, including Canon (CRW, CR2), Nikon (NEF), Olympus (ORF) and Panasonic (RW2).
 *
 * @author Anthony Mandra http://anthonymandra.com
 */
public class X3fMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException, TiffProcessingException
    {
        Metadata metadata = new Metadata();
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

        try {
            new X3fReader().processX3f(new RandomAccessFileReader(randomAccessFile),  0);
        } finally {
            randomAccessFile.close();
        }

        new FileMetadataReader().read(file, metadata);

        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException, TiffProcessingException
    {
        // X3F processing requires random access, as directory index is the last byte in the file.
        // InputStream does not support seeking backwards, so we wrap it with RandomAccessStreamReader, which
        // buffers data from the stream as we seek forward.

        return readMetadata(new RandomAccessStreamReader(inputStream));
    }

    @NotNull
    public static Metadata readMetadata(@NotNull RandomAccessReader reader) throws IOException, TiffProcessingException
    {
        Metadata metadata = new Metadata();
        new X3fReader().processX3f(reader, 0);
        return metadata;
    }
}
