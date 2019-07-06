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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public final class StringUtil
{
    @NotNull
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

    @NotNull
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

    @NotNull
    public static String fromStream(@NotNull InputStream stream) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static int compare(@Nullable String s1, @Nullable String s2)
    {
        boolean null1 = s1 == null;
        boolean null2 = s2 == null;

        if (null1 && null2) {
            return 0;
        } else if (null1) {
            return -1;
        } else if (null2) {
            return 1;
        } else {
            return s1.compareTo(s2);
        }
    }

    @NotNull
    public static String urlEncode(@NotNull String name)
    {
        // Sufficient for now, it seems
        return name.replace(" ", "%20");
    }
}
