/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 27-Nov-2002 10:12:05 using IntelliJ IDEA.
 */
package com.drew.metadata.exif;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string represenations of tag values stored in a <code>CasioType2MakernoteDirectory</code>.
 */
public class CasioType2MakernoteDescriptor extends TagDescriptor
{
    public CasioType2MakernoteDescriptor(Directory directory)
    {
        super(directory);
    }

    public String getDescription(int tagType) throws MetadataException
    {
        switch (tagType) {
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_THUMBNAIL_DIMENSIONS:
                return getThumbnailDimensionsDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_THUMBNAIL_SIZE:
                return getThumbnailSizeDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_THUMBNAIL_OFFSET:
                return getThumbnailOffsetDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_QUALITY_MODE:
                return getQualityModeDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_IMAGE_SIZE:
                return getImageSizeDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FOCUS_MODE_1:
                return getFocusMode1Description();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_ISO_SENSITIVITY:
                return getIsoSensitivityDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_WHITE_BALANCE_1:
                return getWhiteBalance1Description();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FOCAL_LENGTH:
                return getFocalLengthDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_SATURATION:
                return getSaturationDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_CONTRAST:
                return getContrastDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_SHARPNESS:
                return getSharpnessDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_PRINT_IMAGE_MATCHING_INFO:
                return getPrintImageMatchingInfoDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_CASIO_PREVIEW_THUMBNAIL:
                return getCasioPreviewThumbnailDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_WHITE_BALANCE_BIAS:
                return getWhiteBalanceBiasDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_WHITE_BALANCE_2:
                return getWhiteBalance2Description();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_OBJECT_DISTANCE:
                return getObjectDistanceDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FLASH_DISTANCE:
                return getFlashDistanceDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_RECORD_MODE:
                return getRecordModeDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_SELF_TIMER:
                return getSelfTimerDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_QUALITY:
                return getQualityDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FOCUS_MODE_2:
                return getFocusMode2Description();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_TIME_ZONE:
                return getTimeZoneDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_BESTSHOT_MODE:
                return getBestShotModeDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_CCD_ISO_SENSITIVITY:
                return getCcdIsoSensitivityDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_COLOUR_MODE:
                return getColourModeDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_ENHANCEMENT:
                return getEnhancementDescription();
            case CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FILTER:
                return getFilterDescription();
            default:
                return _directory.getString(tagType);
        }
    }

    public String getFilterDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FILTER)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FILTER);
        switch (value) {
            case 0:
                return "Off";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getEnhancementDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_ENHANCEMENT)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_ENHANCEMENT);
        switch (value) {
            case 0:
                return "Off";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getColourModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_COLOUR_MODE)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_COLOUR_MODE);
        switch (value) {
            case 0:
                return "Off";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getCcdIsoSensitivityDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_CCD_ISO_SENSITIVITY)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_CCD_ISO_SENSITIVITY);
        switch (value) {
            case 0:
                return "Off";
            case 1:
                return "On";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getBestShotModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_BESTSHOT_MODE)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_BESTSHOT_MODE);
        switch (value) {
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getTimeZoneDescription()
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_TIME_ZONE)) return null;
        return _directory.getString(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_TIME_ZONE);
    }

    public String getFocusMode2Description() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FOCUS_MODE_2)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FOCUS_MODE_2);
        switch (value) {
            case 1:
                return "Fixation";
            case 6:
                return "Multi-Area Focus";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getQualityDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_QUALITY)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_QUALITY);
        switch (value) {
            case 3:
                return "Fine";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getSelfTimerDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_SELF_TIMER)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_SELF_TIMER);
        switch (value) {
            case 1:
                return "Off";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getRecordModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_RECORD_MODE)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_RECORD_MODE);
        switch (value) {
            case 2:
                return "Normal";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getFlashDistanceDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FLASH_DISTANCE)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FLASH_DISTANCE);
        switch (value) {
            case 0:
                return "Off";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getObjectDistanceDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_OBJECT_DISTANCE)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_OBJECT_DISTANCE);
        return Integer.toString(value) + " mm";
    }

    public String getWhiteBalance2Description() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_WHITE_BALANCE_2)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_WHITE_BALANCE_2);
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

    public String getWhiteBalanceBiasDescription()
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_WHITE_BALANCE_BIAS)) return null;
        return _directory.getString(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_WHITE_BALANCE_BIAS);
    }

    public String getCasioPreviewThumbnailDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_CASIO_PREVIEW_THUMBNAIL)) return null;
        final byte[] bytes = _directory.getByteArray(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_CASIO_PREVIEW_THUMBNAIL);
        return "<" + bytes.length + " bytes of image data>";
    }

    public String getPrintImageMatchingInfoDescription()
    {
        // TODO research PIM specification http://www.ozhiker.com/electronics/pjmt/jpeg_info/pim.html
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_PRINT_IMAGE_MATCHING_INFO)) return null;
        return _directory.getString(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_PRINT_IMAGE_MATCHING_INFO);
    }

    public String getSharpnessDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_SHARPNESS)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_SHARPNESS);
        switch (value) {
            case 0:
                return "-1";
            case 1:
                return "Normal";
            case 2:
                return "+1";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getContrastDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_CONTRAST)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_CONTRAST);
        switch (value) {
            case 0:
                return "-1";
            case 1:
                return "Normal";
            case 2:
                return "+1";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getSaturationDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_SATURATION)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_SATURATION);
        switch (value) {
            case 0:
                return "-1";
            case 1:
                return "Normal";
            case 2:
                return "+1";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getFocalLengthDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FOCAL_LENGTH)) return null;
        double value = _directory.getDouble(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FOCAL_LENGTH);
        return Double.toString(value / 10d) + " mm";
    }

    public String getWhiteBalance1Description() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_WHITE_BALANCE_1)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_WHITE_BALANCE_1);
        switch (value) {
            case 0:
                return "Auto";
            case 1:
                return "Daylight";
            case 2:
                return "Shade";
            case 3:
                return "Tungsten";
            case 4:
                return "Flourescent";
            case 5:
                return "Manual";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getIsoSensitivityDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_ISO_SENSITIVITY)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_ISO_SENSITIVITY);
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

    public String getFocusMode1Description() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FOCUS_MODE_1)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_FOCUS_MODE_1);
        switch (value) {
            case 0:
                return "Normal";
            case 1:
                return "Macro";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getImageSizeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_IMAGE_SIZE)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_IMAGE_SIZE);
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

    public String getQualityModeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_QUALITY_MODE)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_QUALITY_MODE);
        switch (value) {
            case 1:
                return "Fine";
            case 2:
                return "Super Fine";
            default:
                return "Unknown (" + value + ")";
        }
    }

    public String getThumbnailOffsetDescription()
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_THUMBNAIL_OFFSET)) return null;
        return _directory.getString(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_THUMBNAIL_OFFSET);
    }

    public String getThumbnailSizeDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_THUMBNAIL_SIZE)) return null;
        int value = _directory.getInt(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_THUMBNAIL_SIZE);
        return Integer.toString(value) + " bytes";
    }

    public String getThumbnailDimensionsDescription() throws MetadataException
    {
        if (!_directory.containsTag(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_THUMBNAIL_DIMENSIONS)) return null;
        int[] dimensions = _directory.getIntArray(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_THUMBNAIL_DIMENSIONS);
        if (dimensions.length!=2)
            return _directory.getString(CasioType2MakernoteDirectory.TAG_CASIO_TYPE2_THUMBNAIL_DIMENSIONS);
        return dimensions[0] + " x " + dimensions[1] + " pixels";
    }
}