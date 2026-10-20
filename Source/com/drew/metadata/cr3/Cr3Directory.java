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
package com.drew.metadata.cr3;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Holds CR3-container-level metadata: file type brand, Canon compressor version,
 * and the dimensions of the embedded thumbnail and preview images.
 * <p>
 * Camera data (EXIF, makernote, GPS) is stored in the standard directories
 * ({@code ExifIFD0Directory}, {@code ExifSubIFDDirectory}, {@code CanonMakernoteDirectory},
 * {@code GpsDirectory}) that are populated via the CMT1–CMT4 TIFF blobs inside the file.
 */
public class Cr3Directory extends Directory
{
    public static final int TAG_MAJOR_BRAND              = 1;
    public static final int TAG_MINOR_VERSION            = 2;
    public static final int TAG_COMPATIBLE_BRANDS        = 3;
    public static final int TAG_COMPRESSOR_VERSION       = 4;
    public static final int TAG_THUMBNAIL_WIDTH          = 5;
    public static final int TAG_THUMBNAIL_HEIGHT         = 6;
    public static final int TAG_PREVIEW_WIDTH            = 7;
    public static final int TAG_PREVIEW_HEIGHT           = 8;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_MAJOR_BRAND, "Major Brand");
        _tagNameMap.put(TAG_MINOR_VERSION, "Minor Version");
        _tagNameMap.put(TAG_COMPATIBLE_BRANDS, "Compatible Brands");
        _tagNameMap.put(TAG_COMPRESSOR_VERSION, "Compressor Version");
        _tagNameMap.put(TAG_THUMBNAIL_WIDTH, "Thumbnail Width");
        _tagNameMap.put(TAG_THUMBNAIL_HEIGHT, "Thumbnail Height");
        _tagNameMap.put(TAG_PREVIEW_WIDTH, "Preview Width");
        _tagNameMap.put(TAG_PREVIEW_HEIGHT, "Preview Height");
    }

    public Cr3Directory()
    {
        this.setDescriptor(new Cr3Descriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "CR3";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
