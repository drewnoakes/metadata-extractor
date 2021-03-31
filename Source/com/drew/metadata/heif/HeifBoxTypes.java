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

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class HeifBoxTypes
{
    public static final String BOX_FILE_TYPE                        = "ftyp";
    public static final String BOX_PRIMARY_ITEM                     = "pitm";
    public static final String BOX_ITEM_PROTECTION                  = "ipro";
    public static final String BOX_ITEM_INFO                        = "iinf";
    public static final String BOX_ITEM_LOCATION                    = "iloc";
    public static final String BOX_HANDLER                          = "hdlr";
    public static final String BOX_HVC1                             = "hvc1";
    public static final String BOX_IMAGE_SPATIAL_EXTENTS            = "ispe";
    public static final String BOX_AUXILIARY_TYPE_PROPERTY          = "auxC";
    public static final String BOX_IMAGE_ROTATION                   = "irot";
    public static final String BOX_COLOUR_INFO                      = "colr";
    public static final String BOX_PIXEL_INFORMATION                = "pixi";

    private static final ArrayList<String> _boxList = new ArrayList<String>();

    static {
        _boxList.add(BOX_FILE_TYPE);
        _boxList.add(BOX_ITEM_PROTECTION);
        _boxList.add(BOX_PRIMARY_ITEM);
        _boxList.add(BOX_ITEM_INFO);
        _boxList.add(BOX_ITEM_LOCATION);
        _boxList.add(BOX_HANDLER);
        _boxList.add(BOX_HVC1);
        _boxList.add(BOX_IMAGE_SPATIAL_EXTENTS);
        _boxList.add(BOX_AUXILIARY_TYPE_PROPERTY);
        _boxList.add(BOX_IMAGE_ROTATION);
        _boxList.add(BOX_COLOUR_INFO);
        _boxList.add(BOX_PIXEL_INFORMATION);
    }
}
