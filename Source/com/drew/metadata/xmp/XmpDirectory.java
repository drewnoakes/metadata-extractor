/*
 * Copyright 2002-2011 Drew Noakes
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

import com.drew.metadata.Directory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Torsten Skadell, Drew Noakes http://drewnoakes.com
 */
public class XmpDirectory extends Directory
{
	//	These are some Tags, belonging to xmp-data-tags
	//	The numeration is more like enums. The real xmp-tags are strings,
	//	so we do some kind off mapping here...
    public static final int TAG_MAKE 				= 0x0001;
    public static final int TAG_MODEL 				= 0x0002;
    public static final int TAG_EXPOSURE_TIME 		= 0x0003;
    public static final int TAG_SHUTTER_SPEED       = 0x0004;
    public static final int TAG_F_NUMBER 			= 0x0005;
    public static final int TAG_LENS_INFO 			= 0x0006;
    public static final int TAG_LENS 				= 0x0007;
    /** The serial number of the camera. */
    public static final int TAG_SERIAL 				= 0x0008;
    public static final int TAG_FIRMWARE			= 0x0009;
    public static final int TAG_FOCAL_LENGTH		= 0x000a;
    public static final int TAG_APERTURE_VALUE		= 0x000b;
    public static final int TAG_EXPOSURE_PROG		= 0x000c;
    public static final int TAG_DATETIME_ORIGINAL	= 0x000d;
    public static final int TAG_DATETIME_DIGITIZED	= 0x000e;

    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();
    private final Map<String, String> _propertyValueByPath = new HashMap<String, String>();

    static
    {
        _tagNameMap.put(TAG_MAKE, "Make");
        _tagNameMap.put(TAG_MODEL, "Model");
        _tagNameMap.put(TAG_EXPOSURE_TIME, "Exposure Time");
        _tagNameMap.put(TAG_SHUTTER_SPEED, "Shutter Speed Value");
        _tagNameMap.put(TAG_F_NUMBER, "F-Number");
        _tagNameMap.put(TAG_LENS_INFO, "Lens Information");
        _tagNameMap.put(TAG_LENS, "Lens");
        _tagNameMap.put(TAG_SERIAL, "Serial Number");
        _tagNameMap.put(TAG_FIRMWARE, "Firmware");
        _tagNameMap.put(TAG_FOCAL_LENGTH, "Focal Length");
        _tagNameMap.put(TAG_APERTURE_VALUE, "Aperture Value");
        _tagNameMap.put(TAG_EXPOSURE_PROG, "Exposure Program");
        _tagNameMap.put(TAG_DATETIME_ORIGINAL, "Date/Time Original");
        _tagNameMap.put(TAG_DATETIME_DIGITIZED, "Date/Time Digitized");
    }

    public XmpDirectory()
    {
        this.setDescriptor(new XmpDescriptor(this));
    }

    public String getName()
    {
        return "Xmp";
    }

    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    void addProperty(String path, String value)
    {
        _propertyValueByPath.put(path, value);
    }

    /**
     * Gets a map of all XMP properties in this directory, not just the known ones.
     * <p/>
     * This is required because XMP properties are represented as strings, whereas the rest of this library
     * uses integers for keys.
     * @return
     */
    public Map<String, String> getXmpProperties()
    {
        return _propertyValueByPath;
    }
}
