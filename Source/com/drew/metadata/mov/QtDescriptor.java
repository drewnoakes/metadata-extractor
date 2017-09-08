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
package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Payton Garland
 */
public class QtDescriptor extends TagDescriptor<QtDirectory> {

    public QtDescriptor(@NotNull QtDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case (QtDirectory.TAG_MAJOR_BRAND):
                return getMajorBrandDescription(tagType);
            case (QtDirectory.TAG_COMPATIBLE_BRANDS):
                return getCompatibleBrandsDescription(tagType);
            default:
                return super.getDescription(tagType);
        }
    }

    private String getMajorBrandDescription(int tagType)
    {
        String majorBrandKey = new String(_directory.getByteArray(tagType));
        String majorBrandValue = QtDictionary.lookup(QtDirectory.TAG_MAJOR_BRAND, majorBrandKey);
        if (majorBrandValue != null) {
            return majorBrandValue;
        } else {
            return majorBrandKey;
        }
    }

    private String getCompatibleBrandsDescription(int tagType)
    {
        String[] compatibleBrandKeys = _directory.getStringArray(tagType);
        ArrayList<String> compatibleBrandsValues = new ArrayList<String>();
        for (String compatibleBrandsKey : compatibleBrandKeys) {
            String compatibleBrandsValue = QtDictionary.lookup(QtDirectory.TAG_MAJOR_BRAND, compatibleBrandsKey);
            if (compatibleBrandsValue != null) {
                compatibleBrandsValues.add(compatibleBrandsValue);
            } else {
                compatibleBrandsValues.add(compatibleBrandsKey);
            }
        }
        return Arrays.toString(compatibleBrandsValues.toArray());
    }
}
