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
    Unknown("Unknown", null),
    Jpeg("Jpeg", "image/jpeg"),
    Tiff("Tiff", "image/tiff"),
    Psd("Psd", "image/vnd.adobe.photoshop"),
    Png("Png", "image/png"),
    Bmp("Bmp", "image/bmp"),
    Gif("Gif", "image/gif"),
    Ico("Ico", "image/x-icon"),
    Pcx("Pcx", "image/x-pcx"),
    Riff("Riff", null),

    /** Sony camera raw. */
    Arw("Arw", null),
    /** Canon camera raw, version 1. */
    Crw("Crw", null),
    /** Canon camera raw, version 2. */
    Cr2("Cr2", null),
    /** Nikon camera raw. */
    Nef("Nef", null),
    /** Olympus camera raw. */
    Orf("Orf", null),
    /** FujiFilm camera raw. */
    Raf("Raf", null),
    /** Panasonic camera raw. */
    Rw2("Rw2", null);

    private final String _name;

    private final String _mimeType;

    FileType(String name, String mimeType)
    {
        _name = name;
        _mimeType = mimeType;
    }

    @NotNull
    public String getName()
    {
        return _name;
    }

    @Nullable
    public String getMimeType()
    {
        return _mimeType;
    }
}
