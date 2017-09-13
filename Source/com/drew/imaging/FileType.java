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
 * Enumeration of supported file types.
 *
 * MIME Type Source: https://www.freeformatter.com/mime-types-list.html
 *                   https://www.iana.org/assignments/media-types/media-types.xhtml
 */
public enum FileType
{
    Unknown("Unknown", "Unknown", null),
    Jpeg("JPEG", "Joint Photographic Experts Group", "image/jpeg", "jpg", "jpeg", "jpe"),
    Tiff("TIFF", "Tagged Image File Format", "image/tiff", "tiff", "tif"),
    Psd("PSD", "Photoshop Document", "image/vnd.adobe.photoshop", "psd"),
    Png("PNG", "Portable Network Graphics", "image/png", "png"),
    Bmp("BMP", "Device Independent Bitmap", "image/bmp", "bmp"),
    Gif("GIF", "Graphics Interchange Format", "image/gif", "gif"),
    Ico("ICO", "Windows Icon", "image/x-icon", "ico"),
    Pcx("PCX", "PiCture eXchange", "image/x-pcx", "pcx"),
    Riff("RIFF", "Resource Interchange File Format", null),
    Wav("WAV", "Waveform Audio File Format", "audio/vnd.wave", "wav", "wave"),
    Avi("AVI", "Audio Video Interleaved", "video/vnd.avi", "avi"),
    WebP("WebP", "WebP", "image/webp", "webp"),
    Mov("MOV", "QuickTime Movie", "video/quicktime", "mov", "qt"),
    Mp4("MP4", "MPEG-4 Part 14", "video/mp4", "mp4", "m4a", "m4p", "m4b", "m4r", "m4v"),
    Heif("HEIF", "High Efficiency Image File Format", "image/heif", "heif", "heic"),
    Eps("EPS", "Encapsulated PostScript", "application/postscript", "eps", "epsf", "epsi"),

    /** Sony camera raw. */
    Arw("ARW", "Sony Camera Raw", null, "arw"),
    /** Canon camera raw, version 1. */
    Crw("CRW", "Canon Camera Raw", null, "crw"),
    /** Canon camera raw, version 2. */
    Cr2("CR2", "Canon Camera Raw", null, "cr2"),
    /** Nikon camera raw. */
    Nef("NEF", "Nikon Camera Raw", null, "nef"),
    /** Olympus camera raw. */
    Orf("ORF", "Olympus Camera Raw", null, "orf"),
    /** FujiFilm camera raw. */
    Raf("RAF", "FujiFilm Camera Raw", null, "raf"),
    /** Panasonic camera raw. */
    Rw2("RW2", "Panasonic Camera Raw", null, "rw2"),

    // Only file detection
    Aac("AAC", "Advanced Audio Coding", "audio/aac", "m4a"),
    Asf("ASF", "Advanced Systems Format", "video/x-ms-asf", "asf", "wma", "wmv"),
    Cfbf("CFBF", "Compound File Binary Format", null, null),
    Flv("FLV", "Flash Video", "video/x-flv", ".flv", ".f4v,"),
    Indd("INDD", "INDesign Document", "application/octet-stream", ".indd"),
    Mxf("MXF", "Material Exchange Format", "application/mxf", "mxf"),
    Pdf("PDF", "Portable Document Format", "application/pdf", "pdf"),
    Qxp("QXP", "Quark XPress Document", null, "qzp", "qxd"),
    Ram("RAM", "RealAudio", "audio/vnd.rn-realaudio", "aac", "ra"),
    Rtf("RTF", "Rich Text Format", "application/rtf", "rtf"),
    Sit("SIT", "Stuffit Archive", "application/x-stuffit", "sit"),
    Sitx("SITX", "Stuffit X Archive", "application/x-stuffitx", "sitx"),
    Swf("SWF", "Small Web Format", "application/vnd.adobe.flash-movie", "swf"),
    Vob("VOB", "Video Object", "video/dvd", ".vob"),
    Zip("ZIP", "ZIP Archive", "application/zip", ".zip", ".zipx");

    @NotNull private final String _name;
    @NotNull private final String _longName;
    @Nullable private final String _mimeType;
    private final String[] _extensions;

    FileType(@NotNull String name, @NotNull String longName, @Nullable String mimeType, String... extensions)
    {
        _name = name;
        _longName = longName;
        _mimeType = mimeType;
        _extensions = extensions;
    }

    @NotNull
    public String getName()
    {
        return _name;
    }

    @NotNull
    public String getLongName()
    {
        return _longName;
    }

    @Nullable
    public String getMimeType()
    {
        return _mimeType;
    }

    @Nullable
    public String getCommonExtension()
    {
        return (_extensions == null || _extensions.length == 0) ? null : _extensions[0];
    }

    @Nullable
    public String[] getAllExtensions()
    {
        return _extensions;
    }
}
