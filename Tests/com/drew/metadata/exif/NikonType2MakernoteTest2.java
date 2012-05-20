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
package com.drew.metadata.exif;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.lang.Rational;
import com.drew.metadata.Metadata;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author Drew Noakes http://drewnoakes.com
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
        File nikonJpeg = new File("Tests/com/drew/metadata/exif/nikonMakernoteType2b.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(nikonJpeg);
        
        _nikonDirectory = metadata.getDirectory(NikonType2MakernoteDirectory.class);
        _exifIFD0Directory = metadata.getDirectory(ExifIFD0Directory.class);
        _exifSubIFDDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);
        _thumbDirectory = metadata.getDirectory(ExifThumbnailDirectory.class);

        Assert.assertNotNull(_nikonDirectory);
        Assert.assertNotNull(_exifSubIFDDirectory);
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
        Assert.assertEquals("0 1 0 0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FIRMWARE_VERSION));
        Assert.assertEquals("0 0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1));
        Assert.assertEquals("COLOR", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_COLOR_MODE));
        Assert.assertEquals("NORMAL ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_QUALITY_AND_FILE_FORMAT));
        Assert.assertEquals("AUTO        ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_WHITE_BALANCE));
        Assert.assertEquals("AUTO  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_SHARPENING));
        Assert.assertEquals("AF-C  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_TYPE));
        Assert.assertEquals("NORMAL      ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FLASH_SYNC_MODE));
//        assertEquals(new Rational(4416,500), _nikonDirectory.getRational(NikonType3MakernoteDirectory.TAG_NIKON_TYPE2_UNKNOWN_2));
        Assert.assertEquals("AUTO  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_MODE));
        Assert.assertEquals(1300, _nikonDirectory.getInt(0x0011));
        Assert.assertEquals("AUTO         ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_IMAGE_ADJUSTMENT));
        Assert.assertEquals("OFF         ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ADAPTER));
        Assert.assertEquals(0, _nikonDirectory.getInt(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_MANUAL_FOCUS_DISTANCE));
        Assert.assertEquals(1, _nikonDirectory.getInt(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_DIGITAL_ZOOM));
        Assert.assertEquals("                ", _nikonDirectory.getString(0x008f));
        Assert.assertEquals(0, _nikonDirectory.getInt(0x0094));
        Assert.assertEquals("FPNR", _nikonDirectory.getString(0x0095));
        Assert.assertEquals("80 114 105 110 116 73 77 0 48 49 48 48 0 0 13 0 1 0 22 0 22 0 2 0 1 0 0 0 3 0 94 0 0 0 7 0 0 0 0 0 8 0 0 0 0 0 9 0 0 0 0 0 10 0 0 0 0 0 11 0 -90 0 0 0 12 0 0 0 0 0 13 0 0 0 0 0 14 0 -66 0 0 0 0 1 5 0 0 0 1 1 1 0 0 0 9 17 0 0 16 39 0 0 11 15 0 0 16 39 0 0 -105 5 0 0 16 39 0 0 -80 8 0 0 16 39 0 0 1 28 0 0 16 39 0 0 94 2 0 0 16 39 0 0 -117 0 0 0 16 39 0 0 -53 3 0 0 16 39 0 0 -27 27 0 0 16 39 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0", _nikonDirectory.getString(0x0e00));
//        assertEquals("PrintIM", _nikonDirectory.getString(0x0e00));
        Assert.assertEquals(1394, _nikonDirectory.getInt(0x0e10));
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
        Assert.assertEquals("          ", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_IMAGE_DESCRIPTION));
        Assert.assertEquals("NIKON", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_MAKE));
        Assert.assertEquals("E995", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_MODEL));
        Assert.assertEquals(300, _exifIFD0Directory.getDouble(ExifIFD0Directory.TAG_X_RESOLUTION), 0.001);
        Assert.assertEquals(300, _exifIFD0Directory.getDouble(ExifIFD0Directory.TAG_Y_RESOLUTION), 0.001);
        Assert.assertEquals(2, _exifIFD0Directory.getInt(ExifIFD0Directory.TAG_RESOLUTION_UNIT));
        Assert.assertEquals("E995v1.6", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_SOFTWARE));
        Assert.assertEquals("2002:08:29 17:31:40", _exifIFD0Directory.getString(ExifIFD0Directory.TAG_DATETIME));
        Assert.assertEquals(1, _exifIFD0Directory.getInt(ExifIFD0Directory.TAG_YCBCR_POSITIONING));
        
        Assert.assertEquals(new Rational(2439024, 100000000), _exifSubIFDDirectory.getRational(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
        Assert.assertEquals(2.6, _exifSubIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_FNUMBER), 0.001);
        Assert.assertEquals(2, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXPOSURE_PROGRAM));
        Assert.assertEquals(100, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
        Assert.assertEquals("48 50 49 48", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_EXIF_VERSION));
        Assert.assertEquals("2002:08:29 17:31:40", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED));
        Assert.assertEquals("2002:08:29 17:31:40", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
        Assert.assertEquals("1 2 3 0", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_COMPONENTS_CONFIGURATION));
        Assert.assertEquals(0, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXPOSURE_BIAS));
        Assert.assertEquals("0", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_MAX_APERTURE));
        Assert.assertEquals(5, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_METERING_MODE));
        Assert.assertEquals(0, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_WHITE_BALANCE));
        Assert.assertEquals(1, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_FLASH));
        Assert.assertEquals(8.2, _exifSubIFDDirectory.getDouble(ExifSubIFDDirectory.TAG_FOCAL_LENGTH), 0.001);
        Assert.assertEquals("0 0 0 0 0 0 0 0 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_USER_COMMENT));
        Assert.assertEquals("48 49 48 48", _exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_FLASHPIX_VERSION));
        Assert.assertEquals(1, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_COLOR_SPACE));
        Assert.assertEquals(2048, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH));
        Assert.assertEquals(1536, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT));
        Assert.assertEquals(3, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_FILE_SOURCE));
        Assert.assertEquals(1, _exifSubIFDDirectory.getInt(ExifSubIFDDirectory.TAG_SCENE_TYPE));
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
        Assert.assertEquals(6, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_COMPRESSION));
        Assert.assertEquals(1494, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET));
        Assert.assertEquals(6077, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_LENGTH));
        Assert.assertEquals(1494, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_THUMBNAIL_OFFSET));
        Assert.assertEquals(72, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_X_RESOLUTION));
        Assert.assertEquals(72, _thumbDirectory.getInt(ExifThumbnailDirectory.TAG_Y_RESOLUTION));
    }
}
