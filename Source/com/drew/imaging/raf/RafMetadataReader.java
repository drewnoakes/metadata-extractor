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
import com.drew.lang.annotations.NotNull;
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
            metadata = readMetadata(inputStream);
        } finally {
            inputStream.close();
        }
        new FileSystemMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws JpegProcessingException, IOException
    {
        if (!inputStream.markSupported())
            throw new IOException("Stream must support mark/reset");

        inputStream.mark(512);

        byte[] data = new byte[512];
        int bytesRead = inputStream.read(data);

        if (bytesRead == -1)
            throw new IOException("Stream is empty");

        inputStream.reset();

        for (int i = 0; i < bytesRead - 2; i++) {
            // Look for the first three bytes of a JPEG encoded file
            if (data[i] == (byte) 0xff && data[i + 1] == (byte) 0xd8 && data[i + 2] == (byte) 0xff) {
                long bytesSkipped = inputStream.skip(i);
                if (bytesSkipped != i)
                    throw new IOException("Skipping stream bytes failed");
                break;
            }
        }

        return JpegMetadataReader.readMetadata(inputStream);
    }

    private RafMetadataReader() throws Exception
    {
        throw new Exception("Not intended for instantiation");
    }
}
