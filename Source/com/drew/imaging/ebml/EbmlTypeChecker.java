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

import com.drew.imaging.FileType;
import com.drew.imaging.TypeChecker;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mkv.EbmlDirectory;
import com.drew.metadata.mkv.MkvReader;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Identifies Matroska and WebM from the EBML header's DocType.
 * Probing is bounded; an unsupported or unresolved DocType remains EBML.
 */
public class EbmlTypeChecker implements TypeChecker
{
    private static final int HEADER_PROBE_LIMIT = 65536;

    @Override
    public int getByteCount()
    {
        return 4;
    }

    @Override
    @NotNull
    public FileType checkType(@NotNull byte[] bytes)
    {
        return bytes.length >= 4
            && bytes[0] == 0x1A && bytes[1] == 0x45
            && bytes[2] == (byte)0xDF && bytes[3] == (byte)0xA3
            ? FileType.Ebml : FileType.Unknown;
    }

    @Override
    @NotNull
    public FileType checkType(@NotNull byte[] bytes, @NotNull InputStream inputStream) throws IOException
    {
        if (checkType(bytes) == FileType.Unknown)
            return FileType.Unknown;
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

        EbmlDirectory header = metadata.getFirstDirectoryOfType(EbmlDirectory.class);
        String docType = header == null ? null : header.getString(EbmlDirectory.TAG_DOC_TYPE);
        if ("webm".equals(docType))
            return FileType.Webm;
        if ("matroska".equals(docType))
            return FileType.Mkv;
        return FileType.Ebml;
    }
}
