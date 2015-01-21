/*
 * Copyright 2002-2015 Drew Noakes
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
package com.drew.metadata.exif.makernotes;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Age;
import com.drew.metadata.Face;
import com.drew.metadata.TagDescriptor;

import java.io.IOException;

import static com.drew.metadata.exif.makernotes.PanasonicMakernoteDirectory.*;

/**
 * Provides human-readable string representations of tag values stored in a {@link PanasonicMakernoteDirectory}.
 * <p>
 * Some information about this makernote taken from here:
 * <ul>
 * <li><a href="http://www.ozhiker.com/electronics/pjmt/jpeg_info/panasonic_mn.html">http://www.ozhiker.com/electronics/pjmt/jpeg_info/panasonic_mn.html</a></li>
 * <li><a href="http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/Panasonic.html">http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/Panasonic.html</a></li>
 * </ul>
 *
 * @author Drew Noakes https://drewnoakes.com
 * @author Philipp Sandhaus
 */
public class PanasonicMakernoteDescriptor extends TagDescriptor<PanasonicMakernoteDirectory>
{
    public PanasonicMakernoteDescriptor(@NotNull PanasonicMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_QUALITY_MODE:
                return getQualityModeDescription();
            case TAG_FIRMWARE_VERSION:
                return getVersionDescription();
            case TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case TAG_FOCUS_MODE:
                return getFocusModeDescription();
            case TAG_AF_AREA_MODE:
                return getAfAreaModeDescription();
            case TAG_IMAGE_STABILIZATION:
                return getImageStabilizationDescription();
            case TAG_MACRO_MODE:
                return getMacroModeDescription();
            case TAG_RECORD_MODE:
                return getRecordModeDescription();
            case TAG_AUDIO:
                return getAudioDescription();
            case TAG_UNKNOWN_DATA_DUMP:
                return getUnknownDataDumpDescription();
            case TAG_COLOR_EFFECT:
                return getColorEffectDescription();
            case TAG_UPTIME:
                return getUptimeDescription();
            case TAG_BURST_MODE:
                return getBurstModeDescription();
            case TAG_CONTRAST_MODE:
                return getContrastModeDescription();
            case TAG_NOISE_REDUCTION:
                return getNoiseReductionDescription();
            case TAG_SELF_TIMER:
                return getSelfTimerDescription();
            case TAG_ROTATION:
                return getRotationDescription();
            case TAG_AF_ASSIST_LAMP:
                return getAfAssistLampDescription();
            case TAG_COLOR_MODE:
                return getColorModeDescription();
            case TAG_OPTICAL_ZOOM_MODE:
                return getOpticalZoomModeDescription();
            case TAG_CONVERSION_LENS:
                return getConversionLensDescription();
            case TAG_CONTRAST:
                return getContrastDescription();
            case TAG_WORLD_TIME_LOCATION:
                return getWorldTimeLocationDescription();
            case TAG_ADVANCED_SCENE_MODE:
                return getAdvancedSceneModeDescription();
            case TAG_FACE_DETECTION_INFO:
                return getDetectedFacesDescription();
            case TAG_TRANSFORM:
                return getTransformDescription();
			case TAG_TRANSFORM_1:
	            return getTransform1Description();
            case TAG_INTELLIGENT_EXPOSURE:
                return getIntelligentExposureDescription();
            case TAG_FLASH_WARNING:
                return getFlashWarningDescription();
            case TAG_COUNTRY:
                return getCountryDescription();
            case TAG_STATE:
                return getStateDescription();
            case TAG_CITY:
                return getCityDescription();
            case TAG_LANDMARK:
                return getLandmarkDescription();
            case TAG_INTELLIGENT_RESOLUTION:
                return getIntelligentResolutionDescription();
            case TAG_FACE_RECOGNITION_INFO:
                return getRecognizedFacesDescription();
            case TAG_PRINT_IMAGE_MATCHING_INFO:
                return getPrintImageMatchingInfoDescription();
            case TAG_SCENE_MODE:
                return getSceneModeDescription();
            case TAG_FLASH_FIRED:
                return getFlashFiredDescription();
            case TAG_TEXT_STAMP:
		        return getTextStampDescription();
			case TAG_TEXT_STAMP_1:
	             return getTextStamp1Description();
			case TAG_TEXT_STAMP_2:
		         return getTextStamp2Description();
			case TAG_TEXT_STAMP_3:
			     return getTextStamp3Description();
            case TAG_MAKERNOTE_VERSION:
                return getMakernoteVersionDescription();
            case TAG_EXIF_VERSION:
                return getExifVersionDescription();
            case TAG_INTERNAL_SERIAL_NUMBER:
                return getInternalSerialNumberDescription();
            case TAG_TITLE:
	            return getTitleDescription();
			case TAG_BABY_NAME:
	            return getBabyNameDescription();
			case TAG_LOCATION:
	            return getLocationDescription();
			case TAG_BABY_AGE:
		        return getBabyAgeDescription();
			case TAG_BABY_AGE_1:
		        return getBabyAge1Description();
			default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getPrintImageMatchingInfoDescription()
    {
        return getByteLengthDescription(TAG_PRINT_IMAGE_MATCHING_INFO);
    }

    @Nullable
    public String getTextStampDescription()
    {
        return getIndexedDescription(TAG_TEXT_STAMP, 1, "Off", "On");
    }

	@Nullable
    public String getTextStamp1Description()
    {
        return getIndexedDescription(TAG_TEXT_STAMP_1, 1, "Off", "On");
    }

	@Nullable
    public String getTextStamp2Description()
    {
        return getIndexedDescription(TAG_TEXT_STAMP_2, 1, "Off", "On");
    }

	@Nullable
    public String getTextStamp3Description()
    {
        return getIndexedDescription(TAG_TEXT_STAMP_3, 1, "Off", "On");
    }

	@Nullable
    public String getMacroModeDescription()
    {
        return getIndexedDescription(TAG_MACRO_MODE, 1, "Off", "On");
    }

    @Nullable
    public String getFlashFiredDescription()
    {
        return getIndexedDescription(TAG_FLASH_FIRED, 1, "Off", "On");
    }

    @Nullable
    public String getImageStabilizationDescription()
    {
        return getIndexedDescription(TAG_IMAGE_STABILIZATION,
            2,
            "On, Mode 1",
            "Off",
            "On, Mode 2"
        );
    }

    @Nullable
    public String getAudioDescription()
    {
        return getIndexedDescription(TAG_AUDIO, 1, "Off", "On");
    }

    @Nullable
    public String getTransformDescription()
    {
        return getTransformDescription(TAG_TRANSFORM);
    }

    @Nullable
    public String getTransform1Description()
    {
        return getTransformDescription(TAG_TRANSFORM_1);
    }

    @Nullable
    private String getTransformDescription(int tag)
    {
        byte[] values = _directory.getByteArray(tag);
        if (values == null)
            return null;

        RandomAccessReader reader = new ByteArrayReader(values);

        try
        {
            int val1 = reader.getUInt16(0);
            int val2 = reader.getUInt16(2);

            if (val1 == -1 && val2 == 1)
                return "Slim Low";
            if (val1 == -3 && val2 == 2)
                return "Slim High";
            if (val1 == 0 && val2 == 0)
                return "Off";
            if (val1 == 1 && val2 == 1)
                return "Stretch Low";
            if (val1 == 3 && val2 == 2)
                return "Stretch High";

            return "Unknown (" + val1 + " " + val2 + ")";
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public String getIntelligentExposureDescription()
    {
        return getIndexedDescription(TAG_INTELLIGENT_EXPOSURE,
            "Off", "Low", "Standard", "High");
    }

    @Nullable
    public String getFlashWarningDescription()
    {
        return getIndexedDescription(TAG_FLASH_WARNING,
            "No", "Yes (Flash required but disabled)");
    }

    @Nullable
    public String getCountryDescription()
    {
        return getAsciiStringFromBytes(TAG_COUNTRY);
    }

    @Nullable
    public String getStateDescription()
    {
        return getAsciiStringFromBytes(TAG_STATE);
    }

    @Nullable
    public String getCityDescription()
    {
        return getAsciiStringFromBytes(TAG_CITY);
    }

    @Nullable
    public String getLandmarkDescription()
    {
        return getAsciiStringFromBytes(TAG_LANDMARK);
    }

	@Nullable
    public String getTitleDescription()
    {
        return getAsciiStringFromBytes(TAG_TITLE);
    }

	@Nullable
    public String getBabyNameDescription()
    {
        return getAsciiStringFromBytes(TAG_BABY_NAME);
    }

	@Nullable
    public String getLocationDescription()
    {
        return getAsciiStringFromBytes(TAG_LOCATION);
    }

    @Nullable
    public String getIntelligentResolutionDescription()
    {
        return getIndexedDescription(TAG_INTELLIGENT_RESOLUTION,
            "Off", null, "Auto", "On");
    }

    @Nullable
    public String getContrastDescription()
    {
        return getIndexedDescription(TAG_CONTRAST, "Normal");
    }

    @Nullable
    public String getWorldTimeLocationDescription()
    {
        return getIndexedDescription(TAG_WORLD_TIME_LOCATION,
            1, "Home", "Destination");
    }

    @Nullable
    public String getAdvancedSceneModeDescription()
    {
        return getIndexedDescription(TAG_ADVANCED_SCENE_MODE,
            1,
            "Normal",
            "Outdoor/Illuminations/Flower/HDR Art",
            "Indoor/Architecture/Objects/HDR B&W",
            "Creative",
            "Auto",
            null,
            "Expressive",
            "Retro",
            "Pure",
            "Elegant",
            null,
            "Monochrome",
            "Dynamic Art",
            "Silhouette"
        );
    }

    @Nullable
    public String getUnknownDataDumpDescription()
    {
        return getByteLengthDescription(TAG_UNKNOWN_DATA_DUMP);
    }

    @Nullable
    public String getColorEffectDescription()
    {
        return getIndexedDescription(TAG_COLOR_EFFECT,
            1, "Off", "Warm", "Cool", "Black & White", "Sepia"
        );
    }

    @Nullable
    public String getUptimeDescription()
    {
        Integer value = _directory.getInteger(TAG_UPTIME);
        if (value == null)
            return null;
        return value / 100f + " s";
    }

    @Nullable
    public String getBurstModeDescription()
    {
        return getIndexedDescription(TAG_BURST_MODE,
            "Off", null, "On", "Indefinite", "Unlimited"
        );
    }

    @Nullable
    public String getContrastModeDescription()
    {
        Integer value = _directory.getInteger(TAG_CONTRAST_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 0x0: return "Normal";
            case 0x1: return "Low";
            case 0x2: return "High";
            case 0x6: return "Medium Low";
            case 0x7: return "Medium High";
            case 0x100: return "Low";
            case 0x110: return "Normal";
            case 0x120: return "High";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getNoiseReductionDescription()
    {
        return getIndexedDescription(TAG_NOISE_REDUCTION,
            "Standard (0)", "Low (-1)", "High (+1)", "Lowest (-2)", "Highest (+2)"
        );
    }

    @Nullable
    public String getSelfTimerDescription()
    {
        return getIndexedDescription(TAG_SELF_TIMER,
            1, "Off", "10 s", "2 s"
        );
    }

    @Nullable
    public String getRotationDescription()
    {
        Integer value = _directory.getInteger(TAG_ROTATION);
        if (value == null)
            return null;
        switch (value) {
            case 1: return "Horizontal";
            case 3: return "Rotate 180";
            case 6: return "Rotate 90 CW";
            case 8: return "Rotate 270 CW";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAfAssistLampDescription()
    {
        return getIndexedDescription(TAG_AF_ASSIST_LAMP,
            1, "Fired", "Enabled but not used", "Disabled but required", "Disabled and not required"
        );
    }

    @Nullable
    public String getColorModeDescription()
    {
        return getIndexedDescription(TAG_COLOR_MODE,
            "Normal", "Natural", "Vivid"
        );
    }

    @Nullable
    public String getOpticalZoomModeDescription()
    {
        return getIndexedDescription(TAG_OPTICAL_ZOOM_MODE,
            1, "Standard", "Extended"
        );
    }

    @Nullable
    public String getConversionLensDescription()
    {
        return getIndexedDescription(TAG_CONVERSION_LENS,
            1, "Off", "Wide", "Telephoto", "Macro"
        );
    }

    @Nullable
    public String getDetectedFacesDescription()
    {
        return buildFacesDescription(_directory.getDetectedFaces());
    }

    @Nullable
    public String getRecognizedFacesDescription()
    {
        return buildFacesDescription(_directory.getRecognizedFaces());
    }

    @Nullable
    private String buildFacesDescription(@Nullable Face[] faces)
    {
        if (faces == null)
            return null;

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < faces.length; i++)
            result.append("Face ").append(i + 1).append(": ").append(faces[i].toString()).append("\n");

        return result.length() > 0 ? result.substring(0, result.length() - 1) : null;

    }

    private static final String[] _sceneModes = new String[] {
        "Normal", // 1
        "Portrait",
        "Scenery",
        "Sports",
        "Night Portrait",
        "Program",
        "Aperture Priority",
        "Shutter Priority",
        "Macro",
        "Spot", // 10
        "Manual",
        "Movie Preview",
        "Panning",
        "Simple",
        "Color Effects",
        "Self Portrait",
        "Economy",
        "Fireworks",
        "Party",
        "Snow", // 20
        "Night Scenery",
        "Food",
        "Baby",
        "Soft Skin",
        "Candlelight",
        "Starry Night",
        "High Sensitivity",
        "Panorama Assist",
        "Underwater",
        "Beach", // 30
        "Aerial Photo",
        "Sunset",
        "Pet",
        "Intelligent ISO",
        "Clipboard",
        "High Speed Continuous Shooting",
        "Intelligent Auto",
        null,
        "Multi-aspect",
        null, // 40
        "Transform",
        "Flash Burst",
        "Pin Hole",
        "Film Grain",
        "My Color",
        "Photo Frame",
        null,
        null,
        null,
        null, // 50
        "HDR"
    };

    @Nullable
    public String getRecordModeDescription()
    {
        return getIndexedDescription(TAG_RECORD_MODE, 1, _sceneModes);
    }

    @Nullable
    public String getSceneModeDescription()
    {
        return getIndexedDescription(TAG_SCENE_MODE, 1, _sceneModes);
    }

    @Nullable
    public String getFocusModeDescription()
    {
        return getIndexedDescription(TAG_FOCUS_MODE, 1,
            "Auto", "Manual", null, "Auto, Focus Button", "Auto, Continuous");
    }

    @Nullable
    public String getAfAreaModeDescription()
    {
        int[] value = _directory.getIntArray(TAG_AF_AREA_MODE);
        if (value == null || value.length < 2)
            return null;
        switch (value[0]) {
            case 0:
                switch (value[1]) {
                    case 1: return "Spot Mode On";
                    case 16: return "Spot Mode Off";
                    default: return "Unknown (" + value[0] + " " + value[1] + ")";
                }
            case 1:
                switch (value[1]) {
                    case 0: return "Spot Focusing";
                    case 1: return "5-area";
                    default: return "Unknown (" + value[0] + " " + value[1] + ")";
                }
            case 16:
                switch (value[1]) {
                    case 0: return "1-area";
                    case 16: return "1-area (high speed)";
                    default: return "Unknown (" + value[0] + " " + value[1] + ")";
                }
            case 32:
                switch (value[1]) {
                    case 0: return "Auto or Face Detect";
                    case 1: return "3-area (left)";
                    case 2: return "3-area (center)";
                    case 3: return "3-area (right)";
                    default: return "Unknown (" + value[0] + " " + value[1] + ")";
                }
            case 64: return "Face Detect";
            default: return "Unknown (" + value[0] + " " + value[1] + ")";
        }
    }

    @Nullable
    public String getQualityModeDescription()
    {
        return getIndexedDescription(TAG_QUALITY_MODE,
            2,
            "High", // 2
            "Normal",
            null,
            null,
            "Very High",
            "Raw",
            null,
            "Motion Picture" // 9
        );
    }

    @Nullable
    public String getVersionDescription()
    {
        return getVersionBytesDescription(TAG_FIRMWARE_VERSION, 2);
    }

    @Nullable
    public String getMakernoteVersionDescription()
    {
        return getVersionBytesDescription(TAG_MAKERNOTE_VERSION, 2);
    }

    @Nullable
    public String getExifVersionDescription()
    {
        return getVersionBytesDescription(TAG_EXIF_VERSION, 2);
    }

    @Nullable
    public String getInternalSerialNumberDescription()
    {
        return get7BitStringFromBytes(TAG_INTERNAL_SERIAL_NUMBER);
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        return getIndexedDescription(TAG_WHITE_BALANCE,
            1,
            "Auto", // 1
            "Daylight",
            "Cloudy",
            "Incandescent",
            "Manual",
            null,
            null,
            "Flash",
            null,
            "Black & White", // 10
            "Manual",
            "Shade" // 12
        );
    }

	@Nullable
	public String getBabyAgeDescription()
    {
        final Age age = _directory.getAge(TAG_BABY_AGE);
        return age == null ? null : age.toFriendlyString();
    }

	@Nullable
	public String getBabyAge1Description()
    {
        final Age age = _directory.getAge(TAG_BABY_AGE_1);
        return age == null ? null : age.toFriendlyString();
    }
}
