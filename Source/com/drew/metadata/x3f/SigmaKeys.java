package com.drew.metadata.x3f;

import com.drew.metadata.Directory;
import com.drew.metadata.Key;
import com.drew.metadata.MetaUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anthony Mandra http://anthonymandra.com
 * @author Drew Noakes https://drewnoakes.com
 */
public enum SigmaKeys implements Key
{
    TAG_AUTO_EXPOSURE_MODE("AEMODE", 1, "Auto Exposure Mode")
        {
            @Override
            public String getDescription(Directory directory)
            {
                String value = directory.getString(this.getInt());

                if (value == null)
                    return null;

                if ("8".equals(value))
                    return "8 segment";
                else if ("C".equals(value))
                    return "Center Weighted";
                else if ("A".equals(value))
                    return "Average Weighted";

                return "Unknown auto exposure mode.";
            }
        },
    TAG_AUTO_FOCUS_MODE("AFMODE", 2, "Auto Focus Mode")
        {
            @Override
            public String getDescription(Directory directory)
            {
                String value = directory.getString(this.getInt());

                if (value == null)
                    return null;

                if ("AF-S".equals(value))
                    return "Single Focus";
                else if ("AF-C".equals(value))
                    return "Continuous Focus";
                else if ("MF".equals(value))
                    return "Manual Focus";

                return "Unknown auto focus mode.";
            }
        },
    TAG_APERTURE_EXACT("APERTURE", 3, "Exact Aperture"),        //Looks like aper. is f-stop
    TAG_APERTURE_SIMPLE("AP_DESC", 4, "Simplified Aperture"),   //Looks like aper. is f-stop
    TAG_BRACKET_INDEX("BRACKET", 4, "Bracket Index"),
    TAG_BURST_INDEX("BURST", 5, "Burst Index"),
    TAG_MAKE("CAMMANUF", 6, "Camera Make"),
    TAG_MODEL("CAMMODEL", 7, "Camera Model"),   // often contains make
    TAG_NAME("CAMNAME", 8, "Camera Name"),
    TAG_SERIAL("CAMSERIAL", 9, "Camera Serial"),
    TAG_CM_DESC("CM_DESC", 10, "CM_DESC"),              // Image setting, string
    TAG_COLORSPACE("COLORSPACE", 11, "Color space"),    // string
    TAG_CONT_DESC("CONT_DESC", 12, "CONT_DESC"),        // unknown, 0.0 in example
    TAG_DARKTEMP("DARKTEMP", 13, "DARKTEMP"),           // unknown, -1 in example
    TAG_DRIVE("DRIVE", 14, "Drive Mode")
        {
            @Override
            public String getDescription(Directory directory)
            {
                String value = directory.getString(this.getInt());

                if (value == null)
                    return null;

                if ("SINGLE".equals(value))
                    return "Single Shot";
                else if ("MULTI".equals(value))
                    return "Multi Shot";
                else if ("2S".equals(value))
                    return "2 Second timer";
                else if ("10S".equals(value))
                    return "10 Second timer";
                else if ("UP".equals(value))
                    return "Mirror up";
                else if ("AB".equals(value))
                    return "Auto Bracket";
                else if ("OFF".equals(value))
                    return "Off";

                return "Unknown drive mode.";
            }
        },
    TAG_EXPOSURE_COMP("EXPCOMP", 15, "Exposure Compensation"),  // String looks fine
    TAG_EXPOSURE_COMP_NET("EXPNET", 16, "Total Exposure Compensation"), // String looks fine
    TAG_EXPOSURE_TIME("EXPTIME", 17, "Exposure Time")
        {
            @Override
            public String getDescription(Directory directory)
            {
                Integer value = directory.getInteger(this.getInt());
                return value == null ? null : value /*micro*/ / 1000000  + " sec";
            }
        },
    TAG_FIRMWARE_VER("FIRMVERS", 18, "Firmware Version"),
    TAG_FLASH("FLASH", 19, "Flash"),
    TAG_FLASHPOWER("FLASHPOWER", 20, "Flash Power"),    //255 in example which says to me indexed values...
    TAG_FOCAL_LENGTH("FLENGTH", 21, "Focal Length"),    //string should be fine
    TAG_FOCAL_LENGTH_35EQ("FLEQ35MM", 22, "35mm Equivalent Focal Length"),  // string should be fine
    TAG_FOCUS("FOCUS", 23, "Focus Mode")
        {
            @Override
            public String getDescription(Directory directory)
            {
                String value = directory.getString(this.getInt());

                if (value == null)
                    return null;

                if ("AF".equals(value))
                    return "Auto Focus Locked";
                else if ("NO LOCK".equals(value))
                    return "No Auto Focus Lock";
                else if ("M".equals(value))
                    return "Manual";

                return "Unknown focus mode.";
            }
        },
    TAG_FPGAVERS("FPGAVERS", 24, "FPGAVERS"),   // field programmable gate array version??? 0x1024 in example
    TAG_IMAGER_BOARD_ID("IDIMAGEBOARDID", 25, "Imager Board ID"), //string should be fine
    TAG_IMAGER_TEMP("IMAGERTEMP", 26, "Imager Board Temperature"), //string should be fine
    TAG_ISO(" ISO", 27, " ISO"),    //string should be fine
    TAG_LENS_APERTURE_RANGE("LENSARANGE", 28, "Lens Aperture Range"),   //string should be fine
    TAG_LENS_FOCAL_RANGE("LENSFRANGE", 29, "Lens Focal Range"),         //string should be fine
    TAG_LENS_MODEL("LENSMODEL", 30, "Lens Model")
        {
            @Override
            public String getDescription(Directory directory)
            {
                String hexId = directory.getString(this.getInt());
                if (!hexId.startsWith("0x"))
                    hexId = "0x" + hexId;
                Integer id = Integer.decode(hexId);
                String lens = getLens(id);
                return lens == null ? "Unknown lens" : lens;
            }
        },
    TAG_SHOOTING_MODE("PMODE", 31, "Shooting Mode")
        {
            @Override
            public String getDescription(Directory directory)
            {
                String value = directory.getString(this.getInt());

                if (value == null)
                    return null;

                if ("P".equals(value))
                    return "Program";
                else if ("A".equals(value))
                    return "Aperture Priority";
                else if ("S".equals(value))
                    return "Shutter Priority";
                else if ("M".equals(value))
                    return "Manual";

                return "Unknown program mode.";
            }
        },
    TAG_RESOLUTION_SETTING("RESOLUTION", 32, "Resolution Setting"), //String should be fine
    TAG_ROTATION("ROTATION", 33, "Rotation"),    // TODO: AJM, not sure what the model is, pretty sure this'll be in exif anyway
    TAG_SATU_DESC("SATU_DESC", 34, "Saturation"),   // unknown, 0.0 in example
    TAG_SENSOR_ID("SENSORID", 35, "Sensor ID"), //string is fine
    TAG_SHARP_DESC("SHARP_DESC", 36, "Sharpness Setting"),  //0.0 in example
    TAG_SHUTTER_EXACT("SHUTTER", 37, "Exact Shutter Speed"),    // We'll leave this for now
    TAG_SHUTTER_SIMPLE("SH_DESC", 38, "Simplified Shutter speed")
        {
            @Override
            public String getDescription(Directory directory)
            {
                Float apexValue = directory.getFloatObject(getInt());
                return MetaUtil.getShutterSpeedDescription(apexValue);
            }
        },
    TAG_TELECONV("TELECONV", 39, "TELECONV"),   //unknown
    TAG_TIME("TIME", 40, "Capture Time (Unix UTC)")
        {
            @Override
            public String getDescription(Directory directory)
            {
                Long value = directory.getLongObject(getInt());
                if (value==null)
                    return null;
                return new Date(value).toString();
            }
        },
    TAG_WHITE_BALANCE("WB_DESC", 41, "White Balance Mode"); // Blank in my example

    private final int index;    // for backwards compatibility
    private final String key;
    private final String summary;

    SigmaKeys(String key, int index, String summary)
    {
        this.index = index;
        this.key = key;
        this.summary = summary;
    }

    //TODO: Use a sparse array trie, or FastUtil
    private static final Map<Integer, SigmaKeys> intLookup = new HashMap<Integer, SigmaKeys>();
    static {
        for (SigmaKeys type : values())
            intLookup.put(type.getInt(), type);
    }

    @Override
    public String getName()
    {
        return name();
    }

    @Override
    public String getSummary()
    {
        return summary;
    }

    @Override
    public String getDescription(Directory directory)
    {
        return directory.getDescription(getInt());
    }

    @Override
    public Object getValue()
    {
        return key;
    }

    @Override
    public int getInt()
    {
        return index;
    }

    //TODO: Would be cleaner to have a meta class
    private static final HashMap<Integer, String> _lensTypeById = new HashMap<Integer, String>();

    static
    {
        // AJM: Pulled from exiftool, how is he processing the decimal indices?

        // 0x0 => "Sigma 50mm F2.8 EX Macro", (0x0 used for other lenses too)
        // 0x8 - 18-125mm LENSARANGE@18mm=22-4
        _lensTypeById.put(0x10, "Sigma 50mm F2.8 EX DG MACRO");
            // (0x10 = 16)
//        16.1 => "Sigma 70mm F2.8 EX DG Macro");
//        16.2 => "Sigma 105mm F2.8 EX DG Macro");
        _lensTypeById.put(0x16, "Sigma 18-50mm F3.5-5.6 DC"); //PH
        _lensTypeById.put(0x103, "Sigma 180mm F3.5 EX IF HSM APO Macro");
        _lensTypeById.put(0x104, "Sigma 150mm F2.8 EX DG HSM APO Macro");
        _lensTypeById.put(0x105, "Sigma 180mm F3.5 EX DG HSM APO Macro");
        _lensTypeById.put(0x106, "Sigma 150mm F2.8 EX DG OS HSM APO Macro");
        _lensTypeById.put(0x107, "Sigma 180mm F2.8 EX DG OS HSM APO Macro");
        // (0x129 = 297)
        _lensTypeById.put(0x129, "Sigma Lens (0x129)"); //PH
//        297.1, "Sigma 14mm F2.8 EX Aspherical"); //PH
//        297.2, "Sigma 30mm F1.4");
        // (0x131 = 305)
        _lensTypeById.put(0x131, "Sigma Lens (0x131)");
//        305.1, "Sigma 17-70mm F2.8-4.5 DC Macro"); //PH
//        305.2, "Sigma 70-200mm F2.8 APO EX HSM");
//        305.3, "Sigma 120-300mm F2.8 APO EX IF HSM");
        _lensTypeById.put(0x134, "Sigma 100-300mm F4 EX DG HSM APO");
        _lensTypeById.put(0x135, "Sigma 120-300mm F2.8 EX DG HSM APO");
        _lensTypeById.put(0x136, "Sigma 120-300mm F2.8 EX DG OS HSM APO");
        _lensTypeById.put(0x137, "Sigma 120-300mm F2.8 DG OS HSM | S");
        _lensTypeById.put(0x143, "Sigma 600mm F8 Mirror");
        // (0x145 = 325)
        _lensTypeById.put(0x145, "Sigma Lens (0x145)"); //PH
//        325.1, "Sigma 15-30mm F3.5-4.5 EX DG Aspherical"); //PH
//        325.2, "Sigma 18-50mm F2.8 EX DG"); //PH (NC)
//        325.3, "Sigma 20-40mm F2.8 EX DG"); //PH
        _lensTypeById.put(0x150, "Sigma 30mm F1.4 DC HSM");
        // (0x152 = 338)
        _lensTypeById.put(0x152, "Sigma Lens (0x152)");
//        338.1, "Sigma APO 800mm F5.6 EX DG HSM");
//        338.2, "Sigma 12-24mm F4.5-5.6 EX DG ASP HSM");
//        338.3, "Sigma 10-20mm F4-5.6 EX DC HSM");
        _lensTypeById.put(0x165, "Sigma 70-200mm F2.8 EX"); // ...but what specific model?:
        // 70-200mm F2.8 EX APO - Original version, minimum focus distance 1.8m (1999)
        // 70-200mm F2.8 EX DG - Adds "digitally optimized" lens coatings to reduce flare (2005)
        // 70-200mm F2.8 EX DG Macro (HSM) - Minimum focus distance reduced to 1m (2006)
        // 70-200mm F2.8 EX DG Macro HSM II - Improved optical performance (2007)
        _lensTypeById.put(0x169, "Sigma 18-50mm F2.8 EX DC"); //PH (NC)
        _lensTypeById.put(0x183, "Sigma 500mm F4.5 EX HSM APO");
        _lensTypeById.put(0x184, "Sigma 500mm F4.5 EX DG HSM APO");
        _lensTypeById.put(0x194, "Sigma 300mm F2.8 EX HSM APO");
        _lensTypeById.put(0x195, "Sigma 300mm F2.8 EX DG HSM APO");
        _lensTypeById.put(0x200, "Sigma 12-24mm F4.5-5.6 EX DG ASP HSM");
        _lensTypeById.put(0x201, "Sigma 10-20mm F4-5.6 EX DC HSM");
        _lensTypeById.put(0x202, "Sigma 10-20mm F3.5 EX DC HSM");
        _lensTypeById.put(0x203, "Sigma 8-16mm F4.5-5.6 DC HSM");
        _lensTypeById.put(0x204, "Sigma 12-24mm F4.5-5.6 DG HSM II");
        _lensTypeById.put(0x210, "Sigma 18-35mm F1.8 DC HSM | A");
        _lensTypeById.put(0x256, "Sigma 105mm F2.8 EX Macro");
        _lensTypeById.put(0x257, "Sigma 105mm F2.8 EX DG Macro");
        _lensTypeById.put(0x258, "Sigma 105mm F2.8 EX DG OS HSM Macro");
        _lensTypeById.put(0x270, "Sigma 70mm F2.8 EX DG Macro"); //NJ (SD1)
        _lensTypeById.put(0x300, "Sigma 30mm F1.4 EX DC HSM");
        _lensTypeById.put(0x301, "Sigma 30mm F1.4 DC HSM | A");
        _lensTypeById.put(0x302, "Sigma 30mm F1.4 DC DN | C"); //JR (DN lenses are only for Sony E or MFT mount)
        _lensTypeById.put(0x310, "Sigma 50mm F1.4 EX DG HSM");
        _lensTypeById.put(0x311, "Sigma 50mm F1.4 DG HSM | A");
        _lensTypeById.put(0x320, "Sigma 85mm F1.4 EX DG HSM");
        _lensTypeById.put(0x330, "Sigma 30mm F2.8 EX DN");
        _lensTypeById.put(0x340, "Sigma 35mm F1.4 DG HSM");
        _lensTypeById.put(0x345, "Sigma 50mm F2.8 EX Macro");
        _lensTypeById.put(0x346, "Sigma 50mm F2.8 EX DG Macro");
        _lensTypeById.put(0x350, "Sigma 60mm F2.8 DN | A");
        _lensTypeById.put(0x400, "Sigma 19mm F2.8 EX DN");
        _lensTypeById.put(0x401, "Sigma 24mm F1.4 DG HSM | A");
        _lensTypeById.put(0x411, "Sigma 20mm F1.8 EX DG ASP RF");
        _lensTypeById.put(0x412, "Sigma 20mm F1.4 DG HSM | A");
        _lensTypeById.put(0x432, "Sigma 24mm F1.8 EX DG ASP Macro");
        _lensTypeById.put(0x440, "Sigma 28mm F1.8 EX DG ASP Macro");
        _lensTypeById.put(0x461, "Sigma 14mm F2.8 EX ASP HSM");
        _lensTypeById.put(0x475, "Sigma 15mm F2.8 EX Diagonal FishEye");
        _lensTypeById.put(0x476, "Sigma 15mm F2.8 EX DG Diagonal Fisheye");
        _lensTypeById.put(0x477, "Sigma 10mm F2.8 EX DC HSM Fisheye");
        _lensTypeById.put(0x483, "Sigma 8mm F4 EX Circular Fisheye");
        _lensTypeById.put(0x484, "Sigma 8mm F4 EX DG Circular Fisheye");
        _lensTypeById.put(0x485, "Sigma 8mm F3.5 EX DG Circular Fisheye");
        _lensTypeById.put(0x486, "Sigma 4.5mm F2.8 EX DC HSM Circular Fisheye");
        _lensTypeById.put(0x506, "Sigma 70-300mm F4-5.6 APO Macro Super II");
        _lensTypeById.put(0x507, "Sigma 70-300mm F4-5.6 DL Macro Super II");
        _lensTypeById.put(0x508, "Sigma 70-300mm F4-5.6 DG APO Macro");
        _lensTypeById.put(0x509, "Sigma 70-300mm F4-5.6 DG Macro");
        _lensTypeById.put(0x510, "Sigma 17-35 F2.8-4 EX DG ASP");
        _lensTypeById.put(0x512, "Sigma 15-30mm F3.5-4.5 EX DG ASP DF");
        _lensTypeById.put(0x513, "Sigma 20-40mm F2.8 EX DG");
        _lensTypeById.put(0x519, "Sigma 17-35 F2.8-4 EX ASP HSM");
        _lensTypeById.put(0x520, "Sigma 100-300mm F4.5-6.7 DL");
        _lensTypeById.put(0x521, "Sigma 18-50mm F3.5-5.6 DC Macro");
        _lensTypeById.put(0x527, "Sigma 100-300mm F4 EX IF HSM");
        _lensTypeById.put(0x529, "Sigma 120-300mm F2.8 EX HSM IF APO");
        _lensTypeById.put(0x547, "Sigma 24-60mm F2.8 EX DG");
        _lensTypeById.put(0x548, "Sigma 24-70mm F2.8 EX DG Macro");
        _lensTypeById.put(0x549, "Sigma 28-70mm F2.8 EX DG");
        _lensTypeById.put(0x566, "Sigma 70-200mm F2.8 EX IF APO");
        _lensTypeById.put(0x567, "Sigma 70-200mm F2.8 EX IF HSM APO");
        _lensTypeById.put(0x568, "Sigma 70-200mm F2.8 EX DG IF HSM APO");
        _lensTypeById.put(0x569, "Sigma 70-200 F2.8 EX DG HSM APO Macro");
        _lensTypeById.put(0x571, "Sigma 24-70mm F2.8 IF EX DG HSM");
        _lensTypeById.put(0x572, "Sigma 70-300mm F4-5.6 DG OS");
        _lensTypeById.put(0x579, "Sigma 70-200mm F2.8 EX DG HSM APO Macro"); // (also II version)
        _lensTypeById.put(0x580, "Sigma 18-50mm F2.8 EX DC");
        _lensTypeById.put(0x581, "Sigma 18-50mm F2.8 EX DC Macro"); //PH (SD1)
        _lensTypeById.put(0x582, "Sigma 18-50mm F2.8 EX DC HSM Macro");
        _lensTypeById.put(0x583, "Sigma 17-50mm F2.8 EX DC OS HSM"); //PH (also SD1 Kit, is this HSM? - PH)
        _lensTypeById.put(0x588, "Sigma 24-35mm F2 DG HSM | A");
        _lensTypeById.put(0x589, "Sigma APO 70-200mm F2.8 EX DG OS HSM");
        _lensTypeById.put(0x594, "Sigma 300-800mm F5.6 EX HSM IF APO");
        _lensTypeById.put(0x595, "Sigma 300-800mm F5.6 EX DG APO HSM");
        _lensTypeById.put(0x597, "Sigma 200-500mm F2.8 APO EX DG");
        _lensTypeById.put(0x5A8, "Sigma 70-300mm F4-5.6 APO DG Macro (Motorized)");
        _lensTypeById.put(0x5A9, "Sigma 70-300mm F4-5.6 DG Macro (Motorized)");
        _lensTypeById.put(0x633, "Sigma 28-70mm F2.8-4 HS");
        _lensTypeById.put(0x634, "Sigma 28-70mm F2.8-4 DG");
        _lensTypeById.put(0x635, "Sigma 24-105mm F4 DG OS HSM | A");
        _lensTypeById.put(0x644, "Sigma 28-80mm F3.5-5.6 ASP HF Macro");
        _lensTypeById.put(0x659, "Sigma 28-80mm F3.5-5.6 Mini Zoom Macro II ASP");
        _lensTypeById.put(0x661, "Sigma 28-105mm F2.8-4 IF ASP");
        _lensTypeById.put(0x663, "Sigma 28-105mm F3.8-5.6 IF UC-III ASP");
        _lensTypeById.put(0x664, "Sigma 28-105mm F2.8-4 IF DG ASP");
        _lensTypeById.put(0x667, "Sigma 24-135mm F2.8-4.5 IF ASP");
        _lensTypeById.put(0x668, "Sigma 17-70mm F2.8-4 DC Macro OS HSM");
        _lensTypeById.put(0x669, "Sigma 17-70mm F2.8-4.5 DC HSM Macro");
        _lensTypeById.put(0x684, "Sigma 55-200mm F4-5.6 DC");
        _lensTypeById.put(0x686, "Sigma 50-200mm F4-5.6 DC OS HSM");
        _lensTypeById.put(0x689, "Sigma 17-70mm F2.8-4.5 DC Macro");
        _lensTypeById.put(0x690, "Sigma 50-150mm F2.8 EX DC HSM APO");
        _lensTypeById.put(0x691, "Sigma 50-150mm F2.8 EX DC APO HSM II");
        _lensTypeById.put(0x692, "Sigma APO 50-150mm F2.8 EX DC OS HSM");
        _lensTypeById.put(0x709, "Sigma 28-135mm F3.8-5.6 IF ASP Macro");
        _lensTypeById.put(0x723, "Sigma 135-400mm F4.5-5.6 ASP APO");
        _lensTypeById.put(0x725, "Sigma 80-400mm F4.5-5.6 EX OS");
        _lensTypeById.put(0x726, "Sigma 80-400mm F4.5-5.6 EX DG OS APO");
        _lensTypeById.put(0x727, "Sigma 135-400mm F4.5-5.6 DG ASP APO");
        _lensTypeById.put(0x728, "Sigma 120-400mm F4.5-5.6 DG APO OS HSM");
        _lensTypeById.put(0x733, "Sigma 170-500mm F5-6.3 ASP APO");
        _lensTypeById.put(0x734, "Sigma 170-500mm F5-6.3 DG ASP APO");
        _lensTypeById.put(0x735, "Sigma 50-500mm F4-6.3 EX RF HSM APO");
        _lensTypeById.put(0x736, "Sigma 50-500mm F4-6.3 EX DG HSM APO");
        _lensTypeById.put(0x737, "Sigma 150-500mm F5-6.3 APO DG OS HSM");
        _lensTypeById.put(0x738, "Sigma 50-500mm F4.5-6.3 APO DG OS HSM");
        _lensTypeById.put(0x740, "Sigma 150-600mm F5-6.3 DG OS HSM | S");
        _lensTypeById.put(0x745, "Sigma 150-600mm F5-6.3 DG OS HSM | C");
        _lensTypeById.put(0x777, "Sigma 18-200mm F3.5-6.3 DC");
        _lensTypeById.put(0x77D, "Sigma 18-200mm F3.5-6.3 DC (Motorized)");
        _lensTypeById.put(0x787, "Sigma 28-200mm F3.5-5.6 Compact ASP HZ Macro");
        _lensTypeById.put(0x789, "Sigma 18-125mm F3.5-5.6 DC");
        _lensTypeById.put(0x793, "Sigma 28-300mm F3.5-6.3 Macro");
        _lensTypeById.put(0x794, "Sigma 28-200mm F3.5-5.6 DG Compact ASP HZ Macro");
        _lensTypeById.put(0x795, "Sigma 28-300mm F3.5-6.3 DG Macro");
        _lensTypeById.put(0x823, "Sigma 1.4X TC EX APO");
        _lensTypeById.put(0x824, "Sigma 1.4X Teleconverter EX APO DG");
        _lensTypeById.put(0x853, "Sigma 18-125mm F3.8-5.6 DC OS HSM");
        _lensTypeById.put(0x861, "Sigma 18-50mm F2.8-4.5 DC OS HSM"); //NJ (SD1)
        _lensTypeById.put(0x870, "Sigma 2.0X Teleconverter TC-2001"); //JR
        _lensTypeById.put(0x875, "Sigma 2.0X TC EX APO");
        _lensTypeById.put(0x876, "Sigma 2.0X Teleconverter EX APO DG");
        _lensTypeById.put(0x879, "Sigma 1.4X Teleconverter TC-1401"); //JR
        _lensTypeById.put(0x880, "Sigma 18-250mm F3.5-6.3 DC OS HSM");
        _lensTypeById.put(0x882, "Sigma 18-200mm F3.5-6.3 II DC OS HSM");
        _lensTypeById.put(0x883, "Sigma 18-250mm F3.5-6.3 DC Macro OS HSM");
        _lensTypeById.put(0x884, "Sigma 17-70mm F2.8-4 DC OS HSM Macro | C");
        _lensTypeById.put(0x885, "Sigma 18-200mm F3.5-6.3 DC OS HSM Macro | C");
        _lensTypeById.put(0x886, "Sigma 18-300mm F3.5-6.3 DC OS HSM Macro | C");
        _lensTypeById.put(0x888, "Sigma 18-200mm F3.5-6.3 DC OS");
        _lensTypeById.put(0x890, "Sigma Mount Converter MC-11"); //JR
        _lensTypeById.put(0x929, "Sigma 19mm F2.8 DN | A");
        _lensTypeById.put(0x929, "Sigma 30mm F2.8 DN | A");
        _lensTypeById.put(0x929, "Sigma 60mm F2.8 DN | A");
        _lensTypeById.put(0x1003, "Sigma 19mm F2.8"); //PH (DP1 Merrill kit)
        _lensTypeById.put(0x1004, "Sigma 30mm F2.8"); //PH (DP2 Merrill kit)
        _lensTypeById.put(0x1005, "Sigma 50mm F2.8 Macro"); //PH (DP3 Merrill kit)
        _lensTypeById.put(0x1006, "Sigma 19mm F2.8"); //NJ (DP1 Quattro kit)
        _lensTypeById.put(0x1007, "Sigma 30mm F2.8"); //PH (DP2 Quattro kit)
        _lensTypeById.put(0x1008, "Sigma 50mm F2.8 Macro"); //NJ (DP3 Quattro kit)
        _lensTypeById.put(0x1009, "Sigma 14mm F4"); //NJ (DP0 Quattro kit)
        _lensTypeById.put(0x8900, "Sigma 70-300mm F4-5.6 DG OS"); //PH (SD15)
        _lensTypeById.put(0xA100, "Sigma 24-70mm F2.8 DG Macro"); //PH (SD15)
        // 'FFFF' - seen this for a 28-70mm F2.8 lens - PH
    }

    public String getLens(Integer id)
    {
        return _lensTypeById.get(id);
    }
}
