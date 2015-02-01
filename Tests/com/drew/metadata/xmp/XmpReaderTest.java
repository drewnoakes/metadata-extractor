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

import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.Rational;
import com.drew.metadata.Metadata;
import com.drew.tools.FileUtil;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class XmpReaderTest
{
    private XmpDirectory _directory;

    @Before
    public void setUp() throws Exception
    {
        Metadata metadata = new Metadata();
        List<byte[]> jpegSegments = new ArrayList<byte[]>();
        jpegSegments.add(FileUtil.readBytes("Tests/Data/withXmpAndIptc.jpg.app1.1"));
        new XmpReader().readJpegSegments(jpegSegments, metadata, JpegSegmentType.APP1);

        Collection<XmpDirectory> xmpDirectories = metadata.getDirectoriesOfType(XmpDirectory.class);

        assertNotNull(xmpDirectories);
        assertEquals(1, xmpDirectories.size());

        _directory = xmpDirectories.iterator().next();

        assertFalse(_directory.hasErrors());
    }

    /*
    [Xmp] Lens Information = 24/1 70/1 0/0 0/0
    [Xmp] Lens = EF24-70mm f/2.8L USM
    [Xmp] Serial Number = 380319450
    [Xmp] Firmware = 1.2.1
    [Xmp] Make = Canon
    [Xmp] Model = Canon EOS 7D
    [Xmp] Exposure Time = 1/125 sec
    [Xmp] Exposure Program = Manual control
    [Xmp] Aperture Value = F11
    [Xmp] F-Number = F11
    [Xmp] Focal Length = 57.0 mm
    [Xmp] Shutter Speed Value = 1/124 sec
    [Xmp] Date/Time Original = Sun Dec 12 11:41:35 GMT 2010
    [Xmp] Date/Time Digitized = Sun Dec 12 11:41:35 GMT 2010
    */

    @Test
    public void testExtract_LensInformation() throws Exception
    {
        // Note that this tag really holds a rational array, but XmpReader doesn't parse arrays
        assertEquals("24/1 70/1 0/0 0/0", _directory.getString(XmpDirectory.TAG_LENS_INFO));

//        Rational[] info = _directory.getRationalArray(XmpDirectory.TAG_LENS_INFO);
//        assertEquals(new Rational(24, 1), info[0]);
//        assertEquals(new Rational(70, 1), info[1]);
//        assertEquals(new Rational(0, 0), info[2]);
//        assertEquals(new Rational(0, 0), info[3]);
    }

    @Test
    public void testExtract_HasXMPMeta() throws Exception
    {
        assertNotNull(_directory.getXMPMeta());
    }

    @Test
    public void testExtract_Lens() throws Exception
    {
        assertEquals("EF24-70mm f/2.8L USM", _directory.getString(XmpDirectory.TAG_LENS));
    }

/*
    // this requires further research

    @Test
    public void testExtract_Format() throws Exception
    {
        assertEquals("image/tiff", _directory.getString(XmpDirectory.TAG_FORMAT));
    }

    @Test
    public void testExtract_Creator() throws Exception
    {
        assertEquals("", _directory.getString(XmpDirectory.TAG_CREATOR));
    }

    @Test
    public void testExtract_Rights() throws Exception
    {
        assertEquals("", _directory.getString(XmpDirectory.TAG_RIGHTS));
    }

    @Test
    public void testExtract_Description() throws Exception
    {
        assertEquals("", _directory.getString(XmpDirectory.TAG_DESCRIPTION));
    }
*/

    @Test
    public void testExtract_SerialNumber() throws Exception
    {
        assertEquals("380319450", _directory.getString(XmpDirectory.TAG_CAMERA_SERIAL_NUMBER));
    }

    @Test
    public void testExtract_Firmware() throws Exception
    {
        assertEquals("1.2.1", _directory.getString(XmpDirectory.TAG_FIRMWARE));
    }

    @Test
    public void testExtract_Maker() throws Exception
    {
        assertEquals("Canon", _directory.getString(XmpDirectory.TAG_MAKE));
    }

    @Test
    public void testExtract_Model() throws Exception
    {
        assertEquals("Canon EOS 7D", _directory.getString(XmpDirectory.TAG_MODEL));
    }

    @Test
    public void testExtract_ExposureTime() throws Exception
    {
        // Note XmpReader doesn't parse this as a rational even though it appears to be... need more examples
        assertEquals("1/125", _directory.getString(XmpDirectory.TAG_EXPOSURE_TIME));
//        assertEquals(new Rational(1, 125), _directory.getRational(XmpDirectory.TAG_EXPOSURE_TIME));
    }

    @Test
    public void testExtract_ExposureProgram() throws Exception
    {
        assertEquals(1, _directory.getInt(XmpDirectory.TAG_EXPOSURE_PROGRAM));
    }

    @Test
    public void testExtract_FNumber() throws Exception
    {
        assertEquals(new Rational(11, 1), _directory.getRational(XmpDirectory.TAG_F_NUMBER));
    }

    @Test
    public void testExtract_FocalLength() throws Exception
    {
        assertEquals(new Rational(57, 1), _directory.getRational(XmpDirectory.TAG_FOCAL_LENGTH));
    }

    @Test
    public void testExtract_ShutterSpeed() throws Exception
    {
        assertEquals(new Rational(6965784, 1000000), _directory.getRational(XmpDirectory.TAG_SHUTTER_SPEED));
    }

    @Test
    public void testExtract_OriginalDateTime() throws Exception
    {
        final Date actual = _directory.getDate(XmpDirectory.TAG_DATETIME_ORIGINAL);

        // Underlying string value (in XMP data) is: 2010-12-12T12:41:35.00+01:00

        assertEquals(new SimpleDateFormat("hh:mm:ss dd MM yyyy Z").parse("11:41:35 12 12 2010 +0000"), actual);
//        assertEquals(new SimpleDateFormat("HH:mm:ss dd MMM yyyy Z").parse("12:41:35 12 Dec 2010 +0100"), actual);

        Calendar calendar = new GregorianCalendar(2010, 12-1, 12, 11, 41, 35);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        assertEquals(calendar.getTime(), actual);
    }

    @Test
    public void testExtract_DigitizedDateTime() throws Exception
    {
        final Date actual = _directory.getDate(XmpDirectory.TAG_DATETIME_DIGITIZED);

        // Underlying string value (in XMP data) is: 2010-12-12T12:41:35.00+01:00

        assertEquals(new SimpleDateFormat("hh:mm:ss dd MM yyyy Z").parse("11:41:35 12 12 2010 +0000"), actual);
//        assertEquals(new SimpleDateFormat("HH:mm:ss dd MMM yyyy Z").parse("12:41:35 12 Dec 2010 +0100"), actual);

        Calendar calendar = new GregorianCalendar(2010, 12-1, 12, 11, 41, 35);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        assertEquals(calendar.getTime(), actual);
    }

    @Test
    public void testGetXmpProperties() throws Exception
    {
        Map<String,String> propertyMap = _directory.getXmpProperties();

        assertEquals(179, propertyMap.size());

        assertTrue(propertyMap.containsKey("photoshop:Country"));
        assertEquals("Deutschland", propertyMap.get("photoshop:Country"));

        assertTrue(propertyMap.containsKey("tiff:ImageLength"));
        assertEquals("900", propertyMap.get("tiff:ImageLength"));
    }
}
