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
package com.drew.metadata.jpeg;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string versions of the tags stored in a JpegDirectory.
 * Thanks to Darrell Silver (www.darrellsilver.com) for the initial version of this class.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
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
            case JpegDirectory.TAG_COMPRESSION_TYPE:
                return getImageCompressionTypeDescription();
            case JpegDirectory.TAG_COMPONENT_DATA_1:
                return getComponentDataDescription(0);
            case JpegDirectory.TAG_COMPONENT_DATA_2:
                return getComponentDataDescription(1);
            case JpegDirectory.TAG_COMPONENT_DATA_3:
                return getComponentDataDescription(2);
            case JpegDirectory.TAG_COMPONENT_DATA_4:
                return getComponentDataDescription(3);
            case JpegDirectory.TAG_DATA_PRECISION:
                return getDataPrecisionDescription();
            case JpegDirectory.TAG_IMAGE_HEIGHT:
                return getImageHeightDescription();
            case JpegDirectory.TAG_IMAGE_WIDTH:
                return getImageWidthDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getImageCompressionTypeDescription()
    {
        Integer value = _directory.getInteger(JpegDirectory.TAG_COMPRESSION_TYPE);
        if (value==null)
            return null;
        // Note there is no 2 or 12
        switch (value) {
            case 0: return "Baseline";
            case 1: return "Extended sequential, Huffman";
            case 2: return "Progressive, Huffman";
            case 3: return "Lossless, Huffman";
            case 5: return "Differential sequential, Huffman";
            case 6: return "Differential progressive, Huffman";
            case 7: return "Differential lossless, Huffman";
            case 8: return "Reserved for JPEG extensions";
            case 9: return "Extended sequential, arithmetic";
            case 10: return "Progressive, arithmetic";
            case 11: return "Lossless, arithmetic";
            case 13: return "Differential sequential, arithmetic";
            case 14: return "Differential progressive, arithmetic";
            case 15: return "Differential lossless, arithmetic";
            default:
                return "Unknown type: "+ value;
        }
    }
    @Nullable
    public String getImageWidthDescription()
    {
        final String value = _directory.getString(JpegDirectory.TAG_IMAGE_WIDTH);
        if (value==null)
            return null;
        return value + " pixels";
    }

    @Nullable
    public String getImageHeightDescription()
    {
        final String value = _directory.getString(JpegDirectory.TAG_IMAGE_HEIGHT);
        if (value==null)
            return null;
        return value + " pixels";
    }

    @Nullable
    public String getDataPrecisionDescription()
    {
        final String value = _directory.getString(JpegDirectory.TAG_DATA_PRECISION);
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

        return value.getComponentName() + " component: Quantization table " + value.getQuantizationTableNumber()
            + ", Sampling factors " + value.getHorizontalSamplingFactor()
            + " horiz/" + value.getVerticalSamplingFactor() + " vert";
    }
}
