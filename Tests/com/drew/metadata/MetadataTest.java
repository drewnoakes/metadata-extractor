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
package com.drew.metadata;

import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit test case for class Metadata.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class MetadataTest
{
    @Test
    public void testGetDirectoryWhenNotExists()
    {
        assertNull(new Metadata().getFirstDirectoryOfType(ExifSubIFDDirectory.class));
    }

    @Test
    public void testHasErrors() throws Exception
    {
        ExifSubIFDDirectory directory = new ExifSubIFDDirectory();
        directory.addError("Test Error 1");

        Metadata metadata = new Metadata();
        assertFalse(metadata.hasErrors());

        metadata.addDirectory(directory);
        assertTrue(metadata.hasErrors());
    }

    @Test
    public void testToString()
    {
        Metadata metadata = new Metadata();
        assertEquals("Metadata (0 directories)", metadata.toString());

        metadata.addDirectory(new ExifIFD0Directory());
        assertEquals("Metadata (1 directory)", metadata.toString());

        metadata.addDirectory(new ExifSubIFDDirectory());
        assertEquals("Metadata (2 directories)", metadata.toString());
    }
}
