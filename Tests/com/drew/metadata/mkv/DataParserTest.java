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
import com.drew.lang.Charsets;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.math.BigInteger;

import static org.junit.Assert.*;

public class DataParserTest
{
    @Test
    public void acceptsChapterDisplayIdButRejectsReservedIds() throws Exception
    {
        assertEquals(0x80, DataParser.getElementId(new SequentialByteArrayReader(new byte[] {(byte)0x80})));
        for (int id : new int[] {0xFF, 0x7FFF, 0x3FFFFF, 0x1FFFFFFF}) {
            try {
                DataParser.getElementId(new SequentialByteArrayReader(EbmlTestUtil.bytes(id)));
                fail("Reserved ID was accepted: " + id);
            } catch (IOException expected) {
                assertEquals("Reserved EBML element ID.", expected.getMessage());
            }
        }
    }

    @Test
    public void decodesTerminatedAsciiAndUtf8AndConsumesPadding() throws Exception
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(
            "webm\0\0\u00e9\0\0".getBytes(Charsets.UTF_8));
        assertEquals("webm", DataParser.getString(reader, 6));
        assertEquals(6, reader.getPosition());
        assertEquals("\u00e9", DataParser.getString(reader, 4, Charsets.UTF_8));
        assertEquals(10, reader.getPosition());
    }

    @Test(expected = IOException.class)
    public void rejectsUnrepresentableBinarySize() throws Exception
    {
        DataParser.getByteArray(new SequentialByteArrayReader(new byte[0]), 2147483648L);
    }

    @Test
    public void preservesFullUnsignedIntegerRange() throws Exception
    {
        assertEquals(Long.valueOf(Long.MAX_VALUE), DataParser.getUnsignedInteger(
            new SequentialByteArrayReader(EbmlTestUtil.bytes(Long.MAX_VALUE)), 8));
        assertEquals(new BigInteger("9223372036854775808"), DataParser.getUnsignedInteger(
            new SequentialByteArrayReader(EbmlTestUtil.bytes(Long.MIN_VALUE)), 8));
        assertEquals(new BigInteger("18446744073709551615"), DataParser.getUnsignedInteger(
            new SequentialByteArrayReader(EbmlTestUtil.bytes(-1L)), 8));
    }

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
