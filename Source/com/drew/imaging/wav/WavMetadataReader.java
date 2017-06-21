package com.drew.imaging.wav;

import com.drew.imaging.riff.RiffProcessingException;
import com.drew.imaging.riff.RiffReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileSystemMetadataReader;
import com.drew.metadata.wav.WavRiffHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Obtains metadata from WAV files.
 *
 * @author Payton Garland
 */
public class WavMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException
    {
        Metadata metadata = new Metadata();
        new FileSystemMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException, RiffProcessingException
    {
        Metadata metadata = new Metadata();
        new RiffReader().processRiff(new StreamReader(inputStream), new WavRiffHandler(metadata));
        return metadata;
    }
}
