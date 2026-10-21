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
package com.drew.imaging;

import com.drew.lang.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Used by {@link FileTypeDetector} for file types that cannot be identified by a simple byte-prefix analysis.
 */
public interface TypeChecker
{
    /**
     * Gets the number of prefix bytes required by {@link #checkType(byte[])}.
     */
    int getByteCount();

    /**
     * Returns the file type identified within 'bytes', otherwise 'Unknown'.
     */
    FileType checkType(byte[] bytes);

    /**
     * Identifies a file type, optionally examining bytes beyond the prefix.
     * The stream is positioned at the beginning of the input and supports
     * mark/reset. Implementations that read it must restore its position.
     * The default implementation uses only the supplied prefix.
     */
    @NotNull
    default FileType checkType(@NotNull byte[] bytes, @NotNull InputStream inputStream) throws IOException
    {
        return checkType(bytes);
    }
}
