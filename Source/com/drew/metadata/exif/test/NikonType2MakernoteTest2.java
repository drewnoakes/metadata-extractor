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
 * Created by dnoakes on 25-Nov-2002 20:47:31 using IntelliJ IDEA.
 */
package com.drew.metadata.exif.test;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.lang.Rational;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.NikonType2MakernoteDirectory;
import junit.framework.TestCase;

import java.io.File;

/**
 *
 */
public class NikonType2MakernoteTest2 extends TestCase
{
    private NikonType2MakernoteDirectory _nikonDirectory;
    private ExifDirectory _exifDirectory;

    public NikonType2MakernoteTest2(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
        File nikonJpeg = new File("Source/com/drew/metadata/exif/test/nikonMakernoteType2b.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(nikonJpeg);
        _nikonDirectory = (NikonType2MakernoteDirectory)metadata.getDirectory(NikonType2MakernoteDirectory.class);
        _exifDirectory = (ExifDirectory)metadata.getDirectory(ExifDirectory.class);
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
    public void testNikonMakernote_MatchesKnownValues() throws Exception
    {
        assertEquals("0 1 0 0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FIRMWARE_VERSION));
        assertEquals("0 0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1));
        assertEquals("COLOR", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_COLOR_MODE));
        assertEquals("NORMAL ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_QUALITY_AND_FILE_FORMAT));
        assertEquals("AUTO        ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_WHITE_BALANCE));
        assertEquals("AUTO  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_SHARPENING));
        assertEquals("AF-C  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_TYPE));
        assertEquals("NORMAL      ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FLASH_SYNC_MODE));
//        assertEquals(new Rational(4416,500), _nikonDirectory.getRational(NikonType3MakernoteDirectory.TAG_NIKON_TYPE2_UNKNOWN_2));
        assertEquals("AUTO  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_SELECTION));
        assertEquals(1300, _nikonDirectory.getInt(0x0011));
        assertEquals("AUTO         ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_IMAGE_ADJUSTMENT));
        assertEquals("OFF         ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ADAPTER));
        assertEquals(0, _nikonDirectory.getInt(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_MANUAL_FOCUS_DISTANCE));
        assertEquals(1, _nikonDirectory.getInt(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_DIGITAL_ZOOM));
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
        [Exif] X Resolution = 72 dots per inch
        [Exif] Y Resolution = 72 dots per inch
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
        [Exif] Exposure Bias Value = 0
        [Exif] Max Aperture Value = F1
        [Exif] Metering Mode = Multi-segment
        [Exif] Light Source = Unknown
        [Exif] Flash = Flash fired
        [Exif] Focal Length = 8.2 mm
        [Exif] User Comment =
        [Exif] FlashPix Version = 1.00
        [Exif] Color Space = sRGB
        [Exif] Exif Image Width = 2048 pixels
        [Exif] Exif Image Height = 1536 pixels
        [Exif] File Source = Digital Still Camera (DSC)
        [Exif] Scene Type = Directly photographed image
        [Exif] Compression = JPEG compression
        [Exif] Thumbnail Offset = 1494 bytes
        [Exif] Thumbnail Length = 6077 bytes
        [Exif] Thumbnail Data = [6077 bytes of thumbnail data]
    */
    public void testExifDirectory_MatchesKnownValues() throws Exception
    {
        assertEquals("          ", _exifDirectory.getString(ExifDirectory.TAG_IMAGE_DESCRIPTION));
        assertEquals("NIKON", _exifDirectory.getString(ExifDirectory.TAG_MAKE));
        assertEquals("E995", _exifDirectory.getString(ExifDirectory.TAG_MODEL));
        assertEquals(72, _exifDirectory.getDouble(ExifDirectory.TAG_X_RESOLUTION), 0.001);
        assertEquals(72, _exifDirectory.getDouble(ExifDirectory.TAG_Y_RESOLUTION), 0.001);
        assertEquals(2, _exifDirectory.getInt(ExifDirectory.TAG_RESOLUTION_UNIT));
        assertEquals("E995v1.6", _exifDirectory.getString(ExifDirectory.TAG_SOFTWARE));
        assertEquals("2002:08:29 17:31:40", _exifDirectory.getString(ExifDirectory.TAG_DATETIME));
        assertEquals(1, _exifDirectory.getInt(ExifDirectory.TAG_YCBCR_POSITIONING));
        assertEquals(new Rational(2439024, 100000000), _exifDirectory.getRational(ExifDirectory.TAG_EXPOSURE_TIME));
        assertEquals(2.6, _exifDirectory.getDouble(ExifDirectory.TAG_FNUMBER), 0.001);
        assertEquals(2, _exifDirectory.getInt(ExifDirectory.TAG_EXPOSURE_PROGRAM));
        assertEquals(100, _exifDirectory.getInt(ExifDirectory.TAG_ISO_EQUIVALENT));
        assertEquals("48 50 49 48", _exifDirectory.getString(ExifDirectory.TAG_EXIF_VERSION));
        assertEquals("2002:08:29 17:31:40", _exifDirectory.getString(ExifDirectory.TAG_DATETIME_DIGITIZED));
        assertEquals("2002:08:29 17:31:40", _exifDirectory.getString(ExifDirectory.TAG_DATETIME_ORIGINAL));
        assertEquals("1 2 3 0", _exifDirectory.getString(ExifDirectory.TAG_COMPONENTS_CONFIGURATION));
        assertEquals(0, _exifDirectory.getInt(ExifDirectory.TAG_EXPOSURE_BIAS));
        assertEquals("0", _exifDirectory.getString(ExifDirectory.TAG_MAX_APERTURE));
        assertEquals(5, _exifDirectory.getInt(ExifDirectory.TAG_METERING_MODE));
        assertEquals(0, _exifDirectory.getInt(ExifDirectory.TAG_WHITE_BALANCE));
        assertEquals(1, _exifDirectory.getInt(ExifDirectory.TAG_FLASH));
        assertEquals(8.2, _exifDirectory.getDouble(ExifDirectory.TAG_FOCAL_LENGTH), 0.001);
        assertEquals("0 0 0 0 0 0 0 0 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32", _exifDirectory.getString(ExifDirectory.TAG_USER_COMMENT));
        assertEquals("48 49 48 48", _exifDirectory.getString(ExifDirectory.TAG_FLASHPIX_VERSION));
        assertEquals(1, _exifDirectory.getInt(ExifDirectory.TAG_COLOR_SPACE));
        assertEquals(2048, _exifDirectory.getInt(ExifDirectory.TAG_EXIF_IMAGE_WIDTH));
        assertEquals(1536, _exifDirectory.getInt(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT));
        assertEquals(3, _exifDirectory.getInt(ExifDirectory.TAG_FILE_SOURCE));
        assertEquals(1, _exifDirectory.getInt(ExifDirectory.TAG_SCENE_TYPE));
        assertEquals(6, _exifDirectory.getInt(ExifDirectory.TAG_COMPRESSION));
        assertEquals(1494, _exifDirectory.getInt(ExifDirectory.TAG_THUMBNAIL_OFFSET));
        assertEquals(6077, _exifDirectory.getInt(ExifDirectory.TAG_THUMBNAIL_LENGTH));
    }
}
