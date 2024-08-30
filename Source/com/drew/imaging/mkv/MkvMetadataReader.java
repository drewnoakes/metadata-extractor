package com.drew.imaging.mkv;

import com.drew.imaging.heif.HeifReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.heif.HeifBoxHandler;
import com.drew.metadata.mkv.MkvReader;

import java.io.IOException;
import java.io.InputStream;

public class MkvMetadataReader {
    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException {
        Metadata metadata = new Metadata();
        new MkvReader().extract(new StreamReader(inputStream), metadata);
        return metadata;
    }
}
