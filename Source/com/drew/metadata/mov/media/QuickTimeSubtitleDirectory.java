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
package com.drew.metadata.mov.media;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.mov.QuickTimeDirectory;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class QuickTimeSubtitleDirectory extends QuickTimeDirectory
{
    public static final int TAG_VERTICAL_PLACEMENT          = 1;
    public static final int TAG_SOME_SAMPLES_FORCED         = 2;
    public static final int TAG_ALL_SAMPLES_FORCED          = 3;
    public static final int TAG_DEFAULT_TEXT_BOX            = 4;
    public static final int TAG_FONT_IDENTIFIER             = 5;
    public static final int TAG_FONT_FACE                   = 6;
    public static final int TAG_FONT_SIZE                   = 7;
    public static final int TAG_FOREGROUND_COLOR            = 8;

    public QuickTimeSubtitleDirectory()
    {
        this.setDescriptor(new QuickTimeSubtitleDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        QuickTimeMediaDirectory.addQuickTimeMediaTags(_tagNameMap);
        _tagNameMap.put(TAG_VERTICAL_PLACEMENT, "Vertical Placement");
        _tagNameMap.put(TAG_SOME_SAMPLES_FORCED, "Some Samples Forced");
        _tagNameMap.put(TAG_ALL_SAMPLES_FORCED, "All Samples Forced");
        _tagNameMap.put(TAG_DEFAULT_TEXT_BOX, "Default Text Box");
        _tagNameMap.put(TAG_FONT_IDENTIFIER, "Font Identifier");
        _tagNameMap.put(TAG_FONT_FACE, "Font Face");
        _tagNameMap.put(TAG_FONT_SIZE, "Font Size");
        _tagNameMap.put(TAG_FOREGROUND_COLOR, "Foreground Color");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "QuickTime Subtitle";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
