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

package com.drew.metadata.jfif.test;

import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import com.drew.metadata.Tag;
import com.drew.metadata.jfif.JfifDirectory;
import com.drew.metadata.jfif.JfifReader;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class JfifReaderTest
{
    @Test public void testRead() throws Exception
    {
        final byte[] jfifData = new byte[] { 74,70,73,70,0, 1,2, 1, 0,108, 0,108, 0,0 };

        final Metadata metadata = new Metadata();
        final MetadataReader reader = new JfifReader();
        reader.extract(jfifData, metadata);

        Assert.assertEquals(1, metadata.getDirectoryCount());
        JfifDirectory directory = metadata.getDirectory(JfifDirectory.class);
        Assert.assertNotNull(directory);
        Assert.assertFalse(directory.getErrors().toString(), directory.hasErrors());

        Tag[] tags = directory.getTags().toArray(new Tag[directory.getTagCount()]);
        Assert.assertEquals(4, tags.length);

        Assert.assertEquals(JfifDirectory.TAG_JFIF_VERSION, tags[0].getTagType());
        Assert.assertEquals(0x0102, directory.getInt(tags[0].getTagType()));

        Assert.assertEquals(JfifDirectory.TAG_JFIF_UNITS, tags[1].getTagType());
        Assert.assertEquals(1, directory.getInt(tags[1].getTagType()));

        Assert.assertEquals(JfifDirectory.TAG_JFIF_RESX, tags[2].getTagType());
        Assert.assertEquals(108, directory.getInt(tags[2].getTagType()));

        Assert.assertEquals(JfifDirectory.TAG_JFIF_RESY, tags[3].getTagType());
        Assert.assertEquals(108, directory.getInt(tags[3].getTagType()));
    }
}
