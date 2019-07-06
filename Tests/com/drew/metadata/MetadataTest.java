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
package com.drew.metadata;

import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    @Test
    public void testOrderOfSameType()
    {
        Metadata metadata = new Metadata();
        Directory directory2 = new ExifSubIFDDirectory();
        Directory directory3 = new ExifSubIFDDirectory();
        Directory directory1 = new ExifSubIFDDirectory();

        metadata.addDirectory(directory1);
        metadata.addDirectory(directory2);
        metadata.addDirectory(directory3);

        Collection<ExifSubIFDDirectory> directories = metadata.getDirectoriesOfType(ExifSubIFDDirectory.class);

        assertNotNull(directories);
        assertEquals(3, directories.size());
        assertSame(directory1, directories.toArray()[0]);
        assertSame(directory2, directories.toArray()[1]);
        assertSame(directory3, directories.toArray()[2]);
    }

    @Test
    public void testOrderOfDifferentTypes()
    {
        Metadata metadata = new Metadata();
        Directory directory1 = new ExifSubIFDDirectory();
        Directory directory2 = new ExifThumbnailDirectory();
        Directory directory3 = new ExifIFD0Directory();

        metadata.addDirectory(directory1);
        metadata.addDirectory(directory2);
        metadata.addDirectory(directory3);

        List<Directory> directories = new ArrayList<Directory>();
        for (Directory directory : metadata.getDirectories()) {
            directories.add(directory);
        }

        assertEquals(3, directories.size());
        assertSame(directory1, directories.toArray()[0]);
        assertSame(directory2, directories.toArray()[1]);
        assertSame(directory3, directories.toArray()[2]);
    }
}
