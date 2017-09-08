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
public class QuickTimeTimecodeDirectory extends QuickTimeDirectory
{
    // Timecode Media Description Atom
    public static final int TAG_DROP_FRAME                      = 1;
    public static final int TAG_24_HOUR_MAX                     = 2;
    public static final int TAG_NEGATIVE_TIMES_OK               = 3;
    public static final int TAG_COUNTER                         = 4;
    public static final int TAG_TEXT_FONT                       = 5;
    public static final int TAG_TEXT_FACE                       = 6;
    public static final int TAG_TEXT_SIZE                       = 7;
    public static final int TAG_TEXT_COLOR                      = 8;
    public static final int TAG_BACKGROUND_COLOR                = 9;
    public static final int TAG_FONT_NAME                       = 10;

    public QuickTimeTimecodeDirectory()
    {
        this.setDescriptor(new QuickTimeTimecodeDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        QuickTimeMediaDirectory.addQuickTimeMediaTags(_tagNameMap);
        _tagNameMap.put(TAG_DROP_FRAME, "Drop Frame");
        _tagNameMap.put(TAG_24_HOUR_MAX, "24 Hour Max");
        _tagNameMap.put(TAG_NEGATIVE_TIMES_OK, "Negative Times OK");
        _tagNameMap.put(TAG_COUNTER, "Counter");
        _tagNameMap.put(TAG_TEXT_FONT, "Text Font");
        _tagNameMap.put(TAG_TEXT_FACE, "Text Face");
        _tagNameMap.put(TAG_TEXT_SIZE, "Text Size");
        _tagNameMap.put(TAG_TEXT_COLOR, "Text Color");
        _tagNameMap.put(TAG_BACKGROUND_COLOR, "Background Color");
        _tagNameMap.put(TAG_FONT_NAME, "Font Name");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "QuickTime Timecode";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
