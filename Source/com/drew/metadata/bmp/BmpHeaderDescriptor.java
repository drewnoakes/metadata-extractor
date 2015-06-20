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
package com.drew.metadata.bmp;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class BmpHeaderDescriptor extends TagDescriptor<BmpHeaderDirectory>
{
    public BmpHeaderDescriptor(@NotNull BmpHeaderDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case BmpHeaderDirectory.TAG_COMPRESSION:
                return getCompressionDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getCompressionDescription()
    {
        // 0 = None
        // 1 = RLE 8-bit/pixel
        // 2 = RLE 4-bit/pixel
        // 3 = Bit field (or Huffman 1D if BITMAPCOREHEADER2 (size 64))
        // 4 = JPEG (or RLE-24 if BITMAPCOREHEADER2 (size 64))
        // 5 = PNG
        // 6 = Bit field
        try {
            Integer value = _directory.getInteger(BmpHeaderDirectory.TAG_COMPRESSION);
            if (value == null)
                return null;
            Integer headerSize = _directory.getInteger(BmpHeaderDirectory.TAG_HEADER_SIZE);
            if (headerSize == null)
                return null;

            switch (value) {
                case 0: return "None";
                case 1: return "RLE 8-bit/pixel";
                case 2: return "RLE 4-bit/pixel";
                case 3: return headerSize == 64 ? "Bit field" : "Huffman 1D";
                case 4: return headerSize == 64 ? "JPEG" : "RLE-24";
                case 5: return "PNG";
                case 6: return "Bit field";
                default:
                    return super.getDescription(BmpHeaderDirectory.TAG_COMPRESSION);
            }
        } catch (Exception e) {
            return null;
        }
    }
}
