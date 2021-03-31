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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.exif.makernotes.LeicaType5MakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link LeicaType5MakernoteDirectory}.
 * <p>
 * Tag reference from: http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/Panasonic.html
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class LeicaType5MakernoteDescriptor extends TagDescriptor<LeicaType5MakernoteDirectory>
{
    public LeicaType5MakernoteDescriptor(@NotNull LeicaType5MakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TagExposureMode:
                return getExposureModeDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getExposureModeDescription()
    {
        byte[] values = _directory.getByteArray(TagExposureMode);
        if (values == null || values.length < 4)
            return null;

        String join = String.format("%d %d %d %d", values[0], values[1], values[2], values[3]);

        if(join.equals("0 0 0 0"))
            return "Program AE";
        else if(join.equals("1 0 0 0"))
            return "Aperture-priority AE";
        else if(join.equals("1 1 0 0"))
            return "Aperture-priority AE (1)";
        else if(join.equals("2 0 0 0"))
            return "Shutter speed priority AE";  // guess
        else if(join.equals("3 0 0 0"))
            return "Manual";
        else
            return String.format("Unknown (%s)", join);
    }
}
