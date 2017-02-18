/*
 * Copyright 2002-2016 Drew Noakes
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
package com.drew.metadata.x3f;

import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.impl.XMPDateTimeImpl;
import com.adobe.xmp.impl.XMPMetaImpl;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Anthony Mandra
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class X3fDirectory extends Directory
{
    public static final int TAG_XMP_VALUE_COUNT = 0xFFFF;

    // These are some tags, belonging to x3f properties major versions 2 and 3.
    // In version 4 x3f moved to exif only.
    // The numeration is more like enums. The real tags are strings.
    public static final int TAG_AUTO_EXPOSURE_MODE      = 1;    //"8" segement, "C"enter-weighted, "A"vereage-weighted
    public static final int TAG_AUTO_FOCUS_MODE         = 2;    //"AF-S" single, "AF-C" continuous, "MF" manual
    public static final int TAG_APERTURE_EXACT          = 3;    // exact aperture
    public static final int TAG_APERTURE_SIMPLE         = 4;    // rounded aperture
    public static final int TAG_BRACKET_INDEX           = 4;    // bracket position "x of y" or ""
    public static final int TAG_BURST_INDEX             = 5;    // shot number in burst
    public static final int TAG_MAKE                    = 6;    // camera make
    public static final int TAG_MODEL                   = 7;    // camera model (often contains make)
    public static final int TAG_NAME                    = 8;    // personalized name setting
    public static final int TAG_SERIAL                  = 9;
    public static final int TAG_CM_DESC                 = 10;   // image setting, "vivid" in example
    public static final int TAG_COLORSPACE              = 11;   // sRGB, etc.
    public static final int TAG_CONT_DESC               = 12;   // unknown, 0.0 in example
    public static final int TAG_DARKTEMP                = 13;
    public static final int TAG_DRIVE                   = 14;   // "SINGLE", "MULTI", "2s"econd, "10s"econd, "UP", "AB"racket, "OFF"
    public static final int TAG_EXPOSURE_COMP           = 15;
    public static final int TAG_EXPOSURE_COMP_NET       = 16;   // total exposure comp including bracketing
    public static final int TAG_EXPOSURE_TIME           = 17;   // microsecond
    public static final int TAG_FIRMWARE_VER            = 18;
    public static final int TAG_FLASH                   = 19;   // "ON", "OFF", "REDEYE"
    public static final int TAG_FLASHPOWER              = 20;
    public static final int TAG_FOCAL_LENGTH            = 21;
    public static final int TAG_FOCAL_LENGTH_35EQ       = 22;   // 35mm equivalent focal length
    public static final int TAG_FOCUS                   = 23;   // "AF", "NO LOCK", "M"
    public static final int TAG_FPGAVERS                = 24;   // unknown, 0x1024 in example
    public static final int TAG_IMAGER_BOARD_ID         = 25;
    public static final int TAG_IMAGER_TEMP             = 26;   // temperature (c) of imager
    public static final int TAG_ISO                     = 27;
    public static final int TAG_LENS_APERTURE_RANGE     = 28;
    public static final int TAG_LENS_FOCAL_RANGE        = 29;
    public static final int TAG_LENS_MODEL              = 30;   // identifier byte (exiftool)
    public static final int TAG_SHOOTING_MODE           = 31;   // "P"rogram, "A"perture, "S"hutter, "M"anual
    public static final int TAG_RESOLUTION_SETTING      = 32;   // "LOW", "MED", "HI"
    public static final int TAG_ROTATION                = 33;   // unsure of the model they're using for orientation
    public static final int TAG_SATU_DESC   = 34;
    public static final int TAG_SENSOR_ID               = 35;
    public static final int TAG_SHARP_DESC  = 36;
    public static final int TAG_SHUTTER_EXACT           = 37;   // exact shutter speed
    public static final int TAG_SHUTTER_SIMPLE          = 38;   // rounded shutter speed
    public static final int TAG_TELECONV    = 39;
    public static final int TAG_TIME                    = 40;   // Unix UTC
    public static final int TAG_WHITE_BALANCE           = 41;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();
    @NotNull
    protected static final HashMap<Integer, String> _tagPropNameMap = new HashMap<Integer, String>();
    @NotNull
    private final Map<String, String> _propertyValueByPath = new HashMap<String, String>();

    static
    {
        _tagNameMap.put(TAG_AUTO_EXPOSURE_MODE, "AEMODE");
        _tagNameMap.put(TAG_AUTO_FOCUS_MODE, "AFMODE");
        _tagNameMap.put(TAG_APERTURE_EXACT, "APERTURE");
        _tagNameMap.put(TAG_APERTURE_SIMPLE, "AP_DESC");
        _tagNameMap.put(TAG_BRACKET_INDEX, "BRACKET");
        _tagNameMap.put(TAG_BURST_INDEX, "BURST");
        _tagNameMap.put(TAG_MAKE, "CAMMANUF");
        _tagNameMap.put(TAG_MODEL, "CAMMODEL");
        _tagNameMap.put(TAG_NAME, "CAMNAME");
        _tagNameMap.put(TAG_SERIAL, "CAMSERIAL");
        _tagNameMap.put(TAG_CM_DESC, "CM_DESC");
        _tagNameMap.put(TAG_COLORSPACE, "COLORSPACE");
        _tagNameMap.put(TAG_CONT_DESC, "CONT_DESC");
        _tagNameMap.put(TAG_DARKTEMP, "DARKTEMP");
        _tagNameMap.put(TAG_DRIVE, "DRIVE");
        _tagNameMap.put(TAG_EXPOSURE_COMP, "EXPCOMP");
        _tagNameMap.put(TAG_EXPOSURE_COMP_NET, "EXPNET");
        _tagNameMap.put(TAG_EXPOSURE_TIME, "EXPTIME");
        _tagNameMap.put(TAG_FIRMWARE_VER, "FIRMVERS");
        _tagNameMap.put(TAG_FLASH, "FLASH");
        _tagNameMap.put(TAG_FLASHPOWER, "FLASHPOWER");
        _tagNameMap.put(TAG_FOCAL_LENGTH, "FLENGTH");
        _tagNameMap.put(TAG_FOCAL_LENGTH_35EQ, "FLEQ35MM");
        _tagNameMap.put(TAG_FOCUS, "FOCUS");
        _tagNameMap.put(TAG_FPGAVERS, "FPGAVERS");
        _tagNameMap.put(TAG_IMAGER_BOARD_ID, "IDIMAGEBOARDID");
        _tagNameMap.put(TAG_IMAGER_TEMP, "IMAGERTEMP");
        _tagNameMap.put(TAG_ISO, " ISO");
        _tagNameMap.put(TAG_LENS_APERTURE_RANGE, "LENSARANGE");
        _tagNameMap.put(TAG_LENS_FOCAL_RANGE, "LENSFRANGE");
        _tagNameMap.put(TAG_LENS_MODEL, "LENSMODEL");
        _tagNameMap.put(TAG_SHOOTING_MODE, "PMODE");
        _tagNameMap.put(TAG_RESOLUTION_SETTING, "RESOLUTION");
        _tagNameMap.put(TAG_ROTATION, "ROTATION");
        _tagNameMap.put(TAG_SATU_DESC, "SATU_DESC");
        _tagNameMap.put(TAG_SENSOR_ID, "SENSORID");
        _tagNameMap.put(TAG_SHARP_DESC, "SHARP_DESC");
        _tagNameMap.put(TAG_SHUTTER_EXACT, "SHUTTER");
        _tagNameMap.put(TAG_SHUTTER_SIMPLE, "SH_DESC");
        _tagNameMap.put(TAG_TELECONV, "TELECONV");
        _tagNameMap.put(TAG_TIME, "TIME");
        _tagNameMap.put(TAG_WHITE_BALANCE, "WB_DESC");

        // Map the tag to the actual key
        _tagPropNameMap.put(TAG_AUTO_EXPOSURE_MODE, "AEMODE");
        _tagPropNameMap.put(TAG_AUTO_FOCUS_MODE, "AFMODE");
        _tagPropNameMap.put(TAG_APERTURE_EXACT, "APERTURE");
        _tagPropNameMap.put(TAG_APERTURE_SIMPLE, "AP_DESC");
        _tagPropNameMap.put(TAG_BRACKET_INDEX, "BRACKET");
        _tagPropNameMap.put(TAG_BURST_INDEX, "BURST");
        _tagPropNameMap.put(TAG_MAKE, "CAMMANUF");
        _tagPropNameMap.put(TAG_MODEL, "CAMMODEL");
        _tagPropNameMap.put(TAG_NAME, "CAMNAME");
        _tagPropNameMap.put(TAG_SERIAL, "CAMSERIAL");
        _tagPropNameMap.put(TAG_CM_DESC, "CM_DESC");
        _tagPropNameMap.put(TAG_COLORSPACE, "COLORSPACE");
        _tagPropNameMap.put(TAG_CONT_DESC, "CONT_DESC");
        _tagPropNameMap.put(TAG_DARKTEMP, "DARKTEMP");
        _tagPropNameMap.put(TAG_DRIVE, "DRIVE");
        _tagPropNameMap.put(TAG_EXPOSURE_COMP, "EXPCOMP");
        _tagPropNameMap.put(TAG_EXPOSURE_COMP_NET, "EXPNET");
        _tagPropNameMap.put(TAG_EXPOSURE_TIME, "EXPTIME");
        _tagPropNameMap.put(TAG_FIRMWARE_VER, "FIRMVERS");
        _tagPropNameMap.put(TAG_FLASH, "FLASH");
        _tagPropNameMap.put(TAG_FLASHPOWER, "FLASHPOWER");
        _tagPropNameMap.put(TAG_FOCAL_LENGTH, "FLENGTH");
        _tagPropNameMap.put(TAG_FOCAL_LENGTH_35EQ, "FLEQ35MM");
        _tagPropNameMap.put(TAG_FOCUS, "FOCUS");
        _tagPropNameMap.put(TAG_FPGAVERS, "FPGAVERS");
        _tagPropNameMap.put(TAG_IMAGER_BOARD_ID, "IDIMAGEBOARDID");
        _tagPropNameMap.put(TAG_IMAGER_TEMP, "IMAGERTEMP");
        _tagPropNameMap.put(TAG_ISO, " ISO");
        _tagPropNameMap.put(TAG_LENS_APERTURE_RANGE, "LENSARANGE");
        _tagPropNameMap.put(TAG_LENS_FOCAL_RANGE, "LENSFRANGE");
        _tagPropNameMap.put(TAG_LENS_MODEL, "LENSMODEL");
        _tagPropNameMap.put(TAG_SHOOTING_MODE, "PMODE");
        _tagPropNameMap.put(TAG_RESOLUTION_SETTING, "RESOLUTION");
        _tagPropNameMap.put(TAG_ROTATION, "ROTATION");
        _tagPropNameMap.put(TAG_SATU_DESC, "SATU_DESC");
        _tagPropNameMap.put(TAG_SENSOR_ID, "SENSORID");
        _tagPropNameMap.put(TAG_SHARP_DESC, "SHARP_DESC");
        _tagPropNameMap.put(TAG_SHUTTER_EXACT, "SHUTTER");
        _tagPropNameMap.put(TAG_SHUTTER_SIMPLE, "SH_DESC");
        _tagPropNameMap.put(TAG_TELECONV, "TELECONV");
        _tagPropNameMap.put(TAG_TIME, "TIME");
        _tagPropNameMap.put(TAG_WHITE_BALANCE, "WB_DESC");
    }

    @Nullable
    private XMPMeta _xmpMeta;

    public X3fDirectory()
    {
        this.setDescriptor(new X3fDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "XMP";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    void addProperty(@NotNull String path, @NotNull String value)
    {
        _propertyValueByPath.put(path, value);
    }

    /**
     * Gets a map of all XMP properties in this directory, not just the known ones.
     * <p>
     * This is required because XMP properties are represented as strings, whereas the rest of this library
     * uses integers for keys.
     */
    @NotNull
    public Map<String, String> getXmpProperties()
    {
        return Collections.unmodifiableMap(_propertyValueByPath);
    }

    public void setXMPMeta(@NotNull XMPMeta xmpMeta)
    {
        _xmpMeta = xmpMeta;

        try {
            int valueCount = 0;
            for (Iterator i = _xmpMeta.iterator(); i.hasNext(); ) {
                XMPPropertyInfo prop = (XMPPropertyInfo)i.next();
                if (prop.getPath() != null) {
                    //System.out.printf("%s = %s\n", prop.getPath(), prop.getValue());
                    valueCount++;
                }
            }
            setInt(TAG_XMP_VALUE_COUNT, valueCount);
        } catch (XMPException ignored) {
        }
    }

    /**
     * Gets the XMPMeta object used to populate this directory. It can be used for more XMP-oriented operations. If one does not exist it will be
     * created.
     */
    @NotNull
    public XMPMeta getXMPMeta()
    {
        if (_xmpMeta == null)
            _xmpMeta = new XMPMetaImpl();
        return _xmpMeta;
    }

    // TODO: Might consider returning a boolean in the super to allow for exception handling. Failing to set is sufficient for now.
    // TODO: update[Type] avoids rewriting the whole _xmpMeta on processXmpTags(),
    // but with sets exposed this is still less than ideal...
    // At the very least document this carefully!

    public void updateInt(int tagType, int value)
    {
        super.setInt(tagType, value);
        try
        {
            getXMPMeta().setPropertyInteger(_tagSchemaMap.get(tagType), _tagPropNameMap.get(tagType), value);
        }
        catch (XMPException e)
        {
            e.printStackTrace();
        }
    }

    public void updateIntArray(int tagType, int[] ints)
    {
        super.setIntArray(tagType, ints);
        try
        {
            String schemaNS = _tagSchemaMap.get(tagType);
            String propName = _tagPropNameMap.get(tagType);

            getXMPMeta().deleteProperty(schemaNS, propName);

            PropertyOptions po = new PropertyOptions().setArray(true);
            for (int item : ints)
            {
                getXMPMeta().appendArrayItem(schemaNS, propName, po, String.valueOf(item), null);
            }
        }
        catch (XMPException e)
        {
            e.printStackTrace();
        }
    }

    public void updateFloat(int tagType, float value)
    {
        super.setFloat(tagType, value);
        try
        {
            getXMPMeta().setPropertyDouble(_tagSchemaMap.get(tagType), _tagPropNameMap.get(tagType), value);
        }
        catch (XMPException e)
        {
            e.printStackTrace();
        }
    }

    public void updateFloatArray(int tagType, float[] floats)
    {
        super.setFloatArray(tagType, floats);
        try
        {
            String schemaNS = _tagSchemaMap.get(tagType);
            String propName = _tagPropNameMap.get(tagType);

            getXMPMeta().deleteProperty(schemaNS, propName);

            PropertyOptions po = new PropertyOptions().setArray(true);
            for (float item : floats)
            {
                getXMPMeta().appendArrayItem(schemaNS, propName, po, String.valueOf(item), null);
            }
        }
        catch (XMPException e)
        {
            e.printStackTrace();
        }
    }

    public void updateDouble(int tagType, double value)
    {
        super.setDouble(tagType, value);
        try
        {
            getXMPMeta().setPropertyDouble(_tagSchemaMap.get(tagType), _tagPropNameMap.get(tagType), value);
        }
        catch (XMPException e)
        {
            e.printStackTrace();
        }
    }

    public void updateDoubleArray(int tagType, double[] doubles)
    {
        super.setDoubleArray(tagType, doubles);
        try
        {
            String schemaNS = _tagSchemaMap.get(tagType);
            String propName = _tagPropNameMap.get(tagType);

            getXMPMeta().deleteProperty(schemaNS, propName);

            PropertyOptions po = new PropertyOptions().setArray(true);
            for (double item : doubles)
            {
                getXMPMeta().appendArrayItem(schemaNS, propName, po, String.valueOf(item), null);
            }
        }
        catch (XMPException e)
        {
            e.printStackTrace();
        }
    }

    public void updateString(int tagType, String value)
    {
        super.setString(tagType, value);
        try
        {
            getXMPMeta().setProperty(_tagSchemaMap.get(tagType), _tagPropNameMap.get(tagType), value);
        }
        catch (XMPException e)
        {
            e.printStackTrace();
        }
    }

    public void deleteProperty(int tagType)
    {
        getXMPMeta().deleteProperty(_tagSchemaMap.get(tagType), _tagPropNameMap.get(tagType));
    }

    public void updateStringArray(int tagType, String[] strings)
    {
        super.setStringArray(tagType, strings);
        try
        {
            String schemaNS = _tagSchemaMap.get(tagType);
            String propName = _tagPropNameMap.get(tagType);

            getXMPMeta().deleteProperty(schemaNS, propName);

            PropertyOptions po = new PropertyOptions().setArray(true);
            for (String item : strings)
            {
                getXMPMeta().appendArrayItem(schemaNS, propName, po, item, null);
            }
        }
        catch (XMPException e)
        {
            e.printStackTrace();
        }
    }

    public void updateBoolean(int tagType, boolean value)
    {
        super.setBoolean(tagType, value);
        try
        {
            getXMPMeta().setPropertyBoolean(_tagSchemaMap.get(tagType), _tagPropNameMap.get(tagType), value);
        }
        catch (XMPException e)
        {
            e.printStackTrace();
        }
    }

    public void updateLong(int tagType, long value)
    {
        super.setLong(tagType, value);
        try
        {
            getXMPMeta().setPropertyLong(_tagSchemaMap.get(tagType), _tagPropNameMap.get(tagType), value);
        }
        catch (XMPException e)
        {
            e.printStackTrace();
        }
    }

    public void updateDate(int tagType, Date value)
    {
        updateDate(tagType, value, TimeZone.getDefault());
    }

    public void updateDate(int tagType, Date value, TimeZone timeZone)
    {
        super.setDate(tagType, value);
        XMPDateTime date = new XMPDateTimeImpl(value, timeZone);
        try
        {
            getXMPMeta().setPropertyDate(_tagSchemaMap.get(tagType), _tagPropNameMap.get(tagType), date);
        }
        catch (XMPException e)
        {
            e.printStackTrace();
        }
    }

    // TODO: Ignoring rationals for now, not sure their relevance to XMP (rational/floating storage)
    // @Override
    // public void setRational(int tagType, Rational rational)
    // {
    // super.setRational(tagType, rational);
    // }
    //
    // @Override
    // public void setRationalArray(int tagType, Rational[] rationals)
    // {
    // // TODO Auto-generated method stub
    // super.setRationalArray(tagType, rationals);
    // }

    // TODO: Not sure the intention of the byte array, probably store like the other arrays.
    // @Override
    // public void setByteArray(int tagType, byte[] bytes)
    // {
    // // TODO Auto-generated method stub
    // super.setByteArray(tagType, bytes);
    // }
}
