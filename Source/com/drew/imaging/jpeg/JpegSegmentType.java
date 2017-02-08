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
package com.drew.imaging.jpeg;

import com.drew.lang.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An enumeration of the known segment types found in JPEG files.
 *
 * <ul>
 *     <li>http://www.ozhiker.com/electronics/pjmt/jpeg_info/app_segments.html</li>
 *     <li>http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/JPEG.html</li>
 * </ul>
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public enum JpegSegmentType
{
    /** APP0 JPEG segment identifier. Commonly contains JFIF, JFXX. */
    APP0((byte)0xE0, true),

    /** APP1 JPEG segment identifier. Commonly contains Exif. XMP data is also kept in here, though usually in a second instance. */
    APP1((byte)0xE1, true),

        /** APP2 JPEG segment identifier. Commonly contains ICC. */
    APP2((byte)0xE2, true),

    /** APP3 JPEG segment identifier. */
    APP3((byte)0xE3, true),

    /** APP4 JPEG segment identifier. */
    APP4((byte)0xE4, true),

    /** APP5 JPEG segment identifier. */
    APP5((byte)0xE5, true),

    /** APP6 JPEG segment identifier. */
    APP6((byte)0xE6, true),

    /** APP7 JPEG segment identifier. */
    APP7((byte)0xE7, true),

    /** APP8 JPEG segment identifier. */
    APP8((byte)0xE8, true),

    /** APP9 JPEG segment identifier. */
    APP9((byte)0xE9, true),

    /** APPA (App10) JPEG segment identifier. Can contain Unicode comments, though {@link JpegSegmentType#COM} is more commonly used for comments. */
    APPA((byte)0xEA, true),

    /** APPB (App11) JPEG segment identifier. */
    APPB((byte)0xEB, true),

    /** APPC (App12) JPEG segment identifier. */
    APPC((byte)0xEC, true),

    /** APPD (App13) JPEG segment identifier. Commonly contains IPTC, Photoshop data. */
    APPD((byte)0xED, true),

    /** APPE (App14) JPEG segment identifier. Commonly contains Adobe data. */
    APPE((byte)0xEE, true),

    /** APPF (App15) JPEG segment identifier. */
    APPF((byte)0xEF, true),

    /** Start Of Image segment identifier. */
    SOI((byte)0xD8, false),

    /** Define Quantization Table segment identifier. */
    DQT((byte)0xDB, false),

    /** Define Number of Lines segment identifier. */
    DNL((byte)0xDC, false),

    /** Define Restart Interval segment identifier. */
    DRI((byte)0xDD, false),

    /** Define Hierarchical Progression segment identifier. */
    DHP((byte)0xDE, false),

    /** EXPand reference component(s) segment identifier. */
    EXP((byte)0xDF, false),

    /** Define Huffman Table segment identifier. */
    DHT((byte)0xC4, false),

    /** Define Arithmetic Coding conditioning segment identifier. */
    DAC((byte)0xCC, false),

    /** Start-of-Frame (0) segment identifier for Baseline DCT. */
    SOF0((byte)0xC0, true),

    /** Start-of-Frame (1) segment identifier for Extended sequential DCT. */
    SOF1((byte)0xC1, true),

    /** Start-of-Frame (2) segment identifier for Progressive DCT. */
    SOF2((byte)0xC2, true),

    /** Start-of-Frame (3) segment identifier for Lossless (sequential). */
    SOF3((byte)0xC3, true),

//    /** Start-of-Frame (4) segment identifier. */
//    SOF4((byte)0xC4, true),

    /** Start-of-Frame (5) segment identifier for Differential sequential DCT. */
    SOF5((byte)0xC5, true),

    /** Start-of-Frame (6) segment identifier for Differential progressive DCT. */
    SOF6((byte)0xC6, true),

    /** Start-of-Frame (7) segment identifier for Differential lossless (sequential). */
    SOF7((byte)0xC7, true),

    /** Reserved for JPEG extensions. */
    JPG((byte)0xC8, true),

    /** Start-of-Frame (9) segment identifier for Extended sequential DCT. */
    SOF9((byte)0xC9, true),

    /** Start-of-Frame (10) segment identifier for Progressive DCT. */
    SOF10((byte)0xCA, true),

    /** Start-of-Frame (11) segment identifier for Lossless (sequential). */
    SOF11((byte)0xCB, true),

//    /** Start-of-Frame (12) segment identifier. */
//    SOF12((byte)0xCC, true),

    /** Start-of-Frame (13) segment identifier for Differential sequential DCT. */
    SOF13((byte)0xCD, true),

    /** Start-of-Frame (14) segment identifier for Differential progressive DCT. */
    SOF14((byte)0xCE, true),

    /** Start-of-Frame (15) segment identifier for Differential lossless (sequential). */
    SOF15((byte)0xCF, true),

    /** JPEG comment segment identifier for comments. */
    COM((byte)0xFE, true);

    public static final Collection<JpegSegmentType> canContainMetadataTypes;

    static {
        List<JpegSegmentType> segmentTypes = new ArrayList<JpegSegmentType>();
        for (JpegSegmentType segmentType : JpegSegmentType.class.getEnumConstants()) {
            if (segmentType.canContainMetadata) {
                segmentTypes.add(segmentType);
            }
        }
        canContainMetadataTypes = segmentTypes;
    }

    public final byte byteValue;
    public final boolean canContainMetadata;

    JpegSegmentType(byte byteValue, boolean canContainMetadata)
    {
        this.byteValue = byteValue;
        this.canContainMetadata = canContainMetadata;
    }

    @Nullable
    public static JpegSegmentType fromByte(byte segmentTypeByte)
    {
        for (JpegSegmentType segmentType : JpegSegmentType.class.getEnumConstants()) {
            if (segmentType.byteValue == segmentTypeByte)
                return segmentType;
        }
        return null;
    }
}
