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
package com.drew.metadata.exif.makernotes;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.Charsets;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Age;
import com.drew.metadata.Face;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;
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
@SuppressWarnings("WeakerAccess")
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
            case TAG_BRACKET_SETTINGS:
                return getBracketSettingsDescription();
            case TAG_FLASH_CURTAIN:
                return getFlashCurtainDescription();
            case TAG_LONG_EXPOSURE_NOISE_REDUCTION:
                return getLongExposureNoiseReductionDescription();
            case TAG_BABY_NAME:
                return getBabyNameDescription();
            case TAG_LOCATION:
                return getLocationDescription();

            case TAG_LENS_FIRMWARE_VERSION:
                return getLensFirmwareVersionDescription();
            case TAG_INTELLIGENT_D_RANGE:
                return getIntelligentDRangeDescription();
            case TAG_CLEAR_RETOUCH:
                return getClearRetouchDescription();
            case TAG_PHOTO_STYLE:
                return getPhotoStyleDescription();
            case TAG_SHADING_COMPENSATION:
                return getShadingCompensationDescription();

            case TAG_ACCELEROMETER_Z:
                return getAccelerometerZDescription();
            case TAG_ACCELEROMETER_X:
                return getAccelerometerXDescription();
            case TAG_ACCELEROMETER_Y:
                return getAccelerometerYDescription();
            case TAG_CAMERA_ORIENTATION:
                return getCameraOrientationDescription();
            case TAG_ROLL_ANGLE:
                return getRollAngleDescription();
            case TAG_PITCH_ANGLE:
                return getPitchAngleDescription();
            case TAG_SWEEP_PANORAMA_DIRECTION:
                return getSweepPanoramaDirectionDescription();
            case TAG_TIMER_RECORDING:
                return getTimerRecordingDescription();
            case TAG_HDR:
                return getHDRDescription();
            case TAG_SHUTTER_TYPE:
                return getShutterTypeDescription();
            case TAG_TOUCH_AE:
                return getTouchAeDescription();

            case TAG_BABY_AGE:
                return getBabyAgeDescription();
            case TAG_BABY_AGE_1:
                return getBabyAge1Description();
            default:
                return super.getDescription(tagType);
        }
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
    private static String trim(@Nullable String s)
    {
        return s == null ? null : s.trim();
    }

    @Nullable
    public String getCountryDescription()
    {
        return trim(getStringFromBytes(TAG_COUNTRY, Charsets.UTF_8));
    }

    @Nullable
    public String getStateDescription()
    {
        return trim(getStringFromBytes(TAG_STATE, Charsets.UTF_8));
    }

    @Nullable
    public String getCityDescription()
    {
        return trim(getStringFromBytes(TAG_CITY, Charsets.UTF_8));
    }

    @Nullable
    public String getLandmarkDescription()
    {
        return trim(getStringFromBytes(TAG_LANDMARK, Charsets.UTF_8));
    }

    @Nullable
    public String getTitleDescription()
    {
        return trim(getStringFromBytes(TAG_TITLE, Charsets.UTF_8));
    }

    @Nullable
    public String getBracketSettingsDescription()
    {
        return getIndexedDescription(TAG_BRACKET_SETTINGS,
            "No Bracket", "3 Images, Sequence 0/-/+", "3 Images, Sequence -/0/+", "5 Images, Sequence 0/-/+",
            "5 Images, Sequence -/0/+", "7 Images, Sequence 0/-/+", "7 Images, Sequence -/0/+");
    }

    @Nullable
    public String getFlashCurtainDescription()
    {
        return getIndexedDescription(TAG_FLASH_CURTAIN,
            "n/a", "1st", "2nd");
    }

    @Nullable
    public String getLongExposureNoiseReductionDescription()
    {
        return getIndexedDescription(TAG_LONG_EXPOSURE_NOISE_REDUCTION, 1,
            "Off", "On");
    }

    @Nullable
    public String getLensFirmwareVersionDescription()
    {
        // lens version has 4 parts separated by periods
        byte[] bytes = _directory.getByteArray(TAG_LENS_FIRMWARE_VERSION);
        if (bytes == null)
            return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(bytes[i]);
            if (i < bytes.length - 1)
                sb.append(".");
        }
        return sb.toString();
        //return string.Join(".", bytes.Select(b => b.ToString()).ToArray());
    }

    @Nullable
    public String getIntelligentDRangeDescription()
    {
        return getIndexedDescription(TAG_INTELLIGENT_D_RANGE,
            "Off", "Low", "Standard", "High");
    }

    @Nullable
    public String getClearRetouchDescription()
    {
        return getIndexedDescription(TAG_CLEAR_RETOUCH,
                "Off", "On");

    }

    @Nullable
    public String getPhotoStyleDescription()
    {
        return getIndexedDescription(TAG_PHOTO_STYLE,
            "Auto", "Standard or Custom", "Vivid", "Natural", "Monochrome", "Scenery", "Portrait");
    }

    @Nullable
    public String getShadingCompensationDescription()
    {
        return getIndexedDescription(TAG_SHADING_COMPENSATION,
            "Off", "On");
    }

    @Nullable
    public String getAccelerometerZDescription()
    {
        Integer value = _directory.getInteger(TAG_ACCELEROMETER_Z);
        if (value == null)
            return null;

        // positive is acceleration upwards
        return String.valueOf(value.shortValue());
    }

    @Nullable
    public String getAccelerometerXDescription()
    {
        Integer value = _directory.getInteger(TAG_ACCELEROMETER_X);
        if (value == null)
            return null;

        // positive is acceleration to the left
        return String.valueOf(value.shortValue());
    }

    @Nullable
    public String getAccelerometerYDescription()
    {
        Integer value = _directory.getInteger(TAG_ACCELEROMETER_Y);
        if (value == null)
            return null;

        // positive is acceleration backwards
        return String.valueOf(value.shortValue());
    }

    @Nullable
    public String getCameraOrientationDescription()
    {
        return getIndexedDescription(TAG_CAMERA_ORIENTATION,
                "Normal", "Rotate CW", "Rotate 180", "Rotate CCW", "Tilt Upwards", "Tile Downwards");
    }

    @Nullable
    public String getRollAngleDescription()
    {
        Integer value = _directory.getInteger(TAG_ROLL_ANGLE);
        if (value == null)
            return null;

        DecimalFormat format = new DecimalFormat("0.#");
        // converted to degrees of clockwise camera rotation
        return format.format(value.shortValue() / 10.0);
    }

    @Nullable
    public String getPitchAngleDescription()
    {
        Integer value = _directory.getInteger(TAG_PITCH_ANGLE);
        if (value == null)
            return null;

        DecimalFormat format = new DecimalFormat("0.#");
        // converted to degrees of upward camera tilt
        return format.format(-value.shortValue() / 10.0);
    }

    @Nullable
    public String getSweepPanoramaDirectionDescription()
    {
        return getIndexedDescription(TAG_SWEEP_PANORAMA_DIRECTION,
                "Off", "Left to Right", "Right to Left", "Top to Bottom", "Bottom to Top");
    }

    @Nullable
    public String getTimerRecordingDescription()
    {
        return getIndexedDescription(TAG_TIMER_RECORDING,
                "Off", "Time Lapse", "Stop-motion Animation");
    }

    @Nullable
    public String getHDRDescription()
    {
        Integer value = _directory.getInteger(TAG_HDR);
        if (value == null)
            return null;

        switch (value)
        {
            case 0:
                return "Off";
            case 100:
                return "1 EV";
            case 200:
                return "2 EV";
            case 300:
                return "3 EV";
            case 32868:
                return "1 EV (Auto)";
            case 32968:
                return "2 EV (Auto)";
            case 33068:
                return "3 EV (Auto)";
            default:
                return String.format("Unknown (%d)", value);
        }
    }

    @Nullable
    public String getShutterTypeDescription()
    {
        return getIndexedDescription(TAG_SHUTTER_TYPE,
                "Mechanical", "Electronic", "Hybrid");
    }

    @Nullable
    public String getTouchAeDescription()
    {
        return getIndexedDescription(TAG_TOUCH_AE,
                "Off", "On");
    }

    @Nullable
    public String getBabyNameDescription()
    {
        return trim(getStringFromBytes(TAG_BABY_NAME, Charsets.UTF_8));
    }

	@Nullable
    public String getLocationDescription()
    {
        return trim(getStringFromBytes(TAG_LOCATION, Charsets.UTF_8));
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
