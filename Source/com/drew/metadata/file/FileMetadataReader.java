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
package com.drew.metadata.file;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.filter.MetadataFilter;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileMetadataReader
{
    public void read(@NotNull File file, @NotNull Metadata metadata) throws IOException
    {
        read(file, metadata, null);
    }

    public void read(@NotNull File file, @NotNull Metadata metadata, @Nullable final MetadataFilter filter) throws IOException
    {
        if (filter != null && !filter.directoryFilter(FileMetadataDirectory.class))
            return;

        if (!file.isFile())
            throw new IOException("File object must reference a file");
        if (!file.exists())
            throw new IOException("File does not exist");
        if (!file.canRead())
            throw new IOException("File is not readable");

        FileMetadataDirectory directory = new FileMetadataDirectory();

        directory.setString(FileMetadataDirectory.TAG_FILE_NAME, file.getName(), filter);
        directory.setLong(FileMetadataDirectory.TAG_FILE_SIZE, file.length(), filter);
        directory.setDate(FileMetadataDirectory.TAG_FILE_MODIFIED_DATE, new Date(file.lastModified()), filter);

        metadata.addDirectory(directory);
    }
}
