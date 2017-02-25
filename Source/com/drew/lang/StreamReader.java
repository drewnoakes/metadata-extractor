/*
 * Copyright 2002-2017 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */

package com.drew.lang;

import com.drew.lang.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class StreamReader extends SequentialReader
{
    @NotNull
    private final InputStream _stream;

    private long _pos;

    @Override
    public long getPosition()
    {
        return _pos;
    }

    @SuppressWarnings("ConstantConditions")
    public StreamReader(@NotNull InputStream stream)
    {
        if (stream == null)
            throw new NullPointerException();

        _stream = stream;
        _pos = 0;
    }

    @Override
    public byte getByte() throws IOException
    {
        int value = _stream.read();
        if (value == -1)
            throw new EOFException("End of data reached.");
        _pos++;
        return (byte)value;
    }

    @NotNull
    @Override
    public byte[] getBytes(int count) throws IOException
    {
        byte[] bytes = new byte[count];
        getBytes(bytes, 0, count);
        return bytes;
    }

    @Override
    public void getBytes(@NotNull byte[] buffer, int offset, int count) throws IOException
    {
        int totalBytesRead = 0;
        while (totalBytesRead != count)
        {
            final int bytesRead = _stream.read(buffer, offset + totalBytesRead, count - totalBytesRead);
            if (bytesRead == -1)
                throw new EOFException("End of data reached.");
            totalBytesRead += bytesRead;
            assert(totalBytesRead <= count);
        }
        _pos += totalBytesRead;
    }

    @Override
    public void skip(long n) throws IOException
    {
        if (n < 0)
            throw new IllegalArgumentException("n must be zero or greater.");

        long skippedCount = skipInternal(n);

        if (skippedCount != n)
            throw new EOFException(String.format("Unable to skip. Requested %d bytes but skipped %d.", n, skippedCount));
    }

    @Override
    public boolean trySkip(long n) throws IOException
    {
        if (n < 0)
            throw new IllegalArgumentException("n must be zero or greater.");

        return skipInternal(n) == n;
    }

    @Override
    public int available() {
        try {
            return _stream.available();
        } catch (IOException e) {
            return 0;
        }
    }

    private long skipInternal(long n) throws IOException
    {
        // It seems that for some streams, such as BufferedInputStream, that skip can return
        // some smaller number than was requested. So loop until we either skip enough, or
        // InputStream.skip returns zero.
        //
        // See http://stackoverflow.com/questions/14057720/robust-skipping-of-data-in-a-java-io-inputstream-and-its-subtypes
        //
        long skippedTotal = 0;
        while (skippedTotal != n) {
            long skipped = _stream.skip(n - skippedTotal);
            assert(skipped >= 0);
            skippedTotal += skipped;
            if (skipped == 0)
                break;
        }
        _pos += skippedTotal;
        return skippedTotal;
    }
}
