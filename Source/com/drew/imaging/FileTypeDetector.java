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
package com.drew.imaging;

import com.drew.lang.ByteTrie;
import com.drew.lang.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Examines the a file's first bytes and estimates the file's type.
 */
public class FileTypeDetector
{
    private final static ByteTrie<FileType> _root;
    private final static int[] _offsets;

    static
    {
        _root = new ByteTrie<FileType>();
        _root.setDefaultValue(FileType.Unknown);

        // Potential supported offsets
        _offsets = new int[]{0, 4};

        // https://en.wikipedia.org/wiki/List_of_file_signatures

        _root.addPath(FileType.Jpeg, new byte[]{(byte)0xff, (byte)0xd8});
        _root.addPath(FileType.Tiff, "II".getBytes(), new byte[]{0x2a, 0x00});
        _root.addPath(FileType.Tiff, "MM".getBytes(), new byte[]{0x00, 0x2a});
        _root.addPath(FileType.Psd, "8BPS".getBytes());
        _root.addPath(FileType.Png, new byte[]{(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52});
        _root.addPath(FileType.Bmp, "BM".getBytes()); // Standard Bitmap Windows and OS/2
        _root.addPath(FileType.Bmp, "BA".getBytes()); // OS/2 Bitmap Array
        _root.addPath(FileType.Bmp, "CI".getBytes()); // OS/2 Color Icon
        _root.addPath(FileType.Bmp, "CP".getBytes()); // OS/2 Color Pointer
        _root.addPath(FileType.Bmp, "IC".getBytes()); // OS/2 Icon
        _root.addPath(FileType.Bmp, "PT".getBytes()); // OS/2 Pointer
        _root.addPath(FileType.Gif, "GIF87a".getBytes());
        _root.addPath(FileType.Gif, "GIF89a".getBytes());
        _root.addPath(FileType.Ico, new byte[]{0x00, 0x00, 0x01, 0x00});
        _root.addPath(FileType.Pcx, new byte[]{0x0A, 0x00, 0x01}); // multiple PCX versions, explicitly listed
        _root.addPath(FileType.Pcx, new byte[]{0x0A, 0x02, 0x01});
        _root.addPath(FileType.Pcx, new byte[]{0x0A, 0x03, 0x01});
        _root.addPath(FileType.Pcx, new byte[]{0x0A, 0x05, 0x01});
        _root.addPath(FileType.Riff, "RIFF".getBytes());

        _root.addPath(FileType.Arw, "II".getBytes(), new byte[]{0x2a, 0x00, 0x08, 0x00});
        _root.addPath(FileType.Crw, "II".getBytes(), new byte[]{0x1a, 0x00, 0x00, 0x00}, "HEAPCCDR".getBytes());
        _root.addPath(FileType.Cr2, "II".getBytes(), new byte[]{0x2a, 0x00, 0x10, 0x00, 0x00, 0x00, 0x43, 0x52});
        _root.addPath(FileType.Nef, "MM".getBytes(), new byte[]{0x00, 0x2a, 0x00, 0x00, 0x00, (byte)0x08, 0x00});
        _root.addPath(FileType.Orf, "IIRO".getBytes(), new byte[]{(byte)0x08, 0x00});
        _root.addPath(FileType.Orf, "MMOR".getBytes(), new byte[]{(byte)0x00, 0x00});
        _root.addPath(FileType.Orf, "IIRS".getBytes(), new byte[]{(byte)0x08, 0x00});
        _root.addPath(FileType.Raf, "FUJIFILMCCD-RAW".getBytes());
        _root.addPath(FileType.Rw2, "II".getBytes(), new byte[]{0x55, 0x00});

        // Potential root atoms... typically starts with FTYP... often at 4 byte offset
        _root.addPath(FileType.Mov, new byte[]{0x6D, 0x6F, 0x6F, 0x76}); // moov
        _root.addPath(FileType.Mov, new byte[]{0x77, 0x69, 0x64, 0x65}); // wide
        _root.addPath(FileType.Mov, new byte[]{0x6D, 0x64, 0x61, 0x74}); // mdat
        _root.addPath(FileType.Mov, new byte[]{0x66, 0x72, 0x65, 0x65}); // free

        _root.addPath(FileType.Mov, "ftypqt  ".getBytes());

        _root.addPath(FileType.Mp4, "ftypavc1".getBytes());
        _root.addPath(FileType.Mp4, "ftypiso2".getBytes());
        _root.addPath(FileType.Mp4, "ftypisom".getBytes());
        _root.addPath(FileType.Mp4, "ftypM4A ".getBytes());
        _root.addPath(FileType.Mp4, "ftypM4B ".getBytes());
        _root.addPath(FileType.Mp4, "ftypM4P ".getBytes());
        _root.addPath(FileType.Mp4, "ftypM4V ".getBytes());
        _root.addPath(FileType.Mp4, "ftypM4VH".getBytes());
        _root.addPath(FileType.Mp4, "ftypM4VP".getBytes());
        _root.addPath(FileType.Mp4, "ftypmmp4".getBytes());
        _root.addPath(FileType.Mp4, "ftypmp41".getBytes());
        _root.addPath(FileType.Mp4, "ftypmp42".getBytes());
        _root.addPath(FileType.Mp4, "ftypmp71".getBytes());
        _root.addPath(FileType.Mp4, "ftypMSNV".getBytes());
        _root.addPath(FileType.Mp4, "ftypNDAS".getBytes());
        _root.addPath(FileType.Mp4, "ftypNDSC".getBytes());
        _root.addPath(FileType.Mp4, "ftypNDSH".getBytes());
        _root.addPath(FileType.Mp4, "ftypNDSM".getBytes());
        _root.addPath(FileType.Mp4, "ftypNDSP".getBytes());
        _root.addPath(FileType.Mp4, "ftypNDSS".getBytes());
        _root.addPath(FileType.Mp4, "ftypNDXC".getBytes());
        _root.addPath(FileType.Mp4, "ftypNDXH".getBytes());
        _root.addPath(FileType.Mp4, "ftypNDXM".getBytes());
        _root.addPath(FileType.Mp4, "ftypNDXP".getBytes());
        _root.addPath(FileType.Mp4, "ftypNDXS".getBytes());

        _root.addPath(FileType.Heif, "ftypmif1".getBytes());
        _root.addPath(FileType.Heif, "ftypmsf1".getBytes());
        _root.addPath(FileType.Heif, "ftypheic".getBytes());
        _root.addPath(FileType.Heif, "ftypheix".getBytes());
        _root.addPath(FileType.Heif, "ftyphevc".getBytes());
        _root.addPath(FileType.Heif, "ftyphevx".getBytes());
    }

    private FileTypeDetector() throws Exception
    {
        throw new Exception("Not intended for instantiation");
    }

    @NotNull
    public static FileType detectFileType(@NotNull final BufferedInputStream inputStream, @NotNull final int offset) throws IOException
    {
        if (!inputStream.markSupported())
            throw new IOException("Stream must support mark/reset");

        int maxByteCount = _root.getMaxDepth();

        inputStream.mark(maxByteCount);

        byte[] bytes = new byte[maxByteCount];
        inputStream.skip(offset);
        int bytesRead = inputStream.read(bytes);

        if (bytesRead == -1)
            throw new IOException("Stream ended before file's magic number could be determined.");

        inputStream.reset();

        FileType fileType = _root.find(bytes);

        //noinspection ConstantConditions
        return fileType;
    }

    /**
     * Examines the file's bytes and estimates the file's type.
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
        FileType fileType = FileType.Unknown;
        for (int offset : _offsets) {
            fileType = detectFileType(inputStream, offset);
            if (fileType.getIsContainer()) {
                fileType = handleContainer(inputStream, fileType);
            }
            if (!fileType.equals(FileType.Unknown)) {
                break;
            }
        }
        return fileType;
    }

    /**
     * Calls detectFileType at correct offset for the container type being passed in.
     * In the case of fileTypes without magic bytes to identify with (Zip), the fileType will be
     * found within this method alone.
     *
     * @throws IOException if an IO error occurred or the input stream ended unexpectedly.
     */
    @NotNull
    public static FileType handleContainer(@NotNull final BufferedInputStream inputStream, @NotNull FileType fileType) throws IOException
    {
        switch (fileType) {
            case Riff:
                return detectFileType(inputStream, 8);
            case Tiff:
            default:
                return fileType;
        }
    }
}
