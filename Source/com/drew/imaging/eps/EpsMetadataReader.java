package com.drew.imaging.eps;

import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.eps.EpsReader;
import com.drew.metadata.file.FileMetadataReader;

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

        new EpsReader().extract(new FileInputStream(file), metadata);

        new FileMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream)
    {
        Metadata metadata = new Metadata();
        try {
            new EpsReader().extract(inputStream, metadata);
        } catch (IOException e) {

        }
        return metadata;
    }
}
