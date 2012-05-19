/*
 * Copyright 2002-2012 Drew Noakes
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
package com.drew.metadata.test;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test case for class Metadata.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class MetadataTest
{
    @Test public void testGetDirectoryWhenNotExists()
    {
        Assert.assertNull(new Metadata().getDirectory(ExifSubIFDDirectory.class));
    }

    @Test public void testGetOrCreateDirectoryWhenNotExists()
    {
        Assert.assertNotNull(new Metadata().getOrCreateDirectory(ExifSubIFDDirectory.class));
    }

    @Test public void testGetDirectoryReturnsSameInstance()
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        Assert.assertSame(directory, metadata.getDirectory(ExifSubIFDDirectory.class));
    }

    @Test public void testGetOrCreateDirectoryReturnsSameInstance()
    {
        Metadata metadata = new Metadata();
        Directory directory = metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        Assert.assertSame(directory, metadata.getOrCreateDirectory(ExifSubIFDDirectory.class));
        Assert.assertNotSame(directory, metadata.getOrCreateDirectory(IptcDirectory.class));
    }

    @Test
    public void testHasErrors() throws Exception
    {
        Metadata metadata = new Metadata();
        Assert.assertFalse(metadata.hasErrors());
        final ExifSubIFDDirectory directory = metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        directory.addError("Test Error 1");
        Assert.assertTrue(metadata.hasErrors());
    }

    @Test
    public void testGetErrors() throws Exception
    {
        Metadata metadata = new Metadata();
        Assert.assertFalse(metadata.hasErrors());
        final ExifSubIFDDirectory directory = metadata.getOrCreateDirectory(ExifSubIFDDirectory.class);
        directory.addError("Test Error 1");
        Assert.assertTrue(metadata.hasErrors());
    }
}
