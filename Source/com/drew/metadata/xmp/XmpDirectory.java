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
package com.drew.metadata.xmp;

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
import com.drew.metadata.Schema;

import java.util.*;

/**
 * @author Torsten Skadell
 * @author Drew Noakes https://drewnoakes.com
 */
public class XmpDirectory extends Directory
{
    public static final int TAG_XMP_VALUE_COUNT = 0xFFFF;

    // These are some Tags, belonging to xmp-data-tags
    // The numeration is more like enums. The real xmp-tags are strings,
    // so we do some kind of mapping here...
    public static final int TAG_MAKE = 0x0001;
    public static final int TAG_MODEL = 0x0002;
    public static final int TAG_EXPOSURE_TIME = 0x0003;
    public static final int TAG_SHUTTER_SPEED = 0x0004;
    public static final int TAG_F_NUMBER = 0x0005;
    public static final int TAG_LENS_INFO = 0x0006;
    public static final int TAG_LENS = 0x0007;
    public static final int TAG_CAMERA_SERIAL_NUMBER = 0x0008;
    public static final int TAG_FIRMWARE = 0x0009;
    public static final int TAG_FOCAL_LENGTH = 0x000a;
    public static final int TAG_APERTURE_VALUE = 0x000b;
    public static final int TAG_EXPOSURE_PROGRAM = 0x000c;
    public static final int TAG_DATETIME_ORIGINAL = 0x000d;
    public static final int TAG_DATETIME_DIGITIZED = 0x000e;

    /**
     * A value from 0 to 5, or -1 if the image is rejected.
     */
    public static final int TAG_RATING = 0x1001;
    /**
     * Generally a color value Blue, Red, Green, Yellow, Purple
     */
    public static final int TAG_LABEL = 0x2000;

    // dublin core properties
    // this requires further research
    // public static int TAG_TITLE = 0x100;
    /**
     * Keywords
     */
    public static int TAG_SUBJECT = 0x2001;
    // public static int TAG_DATE = 0x1002;
    // public static int TAG_TYPE = 0x1003;
    // public static int TAG_DESCRIPTION = 0x1004;
    // public static int TAG_RELATION = 0x1005;
    // public static int TAG_COVERAGE = 0x1006;
    // public static int TAG_CREATOR = 0x1007;
    // public static int TAG_PUBLISHER = 0x1008;
    // public static int TAG_CONTRIBUTOR = 0x1009;
    // public static int TAG_RIGHTS = 0x100A;
    // public static int TAG_FORMAT = 0x100B;
    // public static int TAG_IDENTIFIER = 0x100C;
    // public static int TAG_LANGUAGE = 0x100D;
    // public static int TAG_AUDIENCE = 0x100E;
    // public static int TAG_PROVENANCE = 0x100F;
    // public static int TAG_RIGHTS_HOLDER = 0x1010;
    // public static int TAG_INSTRUCTIONAL_METHOD = 0x1011;
    // public static int TAG_ACCRUAL_METHOD = 0x1012;
    // public static int TAG_ACCRUAL_PERIODICITY = 0x1013;
    // public static int TAG_ACCRUAL_POLICY = 0x1014;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();
    @NotNull
    protected static final HashMap<Integer, String> _tagSchemaMap = new HashMap<Integer, String>();
    @NotNull
    protected static final HashMap<Integer, String> _tagPropNameMap = new HashMap<Integer, String>();
    @NotNull
    private final Map<String, String> _propertyValueByPath = new HashMap<String, String>();

    static {
        _tagNameMap.put(TAG_XMP_VALUE_COUNT, "XMP Value Count");

        _tagNameMap.put(TAG_MAKE, "Make");
        _tagNameMap.put(TAG_MODEL, "Model");
        _tagNameMap.put(TAG_EXPOSURE_TIME, "Exposure Time");
        _tagNameMap.put(TAG_SHUTTER_SPEED, "Shutter Speed Value");
        _tagNameMap.put(TAG_F_NUMBER, "F-Number");
        _tagNameMap.put(TAG_LENS_INFO, "Lens Information");
        _tagNameMap.put(TAG_LENS, "Lens");
        _tagNameMap.put(TAG_CAMERA_SERIAL_NUMBER, "Serial Number");
        _tagNameMap.put(TAG_FIRMWARE, "Firmware");
        _tagNameMap.put(TAG_FOCAL_LENGTH, "Focal Length");
        _tagNameMap.put(TAG_APERTURE_VALUE, "Aperture Value");
        _tagNameMap.put(TAG_EXPOSURE_PROGRAM, "Exposure Program");
        _tagNameMap.put(TAG_DATETIME_ORIGINAL, "Date/Time Original");
        _tagNameMap.put(TAG_DATETIME_DIGITIZED, "Date/Time Digitized");

        _tagNameMap.put(TAG_RATING, "Rating");
        _tagNameMap.put(TAG_LABEL, "Label");

        // this requires further research
        // _tagNameMap.put(TAG_TITLE, "Title");
        _tagNameMap.put(TAG_SUBJECT, "Subject");
        // _tagNameMap.put(TAG_DATE, "Date");
        // _tagNameMap.put(TAG_TYPE, "Type");
        // _tagNameMap.put(TAG_DESCRIPTION, "Description");
        // _tagNameMap.put(TAG_RELATION, "Relation");
        // _tagNameMap.put(TAG_COVERAGE, "Coverage");
        // _tagNameMap.put(TAG_CREATOR, "Creator");
        // _tagNameMap.put(TAG_PUBLISHER, "Publisher");
        // _tagNameMap.put(TAG_CONTRIBUTOR, "Contributor");
        // _tagNameMap.put(TAG_RIGHTS, "Rights");
        // _tagNameMap.put(TAG_FORMAT, "Format");
        // _tagNameMap.put(TAG_IDENTIFIER, "Identifier");
        // _tagNameMap.put(TAG_LANGUAGE, "Language");
        // _tagNameMap.put(TAG_AUDIENCE, "Audience");
        // _tagNameMap.put(TAG_PROVENANCE, "Provenance");
        // _tagNameMap.put(TAG_RIGHTS_HOLDER, "Rights Holder");
        // _tagNameMap.put(TAG_INSTRUCTIONAL_METHOD, "Instructional Method");
        // _tagNameMap.put(TAG_ACCRUAL_METHOD, "Accrual Method");
        // _tagNameMap.put(TAG_ACCRUAL_PERIODICITY, "Accrual Periodicity");
        // _tagNameMap.put(TAG_ACCRUAL_POLICY, "Accrual Policy");

        _tagPropNameMap.put(TAG_MAKE, "tiff:Make");
        _tagPropNameMap.put(TAG_MODEL, "tiff:Model");
        _tagPropNameMap.put(TAG_EXPOSURE_TIME, "exif:ExposureTime");
        _tagPropNameMap.put(TAG_SHUTTER_SPEED, "exif:ShutterSpeedValue");
        _tagPropNameMap.put(TAG_F_NUMBER, "exif:FNumber");
        _tagPropNameMap.put(TAG_LENS_INFO, "aux:LensInfo");
        _tagPropNameMap.put(TAG_LENS, "aux:Lens");
        _tagPropNameMap.put(TAG_CAMERA_SERIAL_NUMBER, "aux:SerialNumber");
        _tagPropNameMap.put(TAG_FIRMWARE, "aux:Firmware");
        _tagPropNameMap.put(TAG_FOCAL_LENGTH, "exif:FocalLength");
        _tagPropNameMap.put(TAG_APERTURE_VALUE, "exif:ApertureValue");
        _tagPropNameMap.put(TAG_EXPOSURE_PROGRAM, "exif:ExposureProgram");
        _tagPropNameMap.put(TAG_DATETIME_ORIGINAL, "exif:DateTimeOriginal");
        _tagPropNameMap.put(TAG_DATETIME_DIGITIZED, "exif:DateTimeDigitized");

        _tagPropNameMap.put(TAG_RATING, "xmp:Rating");
        _tagPropNameMap.put(TAG_LABEL, "xmp:Label");

        // this requires further research
        // _tagPropNameMap.put(TAG_TITLE, "dc:title");
        _tagPropNameMap.put(TAG_SUBJECT, "dc:subject");
        // _tagPropNameMap.put(TAG_DATE, "dc:date");
        // _tagPropNameMap.put(TAG_TYPE, "dc:type");
        // _tagPropNameMap.put(TAG_DESCRIPTION, "dc:description");
        // _tagPropNameMap.put(TAG_RELATION, "dc:relation");
        // _tagPropNameMap.put(TAG_COVERAGE, "dc:coverage");
        // _tagPropNameMap.put(TAG_CREATOR, "dc:creator");
        // _tagPropNameMap.put(TAG_PUBLISHER, "dc:publisher");
        // _tagPropNameMap.put(TAG_CONTRIBUTOR, "dc:contributor");
        // _tagPropNameMap.put(TAG_RIGHTS, "dc:rights");
        // _tagPropNameMap.put(TAG_FORMAT, "dc:format");
        // _tagPropNameMap.put(TAG_IDENTIFIER, "dc:identifier");
        // _tagPropNameMap.put(TAG_LANGUAGE, "dc:language");
        // _tagPropNameMap.put(TAG_AUDIENCE, "dc:audience");
        // _tagPropNameMap.put(TAG_PROVENANCE, "dc:provenance");
        // _tagPropNameMap.put(TAG_RIGHTS_HOLDER, "dc:rightsHolder");
        // _tagPropNameMap.put(TAG_INSTRUCTIONAL_METHOD, "dc:instructionalMethod");
        // _tagPropNameMap.put(TAG_ACCRUAL_METHOD, "dc:accrualMethod");
        // _tagPropNameMap.put(TAG_ACCRUAL_PERIODICITY, "dc:accrualPeriodicity");
        // _tagPropNameMap.put(TAG_ACCRUAL_POLICY, "dc:accrualPolicy");

        _tagSchemaMap.put(TAG_MAKE, Schema.EXIF_TIFF_PROPERTIES);
        _tagSchemaMap.put(TAG_MODEL, Schema.EXIF_TIFF_PROPERTIES);
        _tagSchemaMap.put(TAG_EXPOSURE_TIME, Schema.EXIF_SPECIFIC_PROPERTIES);
        _tagSchemaMap.put(TAG_SHUTTER_SPEED, Schema.EXIF_SPECIFIC_PROPERTIES);
        _tagSchemaMap.put(TAG_F_NUMBER, Schema.EXIF_SPECIFIC_PROPERTIES);
        _tagSchemaMap.put(TAG_LENS_INFO, Schema.EXIF_ADDITIONAL_PROPERTIES);
        _tagSchemaMap.put(TAG_LENS, Schema.EXIF_ADDITIONAL_PROPERTIES);
        _tagSchemaMap.put(TAG_CAMERA_SERIAL_NUMBER, Schema.EXIF_ADDITIONAL_PROPERTIES);
        _tagSchemaMap.put(TAG_FIRMWARE, Schema.EXIF_ADDITIONAL_PROPERTIES);
        _tagSchemaMap.put(TAG_FOCAL_LENGTH, Schema.EXIF_SPECIFIC_PROPERTIES);
        _tagSchemaMap.put(TAG_APERTURE_VALUE, Schema.EXIF_SPECIFIC_PROPERTIES);
        _tagSchemaMap.put(TAG_EXPOSURE_PROGRAM, Schema.EXIF_SPECIFIC_PROPERTIES);
        _tagSchemaMap.put(TAG_DATETIME_ORIGINAL, Schema.EXIF_SPECIFIC_PROPERTIES);
        _tagSchemaMap.put(TAG_DATETIME_DIGITIZED, Schema.EXIF_SPECIFIC_PROPERTIES);

        _tagSchemaMap.put(TAG_RATING, Schema.XMP_PROPERTIES);
        _tagSchemaMap.put(TAG_LABEL, Schema.XMP_PROPERTIES);

        // this requires further research
        // _tagNameMap.put(TAG_TITLE, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        _tagSchemaMap.put(TAG_SUBJECT, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_DATE, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_TYPE, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_DESCRIPTION, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_RELATION, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_COVERAGE, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_CREATOR, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_PUBLISHER, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_CONTRIBUTOR, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_RIGHTS, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_FORMAT, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_IDENTIFIER, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_LANGUAGE, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_AUDIENCE, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_PROVENANCE, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_RIGHTS_HOLDER, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_INSTRUCTIONAL_METHOD, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_ACCRUAL_METHOD, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_ACCRUAL_PERIODICITY, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
        // _tagNameMap.put(TAG_ACCRUAL_POLICY, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES);
    }

    @Nullable
    private XMPMeta _xmpMeta;

    public XmpDirectory()
    {
        this.setDescriptor(new XmpDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Xmp";
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
    @Nullable
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
        super.setDate(tagType, value);
        XMPDateTime date = new XMPDateTimeImpl(value, TimeZone.getDefault());
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
