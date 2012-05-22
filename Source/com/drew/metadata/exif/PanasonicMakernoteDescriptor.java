/*
 * Copyright 2002-2012 Drew Noakes
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
import com.drew.lang.BufferReader;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Age;
import com.drew.metadata.Face;
import com.drew.metadata.TagDescriptor;

import java.io.UnsupportedEncodingException;

/**
 * Provides human-readable string representations of tag values stored in a <code>PanasonicMakernoteDirectory</code>.
 * <p/>
 * Some information about this makernote taken from here:
 * <ul>
 * <li><a href="http://www.ozhiker.com/electronics/pjmt/jpeg_info/panasonic_mn.html">http://www.ozhiker.com/electronics/pjmt/jpeg_info/panasonic_mn.html</a></li>
 * <li><a href="http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/Panasonic.html">http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/Panasonic.html</a></li>
 * </ul>
 *
 * @author Drew Noakes http://drewnoakes.com, Philipp Sandhaus
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
            case PanasonicMakernoteDirectory.TAG_VERSION:
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
        byte[] values = _directory.getByteArray(PanasonicMakernoteDirectory.TAG_PRINT_IMAGE_MATCHING_INFO);
        if (values == null)
            return null;
        return "(" + values.length + " bytes)";
    }

    @Nullable
    public String getTextStampDescription()
    {
        return getOnOffDescription(PanasonicMakernoteDirectory.TAG_TEXT_STAMP);
    }

	@Nullable
    public String getTextStamp1Description()
    {
        return getOnOffDescription(PanasonicMakernoteDirectory.TAG_TEXT_STAMP_1);
    }

	@Nullable
    public String getTextStamp2Description()
    {
        return getOnOffDescription(PanasonicMakernoteDirectory.TAG_TEXT_STAMP_2);
    }

	@Nullable
    public String getTextStamp3Description()
    {
        return getOnOffDescription(PanasonicMakernoteDirectory.TAG_TEXT_STAMP_3);
    }

	@Nullable
    public String getMacroModeDescription()
    {
        return getOnOffDescription(PanasonicMakernoteDirectory.TAG_MACRO_MODE);
    }

    @Nullable
    public String getFlashFiredDescription()
    {
        return getOnOffDescription(PanasonicMakernoteDirectory.TAG_FLASH_FIRED);
    }

    @Nullable
    public String getImageStabilizationDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_IMAGE_STABILIZATION);
        if (value == null)
            return null;
        switch (value) {
            case 2:
                return "On, Mode 1";
            case 3:
                return "Off";
            case 4:
                return "On, Mode 2";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAudioDescription()
    {
        return getOnOffDescription(PanasonicMakernoteDirectory.TAG_AUDIO);
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

        BufferReader reader = new ByteArrayReader(values);

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
            return null   ;
        }
    }

    @Nullable
    public String getIntelligentExposureDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_INTELLIGENT_EXPOSURE);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "Off";
            case 1:
                return "Low";
            case 2:
                return "Standard";
            case 3:
                return "High";

            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFlashWarningDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_FLASH_WARNING);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "No";
            case 1:
                return "Yes (Flash required but disabled)";
            default:
                return "Unknown (" + value + ")";
        }
    }
	
    @Nullable
    public String getCountryDescription()
    {
        return getTextDescription(PanasonicMakernoteDirectory.TAG_COUNTRY);
    }

    @Nullable
    public String getStateDescription()
    {
        return getTextDescription(PanasonicMakernoteDirectory.TAG_STATE);
    }

    @Nullable
    public String getCityDescription()
    {
        return getTextDescription(PanasonicMakernoteDirectory.TAG_CITY);
    }

    @Nullable
    public String getLandmarkDescription()
    {
        return getTextDescription(PanasonicMakernoteDirectory.TAG_LANDMARK);
    }

	@Nullable
    public String getTitleDescription()
    {
        return getTextDescription(PanasonicMakernoteDirectory.TAG_TITLE);
    }

	@Nullable
    public String getBabyNameDescription()
    {
        return getTextDescription(PanasonicMakernoteDirectory.TAG_BABY_NAME);
    }

	@Nullable
    public String getLocationDescription()
    {
        return getTextDescription(PanasonicMakernoteDirectory.TAG_LOCATION);
    }

    @Nullable
    public String getIntelligentResolutionDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_INTELLIGENT_RESOLUTION);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "Off";
            case 2:
                return "Auto";
            case 3:
                return "On";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getContrastDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_CONTRAST);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "Normal";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getWorldTimeLocationDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_WORLD_TIME_LOCATION);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Home";
            case 2:
                return "Destination";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAdvancedSceneModeDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_ADVANCED_SCENE_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Normal";
            case 2:
                return "Outdoor/Illuminations/Flower/HDR Art";
            case 3:
                return "Indoor/Architecture/Objects/HDR B&W";
            case 4:
                return "Creative";
            case 5:
                return "Auto";
            case 7:
                return "Expressive";
            case 8:
                return "Retro";
            case 9:
                return "Pure";
            case 10:
                return "Elegant";
            case 12:
                return "Monochrome";
            case 13:
                return "Dynamic Art";
            case 14:
                return "Silhouette";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getUnknownDataDumpDescription()
    {
        byte[] value = _directory.getByteArray(PanasonicMakernoteDirectory.TAG_UNKNOWN_DATA_DUMP);
        if (value == null)
            return null;
        return "[" + value.length + " bytes]";
    }

    @Nullable
    public String getColorEffectDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_COLOR_EFFECT);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Off";
            case 2:
                return "Warm";
            case 3:
                return "Cool";
            case 4:
                return "Black & White";
            case 5:
                return "Sepia";
            default:
                return "Unknown (" + value + ")";
        }
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
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_BURST_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "Off";
            case 1:
                return "On";
            case 2:
                return "Infinite";
            case 4:
                return "Unlimited";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getContrastModeDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_CONTRAST_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 0x0:
                return "Normal";
            case 0x1:
                return "Low";
            case 0x2:
                return "High";
            case 0x6:
                return "Medium Low";
            case 0x7:
                return "Medium High";
            case 0x100:
                return "Low";
            case 0x110:
                return "Normal";
            case 0x120:
                return "High";

            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getNoiseReductionDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_NOISE_REDUCTION);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "Standard (0)";
            case 1:
                return "Low (-1)";
            case 2:
                return "High (+1)";
            case 3:
                return "Lowest (-2)";
            case 4:
                return "Highest (+2)";
            default:
                return "Unknown (" + value + ")";
        }
    }


    @Nullable
    public String getSelfTimerDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_SELF_TIMER);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Off";
            case 2:
                return "10 s";
            case 3:
                return "2 s";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getRotationDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_ROTATION);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Horizontal";
            case 3:
                return "Rotate 180";
            case 6:
                return "Rotate 90 CW";
            case 8:
                return "Rotate 270 CW";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getAfAssistLampDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_AF_ASSIST_LAMP);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Fired";
            case 2:
                return "Enabled but not used";
            case 3:
                return "Disabled but required";
            case 4:
                return "Disabled and not required";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getColorModeDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_COLOR_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return "Normal";
            case 1:
                return "Natural";
            case 2:
                return "Vivid";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getOpticalZoomModeDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_OPTICAL_ZOOM_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Standard";
            case 2:
                return "Extended";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getConversionLensDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_CONVERSION_LENS);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Off";
            case 2:
                return "Wide";
            case 3:
                return "Telephoto";
            case 4:
                return "Macro";
            default:
                return "Unknown (" + value + ")";
        }
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

    @Nullable
    public String getRecordModeDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_RECORD_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Normal";
            case 2:
                return "Portrait";
            case 3:
                return "Scenery";
            case 4:
                return "Sports";
            case 5:
                return "Night Portrait";
            case 6:
                return "Program";
            case 7:
                return "Aperture Priority";
            case 8:
                return "Shutter Priority";
            case 9:
                return "Macro";
            case 10:
                return "Spot";
            case 11:
                return "Manual";
            case 12:
                return "Movie Preview";
            case 13:
                return "Panning";
            case 14:
                return "Simple";
            case 15:
                return "Color Effects";
            case 16:
                return "Self Portrait";
            case 17:
                return "Economy";
            case 18:
                return "Fireworks";
            case 19:
                return "Party";
            case 20:
                return "Snow";
            case 21:
                return "Night Scenery";
            case 22:
                return "Food";
            case 23:
                return "Baby";
            case 24:
                return "Soft Skin";
            case 25:
                return "Candlelight";
            case 26:
                return "Starry Night";
            case 27:
                return "High Sensitivity";
            case 28:
                return "Panorama Assist";
            case 29:
                return "Underwater";
            case 30:
                return "Beach";
            case 31:
                return "Aerial Photo";
            case 32:
                return "Sunset";
            case 33:
                return "Pet";
            case 34:
                return "Intelligent ISO";
            case 35:
                return "Clipboard";
            case 36:
                return "High Speed Continuous Shooting";
            case 37:
                return "Intelligent Auto";
            case 39:
                return "Multi-aspect";
            case 41:
                return "Transform";
            case 42:
                return "Flash Burst";
            case 43:
                return "Pin Hole";
            case 44:
                return "Film Grain";
            case 45:
                return "My Color";
            case 46:
                return "Photo Frame";
            case 51:
                return "HDR";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getSceneModeDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_SCENE_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Normal";
            case 2:
                return "Portrait";
            case 3:
                return "Scenery";
            case 4:
                return "Sports";
            case 5:
                return "Night Portrait";
            case 6:
                return "Program";
            case 7:
                return "Aperture Priority";
            case 8:
                return "Shutter Priority";
            case 9:
                return "Macro";
            case 10:
                return "Spot";
            case 11:
                return "Manual";
            case 12:
                return "Movie Preview";
            case 13:
                return "Panning";
            case 14:
                return "Simple";
            case 15:
                return "Color Effects";
            case 16:
                return "Self Portrait";
            case 17:
                return "Economy";
            case 18:
                return "Fireworks";
            case 19:
                return "Party";
            case 20:
                return "Snow";
            case 21:
                return "Night Scenery";
            case 22:
                return "Food";
            case 23:
                return "Baby";
            case 24:
                return "Soft Skin";
            case 25:
                return "Candlelight";
            case 26:
                return "Starry Night";
            case 27:
                return "High Sensitivity";
            case 28:
                return "Panorama Assist";
            case 29:
                return "Underwater";
            case 30:
                return "Beach";
            case 31:
                return "Aerial Photo";
            case 32:
                return "Sunset";
            case 33:
                return "Pet";
            case 34:
                return "Intelligent ISO";
            case 35:
                return "Clipboard";
            case 36:
                return "High Speed Continuous Shooting";
            case 37:
                return "Intelligent Auto";
            case 39:
                return "Multi-aspect";
            case 41:
                return "Transform";
            case 42:
                return "Flash Burst";
            case 43:
                return "Pin Hole";
            case 44:
                return "Film Grain";
            case 45:
                return "My Color";
            case 46:
                return "Photo Frame";
            case 51:
                return "HDR";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getFocusModeDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_FOCUS_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Auto";
            case 2:
                return "Manual";
            case 4:
                return "Auto, Focus Button";
            case 5:
                return "Auto, Continuous";
            default:
                return "Unknown (" + value + ")";
        }
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
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_QUALITY_MODE);
        if (value == null)
            return null;
        switch (value) {
            case 2:
                return "High";
            case 3:
                return "Normal";
            case 6:
                return "Very High";
            case 7:
                return "Raw";
            case 9:
                return "Motion Picture";
            default:
                return "Unknown (" + value + ")";
        }
    }

    @Nullable
    public String getVersionDescription()
    {
        return convertBytesToVersionString(_directory.getIntArray(PanasonicMakernoteDirectory.TAG_VERSION), 2);
    }

    @Nullable
    public String getMakernoteVersionDescription()
    {
        return convertBytesToVersionString(_directory.getIntArray(PanasonicMakernoteDirectory.TAG_MAKERNOTE_VERSION), 2);
    }

    @Nullable
    public String getExifVersionDescription()
    {
        return convertBytesToVersionString(_directory.getIntArray(PanasonicMakernoteDirectory.TAG_EXIF_VERSION), 2);
    }

    @Nullable
    public String getInternalSerialNumberDescription()
    {
        final byte[] bytes = _directory.getByteArray(PanasonicMakernoteDirectory.TAG_INTERNAL_SERIAL_NUMBER);

        if (bytes==null)
            return null;

        int length = bytes.length;
        for (int index = 0; index < bytes.length; index++) {
            int i = bytes[index] & 0xFF;
            if (i == 0 || i > 0x7F) {
                length = index;
                break;
            }
        }

        return new String(bytes, 0, length);
    }

    @Nullable
    public String getWhiteBalanceDescription()
    {
        Integer value = _directory.getInteger(PanasonicMakernoteDirectory.TAG_WHITE_BALANCE);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Auto";
            case 2:
                return "Daylight";
            case 3:
                return "Cloudy";
            case 4:
                return "Incandescent";
            case 5:
                return "Manual";
            case 8:
                return "Flash";
            case 10:
                return "Black & White";
            case 11:
                return "Manual";
            case 12:
                return "Shade";
            default:
                return "Unknown (" + value + ")";
        }
    }

	@Nullable
	public String getBabyAgeDescription()
    {
        final Age age = _directory.getAge(PanasonicMakernoteDirectory.TAG_BABY_AGE);
        if (age==null)
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

	@Nullable
	private String getTextDescription(int tag)
    {
		byte[] values = _directory.getByteArray(tag);
        if (values == null)
            return null;
        try {
            return new String(values, "ASCII").trim();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
	}

    @Nullable
    private String getOnOffDescription(int tag)
    {
        Integer value = _directory.getInteger(tag);
        if (value == null)
            return null;
        switch (value) {
            case 1:
                return "Off";
            case 2:
                return "On";
            default:
                return "Unknown (" + value + ")";
        }
    }
}
