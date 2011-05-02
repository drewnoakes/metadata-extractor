/*
 * Copyright 2002-2011 Drew Noakes
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

import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.NikonType2MakernoteDescriptor;
import com.drew.metadata.exif.NikonType2MakernoteDirectory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class NikonType2MakernoteTest1
{
    private NikonType2MakernoteDirectory _nikonDirectory;
    private NikonType2MakernoteDescriptor _descriptor;

    @Before
    public void setUp() throws Exception
    {
        File metadataFile = new File("Source/com/drew/metadata/exif/test/nikonMakernoteType2a.metadata");
        Metadata metadata = new Metadata();
        final byte[] data = JpegSegmentData.fromFile(metadataFile).getSegment(JpegSegmentReader.SEGMENT_APP1);
        Assert.assertNotNull(data);
        new ExifReader().extract(data, metadata);

        _nikonDirectory = metadata.getOrCreateDirectory(NikonType2MakernoteDirectory.class);
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
    @Test
    public void testNikonMakernote_MatchesKnownValues() throws Exception
    {
        Assert.assertEquals("48 50 48 48", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FIRMWARE_VERSION));
        Assert.assertEquals("0 320", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_1));
        Assert.assertEquals("0 320", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_ISO_REQUESTED));
        Assert.assertEquals("FLASH       ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_WHITE_BALANCE));
        Assert.assertEquals("AUTO  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_SHARPENING));
        Assert.assertEquals("AF-C  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AF_TYPE));
        Assert.assertEquals("NORMAL      ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FLASH_SYNC_MODE));
        Assert.assertEquals("0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_WHITE_BALANCE_FINE));
        Assert.assertEquals("914", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_PREVIEW_IFD));
        Assert.assertEquals("AUTO    ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_TONE_COMPENSATION));
        Assert.assertEquals("6", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS_TYPE));
        Assert.assertEquals("240/10 850/10 35/10 45/10", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS));
        Assert.assertEquals("0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_FLASH_USED));
        Assert.assertEquals("1", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_SHOOTING_MODE));
        Assert.assertEquals("0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_UNKNOWN_20));
        Assert.assertEquals("MODE1   ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_COLOR_MODE));
        Assert.assertEquals("NATURAL    ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LIGHT_SOURCE));
        Assert.assertEquals("0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_HUE_ADJUSTMENT));
        Assert.assertEquals("OFF ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_NOISE_REDUCTION));
        Assert.assertEquals("78/10 78/10", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_SENSOR_PIXEL_SIZE));
    }

    @Test
    public void testGetLensDescription() throws MetadataException
    {
        Assert.assertEquals("24-85mm f/3.5-4.5", _descriptor.getDescription(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_LENS));
        Assert.assertEquals("24-85mm f/3.5-4.5", _descriptor.getLensDescription());
    }

    @Test
    public void testGetHueAdjustmentDescription() throws MetadataException
    {
        Assert.assertEquals("0 degrees", _descriptor.getDescription(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_HUE_ADJUSTMENT));
        Assert.assertEquals("0 degrees", _descriptor.getHueAdjustmentDescription());
    }

    @Test
    public void testGetColorModeDescription() throws Exception
    {
        Assert.assertEquals("Mode I (sRGB)", _descriptor.getDescription(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_CAMERA_COLOR_MODE));
        Assert.assertEquals("Mode I (sRGB)", _descriptor.getColorModeDescription());
    }

    @Test
    public void testGetAutoFlashCompensationDescription() throws Exception
    {
        NikonType2MakernoteDirectory directory = new NikonType2MakernoteDirectory();
        NikonType2MakernoteDescriptor descriptor = new NikonType2MakernoteDescriptor(directory);

        // no entry exists
        Assert.assertNull(descriptor.getAutoFlashCompensationDescription());

        directory.setByteArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION, new byte[] { 0x06, 0x01, 0x06 });
        Assert.assertEquals("1 EV", descriptor.getAutoFlashCompensationDescription());

        directory.setByteArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION, new byte[] { 0x04, 0x01, 0x06 });
        Assert.assertEquals("0.67 EV", descriptor.getAutoFlashCompensationDescription());

        directory.setByteArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION, new byte[] { 0x02, 0x01, 0x06 });
        Assert.assertEquals("0.33 EV", descriptor.getAutoFlashCompensationDescription());

        directory.setByteArray(NikonType2MakernoteDirectory.TAG_NIKON_TYPE2_AUTO_FLASH_COMPENSATION, new byte[] { (byte)0xFE, 0x01, 0x06 });
        Assert.assertEquals("-0.33 EV", descriptor.getAutoFlashCompensationDescription());
    }
}
