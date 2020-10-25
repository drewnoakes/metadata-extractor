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
package com.drew.imaging.quicktime;

import com.drew.imaging.FileType;
import com.drew.imaging.TypeChecker;

import java.util.HashMap;

public class QuickTimeTypeChecker implements TypeChecker
{
    private static final HashMap<String, FileType> _ftypMap;

    static
    {
        _ftypMap = new HashMap<String, FileType>();

        // http://www.ftyps.com

        // QuickTime Mov
        _ftypMap.put("moov", FileType.QuickTime);
        _ftypMap.put("wide", FileType.QuickTime);
        _ftypMap.put("mdat", FileType.QuickTime);
        _ftypMap.put("free", FileType.QuickTime);
        _ftypMap.put("qt  ", FileType.QuickTime);
        _ftypMap.put("3g2a", FileType.QuickTime);

        // MP4
        _ftypMap.put("avc1", FileType.Mp4);
        _ftypMap.put("iso2", FileType.Mp4);
        _ftypMap.put("isom", FileType.Mp4);
        _ftypMap.put("M4A ", FileType.Mp4);
        _ftypMap.put("M4B ", FileType.Mp4);
        _ftypMap.put("M4P ", FileType.Mp4);
        _ftypMap.put("M4V ", FileType.Mp4);
        _ftypMap.put("M4VH", FileType.Mp4);
        _ftypMap.put("M4VP", FileType.Mp4);
        _ftypMap.put("mmp4", FileType.Mp4);
        _ftypMap.put("mp41", FileType.Mp4);
        _ftypMap.put("mp42", FileType.Mp4);
        _ftypMap.put("mp71", FileType.Mp4);
        _ftypMap.put("MSNV", FileType.Mp4);
        _ftypMap.put("NDAS", FileType.Mp4);
        _ftypMap.put("NDSC", FileType.Mp4);
        _ftypMap.put("NDSH", FileType.Mp4);
        _ftypMap.put("NDSM", FileType.Mp4);
        _ftypMap.put("NDSP", FileType.Mp4);
        _ftypMap.put("NDSS", FileType.Mp4);
        _ftypMap.put("NDXC", FileType.Mp4);
        _ftypMap.put("NDXH", FileType.Mp4);
        _ftypMap.put("NDXM", FileType.Mp4);
        _ftypMap.put("NDXP", FileType.Mp4);
        _ftypMap.put("NDXS", FileType.Mp4);
        _ftypMap.put("nvr1", FileType.Mp4);

        // HEIF
        _ftypMap.put("mif1", FileType.Heif);
        _ftypMap.put("msf1", FileType.Heif);
        _ftypMap.put("heic", FileType.Heif);
        _ftypMap.put("heix", FileType.Heif);
        _ftypMap.put("hevc", FileType.Heif);
        _ftypMap.put("hevx", FileType.Heif);

        // CRX
        _ftypMap.put("crx ", FileType.Crx);
    }

    @Override
    public int getByteCount()
    {
        return 12;
    }

    @Override
    public FileType checkType(byte[] bytes)
    {
        // Test at offset 4 for Base Media Format (i.e. QuickTime, MP4, etc...) identifier "ftyp"
        // plus four identifying characters.

        if (bytes[4] == 'f' &&
            bytes[5] == 't' &&
            bytes[6] == 'y' &&
            bytes[7] == 'p') {

            String fourCC = new String(bytes, 8, 4);

            FileType t = _ftypMap.get(fourCC);

            if (t != null)
                return t;
        }

        return FileType.Unknown;
    }
}
