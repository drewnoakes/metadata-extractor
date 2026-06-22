/*
 * Copyright 2002-2026 Drew Noakes and contributors
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
package com.drew.imaging.ico;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.ico.IcoDirectory;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IcoMetadataReaderTest
{
    @Test
    public void testReadMetadataDoesNotTreatUnavailableBytesAsTruncated() throws Exception
    {
        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(createValidIcoBytes())
        {
            @Override
            public synchronized int available()
            {
                return 0;
            }
        });

        Collection<IcoDirectory> directories = metadata.getDirectoriesOfType(IcoDirectory.class);
        ArrayList<IcoDirectory> directoryList = new ArrayList<IcoDirectory>(directories);

        assertEquals(1, directories.size());
        assertFalse(directoryList.get(0).hasErrors());
        assertEquals(16, directoryList.get(0).getInt(IcoDirectory.TAG_IMAGE_WIDTH));
        assertEquals(24, directoryList.get(0).getInt(IcoDirectory.TAG_IMAGE_HEIGHT));
    }

    @Test
    public void testReadMetadataStopsAfterFirstTruncatedEntry() throws Exception
    {
        Metadata metadata = IcoMetadataReader.readMetadata(new ByteArrayInputStream(createMalformedIcoBytes())
        {
            @Override
            public synchronized int available()
            {
                return 0;
            }
        });

        Collection<IcoDirectory> directories = metadata.getDirectoriesOfType(IcoDirectory.class);
        ArrayList<IcoDirectory> directoryList = new ArrayList<IcoDirectory>(directories);

        assertEquals(18, directoryList.size());
        assertTrue(directoryList.get(directoryList.size() - 1).hasErrors());
    }

    private static byte[] createMalformedIcoBytes()
    {
        byte[] bytes = new byte[291];
        bytes[2] = 1;
        bytes[4] = 0x41;
        bytes[5] = (byte)0xD6;
        bytes[7] = (byte)0xFF;
        return bytes;
    }

    private static byte[] createValidIcoBytes()
    {
        final byte width = 16;
        final byte height = 24;

        return new byte[] {
            0, 0,
            1, 0,
            1, 0,
            width,
            height,
            0,
            0,
            1, 0,
            32, 0,
            4, 0, 0, 0,
            22, 0, 0, 0
        };
    }
}
