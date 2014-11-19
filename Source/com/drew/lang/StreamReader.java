/*
 * Copyright 2002-2014 Drew Noakes
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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import com.drew.lang.annotations.NotNull;
import com.google.common.io.ByteStreams;

/**
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class StreamReader extends SequentialReader
{
    @NotNull
    private final InputStream _stream;

    @SuppressWarnings("ConstantConditions")
    public StreamReader(@NotNull final InputStream stream)
    {
        if (stream == null)
            throw new NullPointerException();

        _stream = stream;
    }

    @Override
    protected byte getByte() throws IOException
    {
        final int value = _stream.read();
        if (value == -1)
            throw new EOFException("End of data reached.");
        return (byte)value;
    }

    @NotNull
    @Override
    public byte[] getBytes(final int count) throws IOException
    {
        final byte[] bytes = new byte[count];
        int totalBytesRead = 0;

        while (totalBytesRead != count) {
            final int bytesRead = _stream.read(bytes, totalBytesRead, count - totalBytesRead);
            if (bytesRead == -1)
                throw new EOFException("End of data reached.");
            totalBytesRead += bytesRead;
            assert(totalBytesRead <= count);
        }

        return bytes;
    }

	@Override
	public void skip(final long n) throws IOException {
		if (n < 0) {
			throw new IllegalArgumentException("n must be zero or greater.");
		}

		ByteStreams.skipFully(_stream, n);
	}

	@Override
	public boolean trySkip(final long n) throws IOException {
		if (n < 0) {
			throw new IllegalArgumentException("n must be zero or greater.");
		}

		try {
			ByteStreams.skipFully(_stream, n);
		} catch (final EOFException e) {
			return false;
		}

		return true;
	}
}
