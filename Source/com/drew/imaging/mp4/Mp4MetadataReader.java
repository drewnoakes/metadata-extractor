package com.drew.imaging.mp4;

import com.drew.imaging.quicktime.QtReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4BoxHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class Mp4MetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException
    {
        try {
            Metadata metadata = new Metadata();
            new Mp4Reader().extract(metadata, inputStream, new Mp4BoxHandler(metadata));
            return metadata;
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
