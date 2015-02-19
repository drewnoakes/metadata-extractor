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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Calendar;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.impl.XMPMetaImpl;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.Schema;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Extracts XMP data from a JPEG header segment.
 * <p>
 * The extraction is done with Adobe's XmpCore-Library (XMP-Toolkit)
 * Copyright (c) 1999 - 2007, Adobe Systems Incorporated All rights reserved.
 *
 * @author Torsten Skadell
 * @author Drew Noakes https://drewnoakes.com
 */
public class XmpReader implements JpegSegmentMetadataReader
{
    private static final int FMT_STRING = 1;
    private static final int FMT_RATIONAL = 2;
    private static final int FMT_INT = 3;
    private static final int FMT_DOUBLE = 4;
	private static final int FMT_STRING_ARRAY = 5;
    /**
     * XMP tag namespace.
     * TODO the older "xap", "xapBJ", "xapMM" or "xapRights" namespace prefixes should be translated to the newer "xmp", "xmpBJ", "xmpMM" and "xmpRights" prefixes for use in family 1 group names
     */
    @NotNull
    private static final String SCHEMA_XMP_PROPERTIES = "http://ns.adobe.com/xap/1.0/";
    @NotNull
    private static final String SCHEMA_EXIF_SPECIFIC_PROPERTIES = "http://ns.adobe.com/exif/1.0/";
    @NotNull
    private static final String SCHEMA_EXIF_ADDITIONAL_PROPERTIES = "http://ns.adobe.com/exif/1.0/aux/";
    @NotNull
    private static final String SCHEMA_EXIF_TIFF_PROPERTIES = "http://ns.adobe.com/tiff/1.0/";
    @NotNull
    public static final String XMP_JPEG_PREAMBLE = "http://ns.adobe.com/xap/1.0/\0";
//    @NotNull
//    private static final String SCHEMA_DUBLIN_CORE_SPECIFIC_PROPERTIES = "http://purl.org/dc/elements/1.1/";

    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes()
    {
        return Arrays.asList(JpegSegmentType.APP1);
    }

    /**
     * Version specifically for dealing with XMP found in JPEG segments. This form of XMP has a peculiar preamble, which
     * must be removed before parsing the XML.
     *
     * @param segments The byte array from which the metadata should be extracted.
     * @param metadata The {@link Metadata} object into which extracted values should be merged.
     * @param segmentType The {@link JpegSegmentType} being read.
     */
    public void readJpegSegments(@NotNull Iterable<byte[]> segments, @NotNull Metadata metadata, @NotNull JpegSegmentType segmentType)
    {
        for (byte[] segmentBytes : segments) {
            // XMP in a JPEG file has an identifying preamble which is not valid XML
            final int preambleLength = XMP_JPEG_PREAMBLE.length();

            if (segmentBytes.length < preambleLength || !XMP_JPEG_PREAMBLE.equalsIgnoreCase(new String(segmentBytes, 0, preambleLength)))
                continue;

            byte[] xmlBytes = new byte[segmentBytes.length - preambleLength];
            System.arraycopy(segmentBytes, preambleLength, xmlBytes, 0, xmlBytes.length);
            extract(xmlBytes, metadata);
        }
    }

    /**
     * Performs the XMP data extraction, adding found values to the specified instance of {@link Metadata}.
     * <p>
     * The extraction is done with Adobe's XMPCore library.
     */
    public void extract(@NotNull final byte[] xmpBytes, @NotNull Metadata metadata)
    {
        XmpDirectory directory = new XmpDirectory();

        try {
            XMPMeta xmpMeta = XMPMetaFactory.parseFromBuffer(xmpBytes);
            processXmpTags(directory, xmpMeta);
        } catch (XMPException e) {
            directory.addError("Error processing XMP data: " + e.getMessage());
        }

        if (!directory.isEmpty())
            metadata.addDirectory(directory);
    }

    /**
     * Performs the XMP data extraction, adding found values to the specified instance of {@link Metadata}.
     * <p>
     * The extraction is done with Adobe's XMPCore library.
     */
    public void extract(@NotNull final String xmpString, @NotNull Metadata metadata)
    {
        XmpDirectory directory = new XmpDirectory();

        try {
            XMPMeta xmpMeta = XMPMetaFactory.parseFromString(xmpString);
            processXmpTags(directory, xmpMeta);
        } catch (XMPException e) {
            directory.addError("Error processing XMP data: " + e.getMessage());
        }

        if (!directory.isEmpty())
            metadata.addDirectory(directory);
    }

    private static void processXmpTags(XmpDirectory directory, XMPMeta xmpMeta) throws XMPException
    {
        // store the XMPMeta object on the directory in case others wish to use it
        directory.setXMPMeta(xmpMeta);

        // read all the tags and send them to the directory
        // I've added some popular tags, feel free to add more tags
        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_LENS_INFO, FMT_STRING);
        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_LENS, FMT_STRING);
        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_CAMERA_SERIAL_NUMBER, FMT_STRING);
        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_FIRMWARE, FMT_STRING);

        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_MAKE, FMT_STRING);
        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_MODEL, FMT_STRING);

        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_EXPOSURE_TIME, FMT_STRING);
        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_EXPOSURE_PROGRAM, FMT_INT);
        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_APERTURE_VALUE, FMT_RATIONAL);
        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_F_NUMBER, FMT_RATIONAL);
        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_FOCAL_LENGTH, FMT_RATIONAL);
        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_SHUTTER_SPEED, FMT_RATIONAL);

        processXmpDateTag(xmpMeta, directory, XmpDirectory.TAG_DATETIME_ORIGINAL);
        processXmpDateTag(xmpMeta, directory, XmpDirectory.TAG_DATETIME_DIGITIZED);

        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_RATING, FMT_DOUBLE);
        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_LABEL, FMT_STRING);

        // this requires further research
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:title", XmpDirectory.TAG_TITLE, FMT_STRING);
        processXmpTag(xmpMeta, directory, XmpDirectory.TAG_SUBJECT, FMT_STRING_ARRAY);
        // processXmpDateTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:date", XmpDirectory.TAG_DATE);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:type", XmpDirectory.TAG_TYPE, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:description", XmpDirectory.TAG_DESCRIPTION, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:relation", XmpDirectory.TAG_RELATION, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:coverage", XmpDirectory.TAG_COVERAGE, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:creator", XmpDirectory.TAG_CREATOR, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:publisher", XmpDirectory.TAG_PUBLISHER, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:contributor", XmpDirectory.TAG_CONTRIBUTOR, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:rights", XmpDirectory.TAG_RIGHTS, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:format", XmpDirectory.TAG_FORMAT, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:identifier", XmpDirectory.TAG_IDENTIFIER, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:language", XmpDirectory.TAG_LANGUAGE, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:audience", XmpDirectory.TAG_AUDIENCE, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:provenance", XmpDirectory.TAG_PROVENANCE, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:rightsHolder", XmpDirectory.TAG_RIGHTS_HOLDER, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:instructionalMethod", XmpDirectory.TAG_INSTRUCTIONAL_METHOD,
        // FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:accrualMethod", XmpDirectory.TAG_ACCRUAL_METHOD, FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:accrualPeriodicity", XmpDirectory.TAG_ACCRUAL_PERIODICITY,
        // FMT_STRING);
        // processXmpTag(xmpMeta, directory, Schema.DUBLIN_CORE_SPECIFIC_PROPERTIES, "dc:accrualPolicy", XmpDirectory.TAG_ACCRUAL_POLICY, FMT_STRING);

        for (XMPIterator iterator = xmpMeta.iterator(); iterator.hasNext(); ) {
            XMPPropertyInfo propInfo = (XMPPropertyInfo) iterator.next();
            String path = propInfo.getPath();
            String value = propInfo.getValue();
            if (path != null && value != null)
                directory.addProperty(path, value);
        }
    }

    /**
     * Reads an property value with given namespace URI and property name. Add property value to directory if exists
     */
    private static void processXmpTag(@NotNull XMPMeta meta, @NotNull XmpDirectory directory, int tagType, int formatCode) throws XMPException
    {
        String schemaNS = XmpDirectory._tagSchemaMap.get(tagType);
        String propName = XmpDirectory._tagPropNameMap.get(tagType);
        String property = meta.getPropertyString(schemaNS, propName);

        if (property == null)
            return;

        switch (formatCode) {
            case FMT_RATIONAL:
                String[] rationalParts = property.split("/", 2);
                if (rationalParts.length == 2) {
                    try {
                        Rational rational = new Rational((long) Float.parseFloat(rationalParts[0]), (long) Float.parseFloat(rationalParts[1]));
                        directory.setRational(tagType, rational);
                    } catch (NumberFormatException ex) {
                        directory.addError(String.format("Unable to parse XMP property %s as a Rational.", propName));
                    }
                } else {
                    directory.addError("Error in rational format for tag " + tagType);
                }
                break;
            case FMT_INT:
                try {
                    directory.setInt(tagType, Integer.valueOf(property));
                } catch (NumberFormatException ex) {
                    directory.addError(String.format("Unable to parse XMP property %s as an int.", propName));
                }
                break;
            case FMT_DOUBLE:
                try {
                    directory.setDouble(tagType, Double.valueOf(property));
                } catch (NumberFormatException ex) {
                    directory.addError(String.format("Unable to parse XMP property %s as an double.", propName));
                }
                break;
            case FMT_STRING:
                directory.setString(tagType, property);
                break;
            case FMT_STRING_ARRAY:
                //XMP iterators are 1-based
                int count = meta.countArrayItems(schemaNS, propName);
                String[] array = new String[count];
                for ( int i = 1; i <= count; ++i)
                {
                    array[i-1] = meta.getArrayItem(schemaNS, propName, i).getValue();
                }
                directory.setStringArray(tagType, array);
                break;
            default:
                directory.addError(String.format("Unknown format code %d for tag %d", formatCode, tagType));
        }
    }

    @SuppressWarnings({"SameParameterValue"})
    private static void processXmpDateTag(@NotNull XMPMeta meta, @NotNull XmpDirectory directory, int tagType)
            throws XMPException
    {
        String schemaNS = XmpDirectory._tagSchemaMap.get(tagType);
        String propName = XmpDirectory._tagPropNameMap.get(tagType);
        Calendar cal = meta.getPropertyCalendar(schemaNS, propName);

        if (cal != null) {
            directory.setDate(tagType, cal.getTime());
        }
    }
}
