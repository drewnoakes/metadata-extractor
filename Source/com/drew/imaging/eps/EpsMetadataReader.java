package com.drew.imaging.eps;

import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.annotations.NotNull;
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
        Metadata metadata = new Metadata();
        InputStream stream = new FileInputStream(file);
        try {
            new EpsReader().extract(new RandomAccessStreamReader(stream), metadata);
        } finally {
            stream.close();
        }
        new FileSystemMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull RandomAccessStreamReader stream)
    {
        Metadata metadata = new Metadata();
        try {
            new EpsReader().extract(stream, metadata);
        } catch (IOException e) {

        }
        return metadata;
    }
}
