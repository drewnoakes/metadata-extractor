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
package com.drew.imaging.raf;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author TSGames https://github.com/TSGames
 */
public class RafMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws JpegProcessingException, IOException
    {
        inputStream.mark(512);

        byte[] data = new byte[512];
        inputStream.read(data);
        inputStream.reset();
        for (int i = 0; i < data.length - 2; i++) {
            if (data[i] == (byte) 0xff && data[i + 1] == (byte) 0xd8 && data[i + 2] == (byte) 0xff) {
                inputStream.skip(i);
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
