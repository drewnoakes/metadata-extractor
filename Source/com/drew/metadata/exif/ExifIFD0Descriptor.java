/*
 * Copyright 2002-2014 Drew Noakes
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

package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.io.UnsupportedEncodingException;

import static com.drew.metadata.exif.ExifIFD0Directory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link ExifIFD0Directory}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class ExifIFD0Descriptor extends TagDescriptor<ExifIFD0Directory>
{
    /**
     * Dictates whether rational values will be represented in decimal format in instances
     * where decimal notation is elegant (such as 1/2 -> 0.5, but not 1/3).
     */
    private final boolean _allowDecimalRepresentationOfRationals = true;

    public ExifIFD0Descriptor(@NotNull ExifIFD0Directory directory)
    {
        super(directory);
    }

    // Note for the potential addition of brightness presentation in eV:
    // Brightness of taken subject. To calculate Exposure(Ev) from BrightnessValue(Bv),
    // you must add SensitivityValue(Sv).
    // Ev=BV+Sv   Sv=log2(ISOSpeedRating/3.125)
    // ISO100:Sv=5, ISO200:Sv=6, ISO400:Sv=7, ISO125:Sv=5.32.

    /**
     * Returns a descriptive value of the specified tag for this image.
     * Where possible, known values will be substituted here in place of the raw
     * tokens actually kept in the Exif segment.  If no substitution is
     * available, the value provided by getString(int) will be returned.
     * @param tagType the tag to find a description for
     * @return a description of the image's value for the specified tag, or
     *         <code>null</code> if the tag hasn't been defined.
     */
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_RESOLUTION_UNIT:
                return getResolutionDescription();
            case TAG_YCBCR_POSITIONING:
                return getYCbCrPositioningDescription();
            case TAG_X_RESOLUTION:
                return getXResolutionDescription();
            case TAG_Y_RESOLUTION:
                return getYResolutionDescription();
            case TAG_REFERENCE_BLACK_WHITE:
                return getReferenceBlackWhiteDescription();
            case TAG_ORIENTATION:
                return getOrientationDescription();

            case TAG_WIN_AUTHOR:
               return getWindowsAuthorDescription();
            case TAG_WIN_COMMENT:
               return getWindowsCommentDescription();
            case TAG_WIN_KEYWORDS:
               return getWindowsKeywordsDescription();
            case TAG_WIN_SUBJECT:
               return getWindowsSubjectDescription();
            case TAG_WIN_TITLE:
               return getWindowsTitleDescription();

            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getReferenceBlackWhiteDescription()
    {
        int[] ints = _directory.getIntArray(TAG_REFERENCE_BLACK_WHITE);
        if (ints==null || ints.length < 6)
            return null;
        int blackR = ints[0];
        int whiteR = ints[1];
        int blackG = ints[2];
        int whiteG = ints[3];
        int blackB = ints[4];
        int whiteB = ints[5];
        return String.format("[%d,%d,%d] [%d,%d,%d]", blackR, blackG, blackB, whiteR, whiteG, whiteB);
    }

    @Nullable
    public String getYResolutionDescription()
    {
        Rational value = _directory.getRational(TAG_Y_RESOLUTION);
        if (value==null)
            return null;
        final String unit = getResolutionDescription();
        return String.format("%s dots per %s",
            value.toSimpleString(_allowDecimalRepresentationOfRationals),
            unit == null ? "unit" : unit.toLowerCase());
    }

    @Nullable
    public String getXResolutionDescription()
    {
        Rational value = _directory.getRational(TAG_X_RESOLUTION);
        if (value==null)
            return null;
        final String unit = getResolutionDescription();
        return String.format("%s dots per %s",
            value.toSimpleString(_allowDecimalRepresentationOfRationals),
            unit == null ? "unit" : unit.toLowerCase());
    }

    @Nullable
    public String getYCbCrPositioningDescription()
    {
        return getIndexedDescription(TAG_YCBCR_POSITIONING, 1, "Center of pixel array", "Datum point");
    }

    @Nullable
    public String getOrientationDescription()
    {
        return getIndexedDescription(TAG_ORIENTATION, 1,
            "Top, left side (Horizontal / normal)",
            "Top, right side (Mirror horizontal)",
            "Bottom, right side (Rotate 180)",
            "Bottom, left side (Mirror vertical)",
            "Left side, top (Mirror horizontal and rotate 270 CW)",
            "Right side, top (Rotate 90 CW)",
            "Right side, bottom (Mirror horizontal and rotate 90 CW)",
            "Left side, bottom (Rotate 270 CW)");
    }

    @Nullable
    public String getResolutionDescription()
    {
        // '1' means no-unit, '2' means inch, '3' means centimeter. Default value is '2'(inch)
        return getIndexedDescription(TAG_RESOLUTION_UNIT, 1, "(No unit)", "Inch", "cm");
    }

    /** The Windows specific tags uses plain Unicode. */
    @Nullable
    private String getUnicodeDescription(int tag)
    {
        byte[] bytes = _directory.getByteArray(tag);
        if (bytes == null)
            return null;
        try {
            // Decode the unicode string and trim the unicode zero "\0" from the end.
            return new String(bytes, "UTF-16LE").trim();
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    @Nullable
    public String getWindowsAuthorDescription()
    {
       return getUnicodeDescription(TAG_WIN_AUTHOR);
    }

    @Nullable
    public String getWindowsCommentDescription()
    {
       return getUnicodeDescription(TAG_WIN_COMMENT);
    }

    @Nullable
    public String getWindowsKeywordsDescription()
    {
       return getUnicodeDescription(TAG_WIN_KEYWORDS);
    }

    @Nullable
    public String getWindowsTitleDescription()
    {
       return getUnicodeDescription(TAG_WIN_TITLE);
    }

    @Nullable
    public String getWindowsSubjectDescription()
    {
       return getUnicodeDescription(TAG_WIN_SUBJECT);
    }
}
