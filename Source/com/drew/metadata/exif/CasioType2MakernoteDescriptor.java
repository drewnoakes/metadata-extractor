/*
 * Copyright 2002-2013 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.metadata.exif;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a {@link CasioType2MakernoteDirectory}.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class CasioType2MakernoteDescriptor extends TagDescriptor<CasioType2MakernoteDirectory>
{
    public CasioType2MakernoteDescriptor(@NotNull CasioType2MakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case CasioType2MakernoteDirectory.TAG_THUMBNAIL_DIMENSIONS:
                return getThumbnailDimensionsDescription();
            case CasioType2MakernoteDirectory.TAG_THUMBNAIL_SIZE:
                return getThumbnailSizeDescription();
            case CasioType2MakernoteDirectory.TAG_THUMBNAIL_OFFSET:
                return getThumbnailOffsetDescription();
            case CasioType2MakernoteDirectory.TAG_QUALITY_MODE:
                return getQualityModeDescription();
            case CasioType2MakernoteDirectory.TAG_IMAGE_SIZE:
                return getImageSizeDescription();
            case CasioType2MakernoteDirectory.TAG_FOCUS_MODE_1:
                return getFocusMode1Description();
            case CasioType2MakernoteDirectory.TAG_ISO_SENSITIVITY:
                return getIsoSensitivityDescription();
            case CasioType2MakernoteDirectory.TAG_WHITE_BALANCE_1:
                return getWhiteBalance1Description();
            case CasioType2MakernoteDirectory.TAG_FOCAL_LENGTH:
                return getFocalLengthDescription();
            case CasioType2MakernoteDirectory.TAG_SATURATION:
                return getSaturationDescription();
            case CasioType2MakernoteDirectory.TAG_CONTRAST:
                return getContrastDescription();
            case CasioType2MakernoteDirectory.TAG_SHARPNESS:
                return getSharpnessDescription();
            case CasioType2MakernoteDirectory.TAG_PRINT_IMAGE_MATCHING_INFO:
                return getPrintImageMatchingInfoDescription();
            case CasioType2MakernoteDirectory.TAG_PREVIEW_THUMBNAIL:
                return getCasioPreviewThumbnailDescription();
            case CasioType2MakernoteDirectory.TAG_WHITE_BALANCE_BIAS:
                return getWhiteBalanceBiasDescription();
            case CasioType2MakernoteDirectory.TAG_WHITE_BALANCE_2:
                return getWhiteBalance2Description();
            case CasioType2MakernoteDirectory.TAG_OBJECT_DISTANCE:
                return getObjectDistanceDescription();
            case CasioType2MakernoteDirectory.TAG_FLASH_DISTANCE:
                return getFlashDistanceDescription();
            case CasioType2MakernoteDirectory.TAG_RECORD_MODE:
                return getRecordModeDescription();
            case CasioType2MakernoteDirectory.TAG_SELF_TIMER:
                return getSelfTimerDescription();
            case CasioType2MakernoteDirectory.TAG_QUALITY:
                return getQualityDescription();
            case CasioType2MakernoteDirectory.TAG_FOCUS_MODE_2:
                return getFocusMode2Description();
            case CasioType2MakernoteDirectory.TAG_TIME_ZONE:
                return getTimeZoneDescription();
            case CasioType2MakernoteDirectory.TAG_CCD_ISO_SENSITIVITY:
                return getCcdIsoSensitivityDescription();
            case CasioType2MakernoteDirectory.TAG_COLOUR_MODE:
                return getColourModeDescription();
            case CasioType2MakernoteDirectory.TAG_ENHANCEMENT:
                return getEnhancementDescription();
            case CasioType2MakernoteDirectory.TAG_FILTER:
                return getFilterDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getFilterDescription()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_FILTER, "Off");
    }

    @Nullable
    public String getEnhancementDescription()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_ENHANCEMENT, "Off");
    }

    @Nullable
    public String getColourModeDescription()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_COLOUR_MODE, "Off");
    }

    @Nullable
    public String getCcdIsoSensitivityDescription()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_CCD_ISO_SENSITIVITY, "Off", "On");
    }

    @Nullable
    public String getTimeZoneDescription()
    {
        return _directory.getString(CasioType2MakernoteDirectory.TAG_TIME_ZONE);
    }

    @Nullable
    public String getFocusMode2Description()
    {
        Integer value = _directory.getInteger(CasioType2MakernoteDirectory.TAG_FOCUS_MODE_2);
        if (value==null)
            return null;
        switch (value) {
            case 1:
                return "Fixation";
            case 6:
                return "Multi-Area Focus";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getQualityDescription()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_QUALITY, 3, "Fine");
    }

    @Nullable
    public String getSelfTimerDescription()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_SELF_TIMER, 1, "Off");
    }

    @Nullable
    public String getRecordModeDescription()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_RECORD_MODE, 2, "Normal");
    }

    @Nullable
    public String getFlashDistanceDescription()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_FLASH_DISTANCE, "Off");
    }

    @Nullable
    public String getObjectDistanceDescription()
    {
        Integer value = _directory.getInteger(CasioType2MakernoteDirectory.TAG_OBJECT_DISTANCE);
        if (value==null)
            return null;
        return Integer.toString(value) + " mm";
    }

    @Nullable
    public String getWhiteBalance2Description()
    {
        Integer value = _directory.getInteger(CasioType2MakernoteDirectory.TAG_WHITE_BALANCE_2);
        if (value==null)
            return null;
        switch (value) {
            case 0:
                return "Manual";
            case 1:
                return "Auto"; // unsure about this
            case 4:
                return "Flash"; // unsure about this
            case 12:
                return "Flash";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getWhiteBalanceBiasDescription()
    {
        return _directory.getString(CasioType2MakernoteDirectory.TAG_WHITE_BALANCE_BIAS);
    }

    @Nullable
    public String getCasioPreviewThumbnailDescription()
    {
        final byte[] bytes = _directory.getByteArray(CasioType2MakernoteDirectory.TAG_PREVIEW_THUMBNAIL);
        if (bytes==null)
            return null;
        return "<" + bytes.length + " bytes of image data>";
    }

    @Nullable
    public String getPrintImageMatchingInfoDescription()
    {
        // TODO research PIM specification http://www.ozhiker.com/electronics/pjmt/jpeg_info/pim.html
        return _directory.getString(CasioType2MakernoteDirectory.TAG_PRINT_IMAGE_MATCHING_INFO);
    }

    @Nullable
    public String getSharpnessDescription()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_SHARPNESS, "-1", "Normal", "+1");
    }

    @Nullable
    public String getContrastDescription()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_CONTRAST, "-1", "Normal", "+1");
    }

    @Nullable
    public String getSaturationDescription()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_SATURATION, "-1", "Normal", "+1");
    }

    @Nullable
    public String getFocalLengthDescription()
    {
        Double value = _directory.getDoubleObject(CasioType2MakernoteDirectory.TAG_FOCAL_LENGTH);
        if (value==null)
            return null;
        return Double.toString(value / 10d) + " mm";
    }

    @Nullable
    public String getWhiteBalance1Description()
    {
        return getIndexedDescription(
            CasioType2MakernoteDirectory.TAG_WHITE_BALANCE_1,
            "Auto",
            "Daylight",
            "Shade",
            "Tungsten",
            "Florescent",
            "Manual"
        );
    }

    @Nullable
    public String getIsoSensitivityDescription()
    {
        Integer value = _directory.getInteger(CasioType2MakernoteDirectory.TAG_ISO_SENSITIVITY);
        if (value==null)
            return null;
        switch (value) {
            case 3:
                return "50";
            case 4:
                return "64";
            case 6:
                return "100";
            case 9:
                return "200";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFocusMode1Description()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_FOCUS_MODE_1, "Normal", "Macro");
    }

    @Nullable
    public String getImageSizeDescription()
    {
        Integer value = _directory.getInteger(CasioType2MakernoteDirectory.TAG_IMAGE_SIZE);
        if (value==null)
            return null;
        switch (value) {
            case 0:  return "640 x 480 pixels";
            case 4:  return "1600 x 1200 pixels";
            case 5:  return "2048 x 1536 pixels";
            case 20: return "2288 x 1712 pixels";
            case 21: return "2592 x 1944 pixels";
            case 22: return "2304 x 1728 pixels";
            case 36: return "3008 x 2008 pixels";
            default: return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getQualityModeDescription()
    {
        return getIndexedDescription(CasioType2MakernoteDirectory.TAG_QUALITY_MODE, 1, "Fine", "Super Fine");
    }

    @Nullable
    public String getThumbnailOffsetDescription()
    {
        return _directory.getString(CasioType2MakernoteDirectory.TAG_THUMBNAIL_OFFSET);
    }

    @Nullable
    public String getThumbnailSizeDescription()
    {
        Integer value = _directory.getInteger(CasioType2MakernoteDirectory.TAG_THUMBNAIL_SIZE);
        if (value==null)
            return null;
        return Integer.toString(value) + " bytes";
    }

    @Nullable
    public String getThumbnailDimensionsDescription()
    {
        int[] dimensions = _directory.getIntArray(CasioType2MakernoteDirectory.TAG_THUMBNAIL_DIMENSIONS);
        if (dimensions==null || dimensions.length!=2)
            return _directory.getString(CasioType2MakernoteDirectory.TAG_THUMBNAIL_DIMENSIONS);
        return dimensions[0] + " x " + dimensions[1] + " pixels";
    }
}
