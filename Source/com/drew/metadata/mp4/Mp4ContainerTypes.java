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
package com.drew.metadata.mp4;

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class Mp4ContainerTypes
{
    public static final String BOX_MOVIE                            = "moov";
    public static final String BOX_USER_DATA                        = "udta";
    public static final String BOX_TRACK                            = "trak";
    public static final String BOX_MEDIA                            = "mdia";
    public static final String BOX_MEDIA_INFORMATION                = "minf";
    public static final String BOX_SAMPLE_TABLE                     = "stbl";
    public static final String BOX_METADATA_LIST                    = "ilst";
    public static final String BOX_METADATA                         = "meta";
    public static final String BOX_COMPRESSED_MOVIE                 = "cmov";
    public static final String BOX_MEDIA_TEXT                       = "text";
    public static final String BOX_MEDIA_SUBTITLE                   = "sbtl";
    public static final String BOX_MEDIA_NULL                       = "nmhd";

    public static ArrayList<String> _containerList = new ArrayList<String>();

    static {
        _containerList.add(BOX_MOVIE);
        _containerList.add(BOX_USER_DATA);
        _containerList.add(BOX_TRACK);
        _containerList.add(BOX_MEDIA);
        _containerList.add(BOX_MEDIA_INFORMATION);
        _containerList.add(BOX_SAMPLE_TABLE);
        _containerList.add(BOX_METADATA);
        _containerList.add(BOX_METADATA_LIST);
        _containerList.add(BOX_COMPRESSED_MOVIE);
        _containerList.add(BOX_MEDIA_TEXT);
        _containerList.add(BOX_MEDIA_SUBTITLE);
        _containerList.add(BOX_MEDIA_NULL);
    }
}
