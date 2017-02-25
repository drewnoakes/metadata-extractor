package com.drew.metadata.x3f;

import com.drew.metadata.Directory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Anthony Mandra http://anthonymandra.com
 * @author Drew Noakes https://drewnoakes.com
 */
public enum X3fKeys// implements Key
{
    TAG_AUTO_EXPOSURE_MODE("AEMODE", 1, "Auto Exposure Mode"),
    TAG_AUTO_FOCUS_MODE("AFMODE", 2, "Auto Focus Mode"),
    TAG_APERTURE_EXACT("APERTURE", 3, "Exact Aperture"),
    TAG_APERTURE_SIMPLE("AP_DESC", 4, "Simplified Aperture"),
    TAG_BRACKET_INDEX("BRACKET", 4, "Bracket Index"),
    TAG_BURST_INDEX("BURST", 5, "Burst Index"),
    TAG_MAKE("CAMMANUF", 6, "Camera Make"),
    TAG_MODEL("CAMMODEL", 7, "Camera Model"),
    TAG_NAME("CAMNAME", 8, "Camera Name"),
    TAG_SERIAL("CAMSERIAL", 9, "Camera Serial"),
    TAG_CM_DESC("CM_DESC", 10, "CM_DESC"),
    TAG_COLORSPACE("COLORSPACE", 11, "Color space"),
    TAG_CONT_DESC("CONT_DESC", 12, "CONT_DESC"),
    TAG_DARKTEMP("DARKTEMP", 13, "DARKTEMP"),
    TAG_DRIVE("DRIVE", 14, "Drive Mode"),
    TAG_EXPOSURE_COMP("EXPCOMP", 15, "Exposure Compensation"),
    TAG_EXPOSURE_COMP_NET("EXPNET", 16, "Total Exposure Compensation"),
    TAG_EXPOSURE_TIME("EXPTIME", 17, "Exposure Time"),
    TAG_FIRMWARE_VER("FIRMVERS", 18, "Firmware Version"),
    TAG_FLASH("FLASH", 19, "Flash"),
    TAG_FLASHPOWER("FLASHPOWER", 20, "Flash Power"),
    TAG_FOCAL_LENGTH("FLENGTH", 21, "Focal Length"),
    TAG_FOCAL_LENGTH_35EQ("FLEQ35MM", 22, "35mm Equivalent Focal Length"),
    TAG_FOCUS("FOCUS", 23, "Focus Mode"),
    TAG_FPGAVERS("FPGAVERS", 24, "FPGAVERS"),
    TAG_IMAGER_BOARD_ID("IDIMAGEBOARDID", 25, "Imager Board ID"),
    TAG_IMAGER_TEMP("IMAGERTEMP", 26, "Imager Board Temperature"),
    TAG_ISO(" ISO", 27, " ISO"),
    TAG_LENS_APERTURE_RANGE("LENSARANGE", 28, "Lens Aperture Range"),
    TAG_LENS_FOCAL_RANGE("LENSFRANGE", 29, "Lens Focal Range"),
    TAG_LENS_MODEL("LENSMODEL", 30, "Lens Model"),
    TAG_SHOOTING_MODE("PMODE", 31, "Shooting Mode"),
    TAG_RESOLUTION_SETTING("RESOLUTION", 32, "Resolution Setting"),
    TAG_ROTATION("ROTATION", 33, "Rotation"),
    TAG_SATU_DESC("SATU_DESC", 34, "Saturation"),
    TAG_SENSOR_ID("SENSORID", 35, "Sensor ID"),
    TAG_SHARP_DESC("SHARP_DESC", 36, "Sharpness Setting"),
    TAG_SHUTTER_EXACT("SHUTTER", 37, "Exact Shutter Speed"),
    TAG_SHUTTER_SIMPLE("SH_DESC", 38, "Simplified Shutter speed"),
    TAG_TELECONV("TELECONV", 39, "TELECONV"),
    TAG_TIME("TIME", 40, "Capture Time (Unix UTC)"),
    TAG_WHITE_BALANCE("WB_DESC", 41, "White Balance Mode");

    private final int index;    // for backwards compatibility
    private final String key;
    private final String summary;

    X3fKeys(String key, int index, String summary)
    {
        this.index = index;
        this.key = key;
        this.summary = summary;
    }

    //TODO: Use a sparse array trie, or FastUtil
    private static final Map<Integer, X3fKeys> lookup = new HashMap<Integer, X3fKeys>();
    static {
        for (X3fKeys type : values())
            lookup.put(type.getValue(), type);
    }

    public String getName()
    {
        return key;
    }

    public String getSummary()
    {
        return summary;
    }

    public Integer getValue()
    {
        return index;
    }
}
