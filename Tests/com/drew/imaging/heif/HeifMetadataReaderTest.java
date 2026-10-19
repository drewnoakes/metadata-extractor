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
package com.drew.imaging.heif;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.heif.HeifDirectory;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HeifMetadataReaderTest
{
    private static final int FTYP_LENGTH = 20;

    @Test
    public void testDeeplyNestedBoxesReportErrorRatherThanOverflowingStack() throws Exception
    {
        HeifDirectory directory = readNestedContainers(10000);

        assertTrue(containsError(directory, "Box nesting too deep."));
    }

    @Test
    public void testNestingLimit() throws Exception
    {
        HeifDirectory directory = readNestedContainers(100);
        assertFalse(containsError(directory, "Box nesting too deep."));
        assertEquals("heic", directory.getString(HeifDirectory.TAG_MAJOR_BRAND));

        assertTrue(containsError(readNestedContainers(101), "Box nesting too deep."));
    }

    /**
     * Reads a file in which each 'ipco' box is nested within the previous one. Every box is
     * individually well formed, so only the nesting depth is abnormal.
     */
    private static HeifDirectory readNestedContainers(int count) throws Exception
    {
        byte[] bytes = new byte[FTYP_LENGTH + count * 8];

        writeBoxHeader(bytes, 0, FTYP_LENGTH, "ftyp");
        writeString(bytes, 8, "heic");
        writeString(bytes, 16, "mif1");

        for (int i = 0; i < count; i++) {
            writeBoxHeader(bytes, FTYP_LENGTH + i * 8, (count - i) * 8, "ipco");
        }

        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes));
        HeifDirectory directory = metadata.getFirstDirectoryOfType(HeifDirectory.class);
        assertNotNull(directory);
        return directory;
    }

    private static boolean containsError(Directory directory, String message)
    {
        for (String error : directory.getErrors()) {
            if (error.equals(message))
                return true;
        }
        return false;
    }

    private static void writeBoxHeader(byte[] bytes, int offset, long size, String type)
    {
        bytes[offset] = (byte)(size >> 24);
        bytes[offset + 1] = (byte)(size >> 16);
        bytes[offset + 2] = (byte)(size >> 8);
        bytes[offset + 3] = (byte)size;
        writeString(bytes, offset + 4, type);
    }

    private static void writeString(byte[] bytes, int offset, String value)
    {
        for (int i = 0; i < value.length(); i++) {
            bytes[offset + i] = (byte)value.charAt(i);
        }
    }
}
