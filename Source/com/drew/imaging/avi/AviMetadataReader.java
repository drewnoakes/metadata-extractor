package com.drew.imaging.avi;

import com.drew.imaging.riff.RiffProcessingException;
import com.drew.imaging.riff.RiffReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.avi.AviRiffHandler;
import com.drew.metadata.file.FileMetadataReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Obtains metadata from AVI files.
 *
 * @author Payton Garland
 */
public class AviMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException
    {
        Metadata metadata = new Metadata();
        new FileMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException, RiffProcessingException
    {
        Metadata metadata = new Metadata();
        new RiffReader().processRiff(new StreamReader(inputStream), new AviRiffHandler(metadata));
        return metadata;
    }
}
