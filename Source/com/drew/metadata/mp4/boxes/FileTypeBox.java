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
package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.Mp4Directory;

import java.io.IOException;
import java.util.ArrayList;

/**
 * ISO/IED 14496-12:2015 pg.8
 */
public class FileTypeBox extends Box
{
    String majorBrand;
    long minorVersion;
    ArrayList<String> compatibleBrands;

    public FileTypeBox(SequentialReader reader, Box box) throws IOException
    {
        super(box);

        majorBrand = reader.getString(4);
        minorVersion = reader.getUInt32();
        compatibleBrands = new ArrayList<String>();
        for (int i = 16; i < size; i += 4) {
            compatibleBrands.add(reader.getString(4));
        }
    }

    public void addMetadata(Mp4Directory directory)
    {
        directory.setString(Mp4Directory.TAG_MAJOR_BRAND, majorBrand);
        directory.setLong(Mp4Directory.TAG_MINOR_VERSION, minorVersion);
        directory.setStringArray(Mp4Directory.TAG_COMPATIBLE_BRANDS, compatibleBrands.toArray(new String[compatibleBrands.size()]));
    }
}
