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
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes tags specific to certain Leica cameras.
 * <p>
 * Tag reference from: http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/Panasonic.html
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class LeicaType5MakernoteDirectory extends Directory
{
    public static final int TagLensModel = 0x0303;
    public static final int TagOriginalFileName = 0x0407;
    public static final int TagOriginalDirectory = 0x0408;
    public static final int TagExposureMode = 0x040d;
    public static final int TagShotInfo = 0x0410;
    public static final int TagFilmMode = 0x0412;
    public static final int TagWbRgbLevels = 0x0413;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TagLensModel, "Lens Model");
        _tagNameMap.put(TagOriginalFileName, "Original File Name");
        _tagNameMap.put(TagOriginalDirectory, "Original Directory");
        _tagNameMap.put(TagExposureMode, "Exposure Mode");
        _tagNameMap.put(TagShotInfo, "Shot Info" );
        _tagNameMap.put(TagFilmMode, "Film Mode");
        _tagNameMap.put(TagWbRgbLevels, "WB RGB Levels");
    }

    public LeicaType5MakernoteDirectory()
    {
        this.setDescriptor(new LeicaType5MakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Leica Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
