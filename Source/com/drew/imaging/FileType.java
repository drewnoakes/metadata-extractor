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
package com.drew.imaging;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

/**
 * Enumeration of supported image file formats.
 *
 * MIME Type Source: https://www.freeformatter.com/mime-types-list.html
 *                   https://www.iana.org/assignments/media-types/media-types.xhtml
 */
public enum FileType
{
    Unknown(null),
    Jpeg("image/jpeg", "jpg", "jpeg", "jpe"),
    Tiff("image/tiff", "tiff", "tif"),
    Psd("image/vnd.adobe.photoshop", "psd"),
    Png("image/png", "png"),
    Bmp("image/bmp", "bmp"),
    Gif("image/gif", "gif"),
    Ico("image/x-icon", "ico"),
    Pcx("image/x-pcx", "pcx"),
    Riff(null),

    /** Sony camera raw. */
    Arw(null, "arw"),
    /** Canon camera raw, version 1. */
    Crw(null, "crw"),
    /** Canon camera raw, version 2. */
    Cr2(null, "cr2"),
    /** Nikon camera raw. */
    Nef(null, "nef"),
    /** Olympus camera raw. */
    Orf(null, "orf"),
    /** FujiFilm camera raw. */
    Raf(null, "raf"),
    /** Panasonic camera raw. */
    Rw2(null, "rw2"),

    QuickTime("video/quicktime", "mov");

    @Nullable
    private final String _mimeType;

    private final String[] _extensions;

    FileType(@Nullable String mimeType, String... extensions)
    {
        _mimeType = mimeType;
        _extensions = extensions;
    }

    @NotNull
    public String getName()
    {
        return this.name();
    }

    @Nullable
    public String getMimeType()
    {
        return _mimeType;
    }

    @Nullable
    public String getCommonExtension()
    {
        return _extensions.length == 0 ? null : _extensions[0];
    }

    @Nullable
    public String[] getAllExtensions()
    {
        return _extensions;
    }
}
