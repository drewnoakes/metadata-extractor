package com.drew.imaging.eps;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.RandomAccessStream;
import com.drew.lang.ReaderInfo;
import com.drew.metadata.Metadata;
import com.drew.metadata.eps.EpsReader;
import com.drew.metadata.file.FileSystemMetadataReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Obtains metadata from EPS files.
 *
 * @author Payton Garland
 */
public class EpsMetadataReader {
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException
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
    public static Metadata readMetadata(@NotNull ReaderInfo reader) throws IOException
    {
        Metadata metadata = new Metadata();
        new EpsReader().extract(reader, metadata);
        return metadata;
    }
}
