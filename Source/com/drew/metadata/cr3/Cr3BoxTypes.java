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
package com.drew.metadata.cr3;

/**
 * FourCC box type constants for Canon CR3 files.
 * <p>
 * See https://github.com/lclevy/canon_cr3 for the CR3 format specification.
 */
public final class Cr3BoxTypes
{
    /** File type box (standard ISOBMFF). */
    public static final String BOX_FILE_TYPE                = "ftyp";

    /** Canon Compressor Version — 30-byte ASCII string identifying the encoder version. */
    public static final String BOX_CANON_COMPRESSOR_VERSION = "CNCV";

    /** Canon Compressor Table Pointers — track layout metadata. Not decoded; skipped by the walker. */
    public static final String BOX_CANON_COMPRESSOR_TABLE   = "CCTP";

    /** Canon Compressor Data — per-track records within CCTP. Not decoded; skipped by the walker. */
    public static final String BOX_CANON_COMPRESSOR_DATA    = "CCDT";

    /** Canon Track Base Offsets — file offsets for each track. Not decoded; skipped by the walker. */
    public static final String BOX_CANON_TRACK_BASE_OFFSETS = "CTBO";

    /** Canon Metadata in TIFF format — IFD0 (Make, Model, DateTime, …). */
    public static final String BOX_CANON_TIFF_IFD0          = "CMT1";

    /** Canon Metadata in TIFF format — Exif sub-IFD (exposure, focal length, …). */
    public static final String BOX_CANON_TIFF_EXIF          = "CMT2";

    /** Canon Metadata in TIFF format — Canon makernote IFD. */
    public static final String BOX_CANON_TIFF_MAKERNOTE     = "CMT3";

    /** Canon Metadata in TIFF format — GPS IFD. */
    public static final String BOX_CANON_TIFF_GPS           = "CMT4";

    /** Thumbnail box — 160×120 JPEG inside the Canon metadata UUID. */
    public static final String BOX_THUMBNAIL                = "THMB";

    private Cr3BoxTypes() {}
}
