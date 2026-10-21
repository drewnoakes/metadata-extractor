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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.drew.lang.Charsets;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.metadata.Metadata;

final class EbmlTestUtil
{
    static byte[] concat(byte[]... values) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] value : values)
            out.write(value);
        return out.toByteArray();
    }

    static byte[] bytes(long value)
    {
        int length = 1;
        for (long remaining = value; (remaining >>>= 8) != 0;)
            length++;
        byte[] bytes = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            bytes[i] = (byte)value;
            value >>>= 8;
        }
        return bytes;
    }

    static byte[] size(long value)
    {
        int length = 1;
        while (value >= (1L << (7 * length)) - 1)
            length++;
        byte[] bytes = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            bytes[i] = (byte)value;
            value >>>= 8;
        }
        bytes[0] |= (byte)(1 << (8 - length));
        return bytes;
    }

    static byte[] element(int id, byte[]... values) throws IOException
    {
        byte[] payload = concat(values);
        return concat(bytes(id & 0xFFFFFFFFL), size(payload.length), payload);
    }

    static byte[] unknownElement(int id, int sizeWidth, byte[]... values) throws IOException
    {
        byte[] size = new byte[sizeWidth];
        Arrays.fill(size, (byte)0xFF);
        size[0] = (byte)((1 << (9 - sizeWidth)) - 1);
        return concat(bytes(id & 0xFFFFFFFFL), size, concat(values));
    }

    static byte[] unsigned(int id, long value) throws IOException
    {
        return element(id, bytes(value));
    }

    static byte[] string(int id, String value) throws IOException
    {
        return element(id, value.getBytes(Charsets.UTF_8));
    }

    static byte[] floating(int id, double value) throws IOException
    {
        return element(id, ByteBuffer.allocate(8).putDouble(value).array());
    }

    static byte[] header(String docType) throws IOException
    {
        return element(ElementIDs.EBML_HEADER_ELEMENT,
            unsigned(ElementIDs.EBML_VERSION, 1),
            unsigned(ElementIDs.EBML_READ_VERSION, 1),
            unsigned(ElementIDs.EBML_MAX_ID_LENGTH, 4),
            unsigned(ElementIDs.EBML_MAX_SIZE_LENGTH, 8),
            string(ElementIDs.DOCTYPE, docType),
            unsigned(ElementIDs.DOCTYPE_VERSION, 4),
            unsigned(ElementIDs.DOCTYPE_READ_VERSION, 2));
    }

    static byte[] document(byte[]... children) throws IOException
    {
        return concat(header("matroska"), element(ElementIDs.SEGMENT, children));
    }

    static byte[] video(int number) throws IOException
    {
        return element(ElementIDs.TRACK_ENTRY,
            unsigned(ElementIDs.TRACK_NUMBER, number),
            unsigned(ElementIDs.TRACK_UID, number),
            unsigned(ElementIDs.TRACK_TYPE, 1),
            string(ElementIDs.CODEC_ID, "V_VP8"),
            element(ElementIDs.VIDEO,
                unsigned(ElementIDs.PIXEL_WIDTH, 32),
                unsigned(ElementIDs.PIXEL_HEIGHT, 24)));
    }

    static byte[] audio(int number) throws IOException
    {
        return element(ElementIDs.TRACK_ENTRY,
            unsigned(ElementIDs.TRACK_NUMBER, number),
            unsigned(ElementIDs.TRACK_UID, number),
            unsigned(ElementIDs.TRACK_TYPE, 2),
            string(ElementIDs.CODEC_ID, "A_PCM/INT/LIT"),
            element(ElementIDs.AUDIO,
                floating(ElementIDs.SAMPLING_FREQUENCY, 8000),
                unsigned(ElementIDs.CHANNELS, 1)));
    }

    static Metadata extract(byte[] bytes) throws IOException
    {
        Metadata metadata = new Metadata();
        new MkvReader().extract(new SequentialByteArrayReader(bytes), metadata);
        return metadata;
    }
}
