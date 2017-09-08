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
package com.drew.metadata.mp4;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.ArrayList;
import java.util.Arrays;

import static com.drew.metadata.mp4.Mp4Directory.*;

public class Mp4Descriptor<T extends Directory> extends TagDescriptor<Mp4Directory> {

    public Mp4Descriptor(@NotNull Mp4Directory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_MAJOR_BRAND:
                return getMajorBrandDescription();
            case TAG_COMPATIBLE_BRANDS:
                return getCompatibleBrandsDescription();
            case TAG_DURATION:
                return getDurationDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    private String getMajorBrandDescription()
    {
        byte[] value = _directory.getByteArray(Mp4Directory.TAG_MAJOR_BRAND);
        if (value == null)
            return null;
        return Mp4Dictionary.lookup(TAG_MAJOR_BRAND, new String(value));
    }

    private String getCompatibleBrandsDescription()
    {
        String[] values = _directory.getStringArray(Mp4Directory.TAG_COMPATIBLE_BRANDS);
        if (values == null)
            return null;

        ArrayList<String> compatibleBrandsValues = new ArrayList<String>();
        for (String value : values) {
            String compatibleBrandsValue = Mp4Dictionary.lookup(TAG_MAJOR_BRAND, value);
            compatibleBrandsValues.add(compatibleBrandsValue == null ? value : compatibleBrandsValue);
        }
        return Arrays.toString(compatibleBrandsValues.toArray());
    }

    private String getDurationDescription()
    {
        Long value = _directory.getLongObject(TAG_DURATION);
        if (value == null)
            return null;

        Integer hours = (int)(value / (Math.pow(60, 2)));
        Integer minutes = (int)((value / (Math.pow(60, 1))) - (hours * 60));
        Integer seconds = (int)Math.ceil((value / (Math.pow(60, 0))) - (minutes * 60));
        return String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
    }
}
