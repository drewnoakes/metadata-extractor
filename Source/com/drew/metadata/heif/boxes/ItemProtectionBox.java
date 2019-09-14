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
import java.util.ArrayList;

/**
 * ISO/IEC 14496-12:2015 pg.80, 89-90
 */
public class ItemProtectionBox extends FullBox
{
    int protectionCount;
    ArrayList<ProtectionSchemeInfoBox> protectionSchemes;

    public ItemProtectionBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        protectionCount = reader.getUInt16();
        protectionSchemes = new ArrayList<ProtectionSchemeInfoBox>(protectionCount);
        for (int i = 1; i <= protectionCount; i++) {
            protectionSchemes.add(new ProtectionSchemeInfoBox(reader, box));
        }
    }

    static class ProtectionSchemeInfoBox extends Box
    {
        public ProtectionSchemeInfoBox(SequentialReader reader, Box box) throws IOException
        {
            super(box);
        }

        class OriginalFormatBox extends Box
        {
            String dataFormat;

            public OriginalFormatBox(SequentialReader reader, Box box) throws IOException
            {
                super(reader);

                dataFormat = reader.getString(4);
            }
        }
    }
}
