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

    @Override
    public int getByteCount()
    {
        return 12;
    }

    @Override
    public FileType checkType(byte[] bytes)
    {
        String eightCC = new String(bytes, 4, 8);

        // Test at offset 4 for Base Media Format (i.e. QuickTime, MP4, etc...) identifier "ftyp" plus four identifying characters
        FileType t = _ftypMap.get(eightCC);
        if (t != null)
            return t;

        return FileType.Unknown;
    }
}
