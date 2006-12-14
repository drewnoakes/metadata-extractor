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
import com.drew.metadata.exif.NikonType1MakernoteDirectory;
import junit.framework.TestCase;

import java.io.File;

/**
 *
 */
public class NikonType1MakernoteTest extends TestCase
{
    private NikonType1MakernoteDirectory _nikonDirectory;
    private ExifDirectory _exifDirectory;

    public NikonType1MakernoteTest(String s)
    {
        super(s);
    }

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

    protected void setUp() throws Exception
    {
        File nikonJpeg = new File("Source/com/drew/metadata/exif/test/nikonMakernoteType1.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(nikonJpeg);
        _nikonDirectory = (NikonType1MakernoteDirectory)metadata.getDirectory(NikonType1MakernoteDirectory.class);
        _exifDirectory = (ExifDirectory)metadata.getDirectory(ExifDirectory.class);
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
    public void testNikonMakernote_MatchesKnownValues() throws Exception
    {
        assertTrue(_nikonDirectory.getTagCount() > 0);
        assertEquals(8, _nikonDirectory.getDouble(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_UNKNOWN_1), 0.0001);
        assertEquals(12, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_QUALITY));
        assertEquals(1, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_COLOR_MODE));
        assertEquals(3, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_IMAGE_ADJUSTMENT));
        assertEquals(0, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CCD_SENSITIVITY));
        assertEquals(0, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_WHITE_BALANCE));
        assertEquals(0, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_FOCUS));
        assertEquals("", _nikonDirectory.getString(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_UNKNOWN_2));
        assertEquals(0, _nikonDirectory.getDouble(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_DIGITAL_ZOOM), 0.0001);
        assertEquals(0, _nikonDirectory.getInt(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_CONVERTER));
        int[] unknown3 = _nikonDirectory.getIntArray(NikonType1MakernoteDirectory.TAG_NIKON_TYPE1_UNKNOWN_3);
        int[] expected = new int[] { 0, 0, 16777216, 0, -1609193200, 0, 34833, 6931, 16178, 4372, 4372, -972290529, -921882880, 15112, 0, 0, 1151495, 252903424, 17, 0, 0, 844038208, 55184128, 218129428, 1476410198, 370540566, -250604010, 16711749, 204629079, 1729 };
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
    public void testExifDirectory_MatchesKnownValues() throws Exception
    {
        assertEquals("          ", _exifDirectory.getString(ExifDirectory.TAG_IMAGE_DESCRIPTION));
        assertEquals("NIKON", _exifDirectory.getString(ExifDirectory.TAG_MAKE));
        assertEquals("E950", _exifDirectory.getString(ExifDirectory.TAG_MODEL));
        assertEquals(1, _exifDirectory.getInt(ExifDirectory.TAG_ORIENTATION));
        assertEquals(300, _exifDirectory.getDouble(ExifDirectory.TAG_X_RESOLUTION), 0.001);
        assertEquals(300, _exifDirectory.getDouble(ExifDirectory.TAG_Y_RESOLUTION), 0.001);
        assertEquals(2, _exifDirectory.getInt(ExifDirectory.TAG_RESOLUTION_UNIT));
        assertEquals("v981-79", _exifDirectory.getString(ExifDirectory.TAG_SOFTWARE));
        assertEquals("2001:04:06 11:51:40", _exifDirectory.getString(ExifDirectory.TAG_DATETIME));
        assertEquals(2, _exifDirectory.getInt(ExifDirectory.TAG_YCBCR_POSITIONING));
        assertEquals(new Rational(1, 77), _exifDirectory.getRational(ExifDirectory.TAG_EXPOSURE_TIME));
        assertEquals(5.5, _exifDirectory.getDouble(ExifDirectory.TAG_FNUMBER), 0.001);
        assertEquals(2, _exifDirectory.getInt(ExifDirectory.TAG_EXPOSURE_PROGRAM));
        assertEquals(80, _exifDirectory.getInt(ExifDirectory.TAG_ISO_EQUIVALENT));
        assertEquals("48 50 49 48", _exifDirectory.getString(ExifDirectory.TAG_EXIF_VERSION));
        assertEquals("2001:04:06 11:51:40", _exifDirectory.getString(ExifDirectory.TAG_DATETIME_DIGITIZED));
        assertEquals("2001:04:06 11:51:40", _exifDirectory.getString(ExifDirectory.TAG_DATETIME_ORIGINAL));
        assertEquals("1 2 3 0", _exifDirectory.getString(ExifDirectory.TAG_COMPONENTS_CONFIGURATION));
        assertEquals(4, _exifDirectory.getInt(ExifDirectory.TAG_COMPRESSION_LEVEL));
        assertEquals(0, _exifDirectory.getInt(ExifDirectory.TAG_EXPOSURE_BIAS));
        // this 2.6 *apex*, which is F2.5
        assertEquals(2.6, _exifDirectory.getDouble(ExifDirectory.TAG_MAX_APERTURE), 0.001);
        assertEquals(5, _exifDirectory.getInt(ExifDirectory.TAG_METERING_MODE));
        assertEquals(0, _exifDirectory.getInt(ExifDirectory.TAG_WHITE_BALANCE));
        assertEquals(0, _exifDirectory.getInt(ExifDirectory.TAG_FLASH));
        assertEquals(12.8, _exifDirectory.getDouble(ExifDirectory.TAG_FOCAL_LENGTH), 0.001);
        assertEquals("0 0 0 0 0 0 0 0 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32 32", _exifDirectory.getString(ExifDirectory.TAG_USER_COMMENT));
        assertEquals("48 49 48 48", _exifDirectory.getString(ExifDirectory.TAG_FLASHPIX_VERSION));
        assertEquals(1, _exifDirectory.getInt(ExifDirectory.TAG_COLOR_SPACE));
        assertEquals(1600, _exifDirectory.getInt(ExifDirectory.TAG_EXIF_IMAGE_WIDTH));
        assertEquals(1200, _exifDirectory.getInt(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT));
        assertEquals(3, _exifDirectory.getInt(ExifDirectory.TAG_FILE_SOURCE));
        assertEquals(1, _exifDirectory.getInt(ExifDirectory.TAG_SCENE_TYPE));
        assertEquals(6, _exifDirectory.getInt(ExifDirectory.TAG_COMPRESSION));
        assertEquals(2036, _exifDirectory.getInt(ExifDirectory.TAG_THUMBNAIL_OFFSET));
        assertEquals(4662, _exifDirectory.getInt(ExifDirectory.TAG_THUMBNAIL_LENGTH));
    }
}
