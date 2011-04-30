/*
 * Copyright 2002-2011 Drew Noakes
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

/**
 * A checked replacement for IndexOutOfBoundsException.  Used by BufferReader.
 * 
 * @author Drew Noakes http://drewnoakes.com
 */
public final class BufferBoundsException extends Exception
{
    public BufferBoundsException(byte[] buffer, int index, int bytesRequested)
    {
        super(getMessage(buffer, index, bytesRequested));
    }

    private static String getMessage(byte[] buffer, int index, int bytesRequested)
    {
        if (index < 0)
            return String.format("Attempt to read from buffer using a negative index (%s)", index);

        return String.format("Attempt to read %d byte%s from beyond end of buffer (requested index: %d, max index: %d)",
                bytesRequested, bytesRequested==1?"":"s", index, buffer.length - 1);
    }
}
