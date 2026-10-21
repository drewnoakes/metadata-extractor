package com.drew.metadata.mkv;

import com.drew.lang.SequentialReader;
import com.drew.lang.Charsets;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;

public class DataParser
{
    private static final long[] VSINT_SUBTR = {0x3F, 0x1FFF, 0x0FFFFF, 0x07FFFFFF, 0x03FFFFFFFFL, 0x01FFFFFFFFFFL, 0x00FFFFFFFFFFFFL, 0x007FFFFFFFFFFFFFL};

    static long doDecodeInteger(final SequentialReader reader, boolean signed) throws IOException
    {
        byte firstByte = reader.getByte();
        if (firstByte == 0)
            throw new IOException("Invalid EBML variable-length integer.");
        int position = 7;
        for (; position >= 0; position--)
        {
            if ((firstByte & (1 << position)) != 0)
            {
                break;
            }
        }
        int length = 7 - position;
        byte[] values = reader.getBytes(length);
        long result = (firstByte & ((1L << position) - 1)) << (length * 8);
        for (int i = 1; i <= length; i++)
        {
            result |= ((long) (values[i - 1] & 0xFF) << ((length - i) * 8));
        }
        return signed ? result - VSINT_SUBTR[length] : result;
    }

    static long decodeInteger(final SequentialReader reader) throws IOException
    {
        return doDecodeInteger(reader, false);
    }

    static long getElementSize(final SequentialReader reader) throws IOException
    {
        long start = reader.getPosition();
        long value = decodeInteger(reader);
        int width = (int)(reader.getPosition() - start);
        return value == (1L << (7 * width)) - 1 ? -1 : value;
    }

    static long decodeSignedInteger(final SequentialReader reader) throws IOException
    {
        return doDecodeInteger(reader, true);
    }

    static int getElementId(final SequentialReader reader) throws IOException
    {
        byte firstByte = reader.getByte();
        if (firstByte == 0)
            throw new IOException("Invalid EBML element ID.");
        int position = 7;
        for (; position >= 0; position--)
        {
            if ((firstByte & (1 << position)) != 0)
            {
                break;
            }
        }
        int length = 7 - position;
        if (length > 3)
            throw new IOException("EBML element IDs must not exceed four bytes.");
        byte[] values = reader.getBytes(length);
        int result = ((int) (firstByte & 0xFF)) << (length * 8);
        for (int i = 1; i <= length; i++)
        {
            result |= (((int) values[i - 1] & 0xFF) << ((length - i) * 8));
        }
        return result;
    }

    static long getLong(final SequentialReader reader, long size) throws IOException
    {
        if (size < 0 || size > 8)
            throw new IOException("EBML integers must contain zero to eight bytes.");
        long result = 0L;
        for (long i = size - 1; i >= 0; i--)
        {
            result |= (long) (reader.getByte() & 0xFF) << (i * 8);
        }
        return result;
    }

    static double getDouble(final SequentialReader reader, long size) throws IOException
    {
        if (size == 0)
            return 0;
        if (size == 4)
            return reader.getFloat32();
        if (size == 8)
            return reader.getDouble64();
        throw new IOException("EBML floats must contain zero, four, or eight bytes.");
    }

    static Number getUnsignedInteger(final SequentialReader reader, long size) throws IOException
    {
        long value = getLong(reader, size);
        return value >= 0 ? Long.valueOf(value)
            : BigInteger.valueOf(value & Long.MAX_VALUE).setBit(63);
    }

    static byte[] getByteArray(final SequentialReader reader, long size) throws IOException
    {
        return reader.getBytes(getByteCount(size));
    }

    static String getString(final SequentialReader reader, long size) throws IOException
    {
        return getString(reader, size, Charsets.ASCII);
    }

    static String getString(final SequentialReader reader, long size, Charset charset) throws IOException
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
