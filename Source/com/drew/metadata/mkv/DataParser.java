package com.drew.metadata.mkv;

import com.drew.lang.SequentialReader;

import java.io.IOException;

public class DataParser
{
    private static final long[] VSINT_SUBTR = {0x3F, 0x1FFF, 0x0FFFFF, 0x07FFFFFF, 0x03FFFFFFFFL, 0x01FFFFFFFFFFL, 0x00FFFFFFFFFFFFL, 0x007FFFFFFFFFFFFFL};

    static long doDecodeInteger(final SequentialReader reader, boolean signed) throws IOException
    {
        byte firstByte = reader.getBytes(1)[0];
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

    static long decodeSignedInteger(final SequentialReader reader) throws IOException
    {
        return doDecodeInteger(reader, true);
    }

    static int getElementId(final SequentialReader reader) throws IOException
    {
        byte firstByte = reader.getBytes(1)[0];
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
        int result = ((int) (firstByte & 0xFF)) << (length * 8);
        for (int i = 1; i <= length; i++)
        {
            result |= (((int) values[i - 1] & 0xFF) << ((length - i) * 8));
        }
        return result;
    }

    static long getLong(final SequentialReader reader, long size) throws IOException
    {
        long result = 0L;
        for (long i = size - 1; i >= 0; i--)
        {
            result |= (long) (reader.getByte() & 0xFF) << (i * 8);
        }
        return result;
    }

    static byte[] getByteArray(final SequentialReader reader, long size) throws IOException
    {
        return reader.getBytes((int) size);
    }

    static String getString(final SequentialReader reader, long size) throws IOException
    {
        return reader.getString((int) size);
    }
}
