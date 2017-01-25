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
package com.drew.metadata.png;

import com.drew.imaging.png.PngColorType;
import com.drew.lang.KeyValuePair;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.io.IOException;
import java.util.List;

import static com.drew.metadata.png.PngDirectory.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class PngDescriptor extends TagDescriptor<PngDirectory>
{
    public PngDescriptor(@NotNull PngDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_COLOR_TYPE:
                return getColorTypeDescription();
            case TAG_COMPRESSION_TYPE:
                return getCompressionTypeDescription();
            case TAG_FILTER_METHOD:
                return getFilterMethodDescription();
            case TAG_INTERLACE_METHOD:
                return getInterlaceMethodDescription();
            case TAG_PALETTE_HAS_TRANSPARENCY:
                return getPaletteHasTransparencyDescription();
            case TAG_SRGB_RENDERING_INTENT:
                return getIsSrgbColorSpaceDescription();
            case TAG_TEXTUAL_DATA:
                return getTextualDataDescription();
            case TAG_BACKGROUND_COLOR:
                return getBackgroundColorDescription();
            case TAG_UNIT_SPECIFIER:
                return getUnitSpecifierDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getColorTypeDescription()
    {
        Integer value = _directory.getInteger(TAG_COLOR_TYPE);
        if (value == null)
            return null;
        PngColorType colorType = PngColorType.fromNumericValue(value);
        if (colorType == null)
            return null;
        return colorType.getDescription();
    }

    @Nullable
    public String getCompressionTypeDescription()
    {
        return getIndexedDescription(TAG_COMPRESSION_TYPE, "Deflate");
    }

    @Nullable
    public String getFilterMethodDescription()
    {
        return getIndexedDescription(TAG_FILTER_METHOD, "Adaptive");
    }

    @Nullable
    public String getInterlaceMethodDescription()
    {
        return getIndexedDescription(TAG_INTERLACE_METHOD, "No Interlace", "Adam7 Interlace");
    }

    @Nullable
    public String getPaletteHasTransparencyDescription()
    {
        return getIndexedDescription(TAG_PALETTE_HAS_TRANSPARENCY, null, "Yes");
    }

    @Nullable
    public String getIsSrgbColorSpaceDescription()
    {
        return getIndexedDescription(
            TAG_SRGB_RENDERING_INTENT,
            "Perceptual",
            "Relative Colorimetric",
            "Saturation",
            "Absolute Colorimetric"
        );
    }

    @Nullable
    public String getUnitSpecifierDescription()
    {
        return getIndexedDescription(
            TAG_UNIT_SPECIFIER,
            "Unspecified",
            "Metres"
        );
    }

    @Nullable
    public String getTextualDataDescription()
    {
        Object object = _directory.getObject(TAG_TEXTUAL_DATA);
        if (object == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<KeyValuePair> keyValues = (List<KeyValuePair>)object;
        StringBuilder sb = new StringBuilder();
        for (KeyValuePair keyValue : keyValues) {
            if (sb.length() != 0)
                sb.append('\n');
            sb.append(String.format("%s: %s", keyValue.getKey(), keyValue.getValue()));
        }
        return sb.toString();
    }

    @Nullable
    public String getBackgroundColorDescription()
    {
        byte[] bytes = _directory.getByteArray(TAG_BACKGROUND_COLOR);
        Integer colorType = _directory.getInteger(TAG_COLOR_TYPE);
        if (bytes == null || colorType == null) {
            return null;
        }
        SequentialReader reader = new SequentialByteArrayReader(bytes);
        try {
            // TODO do we need to normalise these based upon the bit depth?
            switch (colorType) {
                case 0:
                case 4:
                    return String.format("Greyscale Level %d", reader.getUInt16());
                case 2:
                case 6:
                    return String.format("R %d, G %d, B %d", reader.getUInt16(), reader.getUInt16(), reader.getUInt16());
                case 3:
                    return String.format("Palette Index %d", reader.getUInt8());
            }
        } catch (IOException ex) {
            return null;
        }
        return null;
    }
}
