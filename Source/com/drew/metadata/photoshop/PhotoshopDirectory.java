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

package com.drew.metadata.photoshop;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Holds the metadata found in the APPD segment of a JPEG file saved by Photoshop.
 *
 * @author Drew Noakes https://drewnoakes.com
 * @author Yuri Binev
 * @author Payton Garland
 */
@SuppressWarnings("WeakerAccess")
public class PhotoshopDirectory extends Directory
{
    public static final int TAG_CHANNELS_ROWS_COLUMNS_DEPTH_MODE                  = 0x03E8;
    public static final int TAG_MAC_PRINT_INFO                                    = 0x03E9;
    public static final int TAG_XML                                               = 0x03EA;
    public static final int TAG_INDEXED_COLOR_TABLE                               = 0x03EB;
    public static final int TAG_RESOLUTION_INFO                                   = 0x03ED;
    public static final int TAG_ALPHA_CHANNELS                                    = 0x03EE;
    public static final int TAG_DISPLAY_INFO_OBSOLETE                             = 0x03EF;
    public static final int TAG_CAPTION                                           = 0x03F0;
    public static final int TAG_BORDER_INFORMATION                                = 0x03F1;
    public static final int TAG_BACKGROUND_COLOR                                  = 0x03F2;
    public static final int TAG_PRINT_FLAGS                                       = 0x03F3;
    public static final int TAG_GRAYSCALE_AND_MULTICHANNEL_HALFTONING_INFORMATION = 0x03F4;
    public static final int TAG_COLOR_HALFTONING_INFORMATION                      = 0x03F5;
    public static final int TAG_DUOTONE_HALFTONING_INFORMATION                    = 0x03F6;
    public static final int TAG_GRAYSCALE_AND_MULTICHANNEL_TRANSFER_FUNCTION      = 0x03F7;
    public static final int TAG_COLOR_TRANSFER_FUNCTIONS                          = 0x03F8;
    public static final int TAG_DUOTONE_TRANSFER_FUNCTIONS                        = 0x03F9;
    public static final int TAG_DUOTONE_IMAGE_INFORMATION                         = 0x03FA;
    public static final int TAG_EFFECTIVE_BLACK_AND_WHITE_VALUES                  = 0x03FB;
    // OBSOLETE                                                                     0x03FC
    public static final int TAG_EPS_OPTIONS                                       = 0x03FD;
    public static final int TAG_QUICK_MASK_INFORMATION                            = 0x03FE;
    // OBSOLETE                                                                     0x03FF
    public static final int TAG_LAYER_STATE_INFORMATION                           = 0x0400;
    // Working path (not saved)                                                     0x0401
    public static final int TAG_LAYERS_GROUP_INFORMATION                          = 0x0402;
    // OBSOLETE                                                                     0x0403
    public static final int TAG_IPTC                                              = 0x0404;
    public static final int TAG_IMAGE_MODE_FOR_RAW_FORMAT_FILES                   = 0x0405;
    public static final int TAG_JPEG_QUALITY                                      = 0x0406;
    public static final int TAG_GRID_AND_GUIDES_INFORMATION                       = 0x0408;
    public static final int TAG_THUMBNAIL_OLD                                     = 0x0409;
    public static final int TAG_COPYRIGHT                                         = 0x040A;
    public static final int TAG_URL                                               = 0x040B;
    public static final int TAG_THUMBNAIL                                         = 0x040C;
    public static final int TAG_GLOBAL_ANGLE                                      = 0x040D;
    // OBSOLETE                                                                     0x040E
    public static final int TAG_ICC_PROFILE_BYTES                                 = 0x040F;
    public static final int TAG_WATERMARK                                         = 0x0410;
    public static final int TAG_ICC_UNTAGGED_PROFILE                              = 0x0411;
    public static final int TAG_EFFECTS_VISIBLE                                   = 0x0412;
    public static final int TAG_SPOT_HALFTONE                                     = 0x0413;
    public static final int TAG_SEED_NUMBER                                       = 0x0414;
    public static final int TAG_UNICODE_ALPHA_NAMES                               = 0x0415;
    public static final int TAG_INDEXED_COLOR_TABLE_COUNT                         = 0x0416;
    public static final int TAG_TRANSPARENCY_INDEX                                = 0x0417;
    public static final int TAG_GLOBAL_ALTITUDE                                   = 0x0419;
    public static final int TAG_SLICES                                            = 0x041A;
    public static final int TAG_WORKFLOW_URL                                      = 0x041B;
    public static final int TAG_JUMP_TO_XPEP                                      = 0x041C;
    public static final int TAG_ALPHA_IDENTIFIERS                                 = 0x041D;
    public static final int TAG_URL_LIST                                          = 0x041E;
    public static final int TAG_VERSION                                           = 0x0421;
    public static final int TAG_EXIF_DATA_1                                       = 0x0422;
    public static final int TAG_EXIF_DATA_3                                       = 0x0423;
    public static final int TAG_XMP_DATA                                          = 0x0424;
    public static final int TAG_CAPTION_DIGEST                                    = 0x0425;
    public static final int TAG_PRINT_SCALE                                       = 0x0426;
    public static final int TAG_PIXEL_ASPECT_RATIO                                = 0x0428;
    public static final int TAG_LAYER_COMPS                                       = 0x0429;
    public static final int TAG_ALTERNATE_DUOTONE_COLORS                          = 0x042A;
    public static final int TAG_ALTERNATE_SPOT_COLORS                             = 0x042B;
    public static final int TAG_LAYER_SELECTION_IDS                               = 0x042D;
    public static final int TAG_HDR_TONING_INFO                                   = 0x042E;
    public static final int TAG_PRINT_INFO                                        = 0x042F;
    public static final int TAG_LAYER_GROUPS_ENABLED_ID                           = 0x0430;
    public static final int TAG_COLOR_SAMPLERS                                    = 0x0431;
    public static final int TAG_MEASUREMENT_SCALE                                 = 0x0432;
    public static final int TAG_TIMELINE_INFORMATION                              = 0x0433;
    public static final int TAG_SHEET_DISCLOSURE                                  = 0x0434;
    public static final int TAG_DISPLAY_INFO                                      = 0x0435;
    public static final int TAG_ONION_SKINS                                       = 0x0436;
    public static final int TAG_COUNT_INFORMATION                                 = 0x0438;
    public static final int TAG_PRINT_INFO_2                                      = 0x043A;
    public static final int TAG_PRINT_STYLE                                       = 0x043B;
    public static final int TAG_MAC_NSPRINTINFO                                   = 0x043C;
    public static final int TAG_WIN_DEVMODE                                       = 0x043D;
    public static final int TAG_AUTO_SAVE_FILE_PATH                               = 0x043E;
    public static final int TAG_AUTO_SAVE_FORMAT                                  = 0x043F;
    public static final int TAG_PATH_SELECTION_STATE                              = 0x0440;
    // PATH INFO                                                                    0x07D0 -> 0x0BB6
    public static final int TAG_CLIPPING_PATH_NAME                                = 0x0BB7;
    public static final int TAG_ORIGIN_PATH_INFO                                  = 0x0BB8;
    // PLUG IN RESOURCES                                                            0x0FA0 -> 0x1387
    public static final int TAG_IMAGE_READY_VARIABLES_XML                         = 0x1B58;
    public static final int TAG_IMAGE_READY_DATA_SETS                             = 0x1B59;
    public static final int TAG_IMAGE_READY_SELECTED_STATE                        = 0x1B5A;
    public static final int TAG_IMAGE_READY_7_ROLLOVER                            = 0x1B5B;
    public static final int TAG_IMAGE_READY_ROLLOVER                              = 0x1B5C;
    public static final int TAG_IMAGE_READY_SAVE_LAYER_SETTINGS                   = 0x1B5D;
    public static final int TAG_IMAGE_READY_VERSION                               = 0x1B5E;
    public static final int TAG_LIGHTROOM_WORKFLOW                                = 0x1F40;
    public static final int TAG_PRINT_FLAGS_INFO                                  = 0x2710;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_CHANNELS_ROWS_COLUMNS_DEPTH_MODE, "Channels, Rows, Columns, Depth, Mode");
        _tagNameMap.put(TAG_MAC_PRINT_INFO, "Mac Print Info");
        _tagNameMap.put(TAG_XML, "XML Data");
        _tagNameMap.put(TAG_INDEXED_COLOR_TABLE, "Indexed Color Table");
        _tagNameMap.put(TAG_RESOLUTION_INFO, "Resolution Info");
        _tagNameMap.put(TAG_ALPHA_CHANNELS, "Alpha Channels");
        _tagNameMap.put(TAG_DISPLAY_INFO_OBSOLETE, "Display Info (Obsolete)");
        _tagNameMap.put(TAG_CAPTION, "Caption");
        _tagNameMap.put(TAG_BORDER_INFORMATION, "Border Information");
        _tagNameMap.put(TAG_BACKGROUND_COLOR, "Background Color");
        _tagNameMap.put(TAG_PRINT_FLAGS, "Print Flags");
        _tagNameMap.put(TAG_GRAYSCALE_AND_MULTICHANNEL_HALFTONING_INFORMATION, "Grayscale and Multichannel Halftoning Information");
        _tagNameMap.put(TAG_COLOR_HALFTONING_INFORMATION, "Color Halftoning Information");
        _tagNameMap.put(TAG_DUOTONE_HALFTONING_INFORMATION, "Duotone Halftoning Information");
        _tagNameMap.put(TAG_GRAYSCALE_AND_MULTICHANNEL_TRANSFER_FUNCTION, "Grayscale and Multichannel Transfer Function");
        _tagNameMap.put(TAG_COLOR_TRANSFER_FUNCTIONS, "Color Transfer Functions");
        _tagNameMap.put(TAG_DUOTONE_TRANSFER_FUNCTIONS, "Duotone Transfer Functions");
        _tagNameMap.put(TAG_DUOTONE_IMAGE_INFORMATION, "Duotone Image Information");
        _tagNameMap.put(TAG_EFFECTIVE_BLACK_AND_WHITE_VALUES, "Effective Black and White Values");
        _tagNameMap.put(TAG_EPS_OPTIONS, "EPS Options");
        _tagNameMap.put(TAG_QUICK_MASK_INFORMATION, "Quick Mask Information");
        _tagNameMap.put(TAG_LAYER_STATE_INFORMATION, "Layer State Information");
        _tagNameMap.put(TAG_LAYERS_GROUP_INFORMATION, "Layers Group Information");
        _tagNameMap.put(TAG_IPTC, "IPTC-NAA Record");
        _tagNameMap.put(TAG_IMAGE_MODE_FOR_RAW_FORMAT_FILES, "Image Mode for Raw Format Files");
        _tagNameMap.put(TAG_JPEG_QUALITY, "JPEG Quality");
        _tagNameMap.put(TAG_GRID_AND_GUIDES_INFORMATION, "Grid and Guides Information");
        _tagNameMap.put(TAG_THUMBNAIL_OLD, "Photoshop 4.0 Thumbnail");
        _tagNameMap.put(TAG_COPYRIGHT, "Copyright Flag");
        _tagNameMap.put(TAG_URL, "URL");
        _tagNameMap.put(TAG_THUMBNAIL, "Thumbnail Data");
        _tagNameMap.put(TAG_GLOBAL_ANGLE, "Global Angle");
        _tagNameMap.put(TAG_ICC_PROFILE_BYTES, "ICC Profile Bytes");
        _tagNameMap.put(TAG_WATERMARK, "Watermark");
        _tagNameMap.put(TAG_ICC_UNTAGGED_PROFILE, "ICC Untagged Profile");
        _tagNameMap.put(TAG_EFFECTS_VISIBLE, "Effects Visible");
        _tagNameMap.put(TAG_SPOT_HALFTONE, "Spot Halftone");
        _tagNameMap.put(TAG_SEED_NUMBER, "Seed Number");
        _tagNameMap.put(TAG_UNICODE_ALPHA_NAMES, "Unicode Alpha Names");
        _tagNameMap.put(TAG_INDEXED_COLOR_TABLE_COUNT, "Indexed Color Table Count");
        _tagNameMap.put(TAG_TRANSPARENCY_INDEX, "Transparency Index");
        _tagNameMap.put(TAG_GLOBAL_ALTITUDE, "Global Altitude");
        _tagNameMap.put(TAG_SLICES, "Slices");
        _tagNameMap.put(TAG_WORKFLOW_URL, "Workflow URL");
        _tagNameMap.put(TAG_JUMP_TO_XPEP, "Jump To XPEP");
        _tagNameMap.put(TAG_ALPHA_IDENTIFIERS, "Alpha Identifiers");
        _tagNameMap.put(TAG_URL_LIST, "URL List");
        _tagNameMap.put(TAG_VERSION, "Version Info");
        _tagNameMap.put(TAG_EXIF_DATA_1, "EXIF Data 1");
        _tagNameMap.put(TAG_EXIF_DATA_3, "EXIF Data 3");
        _tagNameMap.put(TAG_XMP_DATA, "XMP Data");
        _tagNameMap.put(TAG_CAPTION_DIGEST, "Caption Digest");
        _tagNameMap.put(TAG_PRINT_SCALE, "Print Scale");
        _tagNameMap.put(TAG_PIXEL_ASPECT_RATIO, "Pixel Aspect Ratio");
        _tagNameMap.put(TAG_LAYER_COMPS, "Layer Comps");
        _tagNameMap.put(TAG_ALTERNATE_DUOTONE_COLORS, "Alternate Duotone Colors");
        _tagNameMap.put(TAG_ALTERNATE_SPOT_COLORS, "Alternate Spot Colors");
        _tagNameMap.put(TAG_LAYER_SELECTION_IDS, "Layer Selection IDs");
        _tagNameMap.put(TAG_HDR_TONING_INFO, "HDR Toning Info");
        _tagNameMap.put(TAG_PRINT_INFO, "Print Info");
        _tagNameMap.put(TAG_LAYER_GROUPS_ENABLED_ID, "Layer Groups Enabled ID");
        _tagNameMap.put(TAG_COLOR_SAMPLERS, "Color Samplers");
        _tagNameMap.put(TAG_MEASUREMENT_SCALE, "Measurement Scale");
        _tagNameMap.put(TAG_TIMELINE_INFORMATION, "Timeline Information");
        _tagNameMap.put(TAG_SHEET_DISCLOSURE, "Sheet Disclosure");
        _tagNameMap.put(TAG_DISPLAY_INFO, "Display Info");
        _tagNameMap.put(TAG_ONION_SKINS, "Onion Skins");
        _tagNameMap.put(TAG_COUNT_INFORMATION, "Count information");
        _tagNameMap.put(TAG_PRINT_INFO_2, "Print Info 2");
        _tagNameMap.put(TAG_PRINT_STYLE, "Print Style");
        _tagNameMap.put(TAG_MAC_NSPRINTINFO, "Mac NSPrintInfo");
        _tagNameMap.put(TAG_WIN_DEVMODE, "Win DEVMODE");
        _tagNameMap.put(TAG_AUTO_SAVE_FILE_PATH, "Auto Save File Subpath");
        _tagNameMap.put(TAG_AUTO_SAVE_FORMAT, "Auto Save Format");
        _tagNameMap.put(TAG_PATH_SELECTION_STATE, "Subpath Selection State");

        _tagNameMap.put(TAG_CLIPPING_PATH_NAME, "Clipping Path Name");
        _tagNameMap.put(TAG_ORIGIN_PATH_INFO, "Origin Subpath Info");
        _tagNameMap.put(TAG_IMAGE_READY_VARIABLES_XML, "Image Ready Variables XML");
        _tagNameMap.put(TAG_IMAGE_READY_DATA_SETS, "Image Ready Data Sets");
        _tagNameMap.put(TAG_IMAGE_READY_SELECTED_STATE, "Image Ready Selected State");
        _tagNameMap.put(TAG_IMAGE_READY_7_ROLLOVER, "Image Ready 7 Rollover Expanded State");
        _tagNameMap.put(TAG_IMAGE_READY_ROLLOVER, "Image Ready Rollover Expanded State");
        _tagNameMap.put(TAG_IMAGE_READY_SAVE_LAYER_SETTINGS, "Image Ready Save Layer Settings");
        _tagNameMap.put(TAG_IMAGE_READY_VERSION, "Image Ready Version");
        _tagNameMap.put(TAG_LIGHTROOM_WORKFLOW, "Lightroom Workflow");
        _tagNameMap.put(TAG_PRINT_FLAGS_INFO, "Print Flags Information");
    }

    public PhotoshopDirectory()
    {
        this.setDescriptor(new PhotoshopDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Photoshop";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    @Nullable
    public byte[] getThumbnailBytes()
    {
        byte[] storedBytes = getByteArray(PhotoshopDirectory.TAG_THUMBNAIL);
        if (storedBytes == null)
            storedBytes = getByteArray(PhotoshopDirectory.TAG_THUMBNAIL_OLD);
        if (storedBytes == null || storedBytes.length <= 28)
            return null;

        int thumbSize = storedBytes.length - 28;
        byte[] thumbBytes = new byte[thumbSize];
        System.arraycopy(storedBytes, 28, thumbBytes, 0, thumbSize);
        return thumbBytes;
    }
}
