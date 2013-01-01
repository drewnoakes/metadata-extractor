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

import com.drew.lang.BufferBoundsException;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Age;
import com.drew.metadata.Face;
import com.drew.metadata.TagDescriptor;

/**
 * Provides human-readable string representations of tag values stored in a {@link PanasonicMakernoteDirectory}.
 * <p/>
 * Some information about this makernote taken from here:
 * <ul>
 * <li><a href="http://www.ozhiker.com/electronics/pjmt/jpeg_info/panasonic_mn.html">http://www.ozhiker.com/electronics/pjmt/jpeg_info/panasonic_mn.html</a></li>
 * <li><a href="http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/Panasonic.html">http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/Panasonic.html</a></li>
 * </ul>
 *
 * @author Drew Noakes http://drewnoakes.com
 * @author Philipp Sandhaus
 */
public class PanasonicMakernoteDescriptor extends TagDescriptor<PanasonicMakernoteDirectory>
{
    public PanasonicMakernoteDescriptor(@NotNull PanasonicMakernoteDirectory directory)
    {
        super(directory);
    }

    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case PanasonicMakernoteDirectory.TAG_QUALITY_MODE:
                return getQualityModeDescription();
            case PanasonicMakernoteDirectory.TAG_FIRMWARE_VERSION:
                return getVersionDescription();
            case PanasonicMakernoteDirectory.TAG_WHITE_BALANCE:
                return getWhiteBalanceDescription();
            case PanasonicMakernoteDirectory.TAG_FOCUS_MODE:
                return getFocusModeDescription();
            case PanasonicMakernoteDirectory.TAG_AF_AREA_MODE:
                return getAfAreaModeDescription();
            case PanasonicMakernoteDirectory.TAG_IMAGE_STABILIZATION:
                return getImageStabilizationDescription();
            case PanasonicMakernoteDirectory.TAG_MACRO_MODE:
                return getMacroModeDescription();
            case PanasonicMakernoteDirectory.TAG_RECORD_MODE:
                return getRecordModeDescription();
            case PanasonicMakernoteDirectory.TAG_AUDIO:
                return getAudioDescription();
            case PanasonicMakernoteDirectory.TAG_UNKNOWN_DATA_DUMP:
                return getUnknownDataDumpDescription();
            case PanasonicMakernoteDirectory.TAG_COLOR_EFFECT:
                return getColorEffectDescription();
            case PanasonicMakernoteDirectory.TAG_UPTIME:
                return getUptimeDescription();
            case PanasonicMakernoteDirectory.TAG_BURST_MODE:
                return getBurstModeDescription();
            case PanasonicMakernoteDirectory.TAG_CONTRAST_MODE:
                return getContrastModeDescription();
            case PanasonicMakernoteDirectory.TAG_NOISE_REDUCTION:
                return getNoiseReductionDescription();
            case PanasonicMakernoteDirectory.TAG_SELF_TIMER:
                return getSelfTimerDescription();
            case PanasonicMakernoteDirectory.TAG_ROTATION:
                return getRotationDescription();
            case PanasonicMakernoteDirectory.TAG_AF_ASSIST_LAMP:
                return getAfAssistLampDescription();
            case PanasonicMakernoteDirectory.TAG_COLOR_MODE:
                return getColorModeDescription();
            case PanasonicMakernoteDirectory.TAG_OPTICAL_ZOOM_MODE:
                return getOpticalZoomModeDescription();
            case PanasonicMakernoteDirectory.TAG_CONVERSION_LENS:
                return getConversionLensDescription();
            case PanasonicMakernoteDirectory.TAG_CONTRAST:
                return getContrastDescription();
            case PanasonicMakernoteDirectory.TAG_WORLD_TIME_LOCATION:
                return getWorldTimeLocationDescription();
            case PanasonicMakernoteDirectory.TAG_ADVANCED_SCENE_MODE:
                return getAdvancedSceneModeDescription();
            case PanasonicMakernoteDirectory.TAG_FACE_DETECTION_INFO:
                return getDetectedFacesDescription();
            case PanasonicMakernoteDirectory.TAG_TRANSFORM:
                return getTransformDescription();
			case PanasonicMakernoteDirectory.TAG_TRANSFORM_1:
	            return getTransform1Description();
            case PanasonicMakernoteDirectory.TAG_INTELLIGENT_EXPOSURE:
                return getIntelligentExposureDescription();
            case PanasonicMakernoteDirectory.TAG_FLASH_WARNING:
                return getFlashWarningDescription();
            case PanasonicMakernoteDirectory.TAG_COUNTRY:
                return getCountryDescription();
            case PanasonicMakernoteDirectory.TAG_STATE:
                return getStateDescription();
            case PanasonicMakernoteDirectory.TAG_CITY:
                return getCityDescription();
            case PanasonicMakernoteDirectory.TAG_LANDMARK:
                return getLandmarkDescription();
            case PanasonicMakernoteDirectory.TAG_INTELLIGENT_RESOLUTION:
                return getIntelligentResolutionDescription();
            case PanasonicMakernoteDirectory.TAG_FACE_RECOGNITION_INFO:
                return getRecognizedFacesDescription();
            case PanasonicMakernoteDirectory.TAG_PRINT_IMAGE_MATCHING_INFO:
                return getPrintImageMatchingInfoDescription();
            case PanasonicMakernoteDirectory.TAG_SCENE_MODE:
                return getSceneModeDescription();
            case PanasonicMakernoteDirectory.TAG_FLASH_FIRED:
                return getFlashFiredDescription();
            case PanasonicMakernoteDirectory.TAG_TEXT_STAMP:
		        return getTextStampDescription();
			case PanasonicMakernoteDirectory.TAG_TEXT_STAMP_1:
	             return getTextStamp1Description();
			case PanasonicMakernoteDirectory.TAG_TEXT_STAMP_2:
		         return getTextStamp2Description();
			case PanasonicMakernoteDirectory.TAG_TEXT_STAMP_3:
			     return getTextStamp3Description();
            case PanasonicMakernoteDirectory.TAG_MAKERNOTE_VERSION:
                return getMakernoteVersionDescription();
            case PanasonicMakernoteDirectory.TAG_EXIF_VERSION:
                return getExifVersionDescription();
            case PanasonicMakernoteDirectory.TAG_INTERNAL_SERIAL_NUMBER:
                return getInternalSerialNumberDescription();
            case PanasonicMakernoteDirectory.TAG_TITLE:
	            return getTitleDescription();
			case PanasonicMakernoteDirectory.TAG_BABY_NAME:
	            return getBabyNameDescription();
			case PanasonicMakernoteDirectory.TAG_LOCATION:
	            return getLocationDescription();
			case PanasonicMakernoteDirectory.TAG_BABY_AGE:
		        return getBabyAgeDescription();
			case PanasonicMakernoteDirectory.TAG_BABY_AGE_1:
		        return getBabyAge1Description();
			default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getPrintImageMatchingInfoDescription()
    {
        return getByteLengthDescription(PanasonicMakernoteDirectory.TAG_PRINT_IMAGE_MATCHING_INFO);
    }

    @Nullable
    public String getTextStampDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_TEXT_STAMP, 1, "Off", "On");
    }

	@Nullable
    public String getTextStamp1Description()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_TEXT_STAMP_1, 1, "Off", "On");
    }

	@Nullable
    public String getTextStamp2Description()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_TEXT_STAMP_2, 1, "Off", "On");
    }

	@Nullable
    public String getTextStamp3Description()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_TEXT_STAMP_3, 1, "Off", "On");
    }

	@Nullable
    public String getMacroModeDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_MACRO_MODE, 1, "Off", "On");
    }

    @Nullable
    public String getFlashFiredDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_FLASH_FIRED, 1, "Off", "On");
    }

    @Nullable
    public String getImageStabilizationDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_IMAGE_STABILIZATION,
            2,
            "On, Mode 1",
            "Off",
            "On, Mode 2"
        );
    }

    @Nullable
    public String getAudioDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_AUDIO, 1, "Off", "On");
    }

    @Nullable
    public String getTransformDescription()
    {
        return getTransformDescription(PanasonicMakernoteDirectory.TAG_TRANSFORM);
    }

    @Nullable
    public String getTransform1Description()
    {
        return getTransformDescription(PanasonicMakernoteDirectory.TAG_TRANSFORM_1);
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
        } catch (BufferBoundsException e) {
            return null;
        }
    }

    @Nullable
    public String getIntelligentExposureDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_INTELLIGENT_EXPOSURE,
            "Off", "Low", "Standard", "High");
    }

    @Nullable
    public String getFlashWarningDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_FLASH_WARNING,
            "No", "Yes (Flash required but disabled)");
    }
	
    @Nullable
    public String getCountryDescription()
    {
        return getAsciiStringFromBytes(PanasonicMakernoteDirectory.TAG_COUNTRY);
    }

    @Nullable
    public String getStateDescription()
    {
        return getAsciiStringFromBytes(PanasonicMakernoteDirectory.TAG_STATE);
    }

    @Nullable
    public String getCityDescription()
    {
        return getAsciiStringFromBytes(PanasonicMakernoteDirectory.TAG_CITY);
    }

    @Nullable
    public String getLandmarkDescription()
    {
        return getAsciiStringFromBytes(PanasonicMakernoteDirectory.TAG_LANDMARK);
    }

	@Nullable
    public String getTitleDescription()
    {
        return getAsciiStringFromBytes(PanasonicMakernoteDirectory.TAG_TITLE);
    }

	@Nullable
    public String getBabyNameDescription()
    {
        return getAsciiStringFromBytes(PanasonicMakernoteDirectory.TAG_BABY_NAME);
    }

	@Nullable
    public String getLocationDescription()
    {
        return getAsciiStringFromBytes(PanasonicMakernoteDirectory.TAG_LOCATION);
    }

    @Nullable
    public String getIntelligentResolutionDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_INTELLIGENT_RESOLUTION,
            "Off", null, "Auto", "On");
    }

    @Nullable
    public String getContrastDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_CONTRAST, "Normal");
    }

    @Nullable
    public String getWorldTimeLocationDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_WORLD_TIME_LOCATION,
            1, "Home", "Destination");
    }

    @Nullable
    public String getAdvancedSceneModeDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_ADVANCED_SCENE_MODE,
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
        return getByteLengthDescription(PanasonicMakernoteDirectory.TAG_UNKNOWN_DATA_DUMP);
    }

    @Nullable
    public String getColorEffectDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_COLOR_EFFECT,
            1, "Off", "Warm", "Cool", "Black & White", "Sepia"
        );
    }

    @Nullable
    public String getUptimeDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_UPTIME);
        if (value == null)
            return null;
        return value / 100f + " s";
    }

    @Nullable
    public String getBurstModeDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_BURST_MODE,
            "Off", null, "On", "Indefinite", "Unlimited"
        );
    }

    @Nullable
    public String getContrastModeDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_CONTRAST_MODE);
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
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_NOISE_REDUCTION,
            "Standard (0)", "Low (-1)", "High (+1)", "Lowest (-2)", "Highest (+2)"
        );
    }

    @Nullable
    public String getSelfTimerDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_SELF_TIMER,
            1, "Off", "10 s", "2 s"
        );
    }

    @Nullable
    public String getRotationDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_ROTATION);
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
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_AF_ASSIST_LAMP,
            1, "Fired", "Enabled but not used", "Disabled but required", "Disabled and not required"
        );
    }

    @Nullable
    public String getColorModeDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_COLOR_MODE,
            "Normal", "Natural", "Vivid"
        );
    }

    @Nullable
    public String getOpticalZoomModeDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_OPTICAL_ZOOM_MODE,
            1, "Standard", "Extended"
        );
    }

    @Nullable
    public String getConversionLensDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_CONVERSION_LENS,
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

        if (result.length() > 0)
            return result.substring(0, result.length() - 1);

        return null;
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
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_RECORD_MODE, 1, _sceneModes);
    }

    @Nullable
    public String getSceneModeDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_SCENE_MODE, 1, _sceneModes);
    }

    @Nullable
    public String getFocusModeDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_FOCUS_MODE, 1,
            "Auto", "Manual", null, "Auto, Focus Button", "Auto, Continuous");
    }

    @Nullable
    public String getAfAreaModeDescription()
    {
        int[] value = _directory.getIntArray(PanasonicMakernoteDirectory.TAG_AF_AREA_MODE);
        if (value == null || value.length < 2)
            return null;
        switch (value[0]) {
            case 0:
                switch (value[1]) {
                    case 1:
                        return "Spot Mode On";
                    case 16:
                        return "Spot Mode Off";
                    default:
                        return "Unknown (" + value[0] + " " + value[1] + ")";
                }
            case 1:
                switch (value[1]) {
                    case 0:
                        return "Spot Focusing";
                    case 1:
                        return "5-area";
                    default:
                        return "Unknown (" + value[0] + " " + value[1] + ")";
                }
            case 16:
                switch (value[1]) {
                    case 0:
                        return "1-area";
                    case 16:
                        return "1-area (high speed)";
                    default:
                        return "Unknown (" + value[0] + " " + value[1] + ")";
                }
            case 32:
                switch (value[1]) {
                    case 0:
                        return "Auto or Face Detect";
                    case 1:
                        return "3-area (left)";
                    case 2:
                        return "3-area (center)";
                    case 3:
                        return "3-area (right)";
                    default:
                        return "Unknown (" + value[0] + " " + value[1] + ")";
                }
            case 64:
                return "Face Detect";
            default:
                return "Unknown (" + value[0] + " " + value[1] + ")";
        }
    }

    @Nullable
    public String getQualityModeDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_QUALITY_MODE,
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
        return getVersionBytesDescription(PanasonicMakernoteDirectory.TAG_FIRMWARE_VERSION, 2);
    }

    @Nullable
    public String getMakernoteVersionDescription()
    {
        return getVersionBytesDescription(PanasonicMakernoteDirectory.TAG_MAKERNOTE_VERSION, 2);
    }

    @Nullable
    public String getExifVersionDescription()
    {
        return getVersionBytesDescription(PanasonicMakernoteDirectory.TAG_EXIF_VERSION, 2);
    }

    @Nullable
    public String getInternalSerialNumberDescription()
    {
        return get7BitStringFromBytes(PanasonicMakernoteDirectory.TAG_INTERNAL_SERIAL_NUMBER);
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        return getIndexedDescription(PanasonicMakernoteDirectory.TAG_WHITE_BALANCE,
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
        final Age age = _directory.getAge(PanasonicMakernoteDirectory.TAG_BABY_AGE);
        if (age == null)
            return null;
        return age.toFriendlyString();
	}
	
	@Nullable
	public String getBabyAge1Description()
    {
        final Age age = _directory.getAge(PanasonicMakernoteDirectory.TAG_BABY_AGE_1);
        if (age==null)
            return null;
        return age.toFriendlyString();
	}
}
