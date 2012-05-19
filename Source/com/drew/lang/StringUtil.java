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

import java.util.Iterator;

/** @author Drew Noakes http://drewnoakes.com */
public class StringUtil
{
    public static String join(@NotNull Iterable<? extends CharSequence> strings, @NotNull String delimiter)
    {
        int capacity = 0;
        int delimLength = delimiter.length();

        Iterator<? extends CharSequence> iter = strings.iterator();
        if (iter.hasNext())
            capacity += iter.next().length() + delimLength;

        StringBuilder buffer = new StringBuilder(capacity);
        iter = strings.iterator();
        if (iter.hasNext()) {
            buffer.append(iter.next());
            while (iter.hasNext()) {
                buffer.append(delimiter);
                buffer.append(iter.next());
            }
        }
        return buffer.toString();
    }

    public static <T extends CharSequence> String join(@NotNull T[] strings, @NotNull String delimiter)
    {
        int capacity = 0;
        int delimLength = delimiter.length();
        for (T value : strings)
            capacity += value.length() + delimLength;

        StringBuilder buffer = new StringBuilder(capacity);
        boolean first = true;
        for (T value : strings) {
            if (!first) {
                buffer.append(delimiter);
            } else {
                first = false;
            }
            buffer.append(value);
        }
        return buffer.toString();
    }
}
