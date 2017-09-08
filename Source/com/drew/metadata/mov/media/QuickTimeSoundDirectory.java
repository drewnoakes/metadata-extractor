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
public class QuickTimeSoundDirectory extends QuickTimeDirectory
{
    // Sound Sample Description Atom
    public static final int TAG_AUDIO_FORMAT                            = 0x0301;
    public static final int TAG_NUMBER_OF_CHANNELS                      = 0x0302;
    public static final int TAG_AUDIO_SAMPLE_SIZE                       = 0x0303;
    public static final int TAG_AUDIO_SAMPLE_RATE                       = 0x0304;

    public static final int TAG_SOUND_BALANCE                           = 0x0305;

    public QuickTimeSoundDirectory()
    {
        this.setDescriptor(new QuickTimeSoundDescriptor(this));
    }

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        QuickTimeMediaDirectory.addQuickTimeMediaTags(_tagNameMap);
        _tagNameMap.put(TAG_AUDIO_FORMAT, "Format");
        _tagNameMap.put(TAG_NUMBER_OF_CHANNELS, "Number of Channels");
        _tagNameMap.put(TAG_AUDIO_SAMPLE_SIZE, "Sample Size");
        _tagNameMap.put(TAG_AUDIO_SAMPLE_RATE, "Sample Rate");
        _tagNameMap.put(TAG_SOUND_BALANCE, "Balance");
    }

    @Override
    @NotNull
    public String getName()
    {
        return "QuickTime Sound";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
