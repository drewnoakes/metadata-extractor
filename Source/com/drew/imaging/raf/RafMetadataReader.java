/*
 * Copyright 2002-2017 Drew Noakes
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
package com.drew.imaging.raf;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.RandomAccessStream;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.ReaderInfo;
import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileSystemMetadataReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Obtains metadata from RAF (Fujifilm camera raw) image files.
 *
 * @author TSGames https://github.com/TSGames
 * @author Drew Noakes https://drewnoakes.com
 */
public class RafMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws JpegProcessingException, IOException
    {
        InputStream inputStream = new FileInputStream(file);
        Metadata metadata;
        try {
            metadata = readMetadata(new RandomAccessStream(inputStream, file.length()).createReader());
        } finally {
            inputStream.close();
        }
        new FileSystemMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull ReaderInfo reader) throws JpegProcessingException, IOException
    {
        byte[] data = new byte[512];
        int bytesRead = reader.read(data, 0, 512);

        if (bytesRead == -1)
            throw new IOException("Stream is empty");

        reader.skip(-bytesRead);

        for (int i = 0; i < bytesRead - 2; i++) {
            // Look for the first three bytes of a JPEG encoded file
            if (data[i] == (byte) 0xff && data[i + 1] == (byte) 0xd8 && data[i + 2] == (byte) 0xff) {
                reader.skip(i);
                break;
            }
        }

        return JpegMetadataReader.readMetadata(reader);
    }

    private RafMetadataReader() throws Exception
    {
        throw new Exception("Not intended for instantiation");
    }
}
