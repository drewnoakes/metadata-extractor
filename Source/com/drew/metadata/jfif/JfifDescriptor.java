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
package com.drew.metadata.jfif;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string versions of the tags stored in a JfifDirectory.
 * <p/>
 * More info at: http://en.wikipedia.org/wiki/JPEG_File_Interchange_Format
 *
 * @author Yuri B, Drew Noakes
 */
public class JfifDescriptor extends TagDescriptor
{
    public JfifDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case JfifDirectory.TAG_JFIF_RESX:
                return getImageResXDescription();
            case JfifDirectory.TAG_JFIF_RESY:
                return getImageResYDescription();
            case JfifDirectory.TAG_JFIF_VERSION:
                return getImageVersionDescription();
            case JfifDirectory.TAG_JFIF_UNITS:
                return getImageResUnitsDescription();
        }

        return _directory.getString(tagType);
    }

    public String getImageVersionDescription() throws MetadataException
    {
        if (!_directory.containsTag(JfifDirectory.TAG_JFIF_VERSION))
            return null;
        int v = _directory.getInt(JfifDirectory.TAG_JFIF_VERSION);
        return String.format("%d.%d", (v & 0xFF00) >> 8, v & 0xFF);
    }

    public String getImageResYDescription() throws MetadataException
    {
        if (!_directory.containsTag(JfifDirectory.TAG_JFIF_RESY))
            return null;
        int resY = _directory.getInt(JfifDirectory.TAG_JFIF_RESY);
        return String.format("%d dot%s",
                resY,
                resY==1 ? "" : "s");
    }

    public String getImageResXDescription() throws MetadataException
    {
        if (!_directory.containsTag(JfifDirectory.TAG_JFIF_RESX))
            return null;
        int resX = _directory.getInt(JfifDirectory.TAG_JFIF_RESX);
        return String.format("%d dot%s",
                resX,
                resX==1 ? "" : "s");
    }

    public String getImageResUnitsDescription() throws MetadataException
    {
        if (!_directory.containsTag(JfifDirectory.TAG_JFIF_UNITS))
            return null;
        switch (_directory.getInt(JfifDirectory.TAG_JFIF_UNITS)) {
            case 0: return "none";
            case 1: return "inch";
            case 2: return "centimetre";
            default:
                return "unit";
        }
    }
}