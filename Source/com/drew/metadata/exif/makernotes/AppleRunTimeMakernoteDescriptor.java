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
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.Nullable;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * @author Bob Johnson
 */
public class AppleRunTimeMakernoteDescriptor extends TagDescriptor<AppleRunTimeMakernoteDirectory>
{
    public AppleRunTimeMakernoteDescriptor(AppleRunTimeMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case AppleRunTimeMakernoteDirectory.CMTimeFlags:
                return getFlagsDescription();
            case AppleRunTimeMakernoteDirectory.CMTimeValue:
                return getValueDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    // flags bitmask details
    // 0000 0001 = Valid
    // 0000 0010 = Rounded
    // 0000 0100 = Positive Infinity
    // 0000 1000 = Negative Infinity
    // 0001 0000 = Indefinite
    @Nullable
    public String getFlagsDescription()
    {
        try {
            final int value = _directory.getInt(AppleRunTimeMakernoteDirectory.CMTimeFlags);

            StringBuilder sb = new StringBuilder();

            if ((value & 0x1) == 1)
                sb.append("Valid");
            else
                sb.append("Invalid");

            if ((value & 0x2) != 0)
                sb.append(", rounded");

            if ((value & 0x4) != 0)
                sb.append(", positive infinity");

            if ((value & 0x8) != 0)
                sb.append(", negative infinity");

            if ((value & 0x10) != 0)
                sb.append(", indefinite");

            return sb.toString();
        } catch (MetadataException ignored) {
            return null;
        }
    }

    @Nullable
    public String getValueDescription()
    {
        try {
            long value = _directory.getLong(AppleRunTimeMakernoteDirectory.CMTimeValue);
            long scale = _directory.getLong(AppleRunTimeMakernoteDirectory.CMTimeScale);

            return String.format("%d seconds", (value / scale));
        } catch (MetadataException ignored) {
            return null;
        }
    }
}
