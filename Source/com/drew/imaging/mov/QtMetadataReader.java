package com.drew.imaging.mov;

import com.drew.lang.RandomAccessReader;
import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileMetadataReader;
import com.drew.metadata.mov.QtReader;

import java.io.*;

public class QtMetadataReader {
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException
    {
        InputStream inputStream = new FileInputStream(file);
        Metadata metadata = readMetadata(file, inputStream);
        new FileMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull File file, @NotNull InputStream inputStream) throws IOException
    {
        Metadata metadata = new Metadata();
        new QtReader().extract(metadata, file);
        return metadata;
    }
}
