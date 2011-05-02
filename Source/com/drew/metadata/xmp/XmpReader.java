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

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;

import java.util.Calendar;

/**
 * Extracts Xmp data from a JPEG header segment.
 * <p/>
 * The extractions is done with adobe's XmpCore-Library (XMP-Toolkit)
 * Copyright (c) 1999 - 2007, Adobe Systems Incorporated All rights reserved.
 *
 * @author Torsten Skadell, Drew Noakes http://drewnoakes.com
 */
public class XmpReader implements MetadataReader
{
    private static final int FMT_STRING = 1;
    private static final int FMT_RATIONAL = 2;
    private static final int FMT_INT = 3;
    private static final int FMT_DOUBLE = 4;

    @NotNull
    private static final String SCHEMA_EXIF_SPECIFIC_PROPERTIES = "http://ns.adobe.com/exif/1.0/";
    @NotNull
    private static final String SCHEMA_EXIF_ADDITIONAL_PROPERTIES = "http://ns.adobe.com/exif/1.0/aux/";
    @NotNull
    private static final String SCHEMA_EXIF_TIFF_PROPERTIES = "http://ns.adobe.com/tiff/1.0/";

    /**
     * Performs the XMP data extraction, adding found values to the specified instance of <code>Metadata</code>.
     * The extraction is done with Adobe's XmpCore-Lib (XMP-Toolkit)
     */
    public void extract(@NotNull final byte[] data, @NotNull Metadata metadata)
    {
        if (data == null)
            throw new NullPointerException("data");
        if (metadata == null)
            throw new NullPointerException("metadata");

        // once we know there's some data, create the directory and start working on it
        XmpDirectory directory = metadata.getOrCreateDirectory(XmpDirectory.class);

        // check for the header length
        if (data.length <= 30) {
            directory.addError("Xmp data segment must contain at least 30 bytes");
            return;
        }

        // check for the header preamble
        if (!"http://ns.adobe.com/xap/1.0/\0".equals(new String(data, 0, 29))) {
            directory.addError("Xmp data segment doesn't begin with 'http://ns.adobe.com/xap/1.0/'");
            return;
        }

        try {
            // the parser starts at offset of 29 Bytes
            byte[] xmpBuffer = new byte[data.length - 29];
            System.arraycopy(data, 29, xmpBuffer, 0, data.length - 29);

            // use XMPMetaFactory to create a XMPMeta instance based on the parsed data buffer
            XMPMeta xmpMeta = XMPMetaFactory.parseFromBuffer(xmpBuffer);

            // read all the tags and send them to the directory
            // I've added some popular tags, feel free to add more tags
            processXmpTag(xmpMeta, directory, SCHEMA_EXIF_ADDITIONAL_PROPERTIES, "aux:LensInfo", XmpDirectory.TAG_LENS_INFO, FMT_STRING);
            processXmpTag(xmpMeta, directory, SCHEMA_EXIF_ADDITIONAL_PROPERTIES, "aux:Lens", XmpDirectory.TAG_LENS, FMT_STRING);
            processXmpTag(xmpMeta, directory, SCHEMA_EXIF_ADDITIONAL_PROPERTIES, "aux:SerialNumber", XmpDirectory.TAG_CAMERA_SERIAL_NUMBER, FMT_STRING);
            processXmpTag(xmpMeta, directory, SCHEMA_EXIF_ADDITIONAL_PROPERTIES, "aux:Firmware", XmpDirectory.TAG_FIRMWARE, FMT_STRING);

            processXmpTag(xmpMeta, directory, SCHEMA_EXIF_TIFF_PROPERTIES, "tiff:Make", XmpDirectory.TAG_MAKE, FMT_STRING);
            processXmpTag(xmpMeta, directory, SCHEMA_EXIF_TIFF_PROPERTIES, "tiff:Model", XmpDirectory.TAG_MODEL, FMT_STRING);

            processXmpTag(xmpMeta, directory, SCHEMA_EXIF_SPECIFIC_PROPERTIES, "exif:ExposureTime", XmpDirectory.TAG_EXPOSURE_TIME, FMT_STRING);
            processXmpTag(xmpMeta, directory, SCHEMA_EXIF_SPECIFIC_PROPERTIES, "exif:ExposureProgram", XmpDirectory.TAG_EXPOSURE_PROGRAM, FMT_INT);
            processXmpTag(xmpMeta, directory, SCHEMA_EXIF_SPECIFIC_PROPERTIES, "exif:ApertureValue", XmpDirectory.TAG_APERTURE_VALUE, FMT_RATIONAL);
            processXmpTag(xmpMeta, directory, SCHEMA_EXIF_SPECIFIC_PROPERTIES, "exif:FNumber", XmpDirectory.TAG_F_NUMBER, FMT_RATIONAL);
            processXmpTag(xmpMeta, directory, SCHEMA_EXIF_SPECIFIC_PROPERTIES, "exif:FocalLength", XmpDirectory.TAG_FOCAL_LENGTH, FMT_RATIONAL);
            processXmpTag(xmpMeta, directory, SCHEMA_EXIF_SPECIFIC_PROPERTIES, "exif:ShutterSpeedValue", XmpDirectory.TAG_SHUTTER_SPEED, FMT_RATIONAL);
            processXmpDateTag(xmpMeta, directory, SCHEMA_EXIF_SPECIFIC_PROPERTIES, "exif:DateTimeOriginal", XmpDirectory.TAG_DATETIME_ORIGINAL);
            processXmpDateTag(xmpMeta, directory, SCHEMA_EXIF_SPECIFIC_PROPERTIES, "exif:DateTimeDigitized", XmpDirectory.TAG_DATETIME_DIGITIZED);

            for (XMPIterator iterator = xmpMeta.iterator(); iterator.hasNext();) {
                XMPPropertyInfo propInfo = (XMPPropertyInfo) iterator.next();
                String path = propInfo.getPath();
                Object value = propInfo.getValue();
                if (path != null && value != null)
                    directory.addProperty(path, value.toString());
            }

        } catch (XMPException e) {
            directory.addError("Error parsing XMP segment: " + e.getMessage());
        }
    }

    /** Reads an property value with given namespace URI and property name. Add property value to directory if exists */
    private void processXmpTag(@NotNull XMPMeta meta, @NotNull XmpDirectory directory, @NotNull String schemaNS, @NotNull String propName, int tagType, int formatCode) throws XMPException
    {
        String property = meta.getPropertyString(schemaNS, propName);

        if (property == null)
            return;

        switch (formatCode) {
            case FMT_RATIONAL:
                String[] rationalParts = property.split("/", 2);

                if (rationalParts.length == 2) {
                    Rational rational = new Rational((long) Float.parseFloat(rationalParts[0]), (long) Float.parseFloat(rationalParts[1]));
                    directory.setRational(tagType, rational);
                } else {
                    directory.addError("Error in rational format for tag " + tagType);
                }
                break;
            case FMT_INT:
                directory.setInt(tagType, Integer.valueOf(property));
                break;
            case FMT_DOUBLE:
                directory.setDouble(tagType, Double.valueOf(property));
                break;
            case FMT_STRING:
                directory.setString(tagType, property);
                break;
            default:
                directory.addError("Unknown format code " + formatCode + " for tag " + tagType);
        }
    }

    void processXmpDateTag(@NotNull XMPMeta meta, @NotNull XmpDirectory directory, @NotNull String schemaNS, @NotNull String propName, int tagType) throws XMPException
    {
        Calendar cal = meta.getPropertyCalendar(schemaNS, propName);

        if (cal == null)
            return;

        directory.setDate(tagType, cal.getTime());
    }
}
