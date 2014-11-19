/*
 * Copyright 2002-2014 Drew Noakes
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

package com.drew.metadata.adobe;

import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string versions of the tags stored in an AdobeJpegDirectory.
 */
public class AdobeJpegDescriptor extends TagDescriptor<AdobeJpegDirectory>
{
    public AdobeJpegDescriptor(AdobeJpegDirectory directory)
    {
        super(directory);
    }

    @Override
	public String getDescription(int tagType)
    {
        switch (tagType) {
            case AdobeJpegDirectory.TAG_COLOR_TRANSFORM:
                return getColorTransformDescription();
            case AdobeJpegDirectory.TAG_DCT_ENCODE_VERSION:
                return getDctEncodeVersionDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    private String getDctEncodeVersionDescription()
    {
        Integer value = _directory.getInteger(AdobeJpegDirectory.TAG_COLOR_TRANSFORM);
        return value == null
                ? null
                : value == 0x64
                    ? "100"
                    : Integer.toString(value);
    }


    private String getColorTransformDescription()
    {
        Integer value = _directory.getInteger(AdobeJpegDirectory.TAG_COLOR_TRANSFORM);
        if (value==null)
            return null;
        switch (value) {
            case 0: return "Unknown (RGB or CMYK)";
            case 1: return "YCbCr";
            case 2: return "YCCK";
            default: return String.format("Unknown transform (%d)", value);
        }
    }
}
