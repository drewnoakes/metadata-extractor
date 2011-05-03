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

package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a <code>SonyType6MakernoteDirectory</code>.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class SonyType6MakernoteDescriptor extends TagDescriptor<SonyType6MakernoteDirectory>
{
    public SonyType6MakernoteDescriptor(@NotNull SonyType6MakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case SonyType6MakernoteDirectory.TAG_MAKER_NOTE_THUMB_VERSION:
                return getMakerNoteThumbVersionDescription();
        }
        return _directory.getString(tagType);
    }

    @Nullable
    public String getMakerNoteThumbVersionDescription()
    {
        byte[] value = _directory.getByteArray(SonyType6MakernoteDirectory.TAG_MAKER_NOTE_THUMB_VERSION);
        if (value==null)
            return null;
        return new String(value);
    }
}
