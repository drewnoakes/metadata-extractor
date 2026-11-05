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
 *    https://github.com/drewnoakes/metadata-extractor/
 *    https://github.com/drewnoakes/metadata-extractor
 */

package com.drew.metadata.photoshop;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PhotoshopReaderTest
{
    /** A single 8BIM image resource block with a one-byte description and empty payload. */
    private static byte[] resource(int tagType) throws Exception
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        out.writeBytes("8BIM");
        out.writeShort(tagType);    // resource id, big-endian
        out.writeByte(1);           // Pascal-string name length
        out.writeByte('A');         // name (padded to an even length overall)
        out.writeInt(0);            // data length
        return bytes.toByteArray();
    }

    private static PhotoshopDirectory extract(byte[] bytes)
    {
        Metadata metadata = new Metadata();
        new PhotoshopReader().extract(new SequentialByteArrayReader(bytes), bytes.length, metadata);
        PhotoshopDirectory directory = metadata.getFirstDirectoryOfType(PhotoshopDirectory.class);
        assertNotNull(directory);
        return directory;
    }

    @Test
    public void testPathAndPluginTagNames() throws Exception
    {
        ByteArrayOutputStream segment = new ByteArrayOutputStream();
        segment.write(resource(0x07D0));    // first clipping-path resource
        segment.write(resource(0x0FA0));    // first plug-in resource

        PhotoshopDirectory directory = extract(segment.toByteArray());

        assertEquals("Path Info 1", directory.getTagName(0x07D0));
        assertEquals("Plug-in 1 Data", directory.getTagName(0x0FA0));
    }

    @Test
    public void testReadingDoesNotMutateSharedTagNameMap() throws Exception
    {
        int sizeBefore = PhotoshopDirectory._tagNameMap.size();

        ByteArrayOutputStream segment = new ByteArrayOutputStream();
        segment.write(resource(0x07D0));    // clipping-path resource -> stored at 0x07D0
        segment.write(resource(0x0FA0));    // plug-in resource

        extract(segment.toByteArray());

        // Parsing must not add per-image entries to the process-wide static map.
        assertEquals(sizeBefore, PhotoshopDirectory._tagNameMap.size());
        assertFalse(PhotoshopDirectory._tagNameMap.containsKey(0x07D0));
        assertFalse(PhotoshopDirectory._tagNameMap.containsKey(0x0FA0));
    }
}
