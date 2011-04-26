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

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a <code>PanasonicMakernoteDirectory</code>.
 * <p/>
 * Some information about this makernote taken from here:
 * http://www.ozhiker.com/electronics/pjmt/jpeg_info/panasonic_mn.html
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class PanasonicMakernoteDescriptor extends TagDescriptor
{
    public PanasonicMakernoteDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType)
        {
            case PanasonicMakernoteDirectory.TAG_PANASONIC_MACRO_MODE:
                return getMacroModeDescription();
            case PanasonicMakernoteDirectory.TAG_PANASONIC_RECORD_MODE:
                return getRecordModeDescription();
            case PanasonicMakernoteDirectory.TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO:
                return getPrintImageMatchingInfoDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getPrintImageMatchingInfoDescription() throws MetadataException
    {
        if (!_directory.containsTag(PanasonicMakernoteDirectory.TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO)) return null;
        byte[] bytes = _directory.getByteArray(PanasonicMakernoteDirectory.TAG_PANASONIC_PRINT_IMAGE_MATCHING_INFO);
        return "(" + bytes.length + " bytes)";
    }

    public String getMacroModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(PanasonicMakernoteDirectory.TAG_PANASONIC_MACRO_MODE)) return null;
        int value = _directory.getInt(PanasonicMakernoteDirectory.TAG_PANASONIC_MACRO_MODE);
        switch (value) {
            case 1:
                return "On";
            case 2:
                return "Off";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getRecordModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(PanasonicMakernoteDirectory.TAG_PANASONIC_RECORD_MODE)) return null;
        int value = _directory.getInt(PanasonicMakernoteDirectory.TAG_PANASONIC_RECORD_MODE);
        switch (value) {
            case 1:
                return "Normal";
            case 2:
                return "Portrait";
            case 9:
                return "Macro";
            default:
                return "Unknown (" + value + ")";
        }
    }
}
