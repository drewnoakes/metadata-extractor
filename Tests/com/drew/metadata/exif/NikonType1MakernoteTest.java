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
package com.drew.metadata.exif;

import com.drew.lang.Rational;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.makernotes.NikonType1MakernoteDirectory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
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
        Metadata metadata = ExifReaderTest.processBytes("Tests/Data/nikonMakernoteType1.jpg.app1");

        _nikonDirectory = metadata.getFirstDirectoryOfType(NikonType1MakernoteDirectory.class);
        _exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        _exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        _thumbDirectory = metadata.getFirstDirectoryOfType(ExifThumbnailDirectory.class);
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
        [Nikon Makernote] Makernote Unknown 3 = 0 0 16777216 0 2685774096 0 34833 6931 16178 4372 4372 3322676767 3373084416 15112 0 0 1151495 252903424 17 0 0 844038208 55184128 218129428 1476410198 370540566 4044363286 16711749 204629079 1729
    */
    @Test
    public void testNikonMakernote_MatchesKnownValues() throws Exception
    {
        assertTrue(_nikonDirectory.getTagCount() > 0);
        assertEquals(8, _nikonDirectory.getDouble(NikonType1MakernoteDirectory.TAG_UNKNOWN_1), 0.0001);
        assertEquals(12, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_QUALITY));
        assertEquals(1, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_COLOR_MODE));
        assertEquals(3, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_IMAGE_ADJUSTMENT));
        assertEquals(0, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_CCD_SENSITIVITY));
        assertEquals(0, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_WHITE_BALANCE));
        assertEquals(0, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_FOCUS));
        assertEquals("", _nikonDirectory.getString(NikonType1MakernoteDirectory.TAG_UNKNOWN_2));
        assertEquals(0, _nikonDirectory.getDouble(NikonType1MakernoteDirectory.TAG_DIGITAL_ZOOM), 0.0001);
        assertEquals(0, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_CONVERTER));
        long[] unknown3 = (long[])_nikonDirectory.getObject(NikonType1MakernoteDirectory.TAG_UNKNOWN_3);
        long[] expected = new long[] { 0, 0, 16777216, 0, 2685774096L, 0, 34833, 6931, 16178, 4372, 4372, 3322676767L, 3373084416L, 15112, 0, 0, 1151495, 252903424, 17, 0, 0, 844038208, 55184128, 218129428, 1476410198, 370540566, 4044363286L, 16711749, 204629079, 1729 };
        assertNotNull(unknown3);
        assertEquals(expected.length, unknown3.length);
        for (int i = 0; i<expected.length; i++) {
            assertEquals(expected[i], unknown3[i]);
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
        assertEquals("          ", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_IMAGE_DESCRIPTION));
        assertEquals("NIKON", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_MAKE));
        assertEquals("E950", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_MODEL));
        assertEquals(1, _exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION));
        assertEquals(300, _exifIFD0Directory.getDouble(ExifIFD0Directory.TAG_X_RESOLUTION), 0.001);
        assertEquals(300, _exifIFD0Directory.getDouble(ExifIFD0Directory.TAG_Y_RESOLUTION), 0.001);
        assertEquals(2, _exifIFD0Directory.getInt(ExifIFD0Directory.TAG_RESOLUTION_UNIT));
        assertEquals("v981-79", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_SOFTWARE));
        assertEquals("2001:04:06 11:51:40", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_DATETIME));
        assertEquals(2, _exifIFD0Directory.getInt(ExifIFD0Directory.TAG_YCBCR_POSITIONING));

        assertEquals(new Rational(1, 77), _exifSubIFDDirectory.getRational(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
        assertEquals(5.5, _exifSubIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_FNUMBER), 0.001);
        assertEquals(2, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXPOSURE_PROGRAM));
        assertEquals(80, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
        assertEquals("48 50 49 48", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_EXIF_VERSION));
        assertEquals("2001:04:06 11:51:40", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED));
        assertEquals("2001:04:06 11:51:40", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
        assertEquals("1 2 3 0", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_COMPONENTS_CONFIGURATION));
        assertEquals(4, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_COMPRESSED_AVERAGE_BITS_PER_PIXEL));
        assertEquals(0, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXPOSURE_BIAS));
        // this 2.6 *apex*, which is F2.5
        assertEquals(2.6, _exifSubIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_MAX_APERTURE), 0.001);
        assertEquals(5, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_METERING_MODE));
        assertEquals(0, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_WHITE_BALANCE));
        assertEquals(0, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_FLASH));
        assertEquals(12.8, _exifSubIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_FOCAL_LENGTH), 0.001);
        assertEquals("0 0 0 0 0 0 0 0 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_USER_COMMENT));
        assertEquals("48 49 48 48", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_FLASHPIX_VERSION));
        assertEquals(1, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_COLOR_SPACE));
        assertEquals(1600, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH));
        assertEquals(1200, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT));
        assertEquals(3, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_FILE_SOURCE));
        assertEquals(1, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_SCENE_TYPE));

        assertEquals(6, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_COMPRESSION));
        assertEquals(2036, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET));
        assertEquals(4662, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH));
    }
}
