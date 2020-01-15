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
package com.drew.imaging.riff;

import com.drew.imaging.FileType;
import com.drew.imaging.TypeChecker;

public class RiffTypeChecker implements TypeChecker
{
    @Override
    public int getByteCount()
    {
        return 12;
    }

    @Override
    public FileType checkType(byte[] bytes)
    {
        String firstFour = new String(bytes, 0, 4);
        if (!firstFour.equals("RIFF"))
            return FileType.Unknown;

        String fourCC = new String(bytes, 8, 4);
        if (fourCC.equals("WAVE"))
            return FileType.Wav;
        if (fourCC.equals("AVI "))
            return FileType.Avi;
        if (fourCC.equals("WEBP"))
            return FileType.WebP;

        return FileType.Riff;
    }
}
