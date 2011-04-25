/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by dnoakes on 24-Apr-2011 19:37:21 using IntelliJ IDEA.
 */
package com.drew.metadata.xmp.test;

import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.lang.Rational;
import com.drew.metadata.Metadata;
import com.drew.metadata.xmp.XmpDirectory;
import com.drew.metadata.xmp.XmpReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Map;

public class XmpReaderTest
{
    private XmpDirectory _directory;

    @Before
    public void setUp() throws Exception
    {
        // use a known testing image
        File metadataFile = new File("Source/com/drew/metadata/xmp/test/withXmpAndIptc.metadata");
        JpegSegmentData jpegSegmentData = JpegSegmentData.fromFile(metadataFile);
        Metadata metadata = new Metadata();
        XmpReader reader = new XmpReader(jpegSegmentData.getSegment(JpegSegmentReader.SEGMENT_APP1, 1));
        reader.extract(metadata);
        Assert.assertTrue(metadata.containsDirectory(XmpDirectory.class));
        _directory = (XmpDirectory)metadata.getDirectory(XmpDirectory.class);
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
        Assert.assertEquals("24/1 70/1 0/0 0/0", _directory.getString(XmpDirectory.TAG_LENS_INFO));

//        Rational[] info = _directory.getRationalArray(XmpDirectory.TAG_LENS_INFO);
//        Assert.assertEquals(new Rational(24, 1), info[0]);
//        Assert.assertEquals(new Rational(70, 1), info[1]);
//        Assert.assertEquals(new Rational(0, 0), info[2]);
//        Assert.assertEquals(new Rational(0, 0), info[3]);
    }

    @Test
    public void testExtract_Lens() throws Exception
    {
        Assert.assertEquals("EF24-70mm f/2.8L USM", _directory.getString(XmpDirectory.TAG_LENS));
    }

    @Test
    public void testExtract_SerialNumber() throws Exception
    {
        Assert.assertEquals("380319450", _directory.getString(XmpDirectory.TAG_SERIAL));
    }

    @Test
    public void testExtract_Firmware() throws Exception
    {
        Assert.assertEquals("1.2.1", _directory.getString(XmpDirectory.TAG_FIRMWARE));
    }

    @Test
    public void testExtract_Maker() throws Exception
    {
        Assert.assertEquals("Canon", _directory.getString(XmpDirectory.TAG_MAKE));
    }

    @Test
    public void testExtract_Model() throws Exception
    {
        Assert.assertEquals("Canon EOS 7D", _directory.getString(XmpDirectory.TAG_MODEL));
    }

    @Test
    public void testExtract_ExposureTime() throws Exception
    {
        // Note XmpReader doesn't parse this as a rational even though it appears to be... need more examples
        Assert.assertEquals("1/125", _directory.getString(XmpDirectory.TAG_EXPOSURE_TIME));
//        Assert.assertEquals(new Rational(1, 125), _directory.getRational(XmpDirectory.TAG_EXPOSURE_TIME));
    }

    @Test
    public void testExtract_ExposureProgram() throws Exception
    {
        Assert.assertEquals(1, _directory.getInt(XmpDirectory.TAG_EXPOSURE_PROG));
    }

    @Test
    public void testExtract_FNumber() throws Exception
    {
        Assert.assertEquals(new Rational(11, 1), _directory.getRational(XmpDirectory.TAG_F_NUMBER));
    }

    @Test
    public void testExtract_FocalLength() throws Exception
    {
        Assert.assertEquals(new Rational(57, 1), _directory.getRational(XmpDirectory.TAG_FOCAL_LENGTH));
    }

    @Test
    public void testExtract_ShutterSpeed() throws Exception
    {
        Assert.assertEquals(new Rational(6965784, 1000000), _directory.getRational(XmpDirectory.TAG_SHUTTER_SPEED));
    }

    @Test
    public void testExtract_OriginalDateTime() throws Exception
    {
        Assert.assertEquals(new SimpleDateFormat("hh:mm:ss dd MMM yyyy").parse("11:41:35 12 Dec 2010"), _directory.getDate(XmpDirectory.TAG_DATETIME_ORIGINAL));
    }

    @Test
    public void testExtract_DigitizedDateTime() throws Exception
    {
        Assert.assertEquals(new SimpleDateFormat("hh:mm:ss dd MMM yyyy").parse("11:41:35 12 Dec 2010"), _directory.getDate(XmpDirectory.TAG_DATETIME_DIGITIZED));
    }

    @Test
    public void testGetXmpProperties() throws Exception
    {
        Map<String,String> propertyMap = _directory.getXmpProperties();
        for (String key : propertyMap.keySet())
        {
            Object value = propertyMap.get(key);
            System.out.println(key + " -> " + value + " (" + value.getClass().getName() + ")");
        }
    }
}
