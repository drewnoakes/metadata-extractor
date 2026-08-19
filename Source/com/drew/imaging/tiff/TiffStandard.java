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
package com.drew.imaging.tiff;

/**
 * The TIFF format variant of a data stream, as identified by the two-byte marker
 * in the TIFF header.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public enum TiffStandard
{
    /**
     * Classic TIFF: 32-bit offsets, 12-byte IFD entries, marker value 42.
     */
    TIFF,

    /**
     * BigTIFF: 64-bit offsets, 20-byte IFD entries, marker value 43.
     * <p>
     * See <a href="https://www.awaresystems.be/imaging/tiff/bigtiff.html">the BigTIFF specification</a>.
     */
    BIG_TIFF
}
