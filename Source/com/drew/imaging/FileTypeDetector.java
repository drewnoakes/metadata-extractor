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
import java.util.HashMap;

/**
 * Examines the a file's first bytes and estimates the file's type.
 */
public class FileTypeDetector
{
    private final static ByteTrie<FileType> _root;
    private static final HashMap<String, FileType> _ftypMap;

    static
    {
        _root = new ByteTrie<FileType>();
        _root.setDefaultValue(FileType.Unknown);

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
        // NOTE this doesn't work for NEF as it incorrectly flags many other TIFF files as being NEF
//        _root.addPath(FileType.Nef, "MM".getBytes(), new byte[]{0x00, 0x2a, 0x00, 0x00, 0x00, (byte)0x08, 0x00});
        _root.addPath(FileType.Orf, "IIRO".getBytes(), new byte[]{(byte)0x08, 0x00});
        _root.addPath(FileType.Orf, "MMOR".getBytes(), new byte[]{(byte)0x00, 0x00});
        _root.addPath(FileType.Orf, "IIRS".getBytes(), new byte[]{(byte)0x08, 0x00});
        _root.addPath(FileType.Raf, "FUJIFILMCCD-RAW".getBytes());
        _root.addPath(FileType.Rw2, "II".getBytes(), new byte[]{0x55, 0x00});

        _ftypMap = new HashMap<String, FileType>();

        // http://www.ftyps.com

        // QuickTime Mov
        _ftypMap.put("ftypmoov", FileType.Mov);
        _ftypMap.put("ftypwide", FileType.Mov);
        _ftypMap.put("ftypmdat", FileType.Mov);
        _ftypMap.put("ftypfree", FileType.Mov);
        _ftypMap.put("ftypqt  ", FileType.Mov);

        // MP4
        _ftypMap.put("ftypavc1", FileType.Mp4);
        _ftypMap.put("ftypiso2", FileType.Mp4);
        _ftypMap.put("ftypisom", FileType.Mp4);
        _ftypMap.put("ftypM4A ", FileType.Mp4);
        _ftypMap.put("ftypM4B ", FileType.Mp4);
        _ftypMap.put("ftypM4P ", FileType.Mp4);
        _ftypMap.put("ftypM4V ", FileType.Mp4);
        _ftypMap.put("ftypM4VH", FileType.Mp4);
        _ftypMap.put("ftypM4VP", FileType.Mp4);
        _ftypMap.put("ftypmmp4", FileType.Mp4);
        _ftypMap.put("ftypmp41", FileType.Mp4);
        _ftypMap.put("ftypmp42", FileType.Mp4);
        _ftypMap.put("ftypmp71", FileType.Mp4);
        _ftypMap.put("ftypMSNV", FileType.Mp4);
        _ftypMap.put("ftypNDAS", FileType.Mp4);
        _ftypMap.put("ftypNDSC", FileType.Mp4);
        _ftypMap.put("ftypNDSH", FileType.Mp4);
        _ftypMap.put("ftypNDSM", FileType.Mp4);
        _ftypMap.put("ftypNDSP", FileType.Mp4);
        _ftypMap.put("ftypNDSS", FileType.Mp4);
        _ftypMap.put("ftypNDXC", FileType.Mp4);
        _ftypMap.put("ftypNDXH", FileType.Mp4);
        _ftypMap.put("ftypNDXM", FileType.Mp4);
        _ftypMap.put("ftypNDXP", FileType.Mp4);
        _ftypMap.put("ftypNDXS", FileType.Mp4);

        // HEIF
        _ftypMap.put("ftypmif1", FileType.Heif);
        _ftypMap.put("ftypmsf1", FileType.Heif);
        _ftypMap.put("ftypheic", FileType.Heif);
        _ftypMap.put("ftypheix", FileType.Heif);
        _ftypMap.put("ftyphevc", FileType.Heif);
        _ftypMap.put("ftyphevx", FileType.Heif);
    }

    private FileTypeDetector() throws Exception
    {
        throw new Exception("Not intended for instantiation");
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
        if (!inputStream.markSupported())
            throw new IOException("Stream must support mark/reset");

        int maxByteCount = Math.max(16, _root.getMaxDepth());

        inputStream.mark(maxByteCount);

        byte[] bytes = new byte[maxByteCount];
        int bytesRead = inputStream.read(bytes);

        if (bytesRead == -1)
            throw new IOException("Stream ended before file's magic number could be determined.");

        inputStream.reset();

        FileType fileType = _root.find(bytes);

        assert(fileType != null);

        if (fileType == FileType.Unknown) {
            String eightCC = new String(bytes, 4, 8);
            // Test at offset 4 for Base Media Format (i.e. QuickTime, MP4, etc...) identifier "ftyp" plus four identifying characters
            FileType t = _ftypMap.get(eightCC);
            if (t != null)
                return t;
        } else if (fileType == FileType.Riff) {
            String fourCC = new String(bytes, 8, 4);
            if (fourCC.equals("WAVE"))
                return FileType.Wav;
            if (fourCC.equals("AVI "))
                return FileType.Avi;
            if (fourCC.equals("WEBP"))
                return FileType.WebP;
        }

        return fileType;
    }
}
