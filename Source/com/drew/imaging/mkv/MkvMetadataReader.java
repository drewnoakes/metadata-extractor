/*
 * Copyright 2026 Drew Noakes and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.drew.imaging.mkv;

import com.drew.lang.StreamReader;
import com.drew.imaging.FileType;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mkv.MkvReader;
import com.drew.metadata.mkv.EbmlDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.io.EOFException;

public class MkvMetadataReader
{
    private static final int HEADER_PROBE_LIMIT = 65536;

    private MkvMetadataReader()
    {
    }

    /**
     * Probes only the EBML header, restoring the stream afterwards. Large
     * headers whose DocType lies beyond the probe remain classified as EBML
     * until full metadata extraction resolves their document type.
     */
    @NotNull
    public static FileType detectFileType(@NotNull InputStream inputStream) throws IOException
    {
        if (!inputStream.markSupported())
            throw new IOException("Stream must support mark/reset.");
        inputStream.mark(HEADER_PROBE_LIMIT);
        Metadata metadata = new Metadata();
        try {
            new MkvReader().extractHeader(new StreamReader(inputStream) {
                @Override
                public byte getByte() throws IOException
                {
                    if (getPosition() >= HEADER_PROBE_LIMIT)
                        throw new EOFException("EBML header exceeds the file type probe limit.");
                    return super.getByte();
                }

                @Override
                public byte[] getBytes(int count) throws IOException
                {
                    if (count > HEADER_PROBE_LIMIT - getPosition())
                        throw new EOFException("EBML header exceeds the file type probe limit.");
                    return super.getBytes(count);
                }

                @Override
                public boolean trySkip(long count) throws IOException
                {
                    long allowed = Math.min(count, HEADER_PROBE_LIMIT - getPosition());
                    boolean skipped = super.trySkip(allowed);
                    return allowed == count && skipped;
                }
            }, metadata);
        } finally {
            inputStream.reset();
        }
        return getFileType(metadata);
    }

    @NotNull
    public static FileType getFileType(@NotNull Metadata metadata)
    {
        EbmlDirectory header = metadata.getFirstDirectoryOfType(EbmlDirectory.class);
        String docType = header == null ? null : header.getString(EbmlDirectory.TAG_DOC_TYPE);
        if ("webm".equals(docType))
            return FileType.Webm;
        if ("matroska".equals(docType))
            return FileType.Mkv;
        return FileType.Ebml;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException
    {
        Metadata metadata = new Metadata();
        new MkvReader().extract(new StreamReader(inputStream), metadata);
        return metadata;
    }
}
