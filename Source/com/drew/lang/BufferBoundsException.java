/*
 * Copyright 2002-2019 Drew Noakes and contributors
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

import java.io.IOException;

/**
 * A checked replacement for {@link IndexOutOfBoundsException}.  Used by {@link RandomAccessReader}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public final class BufferBoundsException extends IOException
{
    private static final long serialVersionUID = 2911102837808946396L;

    public BufferBoundsException(int index, int bytesRequested, long bufferLength)
    {
        super(getMessage(index, bytesRequested, bufferLength));
    }

    public BufferBoundsException(final String message)
    {
        super(message);
    }

    private static String getMessage(int index, int bytesRequested, long bufferLength)
    {
        if (index < 0)
            return String.format("Attempt to read from buffer using a negative index (%d)", index);

        if (bytesRequested < 0)
            return String.format("Number of requested bytes cannot be negative (%d)", bytesRequested);

        if ((long)index + (long)bytesRequested - 1L > (long)Integer.MAX_VALUE)
            return String.format("Number of requested bytes summed with starting index exceed maximum range of signed 32 bit integers (requested index: %d, requested count: %d)", index, bytesRequested);

        return String.format("Attempt to read from beyond end of underlying data source (requested index: %d, requested count: %d, max index: %d)",
                index, bytesRequested, bufferLength - 1);
    }
}
