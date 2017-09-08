package com.drew.imaging.eps;

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

        new EpsReader().extract(new FileInputStream(file), metadata);

        new FileSystemMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException
    {
        Metadata metadata = new Metadata();
        new EpsReader().extract(inputStream, metadata);
        return metadata;
    }
}
