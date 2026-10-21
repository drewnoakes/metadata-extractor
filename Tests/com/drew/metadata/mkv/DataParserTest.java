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

import com.drew.lang.SequentialByteArrayReader;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class DataParserTest
{
    @Test
    public void decodesAllSizeWidths() throws Exception
    {
        for (int length = 1; length <= 8; length++) {
            byte[] bytes = new byte[length];
            bytes[0] = (byte)(1 << (8 - length));
            bytes[length - 1] |= 1;
            assertEquals(1, DataParser.decodeInteger(new SequentialByteArrayReader(bytes)));
        }
    }

    @Test(expected = IOException.class)
    public void rejectsZeroVintMarker() throws Exception
    {
        DataParser.decodeInteger(new SequentialByteArrayReader(new byte[9]));
    }

    @Test(expected = IOException.class)
    public void rejectsOversizedElementId() throws Exception
    {
        DataParser.getElementId(new SequentialByteArrayReader(new byte[] {8, 0, 0, 0, 1}));
    }

    @Test(expected = IOException.class)
    public void rejectsOversizedInteger() throws Exception
    {
        DataParser.getLong(new SequentialByteArrayReader(new byte[9]), 9);
    }

    @Test
    public void readsEmptyAndFloatingPointValues() throws Exception
    {
        SequentialByteArrayReader empty = new SequentialByteArrayReader(new byte[0]);
        assertEquals(0, DataParser.getLong(empty, 0));
        assertEquals(0, DataParser.getDouble(empty, 0), 0);
        assertEquals(1.25, DataParser.getDouble(new SequentialByteArrayReader(
            ByteBuffer.allocate(4).putFloat(1.25f).array()), 4), 0);
        assertEquals(1.25, DataParser.getDouble(new SequentialByteArrayReader(
            ByteBuffer.allocate(8).putDouble(1.25).array()), 8), 0);
    }

    @Test(expected = IOException.class)
    public void rejectsInvalidFloatWidth() throws Exception
    {
        DataParser.getDouble(new SequentialByteArrayReader(new byte[3]), 3);
    }
}
