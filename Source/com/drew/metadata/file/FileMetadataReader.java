package com.drew.metadata.file;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileMetadataReader
{
    public void read(@NotNull File file, @NotNull Metadata metadata) throws IOException
    {
        if (!file.isFile())
            throw new IOException("File object must reference a file");
        if (!file.exists())
            throw new IOException("File does not exist");
        if (!file.canRead())
            throw new IOException("File is not readable");

        FileMetadataDirectory directory = new FileMetadataDirectory();

        directory.setString(FileMetadataDirectory.TAG_FILE_NAME, file.getName());
        directory.setLong(FileMetadataDirectory.TAG_FILE_SIZE, file.length());
        directory.setDate(FileMetadataDirectory.TAG_FILE_MODIFIED_DATE, new Date(file.lastModified()));

        metadata.addDirectory(directory);
    }
}
