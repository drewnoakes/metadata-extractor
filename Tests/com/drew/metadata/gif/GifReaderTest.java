/*
 * Copyright 2002-2016 Drew Noakes
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

package com.drew.metadata.gif;

import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.filter.MetadataFilter;
import com.drew.metadata.xmp.XmpDirectory;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class GifReaderTest
{
    @NotNull
    public static Metadata processBytes(@NotNull String file, @Nullable MetadataFilter filter) throws Exception
    {
        Metadata metadata = new Metadata();
        InputStream stream = new FileInputStream(file);
        if (filter == null) {
            new GifReader().extract(new StreamReader(stream), metadata);
        } else {
            new GifReader().extract(new StreamReader(stream), metadata, filter);
        }
        stream.close();

        return metadata;
    }

    @Test
    public void testMsPaintGif() throws Exception
    {
        Metadata metadata = processBytes("Tests/Data/mspaint-10x10.gif", null);
        GifHeaderDirectory directory = metadata.getFirstDirectoryOfType(GifHeaderDirectory.class);

        assertNotNull(directory);
        assertFalse(directory.hasErrors());

        assertEquals("89a", directory.getString(GifHeaderDirectory.TAG_GIF_FORMAT_VERSION));
        assertEquals(10, directory.getInt(GifHeaderDirectory.TAG_IMAGE_WIDTH));
        assertEquals(10, directory.getInt(GifHeaderDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(256, directory.getInt(GifHeaderDirectory.TAG_COLOR_TABLE_SIZE));
        assertFalse(directory.getBoolean(GifHeaderDirectory.TAG_IS_COLOR_TABLE_SORTED));
        assertEquals(8, directory.getInt(GifHeaderDirectory.TAG_BITS_PER_PIXEL));
        assertTrue(directory.getBoolean(GifHeaderDirectory.TAG_HAS_GLOBAL_COLOR_TABLE));
        assertEquals(0, directory.getInt(GifHeaderDirectory.TAG_BACKGROUND_COLOR_INDEX));
    }

    @Test
    public void testPhotoshopGif() throws Exception
    {
        Metadata metadata = processBytes("Tests/Data/photoshop-8x12-32colors-alpha.gif", null);
        GifHeaderDirectory directory = metadata.getFirstDirectoryOfType(GifHeaderDirectory.class);

        assertNotNull(directory);
        assertFalse(directory.hasErrors());

        assertEquals("89a", directory.getString(GifHeaderDirectory.TAG_GIF_FORMAT_VERSION));
        assertEquals(8, directory.getInt(GifHeaderDirectory.TAG_IMAGE_WIDTH));
        assertEquals(12, directory.getInt(GifHeaderDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(32, directory.getInt(GifHeaderDirectory.TAG_COLOR_TABLE_SIZE));
        assertFalse(directory.getBoolean(GifHeaderDirectory.TAG_IS_COLOR_TABLE_SORTED));
        assertEquals(5, directory.getInt(GifHeaderDirectory.TAG_BITS_PER_PIXEL));
        assertTrue(directory.getBoolean(GifHeaderDirectory.TAG_HAS_GLOBAL_COLOR_TABLE));
        assertEquals(8, directory.getInt(GifHeaderDirectory.TAG_BACKGROUND_COLOR_INDEX));
    }

    @Test
    public void testExtractFilteredMetadata() throws Exception {
        Metadata metadata = processBytes("Tests/Data/photoshop-8x12-32colors-alpha.gif", new MetadataFilter() {

            @Override
            public boolean tagFilter(Directory directory, int tagType) {
                return
                    directory instanceof GifHeaderDirectory && tagType != GifHeaderDirectory.TAG_GIF_FORMAT_VERSION ||
                    directory instanceof XmpDirectory && tagType != XmpDirectory.TAG_XMP_VALUE_COUNT ||
                    directory instanceof GifImageDirectory && tagType == GifImageDirectory.TAG_HEIGHT;
            }

            @Override
            public boolean directoryFilter(Class<? extends Directory> directory) {
                return directory != GifControlDirectory.class;
            }
        });

        assertEquals(3, metadata.getDirectoryCount());
        Directory directory = metadata.getFirstDirectoryOfType(GifHeaderDirectory.class);
        assertNotNull(directory);
        assertEquals(7, directory.getTagCount());
        assertFalse(directory.containsTag(GifHeaderDirectory.TAG_GIF_FORMAT_VERSION));
        assertTrue(directory.containsTag(GifHeaderDirectory.TAG_BACKGROUND_COLOR_INDEX));
        assertEquals(8, directory.getInt(GifHeaderDirectory.TAG_BACKGROUND_COLOR_INDEX));

        directory = metadata.getFirstDirectoryOfType(XmpDirectory.class);
        assertNotNull(directory);
        assertEquals(1, directory.getTagCount());
        assertFalse(directory.containsTag(XmpDirectory.TAG_XMP_VALUE_COUNT));
        assertTrue(directory.containsTag(XmpDirectory.TAG_CREATOR_TOOL));
        assertEquals("Adobe Photoshop CS6 (Windows)", directory.getString(XmpDirectory.TAG_CREATOR_TOOL));

        directory = metadata.getFirstDirectoryOfType(GifImageDirectory.class);
        assertNotNull(directory);
        assertEquals(1, directory.getTagCount());
        assertTrue(directory.containsTag(GifImageDirectory.TAG_HEIGHT));
        assertEquals(12, directory.getInt(GifImageDirectory.TAG_HEIGHT));

        assertNull(metadata.getFirstDirectoryOfType(GifControlDirectory.class));

        metadata = processBytes("Tests/Data/photoshop-8x12-32colors-alpha.gif", new MetadataFilter() {

            @Override
            public boolean tagFilter(Directory directory, int tagType) {
                return false;
            }

            @Override
            public boolean directoryFilter(Class<? extends Directory> directory) {
                return true;
            }

        });
        assertEquals(3, metadata.getDirectoryCount());
        for (Directory dir : metadata.getDirectories()) {
            assertEquals(0, dir.getTagCount());
        }

        metadata = processBytes("Tests/Data/photoshop-8x12-32colors-alpha.gif", new MetadataFilter() {

            @Override
            public boolean tagFilter(Directory directory, int tagType) {
                return true;
            }

            @Override
            public boolean directoryFilter(Class<? extends Directory> directory) {
                return false;
            }

        });
        assertEquals(0, metadata.getDirectoryCount());
    }

}
