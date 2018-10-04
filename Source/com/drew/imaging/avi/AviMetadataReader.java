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
package com.drew.imaging.avi;

import com.drew.imaging.riff.RiffProcessingException;
import com.drew.imaging.riff.RiffReader;
import com.drew.lang.RandomAccessStream;
import com.drew.lang.ReaderInfo;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.avi.AviRiffHandler;
import com.drew.metadata.file.FileSystemMetadataReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Obtains metadata from AVI files.
 *
 * @author Payton Garland
 */
public class AviMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException, RiffProcessingException
    {
        Metadata metadata;
        if(file.isFile())
        {
            RandomAccessFile raFile = new RandomAccessFile(file, "r");
            try {
                metadata = readMetadata(new RandomAccessStream(raFile).createReader());
            } finally {
                raFile.close();
            }
        }
        else
        {
            InputStream inputStream = new FileInputStream(file);
            try {
                metadata = readMetadata(new RandomAccessStream(inputStream, file.length()).createReader());
            } finally {
                inputStream.close();
            }
        }
        new FileSystemMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull ReaderInfo reader) throws IOException, RiffProcessingException
    {
        Metadata metadata = new Metadata();
        new RiffReader().processRiff(reader, new AviRiffHandler(metadata));
        return metadata;
    }
}
