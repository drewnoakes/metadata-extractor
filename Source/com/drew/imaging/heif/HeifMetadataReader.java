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
package com.drew.imaging.heif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.ErrorDirectory;
import com.drew.metadata.Metadata;
import com.drew.metadata.heif.HeifBoxHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class HeifMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException
    {
        Metadata metadata = new Metadata();
        try {
            new HeifReader().extract(metadata, inputStream, new HeifBoxHandler(metadata));
            return metadata;
        } catch (DataFormatException e) {
            metadata.addDirectory(new ErrorDirectory(e.getMessage()));
        }
        return metadata;
    }
}
