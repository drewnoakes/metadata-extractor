/*
 * Copyright 2002-2012 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */

package com.drew.imaging.bmp;

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.bmp.BmpReader;

import java.io.*;

/**
 * Obtains metadata from BMP files.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class BmpMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException
    {
        Metadata metadata = new Metadata();

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            new BmpReader().extract(new StreamReader(stream), metadata);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException
    {
        Metadata metadata = new Metadata();
        new BmpReader().extract(new StreamReader(inputStream), metadata);
        return metadata;
    }
}
