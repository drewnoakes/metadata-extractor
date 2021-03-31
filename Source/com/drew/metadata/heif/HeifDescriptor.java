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
package com.drew.metadata.heif;

import com.drew.metadata.TagDescriptor;

public class HeifDescriptor extends TagDescriptor<HeifDirectory>
{

    public HeifDescriptor(HeifDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case HeifDirectory.TAG_IMAGE_WIDTH:
            case HeifDirectory.TAG_IMAGE_HEIGHT:
                return getPixelDescription(tagType);
            case HeifDirectory.TAG_IMAGE_ROTATION:
                return getRotationDescription(tagType);
            default:
                return super.getDescription(tagType);
        }
    }

    public String getPixelDescription(int tagType)
    {
        return _directory.getString(tagType) + " pixels";
    }

    public String getRotationDescription(int tagType)
    {
        return (_directory.getInteger(tagType) * 90) + " degrees";
    }
}
