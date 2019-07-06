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
package com.drew.metadata.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.jpeg.JpegDirectory.*;

/**
 * Provides human-readable string versions of the tags stored in a JpegDirectory.
 * Thanks to Darrell Silver (www.darrellsilver.com) for the initial version of this class.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class JpegDescriptor extends TagDescriptor<JpegDirectory>
{
    public JpegDescriptor(@NotNull JpegDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType)
        {
            case TAG_COMPRESSION_TYPE:
                return getImageCompressionTypeDescription();
            case TAG_COMPONENT_DATA_1:
                return getComponentDataDescription(0);
            case TAG_COMPONENT_DATA_2:
                return getComponentDataDescription(1);
            case TAG_COMPONENT_DATA_3:
                return getComponentDataDescription(2);
            case TAG_COMPONENT_DATA_4:
                return getComponentDataDescription(3);
            case TAG_DATA_PRECISION:
                return getDataPrecisionDescription();
            case TAG_IMAGE_HEIGHT:
                return getImageHeightDescription();
            case TAG_IMAGE_WIDTH:
                return getImageWidthDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getImageCompressionTypeDescription()
    {
        return getIndexedDescription(TAG_COMPRESSION_TYPE,
            "Baseline",
            "Extended sequential, Huffman",
            "Progressive, Huffman",
            "Lossless, Huffman",
            null, // no 4
            "Differential sequential, Huffman",
            "Differential progressive, Huffman",
            "Differential lossless, Huffman",
            "Reserved for JPEG extensions",
            "Extended sequential, arithmetic",
            "Progressive, arithmetic",
            "Lossless, arithmetic",
            null, // no 12
            "Differential sequential, arithmetic",
            "Differential progressive, arithmetic",
            "Differential lossless, arithmetic");
    }

    @Nullable
    public String getImageWidthDescription()
    {
        final String value = _directory.getString(TAG_IMAGE_WIDTH);
        if (value==null)
            return null;
        return value + " pixels";
    }

    @Nullable
    public String getImageHeightDescription()
    {
        final String value = _directory.getString(TAG_IMAGE_HEIGHT);
        if (value==null)
            return null;
        return value + " pixels";
    }

    @Nullable
    public String getDataPrecisionDescription()
    {
        final String value = _directory.getString(TAG_DATA_PRECISION);
        if (value==null)
            return null;
        return value + " bits";
    }

    @Nullable
    public String getComponentDataDescription(int componentNumber)
    {
        JpegComponent value = _directory.getComponent(componentNumber);

        if (value==null)
            return null;

        return value.getComponentName() + " component: " + value;
    }
}
