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
package com.drew.metadata.xmp;

import com.adobe.xmp.XMPMeta;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;

import java.util.HashMap;
import java.util.Map;

/** @author Torsten Skadell, Drew Noakes http://drewnoakes.com */
public class XmpDirectory extends Directory
{
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

/*
    // dublin core properties
    // this requires further research
    public static int TAG_TITLE = 0x100;
    public static int TAG_SUBJECT = 0x1001;
    public static int TAG_DATE = 0x1002;
    public static int TAG_TYPE = 0x1003;
    public static int TAG_DESCRIPTION = 0x1004;
    public static int TAG_RELATION = 0x1005;
    public static int TAG_COVERAGE = 0x1006;
    public static int TAG_CREATOR = 0x1007;
    public static int TAG_PUBLISHER = 0x1008;
    public static int TAG_CONTRIBUTOR = 0x1009;
    public static int TAG_RIGHTS = 0x100A;
    public static int TAG_FORMAT = 0x100B;
    public static int TAG_IDENTIFIER = 0x100C;
    public static int TAG_LANGUAGE = 0x100D;
    public static int TAG_AUDIENCE = 0x100E;
    public static int TAG_PROVENANCE = 0x100F;
    public static int TAG_RIGHTS_HOLDER = 0x1010;
    public static int TAG_INSTRUCTIONAL_METHOD = 0x1011;
    public static int TAG_ACCRUAL_METHOD = 0x1012;
    public static int TAG_ACCRUAL_PERIODICITY = 0x1013;
    public static int TAG_ACCRUAL_POLICY = 0x1014;
*/

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();
    @NotNull
    private final Map<String, String> _propertyValueByPath = new HashMap<String, String>();

    static {
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
        
/*
        // this requires further research
        _tagNameMap.put(TAG_TITLE, "Title");
        _tagNameMap.put(TAG_SUBJECT, "Subject");
        _tagNameMap.put(TAG_DATE, "Date");
        _tagNameMap.put(TAG_TYPE, "Type");
        _tagNameMap.put(TAG_DESCRIPTION, "Description");
        _tagNameMap.put(TAG_RELATION, "Relation");
        _tagNameMap.put(TAG_COVERAGE, "Coverage");
        _tagNameMap.put(TAG_CREATOR, "Creator");
        _tagNameMap.put(TAG_PUBLISHER, "Publisher");
        _tagNameMap.put(TAG_CONTRIBUTOR, "Contributor");
        _tagNameMap.put(TAG_RIGHTS, "Rights");
        _tagNameMap.put(TAG_FORMAT, "Format");
        _tagNameMap.put(TAG_IDENTIFIER, "Identifier");
        _tagNameMap.put(TAG_LANGUAGE, "Language");
        _tagNameMap.put(TAG_AUDIENCE, "Audience");
        _tagNameMap.put(TAG_PROVENANCE, "Provenance");
        _tagNameMap.put(TAG_RIGHTS_HOLDER, "Rights Holder");
        _tagNameMap.put(TAG_INSTRUCTIONAL_METHOD, "Instructional Method");
        _tagNameMap.put(TAG_ACCRUAL_METHOD, "Accrual Method");
        _tagNameMap.put(TAG_ACCRUAL_PERIODICITY, "Accrual Periodicity");
        _tagNameMap.put(TAG_ACCRUAL_POLICY, "Accrual Policy");
*/
    }

    @Nullable
    private XMPMeta _xmpMeta;

    public XmpDirectory()
    {
        this.setDescriptor(new XmpDescriptor(this));
    }

    @NotNull
    public String getName()
    {
        return "Xmp";
    }

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
     * <p/>
     * This is required because XMP properties are represented as strings, whereas the rest of this library
     * uses integers for keys.
     */
    @NotNull
    public Map<String, String> getXmpProperties()
    {
        return _propertyValueByPath;
    }

    public void setXMPMeta(@NotNull XMPMeta xmpMeta)
    {
        _xmpMeta = xmpMeta;
    }

    /**
     * Gets the XMPMeta object used to populate this directory.  It can be used for more XMP-oriented operations.
     */
    @Nullable
    public XMPMeta getXMPMeta()
    {
        return _xmpMeta;
    }
}
