/*
 * Copyright 2002-2014 Drew Noakes
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.drew.lang.Rational;

/**
 * Unit tests for {@link ExifIFD0Descriptor}.
 *
 * @author  Drew Noakes http://drewnoakes.com
 */
public class ExifIFD0DescriptorTest
{
    @Test
    public void testXResolutionDescription() throws Exception
    {
        final ExifIFD0Directory directory = new ExifIFD0Directory();
		directory.setRational(ExifCommonDirectoryTags.TAG_X_RESOLUTION, new Rational(72, 1));
        // 2 is for 'Inch'
		directory.setInt(ExifCommonDirectoryTags.TAG_RESOLUTION_UNIT, 2);
        final ExifIFD0Descriptor descriptor = new ExifIFD0Descriptor(directory);
		assertEquals("72 dots per inch", descriptor.getDescription(ExifCommonDirectoryTags.TAG_X_RESOLUTION));
    }

    @Test
    public void testYResolutionDescription() throws Exception
    {
        final ExifIFD0Directory directory = new ExifIFD0Directory();
		directory.setRational(ExifCommonDirectoryTags.TAG_Y_RESOLUTION, new Rational(50, 1));
        // 3 is for 'cm'
		directory.setInt(ExifCommonDirectoryTags.TAG_RESOLUTION_UNIT, 3);
        final ExifIFD0Descriptor descriptor = new ExifIFD0Descriptor(directory);
		assertEquals("50 dots per cm", descriptor.getDescription(ExifCommonDirectoryTags.TAG_Y_RESOLUTION));
    }

    @Test
    public void testWindowsXpFields() throws Exception
    {
        final ExifIFD0Directory directory = ExifReaderTest.processBytes("Tests/Data/windowsXpFields.jpg.app1", ExifIFD0Directory.class);

        assertEquals("Testing artist\0", directory.getString(ExifIFD0Directory.TAG_WIN_AUTHOR, "UTF-16LE"));
        assertEquals("Testing comments\0", directory.getString(ExifIFD0Directory.TAG_WIN_COMMENT, "UTF-16LE"));
        assertEquals("Testing keywords\0", directory.getString(ExifIFD0Directory.TAG_WIN_KEYWORDS, "UTF-16LE"));
        assertEquals("Testing subject\0", directory.getString(ExifIFD0Directory.TAG_WIN_SUBJECT, "UTF-16LE"));
        assertEquals("Testing title\0", directory.getString(ExifIFD0Directory.TAG_WIN_TITLE, "UTF-16LE"));

        final ExifIFD0Descriptor descriptor = new ExifIFD0Descriptor(directory);
        assertEquals("Testing artist", descriptor.getDescription(ExifIFD0Directory.TAG_WIN_AUTHOR));
        assertEquals("Testing comments", descriptor.getDescription(ExifIFD0Directory.TAG_WIN_COMMENT));
        assertEquals("Testing keywords", descriptor.getDescription(ExifIFD0Directory.TAG_WIN_KEYWORDS));
        assertEquals("Testing subject", descriptor.getDescription(ExifIFD0Directory.TAG_WIN_SUBJECT));
        assertEquals("Testing title", descriptor.getDescription(ExifIFD0Directory.TAG_WIN_TITLE));
    }
}
