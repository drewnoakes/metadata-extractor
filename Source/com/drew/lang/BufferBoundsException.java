/*
 * Copyright 2002-2012 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */

package com.drew.lang;

import com.drew.lang.annotations.NotNull;

import java.io.IOException;

/**
 * A checked replacement for {@link IndexOutOfBoundsException}.  Used by {@link RandomAccessReader}.
 * 
 * @author Drew Noakes http://drewnoakes.com
 */
public final class BufferBoundsException extends Exception
{
    private static final long serialVersionUID = 2911102837808946396L;

    public BufferBoundsException(@NotNull byte[] buffer, int index, int bytesRequested)
    {
        super(getMessage(buffer, index, bytesRequested));
    }

    public BufferBoundsException(final String message)
    {
        super(message);
    }

    public BufferBoundsException(final String message, final IOException innerException)
    {
        super(message, innerException);
    }

    private static String getMessage(@NotNull byte[] buffer, int index, int bytesRequested)
    {
        if (index < 0)
            return String.format("Attempt to read from buffer using a negative index (%s)", index);

        return String.format("Attempt to read %d byte%s from beyond end of buffer (requested index: %d, max index: %d)",
                bytesRequested, bytesRequested==1?"":"s", index, buffer.length - 1);
    }
}
