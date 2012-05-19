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
package com.drew.imaging.tiff;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;

import java.io.*;

/**
 * Obtains all available metadata from TIFF formatted files.  Note that TIFF files include many digital camera RAW
 * formats, including Canon (CRW, CR2) and Nikon (NEF).
 *
 * @author Darren Salomons, Drew Noakes http://drewnoakes.com
 */
public class TiffMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException
    {
        Metadata metadata = new Metadata();

        FileInputStream fileInputStream = null;
        DataInputStream dataInputStream = null;
        byte[] buffer;
        try{
            fileInputStream = new FileInputStream(file);
            dataInputStream = new DataInputStream(fileInputStream);
            buffer = new byte[(int)file.length()];
            dataInputStream.readFully(buffer);
        } finally {
            if (dataInputStream != null)
                dataInputStream.close();
            if (fileInputStream != null)
                fileInputStream.close();
        }

        new ExifReader().extractTiff(buffer, metadata);

        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream, boolean waitForBytes) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        // TODO do this in chunks rather than byte-by-byte, and honour 'waitForBytes'
        while((b = inputStream.read()) != -1) {
            out.write(b);
        }
        Metadata metadata = new Metadata();
        new ExifReader().extractTiff(out.toByteArray(), metadata);
        return metadata;
    }
}
