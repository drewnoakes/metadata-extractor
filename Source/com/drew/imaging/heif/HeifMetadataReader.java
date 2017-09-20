package com.drew.imaging.heif;

import com.drew.imaging.mp4.Mp4Reader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.heif.HeifBoxHandler;
import com.drew.metadata.mp4.Mp4BoxHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class HeifMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException
    {
        try {
            Metadata metadata = new Metadata();
            new HeifReader().extract(metadata, inputStream, new HeifBoxHandler(metadata));
            return metadata;
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
