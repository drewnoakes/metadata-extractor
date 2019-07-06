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
package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * ISO/IEC 23008-12:2017 pg.14
 */
public class AuxiliaryTypeProperty extends FullBox
{
    String auxType;
    int[] auxSubtype;

    public AuxiliaryTypeProperty(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        auxType = getZeroTerminatedString((int)box.size - 12, reader);
        // auxSubtype
    }

    private String getZeroTerminatedString(int maxLengthBytes, SequentialReader reader) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < maxLengthBytes; i++) {
            stringBuilder.append((char)reader.getByte());
            if (stringBuilder.charAt(stringBuilder.length() - 1) == 0) {
                break;
            }
        }
        return stringBuilder.toString().trim();
    }
}
