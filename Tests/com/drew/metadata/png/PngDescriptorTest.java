/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
package com.drew.metadata.png;

import com.drew.imaging.png.PngChunkType;
import com.drew.lang.Charsets;
import com.drew.lang.KeyValuePair;
import com.drew.metadata.MetadataException;
import com.drew.metadata.StringValue;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.drew.metadata.png.PngDirectory.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Akihiko Kusanagi
 */
public class PngDescriptorTest
{
    private static Charset _latin1Encoding = Charsets.ISO_8859_1;

    @Test
    public void testGetColorTypeDescription() throws Exception
    {
        PngDirectory directory = new PngDirectory(PngChunkType.IHDR);
        PngDescriptor descriptor = new PngDescriptor(directory);

        directory.setInt(TAG_COLOR_TYPE, 6);
        assertEquals("True Color with Alpha", descriptor.getColorTypeDescription());
        assertEquals("True Color with Alpha", directory.getDescription(TAG_COLOR_TYPE));
    }

     @Test
    public void testGetCompressionTypeDescription() throws Exception
    {
        PngDirectory directory = new PngDirectory(PngChunkType.IHDR);
        PngDescriptor descriptor = new PngDescriptor(directory);

        directory.setInt(TAG_COMPRESSION_TYPE, 0);
        assertEquals("Deflate", descriptor.getCompressionTypeDescription());
        assertEquals("Deflate", directory.getDescription(TAG_COMPRESSION_TYPE));
    }

    @Test
    public void testGetFilterMethodDescription() throws Exception
    {
        PngDirectory directory = new PngDirectory(PngChunkType.IHDR);
        PngDescriptor descriptor = new PngDescriptor(directory);

        directory.setInt(TAG_FILTER_METHOD, 0);
        assertEquals("Adaptive", descriptor.getFilterMethodDescription());
        assertEquals("Adaptive", directory.getDescription(TAG_FILTER_METHOD));
    }

    @Test
    public void testGetInterlaceMethodDescription() throws Exception
    {
        PngDirectory directory = new PngDirectory(PngChunkType.IHDR);
        PngDescriptor descriptor = new PngDescriptor(directory);

        directory.setInt(TAG_INTERLACE_METHOD, 1);
        assertEquals("Adam7 Interlace", descriptor.getInterlaceMethodDescription());
        assertEquals("Adam7 Interlace", directory.getDescription(TAG_INTERLACE_METHOD));
    }

    @Test
    public void testGetPaletteHasTransparencyDescription() throws Exception
    {
        PngDirectory directory = new PngDirectory(PngChunkType.tRNS);
        PngDescriptor descriptor = new PngDescriptor(directory);

        directory.setInt(TAG_PALETTE_HAS_TRANSPARENCY, 1);
        assertEquals("Yes", descriptor.getPaletteHasTransparencyDescription());
        assertEquals("Yes", directory.getDescription(TAG_PALETTE_HAS_TRANSPARENCY));
    }

    @Test
    public void testGetIsSrgbColorSpaceDescription() throws Exception
    {
        PngDirectory directory = new PngDirectory(PngChunkType.sRGB);
        PngDescriptor descriptor = new PngDescriptor(directory);

        directory.setInt(TAG_SRGB_RENDERING_INTENT, 0);
        assertEquals("Perceptual", descriptor.getIsSrgbColorSpaceDescription());
        assertEquals("Perceptual", directory.getDescription(TAG_SRGB_RENDERING_INTENT));
    }

    @Test
    public void testGetUnitSpecifierDescription() throws Exception
    {
        PngDirectory directory = new PngDirectory(PngChunkType.pHYs);
        PngDescriptor descriptor = new PngDescriptor(directory);

        directory.setInt(TAG_UNIT_SPECIFIER, 1);
        assertEquals("Metres", descriptor.getUnitSpecifierDescription());
        assertEquals("Metres", directory.getDescription(TAG_UNIT_SPECIFIER));
    }

    @Test
    public void testGetTextualDataDescription() throws Exception
    {
        List<KeyValuePair> textPairs = new ArrayList<KeyValuePair>();
        StringValue value = new StringValue("value".getBytes(_latin1Encoding), _latin1Encoding);
        textPairs.add(new KeyValuePair("keyword", value));

        PngDirectory directory = new PngDirectory(PngChunkType.tEXt);
        PngDescriptor descriptor = new PngDescriptor(directory);
        directory.setObject(TAG_TEXTUAL_DATA, textPairs);
        assertEquals("keyword: value", descriptor.getTextualDataDescription());
        assertEquals("keyword: value", directory.getDescription(TAG_TEXTUAL_DATA));

        directory = new PngDirectory(PngChunkType.zTXt);
        descriptor = new PngDescriptor(directory);
        directory.setObject(TAG_TEXTUAL_DATA, textPairs);
        assertEquals("keyword: value", descriptor.getTextualDataDescription());
        assertEquals("keyword: value", directory.getDescription(TAG_TEXTUAL_DATA));

        directory = new PngDirectory(PngChunkType.iTXt);
        descriptor = new PngDescriptor(directory);
        directory.setObject(TAG_TEXTUAL_DATA, textPairs);
        assertEquals("keyword: value", descriptor.getTextualDataDescription());
        assertEquals("keyword: value", directory.getDescription(TAG_TEXTUAL_DATA));
    }

    @Test
    public void testGetBackgroundColorDescription() throws Exception
    {
        PngDirectory directory = new PngDirectory(PngChunkType.bKGD);
        PngDescriptor descriptor = new PngDescriptor(directory);

        directory.setByteArray(TAG_BACKGROUND_COLOR, new byte[]{52});
        assertEquals("Palette Index 52", descriptor.getBackgroundColorDescription());
        assertEquals("Palette Index 52", directory.getDescription(TAG_BACKGROUND_COLOR));
        directory.setByteArray(TAG_BACKGROUND_COLOR, new byte[]{0, 52});
        assertEquals("Greyscale Level 52", descriptor.getBackgroundColorDescription());
        assertEquals("Greyscale Level 52", directory.getDescription(TAG_BACKGROUND_COLOR));
        directory.setByteArray(TAG_BACKGROUND_COLOR, new byte[]{0, 50, 0, 51, 0, 52});
        assertEquals("R 50, G 51, B 52", descriptor.getBackgroundColorDescription());
        assertEquals("R 50, G 51, B 52", directory.getDescription(TAG_BACKGROUND_COLOR));
    }
}
