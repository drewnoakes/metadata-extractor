package com.drew.imaging.quicktime;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomHandler;

import java.io.*;
import java.util.zip.DataFormatException;

public class QtMetadataReader {
    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException
    {
        try {
            Metadata metadata = new Metadata();
            new QtReader().extract(metadata, inputStream, new QtAtomHandler(metadata));
            return metadata;
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
