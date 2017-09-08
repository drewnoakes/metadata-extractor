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
public class QuickTimeAtomTypes
{
    public static final String ATOM_FILE_TYPE                = "ftyp";
    public static final String ATOM_MOVIE_HEADER             = "mvhd";
    public static final String ATOM_VIDEO_MEDIA_INFO         = "vmhd";
    public static final String ATOM_SOUND_MEDIA_INFO         = "smhd";
    public static final String ATOM_BASE_MEDIA_INFO          = "gmhd";
    public static final String ATOM_TIMECODE_MEDIA_INFO      = "tcmi";
    public static final String ATOM_HANDLER                  = "hdlr";
    public static final String ATOM_KEYS                     = "keys";
    public static final String ATOM_DATA                     = "data";
    public static final String ATOM_SAMPLE_DESCRIPTION       = "stsd";
    public static final String ATOM_TIME_TO_SAMPLE           = "stts";
    public static final String ATOM_MEDIA_HEADER             = "mdhd";

    public static ArrayList<String> _atomList = new ArrayList<String>();

    static {
        _atomList.add(ATOM_FILE_TYPE);
        _atomList.add(ATOM_MOVIE_HEADER);
        _atomList.add(ATOM_VIDEO_MEDIA_INFO);
        _atomList.add(ATOM_SOUND_MEDIA_INFO);
        _atomList.add(ATOM_BASE_MEDIA_INFO);
        _atomList.add(ATOM_TIMECODE_MEDIA_INFO);
        _atomList.add(ATOM_HANDLER);
        _atomList.add(ATOM_KEYS);
        _atomList.add(ATOM_DATA);
        _atomList.add(ATOM_SAMPLE_DESCRIPTION);
        _atomList.add(ATOM_TIME_TO_SAMPLE);
        _atomList.add(ATOM_MEDIA_HEADER);
    }
}
