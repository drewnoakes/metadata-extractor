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
 * Created by dnoakes on 3-Oct-2002 20:47:31 using IntelliJ IDEA.
 */
package com.drew.metadata.exif.test;

import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.NikonType2MakernoteDescriptor;
import com.drew.metadata.exif.NikonType2MakernoteDirectory;
import junit.framework.TestCase;

import java.io.File;

/**
 *
 */
public class NikonType2MakernoteTest1 extends TestCase
{
    private NikonType2MakernoteDirectory _nikonDirectory;
    private NikonType2MakernoteDescriptor _descriptor;

    public NikonType2MakernoteTest1(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
        File metadataFile = new File("Source/com/drew/metadata/exif/test/nikonMakernoteType2a.metadata");
        Metadata metadata = new ExifReader(JpegSegmentData.FromFile(metadataFile)).extract();

        _nikonDirectory = (NikonType2MakernoteDirectory)metadata.getDirectory(NikonType2MakernoteDirectory.class);
        _descriptor = new NikonType2MakernoteDescriptor(_nikonDirectory);
    }

    /*
        [Nikon Makernote] Firmware Version = 0200
        [Nikon Makernote] ISO = 0 320
        [Nikon Makernote] File Format = FINE
        [Nikon Makernote] White Balance = FLASH
        [Nikon Makernote] Sharpening = AUTO
        [Nikon Makernote] AF Type = AF-C
        [Nikon Makernote] Unknown 17 = NORMAL
        [Nikon Makernote] Unknown 18 =
        [Nikon Makernote] White Balance Fine = 0
        [Nikon Makernote] Unknown 01 =
        [Nikon Makernote] Unknown 02 =
        [Nikon Makernote] Unknown 03 = 914
        [Nikon Makernote] Unknown 19 =
        [Nikon Makernote] ISO = 0 320
        [Nikon Makernote] Tone Compensation = AUTO
        [Nikon Makernote] Unknown 04 = 6
        [Nikon Makernote] Lens Focal/Max-FStop pairs = 240/10 850/10 35/10 45/10
        [Nikon Makernote] Unknown 05 = 0
        [Nikon Makernote] Unknown 06 = 
        [Nikon Makernote] Unknown 07 = 1
        [Nikon Makernote] Unknown 20 = 0
        [Nikon Makernote] Unknown 08 = @
        [Nikon Makernote] Colour Mode = MODE1
        [Nikon Makernote] Unknown 10 = NATURAL
        [Nikon Makernote] Unknown 11 = 0100
        
        
        -
        [Nikon Makernote] Camera Hue = 0
        [Nikon Makernote] Noise Reduction = OFF
        [Nikon Makernote] Unknown 12 = 0100

        [Nikon Makernote] Unknown 13 = 0100{t@7b,4x,D"Y
        [Nikon Makernote] Unknown 15 = 78/10 78/10
    */
    public void testNikonMakernote_MatchesKnownValues() throws Exception
    {
        assertEquals("48 50 48 48", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FIRMWARE_VERSION));
        assertEquals("0 320", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1));
        assertEquals("0 320", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_2));
        assertEquals("FLASH       ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_WHITE_BALANCE));
        assertEquals("AUTO  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_SHARPENING));
        assertEquals("AF-C  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_TYPE));
        assertEquals("NORMAL      ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FLASH_SYNC_MODE));
        assertEquals("0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_WHITE_BALANCE_FINE));
        assertEquals("914", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_UNKNOWN_3));
        assertEquals("AUTO    ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_TONE_COMPENSATION));
        assertEquals("6", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_UNKNOWN_4));
        assertEquals("240/10 850/10 35/10 45/10", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS));
        assertEquals("0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_UNKNOWN_5));
        assertEquals("1", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_UNKNOWN_7));
        assertEquals("0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_UNKNOWN_20));
        assertEquals("MODE1   ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_COLOR_MODE));
        assertEquals("NATURAL    ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LIGHT_SOURCE));
        assertEquals("0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_HUE_ADJUSTMENT));
        assertEquals("OFF ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_NOISE_REDUCTION));
        assertEquals("78/10 78/10", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_UNKNOWN_15));
    }

    public void testGetLensDescription() throws MetadataException
    {
        assertEquals("24-85mm f/3.5-4.5", _descriptor.getDescription(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS));
        assertEquals("24-85mm f/3.5-4.5", _descriptor.getLensDescription());
    }

    public void testGetHueAdjustmentDescription() throws MetadataException
    {
        assertEquals("0 degrees", _descriptor.getDescription(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_HUE_ADJUSTMENT));
        assertEquals("0 degrees", _descriptor.getHueAdjustmentDescription());
    }

    public void testGetColorModeDescription() throws Exception
    {
        assertEquals("Mode I (sRGB)", _descriptor.getDescription(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_COLOR_MODE));
        assertEquals("Mode I (sRGB)", _descriptor.getColorModeDescription());
    }

    public void testGetAutoFlashCompensationDescription() throws Exception
    {
        NikonType2MakernoteDirectory directory = new NikonType2MakernoteDirectory();
        NikonType2MakernoteDescriptor descriptor = new NikonType2MakernoteDescriptor(directory);

        // no entry exists
        assertEquals("Unknown", descriptor.getAutoFlashCompensationDescription());

        directory.setByteArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION, new byte[] { 0x06, 0x01, 0x06 });
        assertEquals("1 EV", descriptor.getAutoFlashCompensationDescription());

        directory.setByteArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION, new byte[] { 0x04, 0x01, 0x06 });
        assertEquals("0.67 EV", descriptor.getAutoFlashCompensationDescription());

        directory.setByteArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION, new byte[] { 0x02, 0x01, 0x06 });
        assertEquals("0.33 EV", descriptor.getAutoFlashCompensationDescription());

        directory.setByteArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION, new byte[] { (byte)0xFE, 0x01, 0x06 });
        assertEquals("-0.33 EV", descriptor.getAutoFlashCompensationDescription());
    }
}
