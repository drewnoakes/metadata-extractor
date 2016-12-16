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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;
import java.util.HashMap;

import static com.drew.metadata.exif.makernotes.OlympusEquipmentMakernoteDirectory.*;

/**
 * Provides human-readable String representations of tag values stored in a {@link OlympusEquipmentMakernoteDirectory}.
 * <p>
 * Some Description functions and the Extender and Lens types lists converted from Exiftool version 10.10 created by Phil Harvey
 * http://www.sno.phy.queensu.ca/~phil/exiftool/
 * lib\Image\ExifTool\Olympus.pm
 *
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class OlympusEquipmentMakernoteDescriptor extends TagDescriptor<OlympusEquipmentMakernoteDirectory>
{
    public OlympusEquipmentMakernoteDescriptor(@NotNull OlympusEquipmentMakernoteDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case TAG_EQUIPMENT_VERSION:
                return getEquipmentVersionDescription();
            case TAG_CAMERA_TYPE_2:
                return getCameraType2Description();
            case TAG_FOCAL_PLANE_DIAGONAL:
                return getFocalPlaneDiagonalDescription();
            case TAG_BODY_FIRMWARE_VERSION:
                return getBodyFirmwareVersionDescription();
            case TAG_LENS_TYPE:
                return getLensTypeDescription();
            case TAG_LENS_FIRMWARE_VERSION:
                return getLensFirmwareVersionDescription();
            case TAG_MAX_APERTURE_AT_MIN_FOCAL:
                return getMaxApertureAtMinFocalDescription();
            case TAG_MAX_APERTURE_AT_MAX_FOCAL:
                return getMaxApertureAtMaxFocalDescription();
            case TAG_MAX_APERTURE:
                return getMaxApertureDescription();
            case TAG_LENS_PROPERTIES:
                return getLensPropertiesDescription();
            case TAG_EXTENDER:
                return getExtenderDescription();
            case TAG_FLASH_TYPE:
                return getFlashTypeDescription();
            case TAG_FLASH_MODEL:
                return getFlashModelDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getEquipmentVersionDescription()
    {
        return getVersionBytesDescription(TAG_EQUIPMENT_VERSION, 4);
    }

    @Nullable
    public String getCameraType2Description()
    {
        String cameratype = _directory.getString(TAG_CAMERA_TYPE_2);
        if(cameratype == null)
            return null;

        if(OlympusMakernoteDirectory.OlympusCameraTypes.containsKey(cameratype))
            return OlympusMakernoteDirectory.OlympusCameraTypes.get(cameratype);

        return cameratype;
    }

    @Nullable
    public String getFocalPlaneDiagonalDescription()
    {
        return _directory.getString(TAG_FOCAL_PLANE_DIAGONAL) + " mm";
    }

    @Nullable
    public String getBodyFirmwareVersionDescription()
    {
        Integer value = _directory.getInteger(TAG_BODY_FIRMWARE_VERSION);
        if (value == null)
            return null;

        String hex = String.format("%04X", value);
        return String.format("%s.%s",
            hex.substring(0, hex.length() - 3),
            hex.substring(hex.length() - 3));
    }

    @Nullable
    public String getLensTypeDescription()
    {
        String str = _directory.getString(TAG_LENS_TYPE);

        if (str == null)
            return null;

        // The String contains six numbers:
        //
        // - Make
        // - Unknown
        // - Model
        // - Sub-model
        // - Unknown
        // - Unknown
        //
        // Only the Make, Model and Sub-model are used to identify the lens type
        String[] values = str.split(" ");

        if (values.length < 6)
            return null;

        try {
            int num1 = Integer.parseInt(values[0]);
            int num2 = Integer.parseInt(values[2]);
            int num3 = Integer.parseInt(values[3]);
            return _olympusLensTypes.get(String.format("%X %02X %02X", num1, num2, num3));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public String getLensFirmwareVersionDescription()
    {
        Integer value = _directory.getInteger(TAG_LENS_FIRMWARE_VERSION);
        if (value == null)
            return null;

        String hex = String.format("%04X", value);
        return String.format("%s.%s",
            hex.substring(0, hex.length() - 3),
            hex.substring(hex.length() - 3));
    }

    @Nullable
    public String getMaxApertureAtMinFocalDescription()
    {
        Integer value = _directory.getInteger(TAG_MAX_APERTURE_AT_MIN_FOCAL);
        if (value == null)
            return null;

        DecimalFormat format = new DecimalFormat("0.#");
        return format.format(CalcMaxAperture(value));
    }

    @Nullable
    public String getMaxApertureAtMaxFocalDescription()
    {
        Integer value = _directory.getInteger(TAG_MAX_APERTURE_AT_MAX_FOCAL);
        if (value == null)
            return null;

        DecimalFormat format = new DecimalFormat("0.#");
        return format.format(CalcMaxAperture(value));
    }

    @Nullable
    public String getMaxApertureDescription()
    {
        Integer value = _directory.getInteger(TAG_MAX_APERTURE);
        if (value == null)
            return null;

        DecimalFormat format = new DecimalFormat("0.#");
        return format.format(CalcMaxAperture(value));
    }

    private static double CalcMaxAperture(int value)
    {
        return Math.pow(Math.sqrt(2.00), value / 256.0);
    }

    @Nullable
    public String getLensPropertiesDescription()
    {
        Integer value = _directory.getInteger(TAG_LENS_PROPERTIES);
        if (value == null)
            return null;

        return String.format("0x%04X", value);
    }

    @Nullable
    public String getExtenderDescription()
    {
        String str = _directory.getString(TAG_EXTENDER);

        if (str == null)
            return null;

        // The String contains six numbers:
        //
        // - Make
        // - Unknown
        // - Model
        // - Sub-model
        // - Unknown
        // - Unknown
        //
        // Only the Make and Model are used to identify the extender
        String[] values = str.split(" ");

        if (values.length < 6)
            return null;

        try {
            int num1 = Integer.parseInt(values[0]);
            int num2 = Integer.parseInt(values[2]);
            String extenderType = String.format("%X %02X", num1, num2);
            return _olympusExtenderTypes.get(extenderType);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public String getFlashTypeDescription()
    {
        return getIndexedDescription(TAG_FLASH_TYPE,
            "None", null, "Simple E-System", "E-System");
    }

    @Nullable
    public String getFlashModelDescription()
    {
        return getIndexedDescription(TAG_FLASH_MODEL,
            "None", "FL-20", "FL-50", "RF-11", "TF-22", "FL-36", "FL-50R", "FL-36R");
    }

    private static final HashMap<String, String> _olympusLensTypes = new HashMap<String, String>();
    private static final HashMap<String, String> _olympusExtenderTypes = new HashMap<String, String>();

    static {
        _olympusLensTypes.put("0 00 00", "None");
        // Olympus lenses (also Kenko Tokina)
        _olympusLensTypes.put("0 01 00", "Olympus Zuiko Digital ED 50mm F2.0 Macro");
        _olympusLensTypes.put("0 01 01", "Olympus Zuiko Digital 40-150mm F3.5-4.5"); //8
        _olympusLensTypes.put("0 01 10", "Olympus M.Zuiko Digital ED 14-42mm F3.5-5.6"); //PH (E-P1 pre-production)
        _olympusLensTypes.put("0 02 00", "Olympus Zuiko Digital ED 150mm F2.0");
        _olympusLensTypes.put("0 02 10", "Olympus M.Zuiko Digital 17mm F2.8 Pancake"); //PH (E-P1 pre-production)
        _olympusLensTypes.put("0 03 00", "Olympus Zuiko Digital ED 300mm F2.8");
        _olympusLensTypes.put("0 03 10", "Olympus M.Zuiko Digital ED 14-150mm F4.0-5.6 [II]"); //11 (The second version of this lens seems to have the same lens ID number as the first version #20)
        _olympusLensTypes.put("0 04 10", "Olympus M.Zuiko Digital ED 9-18mm F4.0-5.6"); //11
        _olympusLensTypes.put("0 05 00", "Olympus Zuiko Digital 14-54mm F2.8-3.5");
        _olympusLensTypes.put("0 05 01", "Olympus Zuiko Digital Pro ED 90-250mm F2.8"); //9
        _olympusLensTypes.put("0 05 10", "Olympus M.Zuiko Digital ED 14-42mm F3.5-5.6 L"); //11 (E-PL1)
        _olympusLensTypes.put("0 06 00", "Olympus Zuiko Digital ED 50-200mm F2.8-3.5");
        _olympusLensTypes.put("0 06 01", "Olympus Zuiko Digital ED 8mm F3.5 Fisheye"); //9
        _olympusLensTypes.put("0 06 10", "Olympus M.Zuiko Digital ED 40-150mm F4.0-5.6"); //PH
        _olympusLensTypes.put("0 07 00", "Olympus Zuiko Digital 11-22mm F2.8-3.5");
        _olympusLensTypes.put("0 07 01", "Olympus Zuiko Digital 18-180mm F3.5-6.3"); //6
        _olympusLensTypes.put("0 07 10", "Olympus M.Zuiko Digital ED 12mm F2.0"); //PH
        _olympusLensTypes.put("0 08 01", "Olympus Zuiko Digital 70-300mm F4.0-5.6"); //7 (seen as release 1 - PH)
        _olympusLensTypes.put("0 08 10", "Olympus M.Zuiko Digital ED 75-300mm F4.8-6.7"); //PH
        _olympusLensTypes.put("0 09 10", "Olympus M.Zuiko Digital 14-42mm F3.5-5.6 II"); //PH (E-PL2)
        _olympusLensTypes.put("0 10 01", "Kenko Tokina Reflex 300mm F6.3 MF Macro"); //20
        _olympusLensTypes.put("0 10 10", "Olympus M.Zuiko Digital ED 12-50mm F3.5-6.3 EZ"); //PH
        _olympusLensTypes.put("0 11 10", "Olympus M.Zuiko Digital 45mm F1.8"); //17
        _olympusLensTypes.put("0 12 10", "Olympus M.Zuiko Digital ED 60mm F2.8 Macro"); //20
        _olympusLensTypes.put("0 13 10", "Olympus M.Zuiko Digital 14-42mm F3.5-5.6 II R"); //PH/20
        _olympusLensTypes.put("0 14 10", "Olympus M.Zuiko Digital ED 40-150mm F4.0-5.6 R"); //19
        // '0 14 10.1", "Olympus M.Zuiko Digital ED 14-150mm F4.0-5.6 II"); //11 (questionable & unconfirmed -- all samples I can find are '0 3 10' - PH)
        _olympusLensTypes.put("0 15 00", "Olympus Zuiko Digital ED 7-14mm F4.0");
        _olympusLensTypes.put("0 15 10", "Olympus M.Zuiko Digital ED 75mm F1.8"); //PH
        _olympusLensTypes.put("0 16 10", "Olympus M.Zuiko Digital 17mm F1.8"); //20
        _olympusLensTypes.put("0 17 00", "Olympus Zuiko Digital Pro ED 35-100mm F2.0"); //7
        _olympusLensTypes.put("0 18 00", "Olympus Zuiko Digital 14-45mm F3.5-5.6");
        _olympusLensTypes.put("0 18 10", "Olympus M.Zuiko Digital ED 75-300mm F4.8-6.7 II"); //20
        _olympusLensTypes.put("0 19 10", "Olympus M.Zuiko Digital ED 12-40mm F2.8 Pro"); //PH
        _olympusLensTypes.put("0 20 00", "Olympus Zuiko Digital 35mm F3.5 Macro"); //9
        _olympusLensTypes.put("0 20 10", "Olympus M.Zuiko Digital ED 40-150mm F2.8 Pro"); //20
        _olympusLensTypes.put("0 21 10", "Olympus M.Zuiko Digital ED 14-42mm F3.5-5.6 EZ"); //20
        _olympusLensTypes.put("0 22 00", "Olympus Zuiko Digital 17.5-45mm F3.5-5.6"); //9
        _olympusLensTypes.put("0 22 10", "Olympus M.Zuiko Digital 25mm F1.8"); //20
        _olympusLensTypes.put("0 23 00", "Olympus Zuiko Digital ED 14-42mm F3.5-5.6"); //PH
        _olympusLensTypes.put("0 23 10", "Olympus M.Zuiko Digital ED 7-14mm F2.8 Pro"); //20
        _olympusLensTypes.put("0 24 00", "Olympus Zuiko Digital ED 40-150mm F4.0-5.6"); //PH
        _olympusLensTypes.put("0 24 10", "Olympus M.Zuiko Digital ED 300mm F4.0 IS Pro"); //20
        _olympusLensTypes.put("0 25 10", "Olympus M.Zuiko Digital ED 8mm F1.8 Fisheye Pro"); //20
        _olympusLensTypes.put("0 30 00", "Olympus Zuiko Digital ED 50-200mm F2.8-3.5 SWD"); //7
        _olympusLensTypes.put("0 31 00", "Olympus Zuiko Digital ED 12-60mm F2.8-4.0 SWD"); //7
        _olympusLensTypes.put("0 32 00", "Olympus Zuiko Digital ED 14-35mm F2.0 SWD"); //PH
        _olympusLensTypes.put("0 33 00", "Olympus Zuiko Digital 25mm F2.8"); //PH
        _olympusLensTypes.put("0 34 00", "Olympus Zuiko Digital ED 9-18mm F4.0-5.6"); //7
        _olympusLensTypes.put("0 35 00", "Olympus Zuiko Digital 14-54mm F2.8-3.5 II"); //PH
        // Sigma lenses
        _olympusLensTypes.put("1 01 00", "Sigma 18-50mm F3.5-5.6 DC"); //8
        _olympusLensTypes.put("1 01 10", "Sigma 30mm F2.8 EX DN"); //20
        _olympusLensTypes.put("1 02 00", "Sigma 55-200mm F4.0-5.6 DC");
        _olympusLensTypes.put("1 02 10", "Sigma 19mm F2.8 EX DN"); //20
        _olympusLensTypes.put("1 03 00", "Sigma 18-125mm F3.5-5.6 DC");
        _olympusLensTypes.put("1 03 10", "Sigma 30mm F2.8 DN | A"); //20
        _olympusLensTypes.put("1 04 00", "Sigma 18-125mm F3.5-5.6 DC"); //7
        _olympusLensTypes.put("1 04 10", "Sigma 19mm F2.8 DN | A"); //20
        _olympusLensTypes.put("1 05 00", "Sigma 30mm F1.4 EX DC HSM"); //10
        _olympusLensTypes.put("1 05 10", "Sigma 60mm F2.8 DN | A"); //20
        _olympusLensTypes.put("1 06 00", "Sigma APO 50-500mm F4.0-6.3 EX DG HSM"); //6
        _olympusLensTypes.put("1 07 00", "Sigma Macro 105mm F2.8 EX DG"); //PH
        _olympusLensTypes.put("1 08 00", "Sigma APO Macro 150mm F2.8 EX DG HSM"); //PH
        _olympusLensTypes.put("1 09 00", "Sigma 18-50mm F2.8 EX DC Macro"); //20
        _olympusLensTypes.put("1 10 00", "Sigma 24mm F1.8 EX DG Aspherical Macro"); //PH
        _olympusLensTypes.put("1 11 00", "Sigma APO 135-400mm F4.5-5.6 DG"); //11
        _olympusLensTypes.put("1 12 00", "Sigma APO 300-800mm F5.6 EX DG HSM"); //11
        _olympusLensTypes.put("1 13 00", "Sigma 30mm F1.4 EX DC HSM"); //11
        _olympusLensTypes.put("1 14 00", "Sigma APO 50-500mm F4.0-6.3 EX DG HSM"); //11
        _olympusLensTypes.put("1 15 00", "Sigma 10-20mm F4.0-5.6 EX DC HSM"); //11
        _olympusLensTypes.put("1 16 00", "Sigma APO 70-200mm F2.8 II EX DG Macro HSM"); //11
        _olympusLensTypes.put("1 17 00", "Sigma 50mm F1.4 EX DG HSM"); //11
        // Panasonic/Leica lenses
        _olympusLensTypes.put("2 01 00", "Leica D Vario Elmarit 14-50mm F2.8-3.5 Asph."); //11
        _olympusLensTypes.put("2 01 10", "Lumix G Vario 14-45mm F3.5-5.6 Asph. Mega OIS"); //16
        _olympusLensTypes.put("2 02 00", "Leica D Summilux 25mm F1.4 Asph."); //11
        _olympusLensTypes.put("2 02 10", "Lumix G Vario 45-200mm F4.0-5.6 Mega OIS"); //16
        _olympusLensTypes.put("2 03 00", "Leica D Vario Elmar 14-50mm F3.8-5.6 Asph. Mega OIS"); //11
        _olympusLensTypes.put("2 03 01", "Leica D Vario Elmar 14-50mm F3.8-5.6 Asph."); //14 (L10 kit)
        _olympusLensTypes.put("2 03 10", "Lumix G Vario HD 14-140mm F4.0-5.8 Asph. Mega OIS"); //16
        _olympusLensTypes.put("2 04 00", "Leica D Vario Elmar 14-150mm F3.5-5.6"); //13
        _olympusLensTypes.put("2 04 10", "Lumix G Vario 7-14mm F4.0 Asph."); //PH (E-P1 pre-production)
        _olympusLensTypes.put("2 05 10", "Lumix G 20mm F1.7 Asph."); //16
        _olympusLensTypes.put("2 06 10", "Leica DG Macro-Elmarit 45mm F2.8 Asph. Mega OIS"); //PH
        _olympusLensTypes.put("2 07 10", "Lumix G Vario 14-42mm F3.5-5.6 Asph. Mega OIS"); //20
        _olympusLensTypes.put("2 08 10", "Lumix G Fisheye 8mm F3.5"); //PH
        _olympusLensTypes.put("2 09 10", "Lumix G Vario 100-300mm F4.0-5.6 Mega OIS"); //11
        _olympusLensTypes.put("2 10 10", "Lumix G 14mm F2.5 Asph."); //17
        _olympusLensTypes.put("2 11 10", "Lumix G 12.5mm F12 3D"); //20 (H-FT012)
        _olympusLensTypes.put("2 12 10", "Leica DG Summilux 25mm F1.4 Asph."); //20
        _olympusLensTypes.put("2 13 10", "Lumix G X Vario PZ 45-175mm F4.0-5.6 Asph. Power OIS"); //20
        _olympusLensTypes.put("2 14 10", "Lumix G X Vario PZ 14-42mm F3.5-5.6 Asph. Power OIS"); //20
        _olympusLensTypes.put("2 15 10", "Lumix G X Vario 12-35mm F2.8 Asph. Power OIS"); //PH
        _olympusLensTypes.put("2 16 10", "Lumix G Vario 45-150mm F4.0-5.6 Asph. Mega OIS"); //20
        _olympusLensTypes.put("2 17 10", "Lumix G X Vario 35-100mm F2.8 Power OIS"); //PH
        _olympusLensTypes.put("2 18 10", "Lumix G Vario 14-42mm F3.5-5.6 II Asph. Mega OIS"); //20
        _olympusLensTypes.put("2 19 10", "Lumix G Vario 14-140mm F3.5-5.6 Asph. Power OIS"); //20
        _olympusLensTypes.put("2 20 10", "Lumix G Vario 12-32mm F3.5-5.6 Asph. Mega OIS"); //20
        _olympusLensTypes.put("2 21 10", "Leica DG Nocticron 42.5mm F1.2 Asph. Power OIS"); //20
        _olympusLensTypes.put("2 22 10", "Leica DG Summilux 15mm F1.7 Asph."); //20
        // '2 23 10", "Lumix G Vario 35-100mm F4.0-5.6 Asph. Mega OIS"); //20 (guess)
        _olympusLensTypes.put("2 24 10", "Lumix G Macro 30mm F2.8 Asph. Mega OIS"); //20
        _olympusLensTypes.put("2 25 10", "Lumix G 42.5mm F1.7 Asph. Power OIS"); //20
        _olympusLensTypes.put("3 01 00", "Leica D Vario Elmarit 14-50mm F2.8-3.5 Asph."); //11
        _olympusLensTypes.put("3 02 00", "Leica D Summilux 25mm F1.4 Asph."); //11
        // Tamron lenses
        _olympusLensTypes.put("5 01 10", "Tamron 14-150mm F3.5-5.8 Di III"); //20 (model C001)


        _olympusExtenderTypes.put("0 00", "None");
        _olympusExtenderTypes.put("0 04", "Olympus Zuiko Digital EC-14 1.4x Teleconverter");
        _olympusExtenderTypes.put("0 08", "Olympus EX-25 Extension Tube");
        _olympusExtenderTypes.put("0 10", "Olympus Zuiko Digital EC-20 2.0x Teleconverter");
    }
}
