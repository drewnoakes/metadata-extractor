/*
 * Copyright 2026 Drew Noakes and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.drew.metadata.mkv;

import com.drew.lang.SequentialReader;
import com.drew.lang.Charsets;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;

final class DataParser
{
    private DataParser()
    {
    }

    static long decodeInteger(@NotNull final SequentialReader reader) throws IOException
    {
        int first = reader.getUInt8();
        int length = getVintLength(first, 8);
        long value = first & ((1 << (8 - length)) - 1);
        for (int i = 1; i < length; i++)
            value = (value << 8) | reader.getUInt8();
        return value;
    }

    static long getElementSize(@NotNull final SequentialReader reader) throws IOException
    {
        long start = reader.getPosition();
        long value = decodeInteger(reader);
        int width = (int)(reader.getPosition() - start);
        return value == (1L << (7 * width)) - 1 ? -1 : value;
    }

    private static int getVintLength(int first, int maximum) throws IOException
    {
        int length = 1;
        int marker = 0x80;
        while ((first & marker) == 0) {
            if (++length > maximum)
                throw new IOException("Invalid EBML variable-length integer width.");
            marker >>= 1;
        }
        return length;
    }

    static int getElementId(@NotNull final SequentialReader reader) throws IOException
    {
        int value = reader.getUInt8();
        int length = getVintLength(value, 4);
        for (int i = 1; i < length; i++)
            value = (value << 8) | reader.getUInt8();
        if (value == (1 << (7 * length + 1)) - 1)
            throw new IOException("Reserved EBML element ID.");
        return value;
    }

    static long getLong(@NotNull final SequentialReader reader, long size) throws IOException
    {
        if (size < 0 || size > 8)
            throw new IOException("EBML integers must contain zero to eight bytes.");
        long result = 0L;
        for (long i = 0; i < size; i++)
            result = (result << 8) | reader.getUInt8();
        return result;
    }

    static double getDouble(@NotNull final SequentialReader reader, long size) throws IOException
    {
        if (size == 0)
            return 0;
        if (size == 4)
            return reader.getFloat32();
        if (size == 8)
            return reader.getDouble64();
        throw new IOException("EBML floats must contain zero, four, or eight bytes.");
    }

    @NotNull
    static Number getUnsignedInteger(@NotNull final SequentialReader reader, long size) throws IOException
    {
        long value = getLong(reader, size);
        return value >= 0 ? Long.valueOf(value)
            : BigInteger.valueOf(value & Long.MAX_VALUE).setBit(63);
    }

    @NotNull
    static byte[] getByteArray(@NotNull final SequentialReader reader, long size) throws IOException
    {
        return reader.getBytes(getByteCount(size));
    }

    @NotNull
    static String getString(@NotNull final SequentialReader reader, long size) throws IOException
    {
        return getString(reader, size, Charsets.ASCII);
    }

    @NotNull
    static String getString(@NotNull final SequentialReader reader, long size, @NotNull Charset charset) throws IOException
    {
        return reader.getNullTerminatedString(getByteCount(size), charset, true);
    }

    private static int getByteCount(long size) throws IOException
    {
        if (size < 0 || size > Integer.MAX_VALUE)
            throw new IOException("EBML value is too large to store in memory.");
        return (int)size;
    }
}
