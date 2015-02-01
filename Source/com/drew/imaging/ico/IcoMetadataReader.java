package com.drew.imaging.ico;

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.ico.IcoReader;

import java.io.*;

/**
 * Obtains metadata from ICO (Windows Icon) files.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class IcoMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException
    {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return readMetadata(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream)
    {
        Metadata metadata = new Metadata();
        new IcoReader().extract(new StreamReader(inputStream), metadata);
        return metadata;
    }
}
