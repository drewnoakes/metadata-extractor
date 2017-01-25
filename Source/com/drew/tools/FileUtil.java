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
package com.drew.tools;

import com.drew.lang.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A series of utility methods for working with the file system. The methods herein are used in unit testing.
 * Use them in production code at your own risk!
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class FileUtil
{
    /**
     * Saves the contents of a <code>byte[]</code> to the specified {@link File}.
     */
    public static void saveBytes(@NotNull File file, @NotNull byte[] bytes) throws IOException
    {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(bytes);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * Reads the contents of a {@link File} into a <code>byte[]</code>. This relies upon {@link File#length()}
     * returning the correct value, which may not be the case when using a network file system. However this method is
     * intended for unit test support, in which case the files should be on the local volume.
     */
    @NotNull
    public static byte[] readBytes(@NotNull File file) throws IOException
    {
        int length = (int)file.length();
        // should only be zero if loading from a network or similar
        assert (length != 0);
        byte[] bytes = new byte[length];

        int totalBytesRead = 0;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            while (totalBytesRead != length) {
                int bytesRead = inputStream.read(bytes, totalBytesRead, length - totalBytesRead);
                if (bytesRead == -1) {
                    break;
                }
                totalBytesRead += bytesRead;
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return bytes;
    }

    /**
     * Reads the contents of a {@link File} into a <code>byte[]</code>. This relies upon <code>File.length()</code>
     * returning the correct value, which may not be the case when using a network file system. However this method is
     * intended for unit test support, in which case the files should be on the local volume.
     */
    @NotNull
    public static byte[] readBytes(@NotNull String filePath) throws IOException
    {
        return readBytes(new File(filePath));
    }
}
