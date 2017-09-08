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
package com.drew.metadata.gif;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Drew Noakes https://drewnoakes.com
 * @author Kevin Mott https://github.com/kwhopper
 */
@SuppressWarnings("WeakerAccess")
public class GifControlDirectory extends Directory
{
    public static final int TAG_DELAY = 1;
    public static final int TAG_DISPOSAL_METHOD = 2;
    public static final int TAG_USER_INPUT_FLAG = 3;
    public static final int TAG_TRANSPARENT_COLOR_FLAG = 4;
    public static final int TAG_TRANSPARENT_COLOR_INDEX = 5;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_DELAY, "Delay");
        _tagNameMap.put(TAG_DISPOSAL_METHOD, "Disposal Method");
        _tagNameMap.put(TAG_USER_INPUT_FLAG, "User Input Flag");
        _tagNameMap.put(TAG_TRANSPARENT_COLOR_FLAG, "Transparent Color Flag");
        _tagNameMap.put(TAG_TRANSPARENT_COLOR_INDEX, "Transparent Color Index");
    }

    public GifControlDirectory()
    {
        this.setDescriptor(new GifControlDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "GIF Control";
    }

    /**
     * @return The {@link DisposalMethod}.
     */
    @NotNull
    public DisposalMethod getDisposalMethod() {
        return (DisposalMethod) getObject(TAG_DISPOSAL_METHOD);
    }

    /**
     * @return Whether the GIF has transparency.
     */
    public boolean isTransparent() {
        Boolean transparent = getBooleanObject(TAG_TRANSPARENT_COLOR_FLAG);
        return transparent != null && transparent;
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    /**
     * Disposal method indicates the way in which the graphic is to be treated
     * after being displayed.
     */
    public enum DisposalMethod {
        NOT_SPECIFIED,
        DO_NOT_DISPOSE,
        RESTORE_TO_BACKGROUND_COLOR,
        RESTORE_TO_PREVIOUS,
        TO_BE_DEFINED,
        INVALID;

        public static DisposalMethod typeOf(int value) {
            switch (value) {
                case 0: return NOT_SPECIFIED;
                case 1: return DO_NOT_DISPOSE;
                case 2: return RESTORE_TO_BACKGROUND_COLOR;
                case 3: return RESTORE_TO_PREVIOUS;
                case 4:
                case 5:
                case 6:
                case 7: return TO_BE_DEFINED;
                default: return INVALID;
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case DO_NOT_DISPOSE:
                    return "Don't Dispose";
                case INVALID:
                    return "Invalid value";
                case NOT_SPECIFIED:
                    return "Not Specified";
                case RESTORE_TO_BACKGROUND_COLOR:
                    return "Restore to Background Color";
                case RESTORE_TO_PREVIOUS:
                    return "Restore to Previous";
                case TO_BE_DEFINED:
                    return "To Be Defined";
                default:
                    return super.toString();
            }
        }
    }
}
