/*
 * Copyright 2002-2011 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.metadata.jpeg;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string versions of the tags stored in a JpegDirectory.
 * Thanks to Darrell Silver (www.darrellsilver.com) for the initial version of this class.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegDescriptor extends TagDescriptor
{
    public JpegDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType)
        {
            case JpegDirectory.TAG_JPEG_COMPRESSION_TYPE:
                return getImageCompressionTypeDescription();
            case JpegDirectory.TAG_JPEG_COMPONENT_DATA_1:
                return getComponentDataDescription(0);
            case JpegDirectory.TAG_JPEG_COMPONENT_DATA_2:
                return getComponentDataDescription(1);
            case JpegDirectory.TAG_JPEG_COMPONENT_DATA_3:
                return getComponentDataDescription(2);
            case JpegDirectory.TAG_JPEG_COMPONENT_DATA_4:
                return getComponentDataDescription(3);
            case JpegDirectory.TAG_JPEG_DATA_PRECISION:
                return getDataPrecisionDescription();
            case JpegDirectory.TAG_JPEG_IMAGE_HEIGHT:
                return getImageHeightDescription();
            case JpegDirectory.TAG_JPEG_IMAGE_WIDTH:
                return getImageWidthDescription();
        }

        return _directory.getString(tagType);
    }

    public String getImageCompressionTypeDescription() throws MetadataException
    {
        if (!_directory.containsTag(JpegDirectory.TAG_JPEG_COMPRESSION_TYPE))
            return null;
        int value = _directory.getInt(JpegDirectory.TAG_JPEG_COMPRESSION_TYPE);
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
    public String getImageWidthDescription()
    {
        return _directory.getString(JpegDirectory.TAG_JPEG_IMAGE_WIDTH) + " pixels";
    }

    public String getImageHeightDescription()
    {
        return _directory.getString(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT) + " pixels";
    }

    public String getDataPrecisionDescription()
    {
        return _directory.getString(JpegDirectory.TAG_JPEG_DATA_PRECISION) + " bits";
    }

    public String getComponentDataDescription(int componentNumber) throws MetadataException
    {
        JpegComponent component = ((JpegDirectory)_directory).getComponent(componentNumber);

        if (component==null)
            throw new MetadataException("No Jpeg component exists with number " + componentNumber);

        StringBuffer sb = new StringBuffer();
        sb.append(component.getComponentName());
        sb.append(" component: Quantization table ");
        sb.append(component.getQuantizationTableNumber());
        sb.append(", Sampling factors ");
        sb.append(component.getHorizontalSamplingFactor());
        sb.append(" horiz/");
        sb.append(component.getVerticalSamplingFactor());
        sb.append(" vert");
        return sb.toString();
    }
}
