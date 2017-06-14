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
    Unknown(null, false),
    Jpeg("image/jpeg", false, ".jpg", ".jpeg", ".jpe"),
    Tiff("image/tiff", true, ".tiff", ".tif"),
    Psd("image/vnd.adobe.photoshop", false, ".psd"),
    Png("image/png", false, ".png"),
    Bmp("image/bmp", false, ".bmp"),
    Gif("image/gif", false, ".gif"),
    Ico("image/x-icon", false, ".ico"),
    Pcx("image/x-pcx", false, ".pcx"),
    Riff(null, true, null),
    Mov(null, true, ".mov"),

    /** Sony camera raw. */
    Arw(null, false, ".arw"),
    /** Canon camera raw, version 1. */
    Crw(null, false, ".crw"),
    /** Canon camera raw, version 2. */
    Cr2(null, false, ".cr2"),
    /** Nikon camera raw. */
    Nef(null, false, ".nef"),
    /** Olympus camera raw. */
    Orf(null, false, ".orf"),
    /** FujiFilm camera raw. */
    Raf(null, false, ".raf"),
    /** Panasonic camera raw. */
    Rw2(null, false, ".rw2");

    private final String _mimeType;

    private final boolean _isContainer;

    private final String[] _extensions;

    FileType(String mimeType, boolean isContainer, String... extensions)
    {
        _mimeType = mimeType;
        _isContainer = isContainer;
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

    @NotNull
    public boolean getIsContainer()
    {
        return _isContainer;
    }

    @Nullable
    public String[] getExtension()
    {
        return _extensions;
    }
}
