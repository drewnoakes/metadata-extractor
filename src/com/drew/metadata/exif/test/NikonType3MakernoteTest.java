/*
 * Created by dnoakes on 3-Oct-2002 20:47:31 using IntelliJ IDEA.
 */
package com.drew.metadata.exif.test;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.NikonType3MakernoteDirectory;
import com.drew.metadata.exif.NikonType3MakernoteDescriptor;
import junit.framework.TestCase;

import java.io.File;

/**
 *
 */
public class NikonType3MakernoteTest extends TestCase
{
    private NikonType3MakernoteDirectory _nikonDirectory;
    private NikonType3MakernoteDescriptor _descriptor;

    public NikonType3MakernoteTest(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
        File nikonJpeg = new File("src/com/drew/metadata/exif/test/nikonMakernoteType3.jpg");
        Metadata metadata = JpegMetadataReader.readMetadata(nikonJpeg);
        _nikonDirectory = (NikonType3MakernoteDirectory)metadata.getDirectory(NikonType3MakernoteDirectory.class);
        _descriptor = new NikonType3MakernoteDescriptor(_nikonDirectory);
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
        assertEquals("0200", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_FIRMWARE_VERSION));
        assertEquals("0 320", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_ISO_1));
        assertEquals("0 320", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_ISO_2));
        assertEquals("FLASH       ", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_WHITE_BALANCE));
        assertEquals("AUTO  ", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_SHARPENING));
        assertEquals("AF-C  ", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_AF_TYPE));
        assertEquals("NORMAL      ", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_UNKNOWN_17));
        assertEquals("0", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_WHITE_BALANCE_FINE));
        assertEquals("914", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_UNKNOWN_3));
        assertEquals("AUTO    ", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_TONE_COMPENSATION));
        assertEquals("6", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_UNKNOWN_4));
        assertEquals("240/10 850/10 35/10 45/10", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_LENS));
        assertEquals("0", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_UNKNOWN_5));
        assertEquals("1", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_UNKNOWN_7));
        assertEquals("0", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_UNKNOWN_20));
        assertEquals("MODE1   ", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_COLOR_MODE));
        assertEquals("NATURAL    ", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_UNKNOWN_10));
        assertEquals("0", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_HUE_ADJUSTMENT));
        assertEquals("OFF ", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_NOISE_REDUCTION));
        assertEquals("78/10 78/10", _nikonDirectory.getString(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_UNKNOWN_15));
    }

    public void testGetLensDescription() throws MetadataException
    {
        assertEquals("24-85mm f/3.5-4.5", _descriptor.getDescription(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_LENS));
        assertEquals("24-85mm f/3.5-4.5", _descriptor.getLensDescription());
    }

    public void testGetHueAdjustmentDescription() throws MetadataException
    {
        assertEquals("0 degrees", _descriptor.getDescription(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_HUE_ADJUSTMENT));
        assertEquals("0 degrees", _descriptor.getHueAdjustmentDescription());
    }

    public void testGetColorModeDescription() throws Exception
    {
        assertEquals("Mode I (sRGB)", _descriptor.getDescription(NikonType3MakernoteDirectory.TAG_NIKON_TYPE3_CAMERA_COLOR_MODE));
        assertEquals("Mode I (sRGB)", _descriptor.getColorModeDescription());
    }
}
