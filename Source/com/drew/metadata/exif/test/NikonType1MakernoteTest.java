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
package com.drew.metadata.exif.test;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.lang.Rational;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import com.drew.metadata.exif.NikonType1MakernoteDirectory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class NikonType1MakernoteTest
{
    private NikonType1MakernoteDirectory _nikonDirectory;
    private ExifIFD0Directory _exifIFD0Directory;
    private ExifSubIFDDirectory _exifSubIFDDirectory;
    private ExifThumbnailDirectory _thumbDirectory;

    /*
        [Interoperability] Interoperability Index = Recommended Exif Interoperability Rules (ExifR98)
        [Interoperability] Interoperability Version = 1.00
        [Jpeg] Data Precision = 8 bits
        [Jpeg] Image Width = 600 pixels
        [Jpeg] Image Height = 800 pixels
        [Jpeg] Number of Components = 3
        [Jpeg] Component 1 = Y component: Quantization table 0, Sampling factors 1 horiz/1 vert
        [Jpeg] Component 2 = Cb component: Quantization table 1, Sampling factors 1 horiz/1 vert
        [Jpeg] Component 3 = Cr component: Quantization table 1, Sampling factors 1 horiz/1 vert
    */

    @Before
    public void setUp() throws Exception
    {
        File nikonJpeg = new File("Source/com/drew/metadata/exif/test/nikonMakernoteType1.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(nikonJpeg);
        _nikonDirectory = metadata.getDirectory(NikonType1MakernoteDirectory.class);
        _exifSubIFDDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);
        _exifIFD0Directory = metadata.getDirectory(ExifIFD0Directory.class);
        _thumbDirectory = metadata.getDirectory(ExifThumbnailDirectory.class);
    }

    /*
        [Nikon Makernote] Makernote Unknown 1 = 08.00
        [Nikon Makernote] Quality = Unknown (12)
        [Nikon Makernote] Color Mode = Color
        [Nikon Makernote] Image Adjustment = Contrast +
        [Nikon Makernote] CCD Sensitivity = ISO80
        [Nikon Makernote] White Balance = Auto
        [Nikon Makernote] Focus = 0
        [Nikon Makernote] Makernote Unknown 2 =
        [Nikon Makernote] Digital Zoom = No digital zoom
        [Nikon Makernote] Fisheye Converter = None
        [Nikon Makernote] Makernote Unknown 3 = 0 0 16777216 0 -1609193200 0 34833 6931 16178 4372 4372 -972290529 -921882880 15112 0 0 1151495 252903424 17 0 0 844038208 55184128 218129428 1476410198 370540566 -250604010 16711749 204629079 1729
    */
    @Test
    public void testNikonMakernote_MatchesKnownValues() throws Exception
    {
        Assert.assertTrue(_nikonDirectory.getTagCount() > 0);
        Assert.assertEquals(8, _nikonDirectory.getDouble(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_UNKNOWN_1), 0.0001);
        Assert.assertEquals(12, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_QUALITY));
        Assert.assertEquals(1, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_COLOR_MODE));
        Assert.assertEquals(3, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_IMAGE_ADJUSTMENT));
        Assert.assertEquals(0, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CCD_SENSITIVITY));
        Assert.assertEquals(0, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_WHITE_BALANCE));
        Assert.assertEquals(0, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_FOCUS));
        Assert.assertEquals("", _nikonDirectory.getString(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_UNKNOWN_2));
        Assert.assertEquals(0, _nikonDirectory.getDouble(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_DIGITAL_ZOOM), 0.0001);
        Assert.assertEquals(0, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CONVERTER));
        int[] unknown3 = _nikonDirectory.getIntArray(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_UNKNOWN_3);
        int[] expected = new int[] { 0, 0, 16777216, 0, -1609193200, 0, 34833, 6931, 16178, 4372, 4372, -972290529, -921882880, 15112, 0, 0, 1151495, 252903424, 17, 0, 0, 844038208, 55184128, 218129428, 1476410198, 370540566, -250604010, 16711749, 204629079, 1729 };
        Assert.assertNotNull(unknown3);
        Assert.assertEquals(expected.length, unknown3.length);
        for (int i = 0; i<expected.length; i++) {
            Assert.assertEquals(expected[i], unknown3[i]);
        }
    }

    /*
        [Exif] Image Description =
        [Exif] Make = NIKON
        [Exif] Model = E950
        [Exif] Orientation = top, left side
        [Exif] X Resolution = 300 dots per inch
        [Exif] Y Resolution = 300 dots per inch
        [Exif] Resolution Unit = Inch
        [Exif] Software = v981-79
        [Exif] Date/Time = 2001:04:06 11:51:40
        [Exif] YCbCr Positioning = Datum point
        [Exif] Exposure Time = 1/77 sec
        [Exif] F-Number = F5.5
        [Exif] Exposure Program = Program normal
        [Exif] ISO Speed Ratings = 80
        [Exif] Exif Version = 2.10
        [Exif] Date/Time Original = 2001:04:06 11:51:40
        [Exif] Date/Time Digitized = 2001:04:06 11:51:40
        [Exif] Components Configuration = YCbCr
        [Exif] Compressed Bits Per Pixel = 4 bits/pixel
        [Exif] Exposure Bias Value = 0
        [Exif] Max Aperture Value = F2.5
        [Exif] Metering Mode = Multi-segment
        [Exif] Light Source = Unknown
        [Exif] Flash = No flash fired
        [Exif] Focal Length = 12.8 mm
        [Exif] User Comment =
        [Exif] FlashPix Version = 1.00
        [Exif] Color Space = sRGB
        [Exif] Exif Image Width = 1600 pixels
        [Exif] Exif Image Height = 1200 pixels
        [Exif] File Source = Digital Still Camera (DSC)
        [Exif] Scene Type = Directly photographed image
        [Exif] Compression = JPEG compression
        [Exif] Thumbnail Offset = 2036 bytes
        [Exif] Thumbnail Length = 4662 bytes
        [Exif] Thumbnail Data = [4662 bytes of thumbnail data]
    */
    @Test
    public void testExifDirectory_MatchesKnownValues() throws Exception
    {
        Assert.assertEquals("          ", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_IMAGE_DESCRIPTION));
        Assert.assertEquals("NIKON", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_MAKE));
        Assert.assertEquals("E950", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_MODEL));
        Assert.assertEquals(1, _exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION));
        Assert.assertEquals(300, _exifIFD0Directory.getDouble(ExifIFD0Directory.TAG_X_RESOLUTION), 0.001);
        Assert.assertEquals(300, _exifIFD0Directory.getDouble(ExifIFD0Directory.TAG_Y_RESOLUTION), 0.001);
        Assert.assertEquals(2, _exifIFD0Directory.getInt(ExifIFD0Directory.TAG_RESOLUTION_UNIT));
        Assert.assertEquals("v981-79", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_SOFTWARE));
        Assert.assertEquals("2001:04:06 11:51:40", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_DATETIME));
        Assert.assertEquals(2, _exifIFD0Directory.getInt(ExifIFD0Directory.TAG_YCBCR_POSITIONING));

        Assert.assertEquals(new Rational(1, 77), _exifSubIFDDirectory.getRational(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
        Assert.assertEquals(5.5, _exifSubIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_FNUMBER), 0.001);
        Assert.assertEquals(2, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXPOSURE_PROGRAM));
        Assert.assertEquals(80, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
        Assert.assertEquals("48 50 49 48", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_EXIF_VERSION));
        Assert.assertEquals("2001:04:06 11:51:40", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED));
        Assert.assertEquals("2001:04:06 11:51:40", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
        Assert.assertEquals("1 2 3 0", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_COMPONENTS_CONFIGURATION));
        Assert.assertEquals(4, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_COMPRESSED_AVERAGE_BITS_PER_PIXEL));
        Assert.assertEquals(0, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXPOSURE_BIAS));
        // this 2.6 *apex*, which is F2.5
        Assert.assertEquals(2.6, _exifSubIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_MAX_APERTURE), 0.001);
        Assert.assertEquals(5, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_METERING_MODE));
        Assert.assertEquals(0, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_WHITE_BALANCE));
        Assert.assertEquals(0, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_FLASH));
        Assert.assertEquals(12.8, _exifSubIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_FOCAL_LENGTH), 0.001);
        Assert.assertEquals("0 0 0 0 0 0 0 0 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_USER_COMMENT));
        Assert.assertEquals("48 49 48 48", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_FLASHPIX_VERSION));
        Assert.assertEquals(1, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_COLOR_SPACE));
        Assert.assertEquals(1600, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH));
        Assert.assertEquals(1200, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT));
        Assert.assertEquals(3, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_FILE_SOURCE));
        Assert.assertEquals(1, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_SCENE_TYPE));

        Assert.assertEquals(6, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_COMPRESSION));
        Assert.assertEquals(2036, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET));
        Assert.assertEquals(4662, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH));
    }
}
