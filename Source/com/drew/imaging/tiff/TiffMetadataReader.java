/*
 * Copyright 2002-2014 Drew Noakes
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
package com.drew.imaging.tiff;

import com.drew.lang.RandomAccessFileReader;
import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Obtains all available metadata from TIFF formatted files.  Note that TIFF files include many digital camera RAW
 * formats, including Canon (CRW, CR2), Nikon (NEF), Olympus (ORF) and Panasonic (RW2).
 *
 * @author Darren Salomons, Drew Noakes http://drewnoakes.com
 */
public class TiffMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException
    {
        Metadata metadata = new Metadata();
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

        try {
            new ExifReader().extractTiff(new RandomAccessFileReader(randomAccessFile), metadata);
        } finally {
            randomAccessFile.close();
        }

        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream)
    {
        // TIFF processing requires random access, as directories can be scattered throughout the byte sequence.
        // InputStream does not support seeking backwards, and so is not a viable option for TIFF processing.
        // We use RandomAccessStreamReader, which buffers data from the stream as we seek forward.

        Metadata metadata = new Metadata();
        new ExifReader().extractTiff(new RandomAccessStreamReader(inputStream), metadata);
        return metadata;
    }
}
