/*
 * Copyright 2026 Drew Noakes and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.drew.imaging.ebml;

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mkv.MkvReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts metadata using the supported Matroska and WebM schemas.
 * Their format-specific parsing is performed by
 * {@link MkvReader}; other EBML document types are reported as unsupported.
 */
public class EbmlMetadataReader
{
    private EbmlMetadataReader()
    {
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException
    {
        Metadata metadata = new Metadata();
        new MkvReader().extract(new StreamReader(inputStream), metadata);
        return metadata;
    }
}
