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
package com.drew.metadata.jfxx;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import static com.drew.metadata.jfxx.JfxxDirectory.*;

/**
 * Provides human-readable string versions of the tags stored in a JfxxDirectory.
 *
 * <ul>
 *   <li>http://en.wikipedia.org/wiki/JPEG_File_Interchange_Format</li>
 *   <li>http://www.w3.org/Graphics/JPEG/jfif3.pdf</li>
 * </ul>
 *
 * @author Drew Noakes
 */
@SuppressWarnings("WeakerAccess")
public class JfxxDescriptor extends TagDescriptor<JfxxDirectory>
{
    public JfxxDescriptor(@NotNull JfxxDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_EXTENSION_CODE:
                return getExtensionCodeDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getExtensionCodeDescription()
    {
        Integer value = _directory.getInteger(TAG_EXTENSION_CODE);
        if (value==null)
            return null;
        switch (value) {
            case 0x10: return "Thumbnail coded using JPEG";
            case 0x11: return "Thumbnail stored using 1 byte/pixel";
            case 0x13: return "Thumbnail stored using 3 bytes/pixel";
            default: return "Unknown extension code " + value;
        }
    }
}
