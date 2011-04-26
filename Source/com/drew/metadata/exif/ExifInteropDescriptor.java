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
 * Provides human-readable string representations of tag values stored in a <code>ExifInteropDirectory</code>.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class ExifInteropDescriptor extends TagDescriptor
{
    public ExifInteropDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case ExifInteropDirectory.TAG_INTEROP_INDEX:
                return getInteropIndexDescription();
            case ExifInteropDirectory.TAG_INTEROP_VERSION:
                return getInteropVersionDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getInteropVersionDescription() throws MetadataException
    {
        if (!_directory.containsTag(ExifInteropDirectory.TAG_INTEROP_VERSION)) return null;
        int[] ints = _directory.getIntArray(ExifInteropDirectory.TAG_INTEROP_VERSION);
        return ExifDescriptor.convertBytesToVersionString(ints);
    }

    public String getInteropIndexDescription()
    {
        if (!_directory.containsTag(ExifInteropDirectory.TAG_INTEROP_INDEX)) return null;
        String interopIndex = _directory.getString(ExifInteropDirectory.TAG_INTEROP_INDEX).trim();
        if ("R98".equalsIgnoreCase(interopIndex)) {
            return "Recommended Exif Interoperability Rules (ExifR98)";
        } else {
            return "Unknown (" + interopIndex + ")";
        }
    }
}
