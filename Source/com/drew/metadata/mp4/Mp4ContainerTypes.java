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
package com.drew.metadata.mp4;

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
}
