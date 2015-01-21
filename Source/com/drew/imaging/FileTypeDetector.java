/*
 * Copyright 2002-2015 Drew Noakes
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Examines the a file's first bytes and estimates the file's type.
 */
public class FileTypeDetector
{
    private static final int JPEG_FILE_MAGIC_NUMBER = 0xFFD8;
    private static final int MOTOROLA_TIFF_MAGIC_NUMBER = 0x4D4D;  // "MM"
    private static final int INTEL_TIFF_MAGIC_NUMBER = 0x4949;     // "II"
    private static final int PSD_MAGIC_NUMBER = 0x3842;            // "8B" // TODO the full magic number is 8BPS
    private static final int PNG_MAGIC_NUMBER = 0x8950;            // "?P" // TODO the full magic number is six bytes long
    private static final int BMP_MAGIC_NUMBER = 0x424D;            // "BM" // TODO technically there are other very rare magic numbers for OS/2 BMP files...
    private static final int GIF_MAGIC_NUMBER = 0x4749;            // "GI" // TODO the full magic number is GIF or possibly GIF89a/GIF87a

    private FileTypeDetector() throws Exception
    {
        throw new Exception("Not intended for instantiation");
    }

    /**
     * Examines the a file's first bytes and estimates the file's type.
     * <p>
     * Requires a {@link BufferedInputStream} in order to mark and reset the stream to the position
     * at which it was provided to this method once completed.
     * <p>
     * Requires the stream to contain at least two bytes.
     *
     * @throws IOException if an IO error occurred or the input stream ended unexpectedly.
     */
    @NotNull
    public static FileType detectFileType(@NotNull final BufferedInputStream inputStream) throws IOException
    {
        int magicNumber = peekMagicNumber(inputStream);

        if (magicNumber == -1)
            throw new IOException("Stream ended before file's magic number could be determined.");

        // This covers all JPEG files
        if ((magicNumber & JPEG_FILE_MAGIC_NUMBER) == JPEG_FILE_MAGIC_NUMBER)
            return FileType.Jpeg;

        // This covers all TIFF and camera RAW files
        if (magicNumber == INTEL_TIFF_MAGIC_NUMBER || magicNumber == MOTOROLA_TIFF_MAGIC_NUMBER)
            return FileType.Tiff;

        // This covers PSD files
        // TODO we should really check all 4 bytes of the PSD magic number
        if (magicNumber == PSD_MAGIC_NUMBER)
            return FileType.Psd;

        // This covers PNG files
        if (magicNumber == PNG_MAGIC_NUMBER)
            return FileType.Png;

        // This covers BMP files
        if (magicNumber == BMP_MAGIC_NUMBER)
            return FileType.Bmp;

        // This covers GIF files
        if (magicNumber == GIF_MAGIC_NUMBER)
            return FileType.Gif;

        return FileType.Unknown;
    }

    /**
     * Reads the first two bytes from <code>inputStream</code>, then rewinds.
     */
    private static int peekMagicNumber(@NotNull final InputStream inputStream) throws IOException
    {
        inputStream.mark(2);
        final int byte1 = inputStream.read();
        final int byte2 = inputStream.read();
        inputStream.reset();

        if (byte1 == -1 || byte2 == -1)
            return -1;

        return byte1 << 8 | byte2;
    }
}
