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
import com.drew.metadata.exif.makernotes.NikonType2MakernoteDirectory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class NikonType2MakernoteTest2
{
    private NikonType2MakernoteDirectory _nikonDirectory;
    private ExifIFD0Directory _exifIFD0Directory;
    private ExifSubIFDDirectory _exifSubIFDDirectory;
    private ExifThumbnailDirectory _thumbDirectory;

    @Before
    public void setUp() throws Exception
    {
        Metadata metadata = ExifReaderTest.processBytes("Tests/Data/nikonMakernoteType2b.jpg.app1");

        _nikonDirectory = metadata.getFirstDirectoryOfType(NikonType2MakernoteDirectory.class);
        _exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        _exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        _thumbDirectory = metadata.getFirstDirectoryOfType(ExifThumbnailDirectory.class);

        assertNotNull(_nikonDirectory);
        assertNotNull(_exifSubIFDDirectory);
    }

    /*
        [Nikon Makernote] Makernote Unknown 1 =
        [Nikon Makernote] ISO Setting = Unknown (0 0)
        [Nikon Makernote] Color Mode = COLOR
        [Nikon Makernote] Quality = NORMAL
        [Nikon Makernote] White Balance = AUTO
        [Nikon Makernote] Image Sharpening = AUTO
        [Nikon Makernote] Focus Mode = AF-C
        [Nikon Makernote] Flash Setting = NORMAL
        [Nikon Makernote] Makernote Unknown 2 = 4416/500
        [Nikon Makernote] ISO Selection = AUTO
        [Nikon Makernote] Unknown tag (0x0011) = 1300
        [Nikon Makernote] Image Adjustment = AUTO
        [Nikon Makernote] Adapter = OFF
        [Nikon Makernote] Focus Distance = 0
        [Nikon Makernote] Digital Zoom = No digital zoom
        [Nikon Makernote] AF Focus Position = Unknown ()
        [Nikon Makernote] Unknown tag (0x008f) =
        [Nikon Makernote] Unknown tag (0x0094) = 0
        [Nikon Makernote] Unknown tag (0x0095) = FPNR
        [Nikon Makernote] Unknown tag (0x0e00) = PrintIM
        [Nikon Makernote] Unknown tag (0x0e10) = 1394
    */
    @Test
    public void testNikonMakernote_MatchesKnownValues() throws Exception
    {
        assertEquals("0 1 0 0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_FIRMWARE_VERSION));
        assertEquals("0 0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_ISO_1));
        assertEquals("COLOR", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_COLOR_MODE));
        assertEquals("NORMAL ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_QUALITY_AND_FILE_FORMAT));
        assertEquals("AUTO        ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_CAMERA_WHITE_BALANCE));
        assertEquals("AUTO  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_CAMERA_SHARPENING));
        assertEquals("AF-C  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_AF_TYPE));
        assertEquals("NORMAL      ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_FLASH_SYNC_MODE));
//        assertEquals(new Rational(4416,500), _nikonDirectory.getRational(NikonType3MakernoteDirectory.TAG_UNKNOWN_2));
        assertEquals("AUTO  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_ISO_MODE));
        assertEquals(1300, _nikonDirectory.getInt(0x0011));
        assertEquals("AUTO         ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_IMAGE_ADJUSTMENT));
        assertEquals("OFF         ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_ADAPTER));
        assertEquals(0, _nikonDirectory.getInt(NikonType2MakernoteDirectory.TAG_MANUAL_FOCUS_DISTANCE));
        assertEquals(1, _nikonDirectory.getInt(NikonType2MakernoteDirectory.TAG_DIGITAL_ZOOM));
        assertEquals("                ", _nikonDirectory.getString(0x008f));
        assertEquals(0, _nikonDirectory.getInt(0x0094));
        assertEquals("FPNR", _nikonDirectory.getString(0x0095));
        assertEquals("80 114 105 110 116 73 77 0 48 49 48 48 0 0 13 0 1 0 22 0 22 0 2 0 1 0 0 0 3 0 94 0 0 0 7 0 0 0 0 0 8 0 0 0 0 0 9 0 0 0 0 0 10 0 0 0 0 0 11 0 -90 0 0 0 12 0 0 0 0 0 13 0 0 0 0 0 14 0 -66 0 0 0 0 1 5 0 0 0 1 1 1 0 0 0 9 17 0 0 16 39 0 0 11 15 0 0 16 39 0 0 -105 5 0 0 16 39 0 0 -80 8 0 0 16 39 0 0 1 28 0 0 16 39 0 0 94 2 0 0 16 39 0 0 -117 0 0 0 16 39 0 0 -53 3 0 0 16 39 0 0 -27 27 0 0 16 39 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0", _nikonDirectory.getString(0x0e00));
//        assertEquals("PrintIM", _nikonDirectory.getString(0x0e00));
        assertEquals(1394, _nikonDirectory.getInt(0x0e10));
    }

    /*
        [Exif] Image Description =
        [Exif] Make = NIKON
        [Exif] Model = E995
        [Exif] X Resolution = 300 dots per inch
        [Exif] Y Resolution = 300 dots per inch
        [Exif] Resolution Unit = Inch
        [Exif] Software = E995v1.6
        [Exif] Date/Time = 2002:08:29 17:31:40
        [Exif] YCbCr Positioning = Center of pixel array
        [Exif] Exposure Time = 2439024/100000000 sec
        [Exif] F-Number = F2.6
        [Exif] Exposure Program = Program normal
        [Exif] ISO Speed Ratings = 100
        [Exif] Exif Version = 2.10
        [Exif] Date/Time Original = 2002:08:29 17:31:40
        [Exif] Date/Time Digitized = 2002:08:29 17:31:40
        [Exif] Components Configuration = YCbCr
        [Exif] Exposure Bias Value = 0 EV
        [Exif] Max Aperture Value = F1
        [Exif] Metering Mode = Multi-segment
        [Exif] White Balance = Unknown
        [Exif] Flash = Flash fired
        [Exif] Focal Length = 8.2 mm
        [Exif] User Comment =
        [Exif] FlashPix Version = 1.00
        [Exif] Color Space = sRGB
        [Exif] Exif Image Width = 2048 pixels
        [Exif] Exif Image Height = 1536 pixels
        [Exif] File Source = Digital Still Camera (DSC)
        [Exif] Scene Type = Directly photographed image
    */
    @Test
    public void testExifDirectory_MatchesKnownValues() throws Exception
    {
        assertEquals("          ", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_IMAGE_DESCRIPTION));
        assertEquals("NIKON", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_MAKE));
        assertEquals("E995", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_MODEL));
        assertEquals(300, _exifIFD0Directory.getDouble(ExifIFD0Directory.TAG_X_RESOLUTION), 0.001);
        assertEquals(300, _exifIFD0Directory.getDouble(ExifIFD0Directory.TAG_Y_RESOLUTION), 0.001);
        assertEquals(2, _exifIFD0Directory.getInt(ExifIFD0Directory.TAG_RESOLUTION_UNIT));
        assertEquals("E995v1.6", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_SOFTWARE));
        assertEquals("2002:08:29 17:31:40", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_DATETIME));
        assertEquals(1, _exifIFD0Directory.getInt(ExifIFD0Directory.TAG_YCBCR_POSITIONING));

        assertEquals(new Rational(2439024, 100000000), _exifSubIFDDirectory.getRational(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
        assertEquals(2.6, _exifSubIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_FNUMBER), 0.001);
        assertEquals(2, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXPOSURE_PROGRAM));
        assertEquals(100, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
        assertEquals("48 50 49 48", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_EXIF_VERSION));
        assertEquals("2002:08:29 17:31:40", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED));
        assertEquals("2002:08:29 17:31:40", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
        assertEquals("1 2 3 0", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_COMPONENTS_CONFIGURATION));
        assertEquals(0, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXPOSURE_BIAS));
        assertEquals("0", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_MAX_APERTURE));
        assertEquals(5, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_METERING_MODE));
        assertEquals(0, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_WHITE_BALANCE));
        assertEquals(1, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_FLASH));
        assertEquals(8.2, _exifSubIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_FOCAL_LENGTH), 0.001);
        assertEquals("0 0 0 0 0 0 0 0 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_USER_COMMENT));
        assertEquals("48 49 48 48", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_FLASHPIX_VERSION));
        assertEquals(1, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_COLOR_SPACE));
        assertEquals(2048, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH));
        assertEquals(1536, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT));
        assertEquals(3, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_FILE_SOURCE));
        assertEquals(1, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_SCENE_TYPE));
    }

    /*
        [Exif Thumbnail] Thumbnail Compression = JPEG (old-style)
        [Exif Thumbnail] X Resolution = 72 dots per inch
        [Exif Thumbnail] Y Resolution = 72 dots per inch
        [Exif Thumbnail] Resolution Unit = Inch
        [Exif Thumbnail] Thumbnail Offset = 1494 bytes
        [Exif Thumbnail] Thumbnail Length = 6077 bytes
    */
    @Test
    public void testExifThumbnailDirectory_MatchesKnownValues() throws Exception
    {
        assertEquals(6, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_COMPRESSION));
        assertEquals(1494, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET));
        assertEquals(6077, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH));
        assertEquals(1494, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET));
        assertEquals(72, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_X_RESOLUTION));
        assertEquals(72, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_Y_RESOLUTION));
    }
}
