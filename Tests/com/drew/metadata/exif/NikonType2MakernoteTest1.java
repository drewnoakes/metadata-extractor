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

import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.makernotes.NikonType2MakernoteDescriptor;
import com.drew.metadata.exif.makernotes.NikonType2MakernoteDirectory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Locale;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class NikonType2MakernoteTest1
{
    private NikonType2MakernoteDirectory _nikonDirectory;
    private NikonType2MakernoteDescriptor _descriptor;

    @Before
    public void setUp() throws Exception
    {
        Locale.setDefault(new Locale("en", "GB"));

        _nikonDirectory = ExifReaderTest.processBytes("Tests/Data/nikonMakernoteType2a.jpg.app1", NikonType2MakernoteDirectory.class);

        assertNotNull(_nikonDirectory);

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
        assertEquals("48 50 48 48", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_FIRMWARE_VERSION));
        assertEquals("0 320", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_ISO_1));
        assertEquals("0 320", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_ISO_REQUESTED));
        assertEquals("FLASH       ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_CAMERA_WHITE_BALANCE));
        assertEquals("AUTO  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_CAMERA_SHARPENING));
        assertEquals("AF-C  ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_AF_TYPE));
        assertEquals("NORMAL      ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_FLASH_SYNC_MODE));
        assertEquals("0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_CAMERA_WHITE_BALANCE_FINE));
        assertEquals("914", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_PREVIEW_IFD));
        assertEquals("AUTO    ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_CAMERA_TONE_COMPENSATION));
        assertEquals("6", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_LENS_TYPE));
        assertEquals("240/10 850/10 35/10 45/10", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_LENS));
        assertEquals("0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_FLASH_USED));
        assertEquals("1", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_SHOOTING_MODE));
        assertEquals("0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_UNKNOWN_20));
        assertEquals("MODE1   ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_CAMERA_COLOR_MODE));
        assertEquals("NATURAL    ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_LIGHT_SOURCE));
        assertEquals("0", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_CAMERA_HUE_ADJUSTMENT));
        assertEquals("OFF ", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_NOISE_REDUCTION));
        assertEquals("78/10 78/10", _nikonDirectory.getString(NikonType2MakernoteDirectory.TAG_SENSOR_PIXEL_SIZE));
    }

    @Test
    public void testGetLensDescription() throws MetadataException
    {
        assertEquals("24-85mm f/3.5-4.5", _descriptor.getDescription(NikonType2MakernoteDirectory.TAG_LENS));
        assertEquals("24-85mm f/3.5-4.5", _descriptor.getLensDescription());
    }

    @Test
    public void testGetHueAdjustmentDescription() throws MetadataException
    {
        assertEquals("0 degrees", _descriptor.getDescription(NikonType2MakernoteDirectory.TAG_CAMERA_HUE_ADJUSTMENT));
        assertEquals("0 degrees", _descriptor.getHueAdjustmentDescription());
    }

    @Test
    public void testGetColorModeDescription() throws Exception
    {
        assertEquals("Mode I (sRGB)", _descriptor.getDescription(NikonType2MakernoteDirectory.TAG_CAMERA_COLOR_MODE));
        assertEquals("Mode I (sRGB)", _descriptor.getColorModeDescription());
    }

    @Test
    public void testGetAutoFlashCompensationDescription() throws Exception
    {
        NikonType2MakernoteDirectory directory = new NikonType2MakernoteDirectory();
        NikonType2MakernoteDescriptor descriptor = new NikonType2MakernoteDescriptor(directory);

        // no entry exists
        assertNull(descriptor.getAutoFlashCompensationDescription());

        directory.setByteArray(NikonType2MakernoteDirectory.TAG_AUTO_FLASH_COMPENSATION, new byte[] { 0x06, 0x01, 0x06 });
        assertEquals("1 EV", descriptor.getAutoFlashCompensationDescription());

        directory.setByteArray(NikonType2MakernoteDirectory.TAG_AUTO_FLASH_COMPENSATION, new byte[] { 0x04, 0x01, 0x06 });
        assertEquals("0.67 EV", descriptor.getAutoFlashCompensationDescription());

        directory.setByteArray(NikonType2MakernoteDirectory.TAG_AUTO_FLASH_COMPENSATION, new byte[] { 0x02, 0x01, 0x06 });
        assertEquals("0.33 EV", descriptor.getAutoFlashCompensationDescription());

        directory.setByteArray(NikonType2MakernoteDirectory.TAG_AUTO_FLASH_COMPENSATION, new byte[] { (byte)0xFE, 0x01, 0x06 });
        assertEquals("-0.33 EV", descriptor.getAutoFlashCompensationDescription());
    }
}
