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

/**
 * Examines the a file's first bytes and estimates the file's type.
 */
public class FileTypeDetector
{
    // https://en.wikipedia.org/wiki/List_of_file_signatures

    private static final int  JPEG_MAGIC_NUMBER          = 0xFFD8;
    private static final int  MOTOROLA_TIFF_MAGIC_NUMBER = 0x4D4D002A;   // "MM?*"
    private static final int  INTEL_TIFF_MAGIC_NUMBER    = 0x49492A00;   // "II*?"
    private static final int  PSD_MAGIC_NUMBER    = 0x38425053;          // "8BPS"
    private static final long PNG_MAGIC_NUMBER    = 0x89504E470D0A1A0AL; // "?PNG????"
    private static final int  BMP_MAGIC_NUMBER    = 0x424D;              // "BM" // TODO technically there are other very rare magic numbers for OS/2 BMP files...
    private static final long GIF87a_MAGIC_NUMBER = 0x474946383761L;     // "GIF87a"
    private static final long GIF89a_MAGIC_NUMBER = 0x474946383961L;     // "GIF89a"
    private static final int  ICO_MAGIC_NUMBER    = 0x00000100;

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
     * Requires the stream to contain at least eight bytes.
     *
     * @throws IOException if an IO error occurred or the input stream ended unexpectedly.
     */
    @NotNull
    public static FileType detectFileType(@NotNull final BufferedInputStream inputStream) throws IOException
    {
        byte[] bytes = new byte[8];

        inputStream.mark(8);

        int bytesRead = inputStream.read(bytes);

        inputStream.reset();

        if (bytesRead != 8)
            throw new IOException("Stream ended before file's magic number could be determined.");

        int  two   =              (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);
        int  four  = two  << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
        long six   = four << 16 | (bytes[4] & 0xFF) << 8 | (bytes[5] & 0xFF);
        long eight = six  << 16 | (bytes[6] & 0xFF) << 8 | (bytes[7] & 0xFF);

        if (two == JPEG_MAGIC_NUMBER)
            return FileType.Jpeg;

        if (two == INTEL_TIFF_MAGIC_NUMBER || two == MOTOROLA_TIFF_MAGIC_NUMBER)
            return FileType.Tiff;

        if (two == BMP_MAGIC_NUMBER)
            return FileType.Bmp;

        if (four == PSD_MAGIC_NUMBER)
            return FileType.Psd;

        if (eight == PNG_MAGIC_NUMBER)
            return FileType.Png;

        if (six == GIF87a_MAGIC_NUMBER || six == GIF89a_MAGIC_NUMBER)
            return FileType.Gif;

        if (four == ICO_MAGIC_NUMBER)
            return FileType.Ico;

        return FileType.Unknown;
    }
}
