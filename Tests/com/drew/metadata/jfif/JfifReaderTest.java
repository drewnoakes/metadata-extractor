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

package com.drew.metadata.jfif;

import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class JfifReaderTest
{
    @Test public void testRead() throws Exception
    {
        final byte[] jfifData = new byte[] {
            74,70,73,70,0,
            1,2,
            1,
            0,108,
            0,108,
            0,0
        };

        final Metadata metadata = new Metadata();
        final JfifReader reader = new JfifReader();
        reader.extract(new ByteArrayReader(jfifData), metadata);

        assertEquals(1, metadata.getDirectoryCount());
        JfifDirectory directory = metadata.getFirstDirectoryOfType(JfifDirectory.class);
        assertNotNull(directory);
        assertFalse(directory.getErrors().toString(), directory.hasErrors());

        Tag[] tags = directory.getTags().toArray(new Tag[directory.getTagCount()]);
        assertEquals(6, tags.length);

        assertEquals(JfifDirectory.TAG_VERSION, tags[0].getTagType());
        assertEquals(0x0102, directory.getInt(tags[0].getTagType()));

        assertEquals(JfifDirectory.TAG_UNITS, tags[1].getTagType());
        assertEquals(1, directory.getInt(tags[1].getTagType()));

        assertEquals(JfifDirectory.TAG_RESX, tags[2].getTagType());
        assertEquals(108, directory.getInt(tags[2].getTagType()));

        assertEquals(JfifDirectory.TAG_RESY, tags[3].getTagType());
        assertEquals(108, directory.getInt(tags[3].getTagType()));

        assertEquals(JfifDirectory.TAG_THUMB_WIDTH, tags[4].getTagType());
        assertEquals(0, directory.getInt(tags[4].getTagType()));

        assertEquals(JfifDirectory.TAG_THUMB_HEIGHT, tags[5].getTagType());
        assertEquals(0, directory.getInt(tags[5].getTagType()));
    }
}
