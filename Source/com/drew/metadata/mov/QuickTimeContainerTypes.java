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

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class QuickTimeContainerTypes
{
    public static final String ATOM_MOVIE                       = "moov";
    public static final String ATOM_USER_DATA                   = "udta";
    public static final String ATOM_TRACK                       = "trak";
    public static final String ATOM_MEDIA                       = "mdia";
    public static final String ATOM_MEDIA_INFORMATION           = "minf";
    public static final String ATOM_SAMPLE_TABLE                = "stbl";
    public static final String ATOM_METADATA_LIST               = "ilst";
    public static final String ATOM_METADATA                    = "meta";
    public static final String ATOM_COMPRESSED_MOVIE            = "cmov";
    public static final String ATOM_MEDIA_TEXT                  = "text";
    public static final String ATOM_MEDIA_SUBTITLE              = "sbtl";
    public static final String ATOM_MEDIA_BASE                  = "gmhd";

    public static ArrayList<String> _containerList = new ArrayList<String>();

    static {
        _containerList.add(ATOM_MOVIE);
        _containerList.add(ATOM_USER_DATA);
        _containerList.add(ATOM_TRACK);
        _containerList.add(ATOM_MEDIA);
        _containerList.add(ATOM_MEDIA_INFORMATION);
        _containerList.add(ATOM_SAMPLE_TABLE);
        _containerList.add(ATOM_METADATA);
        _containerList.add(ATOM_METADATA_LIST);
        _containerList.add(ATOM_COMPRESSED_MOVIE);
        _containerList.add(ATOM_MEDIA_TEXT);
        _containerList.add(ATOM_MEDIA_SUBTITLE);
        _containerList.add(ATOM_MEDIA_BASE);
    }
}
