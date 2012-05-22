/*
 * Copyright 2002-2012 Drew Noakes
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

package com.drew.metadata.photoshop;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Holds the metadata found in the APPD segment of a JPEG file saved by Photoshop.
 *
 * @author Yuri Binev, Drew Noakes http://drewnoakes.com
 */
public class PhotoshopDirectory extends Directory
{
    public static final int TAG_PHOTOSHOP_THUMBNAIL = 0x040C;
    public static final int TAG_PHOTOSHOP_THUMBNAIL_OLD = 0x0409;
    public static final int TAG_PHOTOSHOP_JPEG_QUALITY = 0x0406;
    public static final int TAG_PHOTOSHOP_URL = 0x040B;
    public static final int TAG_PHOTOSHOP_IPTC = 0x0404;
    public static final int TAG_PHOTOSHOP_CAPTION_DIGEST = 0x0425;
    public static final int TAG_PHOTOSHOP_SLICES = 0x041a;
    public static final int TAG_PHOTOSHOP_VERSION = 0x0421;
    public static final int TAG_PHOTOSHOP_RESOLUTION_INFO = 0x03ed;
    public static final int TAG_PHOTOSHOP_XML = 0x03EA;
    public static final int TAG_PHOTOSHOP_COPYRIGHT = 0x040A;
    public static final int TAG_PHOTOSHOP_GLOBAL_ANGLE = 0x040D;
    public static final int TAG_PHOTOSHOP_GLOBAL_ALTITUDE = 0x0419;
    public static final int TAG_PHOTOSHOP_URL_LIST = 0x041E;
    public static final int TAG_PHOTOSHOP_PRINT_SCALE = 0x0426;
    public static final int TAG_PHOTOSHOP_PRINT_FLAGS_INFO = 0x2710;
    public static final int TAG_PHOTOSHOP_PRINT_FLAGS = 0x03F3;
    public static final int TAG_PHOTOSHOP_SEED_NUMBER = 0x0414;
    public static final int TAG_PHOTOSHOP_PIXEL_ASPECT_RATIO = 0x0428;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        // TODO create fields for the numeric tags below

        _tagNameMap.put(TAG_PHOTOSHOP_THUMBNAIL, "Thumbnail Data");
        _tagNameMap.put(TAG_PHOTOSHOP_URL, "URL");
        _tagNameMap.put(TAG_PHOTOSHOP_IPTC, "IPTC-NAA record");
        _tagNameMap.put(TAG_PHOTOSHOP_CAPTION_DIGEST, "Caption digest");
        _tagNameMap.put(TAG_PHOTOSHOP_SLICES, "Slices");
        _tagNameMap.put(TAG_PHOTOSHOP_VERSION, "Version Info");
        _tagNameMap.put(0x03E9, "Mac Print info");
        _tagNameMap.put(TAG_PHOTOSHOP_XML, "XML Data");
        _tagNameMap.put(0x042F, "Print info");
        _tagNameMap.put(TAG_PHOTOSHOP_RESOLUTION_INFO, "Resolution Info");
        _tagNameMap.put(TAG_PHOTOSHOP_PRINT_FLAGS, "Print Flags");
        _tagNameMap.put(0x03E8, "Channels, rows, columns, depth, mode");
        _tagNameMap.put(0x03EB, "Indexed color table");
        _tagNameMap.put(0x03EE, "Alpha channels");
        _tagNameMap.put(0x03EF, "DisplayInfo");
        _tagNameMap.put(0x03F0, "Caption");
        _tagNameMap.put(0x03F1, "Border information");
        _tagNameMap.put(0x03F2, "Background color");
        _tagNameMap.put(0x03F3, "Print flags");
        _tagNameMap.put(0x03F4, "Grayscale and multichannel halftoning information");
        _tagNameMap.put(0x03F5, "Color halftoning information");
        _tagNameMap.put(0x03F6, "Duotone halftoning information");
        _tagNameMap.put(0x03F7, "Grayscale and multichannel transfer function");
        _tagNameMap.put(0x03F8, "Color transfer functions");
        _tagNameMap.put(0x03F9, "Duotone transfer functions");
        _tagNameMap.put(0x03FA, "Duotone image information");
        _tagNameMap.put(0x03FB, "Effective black and white values");
        _tagNameMap.put(0x03FD, "EPS options");
        _tagNameMap.put(0x03FE, "Quick Mask information");
        _tagNameMap.put(0x0400, "Layer state information");
        _tagNameMap.put(0x0402, "Layers group information");
        _tagNameMap.put(0x0405, "Image mode for raw format files");
        _tagNameMap.put(TAG_PHOTOSHOP_JPEG_QUALITY, "JPEG quality");
        _tagNameMap.put(0x0408, "Grid and guides information");
        _tagNameMap.put(0x0411, "ICC Untagged Profile");
        _tagNameMap.put(TAG_PHOTOSHOP_THUMBNAIL_OLD, "Photoshop 4.0 Thumbnail");
        _tagNameMap.put(TAG_PHOTOSHOP_COPYRIGHT, "Copyright flag");
        _tagNameMap.put(TAG_PHOTOSHOP_GLOBAL_ANGLE, "Global Angle");
        _tagNameMap.put(TAG_PHOTOSHOP_GLOBAL_ALTITUDE, "Global Altitude");
        _tagNameMap.put(TAG_PHOTOSHOP_PRINT_FLAGS_INFO, "Print flags information");
        _tagNameMap.put(TAG_PHOTOSHOP_PRINT_SCALE, "Print Scale");
        _tagNameMap.put(TAG_PHOTOSHOP_URL_LIST, "URL List");
        _tagNameMap.put(TAG_PHOTOSHOP_PIXEL_ASPECT_RATIO, "Pixel Aspect Ratio");
        _tagNameMap.put(TAG_PHOTOSHOP_SEED_NUMBER, "Seed number");
    }

    public PhotoshopDirectory()
    {
        this.setDescriptor(new PhotoshopDescriptor(this));
    }

    @NotNull
    public String getName()
    {
        return "Photoshop";
    }

    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    @Nullable
    public byte[] getThumbnailBytes()
    {
        byte[] storedBytes = getByteArray(PhotoshopDirectory.TAG_PHOTOSHOP_THUMBNAIL);
        if (storedBytes == null)
            storedBytes = getByteArray(PhotoshopDirectory.TAG_PHOTOSHOP_THUMBNAIL_OLD);
        if (storedBytes == null)
            return null;

        int thumbSize = storedBytes.length - 28;
        byte[] thumbBytes = new byte[thumbSize];
        System.arraycopy(storedBytes, 28, thumbBytes, 0, thumbSize);
        return thumbBytes;
    }
}
